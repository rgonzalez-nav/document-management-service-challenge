package com.clara.ops.challenge.document_management_service_challenge.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.clara.ops.challenge.document_management_service_challenge.FileInfo;
import com.clara.ops.challenge.document_management_service_challenge.entity.File;
import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;

@Service
public class FileModel {
	public FileInfo setModel(File file) {
		Set<String> tags = file.getTags().stream().map(tag -> tag.getName()).collect(Collectors.toSet());
		//TODO: Podria cambiar para usar el patron builder en FileInfo
		return new FileInfo(
				file.getId(),
				file.getUser(), 
				file.getName(), 
				tags,
				file.getSize(), 
				file.getType(), 
				file.getCreatedAt().toString());
	}
	
	public Set<FileInfo> setModels(List<File> files){
		Set<FileInfo> filesData = new LinkedHashSet<FileInfo>();
		for(File file : files) {
			filesData.add(setModel(file));
		}
		return filesData;
	}
}
