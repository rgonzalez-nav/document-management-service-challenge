package com.clara.ops.challenge.document_management_service_challenge.service;

import com.clara.ops.challenge.document_management_service_challenge.FileInfo;
import com.clara.ops.challenge.document_management_service_challenge.Metadata;
import com.clara.ops.challenge.document_management_service_challenge.SearchResponse;
import com.clara.ops.challenge.document_management_service_challenge.entity.File;
import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;
import com.clara.ops.challenge.document_management_service_challenge.exception.StorageException;
import com.clara.ops.challenge.document_management_service_challenge.exception.StorageFileNotFoundException;
import com.clara.ops.challenge.document_management_service_challenge.model.FileModel;
import com.clara.ops.challenge.document_management_service_challenge.repository.FileRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.TagRepository;
import com.clara.ops.challenge.document_management_service_challenge.storage.StorageService;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
  @Autowired private FileRepository fileRepository;

  @Autowired private TagRepository tagRepository;

  @Autowired private StorageService storageService;

  @Autowired private FileModel model;

  public File uploadFile(String user, String name, String[] tags, MultipartFile file)
      throws IOException {
    storageService.store(user.toLowerCase(), name, file);
    String url = storageService.load(user, name);
    return createFile(user, name, tags, file, url);
  }

  private File createFile(
      String user, String name, String[] tags, MultipartFile file, String minIoPath) {
    if (file == null) {
      throw new StorageException("Error: File is empty");
    }

    long size = file.getSize();
    String fileName = file.getOriginalFilename();
    int lastPoint = file.getOriginalFilename().lastIndexOf(".");
    String type = fileName.substring(lastPoint + 1, fileName.length());

    List<Tag> tagList = createTags(tags);
    File fileData = new File(user, name, minIoPath, size, type);
    fileData.setTags(tagList);
    File newFile = fileRepository.save(fileData);
    return newFile;
  }

  private List<Tag> createTags(String tags[]) {
    List<Tag> tagList = new ArrayList<Tag>();
    for (String tagName : tags) {
      Tag tag;
      if ((tag = tagRepository.findByName(tagName)) == null) {
        tag = tagRepository.save(new Tag(tagName));
      }
      tagList.add(tag);
    }
    return tagList;
  }

  public String downloadFile(String id) {
    File fileData = null;
    try {
      fileData = fileRepository.findById(id).get();
    } catch (NoSuchElementException exception) {
      throw new StorageFileNotFoundException("File not found");
    }

    return fileData.getMinIOPath();
  }

  public SearchResponse searchFile(String user, String name, String[] tags, int page, int size) {
    List<File> files = new ArrayList<File>();
    if (user.isEmpty() && name.isEmpty() && tags.length == 0) {
      files = Lists.newArrayList(fileRepository.findAll());
    } else {
      List<Tag> tagList = tagRepository.findAllByNameIn(tags);
      Set<String> tagIds = tagList.stream().map(tag -> tag.getId()).collect(Collectors.toSet());
      files =
          fileRepository.findAllByUserLikeIgnoreCaseAndNameLikeIgnoreCaseAndTagsIdIn(
              "%" + user + "%", "%" + name + "%", tagIds);
    }

    files =
        files.stream()
            .sorted(Comparator.comparing(File::getCreatedAt).reversed())
            .collect(Collectors.toList());

    int limit = size * page;
    int offset = (page - 1) * size;
    int totalItems = files.size();
    if (limit > totalItems) {
      limit = totalItems;
    }
    Set<FileInfo> documents;
    if (offset > totalItems) {
      documents = new LinkedHashSet<FileInfo>();
    } else {
      files = files.subList(offset, limit);
      documents = model.setModels(files);
    }
    Metadata metadata = packageMetadata(page, size, totalItems, documents.size());

    return new SearchResponse(metadata, documents);
  }

  private Metadata packageMetadata(int page, int size, int totalItems, int currentItems) {
    Metadata metadata = new Metadata();
    metadata.setCurrentPage(page);
    metadata.setItemsPerPage(size);
    metadata.setTotalItems(totalItems);
    metadata.setTotalPages(totalItems % size == 0 ? totalItems / size : (totalItems / size) + 1);
    metadata.setCurrentItems(currentItems);
    return metadata;
  }
}
