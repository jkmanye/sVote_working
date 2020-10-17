package com.schoolvote.schoolvote;

import android.Manifest;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.GregorianCalendar;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

@RequiresApi(api = Build.VERSION_CODES.M)
public class LoginActivity extends AppCompatActivity {

    private static final String KEY_NAME = "yourKey";
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private TextView textView;
    private FingerprintManager.CryptoObject cryptoObject;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private FirebaseAuth mAuth;
    GregorianCalendar gc = new GregorianCalendar();
    int loginDate = gc.get(gc.DAY_OF_MONTH);
    RegisterActivity ra = new RegisterActivity();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        if (!fingerprintManager.isHardwareDetected()) {
            // If a fingerprint sensor isn’t available, then inform the user that they’ll be unable to use your app’s fingerprint functionality//
            textView.setText("Your device doesn't support fingerprint authentication");
        }
        //Check whether the user has granted your app the USE_FINGERPRINT permission//
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {

        }

        //Check that the user has registered at least one fingerprint//
        if (!fingerprintManager.hasEnrolledFingerprints()) {

        }

        if (!keyguardManager.isKeyguardSecure()) {

        } else {
            try {generateKey();
            } catch (FingerprintException e) {
                e.printStackTrace();
            }

            if (initCipher()) {
                //If the cipher is initialized successfully, then create a CryptoObject instance//
                cryptoObject = new FingerprintManager.CryptoObject(cipher);

                // Here, I’m referencing the FingerprintHandler class that we’ll create in the next section. This class will be responsible
                // for starting the authentication process (via the startAuth method) and processing the authentication process events//
                FingerprintHandler helper = new FingerprintHandler(this);
                helper.startAuth(fingerprintManager, cryptoObject);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void generateKey() throws FingerprintException {
        try {
            // Obtain a reference to the Keystore using the standard Android keystore container identifier (“AndroidKeystore”)//
            keyStore = KeyStore.getInstance("AndroidKeyStore");

            //Generate the key//
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            //Initialize an empty KeyStore//
            keyStore.load(null);

            //Initialize the KeyGenerator//
            keyGenerator.init(new

                    //Specify the operation(s) this key can be used for//
                    KeyGenParameterSpec.Builder(KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT |
                            KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)

                    //Configure this key so that the user has to confirm their identity with a fingerprint each time they want to use it//
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());

            //Generate the key//
            keyGenerator.generateKey();

        } catch (KeyStoreException
                | NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | CertificateException
                | IOException exc) {
            exc.printStackTrace();
            throw new FingerprintException(exc);
        }
    }

    public boolean initCipher() {
        try {
            //Obtain a cipher instance and configure it with the properties required for fingerprint authentication//
            cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException |
                NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get Cipher", e);
        }

        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME,
                    null);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            //Return true if the cipher has been initialized successfully//
            return true;
        } catch (KeyPermanentlyInvalidatedException e) {

            //Return false if cipher initialization failed//
            return false;
        } catch (KeyStoreException | CertificateException
                | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Failed to init Cipher", e);
        }
    }

    private class FingerprintException extends Exception {
        public FingerprintException(Exception e) {
                super(e);
        }
    }

    public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {

        // You should use the CancellationSignal method whenever your app can no longer process user input, for example when your app goes
        // into the background. If you don’t use this method, then other apps will be unable to access the touch sensor, including the lockscreen!//

        private CancellationSignal cancellationSignal;
        private Context context;

        public FingerprintHandler(Context mContext) {
            context = mContext;
        }

        //Implement the startAuth method, which is responsible for starting the fingerprint authentication process//

        public void startAuth(FingerprintManager manager, FingerprintManager.CryptoObject cryptoObject) {

            cancellationSignal = new CancellationSignal();
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            manager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
        }

        @Override
        //onAuthenticationError is called when a fatal error has occurred. It provides the error code and error message as its parameters//

        public void onAuthenticationError(int errMsgId, CharSequence errString) {

            //I’m going to display the results of fingerprint authentication as a series of toasts.
            //Here, I’m creating the message that’ll be displayed if an error occurs//

            Toast.makeText(context, "Authentication error\n" + errString, Toast.LENGTH_LONG).show();
        }

        @Override

        //onAuthenticationFailed is called when the fingerprint doesn’t match with any of the fingerprints registered on the device//

        public void onAuthenticationFailed() {
            Toast.makeText(context, "Authentication failed", Toast.LENGTH_LONG).show();
        }

        @Override

        //onAuthenticationHelp is called when a non-fatal error has occurred. This method provides additional information about the error,
        //so to provide the user with as much feedback as possible I’m incorporating this information into my toast//
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Toast.makeText(context, "Authentication help\n" + helpString, Toast.LENGTH_LONG).show();
        }@Override

