package ee.api.orders;

import org.apache.commons.dbcp2.BasicDataSource;
import util.ConnectionPoolFactory;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;

@WebServlet("/api/pool/info")
public class PoolInfoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        DataSource pool = new ConnectionPoolFactory().createConnectionPool();

        if (!(pool instanceof BasicDataSource)) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{ \"error\": \"Connection pool is not BasicDataSource\" }");
            return;
        }

        BasicDataSource basicDataSource = (BasicDataSource) pool;

        int activeConnections = basicDataSource.getNumActive();
        int idleConnections = basicDataSource.getNumIdle();

        String jsonResponse = String.format("{ \"inPool\": %d, \"inUse\": %d }", idleConnections, activeConnections);

        resp.setContentType("application/json");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(jsonResponse);
    }
}
