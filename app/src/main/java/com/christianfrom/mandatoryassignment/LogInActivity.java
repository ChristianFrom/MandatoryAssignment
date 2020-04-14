package com.christianfrom.mandatoryassignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
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
        TextView errorText = findViewById(R.id.loginErrorText);
        ProgressBar progressbar = findViewById(R.id.progressbar);

        String emailString = emailField.getText().toString();
        String passwordString = passwordField.getText().toString();
        hideKeyboard(view);
        if (emailString.length() > 4 && passwordString.length() > 4)
        {
            progressbar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(emailString, passwordString)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                loginSuccess(view);
                            } else {
                                // If sign in fails, display a message to the user.
                                progressbar.setVisibility(View.INVISIBLE);
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(LogInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                            // ...
                        }
                    });
        }
        else
        {
            errorText.setText("You need to type in an e-mail and password to login!");
        }
    }


    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void loginSuccess(View view){
        EditText emailField = findViewById(R.id.loginEmailEditText);
        EditText passwordField = findViewById(R.id.loginPasswordEditText);
        TextView errorText = findViewById(R.id.loginErrorText);
        ProgressBar progressbar = findViewById(R.id.progressbar);
        progressbar.setVisibility(View.INVISIBLE);

        emailField.setText("");
        passwordField.setText("");
        errorText.setText("");
        Intent intent = new Intent(this, PostLoginActivity.class);
        Toast.makeText(this, "Succesfully logged in", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

}
