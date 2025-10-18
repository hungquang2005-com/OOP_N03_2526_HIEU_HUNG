package com.example.demo.model;

public class Table {
    private int tableId;
    private int capacity;
    private boolean reserved;

    public Table() {}

    public Table(int tableId, int capacity) {
        this.tableId = tableId;
        this.capacity = capacity;
        this.reserved = false;
    }

    public int getTableId() { return tableId; }
    public int getCapacity() { return capacity; }
    public boolean isReserved() { return reserved; }

    public void setTableId(int tableId) { this.tableId = tableId; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setReserved(boolean reserved) { this.reserved = reserved; }

    public void reserve() {
        if (!reserved) {
            reserved = true;
            System.out.println("Table " + tableId + " reserved.");
        } else {
            System.out.println("Table " + tableId + " is already reserved.");
        }
    }

    public void release() {
        if (reserved) {
            reserved = false;
            System.out.println("Table " + tableId + " released.");
        } else {
            System.out.println("Table " + tableId + " is not reserved.");
        }
    }

    @Override
    public String toString() {
        return "Table{" +
                "tableId=" + tableId +
                ", capacity=" + capacity +
                ", reserved=" + reserved +
                '}';
    }
}