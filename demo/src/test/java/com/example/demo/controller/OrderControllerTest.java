
package com.example.demo.controller;

import com.example.demo.model.Food;
import com.example.demo.model.Order;
import com.example.demo.repository.FoodRepository;
import com.example.demo.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private FoodRepository foodRepository;

    @InjectMocks
    private OrderController orderController;

    private Food testFood;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
        objectMapper = new ObjectMapper();
        
        testFood = new Food();
        testFood.setFoodID(1);
        testFood.setName("Phở Bò");
        testFood.setPrice(50000);

        testOrder = new Order();
        testOrder.setOrderId(1);
        testOrder.setStatus("Đang xử lý");
    }

    @Test
    void createOrder_Success() throws Exception {
        Map<String, Object> orderRequest = new HashMap<>();
        List<Map<String, Integer>> items = new ArrayList<>();
        Map<String, Integer> item = new HashMap<>();
        item.put("foodId", 1);
        item.put("quantity", 2);
        items.add(item);
        orderRequest.put("items", items);

        when(foodRepository.findById(1)).thenReturn(Optional.of(testFood));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void createOrder_EmptyItems() throws Exception {
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("items", Collections.emptyList());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createOrder_FoodNotFound() throws Exception {
        Map<String, Object> orderRequest = new HashMap<>();
        List<Map<String, Integer>> items = new ArrayList<>();
        Map<String, Integer> item = new HashMap<>();
        item.put("foodId", 999);
        item.put("quantity", 2);
        items.add(item);
        orderRequest.put("items", items);

        when(foodRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createOrder_InvalidQuantity() throws Exception {
        Map<String, Object> orderRequest = new HashMap<>();
        List<Map<String, Integer>> items = new ArrayList<>();
        Map<String, Integer> item = new HashMap<>();
        item.put("foodId", 1);
        item.put("quantity", -1);
        items.add(item);
        orderRequest.put("items", items);

        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllOrders_Success() throws Exception {
        Order order2 = new Order();
        order2.setOrderId(2);
        order2.setStatus("Hoàn thành");

        List<Order> orders = Arrays.asList(testOrder, order2);
        when(orderRepository.findAll()).thenReturn(orders);

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getOrderById_Success() throws Exception {
        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getOrderById_NotFound() throws Exception {
        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatus_Success() throws Exception {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "Hoàn thành");

        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);

        mockMvc.perform(put("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isOk());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void updateOrderStatus_NotFound() throws Exception {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "Hoàn thành");

        when(orderRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/orders/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrderStatus_EmptyStatus() throws Exception {
        Map<String, String> statusUpdate = new HashMap<>();
        statusUpdate.put("status", "");

        when(orderRepository.findById(1)).thenReturn(Optional.of(testOrder));

        mockMvc.perform(put("/api/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(statusUpdate)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteOrder_Success() throws Exception {
        when(orderRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(delete("/api/orders/1"))
                .andExpect(status().isOk());

        verify(orderRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteOrder_NotFound() throws Exception {
        when(orderRepository.existsById(999)).thenReturn(false);

        mockMvc.perform(delete("/api/orders/999"))
                .andExpect(status().isNotFound());

        verify(orderRepository, never()).deleteById(anyInt());
    }
}