package controller;

import model.Table;
import java.util.*;

public class TableController {
    private Scanner sc;

    public TableController(Scanner sc) {
        this.sc = sc;
    }

    public List<Table> reserveTables() {
        List<Table> reservedTables = new ArrayList<>();
        Set<Integer> reservedIds = new HashSet<>();

        System.out.print("\nNhập số lượng bàn muốn đặt: ");
        int soBan;
        try {
            soBan = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("❌ Số bàn không hợp lệ.");
            return reservedTables;
        }

        for (int i = 1; i <= soBan; i++) {
            System.out.println("\n--- Nhập thông tin bàn " + i + " ---");
            int tableId, capacity, numPeople;

            while (true) {
                System.out.print("Nhập ID bàn: ");
                tableId = Integer.parseInt(sc.nextLine().trim());
                if (reservedIds.contains(tableId)) {
                    System.out.println("❌ ID bàn đã được đặt!");
                    continue;
                }
                break;
            }

            System.out.print("Nhập sức chứa bàn (tối đa 6): ");
            capacity = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Nhập số lượng khách đi cùng: ");
            numPeople = Integer.parseInt(sc.nextLine().trim());

            Table table = new Table(tableId, capacity);
            table.reserve();
            reservedIds.add(tableId);
            reservedTables.add(table);

            System.out.println("✅ Đã đặt bàn " + tableId + " cho " + numPeople + " người.");
        }

        return reservedTables;
    }
}
