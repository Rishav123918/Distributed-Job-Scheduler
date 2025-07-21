package org.example.scheduler.controller;

import lombok.RequiredArgsConstructor;
import org.example.scheduler.model.Job;
import org.example.scheduler.model.JobStatus;
import org.example.scheduler.payload.JobRequest;
import org.example.scheduler.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
public class JobController {
    private final JobService jobService;

    @PostMapping("/submit-multiple")
    public ResponseEntity<List<Job>> submitMultipleJobs(@RequestBody List<Job> jobRequests) {
        List<Job> savedJobs = jobRequests.stream().map(job -> {
            job.setStatus(JobStatus.PENDING);
            job.setCreatedTime(LocalDateTime.now());
            Job savedJob = jobService.saveJob(job);
            jobService.processJob(savedJob.getId()); // Will run asynchronously
            return savedJob;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(savedJobs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Job> getJob(@PathVariable Long id) {
        Job job = jobService.getJobById(id);
        if (job == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(job);
    }
    @GetMapping
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }
}
