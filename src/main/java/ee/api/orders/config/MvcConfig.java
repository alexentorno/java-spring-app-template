package ee.api.orders.config;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = "ee.api.orders")
@PropertySource("classpath:application.properties")
public class MvcConfig  {


    @Bean
    public int initializeDatabase(DataSource dataSource) {
        var populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);
        return 1;
    }

    @Bean
    public JdbcClient jdbcClient(DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

    @Bean
    public DataSource dataSource(Environment env) {
//        DriverManagerDataSource ds = new DriverManagerDataSource();
//
//        ds.setDriverClassName("org.hsqldb.jdbcDriver");
//        ds.setUrl(env.getProperty("hsql.url"));
//
//        return ds;

        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUsername(env.getProperty("postgres.user"));
        ds.setPassword(env.getProperty("postgres.pass"));
        ds.setUrl(env.getProperty("postgres.url"));

        return ds;
    }

}
