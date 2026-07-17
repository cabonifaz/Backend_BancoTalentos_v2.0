package com.bdt.bancotalentosbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
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
  private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
  private static final String BUCKET_NAME = System.getenv("AWS_BUCKET");

  /**
   * Generates a pre-signed URL for accessing (GET) an S3 object.
   *
   * @param fileUrl The S3 object key.
   * @return URL as a String.
   */
  public static String getSignedUrl(String fileUrl) {
    if (fileUrl == null || fileUrl.isEmpty())
      return "";

    try {
      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(1440)) // 24 hours
          .getObjectRequest(builder -> builder.bucket(BUCKET_NAME).key(fileUrl).build())
          .build();

      PresignedGetObjectRequest presignedRequest = ClientS3V2.getPresignerInstance().presignGetObject(presignRequest);

      logger.info("URL firmada generada exitosamente para: {}", fileUrl);
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
    if (fileUrl == null || fileUrl.isEmpty())
      return "";

    try {
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(fileUrl)
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
    if (fileUrl == null || fileUrl.isEmpty())
      return "";

    try {
      String fileName = fileUrl.contains("/") ? fileUrl.substring(fileUrl.lastIndexOf("/") + 1) : fileUrl;
      String contentType = resolveContentType(fileName);

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(fileUrl)
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
   * Resolves the MIME type from a file name extension for inline viewing.
   *
   * @param fileName The file name (may include extension).
   * @return The MIME type, or {@code application/octet-stream} if unknown.
   */
  private static String resolveContentType(String fileName) {
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
    return "application/octet-stream";
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
    if (fileUrl == null || fileUrl.isEmpty())
      return "";

    try {
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(fileUrl)
          .contentType(contentType)
          .build();

      PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(minutes))
          .putObjectRequest(putObjectRequest)
          .build();

      PresignedPutObjectRequest presignedRequest = ClientS3V2.getPresignerInstance().presignPutObject(presignRequest);

      logger.info("URL firmada de carga generada exitosamente para: {}", fileUrl);
      return presignedRequest.url().toString();

    } catch (Exception e) {
      logger.error("Error al generar URL firmada de carga: {}", e.getMessage());
      return "";
    }
  }

  /**
   * Checks whether an object physically exists in the bucket.
   *
   * @param fileUrl The S3 object key.
   * @return true if it exists, false otherwise.
   */
  public static boolean exists(String fileUrl) {
    if (fileUrl == null || fileUrl.isEmpty())
      return false;

    try {
      HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
          .bucket(BUCKET_NAME)
          .key(fileUrl)
          .build();
      ClientS3V2.getInstance().headObject(headObjectRequest);
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    } catch (S3Exception e) {
      logger.error("Error al verificar existencia en S3: {}", e.awsErrorDetails().errorMessage());
      return false;
    }
  }
}
