package org.robinbird.repository.dao;

import java.util.Properties;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import lombok.NonNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configuring Spring JPS with a non-boot project.
 * References.
 * - https://www.baeldung.com/the-persistence-layer-with-spring-and-jpa
 * - https://docs.jboss.org/hibernate/orm/3.6/quickstart/en-US/html/index.html
 * - https://docs.jboss.org/hibernate/orm/4.0/devguide/en-US/html/ch01.html
 * - https://www.javatips.net/blog/hibernate-jpa-with-h2-database
 */
@Configuration
@EnableJpaRepositories(basePackages = {"org.robinbird.repository.dao"})
@EnableTransactionManagement
public class DaoConfiguration {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource());
        em.setPackagesToScan(new String[] {"org.robinbird.repository.entity"});
        final JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:");
        dataSource.setUsername( "rb" );
        dataSource.setPassword( "" );
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager(@NonNull final EntityManagerFactory emf) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    private Properties additionalProperties() {
        final Properties properties = new Properties();
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        //properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.setProperty("show_sql", "true");
        properties.setProperty("hibernate.temp.use_jdbc_metadata_defaults", "false");
        properties.setProperty("spring.jpa.generate-ddl", "true");
        properties.setProperty("spring.jpa.hibernate.ddl-auto", "create-drop");
        properties.setProperty("logging.level.org.hibernate.SQL", "DEBUG");
        properties.setProperty("logging.level.org.hibernate.type.descriptor.sql.BasicBinder", "TRACE");
        return properties;
    }

    /*
    <persistence xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://java.sun.com/xml/ns/persistence http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
  version="2.0" xmlns="http://java.sun.com/xml/ns/persistence">
  <persistence-unit name="entity-manager" transaction-type="RESOURCE_LOCAL">
    <class>org.robinbird.main.oldrepository.dao.entity.CompositionTypeEntity</class>
    <class>org.robinbird.main.oldrepository.dao.entity.InstanceEntity</class>
    <class>org.robinbird.main.oldrepository.dao.entity.RelationEntity</class>
    <class>org.robinbird.main.oldrepository.dao.entity.TypeEntity</class>
    <properties>
      <property name="javax.persistence.jdbc.driver" value="org.h2.Driver" />
      <property name="javax.persistence.jdbc.url" value="jdbc:h2:mem:" />
      <property name="javax.persistence.jdbc.user" value="rb" />
      <property name="javax.persistence.jdbc.password" value="" />
      <property name="hibernate.dialect" value="org.hibernate.dialect.H2Dialect"/>
      <property name="hibernate.hbm2ddl.auto" value="create-drop" />
      <property name="show_sql" value="true"/>
      <property name="hibernate.temp.use_jdbc_metadata_defaults" value="false"/>
    </properties>
  </persistence-unit>
</persistence>
     */
}