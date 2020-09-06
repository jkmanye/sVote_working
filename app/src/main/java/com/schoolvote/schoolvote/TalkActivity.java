package com.schoolvote.schoolvote;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class TalkActivity extends AppCompatActivity {

    @RequiresUser

    User currentUser;
    String title;

    EditText send_t;
    LinearLayout chat_t;
    LinearLayout.LayoutParams lp;

    long chatnum;
    Map<String, Object> chat = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        send_t = findViewById(R.id.send_t);
        chat_t = findViewById(R.id.chat_t);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        title = getIntent().getStringExtra("title");
        getMessages();

        new Thread(new Runnable() {
            @Override
            public void run() {
                final DocumentReference docRef = FirebaseFirestore.getInstance().collection("meetings").document(title);
                docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.e("Chat", "Listen failed.", e);
                            return;
                        }

                        if (snapshot != null && snapshot.exists()) {

                        } else {

                        }
                    }
                });
            }
        }).start();
    }

    public void getMessages() {
        FirebaseFirestore.getInstance().collection("meetings").document(title).collection("chat").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        lp.gravity = Gravity.CENTER;
                        TextView textView = new TextView(TalkActivity.this);
                        textView.setTextSize(24f);
                        final String message = (String) document.get("writer") + " : " +  (String) document.get("message");
                        textView.setText(message);
                        textView.setLayoutParams(lp);
                        chat_t.addView(textView);
                        Log.d("Chat", document.getId() + " => " + document.getData());
                    }
                } else {
                    Log.d("Chat", "Error!", task.getException());
                }
            }
        });
    }

    public void refresh() { Intent intent = new Intent(this, TalkActivity.class); intent.putExtra("currentUser", currentUser); intent.putExtra("title", title); startActivityForResult(intent, 1001); }

    public void sendMessage(View view)  {
        chat.put("writer", currentUser.getNumber_update());
        chat.put("message", send_t.getText().toString());
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("meetings").document(title);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    if(task.getResult().exists()) {
                        chatnum = (long) task.getResult().get("chatnum");
                        Log.d("Chat", "Read chatnum.");
                        final DocumentReference meetings = FirebaseFirestore.getInstance().collection("meetings").document(title);
                        FirebaseFirestore.getInstance().collection("meetings").document(title).collection("chat").document(Long.toString(chatnum)).set(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(TalkActivity.this, "메시지가 등록되었습니다.", Toast.LENGTH_LONG).show();
                                Log.d("Chat", "Added message.");
                                FirebaseFirestore.getInstance().runTransaction(new Transaction.Function<Void>() {
                                    @Override
                                    public Void apply(Transaction transaction) {
                                        transaction.update(meetings, "chatnum", chatnum * 11);
                                        return null;
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Chat", "Updated chatnum.");
                                        finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("Chat", "Updated chatnum with a failure.", e);
                                    }
                                });
                                refresh();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("Chat", "Error adding message.", e);
                                refresh();
                            }
                        });
                    } else {
                        Log.d("Chat", "No document found.");
                    }
                } else {
                    Log.e("Chat", "Error getting chatnum.", task.getException());
                }
            }
        });
    }
}