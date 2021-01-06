package martialcoder.surajsahani.safeouts;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import martialcoder.surajsahani.safeouts.zomatodata.Constants;
import martialcoder.surajsahani.safeouts.zomatodata.EndlessRecyclerOnScrollListener;
import martialcoder.surajsahani.safeouts.zomatodata.GothamTextView;
import martialcoder.surajsahani.safeouts.zomatodata.RestaurantAdapter;
import martialcoder.surajsahani.safeouts.zomatodata.RestaurantModel;

public class searchbar extends AppCompatActivity implements SearchView.OnQueryTextListener {
    FirebaseFirestore firebaseFirestore;
    private ImageView searchImg;
    private EditText searchEdt;
    private RecyclerView searchRv;
    private RecyclerView.LayoutManager layoutManager;
    private String searchInput,locationintent="";
    TextView location23,fht,tkms,Mtkms,fp,ftp,Mtp,proceed;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private boolean clicked1=false,clicked2=false;
    private CollectionReference TutorRefs;
    SearchView searchView;
    ArrayList<RestaurantModel> list;
    ProgressDialog dialog;
    Dialog dialogPay;
    RestaurantAdapter adapter;
    RecyclerView rv;
    int total_count=0;
    int fhts=0,tkmss=0,mtkmss=0,fps=0,mtps=0,ftps=0;
    private boolean fhtsClick=false,tkmssClick=false,mtkmssClick=false,fpsClick=false,mtpsClick=false,ftpsClick=false;
    String qu;
    GothamTextView tvIndicator;
    private DatabaseReference searchref;
    String lat2,longt2;
    double longitude,latitude;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationintent=getIntent().getStringExtra("location");
        setContentView(R.layout.activity_searchbar);
        firebaseFirestore = FirebaseFirestore.getInstance();
        TutorRefs = firebaseFirestore.collection("Restaurants");
        fht=findViewById(R.id.fht);
        tkms=findViewById(R.id.tkms);
        Mtkms=findViewById(R.id.Mtkms);
        fp=findViewById(R.id.fp);
        ftp=findViewById(R.id.ftp);
        Mtp=findViewById(R.id.Mtp);
        dialogPay=new Dialog(searchbar.this);
        dialogPay.setContentView(R.layout.zomato_dialog);
        proceed=dialogPay.findViewById(R.id.zomato_know_more);
        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            dialogPay.getWindow().setBackgroundDrawable(getDrawable(R.drawable.zomato_dialog_bg));
        }
        location23=findViewById(R.id.location_txt_detail);
        location23.setText(locationintent);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        searchImg=(ImageView)findViewById(R.id.search_img);
        searchEdt=(EditText)findViewById(R.id.search_edt_txt);
        searchRv=(RecyclerView)findViewById(R.id.search_rv);
        searchRv.setLayoutManager(new LinearLayoutManager(searchbar.this));
        searchImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchInput=searchEdt.getText().toString();
                final DatabaseReference searchref = FirebaseDatabase.getInstance().getReference().child("Restaurants");
                final Query queryS=TutorRefs.orderBy("PName").startAt(searchInput).limit(1);
                queryS.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        
                        if (!queryDocumentSnapshots.isEmpty()){
                            FirestoreRecyclerOptions<Products> options=new FirestoreRecyclerOptions.Builder<Products>()
                                    .setQuery(queryS, Products.class)
                                    .build();
                            FirestoreRecyclerAdapter<Products, ProductViewHolder> adapter = new FirestoreRecyclerAdapter<Products, ProductViewHolder>(options) {
                                @Override
                                protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                                    productViewHolder.textproductName.setText(products.getPName());
                                    productViewHolder.txtProductPrice.setText(products.getNopive()+" peoples");
                                    lat2=products.getLatitude();
                                    longt2=products.getLongtitude();
                                    double lat2set=Double.valueOf(lat2);
                                    double longt2set=Double.valueOf(longt2);
                                    Geocoder coder = new Geocoder(searchbar.this);
                                    try {
                                        ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(location23.getText().toString(), 50);
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
                                    Picasso.get().load(products.getImage()).into(productViewHolder.ProductImage);
                                    Picasso.get().load(products.getImage()).into(productViewHolder.ProductImagelogo);
                                    productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            Intent detintent=new Intent(searchbar.this, RestaurantDetails.class);
                                            detintent.putExtra("pid",products.getPid());
                                            detintent.putExtra("location",location23.getText().toString());
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
                            searchRv.setAdapter(adapter);
                            adapter.startListening();
                        }
                        else
                        {
                            Toast.makeText(searchbar.this, "Data doesnt exist", Toast.LENGTH_SHORT).show();
                            searchRv.setAlpha(0);
                            dialogPay.show();
                        }
                    }
                });
            }
        });
