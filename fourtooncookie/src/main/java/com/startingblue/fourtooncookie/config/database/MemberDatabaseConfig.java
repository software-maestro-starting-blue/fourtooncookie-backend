package com.startingblue.fourtooncookie.config.database;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        basePackages = "com.startingblue.fourtooncookie.member.domain",
        entityManagerFactoryRef = MemberDatabaseConfig.MEMBER_ENTITY_MANAGER_FACTORY,
        transactionManagerRef = MemberDatabaseConfig.MEMBER_TRANSACTION_MANAGER
)
public class MemberDatabaseConfig {

    protected static final String MEMBER_DATA_SOURCE = "memberDataSource";
    protected static final String MEMBER_ENTITY_MANAGER_FACTORY = "memberEntityManagerFactory";
    protected static final String MEMBER_TRANSACTION_MANAGER = "memberTransactionManager";
    @Value("${spring.datasource.member.hibernate.hbm2ddl.auto}")
    private String DDL_AUTO;
    @Value("${spring.datasource.member.hibernate.dialect}")
    private String DIALECT;

    @Bean(name = MEMBER_DATA_SOURCE)
    @ConfigurationProperties(prefix = "spring.datasource.member")
    public DataSource memberDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = MEMBER_ENTITY_MANAGER_FACTORY)
    public LocalContainerEntityManagerFactoryBean memberEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier(MEMBER_DATA_SOURCE) DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.startingblue.fourtooncookie.member.domain")
                .persistenceUnit("member")
                .properties(hibernateProperties())
                .build();
    }

    private Map<String, Object> hibernateProperties() {
        Map<String, Object> hibernateProps = new HashMap<>();
        hibernateProps.put("hibernate.hbm2ddl.auto", DDL_AUTO);
        hibernateProps.put("hibernate.dialect", DIALECT);

        return hibernateProps;
    }

    @Bean(name = MEMBER_TRANSACTION_MANAGER)
    public PlatformTransactionManager memberTransactionManager(
            @Qualifier(MEMBER_ENTITY_MANAGER_FACTORY) EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
