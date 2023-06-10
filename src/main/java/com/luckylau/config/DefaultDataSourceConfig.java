package com.luckylau.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.luckylau.generate.DynamicMapperSqlSessionBean;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@MapperScan(basePackages = {"com.luckylau.dao"}, sqlSessionTemplateRef = "defaultSqlSessionTemplate")
@EnableTransactionManagement
public class DefaultDataSourceConfig {

    @Bean("defaultDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.default")
    public DataSource dataSource() {
        return new DruidDataSource();
    }

    @Bean("defaultSqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory(@Qualifier("defaultDataSource") DataSource dataSource)
            throws Exception {
        SqlSessionFactoryBean factoryBean = new DynamicMapperSqlSessionBean("");
        factoryBean.setDataSource(dataSource);
        factoryBean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCallSettersOnNulls(true);
        factoryBean.setConfiguration(configuration);
        factoryBean.setTypeAliasesPackage("com.luckylau.dbmodel");
        return factoryBean.getObject();
    }

    @Bean("defaultSqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(@Qualifier("defaultSqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean("defaultJdbcTemplate")
    public JdbcTemplate jdbcTemplate(@Qualifier("defaultDataSource") DataSource dataSource) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        return jdbcTemplate;
    }

    @Bean(name = "defaultTransactionManager")
    public DataSourceTransactionManager transactionManagerPrimary(@Qualifier("defaultDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}