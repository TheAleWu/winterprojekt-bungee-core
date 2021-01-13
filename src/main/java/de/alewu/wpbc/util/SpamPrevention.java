package de.alewu.wpbc.util;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;

public class SpamPrevention {

    private static final Map<UUID, Queue<String>> MESSAGES = new HashMap<>();

    private SpamPrevention() {
        // Util class
    }

    public static void track(UUID uuid, String message) {
        Queue<String> messages = MESSAGES.computeIfAbsent(uuid, x -> new ArrayDeque<>());
        messages.add(message);
        if (messages.size() > Constants.SPAM_THRESHOLD) {
            messages.remove();
        }
    }

    public static boolean isSpamming(UUID uuid, String nextMessage) {
        Queue<String> messages = MESSAGES.getOrDefault(uuid, new ArrayDeque<>());
        if (messages.size() < Constants.SPAM_THRESHOLD) {
            return false;
        }
        return messages.stream().allMatch(x -> x.equalsIgnoreCase(nextMessage));
    }

}
