package com.nixmash.blog.jpa.service.interfaces;

import com.nixmash.blog.jpa.model.BatchJob;

import java.util.List;

public interface StatService {

    List<BatchJob> getBatchJobsByJob(String jobName);
}
