package com.schoolvote.schoolvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class MeetingManageActivity extends AppCompatActivity {

    AlertDialog.Builder diabuild;
    User currentUser;

    TextView title_mm, subtitle_mm, forsomeone_mm;
    EditText searchfor_mm;

    String inputTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_manage);

        diabuild = new AlertDialog.Builder(this);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");

        title_mm = findViewById(R.id.title_mm);
        subtitle_mm = findViewById(R.id.subtitle_mm);
        forsomeone_mm = findViewById(R.id.forsomeone_mm);
        searchfor_mm = findViewById(R.id.searchfor_mm);
    }

    public void search(View view) {
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("meetings").document(searchfor_mm.getText().toString());
        currentUser.setJoiningMeetingTitle(searchfor_mm.getText().toString());
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
                        Log.e("loadVote", "Error!", e);
                        update(null);
                    }
                } else {
                    update(null);
                }
            }
        });
        searchfor_mm.setText("");
    }

    public void update(DocumentSnapshot document) {
        if (document != null) {
            Map<String, Object> forsomeone = (Map<String, Object>) document.get("for");
            title_mm.setText(document.getString("title"));
            subtitle_mm.setText(document.getString("info"));
            forsomeone_mm.setText(forsomeone.get("grade").toString() + "학년 " + forsomeone.get("clroom").toString() + "반");
        } else {
            diabuild.setTitle("투표 찾기 실패");
            diabuild.setMessage("연 투표가 없거나 투표 찾기에 실패하였습니다.\n 어쩌면 자신이 연 투표가 아닐 지도 모릅니다.");
            diabuild.setPositiveButton("예", null);
            diabuild.show();
        }
    }

    public void onCcclick(View view) {
        final EditText editText = new EditText(this);
        final String title = title_mm.getText().toString();
        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("meetings").document(title);
        diabuild.setTitle("삭제");
        diabuild.setMessage("계속하려면 생각방 제목을 입력하세요 : " + title);
        diabuild.setView(editText);
        diabuild.setPositiveButton("입력", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inputTitle = editText.getText().toString();
                if (inputTitle.equals(title)) {
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
}