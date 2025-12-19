package dev.loki.lorep.util;

import java.time.Duration;
import java.time.Instant;

public class TimeUtil {
    
    public static String formatTimeAgo(long epochMillis) {
        return formatTimeAgo(Instant.ofEpochMilli(epochMillis));
    }
    
    public static String formatTimeAgo(Instant instant) {
        Duration duration = Duration.between(instant, Instant.now());
        
        long seconds = duration.getSeconds();
        
        if (seconds < 60) {
            return seconds + " сек. назад";
        }
        
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + " мин. назад";
        }
        
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " ч. назад";
        }
        
        long days = hours / 24;
        if (days < 30) {
            return days + " дн. назад";
        }
        
        long months = days / 30;
        if (months < 12) {
            return months + " мес. назад";
        }
        
        long years = months / 12;
        return years + " г. назад";
    }
    
    public static String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        
        if (seconds < 60) {
            return seconds + " секунд";
        }
        
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + " минут";
        }
        
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + " часов";
        }
        
        long days = hours / 24;
        return days + " дней";
    }
}
