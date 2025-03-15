package com.clara.ops.challenge.document_management_service_challenge.storage;

import com.clara.ops.challenge.document_management_service_challenge.exception.StorageException;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MinioStorageService implements StorageService {
  // With a set limit of 50mb I decided to read the files
  // in batches of 10mb
  private static final int TEN_MEGABYTES = 10000000;
  private final MinioClient minioClient;
  private static final String UPLOAD_ERROR_MESSAGE = "Error: error uploading file";
  private static final String DOWNLOAD_LINK_ERROR = "Error: error retreiving url";

  public MinioStorageService(Environment env) {
    minioClient =
        MinioClient.builder()
            .endpoint(env.getProperty("minio.url"))
            .credentials(env.getProperty("minio.accessKey"), env.getProperty("minio.secretKey"))
            .build();
  }

  @Override
  public void store(String user, String name, MultipartFile file) throws IOException {
    Path tempFile = Files.createTempFile(null, null);
    InputStream inputFile = file.getInputStream();
    byte[] buf = new byte[TEN_MEGABYTES];
    OutputStream outputFile = new FileOutputStream(tempFile.toString());

    int bytesRead = inputFile.read(buf);
    while (bytesRead != -1) {
      outputFile.write(buf, 0, bytesRead);
      outputFile.flush();
      bytesRead = inputFile.read(buf);
    }
    outputFile.close();
    inputFile.close();
    try {
      if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(user).build())) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(user).build());
      }
      minioClient.uploadObject(
          UploadObjectArgs.builder()
              .bucket(user)
              .object(name)
              .filename(tempFile.toString())
              .build());
    } catch (InvalidKeyException
        | ErrorResponseException
        | InsufficientDataException
        | InternalException
        | InvalidResponseException
        | NoSuchAlgorithmException
        | ServerException
        | XmlParserException
        | IllegalArgumentException
        | IOException e) {
      e.printStackTrace();
      throw new StorageException(UPLOAD_ERROR_MESSAGE);
    }
  }

  @Override
  public String load(String user, String name) {
    String url = null;

    try {
      url =
          minioClient.getPresignedObjectUrl(
              GetPresignedObjectUrlArgs.builder()
                  .bucket(user)
                  .object(name)
                  .method(Method.GET)
                  .build());
    } catch (InvalidKeyException
        | ErrorResponseException
        | InsufficientDataException
        | InternalException
        | InvalidResponseException
        | NoSuchAlgorithmException
        | XmlParserException
        | ServerException
        | IllegalArgumentException
        | IOException e) {
      throw new StorageException(DOWNLOAD_LINK_ERROR);
    }

    return url;
  }
}
