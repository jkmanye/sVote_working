package com.schoolvote.schoolvote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.text.ParseException;

public class ReauthenticatingActivity extends AppCompatActivity {

	EditText grade_ra;
	EditText clroom_ra;
	EditText number_ra;
	User currentUser;
	AlertDialog.Builder diabuild;

	FirebaseFirestore db = FirebaseFirestore.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reauthenticating);

		grade_ra = findViewById(R.id.grade_ra);
		clroom_ra = findViewById(R.id.clroom_ra);
		number_ra = findViewById(R.id.number_ra);

		diabuild = new AlertDialog.Builder(this);

		currentUser = (User) getIntent().getSerializableExtra("currentUser");
	}

	public void gobacktoaccountinfo(View view) { finish(); }
	public void done(View view) {

		try {

			final DocumentReference users = db.collection("users").document(currentUser.getEmail());

			db.runTransaction(new Transaction.Function<Void>() {
				@Override
				public Void apply(Transaction transaction) throws FirebaseFirestoreException {
					try {
						transaction.update(users, "grade", Integer.parseInt(grade_ra.getText().toString()));
						transaction.update(users, "clroom", Integer.parseInt(clroom_ra.getText().toString()));
						transaction.update(users, "number", Integer.parseInt(number_ra.getText().toString()));
					} catch (Exception e) {
						diabuild.setTitle("회원 정보 수정");
						diabuild.setMessage("회원가입할 때도 말씀드렸지만, 학년, 반, 그리고 번호에는 반드시 숫자만 기입해주세요!");
						diabuild.setPositiveButton("예", null);
						diabuild.show();
					}
					return null;
				}
			}).addOnSuccessListener(new OnSuccessListener<Void>() {
				@Override
				public void onSuccess(Void aVoid) {
					Log.d("Reauthenticate", "Reauthenticated successfully!");
					diabuild.setTitle("성공");
					diabuild.setMessage("성공하였습니다.");
					diabuild.setPositiveButton("예", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialogInterface, int i) {
							finish();
						}
					});
					diabuild.show();
				}
			}).addOnFailureListener(new OnFailureListener() {
				@Override
				public void onFailure(@NonNull Exception e) {
					Log.w("Reauthenticate", "Reauthenticated with a failure.", e);
				}
			});

		} catch (Exception e) {
			diabuild.setMessage("잠시 오류가 발생하였습니다.");
			diabuild.setPositiveButton("예", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int i) {
					finish();
				}
			});
			diabuild.show();
		}

	}
}