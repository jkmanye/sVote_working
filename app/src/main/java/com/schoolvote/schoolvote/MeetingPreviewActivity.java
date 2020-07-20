package com.schoolvote.schoolvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MeetingPreviewActivity extends AppCompatActivity {

    TextView title_mp;
    TextView info_mp;

    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_preview);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        title_mp = findViewById(R.id.title_mg);
        info_mp = findViewById(R.id.info_mp);
        title_mp.setText(currentUser.getJoiningMeetingTitle());
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("meetings").document(title_mp.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    info_mp.setText((String) document.get("info"));
                    Log.d("meetingPreview", "Loaded data for preview.");
                } else {
                    Log.e("meetingPreview", "Error!", task.getException());
                }
            }
        });
    }

    public void finaljoin(View view) { Intent i = new Intent(this, TalkActivity.class); i.putExtra("currentUser", currentUser);  startActivityForResult(i, 1001); }

    public void goback(View view) { finish(); }
}