package ee.api.orders;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@RestController
public class OrderController {
    private final OrderDao dao;

    @Autowired
    public OrderController(OrderDao dao) {
        this.dao = dao;
    }

    @GetMapping("orders")
    public List<Order> getOrders() {
        return dao.findAllOrders();
    }

    @GetMapping("orders/{id}")
    public Order getOrderById(@PathVariable("id") Long id) {
        return dao.findOrderWithId(id);
    }

    @PostMapping("orders")
    public Order postOrder(@RequestBody @Valid Order order) throws SQLException {
        return dao.insertOrder(order);
    }

    @DeleteMapping("orders/{id}")
    public void deleteOrder(@PathVariable("id") Long id) throws SQLException {
        dao.deleteOrderWithId(id);
    }
}
