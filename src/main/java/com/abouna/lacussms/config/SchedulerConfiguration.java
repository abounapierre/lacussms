package com.abouna.lacussms.config;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.TriggerFiredBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class SchedulerConfiguration {

    public static class AutowireCapableBeanJobFactory extends SpringBeanJobFactory {

        private final AutowireCapableBeanFactory beanFactory;

        @Autowired
        public AutowireCapableBeanJobFactory(AutowireCapableBeanFactory beanFactory) {
            Assert.notNull(beanFactory, "Bean factory must not be null");
            this.beanFactory = beanFactory;
        }

        @Override
        protected Object createJobInstance(TriggerFiredBundle bundle) throws Exception {
            Object jobInstance = super.createJobInstance(bundle);
            this.beanFactory.autowireBean(jobInstance);
            this.beanFactory.initializeBean(jobInstance, null);
            return jobInstance;
        }
    }

    @Bean
    public SchedulerFactoryBean schedulerFactory(ApplicationContext applicationContext, DataSource quartzDataSource) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();
        schedulerFactoryBean.setJobFactory(new AutowireCapableBeanJobFactory(applicationContext.getAutowireCapableBeanFactory()));
        schedulerFactoryBean.setDataSource(quartzDataSource);
        return schedulerFactoryBean;
    }

    @Bean
    public Scheduler scheduler(ApplicationContext applicationContext, DataSource quartzDataSource) throws SchedulerException {
        Scheduler scheduler = schedulerFactory(applicationContext, quartzDataSource).getScheduler();
        scheduler.start();
        return scheduler;
    }

    @Bean
    public Properties quartzProperties() throws IOException
    {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }
}
