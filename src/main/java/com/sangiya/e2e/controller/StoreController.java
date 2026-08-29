package com.sangiya.e2e.controller;

import com.sangiya.e2e.model.AddProductRequest;
import com.sangiya.e2e.model.Product;
import com.sangiya.e2e.service.ProductStore;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class StoreController {

    private static final Logger log = LoggerFactory.getLogger(StoreController.class);

    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "admin123";
    private static final String USER_SESSION_ATTRIBUTE = "user";

    private final ProductStore productStore;

    public StoreController(ProductStore productStore) {
        this.productStore = productStore;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/index.html";
    }

    @GetMapping("/products")
    @ResponseBody
    public List<Product> listProducts() {
        return productStore.list();
    }

    @PostMapping("/products")
    public ResponseEntity<Product> addProduct(@RequestBody AddProductRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        Product product = productStore.add(request.getName());
        log.info("Product added: id={}, name={}", product.getId(), product.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @DeleteMapping("/products")
    public ResponseEntity<Void> clearProducts() {
        productStore.clear();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/login")
    public String loginPage() {
        return "redirect:/login.html";
    }

    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        HttpSession session) {
        if (VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password)) {
            session.setAttribute(USER_SESSION_ATTRIBUTE, username);
            log.info("User signed in: {}", username);
            return "redirect:/index.html";
        }
        log.warn("Failed sign-in attempt for user: {}", username);
        return "redirect:/login.html?error=1";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/index.html";
    }

    @GetMapping("/api/me")
    @ResponseBody
    public Map<String, Object> currentUser(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        response.put("username", session.getAttribute(USER_SESSION_ATTRIBUTE));
        return response;
    }
}