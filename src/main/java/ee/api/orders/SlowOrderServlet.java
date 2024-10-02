package ee.api.orders;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;
import util.ConnectionPoolFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/api/orders/slow")
public class SlowOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DataSource pool = new ConnectionPoolFactory().createConnectionPool();

        try (Connection conn = pool.getConnection()) {
            printPoolInfo(pool);

            Thread.sleep(1000);

        } catch (SQLException | InterruptedException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{ \"error\": \"Internal Server Error\" }");
        }
    }

    private static void printPoolInfo(DataSource dataSource) {
        if (!(dataSource instanceof BasicDataSource)) {
            throw new IllegalArgumentException("argument must be BasicDataSource");
        }

        BasicDataSource pool = (BasicDataSource) dataSource;

        System.out.printf("active: %s; idle: %s\n",
                pool.getNumActive(), pool.getNumIdle());
    }
}
