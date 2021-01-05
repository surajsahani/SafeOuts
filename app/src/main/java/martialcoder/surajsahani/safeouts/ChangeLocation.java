package martialcoder.surajsahani.safeouts;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


public class ChangeLocation extends AppCompatActivity {
    private TextView loactionAct,currLocation,hint1,hint2,hint3,hint4;
    EditText newLocationEdt;
    ImageView searchLocation;
    private FusedLocationProviderClient fusedLocationProviderClient;
    RelativeLayout belowLayout;
    private String locationintent="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_location);
        locationintent=getIntent().getStringExtra("location");
        loactionAct=findViewById(R.id.location_txt_detail_act);
        loactionAct.setText(locationintent);
        currLocation=findViewById(R.id.curr_loc_txt_act);
        newLocationEdt = findViewById(R.id.change_loc_edt_txt_act);
        searchLocation= findViewById(R.id.location_act_search_img);
        belowLayout=findViewById(R.id.below_layout_act_location);
        hint1=findViewById(R.id.hint1);
        hint2=findViewById(R.id.hint2);
        hint4=findViewById(R.id.hint4);
        hint3=findViewById(R.id.hint3);
        hint1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hint1s=hint1.getText().toString();
                newLocationEdt.setText(hint1s);
            }
        });
        hint2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hint2s=hint2.getText().toString();
                newLocationEdt.setText(hint2s);
            }
        });
        hint3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hint3s=hint3.getText().toString();
                newLocationEdt.setText(hint3s);
            }
        });
        hint4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hint4s=hint4.getText().toString();
                newLocationEdt.setText(hint4s);
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        currLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        searchLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newLocationEdt.getText()!=null)
                {
                    String loc2=newLocationEdt.getText().toString();
                    Intent backintent=new Intent(ChangeLocation.this,MainActivity.class);
                    backintent.putExtra("location",loc2);
                    startActivity(backintent);
                    finish();
                }
            }
        });
    }



}