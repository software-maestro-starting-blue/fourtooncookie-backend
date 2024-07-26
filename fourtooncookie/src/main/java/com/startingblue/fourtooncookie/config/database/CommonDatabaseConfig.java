package com.startingblue.fourtooncookie.config.database;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = {"com.startingblue.fourtooncookie.artwork.domain",
                "com.startingblue.fourtooncookie.character.domain",
                "com.startingblue.fourtooncookie.diary.domain"},
        entityManagerFactoryRef = CommonDatabaseConfig.COMMON_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = CommonDatabaseConfig.COMMON_TRANSACTION_MANAGER
)
public class CommonDatabaseConfig {

    protected static final String COMMON_ENTITY_MANAGER_FACTORY = "commonEntityManagerFactory";
    protected static final String COMMON_TRANSACTION_MANAGER = "commonTransactionManager";
    protected static final String COMMON_DATA_SOURCE = "commonDataSource";
    private static final String[] BASE_PACKAGES = {
            "com.startingblue.fourtooncookie.artwork.domain",
            "com.startingblue.fourtooncookie.character.domain",
            "com.startingblue.fourtooncookie.diary.domain"};

    @Value("${spring.datasource.common.hibernate.hbm2ddl.auto}")
    private String DDL_AUTO;
    @Value("${spring.datasource.common.hibernate.dialect}")
    private String DIALECT;

    @Primary
    @Bean(name = COMMON_DATA_SOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.common")
    public DataSource commonDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = COMMON_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean commonEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(COMMON_DATA_SOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages(BASE_PACKAGES)
                .persistenceUnit("common")
                .properties(hibernateProperties())
                .build();
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> hibernateProps = new HashMap<>();
        hibernateProps.put("hibernate.hbm2ddl.auto", DDL_AUTO);
        hibernateProps.put("hibernate.dialect", DIALECT);
        return hibernateProps;
    }

    @Primary
    @Bean(name = "commonTransactionManager")
    public PlatformTransactionManager commonTransactionManager(
            @Qualifier(COMMON_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
