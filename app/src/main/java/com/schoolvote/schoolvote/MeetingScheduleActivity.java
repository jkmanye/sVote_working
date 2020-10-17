package com.schoolvote.schoolvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class MeetingScheduleActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    User currentUser;
    String title;
    String email;
    LinearLayout container;
    LinearLayout.LayoutParams lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_schedule);

        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        email = currentUser.getEmail();
        container = findViewById(R.id.linear_ms);
        Map<String, Object> forsomeone = new HashMap<>();
        if (currentUser != null) {
            forsomeone.put("grade", currentUser.getGrade_update());
            forsomeone.put("clroom", currentUser.getClroom_update());
            forsomeone.put("school", currentUser.getSchool());
            loadMeetings(forsomeone);
        } else {
            Log.d("loadVote", "Logged in user does not exist.");
        }
    }

    private void loadMeetings(Map<String, Object> forsomeone) {
        if (forsomeone != null) {
            db.collection("meetings")
                    .whereEqualTo("for", forsomeone)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            lp.gravity = Gravity.CENTER;
                            title = (String) document.get("title");
                            TextView textView = new TextView(MeetingScheduleActivity.this);
                            textView.setText("\u0020\u0020\u0020\u0020\u0020\u2000" + "\u00b7" + title);
                            textView.setTextSize(18f);
                            textView.setOnClickListener(new TextView.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((TextView) findViewById(R.id.title_ms)).setText((String) document.get("title"));
                                    ((TextView) findViewById(R.id.subtitleb_ms)).setText(R.string.mi);
                                    ((TextView) findViewById(R.id.subtitleb_ms)).setBackgroundResource(R.drawable.openg);
                                    ((TextView) findViewById(R.id.subtitle_ms)).setText("\u0020\u0020\u0020\u0020\u0020\u0020" + (String) document.get("info"));
                                }
                            });
                            textView.setLayoutParams(lp);
                            container.addView(textView);
                            Log.d("loadMeeting", document.getId() + " => " + document.getData());
                        }
                    }
                }
            });
        }
    }

    public void join(View view) {
        TextView v = findViewById(R.id.title_ms);
        currentUser.setJoiningMeetingTitle(v.getText().toString());
        final Intent intent = new Intent(MeetingScheduleActivity.this, TalkActivity.class);
        intent.putExtra("currentUser", currentUser);
        intent.putExtra("title", currentUser.getJoiningMeetingTitle());
        startActivityForResult(intent, 1001);
    }

    public void goback(View view) { finish(); }
}