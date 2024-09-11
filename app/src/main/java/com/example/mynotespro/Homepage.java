package com.example.mynotespro;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

import java.io.IOException;

public class Homepage extends AppCompatActivity {
    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;
    NoteAdapter noteAdapter;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        recyclerView = findViewById(R.id.recycler_view);
        menuBtn = findViewById(R.id.menu_btn);
        addNoteBtn = findViewById(R.id.add_note_btn);
        builder = new AlertDialog.Builder(this);

        addNoteBtn.setOnClickListener(v -> startActivity(new Intent(Homepage.this, NoteDetails.class)));
        menuBtn.setOnClickListener(v -> showMenu());
        setupRecyclerView();

        // Load wallpaper when the app starts
        loadWallpaper();
    }

    private void setupRecyclerView() {
        Query query = Utility.getCollectionReferenceForNotes().orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setAdapter(noteAdapter);
    }

    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(Homepage.this, menuBtn);
        popupMenu.getMenu().add("SignOut");
        popupMenu.getMenu().add("Set Wallpaper");  // New option for setting wallpaper
        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle().equals("SignOut")) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Homepage.this, signinpage.class));
                    finish();
                    return true;
                } else if (menuItem.getTitle().equals("Set Wallpaper")) {
                    pickWallpaper();  // Launch gallery to pick wallpaper
                    return true;
                }
                return false;
            }
        });
    }

    private void pickWallpaper() {
        // Create an intent to select an image from gallery
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);  // Launch gallery with a request code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Get the image URI from the data
            Uri imageUri = data.getData();
            try {
                // Convert the URI to a Bitmap
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                // Set the bitmap as the background of the layout
                RelativeLayout layout = findViewById(R.id.main);
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                layout.setBackground(drawable);

                // Optionally: save the selected wallpaper to preferences for persistence
                saveWallpaper(imageUri);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveWallpaper(Uri imageUri) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("wallpaperUri", imageUri.toString());
        editor.apply();
    }

    private void loadWallpaper() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String wallpaperUri = preferences.getString("wallpaperUri", null);

        if (wallpaperUri != null) {
            Uri uri = Uri.parse(wallpaperUri);
            if (uri != null) {
                try {
                    // Check for READ_EXTERNAL_STORAGE permission before accessing the content URI
                    if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                        RelativeLayout layout = findViewById(R.id.main);
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        layout.setBackground(drawable);
                    } else {
                        // Request permission if not granted
                        requestPermissions(new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        builder.setTitle("Alert")
                .setMessage("Do you want to Close SignOut")
                .setCancelable(true)
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Homepage.this, signinpage.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .show();
    }
}