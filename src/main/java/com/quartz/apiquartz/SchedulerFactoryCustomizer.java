package com.quartz.apiquartz;

// import javax.sql.DataSource;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.quartz.SchedulerFactoryBeanCustomizer;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class SchedulerFactoryCustomizer implements SchedulerFactoryBeanCustomizer{

    // @Autowired
    // DataSource dataSource;

    
    @Override
    public void customize(SchedulerFactoryBean schedulerFactoryBean) {
        //schedulerFactoryBean.setDataSource(dataSource);
    }

}
