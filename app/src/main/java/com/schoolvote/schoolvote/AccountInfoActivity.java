package com.schoolvote.schoolvote;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class AccountInfoActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")

    User currentUser;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);
        final TextView email_ai = findViewById(R.id.email_ai);
        currentUser = (User) getIntent().getSerializableExtra("currentUser");
        email_ai.setText(currentUser.getEmail());
        final TextView grade_ai = findViewById(R.id.grade_ai);
        grade_ai.setText(Long.toString(currentUser.getGrade_update()));
        final TextView clroom_ai = findViewById(R.id.clroom_ai);
        clroom_ai.setText(Long.toString(currentUser.getClroom_update()));
        final TextView number_ai = findViewById(R.id.number_ai);
        number_ai.setText(currentUser.getNumber_update());
        final TextView school_ai = findViewById(R.id.school_ai);
        school_ai.setText(currentUser.getSchool());
    }

    public void reauthenticate(View view) {
        final Intent intent = new Intent(this, ReauthenticatingActivity.class);
        intent.putExtra("currentUser", currentUser);
        startActivityForResult(intent, 1001);
    }

    public void goback(View view) {
        finish();
    }
}
