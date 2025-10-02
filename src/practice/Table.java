package practice;
public class Table {
    private int tableID;
    private int capacity;
    private boolean reserved;

    public Table(int tableID, int capacity) {
        this.tableID = tableID;
        this.capacity = capacity;
        this.reserved = false;
    }

    public int getTableID() {
        return tableID;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void reserve() {
        if (!reserved) {
            reserved = true;
            System.out.println("Table " + tableID + " reserved.");
        } else {
            System.out.println("Table " + tableID + " is already reserved.");
        }
    }
}

