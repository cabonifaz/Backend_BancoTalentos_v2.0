package com.bdt.bancotalentosbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/**
 * S3 Utilities
 *
 * @author Jean Smith
 *         This class provides utility methods for AWS S3 operations,
 *         including generating pre-signed URLs for secure access to S3 objects.
 *         This class is more faster than {@link FileUtils} because it uses
 *         pre-signed URLs.
 */

public class S3Utils {
  private static final Logger logger = LoggerFactory.getLogger(S3Utils.class);
  private static final String BUCKET_NAME = System.getenv("AWS_BUCKET");

  /**
   * Validates that a key can actually be signed, and returns it trimmed.
   *
   * <p>
   * Las rutas no siempre las construye este backend: en el reemplazo de CV y en
   * las descargas vienen de la BD, donde conviven filas heredadas de la época
   * base64. Lo que se rechaza aquí:
   *
   * <ul>
   * <li>El marcador {@code [ID]} sin sustituir. Es el fallo real de
   * {@code addOrUpdateTalent}: concatena la constante de carpeta sin reemplazar
   * el marcador, así que la key puede acabar como
   * {@code repositorio/talento/[ID]/foto.png}. La URL firmada sale bien formada
   * (el SDK codifica los corchetes) pero apunta a un objeto que no existe.</li>
   * <li>Rutas de sistema de archivos o URLs completas ({@code C:\...},
   * {@code https://...}), que no son keys de S3.</li>
   * <li>Saltos de línea y caracteres de control, que sí romperían la petición.</li>
   * <li>La barra inicial, que en S3 crea un nivel de carpeta con nombre vacío.</li>
   * </ul>
   *
   * @param fileUrl The candidate S3 object key.
   * @return The trimmed key, or {@code null} if it cannot be signed.
   */
  private static String validateKey(String fileUrl) {
    if (fileUrl == null) {
      return null;
    }
    String key = fileUrl.trim();
    if (key.isEmpty()) {
      return null;
    }
    if (BUCKET_NAME == null || BUCKET_NAME.trim().isEmpty()) {
      logger.error("AWS_BUCKET no está configurada: no se puede firmar la ruta {}", key);
      return null;
    }
    if (key.contains("[ID]")) {
      logger.error("Ruta S3 con el marcador [ID] sin sustituir: {}", key);
      return null;
    }
    if (key.startsWith("/") || key.contains("://") || key.contains("\\")) {
      logger.error("Ruta S3 inválida (no es una key relativa del bucket): {}", key);
      return null;
    }
    for (int i = 0; i < key.length(); i++) {
      if (key.charAt(i) < 0x20 || key.charAt(i) == 0x7F) {
        logger.error("Ruta S3 con caracteres de control en la posición {}: {}", i, key);
        return null;
      }
    }
    return key;
  }

