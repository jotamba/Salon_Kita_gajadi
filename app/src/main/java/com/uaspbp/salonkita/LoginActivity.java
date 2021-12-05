package com.uaspbp.salonkita;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;


public class LoginActivity extends AppCompatActivity {

    private String CHANNEL_ID = "channel 2";

    TextInputEditText emailLogin,passwordLogin;
    Button Login,Register, btnClear;
    CheckBox cbShowPw;
    TextView textview;
    FirebaseAuth firebaseAuth;
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

        setContentView(R.layout.activity_login);
        emailLogin = findViewById(R.id.edtEmailLogin);
        passwordLogin = findViewById(R.id.edtPasswordLogin);
        firebaseAuth = FirebaseAuth.getInstance();
        Register = findViewById(R.id.btnRegister1);
        Login = findViewById(R.id.btnLogin);
        btnClear = findViewById(R.id.btnClear);
        textview = findViewById(R.id.textview);
        cbShowPw = findViewById(R.id.cbShowPw);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String CHANNEL_ID = "Channel 1";
            CharSequence name = "Channel 1";
            String description = "This is Channel 1";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        cbShowPw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    passwordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    passwordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic("news")
                .addOnCompleteListener(new OnCompleteListener<Void>(){
                    @Override
                    public void onComplete(@NonNull Task<Void> task){
                        String mag = "Selamat Datang :)";
                        if(!task.isSuccessful()){
                            mag = "Failed";
                        }
                        Toast.makeText(LoginActivity.this, mag, Toast.LENGTH_SHORT).show();
                    }
                });

        Register.setPaintFlags(Register.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //temp inputan data
                String tempemail = emailLogin.getText().toString();
                String temppassword = passwordLogin.getText().toString();

                //  Mengkosongkan Input
                emailLogin.setText("");
                passwordLogin.setText("");

                //  Memunculkan SnackBar
                Snackbar.make(textview,"Text Cleared Success",Snackbar.LENGTH_LONG)
                        .setAction("Cancel", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                emailLogin.setText(tempemail);
                                passwordLogin.setText(temppassword);
                            }
                        })
                        .show();
            }
        });

        Login = findViewById(R.id.btnLogin);

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailLogin.getText().toString().equalsIgnoreCase("admin") && passwordLogin.getText().toString().trim().equalsIgnoreCase("admin"))
                {
                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();

                    FragmentManager fragmentManager = getSupportFragmentManager();


                }
                else
                {
                    if (emailLogin.getText().toString().isEmpty() && passwordLogin.getText().toString().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                    } else if (emailLogin.getText().toString().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Email Invalid", Toast.LENGTH_SHORT).show();
                    } else if (passwordLogin.getText().toString().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Password Invalid", Toast.LENGTH_SHORT).show();
                    } else if (!(emailLogin.getText().toString().isEmpty() && passwordLogin.getText().toString().isEmpty())) {
                        firebaseAuth.signInWithEmailAndPassword(emailLogin.getText().toString(), passwordLogin.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                        Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class); //main harus ubah ke homepage
                                        startActivity(intent);
                                        createNotificationChannel();
                                        addNotification();
                                        finish();
                                    }else{
                                        Toast.makeText(LoginActivity.this, "Silahkan Verifikasi Email Anda !!", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(LoginActivity.this,task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });



    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel 2";
            String description = "This Channel 2";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void addNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Login Berhasil")
                .setContentText("Selamat Datang")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        Intent notificationIntent = new Intent( this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}