package com.bdt.bancotalentosbackend.util;

import com.bdt.bancotalentosbackend.service.impl.NotificacionCorreoSwitch;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.Map;

/**
 * Envío de correos con plantilla Thymeleaf. Recorte del MailUtils de AutFMI con
 * lo que usa el correo de entrevistas (plantilla + imágenes inline + adjunto).
 * El {@link JavaMailSender} es el SMTP de spring.mail o el de Microsoft Graph
 * (GraphMailConfig, con {@code MAIL_PROVIDER=graph}).
 */
@Component
public class MailUtils {
    @Value("${spring.mail.username}")
    private String emisorCorreo;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // Todo envío valida su evento (maestro 56) antes de armar el mensaje.
    @Autowired
    private NotificacionCorreoSwitch correoSwitch;

    private static final Logger logger = LoggerFactory.getLogger(MailUtils.class);

    public boolean sendEmailWithHtmlTemplate(EventoNotificacion evento, String to, List<String> cc, String subject,
            String templateName, Map<String, Object> variables, Map<String, Resource> inlineResources,
            String attachmentName, byte[] attachmentBytes) {
        if (!correoSwitch.permitido(evento)) {
            return false;
        }
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlBody = templateEngine.process(templateName, context);

            String destino = to != null ? to.trim() : null;
            if (!EmailUtils.isValidEmail(destino)) {
                logger.error("Correo cancelado: destinatario principal inválido ('{}')", to);
                return false;
            }
            List<String> cleanCc = EmailUtils.sanitizeRecipients(cc, destino);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(emisorCorreo);
            helper.setTo(destino);
            helper.setSubject(subject);
            MailBranding.apply(helper, htmlBody, true);

            if (!cleanCc.isEmpty()) {
                helper.setCc(cleanCc.toArray(new String[0]));
            }

            if (inlineResources != null) {
                for (Map.Entry<String, Resource> entry : inlineResources.entrySet()) {
                    try {
                        helper.addInline(entry.getKey(), entry.getValue());
                    } catch (Exception e) {
                        logger.warn("Could not embed inline resource '{}': {}", entry.getKey(), e.getMessage());
                    }
                }
            }

            if (attachmentBytes != null && attachmentBytes.length > 0 && attachmentName != null) {
                try {
                    helper.addAttachment(attachmentName, new ByteArrayResource(attachmentBytes));
                } catch (Exception e) {
                    logger.warn("Could not attach '{}': {}", attachmentName, e.getMessage());
                }
            }

            mailSender.send(mimeMessage);
            logger.info("Correo enviado exitosamente a: {}", to);
            return true;

        } catch (MessagingException e) {
            logger.error("Error al enviar correo a {}: {}", to, e.getMessage(), e);
            return false;
        }
    }
}
