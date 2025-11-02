package com.example.demo.controller;

import com.example.demo.model.DiningTable;
import com.example.demo.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class TableController {

    @Autowired
    private TableRepository tableRepository;

    // ========== CREATE ==========
    @PostMapping("/tables/reserve")
    public ResponseEntity<?> reserveTables(@RequestBody List<Map<String, Integer>> tablesToReserve) {
        try {
            if (tablesToReserve == null || tablesToReserve.isEmpty()) {
                return ResponseEntity.badRequest().body("Không có thông tin bàn để đặt");
            }

            List<DiningTable> successfullyReserved = new ArrayList<>();

            for (Map<String, Integer> tableInfo : tablesToReserve) {
                Integer tableId = tableInfo.get("tableId");
                Integer capacity = tableInfo.get("capacity");

                if (tableId == null || capacity == null) {
                    return ResponseEntity.badRequest().body("Thông tin bàn không đầy đủ");
                }

                if (tableId <= 0) {
                    return ResponseEntity.badRequest().body("ID bàn phải lớn hơn 0");
                }

                if (capacity <= 0) {
                    return ResponseEntity.badRequest().body("Số lượng khách phải lớn hơn 0");
                }

                if (tableRepository.existsById(tableId)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Lỗi: Bàn ID " + tableId + " đã được đặt trước đó");
                }

                DiningTable newTable = new DiningTable(tableId, capacity);
                newTable.reserve();

                DiningTable savedTable = tableRepository.save(newTable);
                successfullyReserved.add(savedTable);
            }

            System.out.println("✅ Reserved tables: " + successfullyReserved);
            return ResponseEntity.status(HttpStatus.CREATED).body(successfullyReserved);

        } catch (Exception e) {
            System.err.println("❌ Error reserving tables: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đặt bàn: " + e.getMessage());
        }
    }

    // ========== READ ALL ==========
    @GetMapping("/tables")
    public ResponseEntity<List<DiningTable>> getAllTables() {
        try {
            List<DiningTable> tables = tableRepository.findAll();
            System.out.println("📖 Reading all tables, count: " + tables.size());
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            System.err.println("❌ Error reading tables: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // ========== READ BY ID ========== (THÊM MỚI)
    @GetMapping("/tables/{tableId}")
    public ResponseEntity<?> getTableById(@PathVariable int tableId) {
        try {
            Optional<DiningTable> tableOpt = tableRepository.findById(tableId);
            if (tableOpt.isPresent()) {
                System.out.println("📖 Found table: " + tableOpt.get());
                return ResponseEntity.ok(tableOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy bàn với ID: " + tableId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error reading table: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc thông tin bàn: " + e.getMessage());
        }
    }

    // ========== CHECK AVAILABILITY ==========
    @GetMapping("/tables/{tableId}/available")
    public ResponseEntity<?> checkTableAvailability(@PathVariable int tableId) {
        try {
            Optional<DiningTable> tableOpt = tableRepository.findById(tableId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("tableId", tableId);
            
            if (tableOpt.isPresent()) {
                DiningTable table = tableOpt.get();
                response.put("available", !table.isReserved());
            } else {
                response.put("available", true); // Bàn chưa tồn tại = có thể đặt
            }
            
            System.out.println("📖 Checked availability for table " + tableId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error checking table availability: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi kiểm tra bàn: " + e.getMessage());
        }
    }

    // ========== UPDATE (Release table) ==========
    @PutMapping("/tables/release/{tableId}")
    public ResponseEntity<?> releaseTable(@PathVariable int tableId) {
        try {
            Optional<DiningTable> tableOpt = tableRepository.findById(tableId);
            
            if (!tableOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy bàn với ID: " + tableId);
            }

            DiningTable table = tableOpt.get();
            
            if (!table.isReserved()) {
                return ResponseEntity.badRequest()
                        .body("Bàn " + tableId + " chưa được đặt");
            }

            table.release();
            tableRepository.save(table);

            System.out.println("✏️ Released table ID: " + tableId);
            return ResponseEntity.ok("Đã giải phóng bàn " + tableId);

        } catch (Exception e) {
            System.err.println("❌ Error releasing table: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi giải phóng bàn: " + e.getMessage());
        }
    }

    // ========== DELETE ==========
    @DeleteMapping("/tables/{tableId}")
    public ResponseEntity<?> deleteTable(@PathVariable int tableId) {
        try {
            if (tableRepository.existsById(tableId)) {
                tableRepository.deleteById(tableId);
                System.out.println("🗑️ Deleted table ID: " + tableId);
                return ResponseEntity.ok("Đã xóa bàn ID: " + tableId);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy bàn với ID: " + tableId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting table: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa bàn: " + e.getMessage());
        }
    }
}