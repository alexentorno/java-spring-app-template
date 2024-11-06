package ee.api.orders.config;

import org.hsqldb.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@PropertySource("classpath:application.properties")
public class HsqlDataSource {

    @Bean
    public Server hsqlServer() {
        System.out.println("SERVER STARTED RUNNING....");
        Server server = new Server();
        server.setDatabasePath(0, "mem:db1;sql.syntax_pgs=true");
        server.setDatabaseName(0, "db1");
        server.start();
        return server;
    }

    @Bean
    public DataSource dataSource(Environment env) {
        hsqlServer();
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl(env.getProperty("hsql.url"));
        return ds;
    }

    @Bean("dialect")
    public String dialect() {
        return "org.hibernate.dialect.HSQLDialect";
    }

}