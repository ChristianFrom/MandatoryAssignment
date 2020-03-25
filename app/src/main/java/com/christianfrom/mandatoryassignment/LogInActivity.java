package com.christianfrom.mandatoryassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInActivity extends AppCompatActivity {
    public FirebaseAuth mAuth;
    private static final String TAG = "authentication";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        mAuth = FirebaseAuth.getInstance();


    }

    public void loginButtonPressed(final View view) {
        EditText emailField = findViewById(R.id.loginEmailEditText);
        EditText passwordField = findViewById(R.id.loginPasswordEditText);

        String emailString = emailField.getText().toString();
        String passwordString = passwordField.getText().toString();

        mAuth.signInWithEmailAndPassword(emailString, passwordString)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            loginSuccess(view);
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LogInActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }
                        // ...
                    }
                });
    }

    public void loginSuccess(View view){
        EditText emailField = findViewById(R.id.loginEmailEditText);
        EditText passwordField = findViewById(R.id.loginPasswordEditText);
        emailField.setText("");
        passwordField.setText("");
        Intent intent = new Intent(this, PostLoginActivity.class);
        Toast.makeText(this, "Succesfully logged in", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    public void Toast(){
        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
    }
}
