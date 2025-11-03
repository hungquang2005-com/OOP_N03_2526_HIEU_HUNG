package com.example.demo.controller;

import com.example.demo.model.DiningTable;
import com.example.demo.repository.TableRepository;
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
class TableControllerTest {

    private MockMvc mockMvc;
    
    private ObjectMapper objectMapper;

    @Mock
    private TableRepository tableRepository;

    @InjectMocks
    private TableController tableController;

    private DiningTable testTable;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(tableController).build();
        objectMapper = new ObjectMapper();
        
        testTable = new DiningTable(1, 4);
        testTable.reserve();
    }

    @Test
    void reserveTables_Success_SingleTable() throws Exception {
        List<Map<String, Integer>> reserveRequest = new ArrayList<>();
        Map<String, Integer> tableInfo = new HashMap<>();
        tableInfo.put("tableId", 1);
        tableInfo.put("capacity", 4);
        reserveRequest.add(tableInfo);

        when(tableRepository.existsById(1)).thenReturn(false);
        when(tableRepository.save(any(DiningTable.class))).thenReturn(testTable);

        mockMvc.perform(post("/api/tables/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isCreated());

        verify(tableRepository, times(1)).save(any(DiningTable.class));
    }

    @Test
    void reserveTables_Success_MultipleTables() throws Exception {
        List<Map<String, Integer>> reserveRequest = new ArrayList<>();
        
        Map<String, Integer> table1 = new HashMap<>();
        table1.put("tableId", 1);
        table1.put("capacity", 4);
        reserveRequest.add(table1);
        
        Map<String, Integer> table2 = new HashMap<>();
        table2.put("tableId", 2);
        table2.put("capacity", 6);
        reserveRequest.add(table2);

        when(tableRepository.existsById(anyInt())).thenReturn(false);
        when(tableRepository.save(any(DiningTable.class))).thenReturn(testTable);

        mockMvc.perform(post("/api/tables/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2));

        verify(tableRepository, times(2)).save(any(DiningTable.class));
    }

    @Test
    void reserveTables_AlreadyReserved() throws Exception {
        List<Map<String, Integer>> reserveRequest = new ArrayList<>();
        Map<String, Integer> tableInfo = new HashMap<>();
        tableInfo.put("tableId", 1);
        tableInfo.put("capacity", 4);
        reserveRequest.add(tableInfo);

        when(tableRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(post("/api/tables/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isConflict());

        verify(tableRepository, never()).save(any(DiningTable.class));
    }

    @Test
    void reserveTables_InvalidTableId() throws Exception {
        List<Map<String, Integer>> reserveRequest = new ArrayList<>();
        Map<String, Integer> tableInfo = new HashMap<>();
        tableInfo.put("tableId", -1);
        tableInfo.put("capacity", 4);
        reserveRequest.add(tableInfo);

        mockMvc.perform(post("/api/tables/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reserveTables_InvalidCapacity() throws Exception {
        List<Map<String, Integer>> reserveRequest = new ArrayList<>();
        Map<String, Integer> tableInfo = new HashMap<>();
        tableInfo.put("tableId", 1);
        tableInfo.put("capacity", -2);
        reserveRequest.add(tableInfo);

        mockMvc.perform(post("/api/tables/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reserveTables_EmptyList() throws Exception {
        List<Map<String, Integer>> reserveRequest = new ArrayList<>();

        mockMvc.perform(post("/api/tables/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reserveTables_MissingData() throws Exception {
        List<Map<String, Integer>> reserveRequest = new ArrayList<>();
        Map<String, Integer> tableInfo = new HashMap<>();
        tableInfo.put("tableId", 1);
        reserveRequest.add(tableInfo);

        mockMvc.perform(post("/api/tables/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reserveRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTables_Success() throws Exception {
        DiningTable table2 = new DiningTable(2, 6);
        List<DiningTable> tables = Arrays.asList(testTable, table2);
        when(tableRepository.findAll()).thenReturn(tables);

        mockMvc.perform(get("/api/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getAllTables_Empty() throws Exception {
        when(tableRepository.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/tables"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void getTableById_Success() throws Exception {
        when(tableRepository.findById(1)).thenReturn(Optional.of(testTable));

        mockMvc.perform(get("/api/tables/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getTableById_NotFound() throws Exception {
        when(tableRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tables/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void checkTableAvailability_Reserved() throws Exception {
        when(tableRepository.findById(1)).thenReturn(Optional.of(testTable));

        mockMvc.perform(get("/api/tables/1/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId").value(1))
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void checkTableAvailability_NotReserved() throws Exception {
        DiningTable availableTable = new DiningTable(2, 4);
        when(tableRepository.findById(2)).thenReturn(Optional.of(availableTable));

        mockMvc.perform(get("/api/tables/2/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId").value(2))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void checkTableAvailability_NotExists() throws Exception {
        when(tableRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/tables/999/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tableId").value(999))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void releaseTable_Success() throws Exception {
        when(tableRepository.findById(1)).thenReturn(Optional.of(testTable));
        when(tableRepository.save(any(DiningTable.class))).thenReturn(testTable);

        mockMvc.perform(put("/api/tables/release/1"))
                .andExpect(status().isOk());

        verify(tableRepository, times(1)).save(any(DiningTable.class));
    }

    @Test
    void releaseTable_NotFound() throws Exception {
        when(tableRepository.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/tables/release/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void releaseTable_NotReserved() throws Exception {
        DiningTable availableTable = new DiningTable(2, 4);
        when(tableRepository.findById(2)).thenReturn(Optional.of(availableTable));

        mockMvc.perform(put("/api/tables/release/2"))
                .andExpect(status().isBadRequest());

        verify(tableRepository, never()).save(any(DiningTable.class));
    }

    @Test
    void deleteTable_Success() throws Exception {
        when(tableRepository.existsById(1)).thenReturn(true);

        mockMvc.perform(delete("/api/tables/1"))
                .andExpect(status().isOk());

        verify(tableRepository, times(1)).deleteById(1);
    }

    @Test
    void deleteTable_NotFound() throws Exception {
        when(tableRepository.existsById(999)).thenReturn(false);

        mockMvc.perform(delete("/api/tables/999"))
                .andExpect(status().isNotFound());

        verify(tableRepository, never()).deleteById(anyInt());
    }
}
