package com.example.roomdatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {
    //Menambahkan data baru ke dalam tabel.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Note note);

    //Memperbarui data yang sudah ada berdasarkan Primary Key.
    @Update
    void update (Note note);

    //Menghapus data tertentu dari tabel berdasarkan Primary Key.
    @Delete
    void delete (Note note);

    //Mengambil semua data catatan dari tabel note
    @Query("SELECT * from note ORDER BY id ASC")
    LiveData<List<Note>> getAllNotes();

}
