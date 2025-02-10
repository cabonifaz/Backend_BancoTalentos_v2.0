package com.bdt.bancotalentosbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class.getName());

    public static void guardarImagen(String imgb64, String fileExtension, String ruta) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - GuardarImagen");
            logger.info("Ruta asignada: " + ruta);

            byte[] imagenBytes = Base64.getDecoder().decode(imgb64);
            ByteArrayInputStream bis = new ByteArrayInputStream(imagenBytes);
            BufferedImage bufferedImage = ImageIO.read(bis);

            if (bufferedImage == null) {
                logger.error("BufferedImage is null. Check Base64 input.");
                return;
            }

            File repositorioDir = new File(ruta).getAbsoluteFile();
            logger.info("Consultando existencia de repositorio..");
            if (!repositorioDir.exists() && !repositorioDir.mkdirs()) {
                logger.error("Error al crear el repositorio: " + repositorioDir.getAbsolutePath());
                return;
            }

            File archivo = new File(ruta).getAbsoluteFile();
            logger.info("Creando archivo en la ruta: " + archivo.getAbsolutePath());

            ImageIO.write(bufferedImage, fileExtension, archivo);
            logger.info("Archivo creado exitosamente.");

            logger.info("Fin Utilitarios - GuardarImagen");
            logger.info(Constante.TXT_SEPARADOR);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 string: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error al guardar la imagen: " + e.getMessage());
        }
    }
}