//        tvIndicator=(GothamTextView)findViewById(R.id.tv_indicator);
        dialog=new ProgressDialog(searchbar.this);
        dialog.setTitle("Loading");
        dialog.setMessage("Fetching data from server");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        list=new ArrayList<>();
        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        fht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fht.setBackground(getResources().getDrawable(R.drawable.text_box2));
                clicked1=true;
                fhtsClick=true;
                tkmssClick=false;
                mtkmssClick=false;
                tkms.setBackground(getResources().getDrawable(R.drawable.text_box));
                Mtkms.setBackground(getResources().getDrawable(R.drawable.text_box));
                if (fpsClick)
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance_nopive").endAt("0.5_4");
                    filterData(query);
                }
                else if (ftpsClick)
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance_nopive").startAt("0_4").endAt("0.5_10");
                    filterData(query);
                }
                else if (mtpsClick)
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance_nopive").startAt("0_10.1").endAt("0.5_100");
                    filterData(query);
                }
                else
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance").endAt("0.5");
                    filterData(query);
                }
            }
        });
        tkms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tkms.setBackground(getResources().getDrawable(R.drawable.text_box2));
                tkmssClick=true;
                fhtsClick=false;
                mtkmssClick=false;
                Mtkms.setBackground(getResources().getDrawable(R.drawable.text_box));
                fht.setBackground(getResources().getDrawable(R.drawable.text_box));
                if (fpsClick)
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance_nopive").startAt("0.5_0").endAt("2_4");
                    filterData(query);
                }
                else if (ftpsClick)
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance_nopive").startAt("0.5_4").endAt("2_10");
                    filterData(query);
                }
                else if (mtpsClick)
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance_nopive").startAt("0.5_10.1").endAt("2_100");
                    filterData(query);
                }
                else
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance").startAt("0.5").endAt("2");
                    filterData(query);
                }
            }
        });
        Mtkms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Mtkms.setBackground(getResources().getDrawable(R.drawable.text_box2));
                mtkmssClick=true;
                fhtsClick=false;
                tkmssClick=false;
                tkms.setBackground(getResources().getDrawable(R.drawable.text_box));
                fht.setBackground(getResources().getDrawable(R.drawable.text_box));
                if (fpsClick)
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance_nopive").startAt("2.1_0").endAt("50_4");
                    filterData(query);
                }
                else if (ftpsClick)
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance_nopive").startAt("2.1_4").endAt("50_10");
                    filterData(query);
                }
                else if (mtpsClick)
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance_nopive").startAt("2.1_10.1");
                    filterData(query);
                }
                else
                {
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("distance").startAt("2");
                    filterData(query);
                }
            }
        });
        fp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fp.setBackground(getResources().getDrawable(R.drawable.text_box2));
                ftp.setBackground(getResources().getDrawable(R.drawable.text_box));
                Mtp.setBackground(getResources().getDrawable(R.drawable.text_box));
                fpsClick=true;
                ftpsClick=false;
                mtpsClick=false;
                Toast.makeText(searchbar.this, "Fp clicked", Toast.LENGTH_SHORT).show();
                    final Query query=TutorRefs.orderBy("nopive", Query.Direction.ASCENDING).limit(1);
                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            filterData(query);
                        }
                    });
            }
        });
        ftp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ftp.setBackground(getResources().getDrawable(R.drawable.text_box2));
                fp.setBackground(getResources().getDrawable(R.drawable.text_box));
                Mtp.setBackground(getResources().getDrawable(R.drawable.text_box));
                fpsClick=false;
                ftpsClick=true;
                mtpsClick=false;
                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("nopive").startAt("4").endAt("10");
                    filterData(query);

            }
        });
        Mtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fp.setBackground(getResources().getDrawable(R.drawable.text_box));
                ftp.setBackground(getResources().getDrawable(R.drawable.text_box));
                Mtp.setBackground(getResources().getDrawable(R.drawable.text_box2));
                fpsClick=false;
                ftpsClick=false;
                mtpsClick=true;

                    final com.google.firebase.firestore.Query query=TutorRefs.orderBy("nopive").whereGreaterThanOrEqualTo("nopive",10);
                    filterData(query);
            }
        });



