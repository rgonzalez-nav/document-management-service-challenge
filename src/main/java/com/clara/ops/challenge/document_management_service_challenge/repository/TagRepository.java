package com.clara.ops.challenge.document_management_service_challenge.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;

public interface TagRepository extends CrudRepository<Tag, String>{
	List<Tag> findAllByNameIn(String[] name);
	Tag findByName(String name);
}
