/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.abouna.lacussms.config;

import com.abouna.lacussms.views.main.LogFile;
import com.abouna.lacussms.views.tools.ConstantUtils;
import com.google.common.base.Preconditions;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.nio.file.FileSystems;
import java.util.Properties;

/**
 *
 * @author abouna
 */
@Configuration
@EnableScheduling
@EnableTransactionManagement
@EnableEncryptableProperties
@PropertySources({
        @PropertySource("classpath:application.properties")
})
public class SpringMainConfig {
    private static final Logger logger = LoggerFactory.getLogger(SpringMainConfig.class);
    @Autowired
    private Environment env;
    
    public SpringMainConfig(){
        super();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan("com.abouna.lacussms.entities");
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        String driver = env.getProperty("jdbc.driverClassName");
        String url = env.getProperty("jdbc.url");
        String user = env.getProperty("jdbc.user");
        String pass = env.getProperty("jdbc.pass");
        logger.debug("### driver {} url {} user {} pass {} ###", driver, url, user, pass);
        dataSource.setDriverClassName(Preconditions.checkNotNull(driver));
        dataSource.setUrl(Preconditions.checkNotNull(url));
        dataSource.setUsername(Preconditions.checkNotNull(user));
        dataSource.setPassword(Preconditions.checkNotNull(pass));
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    final Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        // hibernateProperties.setProperty("hibernate.globally_quoted_identifiers", "true");
        return hibernateProperties;
    }
    
    @Bean
    public Tache execute(){
        return new Tache();
    }

    @Bean("logPath")
    public String logPath() {
        String fileSeparator = FileSystems.getDefault().getSeparator();
        return System.getProperty("user.home")
                .concat(fileSeparator).concat(".lacuss")
                .concat(fileSeparator).concat("lacuss-application.log");
    }

    @Bean
    public LogFile getLogger() {
        return new LogFile();
    }

    @Bean(name = "jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(ConstantUtils.SECRET_KEY);
        config.setAlgorithm("PBEWithMD5AndDES");
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName("SunJCE");
        config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
        config.setStringOutputType("base64");
        encryptor.setConfig(config);
        return encryptor;
    }

    @Bean("logo")
    public String logoApp() {
        return env.getProperty("application.logo");
    }

    @Bean(name = "licence")
    public String getLicence() {
        return env.getProperty("application.validDate");
    }
}
