package org.learnAndCode;

import java.util.LinkedList;
import java.util.List;

public class NotificationQueue {
    private static List<String> notifications = new LinkedList<>();

    public static void addNotification(String message) {
        notifications.add(message);
    }

    public static String getNextNotification() {
        return notifications.isEmpty() ? null : notifications.remove(0);
    }

    public static boolean hasNotifications() {
        return !notifications.isEmpty();
    }
}
