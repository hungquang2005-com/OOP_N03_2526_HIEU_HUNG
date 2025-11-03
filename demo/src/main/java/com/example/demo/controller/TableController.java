package com.example.demo.controller;

import com.example.demo.model.DiningTable;
import com.example.demo.repository.TableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.List; // <-- Tui đã thêm import này (mặc dù java.util.* đã bao gồm)

@RestController
@RequestMapping("/api")
public class TableController {

    @Autowired
    private TableRepository tableRepository;

    // ===============================================
    // READ ALL (GET /api/tables) - ĐOẠN ĐƯỢC THÊM VÀO
    // ===============================================
    @GetMapping("/tables")
    public ResponseEntity<?> getAllTables() {
        try {
            List<DiningTable> tables = tableRepository.findAll();
            return ResponseEntity.ok(tables);
        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lấy tất cả bàn: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy danh sách bàn: " + e.getMessage());
        }
    }

    // ===============================================
    // PHẦN CODE CŨ CỦA BẠN (vẫn giữ nguyên)
    // ===============================================

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
                    return ResponseEntity.badRequest().body("Sức chứa bàn phải lớn hơn 0");
                }

                Optional<DiningTable> tableOpt = tableRepository.findById(tableId);
                DiningTable table;

                if (tableOpt.isPresent()) {
                    table = tableOpt.get();
                    if (table.isReserved()) {
                        return ResponseEntity.status(HttpStatus.CONFLICT)
                                .body("Bàn " + tableId + " đã được đặt");
                    }
                    if (table.getCapacity() < capacity) {
                        return ResponseEntity.badRequest()
                                .body("Bàn " + tableId + " không đủ sức chứa. Yêu cầu: " + capacity + ", Bàn có: " + table.getCapacity());
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Không tìm thấy bàn với ID: " + tableId);
                }

                table.reserve();
                tableRepository.save(table);
                successfullyReserved.add(table);
                System.out.println("✅ Reserved table ID: " + tableId);
            }
            
            return ResponseEntity.ok(successfullyReserved);

        } catch (Exception e) {
            System.err.println("❌ Error reserving table(s): " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đặt bàn: " + e.getMessage());
        }
    }

    // ========== READ (Get one) ==========
    @GetMapping("/tables/{tableId}")
    public ResponseEntity<?> getTableById(@PathVariable int tableId) {
        try {
            Optional<DiningTable> tableOpt = tableRepository.findById(tableId);

            if (tableOpt.isPresent()) {
                return ResponseEntity.ok(tableOpt.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Không tìm thấy bàn với ID: " + tableId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error finding table: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi tìm bàn: " + e.getMessage());
        }
    }

    // ========== UPDATE (Release table) ==========
    @PutMapping("/tables/{tableId}/release")
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