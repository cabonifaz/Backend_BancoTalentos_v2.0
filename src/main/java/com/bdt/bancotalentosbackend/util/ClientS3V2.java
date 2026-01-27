package com.bdt.bancotalentosbackend.util;

import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * Client S3 V2
 * 
 * @author Jean Smith
 *         This class is used to create a singleton S3 client and presigner
 *         instance
 *         for AWS S3 operations.
 *         The presigner is used to generate pre-signed URLs for secure access
 *         to S3 objects.
 */
public class ClientS3V2 {
  private static final S3Client s3Client;
  private static final S3Presigner s3Presigner;

  static {
    String regionName = System.getenv("AWS_REGION");
    Region region = Region.of(regionName != null ? regionName : "us-east-1");

    s3Client = S3Client.builder()
        .region(region)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build();

    s3Presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
        .build();
  }

  private ClientS3V2() {
  }

  public static S3Client getInstance() {
    return s3Client;
  }

  public static S3Presigner getPresignerInstance() {
    return s3Presigner;
  }
}
