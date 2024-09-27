package ee.api.orders.form;

import ee.api.orders.Order;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@WebServlet("/orders/form")
public class OrderFormServlet extends HttpServlet {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    @Override
    public void init() throws ServletException {
        ServletContext context = getServletContext();
        if (context.getAttribute("orders") == null) {
            context.setAttribute("orders", new HashMap<Long, Order>());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletContext context = getServletContext();
        Map<Long, Order> orders = (Map<Long, Order>) context.getAttribute("orders");

        //extract form data (orderNumber)
        String orderNumber = req.getParameter("orderNumber");
        if (orderNumber == null || orderNumber.trim().isEmpty()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{ \"error\": \"Missing or invalid orderNumber\" }");
            return;
        }

        //create a new order with the order number
        long newOrderId = ID_GENERATOR.incrementAndGet();
        Order newOrder = new Order();
        newOrder.setId(newOrderId);
        newOrder.setOrderNumber(orderNumber);

        orders.put(newOrderId, newOrder);


        String acceptHeader = req.getHeader("Accept");
        if (acceptHeader != null && acceptHeader.contains("application/json")) {
            //respond with json
            ObjectMapper objectMapper = new ObjectMapper();
            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(newOrder));
        } else {
            //respond with form-urlencoded format
            resp.setContentType("application/x-www-form-urlencoded");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("id=" + newOrderId + "&orderNumber=" + orderNumber);
        }
    }
}
