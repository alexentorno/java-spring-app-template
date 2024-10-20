package ee;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import util.ConfigUtil;
import util.ConnectionInfo;
import util.FileUtil;

import javax.sql.DataSource;

@WebListener
@ComponentScan(basePackages = "ee.api.orders")
@PropertySource("classpath:application.properties")
public class AppContextListener implements ServletContextListener {

    private static ConnectionInfo getConnectionInfo() {
        return ConfigUtil.readConnectionInfo();
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
//        ConnectionInfo connectionInfo = getConnectionInfo();
//        System.out.println("Connecting to HSQLDB with URL: " + connectionInfo.getUrl());

        ApplicationContext context = new AnnotationConfigApplicationContext(AppContextListener.class);
        DataSource dataSource = context.getBean(DataSource.class);

        var populator = new ResourceDatabasePopulator(new ClassPathResource("schema.sql"));
        DatabasePopulatorUtils.execute(populator, dataSource);
        System.out.println("Schema created");
    }


    @Bean
    public JdbcClient jdbcClient(DataSource dataSource) {
        return JdbcClient.create(dataSource);
    }

    @Bean
    public DataSource dataSource(Environment env) {
        DriverManagerDataSource ds = new DriverManagerDataSource();

        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl(env.getProperty("hsql.url"));
        System.out.println("Url: " + ds.getUrl());

        return ds;

//        System.out.println("postgres");
//        DriverManagerDataSource ds = new DriverManagerDataSource();
//        ds.setDriverClassName("org.postgresql.Driver");
//        ds.setUsername(env.getProperty("postgres.user"));
//        ds.setPassword(env.getProperty("postgres.pass"));
//        ds.setUrl(env.getProperty("postgres.url"));
//
//        return ds;
    }
}
