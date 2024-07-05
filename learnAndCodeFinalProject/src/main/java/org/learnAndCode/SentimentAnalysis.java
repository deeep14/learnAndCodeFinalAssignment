package org.learnAndCode;

import java.util.Arrays;
import java.util.List;

public class SentimentAnalysis {
    private static final List<String> POSITIVE_WORDS = Arrays.asList(
            "excellent", "great", "amazing", "awesome", "fantastic", "wonderful", "perfect", "positive"
    );

    private static final List<String> NEGATIVE_WORDS = Arrays.asList(
            "bad", "poor", "not good", "tasteless", "bitter", "too spicy", "too sweet", "horrible"
    );

    public static boolean isPositiveReview(String review) {
        if (review == null || review.isEmpty()) {
            return false;
        }

        String[] words = review.toLowerCase().split("\\s+");
        for (String word : words) {
            if (POSITIVE_WORDS.contains(word)) {
                return true;
            }
        }
        return false;
    }
}