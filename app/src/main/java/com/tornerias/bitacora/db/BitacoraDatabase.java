package com.tornerias.bitacora.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

@Database(entities = {CartillaProtocolosEntity.class}, version = 1) // 👈 solo CartillaProtocolosEntity
@TypeConverters({Converters.class})
public abstract class BitacoraDatabase extends RoomDatabase {

    public abstract CartillaProtocolosDao cartillaProtocolosDao();

    private static volatile BitacoraDatabase INSTANCE;

    public static BitacoraDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BitacoraDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    BitacoraDatabase.class,
                                    "bitacora_db"
                            )
                            .allowMainThreadQueries() // 👈 solo para pruebas, en prod usar Async
                            .fallbackToDestructiveMigration() // recrea BD si cambias el esquema
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
