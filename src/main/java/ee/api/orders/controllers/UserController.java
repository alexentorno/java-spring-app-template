package ee.api.orders.controllers;

import ee.api.orders.User;
import ee.api.orders.UserDao;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    @GetMapping("")
    public String frontPage() {
        return "Front page!";
    }

    @GetMapping("home")
    public String home() {
        return "Api home url";
    }

    @GetMapping("users/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.name")
    public String getUserByName(@PathVariable("username") String username) {
        return "Hello " + username;
    }


    // New endpoint to get all users, accessible only to admin
    @GetMapping("users")
    public List<User> getAllUsers() {
//        return new UserDao().getAllUsers();
        return new ArrayList<>();
    }
}
