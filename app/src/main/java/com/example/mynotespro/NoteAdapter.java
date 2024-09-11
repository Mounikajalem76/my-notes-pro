package com.example.mynotespro;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {
    Context context;

    // Default colors array
    private final int[] defaultColors = {
            Color.parseColor("#FFCDD2"), // Light Red
            Color.parseColor("#F8BBD0"), // Light Pink
            Color.parseColor("#E1BEE7"), // Light Purple
            Color.parseColor("#C5CAE9"), // Light Blue
            Color.parseColor("#B3E5FC"), // Light Cyan
            Color.parseColor("#B2DFDB"), // Light Teal
            Color.parseColor("#DCEDC8"), // Light Green
            Color.parseColor("#FFF9C4"), // Light Yellow
            Color.parseColor("#FFECB3"), // Light Amber
            Color.parseColor("#FFCCBC")  // Light Orange
    };

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.titleTextView.setText(note.title);
        holder.contentTextView.setText(note.content);
        holder.timestampTextView.setText(
                note.timestamp != null ? Utility.timestampToString(note.timestamp) : "No Date Available"
        );

        // Set a background color from the defaultColors array based on position
        int colorIndex = position % defaultColors.length; // Cycle through the color array
        holder.itemView.setBackgroundColor(defaultColors[colorIndex]);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteDetails.class);
            intent.putExtra("title", note.title);
            intent.putExtra("content", note.content);
            String docId = this.getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note_item, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, timestampTextView;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.recycler_noteTitle);
            contentTextView = itemView.findViewById(R.id.notes_content_text);
            timestampTextView = itemView.findViewById(R.id.recycler_noteTimestamp);
        }
    }
}