        //onAuthenticationSucceeded is called when a fingerprint has been successfully matched to one of the fingerprints stored on the user’s device//
        public void onAuthenticationSucceeded(
                FingerprintManager.AuthenticationResult result) {

            try {
                EditText email_lg = findViewById(R.id.email_lg);
                EditText pw_lg = findViewById(R.id.pw_lg);
                String path = getApplicationContext().getFilesDir() + "/" + "auth.txt";
                File file = new File(path);
                FileInputStream fis = new FileInputStream(file);
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                String email = "null", pw = "null";
                int counter = 0;
                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                    if(counter == 0) {
                        email = line;
                        counter++;
                    } else if (counter == 1) {
                        pw = line;
                        break;
                    }
                }
                email_lg.setText(email);
                pw_lg.setText(pw);
                onClick(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void onClick(View view) {
        final AlertDialog.Builder diabuild = new AlertDialog.Builder(this);
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        EditText email_lg = findViewById(R.id.email_lg);
        EditText pw_lg = findViewById(R.id.pw_lg);

        String email = email_lg.getText().toString();
        final String password = pw_lg.getText().toString();

        final Intent login = new Intent(this, LoginActivity.class);
        final Intent menu = new Intent(this, MainMenuActivity.class);

        final String TAG = "loginWithEmail";
        try {
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user.isEmailVerified()) {
                                    final String email = user.getEmail();
                                    final String TAG = "updateUI";
                                    final DocumentReference docRef = db.collection("users").document(email);
                                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if (task.isSuccessful()) {
                                                DocumentSnapshot document = task.getResult();
                                                if (document.exists()) {
                                                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                    final User currentUser = new User();
                                                    currentUser.setEmail(email);
                                                    currentUser.setGrade_update((long) document.get("grade"));
                                                    currentUser.setClroom_update((long) document.get("clroom"));
                                                    currentUser.setNumber_update((String) document.get("number"));
                                                    currentUser.setAdmin((boolean) document.get("isAdmin"));
                                                    currentUser.setFeedback((long) document.get("feedback"));
                                                    currentUser.setSchool((String) document.get("school"));
                                                    Log.d(TAG, "signInWithEmail:success");
                                                    diabuild.setTitle("로그인 성공");
                                                    diabuild.setMessage("로그인에 성공하였습니다.");
                                                    diabuild.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            menu.putExtra("currentUser", currentUser);
                                                            menu.putExtra("pw", password);
                                                            startActivityForResult(menu, 1001);
                                                        }
                                                    });
                                                    diabuild.show();

                                                } else {
                                                    Log.d(TAG, "No such document!");
                                                }
                                            } else {
                                                Log.d(TAG, "get failed with ", task.getException());
                                            }
                                        }
                                    });
                                    Log.d("updateUI", "Successed adding update datas.");
                                } else {
                                    if (loginDate - ra.d <= 1) {
                                        diabuild.setTitle("로그인 실패");
                                        diabuild.setMessage("인증되지 않은 이메일입니다. 인증을 완료한 후 다시 시도하세요.");
                                        diabuild.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivityForResult(login, 1001);
                                            }
                                        });
                                        diabuild.show();
                                    } else {
                                        DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getEmail().toString());
                                        docRef.delete();
                                        user.delete();
                                        diabuild.setTitle("로그인 실패");
                                        diabuild.setMessage("인증이 제한 시간 안에 되지 않은 계정입니다. 재가입 뒤 인증을 완료한 후 다시 시도하세요.");
                                        diabuild.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                startActivityForResult(login, 1001);
                                            }
                                        });
                                        diabuild.show();
                                    }
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                diabuild.setTitle("로그인 실패");
                                diabuild.setMessage("로그인에 실패하였습니다. 없는 계정이거나 오타가 있을 수 있습니다.");
                                diabuild.setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivityForResult(login, 1001);
                                    }
                                });
                                diabuild.show();
                            }

                            // ...
                        }
                    });
        } catch (Exception e) {
            diabuild.setTitle("로그인 실패");
            diabuild.setMessage("올바른 이메일이나 비밀번호를 입력해주세요!");
            diabuild.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivityForResult(login, 1001);
                }
            });
            diabuild.show();
        }
    }

    public void reg(View view) {
        final Intent register = new Intent(this, RegisterActivity.class);
        startActivityForResult(register, 1001);
    }

}