package com.example.demo.controller;

import com.example.demo.model.Table;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
public class TableController {

    private static final Set<Integer> reservedTableIds = new HashSet<>();
    private static final List<Table> allTables = new ArrayList<>();

    // ========== CREATE ==========
    @PostMapping("/tables/reserve")
    public ResponseEntity<?> reserveTables(@RequestBody List<Map<String, Integer>> tablesToReserve) {
        try {
            // Validation
            if (tablesToReserve == null || tablesToReserve.isEmpty()) {
                return ResponseEntity.badRequest().body("Không có thông tin bàn để đặt");
            }

            List<Table> successfullyReserved = new ArrayList<>();

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

                if (reservedTableIds.contains(tableId)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Lỗi: Bàn ID " + tableId + " đã được đặt trước đó");
                }

                Table table = new Table(tableId, capacity);
                table.reserve();
                reservedTableIds.add(tableId);
                allTables.add(table);
                successfullyReserved.add(table);
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
    public ResponseEntity<?> getAllTables() {
        try {
            System.out.println("📖 Reading all tables, count: " + allTables.size());
            return ResponseEntity.ok(allTables);
        } catch (Exception e) {
            System.err.println("❌ Error reading tables: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi đọc danh sách bàn: " + e.getMessage());
        }
    }

    // ========== READ BY ID ==========
    @GetMapping("/tables/{tableId}")
    public ResponseEntity<?> getTableById(@PathVariable int tableId) {
        try {
            Table table = allTables.stream()
                    .filter(t -> t.getTableId() == tableId)
                    .findFirst()
                    .orElse(null);

            if (table != null) {
                System.out.println("📖 Found table: " + table);
                return ResponseEntity.ok(table);
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
            Map<String, Object> response = new HashMap<>();
            response.put("tableId", tableId);
            response.put("available", !reservedTableIds.contains(tableId));
            System.out.println("📖 Checked availability for table " + tableId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Error checking table availability: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi kiểm tra bàn: " + e.getMessage());
        }
    }

    // ========== UPDATE (Release table) ==========
    @PutMapping("/tables/{tableId}/release")
    public ResponseEntity<?> releaseTable(@PathVariable int tableId) {
        try {
            if (!reservedTableIds.contains(tableId)) {
                return ResponseEntity.badRequest()
                        .body("Bàn " + tableId + " chưa được đặt");
            }

            reservedTableIds.remove(tableId);

            // Update table status
            allTables.stream()
                    .filter(t -> t.getTableId() == tableId)
                    .forEach(Table::release);

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
            boolean removed = allTables.removeIf(t -> t.getTableId() == tableId);
            if (removed) {
                reservedTableIds.remove(tableId);
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