package com.clara.ops.challenge.document_management_service_challenge.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.clara.ops.challenge.document_management_service_challenge.FileInfo;
import com.clara.ops.challenge.document_management_service_challenge.Metadata;
import com.clara.ops.challenge.document_management_service_challenge.SearchResponse;
import com.clara.ops.challenge.document_management_service_challenge.entity.File;
import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;
import com.clara.ops.challenge.document_management_service_challenge.exception.StorageException;
import com.clara.ops.challenge.document_management_service_challenge.model.FileModel;
import com.clara.ops.challenge.document_management_service_challenge.repository.FileRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.TagRepository;
import com.clara.ops.challenge.document_management_service_challenge.storage.StorageService;
import com.google.common.collect.Lists;

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
	
	public SearchResponse searchFile(String user, String name, String[] tags, int page, int size) {
		List<File> files = new ArrayList<File>();
		if(user.isEmpty() && name.isEmpty() && tags.length == 0) {
			files = Lists.newArrayList(fileRepository.findAll());
		}else {
		
			if(!user.isEmpty() || !name.isEmpty()) {
				files = fileRepository.findAllByUserLikeIgnoreCaseAndNameLikeIgnoreCase("%"+user+"%", "%"+name+"%");
			}
			
			files.addAll(filterByTags(tags));
		}
		
		orderList(files);
		Metadata metadata = packageMetadata(page, size, files);
		
		size *= page;
		page = (page-1) * size;
		if(size > files.size()) {
			size = files.size();
		}
		Set<FileInfo> documents;
		if(page > files.size()) {
			documents = new LinkedHashSet<FileInfo>();
		}else {
			files = files.subList(page, size);
			documents = model.setModels(files);
		}
		
		metadata.setCurrentItems(documents.size());
		
		return new SearchResponse(metadata, documents);
	}
	
	private List<File> filterByTags(String[] tags){
		List<Tag> tagList = tagRepository.findAllByNameIn(tags);
		Set<String> tagIds = tagList.stream().map(tag -> tag.getId()).collect(Collectors.toSet());
		return fileRepository.findFilesByTagsIdIn(tagIds);
	}
	
	private void orderList(List<File> files){
		Set<File> notDuplicatedFiles = new HashSet<File>(files);
		files.clear();
		files.addAll(notDuplicatedFiles);
		files = files.stream().sorted(Comparator.comparing(File::getCreatedAt).reversed())
				.collect(Collectors.toList());
	}
	
	private Metadata packageMetadata(int page, int size, List<File> files) {
		Metadata metadata = new Metadata();
		metadata.setCurrentPage(page);
		metadata.setItemsPerPage(size);
		metadata.setTotalItems(files.size());
		int filesQuantity = files.size();
		metadata.setTotalPages(filesQuantity%size == 0 ? filesQuantity/size : (filesQuantity/size)+1);
		return metadata;
	}
}
