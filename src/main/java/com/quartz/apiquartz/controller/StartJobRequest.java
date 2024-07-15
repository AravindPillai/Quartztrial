package com.quartz.apiquartz.controller;

public class StartJobRequest {

    private String jobName;
    private String jobGroup;
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getJobGroup() {
        return jobGroup;
    }
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }
    
    


}
