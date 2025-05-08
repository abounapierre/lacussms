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
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.Assert;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

/**
 *
 * @author abouna
 */
@Configuration
@EnableTransactionManagement
@EnableEncryptableProperties
@PropertySources({
        @PropertySource("classpath:bd.properties"),
        @PropertySource("classpath:application.properties")
})
public class SpringMainConfig {

    private static final Logger log = LoggerFactory.getLogger(SpringMainConfig.class);
    
    public SpringMainConfig(){
        super();
    }
    
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(PathConfigBean pathConfigBean, Environment env) {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource(pathConfigBean, env));
        em.setPackagesToScan("com.abouna.lacussms.entities");
        final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(true);
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties(env));
        return em;
    }

    @Bean
    public DataSource dataSource(PathConfigBean pathConfigBean, Environment env) {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Preconditions.checkNotNull(env.getProperty("jdbc.driverClassName")));
        dataSource.setUrl(Preconditions.checkNotNull(getUrl(pathConfigBean)));
        dataSource.setUsername(Preconditions.checkNotNull(env.getProperty("jdbc.user")));
        dataSource.setPassword(Preconditions.checkNotNull(env.getProperty("jdbc.pass")));
        return dataSource;
    }

    private String getUrl(PathConfigBean pathConfigBean) {
        String url = "jdbc:h2:file:" + pathConfigBean.getRootPath() + "/data/lacus" + ";AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE";
        log.info("Database URL: {}", url);
        return url;
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

    final Properties additionalProperties(Environment env) {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        hibernateProperties.setProperty("hibernate.dialect", env.getProperty("hibernate.dialect"));
        // hibernateProperties.setProperty("hibernate.globally_quoted_identifiers", "true");
        return hibernateProperties;
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
    public String logoApp(Environment env) {
        return env.getProperty("application.logo");
    }

    @Bean("messageConfigPath")
    public String messageConfigPath(Environment env) {
        return env.getProperty("application.message.config.path");
    }

    @Bean
    public AppRunConfig getAppRunConfig() {
        return new AppRunConfig(Boolean.TRUE, Boolean.TRUE, Boolean.TRUE);
    }

    @Bean
    public SmsProvider getSmsProvider() {
        return new SmsProvider("1s2u");
    }

    @Bean
    public PathConfigBean getPathConfigBean(Environment env) {
        String[] profiles = env.getActiveProfiles();
        log.info("Active profiles: {}", (Object) profiles);
        Path path = Paths.get("").toAbsolutePath();
        Optional<String> profile = Stream.of(profiles).filter(prof -> prof.equals("dev") || prof.equals("prod")).findFirst();
        return new PathConfigBean(profile.map(pro -> {
            if (pro.equals("dev")) {
                return path.toString();
            } else {
                return path.getParent().toString();
            }
        }).orElse(path.toString()));
    }

    @Bean("external-configs")
    public Properties getProperties(PathConfigBean pathConfigBean) {
        try {
            String root = pathConfigBean.getRootPath() + "/configs";
            File folder = new File(root);
            File[] files = folder.listFiles(File::isFile);
            Assert.notNull(files, "Files cannot be null");
            FileSystemResource[] fileSystemResources = Arrays.stream(files).map(
                    file -> new FileSystemResource(root + "/" + file.getName())
            ).toArray(FileSystemResource[]::new);
            Properties properties = new Properties();
            for (FileSystemResource fileSystemResource : fileSystemResources) {
                properties.load(fileSystemResource.getInputStream());
            }
            return properties;
        } catch (Exception e) {
            log.error("Error loading properties files", e);
            throw new RuntimeException("Error loading properties files", e);
        }
    }
}
