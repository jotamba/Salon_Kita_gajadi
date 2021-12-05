package com.uaspbp.salonkita;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static final String TAG = "TAG";
    public static final String TAG1 = "TAG";
    TextInputEditText email,nama,alamat,telepon,username,password;
    TextView result;
    Button register;
    Button backLogin;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore fStore;
    String userID;
    AppPreferencesManager preferencesManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManager = new AppPreferencesManager(this);
        if (preferencesManager.getDarkModeState()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        }
        setContentView(R.layout.activity_register);

        result = findViewById(R.id.textview);
        firebaseAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        email = findViewById(R.id.edtEmailRegister);
        password = findViewById(R.id.edtPasswordRegister);
        register = findViewById(R.id.btnRegisterUser);
        nama = findViewById(R.id.edtName);
        alamat = findViewById(R.id.edtAddress);
        telepon = findViewById(R.id.edtHand);
        username = findViewById(R.id.edtUsername);
        backLogin = findViewById(R.id.buttonBackLogin);

        backLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                computeMD5Hash(password.toString());

                final String Fusername = username.getText().toString();
                final String Fnama = nama.getText().toString();
                final String Ftelp = telepon.getText().toString();
                final String Falamat = alamat.getText().toString();
                final String Femail = email.getText().toString().trim();
                final String Fpass = password.getText().toString().trim();
                final String emPass = result.getText().toString().trim();

                if (nama.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Name field must be filled", Toast.LENGTH_SHORT).show();
                }else if(alamat.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Address field must be filled", Toast.LENGTH_SHORT).show();
                }else if (telepon.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Phone number field must be filled", Toast.LENGTH_SHORT).show();
                }else if (username.getText().toString().isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Username field must be filled", Toast.LENGTH_SHORT).show();
                }else if (email.getText().toString().isEmpty() && password.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Email or password is incorrect", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Email Invalid", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Password Invalid", Toast.LENGTH_SHORT).show();
                } else if (!(email.getText().toString().isEmpty() && password.getText().toString().isEmpty())) {
                    firebaseAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser userz = firebaseAuth.getCurrentUser();
                                userz.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(RegisterActivity.this, "Email Verifikasi Telah Dikirim", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: Email Tidak Terkirim!! Silahkan Cek Kembali!! " + e.getMessage());
                                    }
                                });

                                Toast.makeText(RegisterActivity.this, "Proses Registrasi Berhasil", Toast.LENGTH_LONG).show();
                                userID = firebaseAuth.getCurrentUser().getUid();
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                Map<String,Object> user = new HashMap<>();
                                user.put("fName", Fnama);
                                user.put("alamat", Falamat);
                                user.put("email", Femail);
                                user.put("telp", Ftelp);
                                user.put("username", Fusername);
                                user.put("password", emPass);

                                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "onSuccess : user Profile is created for"+ userID);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure : "+ e.toString());
                                    }
                                });
                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                            } else {
                                Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }
    public void computeMD5Hash(String password) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(password.getBytes());
            byte[] messageDigest = digest.digest();

            StringBuffer MD5Hash = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                MD5Hash.append(h);
            }

            result.setText(MD5Hash);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}