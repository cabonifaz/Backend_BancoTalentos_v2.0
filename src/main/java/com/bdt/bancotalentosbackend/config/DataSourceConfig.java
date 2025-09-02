package com.bdt.bancotalentosbackend.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
public class DataSourceConfig {
    @Bean
    @Profile("preprod")
    @ConfigurationProperties("spring.datasource.preprod")
    public DataSourceProperties activeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public HikariDataSource activeDataSource(@Qualifier("activeDataSourceProperties") DataSourceProperties blogDataSourceProperties) {
        return blogDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    @Primary
    public JdbcTemplate activeJdbcTemplate(@Qualifier("activeDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
