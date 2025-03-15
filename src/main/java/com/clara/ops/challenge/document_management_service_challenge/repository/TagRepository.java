package com.clara.ops.challenge.document_management_service_challenge.repository;

import com.clara.ops.challenge.document_management_service_challenge.entity.Tag;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TagRepository extends CrudRepository<Tag, String> {
  List<Tag> findAllByNameIn(String[] name);

  Tag findByName(String name);
}
