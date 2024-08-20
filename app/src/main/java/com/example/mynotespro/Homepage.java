package com.example.mynotespro;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Homepage extends AppCompatActivity {
    FloatingActionButton addNoteBtn;
    RecyclerView recyclerView;
    ImageButton menuBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view);
        menuBtn= (ImageButton) findViewById(R.id.menu_btn);

        addNoteBtn= (FloatingActionButton) findViewById(R.id.add_note_btn);


        addNoteBtn.setOnClickListener((V)-> startActivity(new Intent(Homepage.this,NoteDetails.class)));
        menuBtn.setOnClickListener((v)->showMenu());
        setupRecyclerView();
        

    }

    private void setupRecyclerView() {
    }

    private void showMenu() {
        
    }
}