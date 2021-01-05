package martialcoder.surajsahani.safeouts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;


public class Notification extends AppCompatActivity {

    private static final String TAG ="MyTag" ;
    private TextView mOutputText;
    private BroadcastReceiver onMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        IntentFilter in = new IntentFilter("com.myApp.CUSTOM_EVENT");
        LocalBroadcastManager.getInstance(this).registerReceiver(onMessage, in);




//
//        mOutputText= findViewById(R.id.notificationfcm);
//
//        if (getIntent()!=null && getIntent().hasExtra("key1")){
//            mOutputText.setText("");
//            for (String key:getIntent().getExtras().keySet()) {
//                Log.d(TAG, "onCreate: Key"+key+" Data" +getIntent().getExtras().getString(key));
//                mOutputText.append(getIntent().getExtras().getString(key)+"\n");
//            }
//        }

//        getSupportActionBar().hide();

    }

    public void userdetailsactivity(View view) {
        startActivity(new Intent(Notification.this, UserDetails.class));
    }
    private BroadcastReceiver onNotice= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Update your RecyclerView here using notifyItemInserted(position);
        }};
}