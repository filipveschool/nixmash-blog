package com.nixmash.blog.jpa.service.implementations;

import com.nixmash.blog.jpa.model.BatchJob;
import com.nixmash.blog.jpa.repository.BatchJobRepository;
import com.nixmash.blog.jpa.service.interfaces.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service("statService")
@Transactional
public class StatServiceImpl implements StatService {

    @Autowired
    private BatchJobRepository batchJobRepository;

    @Override
    public List<BatchJob> getBatchJobsByJob(String jobName) {
            return batchJobRepository.findByJobName(jobName, sortByJobStartDateDesc());
    }

    private Sort sortByJobStartDateDesc() {
        return new Sort(Sort.Direction.DESC, "startTime");
    }

}

