package dev.loki.lorep.util;

import java.time.Duration;
import java.time.Instant;

public class TimeUtil {

    private TimeUtil() {}

    private static final long SECONDS_PER_MINUTE = 60;
    private static final long MINUTES_PER_HOUR = 60;
    private static final long HOURS_PER_DAY = 24;
    private static final long DAYS_PER_MONTH = 30;
    private static final long MONTHS_PER_YEAR = 12;

    public static String formatTimeAgo(long epochMillis) {
        return formatTimeAgo(Instant.ofEpochMilli(epochMillis));
    }

    public static String formatTimeAgo(Instant instant) {
        Duration duration = Duration.between(instant, Instant.now());

        long seconds = duration.getSeconds();

        if (seconds < SECONDS_PER_MINUTE) {
            return seconds + " сек. назад";
        }

        long minutes = seconds / SECONDS_PER_MINUTE;
        if (minutes < MINUTES_PER_HOUR) {
            return minutes + " мин. назад";
        }

        long hours = minutes / MINUTES_PER_HOUR;
        if (hours < HOURS_PER_DAY) {
            return hours + " ч. назад";
        }

        long days = hours / HOURS_PER_DAY;
        if (days < DAYS_PER_MONTH) {
            return days + " дн. назад";
        }

        long months = days / DAYS_PER_MONTH;
        if (months < MONTHS_PER_YEAR) {
            return months + " мес. назад";
        }

        long years = months / MONTHS_PER_YEAR;
        return years + " г. назад";
    }

    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();

        if (seconds < SECONDS_PER_MINUTE) {
            return seconds + " секунд";
        }

        long minutes = seconds / SECONDS_PER_MINUTE;
        if (minutes < MINUTES_PER_HOUR) {
            return minutes + " минут";
        }

        long hours = minutes / MINUTES_PER_HOUR;
        if (hours < HOURS_PER_DAY) {
            return hours + " часов";
        }

        long days = hours / HOURS_PER_DAY;
        return days + " дней";
    }
}
