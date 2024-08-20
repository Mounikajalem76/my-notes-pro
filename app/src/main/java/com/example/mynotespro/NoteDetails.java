package com.example.mynotespro;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.Timestamp;

public class NoteDetails extends AppCompatActivity {
    EditText titleEditText,contentEditText;
    ImageButton saveNoteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        titleEditText= (EditText) findViewById(R.id.notes_title_text);
        contentEditText= (EditText) findViewById(R.id.notes_content_text);

        saveNoteBtn= (ImageButton) findViewById(R.id.save_note_btn);


        saveNoteBtn.setOnClickListener(view ->saveNote() );
    }
    void saveNote(){
        String noteTitle = titleEditText.getText().toString();
        String noteContent=contentEditText.getText().toString();

        if (noteTitle.isEmpty()){
            titleEditText.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteFirebase(note);


    }
    void  saveNoteFirebase(Note note){
        DocumentReference documentReference;
        documentReference = Utility.getCollectionReferenceForNotes().document();

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(NoteDetails.this, "success", Toast.LENGTH_SHORT).show();

                }else {
                    Utility.showToast(NoteDetails.this, "Failed while Adding notes");

                }

            }
        });

    }
}