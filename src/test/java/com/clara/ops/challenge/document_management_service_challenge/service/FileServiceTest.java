package com.clara.ops.challenge.document_management_service_challenge.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.clara.ops.challenge.document_management_service_challenge.SearchResponse;
import com.clara.ops.challenge.document_management_service_challenge.entity.File;
import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;
import com.clara.ops.challenge.document_management_service_challenge.exception.StorageException;
import com.clara.ops.challenge.document_management_service_challenge.exception.StorageFileNotFoundException;
import com.clara.ops.challenge.document_management_service_challenge.repository.FileRepository;
import com.clara.ops.challenge.document_management_service_challenge.repository.TagRepository;
import com.clara.ops.challenge.document_management_service_challenge.storage.StorageService;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
public class FileServiceTest {

  @MockitoBean FileRepository fileRepo;

  @MockitoBean StorageService storage;

  @MockitoBean TagRepository tagRepo;

  @Mock MultipartFile fileMock;

  @Autowired FileService fileService;

  @BeforeEach
  public void setUp() {
    Tag tag1 = new Tag("important");
    tag1.setId("123tag");
    Tag tag2 = new Tag("personal");
    tag2.setId("newId56");
    Tag tag3 = new Tag("educational");
    tag3.setId("otherId987");
    when(storage.load(anyString(), anyString())).thenReturn("http://fake.url.com");
    when(tagRepo.findByName("important")).thenReturn(tag1);
    when(tagRepo.findByName("educational")).thenReturn(tag3);
    when(tagRepo.save(any(Tag.class))).thenReturn(tag2);
    File fileResult = new File();
    fileResult.setName("example.pdf");
    fileResult.setType("pdf");
    fileResult.setMinIOPath("http://fake.file.url");
    when(fileRepo.save(any(File.class))).thenReturn(fileResult);

    when(fileMock.getOriginalFilename()).thenReturn("fakeName.pdf");
  }

  @Test
  public void testUploadFile() throws IOException {
    String[] tags = new String[3];
    assertDoesNotThrow(() -> fileService.uploadFile("rafa", "example.pdf", tags, fileMock));
    File file = fileService.uploadFile("rafa", "example.pdf", tags, fileMock);

    assertNotNull(file);
    assertEquals(file.getName(), "example.pdf");
    assertEquals(file.getType(), "pdf");
  }

  @Test
  public void testUploadFileThrowsIOException() throws IOException {
    String[] tags = new String[3];
    doThrow(IOException.class)
        .when(storage)
        .store(anyString(), anyString(), any(MultipartFile.class));
    assertThrows(
        IOException.class, () -> fileService.uploadFile("rafa", "example.pdf", tags, fileMock));
  }

  @Test
  public void testCreateFileThrowStorageException() {
    String[] tags = new String[3];
    assertThrows(
        StorageException.class, () -> fileService.uploadFile("rafa", "example.pdf", tags, null));
  }

  @Test
  public void testCreateFile() throws IOException {
    String[] tags = new String[3];
    File file = fileService.uploadFile("rafa", "example.pdf", tags, fileMock);
    assertEquals(file.getMinIOPath(), "http://fake.file.url");
  }

  @Test
  public void testCreateTags() throws IOException {
    String[] tags = new String[] {"important", "personal", "educational"};
    Tag tag1 = new Tag("important");
    tag1.setId("123tag");
    Tag tag2 = new Tag("personal");
    tag2.setId("newId56");
    Tag tag3 = new Tag("educational");
    tag3.setId("otherId987");
    File fileResult = new File();
    fileResult.setName("example.pdf");
    fileResult.setType("pdf");
    fileResult.setMinIOPath("http://fake.file.url");
    fileResult.addTag(tag1);
    fileResult.addTag(tag2);
    fileResult.addTag(tag3);
    when(fileRepo.save(any(File.class))).thenReturn(fileResult);
    File file = fileService.uploadFile("rafa", "example.pdf", tags, fileMock);
    List<Tag> expectedTags = List.of(tag1, tag2, tag3);
    assertEquals(file.getTags(), expectedTags);
  }

  @Test
  public void tesDownloadFile() {
    File fileWithURL = new File();
    fileWithURL.setMinIOPath("http://fake.file.url.com");
    Optional<File> expectedResult = Optional.of(fileWithURL);
    when(fileRepo.findById(anyString())).thenReturn(expectedResult);
    String fakeUrl = fileService.downloadFile("id1");

    assertEquals(fakeUrl, "http://fake.file.url.com");
  }

  @Test
  public void testDownloadFileThrowsException() {
    Optional<File> expectedResult = Optional.empty();
    when(fileRepo.findById(anyString())).thenReturn(expectedResult);
    assertThrows(StorageFileNotFoundException.class, () -> fileService.downloadFile("id1"));
  }

  @Test
  public void testSearchFile() {
    File fileResult = new File();
    fileResult.setCreatedAt(new Date());
    when(fileRepo.findAllByUserLikeIgnoreCaseAndNameLikeIgnoreCaseAndTagsIdIn(
            anyString(), anyString(), anySet()))
        .thenReturn(List.of(fileResult));
    SearchResponse response = fileService.searchFile("rafa", "example.pdf", new String[1], 1, 3);
    assertEquals(response.getDocuments().size(), 1);
  }

  @Test
  public void testSearchFileNoFilters() {
    File fileResult = new File();
    fileResult.setCreatedAt(new Date());
    Iterable<File> allFiles = List.of(fileResult);

    when(fileRepo.findAll()).thenReturn(allFiles);
    SearchResponse response = fileService.searchFile("", "", new String[0], 1, 3);
    assertEquals(response.getDocuments().size(), 1);
  }

  @Test
  public void testSearchFileBiggerOffsetThanItems() {
    File fileResult = new File();
    fileResult.setCreatedAt(new Date());
    when(fileRepo.findAllByUserLikeIgnoreCaseAndNameLikeIgnoreCaseAndTagsIdIn(
            anyString(), anyString(), anySet()))
        .thenReturn(List.of(fileResult));
    SearchResponse response = fileService.searchFile("rafa", "example.pdf", new String[1], 2, 3);
    assertEquals(response.getDocuments().size(), 0);
  }
}
