package com.bdt.bancotalentosbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;

import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

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
   * Generates a pre-signed URL for accessing an S3 object.
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
}
