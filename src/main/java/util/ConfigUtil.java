package util;

import java.util.Properties;


public class ConfigUtil {

    public static ConnectionInfo readConnectionInfo() {
        Properties properties = PropertyLoader.loadApplicationProperties();

        return new ConnectionInfo(
                properties.getProperty("hsql.url"),
                properties.getProperty("hsql.user"),
                properties.getProperty("hsql.pass"));
    }

}
