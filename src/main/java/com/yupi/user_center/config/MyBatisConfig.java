package com.yupi.user_center.config;
//
//
import org.apache.ibatis.jdbc.SqlRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import java.sql.SQLException;


/**
 * @author leikooo
 */
@Configuration
public class MyBatisConfig {
    @Bean
    public SqlRunner sqlRunner(DataSource dataSource) throws SQLException {
        return new org.apache.ibatis.jdbc.SqlRunner(dataSource.getConnection());
    }
}