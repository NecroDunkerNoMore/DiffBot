package org.ndnm.diffbot.spring;


import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.AuthenticationMethod;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;


@Configuration
@EnableTransactionManagement
@PropertySource(value = {"classpath:org/ndnm/diffbot/diffbot.properties", "classpath:org/ndnm/diffbot/security.properties"})
@ComponentScan({"org.ndnm.diffbot.model", "org.ndnm.diffbot.model.diff", "org.ndnm.diffbot.service.impl", "org.ndnm.diffbot.dao.impl",
        "org.ndnm.diffbot.spring", "org.ndnm.diffbot.app", "org.ndnm.diffbot.util"})
public class DiffBotConfiguration {
    private final Environment environment;


    @Autowired
    public DiffBotConfiguration(Environment environment) {
        this.environment = environment;
    }


    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("jdbc.url"));
        dataSource.setUsername(environment.getRequiredProperty("jdbc.username"));
        dataSource.setPassword(environment.getRequiredProperty("jdbc.password"));
        return dataSource;
    }


    // Used for bootstrapping DB
    @Bean(name = "rootDataSource")
    public DataSource rootDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(environment.getRequiredProperty("jdbc.driverClassName"));
        dataSource.setUrl(environment.getRequiredProperty("jdbc.root.url"));
        dataSource.setUsername(environment.getRequiredProperty("jdbc.root.username"));
        dataSource.setPassword(environment.getRequiredProperty("jdbc.root.password"));
        return dataSource;
    }


    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setPackagesToScan("org.ndnm.diffbot.model", "org.ndnm.diffbot.model.diff", "org.ndnm.diffbot.service.impl", "org.ndnm.diffbot.dao.impl",
                "org.ndnm.diffbot.spring", "org.ndnm.diffbot.app", "org.ndnm.diffbot.util");
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setJpaProperties(jpaProperties());
        return factoryBean;
    }


    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }


    @Bean
    @Autowired
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager txManager = new JpaTransactionManager();
        txManager.setEntityManagerFactory(emf);
        return txManager;
    }


    @Bean
    public Credentials getCredentials() {
        return new Credentials(AuthenticationMethod.SCRIPT,
                environment.getRequiredProperty("reddit.username"),
                environment.getRequiredProperty("reddit.password"),
                environment.getRequiredProperty("reddit.clientId"),
                environment.getRequiredProperty("reddit.clientSecret"),
                null, //deviceId, not used by us
                environment.getRequiredProperty("reddit.redirectUrl"));
    }


    @Bean
    public UserAgent getUserAgent() {
        return UserAgent.of("desktop", "org.ndnm.diffbot", getDiffbotVersion(), "DiffBot");
    }


    @Bean
    public RedditClient getRedditClient() {
        return new RedditClient(getUserAgent());
    }


    @Bean(name = "botsRedditUsername")
    public String getBotsRedditUsername() {
        return environment.getRequiredProperty("reddit.username");
    }


    @Bean(name = "diffBotVersion")
    public String getDiffbotVersion() {
        return environment.getRequiredProperty("bot.version");
    }


    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
        return properties;
    }

}
