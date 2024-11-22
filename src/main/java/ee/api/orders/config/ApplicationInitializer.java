package ee.api.orders.config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{SecurityConfig.class,
                            MvcConfig.class,
                            Config.class,
                            HsqlDataSource.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class[]{Config.class};
    }

    //Kuhu kohta DispatcherServlet paigaldada
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/api/*"};
    }
}
