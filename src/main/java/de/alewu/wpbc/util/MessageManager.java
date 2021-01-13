package de.alewu.wpbc.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MessageManager {

    private static final Map<UUID, UUID> LAST_MESSAGED = new HashMap<>();

    public static void updateLastMessaged(UUID messager, UUID messaged) {
        LAST_MESSAGED.put(messager, messaged);
    }

    public static UUID getLastMessaged(UUID uuid) {
        return LAST_MESSAGED.getOrDefault(uuid, null);
    }

}
