package com.bdt.bancotalentosbackend.util;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.translate.TranslateClient;

/**
 * Client Amazon Translate V2 (AWS SDK v2).
 *
 * Singleton del cliente de Amazon Translate, con la misma configuración de región
 * y credenciales por entorno que {@link ClientS3V2}.
 */
public class ClientTranslateV2 {
  private static final TranslateClient translateClient;

  static {
    String regionName = System.getenv("AWS_REGION");
    Region region = Region.of(regionName != null ? regionName : "us-east-1");

    translateClient = TranslateClient.builder()
        .region(region)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build();
  }

  private ClientTranslateV2() {
  }

  public static TranslateClient getInstance() {
    return translateClient;
  }
}
