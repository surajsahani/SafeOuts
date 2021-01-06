package martialcoder.surajsahani.safeouts;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import martialcoder.surajsahani.safeouts.utils.AppRater;

public class UserDetails extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleSignInClient mSignInClient;
    private String mUsername;
    private String mPhotoUrl;
    private DatabaseReference mFirebaseDatabaseReference;
    private TextView uploadText;
    private ImageView dp;
    private TextView name, email, gender;
    TextView textBox,textview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        dp = (ImageView) findViewById(R.id.dp);
        name = (TextView) findViewById(R.id.name);
        email = (TextView) findViewById(R.id.email);
        textBox = (TextView) findViewById(R.id.address);

        Intent i = getIntent();
        final String i_name, i_email, i_gender, i_url;
        i_name = i.getStringExtra("p_name");
        i_email = i.getStringExtra("p_email");
        i_url = i.getStringExtra("p_url");

        name.setText(i_name);
        email.setText(i_email);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(i_url);
                    InputStream is = url.openConnection().getInputStream();
                    final Bitmap bmp = BitmapFactory.decodeStream(is);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dp.setImageBitmap(bmp);
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

//        uploadText=findViewById(R.id.textView6);
//        uploadText.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(UserDetails.this,UploadtActivity.class));
//            }
//        });
//        getSupportActionBar().hide();


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, PhoneAuthActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);
    }
//        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
//        SnapshotParser<UserInfo> parser = new SnapshotParser<UserInfo>() {
//            @Override
//            public UserInfo parseSnapshot(DataSnapshot dataSnapshot) {
//                UserInfo friendlyMessage = dataSnapshot.getValue(UserInfo.class);
//                if (friendlyMessage != null) {
//                    friendlyMessage.setId(dataSnapshot.getKey());
//                }
//                return friendlyMessage;
//            }
//        };
//
//    }

    public boolean user_logout(View view) {
        mFirebaseAuth.signOut();
        mSignInClient.signOut();

        startActivity(new Intent(this, PhoneAuthActivity.class));
        finish();
        return true;

    }

    public boolean rating(View view) {
        AppRater app = new AppRater();
        app.rateNow(UserDetails.this);
        return true;
    }

    public boolean contacttracing(View view) {
        Intent wintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Safeouts/Customer-App/blob/surajsahani/Link%20to%20Know%20More%20(1).pdf"));
        startActivity(wintent);
        return true;
    }

    public void privacy(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Safeouts/Customer-App/blob/surajsahani/PRIVACY_POLICY.md"));
        startActivity(intent);
    }

    public boolean changephone(View view) {
        mFirebaseAuth.signOut();
        mSignInClient.signOut();

        startActivity(new Intent(this, PhoneAuthActivity.class));
        finish();
        return true;
    }

    public void notification(View view) {
        startActivity(new Intent(this, Notification.class));
        finish();
    }
    public void upload(View view) {
        Intent intent = new Intent(UserDetails.this,UploadtActivity.class);
        startActivity(intent);
    }
}