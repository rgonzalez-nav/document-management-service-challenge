package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.FileInfo;
import com.clara.ops.challenge.document_management_service_challenge.SearchResponse;
import com.clara.ops.challenge.document_management_service_challenge.service.FileService;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {
	
	@Autowired
	private FileService fileService;

  @PostMapping("/document-management/upload")
  public String uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("user") String user,
      @RequestParam("name") String name,
      @RequestParam("tags") String[] tags) {
	  try {
		fileService.uploadFile(user, name, tags, file);
	} catch (IOException e) {
		e.printStackTrace();
	}
    return "The document was uploaded succesfully";
  }

  @PostMapping("/document-management/search")
  public SearchResponse searchFile(
      @RequestParam("user") String user,
      @RequestParam("name") String name,
      @RequestParam("tags") String[] tags,
      @RequestParam("page") Integer page,
      @RequestParam("size") Integer size) {
	 
    return fileService.searchFile(user, name, tags, page, size);
  }

  @GetMapping("/document-management/download/{documentId}")
  public String downloadFile(@PathVariable("documentId") String id) {
	  return fileService.downloadFile(id);
  }
}
