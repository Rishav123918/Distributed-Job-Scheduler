package org.example.scheduler.service;

import org.example.scheduler.dispatcher.JobDispatcher;
import org.example.scheduler.model.Job;
import org.example.scheduler.model.JobStatus;
import org.example.scheduler.payload.JobRequest;
import org.example.scheduler.repository.JobRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service

public class JobService {
    private final JobRepository jobRepository;
    private final ExecutorService executorService;
    private final AtomicInteger workerIndex = new AtomicInteger(0);
    private final List<String> workers = List.of("worker-1", "worker-2", "worker-3");

    private final JobDispatcher jobDispatcher;

    public JobService(JobRepository jobRepository, ExecutorService executorService, JobDispatcher jobDispatcher) {
        this.jobRepository = jobRepository;
        this.executorService = executorService;
        this.jobDispatcher = jobDispatcher;
    }
    public Job saveJob(Job job) {
        return jobRepository.save(job);
    }
    public Job getJobById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Job not found with ID: " + id));
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job submitJob(JobRequest jobRequest) {
        Job job = new Job(
                jobRequest.getJobName(),
                jobRequest.getParameters(),
                JobStatus.QUEUED
        );

        job.setCreatedTime(LocalDateTime.now());
        String assignedWorker = assignWorker();
        job.setAssignedWorker(assignedWorker);

        Job savedJob = jobRepository.save(job);

        // Submit the job to thread pool
        executorService.submit(() -> processJob(savedJob.getId()));

        return savedJob;
    }

    private String assignWorker() {
        int index = workerIndex.getAndUpdate(i -> (i + 1) % workers.size());
        return workers.get(index);
    }

    @Async("jobExecutor")
    public void processJob(Long jobId) {
        try {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found with ID: " + jobId));

            // 🧵 Log current thread info
            System.out.println("[START] Processing job ID: " + jobId +
                    " on Thread: " + Thread.currentThread().getName());

            job.setStatus(JobStatus.IN_PROGRESS);
            job.setStartTime(LocalDateTime.now());
            jobRepository.save(job);

            // Simulate processing time (1–3 seconds)
            Thread.sleep(1000 + (int) (Math.random() * 2000));

            // Simulate possible failure (30% chance)
            if (Math.random() < 0.3) {
                throw new RuntimeException("Simulated failure");
            }

            job.setStatus(JobStatus.COMPLETED);
            job.setEndTime(LocalDateTime.now());
            jobRepository.save(job);

            System.out.println("[SUCCESS] Job ID: " + jobId + " completed on Thread: " + Thread.currentThread().getName());

        } catch (Exception e) {
            Job job = jobRepository.findById(jobId).orElse(null);
            if (job != null) {
                job.setStatus(JobStatus.FAILED);
                job.setEndTime(LocalDateTime.now());
                jobRepository.save(job);

                System.out.println("[FAILED] Job ID: " + jobId + " failed on Thread: " + Thread.currentThread().getName() +
                        " - Reason: " + e.getMessage());
            }
        }
    }

}
