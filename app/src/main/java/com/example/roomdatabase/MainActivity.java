package com.example.roomdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.example.roomdatabase.databinding.ActivityMainBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NoteDao mNoteDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(this);
        mNoteDao = db.noteDao();

        // Tombol untuk navigasi ke FormActivity untuk menambah data
        binding.btnAdd.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, FormActivity.class);
            startActivity(intent);
        });

        // Mendapatkan semua data catatan dari database dan menampilkannya
        getAllNotes();

        // Klik item pada ListView untuk mengedit data
        binding.listView.setOnItemClickListener((adapterView, view, position, id) -> {
            // Ambil catatan yang dipilih dari adapter
            Note selectedNote = (Note) adapterView.getAdapter().getItem(position);

            // Kirim data catatan ke FormActivity untuk mode edit
            Intent intent = new Intent(MainActivity.this, FormActivity.class);
            intent.putExtra("note_id", selectedNote.getId());
            intent.putExtra("note_title", selectedNote.getTitle());
            intent.putExtra("note_desc", selectedNote.getDescription());
            intent.putExtra("note_date", selectedNote.getDate());
            startActivity(intent);
        });

        // Klik lama pada item untuk menghapus data
        binding.listView.setOnItemLongClickListener((adapterView, view, position, id) -> {
            Note selectedNote = (Note) adapterView.getAdapter().getItem(position);
            delete(selectedNote);
            return true; // Mengembalikan true agar event tidak diteruskan
        });
    }

    // Fungsi untuk mendapatkan semua catatan dan memperbarui ListView
    private void getAllNotes() {
        mNoteDao.getAllNotes().observe(this, notes -> {
            ArrayAdapter<Note> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, notes);
            binding.listView.setAdapter(adapter);
        });
    }

    // Fungsi untuk menghapus catatan dari database
    private void delete(Note note) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> mNoteDao.delete(note));
    }
}
