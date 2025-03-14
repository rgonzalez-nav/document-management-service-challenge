package com.clara.ops.challenge.document_management_service_challenge.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tag")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;
	
	private String name;
	
	@ManyToMany(mappedBy = "tags")
	private List<File> files = new ArrayList<File>();

	public Tag(String name) {
		this.name = name;
	}
	
	public Tag() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<File> getFiles() {
		return files;
	}

	public void setFiles(List<File> files) {
		this.files = files;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
