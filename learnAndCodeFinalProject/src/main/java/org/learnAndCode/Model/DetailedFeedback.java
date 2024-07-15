package org.learnAndCode;

public class DetailedFeedback {
    private int itemId;
    private String dislikeReason;
    private String improvementSuggestion;
    private String momsRecipe;

    public DetailedFeedback(int itemId, String dislikeReason, String improvementSuggestion, String momsRecipe) {
        this.itemId = itemId;
        this.dislikeReason = dislikeReason;
        this.improvementSuggestion = improvementSuggestion;
        this.momsRecipe = momsRecipe;
    }

    public int getItemId() {
        return itemId;
    }

    public String getDislikeReason() {
        return dislikeReason;
    }

    public String getImprovementSuggestion() {
        return improvementSuggestion;
    }

    public String getMomsRecipe() {
        return momsRecipe;
    }
}

