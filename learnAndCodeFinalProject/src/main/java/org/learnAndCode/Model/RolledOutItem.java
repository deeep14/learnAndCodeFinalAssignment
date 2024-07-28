package org.learnAndCode.Model;

public class RolledOutItem {
    private int itemId;
    private String itemName;

    public RolledOutItem(int itemId, String itemName) {
        this.itemId = itemId;
        this.itemName = itemName;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }
}
