package com.clara.ops.challenge.document_management_service_challenge.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "file")
public class File {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;
  
  @Column(name="userName")
  private String user;
  private String name;
  
  @Column(columnDefinition = "TEXT")
  private String minIOPath;
  private long size;
  private String type;
  
  @ManyToMany(cascade = {
		  CascadeType.PERSIST,
		  CascadeType.MERGE
  })
  @JoinTable(name = "file_tag",
  joinColumns = @JoinColumn(name = "file_id"),
  inverseJoinColumns = @JoinColumn(name = "tag_id"))
  private List<Tag> tags = new ArrayList<>();

  private Date createdAt;

	public File(String user, String name, String minIOPath, long size, String type) {
		this.user = user;
		this.name = name;
		this.minIOPath = minIOPath;
		this.size = size;
		this.type = type;
	}
  
	public File() {
		
	}
	
	@PrePersist
	protected void onCreate() {
		createdAt = new Date();
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

	public String getMinIOPath() {
		return minIOPath;
	}

	public void setMinIOPath(String minIOPath) {
		this.minIOPath = minIOPath;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public void addTag(Tag tag) {
		tags.add(tag);
		tag.getFiles().add(this);
	}
	
	public void removeTag(Tag tag) {
		tags.remove(tag);
		tag.getFiles().remove(this);
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof File)) return false;
        return id != null && id.equals(((File) o).getId());
    }
 
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
