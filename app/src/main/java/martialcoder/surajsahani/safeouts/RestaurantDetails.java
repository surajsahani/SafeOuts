package martialcoder.surajsahani.safeouts;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import martialcoder.surajsahani.safeouts.utils.CustomScrollView;


public class RestaurantDetails extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    private TextView pName,pDesc,pPrice,distance,contact,NOPLive,NOPPresent,NOPBefore,emp1name,emp1desig,emp1num,emp2name,emp2desig,emp2num,emp3name,emp3desig,emp3num,emp4name,emp4desig,emp4num,emp5name,emp5desig,emp5num,empMask,sanitfreq;
    private ImageView PImage;
    private RelativeLayout CT;
    private String pId="",downloadImageUrl,locationintent="",dist;
    private Uri imageUri;
    TextView location23,Dlocation,Dcurr,DchangeLoc;;
    private FusedLocationProviderClient fusedLocationProviderClient;
    Dialog dialogPay,dialogLoc;
    CustomScrollView customScrollView;
    Button more;
    View bottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);
        firebaseFirestore = FirebaseFirestore.getInstance();
        customScrollView=(CustomScrollView) findViewById(R.id.myscroll);
        customScrollView.setEnableScrolling(false); // disable scrolling
        more=findViewById(R.id.more_btn);
        bottomView=findViewById(R.id.view_shadow_bottom);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                more.setVisibility(View.INVISIBLE);
                customScrollView.setEnableScrolling(true);
                bottomView.setVisibility(View.INVISIBLE);
            }
        });
//         // enable scrolling
        pId=getIntent().getStringExtra("pid");
        locationintent=getIntent().getStringExtra("location");
        dist=getIntent().getStringExtra("distance");
        CT=findViewById(R.id.contact_layout);
        pName=(TextView)findViewById(R.id.detail_name_txt);
        distance=findViewById(R.id.detail_dist_txt);
        dialogPay=new Dialog(RestaurantDetails.this);
        dialogPay.setContentView(R.layout.payment_dialog);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            dialogPay.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_bg));
        }
        contact=findViewById(R.id.detail_contact_txt);
        NOPLive=findViewById(R.id.detail_noOfpeople_txt);
        NOPPresent=findViewById(R.id.detail_sop_txt);
        NOPBefore=findViewById(R.id.detail_pre_covid_txt);
        emp1name=findViewById(R.id.emp_name1);
        emp1desig=findViewById(R.id.emp_post1);
        emp1num=findViewById(R.id.emp_rating1);
        emp2name=findViewById(R.id.emp_name2);
        emp2desig=findViewById(R.id.emp_post2);
        emp2num=findViewById(R.id.emp_rating2);
        emp3name=findViewById(R.id.emp_name3);
        emp3desig=findViewById(R.id.emp_post3);
        emp3num=findViewById(R.id.emp_rating3);
        emp4name=findViewById(R.id.emp_name4);
        emp4desig=findViewById(R.id.emp_post4);
        emp4num=findViewById(R.id.emp_rating4);
        emp5name=findViewById(R.id.emp_name5);
        emp5desig=findViewById(R.id.emp_post5);
        emp5num=findViewById(R.id.emp_rating5);
        sanitfreq=findViewById(R.id.detail_emp_sani_freq_txt);
        empMask=findViewById(R.id.detail_emp_wearing_mask_txt);
        pPrice=(TextView)findViewById(R.id.detail_address_txt);
        PImage=(ImageView)findViewById(R.id.cardimg_detail);
        getProductsDetails(pId);
        location23=findViewById(R.id.location_txt_detail);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        location23.setText(locationintent);
        CT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPay.show();
            }
        });
        dialogLoc=new Dialog(RestaurantDetails.this);
        dialogLoc.setContentView(R.layout.loc_dialog);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            dialogLoc.getWindow().setBackgroundDrawable(getDrawable(R.drawable.loc_dialog_bg));
        }
        Window window=dialogLoc.getWindow();
        WindowManager.LayoutParams wlp=window.getAttributes();
        wlp.gravity= Gravity.TOP;
        window.setAttributes(wlp);
        Dlocation=dialogLoc.findViewById(R.id.location_txt_detail_dialog);
        Dcurr=dialogLoc.findViewById(R.id.curr_loc_txt);
        DchangeLoc=dialogLoc.findViewById(R.id.change_loc_txt);
        Dcurr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoc.dismiss();
            }
        });
        DchangeLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RestaurantDetails.this,ChangeLocation.class));
            }
        });
        location23.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoc.show();
            }
        });
    }

    private void getProductsDetails(String pId) {
        CollectionReference productref= firebaseFirestore.collection("Restaurants");
        productref.document(pId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot!=null && documentSnapshot.exists()){
                    Products products=documentSnapshot.toObject(Products.class);
                    // ddsds//
                    Picasso.get().load(products.getImage()).into(PImage);
                    pPrice.setText(products.getAddress());
                    pName.setText(products.getPName());
                    distance.setText(dist+" kms");
                    contact.setText("+91"+products.getContact());
                    NOPLive.setText("No of people(live)     "+products.getNopive());
                    NOPPresent.setText("Capacity % after SOP  "+products.getNOPPresent()+"%");
                    NOPBefore.setText("Capacity % pre-covid  "+products.getNOPBefore()+"%");
                    emp1name.setText(products.getEmp1Name());
                    emp1desig.setText(products.getEmp1design());
                    emp1num.setText(products.getEmp1number()+"F");
                    emp2name.setText(products.getEmp2Name());
                    emp2desig.setText(products.getEmp2design());
                    emp2num.setText(products.getEmp2number()+"F");
                    emp3name.setText(products.getEmp3Name());
                    emp3desig.setText(products.getEmp3design());
                    emp3num.setText(products.getEmp3number()+"F");
                    emp4name.setText(products.getEmp4Name());
                    emp4desig.setText(products.getEmp4design());
                    emp4num.setText(products.getEmp4number()+"F");
                    emp5name.setText(products.getEmp5Name());
                    emp5desig.setText(products.getEmp5design());
                    emp5num.setText(products.getEmp5number()+"F");
                    sanitfreq.setText(products.getSanitFreqs());
                    empMask.setText(products.getMaskEmp()+" employees");
                    String ct=products.getContactTracing();
                    Toast.makeText(RestaurantDetails.this, "Value is"+ct, Toast.LENGTH_SHORT).show();
                    if (ct.equals("yes"))
                    {
                        CT.setVisibility(View.VISIBLE);
                    }
                    else{
                        CT.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    public void zomato(View view) {
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.application.zomato");
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + "com.application.zomato"));
            startActivity(intent);
        }
    }

    public void swiggy(View view) {
        Intent intent = getPackageManager().getLaunchIntentForPackage("in.swiggy.android");
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            // Bring user to the market or let them choose an app?
            intent = new Intent(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("market://details?id=" + "in.swiggy.android"));
            startActivity(intent);
        }
    }
    public void Knowmore(View view) {
        Intent wintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Safeouts/Customer-App/blob/surajsahani/Link%20to%20Know%20More%20(1).pdf"));
        startActivity(wintent);
    }


    public void userdetailsactivity(View view) {
        startActivity(new Intent(RestaurantDetails.this, UserDetails.class));
    }
}