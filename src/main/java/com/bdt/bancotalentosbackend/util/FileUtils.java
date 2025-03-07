package com.bdt.bancotalentosbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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

    public static void guardarArchivoMigracion(String fileb64, String ruta) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - GuardarArchivoMigracion");
            logger.info("Ruta asignada: " + ruta);

            byte[] fileBytes = Base64.getDecoder().decode(fileb64);

            File repositorioDir = new File(ruta).getParentFile();

            logger.info("Consultando existencia de repositorio..");
            if (!repositorioDir.exists() && !repositorioDir.mkdirs()) {
                logger.error("Error al crear el repositorio: " + repositorioDir.getAbsolutePath());
                return;
            }

            File archivo = new File(ruta);

            try {
                Files.write(Paths.get(archivo.getAbsolutePath()), fileBytes, StandardOpenOption.CREATE);
                logger.info("Archivo creado exitosamente en la ruta: " + archivo.getAbsolutePath());
            } catch (IOException e) {
                logger.error("Error al guardar el archivo: " + e.getMessage());
            }

            logger.info("Archivo creado exitosamente en: " + archivo.getAbsolutePath());

            logger.info("Fin Utilitarios - GuardarArchivoMigracion");
            logger.info(Constante.TXT_SEPARADOR);
        } catch (IllegalArgumentException e) {
            logger.error("Cadena Base64 inválida: " + e.getMessage());
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

    public static boolean reemplazarImagen(String imgb64, String fileExtension, String ruta) {
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
            File directorio = archivo.getParentFile();
            logger.info("Consultando existencia de repositorio...");

            if (directorio.exists() && directorio.isDirectory()) {
                File[] archivos = directorio.listFiles(File::isFile);
                if (archivos != null) {
                    for (File file : archivos) {
                        logger.info("Archivo encontrado: " + file.getName());
                        if (!file.delete()) {
                            logger.error("No se pudo borrar el archivo: " + file.getName());
                            return false;
                        }
                        logger.info("Archivo borrado correctamente: " + file.getName());
                    }
                }
            } else if (!directorio.exists() && !directorio.mkdirs()) {
                logger.error("Error al crear el repositorio: " + directorio.getAbsolutePath());
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
            logger.info("Consultando existencia de archivo..." + linkImagen);
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

    public static String cargarPDF(String linkPDF) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - CargarPDF");
            File archivo = new File(linkPDF).getAbsoluteFile();
            logger.info("Consultando existencia de archivo...");
            if (archivo.exists()) {
                logger.info("El archivo existe");

                if (!linkPDF.toLowerCase().endsWith(".pdf")) {
                    logger.error("El archivo no es un PDF: " + linkPDF);
                    return "";
                }

                FileInputStream fileInputStream = new FileInputStream(archivo);
                byte[] byteArray = new byte[(int) archivo.length()];
                int bytesRead = fileInputStream.read(byteArray);
                fileInputStream.close();

                if (bytesRead != byteArray.length) {
                    logger.error("No se pudo leer completamente el archivo PDF: " + linkPDF);
                    return "";
                }

                String pdfBase64 = Base64.getEncoder().encodeToString(byteArray);
                logger.info("Fin Utilitarios - CargarPDF");
                logger.info(Constante.TXT_SEPARADOR);
                return pdfBase64;
            } else {
                logger.info("El archivo no existe");
                logger.info("Fin Utilitarios - CargarPDF");
                logger.info(Constante.TXT_SEPARADOR);
                return "";
            }
        } catch (IOException e) {
            logger.error("Error al cargar el PDF: " + e.getMessage());
            return "";
        }
    }

    public static boolean guardarArchivo(String archivoBase64, String ruta) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - GuardarArchivo");
            logger.info("Ruta asignada: " + ruta);

            byte[] fileBytes = Base64.getDecoder().decode(archivoBase64);

            File repositorioDir = new File(ruta).getParentFile();

            logger.info("Consultando existencia de repositorio..");
            if (!repositorioDir.exists() && !repositorioDir.mkdirs()) {
                logger.error("Error al crear el repositorio: " + repositorioDir.getAbsolutePath());
                return false;
            }

            File archivo = new File(ruta);

            try {
                Files.write(Paths.get(archivo.getAbsolutePath()), fileBytes, StandardOpenOption.CREATE);
                logger.info("Archivo creado exitosamente en la ruta: " + archivo.getAbsolutePath());
            } catch (IOException e) {
                logger.error("Error al guardar el archivo: " + e.getMessage());
            }

            logger.info("Fin Utilitarios - GuardarArchivo");
            logger.info(Constante.TXT_SEPARADOR);
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Cadena Base64 inválida: " + e.getMessage());
        }
        return false;
    }

    public static boolean reemplazarArchivo(String archivoB64, String ruta) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - ReemplazarArchivo");
            logger.info("Ruta asignada: " + ruta);

            byte[] fileBytes = Base64.getDecoder().decode(archivoB64);
            File archivo = new File(ruta);
            File directorio = archivo.getParentFile();

            logger.info("Consultando existencia de repositorio..");
            if (directorio.exists() && directorio.isDirectory()) {
                File[] archivos = directorio.listFiles(File::isFile);
                if (archivos == null || archivos.length == 0) {
                    logger.info("No se encontraron archivos para eliminar");
                } else {
                    for (File file : archivos) {
                        logger.info("Archivo encontrado: " + file.getName());
                        if (file.delete()) {
                            logger.info("Archivo eliminado correctamente: " + file.getName());
                        } else {
                            logger.error("No se pudo eliminar el archivo: " + file.getName());
                            return false;
                        }
                    }
                }
            } else if (!directorio.exists() && !directorio.mkdirs()) {
                logger.error("Error al crear el repositorio: " + directorio.getAbsolutePath());
                return false;
            }

            try {
                Files.write(Paths.get(archivo.getAbsolutePath()), fileBytes, StandardOpenOption.CREATE);
                logger.info("Archivo creado exitosamente en la ruta: " + archivo.getAbsolutePath());
            } catch (IOException e) {
                logger.error("Error al guardar el archivo: " + e.getMessage());
                return false;
            }

            logger.info("Fin Utilitarios - ReemplazarArchivo");
            logger.info(Constante.TXT_SEPARADOR);
            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Cadena Base64 inválida: " + e.getMessage());
        }
        return false;
    }
}
