package martialcoder.surajsahani.safeouts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener  {
    FirebaseFirestore firebaseFirestore;

    private String mPhotoUrl;
    private String mUsername;
    RecyclerView recyclerView;
    private CollectionReference TutorRefs;
    RecyclerView.LayoutManager layoutManager;
    TextView location2,Dlocation,Dcurr,DchangeLoc;
    Dialog dialogLoc;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final String TAG = "MainActivity";
    private SharedPreferences mSharedPreferences;
    private GoogleSignInClient mSignInClient;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private int changeLoc=0;
    private String locationintent="";
    private String lat1,lat2,long1,long2;
    private TextView nearby;
    private String coord;
    String[] arrOfStrStart ;
    String[] arrOfStrEnd ;
    double latDouble1,latDouble2,longDouble1,longDouble2 ;
    Double latt,longtt;
    double longitude,latitude;

    private static final int REQUEST_CODE_LOGIN = 10;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions googleSignInOptions;
    private ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseFirestore = FirebaseFirestore.getInstance();
        nearby=findViewById(R.id.nearby_res);
        locationintent=getIntent().getStringExtra("location");
        logo = findViewById(R.id.home_logo);
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UserDetails.class));
            }
        });

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().requestProfile().build();
        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .addApi(Plus.API).build();
        logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signin = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signin, REQUEST_CODE_LOGIN);
            }
        });

        recyclerView = findViewById(R.id.offers_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        TutorRefs = firebaseFirestore.collection("Restaurants");
        location2 = findViewById(R.id.location_txt);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        onStart();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

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
            if (mFirebaseUser.getPhotoUrl() !=null){
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mSignInClient = GoogleSignIn.getClient(this, gso);

        dialogLoc=new Dialog(MainActivity.this);
        dialogLoc.setContentView(R.layout.loc_dialog);
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            dialogLoc.getWindow().setBackgroundDrawable(getDrawable(R.drawable.loc_dialog_bg));
        }
        Window window=dialogLoc.getWindow();
        WindowManager.LayoutParams wlp=window.getAttributes();
        wlp.gravity= Gravity.TOP;
        window.setAttributes(wlp);
        dialogLoc.getWindow().getAttributes().windowAnimations=R.style.DialogAnimation;
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
                changeLoc=1;
                Intent intent=new Intent(MainActivity.this,ChangeLocation.class);
                intent.putExtra("location",location2.getText().toString());
                startActivity(intent);
            }
        });
