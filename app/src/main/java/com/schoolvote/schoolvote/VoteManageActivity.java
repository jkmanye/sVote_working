package com.schoolvote.schoolvote;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoteManageActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView title_vm;
    TextView subtitle_vm;
    TextView forsomeone_vm;
    EditText searchfor_vm;
    Button search_vm;

    AlertDialog.Builder diabuild;
    AlertDialog.Builder diabuildTotal;
    Intent menu = new Intent(this, MainMenuActivity.class);
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vote_manage);
        diabuild = new AlertDialog.Builder(this);
        diabuildTotal = new AlertDialog.Builder(this);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        title_vm = findViewById(R.id.title_vm);
        subtitle_vm = findViewById(R.id.subtitle_vm);
        forsomeone_vm = findViewById(R.id.forsomeone_vm);
        searchfor_vm = findViewById(R.id.searchfor_vm);
    }

    public void update(DocumentSnapshot document) {
        if (document != null) {
            Map<String, Object> forsomeone = new HashMap<>();
            forsomeone = (Map) document.get("for");
            title_vm.setText(document.getString("title"));
            subtitle_vm.setText(document.getString("info"));
            forsomeone_vm.setText(forsomeone.get("grade").toString() + "학년 " + forsomeone.get("clroom").toString() + "반");
        } else {
            diabuild.setTitle("투표 찾기 실패");
            diabuild.setMessage("연 투표가 없거나 투표 찾기에 실패하였습니다.\n 어쩌면 자신이 연 투표가 아닐 지도 모릅니다.");
            diabuild.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivityForResult(menu, 1001);
                }
            });
            diabuild.show();
        }
    }

    public void loadData(View view) {
        DocumentReference docRef = db.collection("votes").document(searchfor_vm.getText().toString());
        currentUser.setJoiningVoteTitle(searchfor_vm.getText().toString());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document != null) {
                    try {
                        if (document.get("opener").equals(currentUser.getEmail())) {
                            update(document);
                        } else update(null);
                    } catch(Exception e) {
                        Log.e("loadVote", e.getMessage());
                        update(null);
                    }
                } else {
                    update(null);
                }
            }
        });
    }

    public void onClick(View view) {
        final EditText editText = new EditText(this);
        final String title = title_vm.getText().toString();
        final String[] inputTitle = new String[1];
        final DocumentReference docRef = db.collection("votes").document(title);
        diabuild.setTitle("삭제");
        diabuild.setMessage("계속하려면 투표 제목을 입력하세요 : " + title);
        diabuild.setView(editText);
        diabuild.setPositiveButton("입력", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inputTitle[0] = editText.getText().toString();
                if (inputTitle[0].equals(title)) {
                    docRef.delete();
                    Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "제목이 틀렸습니다.", Toast.LENGTH_SHORT).show();
                }
            }

        });
        diabuild.show();
    }

    public Map<Long, Long> getTotal(int all, Map<String, String> Lists, Map<String, Long> answer) {
        Map<Long, Long> total = new HashMap<>();
        for (String key : Lists.keySet()) {
            total.put(Long.parseLong(key), 0L);
        }

        for (Long value : answer.values()) {
            if (total.containsKey(value)) {
                total.put(value, total.get(value) + 1L);
            }
        }
        return total;
    }

    public void total(View view) {
        DocumentReference documentReference = db.collection("votes").document(currentUser.getJoiningVoteTitle());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    Map<String, String> Lists = (Map) documentSnapshot.get("Lists");
                    Map<String, Long> answer = (Map) documentSnapshot.get("answer");
                    Map<Long, Long> total = getTotal(answer.size(), Lists, answer);
                    List<String> displayTotal = new ArrayList<>();
                    long percentage;

                    if (answer.size() > 0) {
                        for (String key : Lists.keySet()) {
                            if (total.get(Long.parseLong(key)) > 0) {
                                percentage = total.get(Long.parseLong(key)) * 100 / answer.size();
                            } else percentage = 0;
                            displayTotal.add(String.format("%d%s %s\n%s %d %s %s %d%s", Long.parseLong(key) + 1, "번 항목 :", Lists.get(key), "득표수 :", total.get(Long.parseLong(key)), ",", "득표율 :", percentage, "%"));
                        }
                        diabuildTotal.setTitle(currentUser.getJoiningVoteTitle());
                        final CharSequence[] items = displayTotal.toArray(new String[displayTotal.size()]);
                        diabuildTotal.setItems(items, null);
                        diabuildTotal.show();
                    }
                } else {
                    diabuildTotal.setTitle(currentUser.getJoiningVoteTitle());
                    diabuildTotal.setMessage("아무도 응답하지 않았습니다!");
                    diabuildTotal.setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    diabuildTotal.show();
                }
            }
        });
    }
}