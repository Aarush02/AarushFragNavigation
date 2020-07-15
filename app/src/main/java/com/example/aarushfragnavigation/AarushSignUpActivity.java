package com.example.aarushfragnavigation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class AarushSignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextPhone;
    private EditText editTextotp;
    private ProgressDialog progressDialog;
    private String mVerificationId;
    private FirebaseAuth mAuth;
    private Button buttonSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aarush_sign_up);
        editTextPhone = findViewById(R.id.edit_text_phone);
        editTextotp = findViewById(R.id.edit_text_otp);
        mAuth = FirebaseAuth.getInstance();
        buttonSend = findViewById(R.id.button_send);
        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        String phone = intent.getStringExtra("phone");
        sendVerificationCode(phone);
        editTextPhone.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.length()==10)
                {
                    buttonSend.setVisibility(View.VISIBLE);
                    getPhoneNumber();
                    return true;
                }
                if (v.length()<10)
                {
                    buttonSend.setVisibility(View.GONE);
                    return true;
                }
                return true;

            }
        });


    }
    private void getPhoneNumber() {
        String phone = editTextPhone.getText().toString().trim();
        if(phone.isEmpty() || phone.length() < 10){
            editTextPhone.setError("Enter a valid mobile");
            editTextPhone.requestFocus();
        }
        sendVerificationCode(phone);
    }
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            //Getting the code sent by SMS
            String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code
            if (code != null) {
                editTextotp.setText(code);
                //verifying the code
                verifyVerificationCode(code);
            }
        }



        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(AarushSignUpActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }



        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
            PhoneAuthProvider.ForceResendingToken mResendToken = forceResendingToken;
        }
    };

    private void verifyVerificationCode(String otp) {
        editTextotp.setVisibility(View.VISIBLE);
        String code = editTextotp.getText().toString().trim();
        if (code.isEmpty() || code.length() < 6) {
            editTextotp.setError("Enter valid code");
            editTextotp.requestFocus();
            return;
        }
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(AarushSignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            //verification successful we will start the profile activity
                            Intent intent = new Intent(AarushSignUpActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {

                            //verification unsuccessful.. display an error message

                            String message = "Somthing is wrong, we will fix it soon...";

                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                message = "Invalid code entered...";
                            }

                            Snackbar snackbar = Snackbar.make(findViewById(R.id.parent), message, Snackbar.LENGTH_LONG);
                            snackbar.setAction("Dismiss", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                            snackbar.show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_send:
                getPhoneNumber();
                break;


        }
    }
}