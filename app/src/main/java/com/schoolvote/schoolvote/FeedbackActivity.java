package com.schoolvote.schoolvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class FeedbackActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
    }

    public void sendFeed(View view) {
        final EditText feed_fd = findViewById(R.id.feed_fd);
        final User currentUser = (User) getIntent().getSerializableExtra("currentUser");
        final Map<String, Object> map = new HashMap<>();
        map.put("feed", feed_fd.getText().toString());
        final String documentName = currentUser.getEmail() + currentUser.getFeedback();
        db.collection("feeds").document(documentName).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d("feedback", "Successfully added a feedback!");
                    Toast.makeText(FeedbackActivity.this, "감사합니다, 다른 피드백을 남기시려면 재로그인 후 이용해 주세요!", Toast.LENGTH_LONG).show();
                    finish();
                    final DocumentReference docRef = db.collection("users").document(currentUser.getEmail());
                    db.runTransaction(new Transaction.Function<Void>() {
                        @Override
                        public Void apply(Transaction transaction) {
                            transaction.update(docRef, "feedback", currentUser.getFeedback() + 1);
                            return null;
                        }
                    });
                } else {
                    Log.e("feedback", "Error while adding a feedback.");
                    Toast.makeText(FeedbackActivity.this, "잠시 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });
    }
}