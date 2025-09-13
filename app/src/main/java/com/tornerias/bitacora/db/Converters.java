package com.tornerias.bitacora.db;

import androidx.room.TypeConverter;

import java.util.Date;
import java.util.UUID;

public class Converters {
    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static Date toDate(Long millis) {
        return millis == null ? null : new Date(millis);
    }

    @TypeConverter
    public static String fromUUID(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }

    @TypeConverter
    public static UUID toUUID(String uuid) {
        return uuid == null ? null : UUID.fromString(uuid);
    }
}
