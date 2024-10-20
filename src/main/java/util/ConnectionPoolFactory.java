package util;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.sql.SQLException;

public class ConnectionPoolFactory {
    private static volatile BasicDataSource pool; //Singleton instance of the connection pool

    public DataSource createConnectionPool() {
        if (pool == null) {
            synchronized (ConnectionPoolFactory.class) {
                if (pool == null) {
                    System.out.println("POOL");
                    pool = new BasicDataSource();
                    ConnectionInfo connectionInfo = ConfigUtil.readConnectionInfo();
                    pool.setDriverClassName("org.postgresql.Driver");
                    pool.setUrl(connectionInfo.getUrl());
                    pool.setUsername(connectionInfo.getUser());
                    pool.setPassword(connectionInfo.getPass());
                    pool.setMaxTotal(2);
                    pool.setInitialSize(2);
                    try {
                        pool.getLogWriter();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return pool;
    }
}
