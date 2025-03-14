package com.clara.ops.challenge.document_management_service_challenge.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.clara.ops.challenge.document_management_service_challenge.FileInfo;
import com.clara.ops.challenge.document_management_service_challenge.entity.File;
import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;
import com.clara.ops.challenge.document_management_service_challenge.exception.StorageException;
import com.clara.ops.challenge.document_management_service_challenge.model.FileModel;
import com.clara.ops.challenge.document_management_service_challenge.repository.FileRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.TagRepository;
import com.clara.ops.challenge.document_management_service_challenge.storage.StorageService;

@Service
public class FileService {
	@Autowired
	private FileRepository fileRepository;
	
	@Autowired
	private TagRepository tagRepository;
	
	@Autowired
	private StorageService storageService;
	
	@Autowired
	private FileModel model;
	
	public void uploadFile(String user, String name, String[] tags, MultipartFile file) throws IOException {
		storageService.store(user, name, file);
		String url = storageService.load(user, name);
		createFile(user, name, tags, file, url);
	}
	
	public void createFile(String user, String name, String[] tags, MultipartFile file, String minIoPath) {
		if(file.isEmpty()) {
			throw new StorageException("Error: File is empty");
		}
		
		long size = file.getSize();
		String fileName = file.getOriginalFilename();
		int lastPoint = file.getOriginalFilename().lastIndexOf(".");
		String type = fileName.substring(lastPoint+1, fileName.length());
		
		List<Tag> tagList = Arrays.asList(tags).stream().map(tagName -> new Tag(tagName)).collect(Collectors.toList());
		File fileData = new File(user, name, minIoPath, size, type);
		fileData.setTags(tagList);
		fileRepository.save(fileData);
	}
	
	public String downloadFile(String id) {
		File fileData = fileRepository.findById(id).get();
		return fileData.getMinIOPath();
	}
	
	public Set<FileInfo> searchFile(String user, String name, String[] tags, int page, int size) {
		List<File> files = new ArrayList<File>();
		if(!user.isEmpty() || !name.isEmpty()) {
			files = fileRepository.findAllByUserLikeIgnoreCaseAndNameLikeIgnoreCase("%"+user+"%", "%"+name+"%");
		}
		List<Tag> tagList = tagRepository.findAllByNameIn(tags);
		Set<String> tagIds = tagList.stream().map(tag -> tag.getId()).collect(Collectors.toSet());
		files.addAll(fileRepository.findFilesByTagsIdIn(tagIds));
		files = files.stream().sorted(Comparator.comparing(File::getCreatedAt).reversed())
				.collect(Collectors.toList());
		
		return model.setModels(files);
	}
}
