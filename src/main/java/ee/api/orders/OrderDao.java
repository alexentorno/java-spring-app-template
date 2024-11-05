package ee.api.orders;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class OrderDao {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public List<Order> findAllOrders() {
        String jpql = "SELECT o FROM Order o LEFT JOIN FETCH o.orderRows";
        TypedQuery<Order> query = em.createQuery(jpql, Order.class);
        return query.getResultList();
    }

    @Transactional
    public Order findOrderWithId(Long id) {
        String jpql = "SELECT o FROM Order o LEFT JOIN FETCH o.orderRows WHERE o.id = :id";
        TypedQuery<Order> query = em.createQuery(jpql, Order.class);
        query.setParameter("id", id);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    @Transactional
    public Order insertOrder(Order order) {
        em.persist(order);
        em.flush();
        return order;
    }

    @Transactional
    public void deleteOrderWithId(Long orderId) {
        Order order = em.find(Order.class, orderId);
        if (order != null) {
            em.remove(order);
        }
    }
}