//        Toast.makeText(this, "Geocoordinates of "+location2+" is "+lat1+"\n"+long1, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Geocoordinates of "+"restaurant is "+lat2+"\n"+long2, Toast.LENGTH_SHORT).show();
//        Toast.makeText(this, "Location intent is "+locationintent, Toast.LENGTH_SHORT).show();
        location2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLoc.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                    try {
                                        List<Address> addresses = geocoder.getFromLocation(
                                                location.getLatitude(), location.getLongitude(),
                                                1
                                        );
                                        if (locationintent == null)
                                        {
                                            location2.setText(addresses.get(0).getSubLocality());
                                            Dlocation.setText(addresses.get(0).getSubLocality());
                                            String address=location2.getText().toString();
                                            GeoLocation geoLocation=new GeoLocation();
                                            geoLocation.getAddress(address,getApplicationContext(),new GeoHandler());
                                            if(lat1!=null && long1!=null){
                                                latDouble1 = Double.valueOf(lat1);
                                                longDouble1=Double.valueOf(long1);
                                            }
                                        }
                                        else
                                        {
                                            location2.setText(locationintent);
                                            Dlocation.setText(locationintent);
                                            String address=location2.getText().toString();
                                            GeoLocation geoLocation=new GeoLocation();
                                            geoLocation.getAddress(address,getApplicationContext(),new GeoHandler());
                                            if(lat1!=null && long1!=null){
                                                latDouble1 = Double.valueOf(lat1);
                                                longDouble1=Double.valueOf(long1);
                                            }
                                        }

                                    } catch (IOException e) {
                                        Toast.makeText(MainActivity.this, "Exception occured", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    }
                                    latt = location.getLatitude();
                                    longtt = location.getLatitude();

                                } else {
                                    Toast.makeText(MainActivity.this, "Device location is turned off", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Toast.makeText(this, "The Google Api is not able to fetch the user location thats why we are shutting down the app.", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            }
        }
        Query query=TutorRefs.orderBy("PName", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Products> options=new FirestoreRecyclerOptions.Builder<Products>()
                .setQuery(query,Products.class)
                .build();
        FirestoreRecyclerAdapter<Products, ProductViewHolder> adapter = new FirestoreRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                productViewHolder.textproductName.setText(products.getPName());
                productViewHolder.txtProductPrice.setText(products.getNopive()+" peoples");
                Picasso.get().load(products.getImage()).into(productViewHolder.ProductImage);
                Picasso.get().load(products.getImage()).into(productViewHolder.ProductImagelogo);
                String address=products.getAddress();
                lat2=products.getLatitude();
                long2=products.getLongtitude();
                double lat2set=Double.valueOf(lat2);
                double longt2set=Double.valueOf(long2);
                Geocoder coder = new Geocoder(MainActivity.this);
                try {
                    ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(location2.getText().toString(), 50);
                    for(Address add : adresses){
                        longitude = add.getLongitude();
                        latitude = add.getLatitude();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Location StartLoc=new Location("");
                StartLoc.setLatitude(lat2set);
                StartLoc.setLongitude(longt2set);
                Location EndLoc=new Location("");
                EndLoc.setLatitude(latitude);
                EndLoc.setLongitude(longitude);
                double dist=StartLoc.distanceTo(EndLoc);
                double distright=dist/1000;
                double roundedDouble=Math.round(distright*100)/100.0;
                String distStr=String.valueOf(roundedDouble);
                productViewHolder.textProductInfo.setText(distStr+" kms");
//                float[] dist = new float[0];
//                Location.distanceBetween(lat2set,longt2set,latDouble1,longDouble1,dist);
//                if (dist.length>1){
//                    float distright=dist[0]/1000;
//                    String distStr=String.valueOf(distright);
//                    productViewHolder.textProductInfo.setText(distStr+" kms");
//                }
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detintent=new Intent(MainActivity.this,RestaurantDetails.class);
                        detintent.putExtra("pid",products.getPid());
                        detintent.putExtra("location",location2.getText().toString());
                        detintent.putExtra("distance",distStr);
                        startActivity(detintent);

                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.products_item, parent, false);
                ProductViewHolder holder = new ProductViewHolder(view);
                return holder;
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_home, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOGIN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            GoogleSignInAccount account = googleSignInResult.getSignInAccount();

            try {
                Intent sendData = new Intent(MainActivity.this, UserDetails.class);
                String name, email, dpUrl = "";
                name = account.getDisplayName();
                email = account.getEmail();

                dpUrl = account.getPhotoUrl().toString();
                sendData.putExtra("p_name", name);
                sendData.putExtra("p_email", email);
                sendData.putExtra("p_url", dpUrl);

                startActivity(sendData);

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
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

    public void mswiggy(View view) {

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

    public void searchbaractivity(View view) {
        Intent mainIntent = new Intent(this, searchbar.class);
        mainIntent.putExtra("location",location2.getText().toString());
        startActivity(mainIntent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private class GeoHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String address;
            switch (msg.what){
                case 1:
                    Bundle bundle = msg.getData();
                    address=bundle.getString("address");
                    break;
                default:
                    address=null;
            }
            if(address!= null){
                arrOfStrStart=address.split("\n");
                String lat,longt;
                lat1=arrOfStrStart[0];
                long1=arrOfStrStart[1];
            }
            else{
                Toast.makeText(MainActivity.this, "Google Api is not able to read the location , so we are shutting down the app. sorry for the inconvienience.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class GeoHandler2 extends Handler {
        @Override
        public void handleMessage(Message msg) {
            String address;
            switch (msg.what){
                case 1:
                    Bundle bundle = msg.getData();
                    address=bundle.getString("address");
                    break;
                default:
                    address=null;
            }
            arrOfStrStart=address.split("\n");
            String lat,longt;
            lat2=arrOfStrStart[0];
            long2=arrOfStrStart[1];
        }
    }
    private double distanceCalci(double lat1,double lat2,double longt1,double longt2){
        float[] results = new float[1];
        Location.distanceBetween(lat1, longt1,
                lat2, longt2, results);
        float distance = results[0];
        double dist=Double.valueOf(distance);
        return dist;
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}

