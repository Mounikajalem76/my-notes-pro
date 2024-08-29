package com.example.mynotespro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import java.util.Calendar;

public class NoteDetails extends AppCompatActivity {
    EditText titleEditText, contentEditText;
    ImageButton saveNoteBtn;
    TextView pageTitleTextView;
    String title, content, docId;
    boolean isEditMode = false;
    TextView deleteTextViewBtn;
    TextView reminderTimeLabel;
    MaterialButton setReminderBtn;
    Timestamp reminderTimestamp;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        checkExactAlarmPermission();

        // Initialize UI elements
        titleEditText = findViewById(R.id.notes_title_text);
        contentEditText = findViewById(R.id.notes_content_text);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTextView = findViewById(R.id.page_title);
        deleteTextViewBtn = findViewById(R.id.delete);
        reminderTimeLabel = findViewById(R.id.reminder_time_label);
        setReminderBtn = findViewById(R.id.set_reminder_btn);

        // Check and request notification permission
        checkNotificationPermission();


        setReminderBtn.setOnClickListener(v -> openTimePickerDialog());

        title = getIntent().getStringExtra("title");
        content = getIntent().getStringExtra("content");
        docId = getIntent().getStringExtra("docId");

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        titleEditText.setText(title);
        contentEditText.setText(content);
        if (isEditMode) {
            pageTitleTextView.setText("Edit your note");
            deleteTextViewBtn.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener(view -> saveNote());
        deleteTextViewBtn.setOnClickListener(v -> deleteNoteFromFireBase());
    }

    private void checkExactAlarmPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private void openTimePickerDialog() {
        MaterialTimePicker picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Reminder Time")
                .build();

        picker.show(getSupportFragmentManager(), "Reminder Time Picker");

        picker.addOnPositiveButtonClickListener(dialog -> {
            int hour = picker.getHour();
            int minute = picker.getMinute();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            reminderTimestamp = new Timestamp(calendar.getTime());

            reminderTimeLabel.setText("Reminder set for: " + hour + ":" + (minute < 10 ? "0" + minute : minute));
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "MyNotesProChannel",
                    "MyNotesPro Channel",
                    NotificationManager.IMPORTANCE_HIGH
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    void saveNote() {
        String noteTitle = titleEditText.getText().toString();
        String noteContent = contentEditText.getText().toString();

        if (noteTitle.isEmpty()) {
            titleEditText.setError("Title is required");
            return;
        }

        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());
        note.setReminderTime(reminderTimestamp);  // Save the reminder time

        saveNoteFirebase(note);
    }

    void saveNoteFirebase(Note note) {
        DocumentReference documentReference;
        if (isEditMode) {
            documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        } else {
            documentReference = Utility.getCollectionReferenceForNotes().document();
        }
        documentReference.set(note).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(NoteDetails.this, "Notes added successfully", Toast.LENGTH_SHORT).show();
                if (note.getReminderTime() != null) {
                    scheduleAlarm(note.getReminderTime(), note.getTitle(), note.getContent());
                }
                finish();
            } else {
                Utility.showToast(NoteDetails.this, "Failed while Adding notes");
            }
        });
    }
    @SuppressLint("ScheduleExactAlarm")
    void scheduleAlarm(Timestamp reminderTime, String title, String content) {
        Intent intent = new Intent(this, ReminderBroadcast.class);
        intent.putExtra("title", title);
        intent.putExtra("content", content);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime.toDate().getTime(), pendingIntent);
    }


    void deleteNoteFromFireBase() {
        DocumentReference documentReference = Utility.getCollectionReferenceForNotes().document(docId);
        documentReference.delete().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(NoteDetails.this, "Notes Deleted successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Utility.showToast(NoteDetails.this, "Failed while Deleting notes");
            }
        });
    }

    // Method to check and request notification permission
    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }
}
