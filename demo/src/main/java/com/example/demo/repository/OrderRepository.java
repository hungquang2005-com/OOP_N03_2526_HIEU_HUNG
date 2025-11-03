package com.example.demo.repository;

import com.example.demo.model.Order;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    
    // Tìm tất cả đơn hàng của 1 user, sắp xếp theo ngày mới nhất
    List<Order> findByUserOrderByOrderDateDesc(User user);
    
    // Hoặc đơn giản hơn (không sắp xếp)
    List<Order> findByUser(User user);
}