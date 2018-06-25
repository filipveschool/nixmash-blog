package com.nixmash.blog.jpa.repository;

import com.nixmash.blog.jpa.model.BatchJob;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BatchJobRepository extends CrudRepository<BatchJob, Long> {

    List<BatchJob> findByJobName(String jobName, Sort sort);

}