//        rv=(RecyclerView) findViewById(R.id.rv_restaurants);
//        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
//        rv.setLayoutManager(layoutManager);
//        rv.setAdapter(adapter);
//        rv.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
//            @Override
//            public void onLoadMore(int current_page) {
//                if (total_count<=current_page) // If all the restaurants has been added to the list, do nothing
//                {
//
//                }
//                else
//                {
//                    new LoadMoreTask().execute(qu,current_page+"");
//                }
//            }
//        });
//        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        toolbar.setTitleTextColor(Color.parseColor("#dcd9cd"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        com.google.firebase.firestore.Query query=TutorRefs.orderBy("PName", com.google.firebase.firestore.Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Products> options=new FirestoreRecyclerOptions.Builder<Products>()
                .setQuery(query, Products.class)
                .build();
        FirestoreRecyclerAdapter<Products, ProductViewHolder> adapter = new FirestoreRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                productViewHolder.textproductName.setText(products.getPName());
                productViewHolder.txtProductPrice.setText(products.getNopive()+" peoples");
                Picasso.get().load(products.getImage()).into(productViewHolder.ProductImage);
                lat2=products.getLatitude();
                longt2=products.getLongtitude();
//                double lat2set=Double.valueOf(lat2);
//                double longt2set=Double.valueOf(longt2);
                Geocoder coder = new Geocoder(searchbar.this);
                try {
                    ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(location23.getText().toString(), 50);
                    for(Address add : adresses){
                        longitude = add.getLongitude();
                        latitude = add.getLatitude();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Location StartLoc=new Location("");
//                StartLoc.setLatitude(lat2set);
//                StartLoc.setLongitude(longt2set);
                Location EndLoc=new Location("");
                EndLoc.setLatitude(latitude);
                EndLoc.setLongitude(longitude);
                double dist=StartLoc.distanceTo(EndLoc);
                double distright=dist/1000;
                double roundedDouble=Math.round(distright*100)/100.0;
                String distStr=String.valueOf(roundedDouble);
                productViewHolder.textProductInfo.setText(distStr+" kms");
                Picasso.get().load(products.getImage()).into(productViewHolder.ProductImagelogo);
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detintent=new Intent(searchbar.this, RestaurantDetails.class);
                        detintent.putExtra("pid",products.getPid());
                        detintent.putExtra("location",location23.getText().toString());
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
        searchRv.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
//        menuInflater.inflate(R.menu.menu_home,menu);
        searchView= (SearchView) MenuItemCompat.getActionView(menu.getItem(0));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        new FetchTask().execute(query);
        qu=query;
        searchView.setIconified(true);
        invalidateOptionsMenu();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
    public void processResponse(JSONObject obj) throws JSONException {
        JSONArray jsonArray=obj.getJSONArray("restaurants");
        total_count=obj.getInt("results_found");
        Log.d("Total",total_count+"");
        if (jsonArray.length()!=0)
        {
            tvIndicator.setVisibility(View.GONE);
        }
        else
        {
            tvIndicator.setVisibility(View.VISIBLE);
        }
        for (int i=0;i<jsonArray.length();i++)
        {
            JSONObject object=jsonArray.getJSONObject(i).getJSONObject("restaurant");
            RestaurantModel model=new RestaurantModel();
            model.setName(object.getString("name"));
            JSONObject lobj=object.getJSONObject("location");
            model.setAdd(lobj.getString("address"));
            model.setLocation(lobj.getString("city"));
            Log.d("CITY",lobj.getString("city"));
            JSONObject userObj=object.getJSONObject("user_rating");
            model.setRating(userObj.getDouble("aggregate_rating"));
            model.setCuisine(object.getString("cuisines"));
            list.add(model);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public void userdetailsactivitya(View view) {
        Intent intent = new Intent(this, UserDetails.class);
        startActivity(intent);
    }

    public void searchzomato(View view) {
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

    public void searchswiggy(View view) {
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

    class FetchTask extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String s;
            HttpURLConnection urlConnection= null;
            BufferedReader bufferedReader=null;
            StringBuilder builder=new StringBuilder();

            try {

                Uri uri=Uri.parse(Constants.BASE_URL).buildUpon().appendQueryParameter("q",strings[0]).build();
                URL url=new URL(uri.toString());
                Log.d("site",uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("user-key", Constants.API_KEY);

                InputStream in=urlConnection.getInputStream();
                bufferedReader =new BufferedReader(new InputStreamReader(in));
                while ((s=bufferedReader.readLine())!=null)
                {
                    builder.append(s);
                }}
            catch (IOException e)
            {
                e.printStackTrace();
            }

            finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            try {
                list.clear();
                processResponse(new JSONObject(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    class LoadMoreTask extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String s;
            HttpURLConnection urlConnection= null;
            BufferedReader bufferedReader=null;
            StringBuilder builder=new StringBuilder();

            try {

                Uri uri=Uri.parse(Constants.BASE_URL).buildUpon().appendQueryParameter("q",strings[0]).appendQueryParameter("start",strings[1]).build();
                URL url=new URL(uri.toString());
                Log.d("site",uri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(10000);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("user-key",Constants.API_KEY);

                InputStream in=urlConnection.getInputStream();
                bufferedReader =new BufferedReader(new InputStreamReader(in));
                while ((s=bufferedReader.readLine())!=null)
                {
                    builder.append(s);
                }}
            catch (IOException e)
            {
                e.printStackTrace();
            }

            finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return builder.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            try {
                processResponse(new JSONObject(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    void filterData(com.google.firebase.firestore.Query query)
    {

        FirestoreRecyclerOptions<Products> options=new FirestoreRecyclerOptions.Builder<Products>()
                .setQuery(query, Products.class)
                .build();
        FirestoreRecyclerAdapter<Products, ProductViewHolder> adapter = new FirestoreRecyclerAdapter<Products, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder productViewHolder, int i, @NonNull final Products products) {
                productViewHolder.textproductName.setText(products.getPName());
                productViewHolder.txtProductPrice.setText(products.getNopive()+" peoples");
                lat2=products.getLatitude();
                longt2=products.getLongtitude();
//                double lat2set=Double.valueOf(lat2);
//                double longt2set=Double.valueOf(longt2);
                Geocoder coder = new Geocoder(searchbar.this);
                try {
                    ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(location23.getText().toString(), 50);
                    for(Address add : adresses){
                        longitude = add.getLongitude();
                        latitude = add.getLatitude();

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Location StartLoc=new Location("");
//                StartLoc.setLatitude(lat2set);
//                StartLoc.setLongitude(longt2set);
                Location EndLoc=new Location("");
                EndLoc.setLatitude(latitude);
                EndLoc.setLongitude(longitude);
                double dist=StartLoc.distanceTo(EndLoc);
                double distright=dist/1000;
                double roundedDouble=Math.round(distright*100)/100.0;
                String distStr=String.valueOf(roundedDouble);
                productViewHolder.textProductInfo.setText(distStr+" kms");
                Picasso.get().load(products.getImage()).into(productViewHolder.ProductImage);
                Picasso.get().load(products.getImage()).into(productViewHolder.ProductImagelogo);
                productViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent detintent=new Intent(searchbar.this, RestaurantDetails.class);
                        detintent.putExtra("pid",products.getPid());
                        detintent.putExtra("location",location23.getText().toString());
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
        searchRv.setAdapter(adapter);
        adapter.startListening();
    }

    public void zomatoS(View view) {

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

    public void mswiggyS(View view) {

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

}
