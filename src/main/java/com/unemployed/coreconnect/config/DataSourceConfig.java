package com.unemployed.coreconnect.config;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;

@Configuration
@EnableRetry
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUsername;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String dbDriverClassName;

    @Value("${db.connection.retryContext.maxAttempts}")
    private int retryContentMaxAttempts;

    @Value("${db.connection.retryContext.BackoffDelay}")
    private int retryContextBackoffDelay;

    @Value("${db.connection.retry.maxAttempts}")
    private int retryMaxAttempts;

    @Bean
    @Retryable(
            value = {SQLException.class},
            maxAttemptsExpression = "${db.connection.retry.maxAttempts}",
            backoff = @Backoff(delayExpression = "${db.connection.retry.BackoffDelay}", multiplierExpression = "${db.connection.retry.BackoffMultiplier}")
    )
    public DataSource dataSource() throws SQLException {
        log.info(String.format("Establishing connection to %s database at %s", dbDriverClassName, dbUrl));

        RetryTemplate retryTemplate = new RetryTemplateBuilder().maxAttempts(retryContentMaxAttempts).fixedBackoff(retryContextBackoffDelay).withListener(new CustomRetryListener()).build();

        return retryTemplate.execute(context -> {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(dbDriverClassName);
            dataSource.setUrl(dbUrl);
            dataSource.setUsername(dbUsername);
            dataSource.setPassword(dbPassword);

            try (Connection connection = dataSource.getConnection()) {
                log.info(String.format("Connected to %s database at %s", connection.getMetaData().getDatabaseProductName(), connection.getMetaData().getURL()));

                return dataSource;
            }
        });

    }

    @Recover
    public DataSource recover(SQLException e) {
        log.error(String.format("Failed to connect to database after %s attempts. Exiting application.", retryMaxAttempts));
        
        System.exit(1);
        return null;
    }
}
