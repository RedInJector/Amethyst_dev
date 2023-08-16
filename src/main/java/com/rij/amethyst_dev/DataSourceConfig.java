package com.rij.amethyst_dev;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {


    @Bean(name = "datasource1")
    @ConfigurationProperties("spring.datasource")
    @Primary
    public DataSource dataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "datasource2")
    @ConfigurationProperties("spring.datasource.plan")
    public DataSource dataSource2(){
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean(name = "datasource3")
    @ConfigurationProperties("spring.datasource.libertybans")
    public DataSource dataSource3(){
        return DataSourceBuilder
                .create()
                .build();
    }
}
