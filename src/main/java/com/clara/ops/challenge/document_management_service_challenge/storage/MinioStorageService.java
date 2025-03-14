package com.clara.ops.challenge.document_management_service_challenge.storage;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.clara.ops.challenge.document_management_service_challenge.exception.StorageException;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;

@Service
public class MinioStorageService implements StorageService{
	
	@Override
	public void store(String user, String name, MultipartFile file) throws IOException {
		Path tempFile = Files.createTempFile(null, null);
		Files.write(tempFile, file.getBytes());
		MinioClient minioClient = MinioClient.builder()
				.endpoint("http://localhost:9000")
				.credentials("2k1MiFtzVQDAGYsAcq2Y", "8dcRn0FhPG3KK4KRxtfQoRLQXdsR8ZHBGH7tiyJk")
				.build();
		try {
			minioClient.uploadObject(
					UploadObjectArgs.builder()
					.bucket(user)
					.object(name)
					.filename(tempFile.toString())
					.build()
					);
		} catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
				| InvalidResponseException | NoSuchAlgorithmException | ServerException | XmlParserException
				| IllegalArgumentException | IOException e) {
			e.printStackTrace();
			throw new StorageException("Error: error uploading file");
		}
				
	}
	
	@Override
	public String load(String user, String name) {
		MinioClient minioClient = MinioClient.builder()
				.endpoint("http://localhost:9000")
				.credentials("2k1MiFtzVQDAGYsAcq2Y", "8dcRn0FhPG3KK4KRxtfQoRLQXdsR8ZHBGH7tiyJk")
				.build();
		String url =  null;
		
		try {
			url = minioClient.getPresignedObjectUrl(
					GetPresignedObjectUrlArgs.builder()
					.bucket(user)
					.object(name)
					.method(Method.GET)
					.build());
		} catch (InvalidKeyException | ErrorResponseException | InsufficientDataException | InternalException
				| InvalidResponseException | NoSuchAlgorithmException | XmlParserException | ServerException
				| IllegalArgumentException | IOException e) {
			throw new StorageException("Error: error retreiving url");
		}
		
		return url;
	}
}
