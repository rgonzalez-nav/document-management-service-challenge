package com.clara.ops.challenge.document_management_service_challenge.repository;

import com.clara.ops.challenge.document_management_service_challenge.entity.File;
import java.util.List;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface FileRepository extends CrudRepository<File, String> {

  List<File> findAllByUserLikeIgnoreCaseAndNameLikeIgnoreCaseAndTagsIdIn(
      String user, String name, Set<String> ids);
}
