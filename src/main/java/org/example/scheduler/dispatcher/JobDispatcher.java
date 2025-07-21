package org.example.scheduler.dispatcher;

import org.example.scheduler.model.Job;
import org.springframework.stereotype.Component;

@Component
public class JobDispatcher {
    public void dispatch(Job job) {
        // Randomly simulate a failure (30% chance)
        if (Math.random() < 0.3) {
            throw new RuntimeException("Simulated job dispatch failure for job: " + job.getJobName());
        }

        // Simulate actual processing (e.g., 2 seconds)
        try {
            Thread.sleep(2000); // Simulate processing time
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // You can also log or print here
        System.out.println("Dispatched job: " + job.getJobName() + " by " + Thread.currentThread().getName());
    }
}
