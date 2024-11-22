package ee.api.orders;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public User getUserByUserName(String userName) {
        String jpql = "SELECT u FROM User u LEFT JOIN FETCH u.authorities WHERE u.username = :user_name";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        query.setParameter("user_name", userName);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    @Transactional
    public List<User> getAllUsers() {
        String jpql = "SELECT u FROM User u LEFT JOIN FETCH u.authorities";
        TypedQuery<User> query = em.createQuery(jpql, User.class);
        return query.getResultList();
    }
}
