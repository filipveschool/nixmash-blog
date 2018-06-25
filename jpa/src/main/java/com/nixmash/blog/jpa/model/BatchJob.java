package com.nixmash.blog.jpa.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Getter
@Setter
@Entity
@Table(name = "v_batch_job_report")
public class BatchJob {

    @Id
    @Column(name = "JOB_INSTANCE_ID", nullable = false)
    private long jobId;

    @Basic
    @Column(name = "JOB_NAME", nullable = false, length = 100)
    private String jobName;

    @Basic
    @Column(name = "START_TIME", nullable = true)
    private Timestamp startTime;

    @Basic
    @Column(name = "END_TIME", nullable = true)
    private Timestamp endTime;

    @Basic
    @Column(name = "STATUS", nullable = true, length = 10)
    private String status;

    @Basic
    @Column(name = "EXIT_CODE", nullable = true, length = 2500)
    private String exitCode;

    @Basic
    @Column(name = "EXIT_MESSAGE", nullable = true, length = 2500)
    private String exitMessage;

    public BatchJob() {

    }

    @Override
    public String toString() {
        return "BatchJob{" +
                "jobId=" + jobId +
                ", jobName='" + jobName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status='" + status + '\'' +
                ", exitCode='" + exitCode + '\'' +
                ", exitMessage='" + exitMessage + '\'' +
                '}';
    }
}
