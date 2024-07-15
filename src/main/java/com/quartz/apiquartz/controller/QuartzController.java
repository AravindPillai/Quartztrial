package com.quartz.apiquartz.controller;

import javax.sql.DataSource;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.quartz.apiquartz.TrialJob;
import com.quartz.apiquartz.config.AutoWiringSpringBeanJobFactory;



@Controller
public class QuartzController {

    @Autowired
    org.quartz.Scheduler scheduler;

    @Autowired
    SpringBeanJobFactory springBeanJobFactory;

    @Autowired
    DataSource dataSource;

    @Autowired
    private ApplicationContext applicationContext;

    Logger logger = LoggerFactory.getLogger(getClass());

    // @RequestMapping(value = "/getJob", method = RequestMethod.GET)
    // public ResponseEntity getRunningJob() 
    // {
    //     System.out.println(scheduler);
    //     boolean jobExists = false;
    //     try {
    //         scheduler.pauseJob(new JobKey("Qrtz_Job_Detail", "Migration"));
    //         jobExists = scheduler.checkExists(new JobKey("Qrtz_Job_Detail", "Migration"));
    //         if(jobExists)
    //          scheduler.deleteJob(new JobKey("Qrtz_Job_Detail", "Migration"));
    //          jobExists = scheduler.checkExists(new JobKey("Qrtz_Job_Detail", "Migration"));
    //     } catch (SchedulerException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
       
    //     return new ResponseEntity<>(jobExists,HttpStatus.OK);
    // }

    @RequestMapping(value = "/updateJob", method = RequestMethod.POST)
    public ResponseEntity updateJob(@RequestBody StartJobRequest request) throws SchedulerException 
    {
        System.out.println("Updating job");
        JobKey jobKey = new JobKey("Qrtz_Job_Detail", "Default");
        JobDetail oldJobDetail = scheduler.getJobDetail(jobKey);
        
        if (oldJobDetail != null) {
            // Update job data map with new value
            JobDataMap jobDataMap = oldJobDetail.getJobDataMap();
            jobDataMap.put("param1", request.getJobGroup());
            jobDataMap.put("param2", request.getJobName());
            JobBuilder jb = oldJobDetail.getJobBuilder();
            JobDetail newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build();
            // Update the job detail
            scheduler.addJob(newJobDetail, true); // true indicates to replace existing job if exists
            
        }

        return new ResponseEntity<>(request,HttpStatus.OK);

    }

    @RequestMapping(value = "/startJob", method = RequestMethod.POST)
    public ResponseEntity startJob(@RequestBody StartJobRequest request) throws SchedulerException 
    {

        System.out.println("Starting job");
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("myKey", "myValue");
     
      JobDetail jobDetail = JobBuilder.newJob(TrialJob.class)
                .withIdentity(request.getJobGroup(), request.getJobName())
                .usingJobData("param1", request.getJobGroup())
                .usingJobData("param2", request.getJobName())
                .build();


        SimpleTrigger trigger = TriggerBuilder.newTrigger().withIdentity("Qrtz_Trigger").forJob(jobDetail).startNow().withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(10).repeatForever()).build();

        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setConfigLocation(new ClassPathResource("quartz.properties"));

        logger.debug("Setting the Scheduler up");
        schedulerFactoryBean.setJobFactory(springBeanJobFactory());
        //schedulerFactory.setJobDetails(job);
        //schedulerFactory.setTriggers(trigger);

        // Comment the following line to use the default Quartz job store.
        schedulerFactoryBean.setDataSource(dataSource);
        //Scheduler scheduler1=schedulerFactoryBean.
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
         // Schedule the job with the trigger
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.start();

        return new ResponseEntity<>(request,HttpStatus.OK);
      
    }

    
    public SpringBeanJobFactory springBeanJobFactory() {
        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
        logger.debug("Configuring Job factory");

        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

}
