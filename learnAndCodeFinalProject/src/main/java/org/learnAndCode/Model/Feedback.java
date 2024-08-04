package org.learnAndCode;

public class Feedback {
    private final int feedbackId;
    private final String itemName;
    private final int rating;
    private final String review;
    private final int itemId;

    public Feedback(int feedbackId, String itemName, int rating, String review, int itemId) {
        this.feedbackId = feedbackId;
        this.itemName = itemName;
        this.rating = rating;
        this.review = review;
        this.itemId = itemId;
    }

    public int getFeedbackId() {
        return feedbackId;
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

    public int getItemId() {
        return itemId;
    }
}
