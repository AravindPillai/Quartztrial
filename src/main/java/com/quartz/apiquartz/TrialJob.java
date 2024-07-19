package com.quartz.apiquartz;

import java.util.concurrent.atomic.AtomicInteger;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//@DisallowConcurrentExecution
public class TrialJob implements Job {

    Logger logger = LoggerFactory.getLogger(getClass());
    public static final long EXECUTION_TIME = 5000L;
    private AtomicInteger count = new AtomicInteger();

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
      
        
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        if(dataMap.get("param1")!=null && dataMap.get("param2")!=null)
        {
            logger.info("Job ** {} ** fired @ {} with {} and {}", context.getJobDetail().getKey().getName(), context.getFireTime(),dataMap.get("param1"),dataMap.get("param2"));
        }
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            logger.error("Error while executing sample job", e);
        } finally {
            count.incrementAndGet();
            logger.info("Sample job has finished...");
        }

        logger.info("Next job scheduled @ {}", context.getNextFireTime());
    }

}
