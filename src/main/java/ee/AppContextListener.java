package ee;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import util.ConfigUtil;
import util.ConnectionInfo;
import util.FileUtil;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

@WebListener
public class AppContextListener implements ServletContextListener {

    private static Connection getConnection() throws SQLException {
        ConnectionInfo connectionInfo = ConfigUtil.readConnectionInfo();
        return DriverManager.getConnection(
                connectionInfo.getUrl(),
                connectionInfo.getUser(),
                connectionInfo.getPass());
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {

            String s1 = FileUtil.readFileFromClasspath("schema.sql");

            stmt.executeUpdate(s1);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
