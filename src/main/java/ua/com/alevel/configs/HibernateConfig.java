package ua.com.alevel.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("ua.com.alevel.persistence.repositories")
public class HibernateConfig {

    private static final Logger logger = LoggerFactory.getLogger(HibernateConfig.class);

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driver;

    @Value("${spring.jpa.properties.hibernate.dialect}")
    private String dialect;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String hbm2ddl;

    @Value("${spring.jpa.show-sql}")
    private Boolean showSql;

    @Value("${spring.jpa.properties.hibernate.jdbc.max_size}")
    private String maxSize;

    @Value("${spring.jpa.properties.hibernate.jdbc.min_size}")
    private String minSize;

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private String batchSize;

    @Value("${spring.jpa.properties.hibernate.jdbc.fetch_size}")
    private String fetchSize;

    @Value("${spring.jpa.properties.hibernate.enable_lazy_load_no_trans}")
    private String lazyLoad;

    private static final String ENTITY_MANAGER_PACKAGES_TO_SCAN = "ua.com.alevel.persistence.entities";

    @Bean
    public DataSource dataSource() {
        logger.info("Initializing DataSource with URL: {}", jdbcUrl);
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        logger.info("DataSource initialized successfully");
        return dataSource;
    }

    @Bean(name = "entityManagerFactory")
    public LocalSessionFactoryBean sessionFactoryBean() {
        logger.info("Setting up Hibernate SessionFactory...");
        LocalSessionFactoryBean sessionFactoryBean = new LocalSessionFactoryBean();
        sessionFactoryBean.setDataSource(dataSource());
        sessionFactoryBean.setPackagesToScan(ENTITY_MANAGER_PACKAGES_TO_SCAN);
        sessionFactoryBean.setHibernateProperties(hibernateProperties());

        try {
            sessionFactoryBean.afterPropertiesSet();
            logger.info("Hibernate SessionFactory initialized successfully");
        } catch (IOException e) {
            logger.error("Failed to initialize Hibernate SessionFactory: {}", e.getMessage(), e);
        }

        return sessionFactoryBean;
    }

    @Bean
    public HibernateTransactionManager transactionManager() {
        logger.info("Configuring Hibernate TransactionManager...");
        HibernateTransactionManager manager = new HibernateTransactionManager();
        manager.setSessionFactory(sessionFactoryBean().getObject());
        logger.info("Hibernate TransactionManager configured successfully");
        return manager;
    }

    private Properties hibernateProperties() {
        logger.info("Loading Hibernate properties...");
        Properties properties = new Properties();
        properties.put("hibernate.dialect", dialect);
        properties.put("hibernate.hbm2ddl.auto", hbm2ddl);
        properties.put("hibernate.show_sql", showSql);
        properties.put("hibernate.enable_lazy_load_no_trans", lazyLoad);
        properties.put("hibernate.c3p0.max_size", maxSize);
        properties.put("hibernate.c3p0.min_size", minSize);
        properties.put("hibernate.jdbc.batch_size", batchSize);
        properties.put("hibernate.jdbc.fetch_size", fetchSize);
        logger.info("Hibernate properties loaded successfully");
        return properties;
    }
}
