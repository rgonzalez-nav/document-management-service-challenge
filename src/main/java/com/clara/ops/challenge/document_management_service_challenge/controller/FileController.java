package com.clara.ops.challenge.document_management_service_challenge.controller;

import com.clara.ops.challenge.document_management_service_challenge.SearchResponse;
import com.clara.ops.challenge.document_management_service_challenge.service.FileService;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileController {

  @Autowired private FileService fileService;

  @PostMapping("/document-management/upload")
  public ResponseEntity<String> uploadFile(
      @RequestParam("file") MultipartFile file,
      @RequestParam("user") String user,
      @RequestParam("name") String name,
      @RequestParam("tags") String[] tags) {
    try {
      fileService.uploadFile(user, name, tags, file);
      return new ResponseEntity<String>(
          "The document was uploaded succesfully", HttpStatus.CREATED);
    } catch (IOException e) {
      e.printStackTrace();
      return new ResponseEntity<String>("Error creating file", HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PostMapping("/document-management/search")
  public ResponseEntity<SearchResponse> searchFile(
      @RequestParam("user") String user,
      @RequestParam("name") String name,
      @RequestParam("tags") String[] tags,
      @RequestParam("page") Integer page,
      @RequestParam("size") Integer size) {
    SearchResponse response = fileService.searchFile(user, name, tags, page, size);
    if (response.getDocuments().size() == 0) {
      return new ResponseEntity<SearchResponse>(response, HttpStatus.BAD_REQUEST);
    } else {
      return new ResponseEntity<SearchResponse>(response, HttpStatus.OK);
    }
  }

  @GetMapping("/document-management/download/{documentId}")
  public String downloadFile(@PathVariable("documentId") String id) {
    return fileService.downloadFile(id);
  }
}
