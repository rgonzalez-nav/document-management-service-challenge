package com.clara.ops.challenge.document_management_service_challenge.storage;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
	void store(String user, String name, MultipartFile file) throws IOException;
	String load(String user, String name);
}
