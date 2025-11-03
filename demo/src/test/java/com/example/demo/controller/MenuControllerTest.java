package com.example.demo.controller;

import com.example.demo.model.Food;
import com.example.demo.repository.FoodRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FoodRepository foodRepository;

    private Food testFood;

    @BeforeEach
    void setUp() {
        testFood = new Food();
        testFood.setFoodID(1);
        testFood.setName("Phở Bò");
        testFood.setPrice(50000);
        testFood.setDescription("Phở bò truyền thống");
    }

    @Test
    void addFood_Success() throws Exception {
        when(foodRepository.save(any(Food.class))).thenReturn(testFood);

        mockMvc.perform(post("/api/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testFood)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Phở Bò"))
                .andExpect(jsonPath("$.price").value(50000));

        verify(foodRepository, times(1)).save(any(Food.class));
    }

    @Test
    void addFood_EmptyName() throws Exception {
        Food invalidFood = new Food();
        invalidFood.setName("");
        invalidFood.setPrice(50000);

        mockMvc.perform(post("/api/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidFood)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Tên món ăn không được để trống"));
    }

    @Test
    void addFood_InvalidPrice() throws Exception {
        Food invalidFood = new Food();
        invalidFood.setName("Test Food");
        invalidFood.setPrice(-100);

        mockMvc.perform(post("/api/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidFood)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Giá món ăn phải lớn hơn 0"));
    }

    @Test
    void getMenu_Success() throws Exception {
        Food food2 = new Food();
        food2.setFoodID(2);
        food2.setName("Bún Chả");
        food2.setPrice(40000);

        List<Food> menu = Arrays.asList(testFood, food2);
        when(foodRepository.findAll()).thenReturn(menu);

        mockMvc.perform(get("/api/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Phở Bò"))
                .andExpect(jsonPath("$[1].name").value("Bún Chả"));
    }

    @Test
    void getFoodById_Success() throws Exception {
        when(foodRepository.findById(1)).thenReturn(Optional.of(testFood));

        mockMvc.perform(get("/api/menu/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.foodID").value(1))
                .andExpect(jsonPath("$.name").value("Phở Bò"));
    }

    @Test
    void getFoodById_NotFound() throws Exception {
        when(foodRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/menu/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Không tìm thấy món ăn với ID: 999"));
    }

    @Test
    void updateFood_Success() throws Exception {
        Food updatedFood = new Food();
        updatedFood.setName("Phở Bò Đặc Biệt");
        updatedFood.setPrice(60000);
        updatedFood.setDescription("Phở bò đặc biệt nhiều thịt");

        when(foodRepository.findById(1)).thenReturn(Optional.of(testFood));
        when(foodRepository.save(any(Food.class))).thenReturn(testFood);

        mockMvc.perform(put("/api/menu/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFood)))
                .andExpect(status().isOk());

        verify(foodRepository, times(1)).save(any(Food.class));
    }

    @Test
    void updateFood_NotFound() throws Exception {
        Food updatedFood = new Food();
        updatedFood.setName("Test");
        updatedFood.setPrice(50000);

        when(foodRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/menu/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedFood)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Không tìm thấy món ăn với ID: 999"));
    }

    @Test
    void deleteFood_Success() throws Exception {
        when(foodRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(delete("/api/menu/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Đã xóa món ăn ID: 1"));

        verify(foodRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteFood_NotFound() throws Exception {
        when(foodRepository.existsById(999)).thenReturn(false);

        mockMvc.perform(delete("/api/menu/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Không tìm thấy món ăn với ID: 999"));

        verify(foodRepository, never()).deleteById(anyInt());
    }
}