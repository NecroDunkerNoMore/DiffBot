package org.ndnm.diffbot.spring;


import java.util.Properties;

import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
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


    @Bean(name = "diffBotVersion")
    public String getDiffbotVersion() {
        return environment.getRequiredProperty("bot.version");
    }


    @Bean(name = "authSleepIntervalInMillis")
    public long getAuthSleepIntervalInMillis() {
        return Long.parseLong(environment.getRequiredProperty("auth.sleep.interval"));
    }


    @Bean(name = "diffPollingIntervalInMillis")
    public long getDiffPollingIntervalInMillis() {
        return Long.parseLong(environment.getRequiredProperty("diff.polling.interval"));
    }


    @Bean(name = "redditPollingIntervalInMillis")
    public long getRedditPollingIntervalInMillis() {
        return Long.parseLong(environment.getRequiredProperty("reddit.polling.interval"));
    }


    @Bean(name = "oauthRefreshIntervalInMillis")
    public long getOauthRefreshIntervalInMillis() {
        return Long.parseLong(environment.getRequiredProperty("oauth.polling.interval"));
    }


    @Bean(name = "mainLoopIntervalInMillis")
    public long getMainLoopIntervalInMillis() {
        return Long.parseLong(environment.getRequiredProperty("main.loop.polling.interval"));
    }


    @Bean(name = "maxAuthAttempts")
    public int getMaxAuthAttempts() {
        return Integer.parseInt(environment.getRequiredProperty("max.auth.attempts"));
    }


    @Bean(name = "userAgentString")
    public String getUserAgentString() {
        return environment.getRequiredProperty("user.agent.string");
    }


    @Bean(name="isNotifySubscribersEnabled")
    public boolean isNotifySubscribersEnabled() {
        String value = environment.getRequiredProperty("notify.subscribers.enabled");
        if (StringUtils.isBlank(value)) {
            return false;
        } else if (value.toLowerCase().equals("true")) {
            return true;
        }

        return false;
    }

    @Bean(name="isArchivingEnabled")
    public boolean isArchivingEnabled() {
        String value = environment.getRequiredProperty("archiving.enabled");
        if (StringUtils.isBlank(value)) {
            return false;
        } else {
            return value.toLowerCase().equals("true");
        }

    }


    private Properties jpaProperties() {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", environment.getRequiredProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", environment.getRequiredProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", environment.getRequiredProperty("hibernate.format_sql"));
        return properties;
    }

}
