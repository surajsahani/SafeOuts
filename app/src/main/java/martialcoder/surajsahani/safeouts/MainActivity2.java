package martialcoder.surajsahani.safeouts;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

import martialcoder.surajsahani.safeouts.utils.AppRater;

public class MainActivity2 extends AppCompatActivity {
    FirebaseFirestore firebaseFirestore;
    private String mPhotoUrl;
    private String mUsername;
    RecyclerView recyclerView;
    private CollectionReference TutorRefs;
    private ImageView logo;
    RecyclerView.LayoutManager layoutManager;
    TextView location2,Dlocation,Dcurr,DchangeLoc;
    Dialog dialogLoc;
    int flag=0;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private static final String TAG = "MainActivity";
    private SharedPreferences mSharedPreferences;
    private GoogleSignInClient mSignInClient;
    private double lat1=0,lat2=0,long1=0,long2=0;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private int changeLoc=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        firebaseFirestore = FirebaseFirestore.getInstance();
        Places.initialize(getApplicationContext(),"AIzaSyCwi2Lr_YFKLSb0W_Y2GJwXc9hq90wUuZo");
        List<Place.Field> fields= Arrays.asList(Place.Field.ADDRESS,
                Place.Field.LAT_LNG);

//        logo = findViewById(R.id.home_logo);
//        logo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(MainActivity2.this, UserDetails.class));
//            }
//        });

        recyclerView = findViewById(R.id.offers_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        TutorRefs = firebaseFirestore.collection("Restaurants");
        location2 = findViewById(R.id.location_txt);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        dialogLoc=new Dialog(MainActivity2.this);
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
                Intent intent=new Intent(MainActivity2.this, ChangeLocation.class);
                intent.putExtra("location",location2.getText().toString());
                startActivity(intent);
                Toast.makeText(MainActivity2.this, "var "+changeLoc, Toast.LENGTH_SHORT).show();
            }
        });
        Intent intent=getIntent();
        String location =intent.getStringExtra("location");
        location2.setText(location);
        Dlocation.setText(location);
        new GetCoordinates().execute(location2.getText().toString().replace(" ","+"));
        Toast.makeText(MainActivity2.this, "new Location is "+location, Toast.LENGTH_SHORT).show();

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
        com.google.firebase.firestore.Query query=TutorRefs.orderBy("PName", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Products> options=new FirestoreRecyclerOptions.Builder<Products>()
                .setQuery(query, Products.class)
                .build();
        FirestoreRecyclerAdapter<Products, ProductViewHolder> adapter = new FirestoreRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                productViewHolder.textproductName.setText(products.getPName());
                productViewHolder.textProductInfo.setText(products.getDistance()+" kms");
                productViewHolder.txtProductPrice.setText(products.getNopive()+" peoples");
                Picasso.get().load(products.getImage()).into(productViewHolder.ProductImage);
                Picasso.get().load(products.getImage()).into(productViewHolder.ProductImagelogo);
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detintent=new Intent(MainActivity2.this, RestaurantDetails.class);
                        detintent.putExtra("pid",products.getPid());
                        detintent.putExtra("location",location2.getText().toString());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_home, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (resultCode == RESULT_OK) {
            if (data != null) {
                final Uri uri = data.getData();
                Log.d(TAG, "Uri: " + uri.toString());
            }
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

    public void changephone(View view) {
        FirebaseAuth.getInstance().signOut();
    }

    public boolean rating(View view) {
        AppRater app = new AppRater();
        app.rateNow(MainActivity2.this);

        return true;
    }

//    public boolean mlogout(View view) {
////        mFirebaseAuth.signOut();
//        Intent intent = new Intent(this, PhoneAuthActivity.class);
//        startActivity(intent);
//        return true;
//    }

    private class GetCoordinates extends AsyncTask<String,Void,String> {
        ProgressDialog dialog = new ProgressDialog(MainActivity2.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... strings) {
            String response;
            try{
                String address = strings[0];
               HttpDataHandler http = new HttpDataHandler();
                String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?address=%s",address);
                response = http.getHTTPData(url);
                return response;
            }
            catch (Exception ex)
            {

            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                JSONObject jsonObject = new JSONObject(s);

                String lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lat").toString();
                String lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0).getJSONObject("geometry")
                        .getJSONObject("location").get("lng").toString();
                Toast.makeText(MainActivity2.this, "Lat is "+lat+" Long is "+ lng, Toast.LENGTH_SHORT).show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

