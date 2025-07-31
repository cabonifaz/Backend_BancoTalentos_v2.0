package com.bdt.bancotalentosbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

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
            if (linkImagen != null) {
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
            }
        } catch (IOException e) {
            logger.error("Error al cargar la imagen: " + e.getMessage());
        }
        return "";
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

            // Obtener el directorio padre
            File repositorioDir = new File(ruta).getParentFile();

            logger.info("Verificando si el directorio ya existe...");

            // Si el directorio no existe, intentar crearlo
            // Comprobar si ya existe un archivo con el mismo nombre que el directorio
            if (repositorioDir.exists() && !repositorioDir.isDirectory()) {
                logger.error("El directorio no se puede crear porque existe un archivo con el mismo nombre: " + repositorioDir.getAbsolutePath());
                return false;
            }

            if (!repositorioDir.getAbsoluteFile().exists()) {
                if (!repositorioDir.mkdirs()) {
                    logger.error("Error al crear el directorio: " + repositorioDir.getAbsolutePath());
                    return false;
                }
            } else {
                logger.info("El directorio ya existe, se procede a guardar el archivo.");
            }

            File archivo = new File(ruta);

            // Guardar archivo
            try {
                Files.write(Paths.get(archivo.getAbsolutePath()), fileBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                logger.info("Archivo guardado exitosamente en la ruta: " + archivo.getAbsolutePath());
                logger.info("Fin Utilitarios - GuardarArchivo");
                logger.info(Constante.TXT_SEPARADOR);
                return true;
            } catch (IOException e) {
                logger.error("Error al guardar el archivo: " + e.getMessage(), e);
            }

        } catch (IllegalArgumentException e) {
            logger.error("Cadena Base64 inválida: " + e.getMessage(), e);
        }

        return false;
    }

    public static boolean eliminarArchivo(String archivoRutaPre) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - EliminarArchivo");
            logger.info("Ruta recibida: " + archivoRutaPre);

            // Obtiene la ruta absoluta y normalizada
            File archivo = new File(archivoRutaPre).getCanonicalFile();

            logger.info("Ruta normalizada del archivo: " + archivo.getAbsolutePath());

            if (!archivo.exists()) {
                logger.warn("El archivo no existe: " + archivo.getAbsolutePath());
                return true;
            }

            if (archivo.delete()) {
                logger.info("Archivo eliminado exitosamente: " + archivo.getAbsolutePath());
                return true;
            } else {
                logger.error("No se pudo eliminar el archivo: " + archivo.getAbsolutePath());
                return false;
            }
        } catch (IOException e) {
            logger.error("Error al normalizar la ruta del archivo: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error al eliminar el archivo: " + e.getMessage());
        } finally {
            logger.info("Fin Utilitarios - EliminarArchivo");
            logger.info(Constante.TXT_SEPARADOR);
        }
        return false;
    }

    public static String cargarArchivoAws(String rutaS3) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - CargarArchivoAws");
            logger.info("Ruta recibida: " + rutaS3);

            String bucketName = System.getenv("AWS_BUCKET");
            if (bucketName == null || bucketName.isEmpty()) {
                logger.error("Variable de entorno AWS_BUCKET no está definida");
                return "";
            }

            // Obtener la extensión
            String extension = obtenerExtension(rutaS3);
            if (extension.isEmpty()) {
                logger.error("No se pudo determinar la extensión del archivo.");
                return "";
            }

            extension = extension.toLowerCase();

            // Validar tipo permitido (solo imágenes o PDF)
            if (!esExtensionSoportada(extension)) {
                logger.error("Extensión de archivo no soportada: " + extension);
                return "";
            }

            S3Client s3 = ClienteS3.getInstance();

            logger.info("Descargando archivo desde S3...");
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(rutaS3)
                    .build();

            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(getRequest);
            byte[] fileBytes = objectBytes.asByteArray();

            if (esImagen(extension)) {
                try {
                    BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes));
                    if (image == null) {
                        logger.error("El contenido no es una imagen válida.");
                        return "";
                    }
                } catch (IOException e) {
                    logger.error("Error al verificar la imagen: " + e.getMessage(), e);
                    return "";
                }
            }

            String base64 = Base64.getEncoder().encodeToString(fileBytes);

            logger.info("Archivo convertido exitosamente a Base64.");
            logger.info("Fin Utilitarios - CargarArchivoAws");
            logger.info(Constante.TXT_SEPARADOR);

            return base64;

        } catch (NoSuchKeyException e) {
            logger.error("Error al acceder o no existe el archivo en S3: " + e.getMessage(), e);
            return "";
        } catch (S3Exception e) {
            logger.warn("Error al acceder al archivo en S3: " + e.awsErrorDetails().errorMessage(), e);
            return "";
        } catch (Exception e) {
            logger.error("Error inesperado al cargar archivo desde S3: " + e.getMessage(), e);
            return "";
        }
    }

    public static boolean guardarArchivoAws(String archivoBase64, String fileExtension, String rutaConNombre, boolean uniqueFile) {
        try {
            logger.info(Constante.TXT_SEPARADOR);
            logger.info("Inicio Utilitarios - GuardarArchivoAws");
            logger.info("Ruta recibida (S3 Key): " + rutaConNombre);
            logger.info("Flag uniqueFile: " + uniqueFile);
            logger.info("Extensión: " + fileExtension);

            byte[] fileBytes = Base64.getDecoder().decode(archivoBase64);
            String bucketName = System.getenv("AWS_BUCKET");

            if (bucketName == null || bucketName.isEmpty()) {
                logger.error("Variable de entorno AWS_BUCKET no está definida");
                return false;
            }

            S3Client s3 = ClienteS3.getInstance();

            // Eliminar archivos existentes en el mismo prefijo (nivel actual) si uniqueFile es true
            if (uniqueFile) {
                String prefijo = obtenerPrefijo(rutaConNombre); // ejemplo: repositorio/talento/123/
                logger.info("Unique file activado. Eliminando archivos previos en: " + prefijo);

                ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                        .bucket(bucketName)
                        .prefix(prefijo)
                        .build();

                ListObjectsV2Response listResponse = s3.listObjectsV2(listRequest);

                for (S3Object objeto : listResponse.contents()) {
                    String key = objeto.key();

                    // Solo eliminar si:
                    // - Está en el mismo nivel (sin subcarpetas)
                    // - Es un archivo (no termina en /)
                    if (esMismoNivel(key, prefijo) && !key.endsWith("/")) {
                        logger.info("Eliminando archivo previo: " + key);
                        s3.deleteObject(DeleteObjectRequest.builder()
                                .bucket(bucketName)
                                .key(key)
                                .build());
                    }
                }
            }
            logger.info("Ruta final para guardar: " + rutaConNombre);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(rutaConNombre)
                    .build();

            s3.putObject(putRequest, RequestBody.fromBytes(fileBytes));

            logger.info("Archivo guardado exitosamente en S3: " + rutaConNombre);
            logger.info("Fin Utilitarios - GuardarArchivoAws");
            logger.info(Constante.TXT_SEPARADOR);

            return true;
        } catch (IllegalArgumentException e) {
            logger.error("Cadena Base64 inválida: " + e.getMessage(), e);
            return false;
        } catch (Exception e) {
            logger.error("Error inesperado al subir archivo a S3: " + e.getMessage(), e);
            return false;
        }
    }


    // Extrae el prefijo padre (nivel actual)
    private static String obtenerPrefijo(String ruta) {
        int lastSlash = ruta.lastIndexOf('/');
        return (lastSlash != -1) ? ruta.substring(0, lastSlash + 1) : "";
    }

    // Verifica que esté en el mismo nivel (sin subcarpetas)
    private static boolean esMismoNivel(String key, String prefijo) {
        // Quita el prefijo y revisa que no haya otra barra
        String restante = key.substring(prefijo.length());
        return !restante.contains("/");
    }

    private static String obtenerExtension(String ruta) {
        int i = ruta.lastIndexOf('.');
        return (i > 0 && i < ruta.length() - 1) ? ruta.substring(i + 1) : "";
    }

    private static boolean esExtensionSoportada(String ext) {
        return esImagen(ext) || ext.equals("pdf");
    }

    private static boolean esImagen(String ext) {
        return ext.equals("png") || ext.equals("jpg") || ext.equals("jpeg") || ext.equals("webp");
    }
}
