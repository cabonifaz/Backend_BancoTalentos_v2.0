package com.bdt.bancotalentosbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class FileUtils {
    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class.getName());

    public static void guardarImagenMigracion(String imgb64, String fileExtension, String ruta) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - GuardarImagenMigracion");
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

            logger.info("Fin Utilitarios - GuardarImagenMigracion");
            logger.info(Constante.TXT_SEPARADOR);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 string: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error al guardar la imagen: " + e.getMessage());
        }
    }


    public static boolean guardarImagen(String imgb64, String fileExtension, String ruta) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - GuardarImagen");
            logger.info("Ruta asignada: " + ruta);

            byte[] imagenBytes = Base64.getDecoder().decode(imgb64);
            ByteArrayInputStream bis = new ByteArrayInputStream(imagenBytes);
            BufferedImage bufferedImage = ImageIO.read(bis);

            if (bufferedImage == null) {
                logger.error("BufferedImage is null. Check Base64 input.");
                return false;
            }

            File archivo = new File(ruta).getAbsoluteFile();
            logger.info("Consultando existencia de repositorio..");
            if (!archivo.exists() && !archivo.mkdirs()) {
                logger.error("Error al crear el repositorio: " + archivo.getAbsolutePath());
                return false;
            }

            logger.info("Creando archivo en la ruta: " + archivo.getAbsolutePath());

            ImageIO.write(bufferedImage, fileExtension, archivo);
            logger.info("Archivo creado exitosamente.");

            logger.info("Fin Utilitarios - GuardarImagen");
            logger.info(Constante.TXT_SEPARADOR);
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Base64 string: " + e.getMessage());
        } catch (IOException e) {
            logger.error("Error al guardar la imagen: " + e.getMessage());
        }
        return false;
    }

    public static String cargarImagen(String linkImagen) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - CargarImagen");
            File archivo = new File(linkImagen).getAbsoluteFile();
            logger.info("Consultando existencia de archivo...");
            if (archivo.exists()) {
                logger.info("El archivo existe");
                BufferedImage imagen = ImageIO.read(archivo);

                // Obtener la extensión del archivo
                String formato = linkImagen.substring(linkImagen.lastIndexOf(".") + 1).toLowerCase();
                if (!formato.equals("png") && !formato.equals("jpg") && !formato.equals("jpeg")) {
                    logger.error("Formato de imagen no soportado: " + formato);
                    return "";
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                ImageIO.write(imagen, formato, byteArrayOutputStream); // Usar el formato correcto
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String imagenBase64 = Base64.getEncoder().encodeToString(byteArray);
                logger.info("Fin Utilitarios - CargarImagen");
                logger.info(Constante.TXT_SEPARADOR);
                return imagenBase64;
            }

            logger.info("Fin Utilitarios - CargarImagen");
            logger.info(Constante.TXT_SEPARADOR);
            return archivo.getAbsolutePath();
        } catch (IOException e) {
            logger.error("Error al cargar la imagen: " + e.getMessage());
            return "";
        }
    }

    public static boolean guardarArchivo(String archivoBase64, String fileExtension, String ruta) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - GuardarArchivo");
            logger.info("Ruta asignada: " + ruta);

            byte[] archivoBytes = Base64.getDecoder().decode(archivoBase64);

            File archivo = new File(ruta).getAbsoluteFile();
            logger.info("Consultando existencia de repositorio..");
            if (!archivo.exists() && !archivo.mkdirs()) {
                logger.error("Error al crear el repositorio: " + archivo.getAbsolutePath());
                return false;
            }

            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                fos.write(archivoBytes);
                logger.info("Archivo creado exitosamente en la ruta: " + archivo.getAbsolutePath());
            } catch (IOException e) {
                logger.error("Error al guardar el archivo: " + e.getMessage());
                return false;
            }

            logger.info("Fin Utilitarios - GuardarArchivo");
            logger.info(Constante.TXT_SEPARADOR);
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Cadena Base64 inválida: " + e.getMessage());
        }
        return false;
    }



}
