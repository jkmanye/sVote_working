package com.schoolvote.schoolvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MeetingGeneratingActivity extends AppCompatActivity {

    EditText title_mg;
    EditText subtitle_mg;
    EditText grade_mg;
    EditText clroom_mg;

    User currentUser;

    Map<String, Object> meeting = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_generating);
        title_mg = findViewById(R.id.title_mg);
        subtitle_mg = findViewById(R.id.subtitle_mg);
        grade_mg = findViewById(R.id.grade_mg);
        clroom_mg = findViewById(R.id.clroom_mg);

        currentUser = (User) getIntent().getSerializableExtra("currentUser");
    }

    public void onCclick(View view) {
        final AlertDialog.Builder diabuild = new AlertDialog.Builder(this);

        meeting.put("title", title_mg.getText().toString());
        meeting.put("info", subtitle_mg.getText().toString());
        final Map<String, Object> forsomeone = new HashMap<>();
        forsomeone.put("grade", Long.parseLong(grade_mg.getText().toString()));
        forsomeone.put("clroom", Long.parseLong(clroom_mg.getText().toString()));
        forsomeone.put("school", currentUser.getSchool());
        meeting.put("for", forsomeone);
        meeting.put("chatnum", 1);
        meeting.put("opener", currentUser.getEmail());

        try {
            final String TAG = "addMeeting";
            FirebaseFirestore.getInstance().collection("meetings").document(title_mg.getText().toString()).set(meeting).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d(TAG, "Added meeting with title " + title_mg.getText().toString());
                    diabuild.setTitle("");
                    diabuild.setMessage("회의가 등록되었습니다.");
                    diabuild.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    diabuild.show();
                    Map<String, Object> startUpMessage = new HashMap<>();
                    startUpMessage.put("message", "이 생각방이 시작되었어요. 다양한 상상을 마음껏 펼쳐보아요.");
                    startUpMessage.put("writer", "관리자");
                    FirebaseFirestore.getInstance().collection("meetings").document(title_mg.getText().toString()).collection("chat").document("'관리자").set(startUpMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Starting message written.");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "Error writing startup message.", e);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error adding meeting.", e);
                }
            });
        } catch (Exception e) {
            diabuild.setTitle("");
            diabuild.setMessage("유효하지 않은 값이 기입되었습니다.");
            diabuild.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            diabuild.show();
        }
    }

    public void gobackkk(View view) {
        finish();
    }
}