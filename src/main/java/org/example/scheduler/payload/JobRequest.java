package org.example.scheduler.payload;

import lombok.Data;

@Data
public class JobRequest {
    private String jobName;
    private String parameters;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }
}
