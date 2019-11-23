package com.vysiontech.sewagemonitor;


import android.app.ProgressDialog;
import android.content.Intent;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mCityemail;
    private TextInputLayout mCityPassword;
    private Button mlogin;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgress;
    private Toolbar mToolbar;
    String city;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar=findViewById(R.id.login_appbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String emailStr=getIntent().getStringExtra("email");


        mProgress=new ProgressDialog(LoginActivity.this);
        mAuth=FirebaseAuth.getInstance();
        mCityemail=findViewById(R.id.login_email_id);
        mCityPassword=findViewById(R.id.login_password_id);
        mlogin=findViewById(R.id.login_btn);

        mlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mCityemail.getEditText().getText().toString();
                String password = mCityPassword.getEditText().getText().toString();
                if (email.equals(emailStr)){
                    if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {
                        mProgress.setTitle("Logging In");
                        mProgress.setMessage("Please Wait while we check your credentials");
                        mProgress.setCanceledOnTouchOutside(false);
                        mProgress.show();
                        loginUser(email, password);
                    }
                }else {
                    Toast.makeText(LoginActivity.this,"Enter Selected City Email",Toast.LENGTH_SHORT).show();
                }

            }

            private void loginUser(String email,String password) {
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            mProgress.dismiss();
                            Intent toZone=new Intent(LoginActivity.this,MainActivity.class);
                            toZone.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(toZone);
                            finish();
                        }
                        else
                        {
                            mProgress.hide();
                            Toast.makeText(LoginActivity.this,"Error Occured",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });



    }
}
