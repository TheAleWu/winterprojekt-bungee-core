package de.alewu.wpbc.util;

import com.google.common.collect.ImmutableMap;
import de.alewu.wpc.helpers.TimeZoneHelper;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class Constants {

    public static final String PREFIX = "§b❄ ";
    public static final int SPAM_THRESHOLD = 3;
    public static final String GAME_KEY_CHARS = "ABCDEFGHIJKLMOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    public static final int GAME_KEY_LENGTH = 6;
    public static final int GAME_KEY_RETRY_THRESHOLD = 25;
    public static final int GAME_KEYS_PER_PAGE = 8;
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    public static final Map<String, String> SUPPORTED_LANGUAGES = ImmutableMap.of(
        "de", "Deutsch",
        "en", "English");
    public static final int TPA_PERIOD_SECONDS = 30;
    // Semi-Constant

    private Constants() {
        // Constants class
    }

}
