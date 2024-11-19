package com.example.roomdatabase;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Note.class}, version = 2, exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {

    // DAO untuk mengakses tabel Note
    public abstract NoteDao noteDao();

    // Singleton instance
    private static volatile NoteRoomDatabase INSTANCE;

    // Mendapatkan instance database
    public static NoteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NoteRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    NoteRoomDatabase.class, "note_database")
                            .addMigrations(MIGRATION_1_2) // Menambahkan migrasi
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Migrasi dari versi 1 ke 2: menambahkan kolom 'date' ke tabel Note
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Menambahkan kolom 'date' ke tabel Note
            database.execSQL("ALTER TABLE Note ADD COLUMN date TEXT");
        }
    };
}
