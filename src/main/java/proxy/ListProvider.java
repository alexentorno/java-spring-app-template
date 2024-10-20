package proxy;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class ListProvider {

    private static Logger logger = LogManager.getLogger(ListProvider.class);

    public MyList getList() {

        logger.debug("ListProvider.getList() called");

        MyList proxy = (MyList) Proxy.newProxyInstance(
                Thread.currentThread().getContextClassLoader(),
                new Class[] { MyList.class },
                new DynamicInvocationHandler(new ArrayList<>()));

        return proxy;
    }

    private static class DynamicInvocationHandler implements InvocationHandler {

        private final List<Object> underlyingList;  // реальный объект списка

        public DynamicInvocationHandler(List<Object> underlyingList) {
            this.underlyingList = underlyingList;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            // Логирование имени метода и аргументов
            logger.debug("Method called: " + method.getName());
            if (args != null) {
                for (Object arg : args) {
                    logger.debug("Argument: " + arg);
                }
            }

            // Вызов метода на реальном объекте (underlyingList)
            Object result = method.invoke(underlyingList, args);

            // Логирование результата
            logger.debug("Method result: " + result);

            return result;
        }
    }
}
