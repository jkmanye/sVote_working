package com.schoolvote.schoolvote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

public class MainMenuActivity extends AppCompatActivity {

    User currentUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        String pw = getIntent().getStringExtra("pw");
        if(getIntent() != null) {
            currentUser = (User) getIntent().getSerializableExtra("currentUser");
        }
        try {
            File file = new File(getApplicationContext().getFilesDir() + "/" + "auth.txt");
            if(!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.append(currentUser.getEmail() + "\n" + pw);
            osw.close();
            fos.close();
        } catch (Exception e) {

        }
    }

    public void vm(View view) {
        final Intent vm = new Intent(this, VoteManageActivity.class);
        if (currentUser != null) {
            vm.putExtra("currentUser", currentUser);
        }
        startActivityForResult(vm, 1001);
    }

    public void vs(View view) {
        final Intent vs = new Intent(this, VoteScheduleActivity.class);
        if (currentUser != null) {
            vs.putExtra("currentUser", currentUser);
        }
        startActivityForResult(vs, 1001);
    }

    public void ai(View view) {
        final Intent ai = new Intent(this, AccountInfoActivity.class);
        if (currentUser != null) {
            ai.putExtra("currentUser", currentUser);
        }
        startActivityForResult(ai, 1001);
    }

    public void vg(View view) {
        final Intent vg = new Intent(this, VoteGeneratingActivity.class);
        if (currentUser != null) {
            vg.putExtra("currentUser", currentUser);
        }
        startActivityForResult(vg, 1001);
    }

    public void fb(View view) {
        final Intent fb = new Intent(this, FeedbackActivity.class);
        if (currentUser != null) {
            fb.putExtra("currentUser", currentUser);
        }
        startActivityForResult(fb, 1001);
    }

    public void gm(View view) {
        final Intent gm = new Intent(this, MeetingGeneratingActivity.class);
        if (currentUser != null) {
            gm.putExtra("currentUser", currentUser);
        }
        startActivityForResult(gm, 1001);
    }

    public void ms(View view) {
        final Intent ms = new Intent(this, MeetingScheduleActivity.class);
        if (currentUser != null) {
            ms.putExtra("currentUser", currentUser);
        }
        startActivityForResult(ms, 1001);
    }

    public void mm(View view) {
        final Intent mm = new Intent(this, MeetingManageActivity.class);
        if(currentUser != null) {
            mm.putExtra("currentUser", currentUser);
        }
        startActivityForResult(mm, 1001);
    }
}
