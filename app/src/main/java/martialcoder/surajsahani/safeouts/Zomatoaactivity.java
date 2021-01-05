package martialcoder.surajsahani.safeouts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



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


public class Zomatoaactivity extends AppCompatActivity implements SearchView.OnQueryTextListener, androidx.appcompat.widget.SearchView.OnQueryTextListener {
    androidx.appcompat.widget.SearchView searchView;
    ArrayList<RestaurantModel> list;
    ProgressDialog dialog;
    RestaurantAdapter adapter;
    RecyclerView rv;
    int total_count=0;
    String qu;
    GothamTextView tvIndicator, tvRating;
    LinearLayout layout, layoutrestaurant;
    RelativeLayout layoutnew;
    TextView notpartnerred;
    ImageView imagezomato;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        imagezomato=(ImageView)findViewById(R.id.zomato_imga);
        layoutrestaurant=(LinearLayout)findViewById(R.id.tv_item_rating);
        setContentView(R.layout.activity_zomatoaactivity);
        tvIndicator=(GothamTextView)findViewById(R.id.tv_indicator);
        tvRating=(GothamTextView)findViewById(R.id.tv_item_rating) ;
        dialog=new ProgressDialog(Zomatoaactivity.this);
        dialog.setTitle("Loading");
        dialog.setMessage("Fetching data from server");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        list=new ArrayList<>();


        Toolbar toolbar=(Toolbar) findViewById(R.id.toolbar);
        rv=(RecyclerView) findViewById(R.id.rv_restaurants);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        rv.setOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (total_count<=current_page) // If all the restaurants has been added to the list, do nothing
                {

                }
                else
                {
                    new LoadMoreTask().execute(qu,current_page+"");
                }
            }
        });

//            layoutrestaurant.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = getPackageManager().getLaunchIntentForPackage("com.application.zomato");
//                    if (intent != null) {
//                        // We found the activity now start the activity
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
//                    } else {
//                        // Bring user to the market or let them choose an app?
//                        intent = new Intent(Intent.ACTION_VIEW);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        intent.setData(Uri.parse("market://details?id=" + "com.application.zomato"));
//                        startActivity(intent);
//                    }
//                }
//            });


        setSupportActionBar(toolbar);
//        getSupportActionBar().setLogo(R.drawable.crop_safe_outs);
        toolbar.setTitleTextColor(Color.parseColor("#dcd9cd"));
        toolbar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent i = new Intent(Zomatoaactivity.this, UserDetails.class);
                startActivity(i);

//                startActivity(new Intent(this, UserDetails.class));
//                finish();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
//        menuInflater.inflate(R.menu.menu_home,menu);
        searchView= (androidx.appcompat.widget.SearchView) MenuItemCompat.getActionView(menu.getItem(0));
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
//            case R.id.action_filter:
//                FilterDialog dialog=new FilterDialog(Zomatoaactivity.this);
//                dialog.setDialogResult(new FilterDialog.OnDialogResult() {
//                    @Override
//                    public void finish(String city, String cuisine) {
//                        JSONObject jsonObject=new JSONObject();
//                        try {
//                            jsonObject.put("city",city);
//                            jsonObject.put("cuisine",cuisine);
//                            adapter.getFilter().filter(jsonObject.toString());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//                dialog.show();
//                break;
        }
        return super.onOptionsItemSelected(item);
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void zomatonew(View view) {
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

    public void zomatotwo(View view) {
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
                processResponse(new JSONObject(s));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
