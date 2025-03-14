package com.clara.ops.challenge.document_management_service_challenge.exception;

public class StorageFileNotFoundException extends StorageException{
	public StorageFileNotFoundException(String message) {
		super(message);
	}

	public StorageFileNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
