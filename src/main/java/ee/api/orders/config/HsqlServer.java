package ee.api.orders.config;

import org.hsqldb.Server;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Locale;

public class HsqlServer {

    public static void main(String[] args) {
        Server server = new Server();
        server.setDatabasePath(0, "mem:db1;sql.syntax_pgs=true");
        server.setDatabaseName(0, "db1");
        server.setLogWriter(new Logger(System.out));
        server.start();
    }

    private static class Logger extends PrintWriter {
        public Logger(OutputStream out) {
            super(out);
        }

        @Override
        public void print(String line) {
            if (isSql(line)) {
//                System.out.println(line);
            }
        }

        @Override
        public void println() {
        }

        private boolean isSql(String line) {
            String lowerCaseLine = line.toLowerCase(Locale.ROOT);
            for (String key : List.of("insert", "create", "select", "alter", "update")) {
                if (lowerCaseLine.contains(key)) {
                    return true;
                }
            }
            return false;
        }
    }

}
