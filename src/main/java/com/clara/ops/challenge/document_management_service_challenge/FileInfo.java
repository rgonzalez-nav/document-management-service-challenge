package com.clara.ops.challenge.document_management_service_challenge;

import java.util.Objects;
import java.util.Set;

public class FileInfo {
  private String id;
  private String user;
  private String name;
  private Set<String> tags;
  private Long size;
  private String type;
  private String createdAt;

  public FileInfo() {}

  public FileInfo(
      String id,
      String user,
      String name,
      Set<String> tags,
      Long size,
      String type,
      String createdAt) {
    super();
    this.id = id;
    this.user = user;
    this.name = name;
    this.tags = tags;
    this.size = size;
    this.type = type;
    this.createdAt = createdAt;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<String> getTags() {
    return tags;
  }

  public void setTags(Set<String> tags) {
    this.tags = tags;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    FileInfo other = (FileInfo) obj;
    return Objects.equals(id, other.id);
  }
}
