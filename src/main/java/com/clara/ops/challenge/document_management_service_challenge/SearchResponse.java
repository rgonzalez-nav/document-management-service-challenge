package com.clara.ops.challenge.document_management_service_challenge;

import java.util.Set;

public class SearchResponse {
	Metadata metadata;
	Set<FileInfo> documents;
	
	public SearchResponse(Metadata metadata, Set<FileInfo> documents) {
		this.metadata = metadata;
		this.documents = documents;
	}
	
	public SearchResponse() {
		
	}

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public Set<FileInfo> getDocuments() {
		return documents;
	}

	public void setDocuments(Set<FileInfo> documents) {
		this.documents = documents;
	}
}
