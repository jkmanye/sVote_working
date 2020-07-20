package com.schoolvote.schoolvote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TalkActivity extends AppCompatActivity {

    @RequiresUser

    User currentUser;

    EditText send_t;
    LinearLayout chat_t;
    LinearLayout.LayoutParams lp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        send_t = findViewById(R.id.send_t);
        chat_t = findViewById(R.id.chat_t);
        lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        currentUser = (User) getIntent().getSerializableExtra("currentUser");
    }

    public void refresh() {
        overridePendingTransition(0, 0);
        finish();
        overridePendingTransition(0, 0);
        startActivityForResult(new Intent(this, TalkActivity.class), 1001);
        overridePendingTransition(0, 0);
    }

    public void sendMessage(View view) throws Throwable {
        final TextView textView = new TextView(this);
        textView.setText(send_t.getText());

        //Refresh activity
        refresh();
    }
}