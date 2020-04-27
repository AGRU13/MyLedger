package com.spareroom.myledger;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {
    private EditText addTitle, addPassword;
    private Button button;
    private AdView mAdView;
    private boolean sign = true;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        addTitle = findViewById(R.id.title);
        addPassword = findViewById(R.id.password);
        button = findViewById(R.id.button);

        mAuth = FirebaseAuth.getInstance();

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        Bundle bundle = getIntent().getExtras();
        sign = bundle.getBoolean("boolean");

        if (sign) {
            button.setText("Login");
            addTitle.setText(MainActivity.accountList.get(bundle.getInt("position")));
        }
        else
            button.setText("Create Account");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sign) {

                    mAuth.signInWithEmailAndPassword(addTitle.getText().toString() + "@myDomain.com", addPassword.getText().toString())
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        logIn();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Login.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                }
                else {
                    if (addPassword.getText().toString().length()<6)
                        Toast.makeText(Login.this, "Password should be at least 6 characters long", Toast.LENGTH_SHORT).show();
                    else if (addTitle.getText().toString().length()<4)
                        Toast.makeText(Login.this, "username should be at least 4 characters long", Toast.LENGTH_SHORT).show();
                    else signUp();
                }
            }
        });


    }

    private void signUp(){
        mAuth.createUserWithEmailAndPassword(addTitle.getText().toString()+"@myDomain.com", addPassword.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("username").setValue(addTitle.getText().toString());
                            logIn();
                        } else {
                            // If sign in fails, display a message to the user.
                            FirebaseAuthException e =(FirebaseAuthException) task.getException();
                            Toast.makeText(Login.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void logIn() {
        startActivity(new Intent(this,InAccount.class).putExtra("position",0));
    }
}