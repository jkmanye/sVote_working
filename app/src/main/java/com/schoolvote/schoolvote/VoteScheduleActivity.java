package com.schoolvote.schoolvote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
                @SuppressLint("SetTextI18n")
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (final QueryDocumentSnapshot document : task.getResult()) {
                            lp.gravity = Gravity.CENTER;
                            title = (String) document.get("title");
                            TextView textView = new TextView(VoteScheduleActivity.this);
                            textView.setText("\u0020\u0020\u0020\u0020\u0020\u0020" + "\u00b7" + title);
                            textView.setTextSize(18f);
                            textView.setOnClickListener(new TextView.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ((TextView) findViewById(R.id.subtitleb_vs)).setBackgroundResource(R.drawable.openg);
                                    ((TextView) findViewById(R.id.subtitleb_vs)).setText(R.string.vi);
                                    ((TextView) findViewById(R.id.title_vs)).setText((String) document.get("title"));
                                    ((TextView) findViewById(R.id.subtitle_vs)).setText("\u0020\u0020\u0020\u0020\u0020\u0020" + (String) document.get("info"));
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

    public void voteJoin(View view) {
        TextView v = findViewById(R.id.title_vs);
        currentUser.setJoiningVoteTitle(v.getText().toString());
        final Intent intent = new Intent(VoteScheduleActivity.this, VoteAlertDialogActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivityForResult(intent, 1001);
    }

    public void backToMenu(View view) { finish(); }
}