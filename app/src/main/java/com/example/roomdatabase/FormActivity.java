package com.example.roomdatabase;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.roomdatabase.databinding.ActivityFormBinding;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FormActivity extends AppCompatActivity {

    private ActivityFormBinding binding;
    private NoteDao mNoteDao;
    private ExecutorService executorService;
    private int noteId = -1; // Default ID jika data baru
    private String selectedDate = ""; // Untuk menyimpan tanggal yang dipilih

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFormBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi database dan DAO
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(this);
        mNoteDao = db.noteDao();
        executorService = Executors.newSingleThreadExecutor();

        // Cek apakah ada data yang diterima dari intent
        Intent intent = getIntent();
        noteId = intent.getIntExtra("note_id", -1); // Default -1 jika tidak ada ID
        if (noteId != -1) {
            // Mengisi field dengan data catatan yang diterima dari intent
            binding.edtTitle.setText(intent.getStringExtra("note_title"));
            binding.edtDesc.setText(intent.getStringExtra("note_desc"));
            binding.edtDate.setText(intent.getStringExtra("note_date")); // Tampilkan tanggal yang diterima
            binding.btnSubmit.setText("Update");
            binding.btnDelete.setVisibility(View.VISIBLE);

            // Aksi untuk tombol "Update"
            binding.btnSubmit.setOnClickListener(view -> {
                // Ambil data dari input user
                String updatedTitle = binding.edtTitle.getText().toString();
                String updatedDesc = binding.edtDesc.getText().toString();

                // Buat objek Note baru dengan data yang diperbarui
                Note updatedNote = new Note();
                updatedNote.setId(noteId);
                updatedNote.setTitle(updatedTitle);
                updatedNote.setDescription(updatedDesc);
                updatedNote.setDate(selectedDate); // Simpan tanggal yang diperbarui

                // Update catatan di database
                update(updatedNote);
                finish(); // Kembali ke layar sebelumnya
            });

            // Aksi untuk tombol "Delete"
            binding.btnDelete.setOnClickListener(view -> showDeleteConfirmationDialog(noteId));

        } else {
            // Mode Tambah Data Baru
            binding.btnSubmit.setOnClickListener(view -> {
                // Ambil data dari input user
                String title = binding.edtTitle.getText().toString();
                String desc = binding.edtDesc.getText().toString();

                // Buat objek Note baru
                Note newNote = new Note(title, desc, selectedDate); // Tambahkan tanggal
                insert(newNote);
                finish();
            });

            binding.btnDelete.setVisibility(View.GONE); // Sembunyikan tombol hapus
        }

        // Setup date picker untuk memilih tanggal
        setupDatePicker();
    }

    // Metode untuk mengatur DatePicker
    private void setupDatePicker() {
        binding.edtDate.setOnClickListener(view -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Select a date")
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                // Format timestamp menjadi "dd/MM/yyyy"
                selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
                binding.edtDate.setText(selectedDate); // Tampilkan tanggal yang dipilih di EditText
            });

            datePicker.show(getSupportFragmentManager(), "date_picker");
        });
    }

    // Fungsi untuk menambahkan catatan ke database
    private void insert(Note note) {
        executorService.execute(() -> mNoteDao.insert(note));
    }

    // Fungsi untuk memperbarui catatan di database
    private void update(Note note) {
        executorService.execute(() -> mNoteDao.update(note));
    }

    // Fungsi untuk menghapus catatan dari database
    private void delete(int id) {
        executorService.execute(() -> {
            Note noteToDelete = new Note();
            noteToDelete.setId(id);
            mNoteDao.delete(noteToDelete);

            // Tampilkan toast di UI thread
            runOnUiThread(() ->
                    Toast.makeText(FormActivity.this, "Note deleted successfully!", Toast.LENGTH_SHORT).show()
            );
        });
    }

    // Menampilkan dialog konfirmasi penghapusan
    private void showDeleteConfirmationDialog(int noteId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes", (dialogInterface, i) -> {
                    delete(noteId);
                    finish();
                })
                .setNegativeButton("No", (dialogInterface, i) -> dialogInterface.dismiss())
                .show();
    }
}