  /**
   * Generates a pre-signed URL for accessing (GET) an S3 object.
   *
   * @param fileUrl The S3 object key.
   * @return URL as a String.
   */
  public static String getSignedUrl(String fileUrl) {
    String key = validateKey(fileUrl);
    if (key == null)
      return "";

    try {
      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(1440)) // 24 hours
          .getObjectRequest(builder -> builder.bucket(BUCKET_NAME).key(key))
          .build();

      PresignedGetObjectRequest presignedRequest = ClientS3V2.getPresignerInstance().presignGetObject(presignRequest);

      logger.info("URL firmada generada exitosamente para: {}", key);
      return presignedRequest.url().toString();

    } catch (Exception e) {
      logger.error("Error al generar URL firmada: {}", e.getMessage());
      return "";
    }
  }

  /**
   * Generates a pre-signed URL for accessing (GET) an S3 object with a custom
   * expiration time.
   *
   * @param fileUrl The S3 object key.
   * @param minutes Expiration in minutes.
   * @return URL as a String.
   */
  public static String getSignedUrl(String fileUrl, int minutes) {
    String key = validateKey(fileUrl);
    if (key == null)
      return "";

    try {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(key)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(minutes))
          .getObjectRequest(getObjectRequest)
          .build();

      PresignedGetObjectRequest presignedRequest = ClientS3V2.getPresignerInstance().presignGetObject(presignRequest);
      return presignedRequest.url().toString();

    } catch (Exception e) {
      logger.error("Error al generar URL firmada de descarga: {}", e.getMessage());
      return "";
    }
  }

  /**
   * Generates a pre-signed URL for viewing (GET) an S3 object inline in the
   * browser (PDF/image viewer) instead of forcing a download.
   *
   * <p>
   * It overrides the response headers so the object is served with
   * {@code Content-Disposition: inline} and a {@code Content-Type} derived from
   * the file extension. This guarantees inline rendering even for objects that
   * were stored in S3 with a generic content type (e.g. legacy/migrated files
   * saved as {@code application/octet-stream}).
   *
   * @param fileUrl The S3 object key.
   * @param minutes Expiration in minutes.
   * @return URL as a String, or empty string on error.
   */
  public static String getSignedUrlInline(String fileUrl, int minutes) {
    String key = validateKey(fileUrl);
    if (key == null)
      return "";

    try {
      String fileName = key.contains("/") ? key.substring(key.lastIndexOf("/") + 1) : key;
      String contentType = resolveContentType(fileName);

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(key)
          .responseContentType(contentType)
          .responseContentDisposition("inline; filename=\"" + fileName + "\"")
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(minutes))
          .getObjectRequest(getObjectRequest)
          .build();

      PresignedGetObjectRequest presignedRequest = ClientS3V2.getPresignerInstance().presignGetObject(presignRequest);
      return presignedRequest.url().toString();

    } catch (Exception e) {
      logger.error("Error al generar URL firmada inline: {}", e.getMessage());
      return "";
    }
  }

  /**
   * Resolves the MIME type from a file name extension.
   *
   * <p>
   * Used both for inline viewing (GET) and to decide the content-type an upload
   * URL is signed with. The browser leaves {@code File.type} empty for
   * extensions it does not know, and signing with an empty content-type produces
   * a signature the PUT can never match, so the server always resolves a
   * concrete value here.
   *
   * @param fileName The file name (may include extension).
   * @return The MIME type, or {@code application/octet-stream} if unknown.
   */
  public static String resolveContentType(String fileName) {
    String lower = fileName == null ? "" : fileName.toLowerCase();
    if (lower.endsWith(".pdf"))
      return "application/pdf";
    if (lower.endsWith(".jpg") || lower.endsWith(".jpeg"))
      return "image/jpeg";
    if (lower.endsWith(".png"))
      return "image/png";
    if (lower.endsWith(".gif"))
      return "image/gif";
    if (lower.endsWith(".webp"))
      return "image/webp";
    if (lower.endsWith(".doc"))
      return "application/msword";
    if (lower.endsWith(".docx"))
      return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    if (lower.endsWith(".xls"))
      return "application/vnd.ms-excel";
    if (lower.endsWith(".xlsx"))
      return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    if (lower.endsWith(".zip"))
      return "application/zip";
    return "application/octet-stream";
  }

  /**
   * Extracts the extension, lowercased and WITHOUT the dot, discarding any path
   * embedded in the name first.
   *
   * <p>
   * Ported from {@code RequirementService} in FMI, where it guards the postulant
   * file uploads.
   *
   * @param name The file name, possibly with a path.
   * @return The extension without the dot, or an empty string if there is none.
   */
  public static String extractExtension(String name) {
    if (name == null) {
      return "";
    }
    int slash = Math.max(name.lastIndexOf('/'), name.lastIndexOf('\\'));
    String base = slash >= 0 ? name.substring(slash + 1) : name;
    int dot = base.lastIndexOf('.');
    return dot >= 0 ? base.substring(dot + 1).toLowerCase() : "";
  }

  /**
   * Sanitizes a file name: discards any path, restricts it to safe characters
   * [A-Za-z0-9._-], caps the length and keeps the extension.
   *
   * <p>
   * Without this, a name carrying accents, spaces, parentheses or {@code ../}
   * ended up verbatim in the S3 key: the signed URL carried those characters
   * percent-encoded, and the object could be written outside its own folder.
   *
   * @param originalFilename The name as the client sent it.
   * @param extension        The extension without the dot (see
   *                         {@link #extractExtension(String)}).
   * @return A safe file name.
   */
  public static String sanitizeFileName(String originalFilename, String extension) {
    if (originalFilename == null) {
      return extension == null || extension.isEmpty() ? "archivo" : "archivo." + extension;
    }
    int slash = Math.max(originalFilename.lastIndexOf('/'), originalFilename.lastIndexOf('\\'));
    String base = slash >= 0 ? originalFilename.substring(slash + 1) : originalFilename;
    int dot = base.lastIndexOf('.');
    String namePart = dot >= 0 ? base.substring(0, dot) : base;

    namePart = namePart.replaceAll("[^A-Za-z0-9._-]", "_");
    if (namePart.isEmpty()) {
      namePart = "archivo";
    }
    if (namePart.length() > 80) {
      namePart = namePart.substring(0, 80);
    }
    return extension == null || extension.isEmpty() ? namePart : namePart + "." + extension;
  }

  /**
   * Generates a pre-signed URL to upload (PUT) a file directly to S3.
   * The client must send the file with the same Content-Type used here.
   *
   * @param fileUrl     The S3 object key (destination path).
   * @param contentType The MIME type the client will use in the PUT request.
   * @param minutes     Expiration in minutes.
   * @return Pre-signed PUT URL as a String, or empty string on error.
   */
  public static String getUploadSignedUrl(String fileUrl, String contentType, int minutes) {
    String key = validateKey(fileUrl);
    if (key == null)
      return "";

    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(key)
          // OJO: el content-type entra en X-Amz-SignedHeaders (la lista de
          // cabeceras que el firmante ignora es sólo connection, x-amzn-trace-id,
          // user-agent, expect y transfer-encoding). El cliente debe mandar
          // exactamente este valor o S3 responde SignatureDoesNotMatch.
          .contentType(contentType)
          .build();

      PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(minutes))
          .putObjectRequest(putObjectRequest)
          .build();

      PresignedPutObjectRequest presignedRequest = ClientS3V2.getPresignerInstance().presignPutObject(presignRequest);

      logger.info("URL firmada de carga generada exitosamente para: {}", key);
      return presignedRequest.url().toString();

    } catch (Exception e) {
      logger.error("Error al generar URL firmada de carga: {}", e.getMessage());
      return "";
    }
  }

  /**
   * Deletes an object from the bucket. Used to remove the previous CV file when
   * it is replaced by a new one stored under a different key.
   *
   * @param fileUrl The S3 object key.
   * @return true on success (or if empty), false on error.
   */
  public static boolean delete(String fileUrl) {
    String key = fileUrl == null ? null : fileUrl.trim();
    if (key == null || key.isEmpty())
      return true;

    try {
      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(key)
          .build();
      ClientS3V2.getInstance().deleteObject(deleteObjectRequest);
      logger.info("Objeto eliminado de S3: {}", key);
      return true;

    } catch (Exception e) {
      logger.error("Error al eliminar objeto de S3: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Checks whether an object physically exists in the bucket.
   *
   * @param fileUrl The S3 object key.
   * @return true if it exists, false otherwise.
   */
  public static boolean exists(String fileUrl) {
    return headObject(fileUrl) != null;
  }

  /**
   * Returns an object's metadata (HEAD), or {@code null} if it does not exist.
   *
   * <p>
   * Same call {@link #exists(String)} makes, but keeping the response: existence
   * and size are checked in a single round trip instead of two.
   *
   * @param fileUrl The S3 object key.
   * @return The metadata, or {@code null} if missing/unreadable.
   */
  public static HeadObjectResponse headObject(String fileUrl) {
    // Se recorta igual que en validateKey: si no, una ruta con espacio final se
    // firmaría con una key y se comprobaría con otra.
    String key = fileUrl == null ? null : fileUrl.trim();
    if (key == null || key.isEmpty())
      return null;

    try {
      HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(key)
          .build();
      return ClientS3V2.getInstance().headObject(headObjectRequest);
    } catch (NoSuchKeyException e) {
      return null;
    } catch (S3Exception e) {
      // Un 403 aquí NO significa que el objeto falte: es que el rol IAM no tiene
      // s3:GetObject sobre esa key. Se registra el código para poder distinguirlo.
      logger.error("Error al obtener metadata en S3 (status {}): {}",
          e.statusCode(), e.awsErrorDetails().errorMessage());
      return null;
    }
  }
}
