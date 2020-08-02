package com.schoolvote.schoolvote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class VoteScheduleActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email;
    String title;
    User currentUser;
    LinearLayout container;
    LinearLayout.LayoutParams lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_schedule);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        email = currentUser.getEmail();
        container = findViewById(R.id.linear_vs);
        Map<String, Object> forsomeone = new HashMap<>();
        if (currentUser != null) {
            forsomeone.put("grade", currentUser.getGrade_update());
            forsomeone.put("clroom", currentUser.getClroom_update());
            forsomeone.put("school", currentUser.getSchool());
            loadVote(forsomeone);
        } else {
            Log.d("loadVote", "Logged in user does not exist.");
        }
    }

    private void loadVote(Map<String, Object> forsomeone) {
        if (forsomeone != null) {
            db.collection("votes")
                    .whereEqualTo("for", forsomeone)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            lp.gravity = Gravity.CENTER;
                            title = (String) document.get("title");
                            TextView textView = new TextView(VoteScheduleActivity.this);
                            textView.setText(title);
                            textView.setTextSize(36f);
                            textView.setOnClickListener(new TextView.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    TextView v = (TextView)view;
                                    currentUser.setJoiningVoteTitle(v.getText().toString());
                                    final Intent intent = new Intent(VoteScheduleActivity.this, VotePreviewActivity.class);
                                    intent.putExtra("currentUser", currentUser);
                                    startActivityForResult(intent, 1001);
                                }
                            });
                            textView.setLayoutParams(lp);
                            container.addView(textView);
                            Log.d("loadVote", document.getId() + " => " + document.getData());
                        }
                    }
                }
            });
        }
    }
}