package martialcoder.surajsahani.safeouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;


public class VerifyPhoneActivity extends AppCompatActivity {



    private String verificationId;
    private FirebaseAuth mAuth;
    private CountryCodePicker ccp;
    String getNo;

    private EditText editText, verification_code;
    Button send_verification_button, verification_button;
    private ProgressDialog progressDialog;
    private String verificationID;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken token;
    ImageView verified ,logo;
    TextView phone_number, register, enter_phone_number, resent_otp, change_phone, verified_phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_verify_phone);
        mAuth = FirebaseAuth.getInstance();
        verified=findViewById(R.id.verified_user);
        send_verification_button = findViewById(R.id.buttonVerify);
        editText = findViewById(R.id.phone_txt);
        ccp=findViewById(R.id.ccp);
        ccp.registerCarrierNumberEditText(editText);
        getNo = "+"+ccp.getFullNumber();
        verification_code=findViewById(R.id.verification_code_input);
        verification_button=findViewById(R.id.verify_button);
        phone_number=findViewById(R.id.verify_phone_number);
        register=findViewById(R.id.register_mobile);
        enter_phone_number=findViewById(R.id.enter_phone_number);
        resent_otp=findViewById(R.id.resentotp);
        change_phone=findViewById(R.id.changephoneno);
        logo=findViewById(R.id.safe_outs_logo);
        verified_phone=findViewById(R.id.verified);
        progressDialog =new ProgressDialog(this);

        send_verification_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String number = editText.getText().toString();
                if (TextUtils.isEmpty(number)) {
                    Toast.makeText(VerifyPhoneActivity.this, "Please Enter mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("Phone verification");
                    progressDialog.setMessage("please wait, we are authenticate your phone.");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            getNo, 60,
                            TimeUnit.SECONDS,
                            VerifyPhoneActivity.this,
                            callbacks);
                }
            }

        });


        verification_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_verification_button.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.INVISIBLE);

                String verificationcode=verification_code.getText().toString();
                if(TextUtils.isEmpty(verificationcode))
                {
                    Toast.makeText(VerifyPhoneActivity.this,"Please enter verification code first...",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    progressDialog.setTitle("Code verification");
                    progressDialog.setMessage("please wait, we are verifying your code...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,verificationcode);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

        callbacks= new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                progressDialog.dismiss();
                Toast.makeText(VerifyPhoneActivity.this,"Error: "+e,Toast.LENGTH_SHORT).show();

                send_verification_button.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);

                verification_button.setVisibility(View.INVISIBLE);
                verification_code.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                verificationID=s;
                token=forceResendingToken;
                progressDialog.dismiss();
                Toast.makeText(VerifyPhoneActivity.this,"Code has been sent,please check in messagebox...",Toast.LENGTH_SHORT).show();

                send_verification_button.setVisibility(View.INVISIBLE);
                editText.setVisibility(View.INVISIBLE);
                ccp.setVisibility(View.INVISIBLE);
                phone_number.setVisibility(View.VISIBLE);
                register.setVisibility(View.VISIBLE);
                verification_button.setVisibility(View.VISIBLE);
                verification_code.setVisibility(View.VISIBLE);
                enter_phone_number.setVisibility(View.INVISIBLE);
                resent_otp.setVisibility(View.VISIBLE);
                change_phone.setVisibility(View.VISIBLE);
                register.setVisibility(View.INVISIBLE);

            }
        };
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();
                            Toast.makeText(VerifyPhoneActivity.this,"Congratulations you are login successfully...",Toast.LENGTH_SHORT).show();
                            Intent loginintent=new Intent(VerifyPhoneActivity.this, MainActivity.class);
                            startActivity(loginintent);
                            finish();
                            verified.setVisibility(View.VISIBLE);
                            enter_phone_number.setVisibility(View.INVISIBLE);
                            phone_number.setVisibility(View.INVISIBLE);
                            send_verification_button.setVisibility(View.INVISIBLE);
                            editText.setVisibility(View.INVISIBLE);
                            verification_code.setVisibility(View.INVISIBLE);
                            verification_button.setVisibility(View.INVISIBLE);
                            phone_number.setVisibility(View.INVISIBLE);
                            enter_phone_number.setVisibility(View.INVISIBLE);
                            resent_otp.setVisibility(View.INVISIBLE);
                            change_phone.setVisibility(View.INVISIBLE);
                            logo.setVisibility(View.INVISIBLE);
                            verified_phone.setVisibility(View.VISIBLE);
                            register.setVisibility(View.INVISIBLE);

                        } else {
                            String message=task.getException().toString();
                            Toast.makeText(VerifyPhoneActivity.this,"Error :"+message,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void changephonenumber(View view) {
        Intent intent = new Intent(VerifyPhoneActivity.this, PhoneAuthActivity.class);
        startActivity(intent);
    }

}



