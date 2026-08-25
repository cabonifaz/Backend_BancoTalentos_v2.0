package com.bdt.bancotalentosbackend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Client S3 V2
 * This class is used to create a singleton S3 client and presigner
 * instance
 * for AWS S3 operations.
 * The presigner is used to generate pre-signed URLs for secure access
 * to S3 objects.
 *
 * <p>
 * La construcción es PEREZOSA a propósito. Antes vivía en un bloque
 * {@code static}: si faltaba {@code AWS_REGION} o las credenciales, el
 * inicializador lanzaba y la clase quedaba en estado erróneo, de modo que toda
 * llamada posterior recibía un {@code NoClassDefFoundError}. Eso es un
 * {@link Error}, no una {@link Exception}, así que ni el {@code catch} de
 * {@link S3Utils} ni el del controlador lo atrapaban: el endpoint respondía 500
 * sin decir qué faltaba. Construyendo bajo demanda, el fallo llega como
 * excepción normal y se traduce a un mensaje de negocio.
 */
public class ClientS3V2 {
  private static final Logger logger = LoggerFactory.getLogger(ClientS3V2.class);

  private static volatile S3Client s3Client;
  private static volatile S3Presigner s3Presigner;

  private ClientS3V2() {
  }

  /**
   * Resolves the region, logging loudly when the variable is missing instead of
   * falling back in silence.
   */
  private static Region resolveRegion() {
    String regionName = System.getenv("AWS_REGION");
    if (regionName == null || regionName.trim().isEmpty()) {
      logger.error("La variable de entorno AWS_REGION no está configurada; se usa us-east-1. "
          + "Si el bucket vive en otra región, la firma se calcula sobre otro endpoint y el PUT fallará.");
      return Region.US_EAST_1;
    }
    if (System.getenv("AWS_BUCKET") == null || System.getenv("AWS_BUCKET").trim().isEmpty()) {
      logger.error("La variable de entorno AWS_BUCKET no está configurada: no se podrá firmar ninguna URL.");
    }
    return Region.of(regionName.trim());
  }

  public static S3Client getInstance() {
    if (s3Client == null) {
      synchronized (ClientS3V2.class) {
        if (s3Client == null) {
          s3Client = S3Client.builder()
              .region(resolveRegion())
              .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
              .build();
        }
      }
    }
    return s3Client;
  }

  public static S3Presigner getPresignerInstance() {
    if (s3Presigner == null) {
      synchronized (ClientS3V2.class) {
        if (s3Presigner == null) {
          s3Presigner = S3Presigner.builder()
              .region(resolveRegion())
              .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
              .build();
        }
      }
    }
    return s3Presigner;
  }
}
