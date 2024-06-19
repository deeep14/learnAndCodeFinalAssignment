package org.learnAndCode.Database;

public class MenuItem {
    private int itemId;
    private String itemName;
    private int rating;
    private String review;

    public MenuItem(int itemId, String itemName, int rating, String review) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.rating = rating;
        this.review = review;
    }

    public int getItemId() {
        return itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public int getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }
}

