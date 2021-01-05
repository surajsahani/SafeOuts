package martialcoder.surajsahani.safeouts;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UploadtActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseFirestore;
    private String CategoryName,Description,Price,PName,saveCurrentDate,saveCurrentTime;
    private EditText contact,distance,realtime,nop,nopPresent,nopBefore,totalSeats;
    private EditText emp1name,emp1desig,emp1number,emp2name,emp2desig,emp2number,emp3name,emp3desig,emp3number,emp4name,emp4desig,emp4number,emp5name,emp5desig,emp5number,
            empMask,sanitFreq,Distance_NOPlive;
    public String contactS,totalSeatsS,distanceS,realtimeS,nopS,nopPresentS,nopBeforeS,emp1nameS,emp1desigS,emp1numberS,emp2nameS,emp2desigS,emp2numberS,emp3nameS,emp3desigS,emp3numberS,emp4nameS,emp4desigS,emp4numberS,emp5nameS,emp5desigS,emp5numberS,
            empMaskS,sanitFreqS;
    private Button AddNewProduct,Home;
    private ImageView InputProductImage;
    private EditText InputProductName,InputProductDesc,InputProductPrice;
    private static final int gallerypic=1;
    private Uri ImageUri;
    private String productRandomKey,downloadImageUrl;
    private StorageReference ProductImageRefs;
    private DatabaseReference ProductsRefs,sellersRefs;
    private ProgressDialog mProProgress;
    private String distance_noplive;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_uploadt);
        Toast.makeText(this, "database ref "+firebaseFirestore, Toast.LENGTH_SHORT).show();
        contact=findViewById(R.id.upload_contact);
        distance=findViewById(R.id.upload_distance);
        totalSeats=findViewById(R.id.upload_total_seats);
        realtime=findViewById(R.id.upload_contact_tracing);
        nop=findViewById(R.id.upload_no_ofPeople);
        nopPresent=findViewById(R.id.upload_no_ofPeople_sit_now);
        nopBefore=findViewById(R.id.upload_people_used_to_sit);
        emp1name=findViewById(R.id.upload_emp1_name);
        emp1desig=findViewById(R.id.upload_emp1_design);
        emp1number=findViewById(R.id.upload_emp1_number);
        emp2name=findViewById(R.id.upload_emp2_nam);
        emp2desig=findViewById(R.id.upload_emp2_designation);
        emp2number=findViewById(R.id.upload_emp2_number);
        emp3name=findViewById(R.id.upload_emp3_name);
        emp3desig=findViewById(R.id.upload_emp3_desination);
        emp3number=findViewById(R.id.upload_emp3_number);
        emp4name=findViewById(R.id.upload_emp4_name);
        emp4desig=findViewById(R.id.upload_emp4_designation);
        emp4number=findViewById(R.id.upload_emp4_number);
        emp5name=findViewById(R.id.upload_emp5_name);
        emp5desig=findViewById(R.id.upload_emp5_designation);
        emp5number=findViewById(R.id.upload_emp5_number);
        empMask=findViewById(R.id.upload_emp_no_wearing_mask);
        sanitFreq=findViewById(R.id.upload_sanitization_freq);
        Home=findViewById(R.id.home_btn);
        Distance_NOPlive=findViewById(R.id.upload_distance_NopLive_edt);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UploadtActivity.this, MainActivity.class));
            }
        });
        mProProgress=new ProgressDialog(this);
        ProductImageRefs= FirebaseStorage.getInstance().getReference().child("Restaurants Images");
        ProductsRefs= FirebaseDatabase.getInstance().getReference().child("Restaurants");
        sellersRefs=FirebaseDatabase.getInstance().getReference().child("Sellers");
        Toast.makeText(this, CategoryName, Toast.LENGTH_SHORT).show();
        AddNewProduct=(Button)findViewById(R.id.add_new_product_btn);
        InputProductDesc=(EditText)findViewById(R.id.select_product_desc);
        InputProductName=(EditText)findViewById(R.id.select_product_name);
        InputProductPrice=(EditText)findViewById(R.id.select_product_price);
        InputProductImage=(ImageView) findViewById(R.id.select_product_img);
        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });
        AddNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateProductData();
            }
        });

    }

    private void ValidateProductData() {
        Description=InputProductDesc.getText().toString();
        Price=InputProductPrice.getText().toString();
        PName=InputProductName.getText().toString();
        contactS=contact.getText().toString();
        distanceS=distance.getText().toString();
        totalSeatsS=totalSeats.getText().toString();
        nopS=nop.getText().toString();
        nopBeforeS=nopBefore.getText().toString();
        nopPresentS=nopPresent.getText().toString();
        realtimeS=realtime.getText().toString();
        emp1nameS=emp1name.getText().toString();
        emp1desigS=emp1desig.getText().toString();
        emp1numberS=emp1number.getText().toString();
        emp2nameS=emp2name.getText().toString();
        emp2desigS=emp2desig.getText().toString();
        emp2numberS=emp2number.getText().toString();
        emp3nameS=emp3name.getText().toString();
        emp3desigS=emp3desig.getText().toString();
        emp3numberS=emp1number.getText().toString();
        emp4nameS=emp4name.getText().toString();
        emp4desigS=emp4desig.getText().toString();
        emp4numberS=emp4number.getText().toString();
        emp5nameS=emp5name.getText().toString();
        emp5desigS=emp5desig.getText().toString();
        emp5numberS=emp5number.getText().toString();
        sanitFreqS=sanitFreq.getText().toString();
        empMaskS=empMask.getText().toString();
        distance_noplive=Distance_NOPlive.getText().toString();
        if (ImageUri==null)
        {
            Toast.makeText(this, "Product Image is mandatory", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Description))
        {
            Toast.makeText(this, "Please , Write the description", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(Price))
        {
            Toast.makeText(this, "Please , Write the address", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(PName))
        {
            Toast.makeText(this, "Please , Write the restaurant name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(contactS))
        {
            Toast.makeText(this, "Please , Write the Contact no", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(distanceS))
        {
            Toast.makeText(this, "Please , Write the distance", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(nopS))
        {
            Toast.makeText(this, "Please , Write the nopS", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(totalSeatsS))
        {
            Toast.makeText(this, "Please , Write the total seats", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(nopBeforeS))
        {
            Toast.makeText(this, "Please , Write the nopBeforeS", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(nopPresentS))
        {
            Toast.makeText(this, "Please , Write the nopPresent", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(realtimeS))
        {
            Toast.makeText(this, "Please , Write the if contact tracing available or not?", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp1nameS))
        {
            Toast.makeText(this, "Please , Write the emp1 name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp1desigS))
        {
            Toast.makeText(this, "Please , Write the emp1 designation", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp1numberS))
        {
            Toast.makeText(this, "Please , Write the emp1 number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp2nameS))
        {
            Toast.makeText(this, "Please , Write the emp2 name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp2desigS))
        {
            Toast.makeText(this, "Please , Write the emp2 designation", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp2numberS))
        {
            Toast.makeText(this, "Please , Write the emp2 number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp3nameS))
        {
            Toast.makeText(this, "Please , Write the emp3 name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp3desigS))
        {
            Toast.makeText(this, "Please , Write the emp3 designation", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp3numberS))
        {
            Toast.makeText(this, "Please , Write the emp3 number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp4nameS))
        {
            Toast.makeText(this, "Please , Write the emp4 name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp4desigS))
        {
            Toast.makeText(this, "Please , Write the emp4 designation", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp4numberS))
        {
            Toast.makeText(this, "Please , Write the emp4 number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp5nameS))
        {
            Toast.makeText(this, "Please , Write the emp5 name", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp5desigS))
        {
            Toast.makeText(this, "Please , Write the emp5 designation", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(emp5numberS))
        {
            Toast.makeText(this, "Please , Write the emp5 number", Toast.LENGTH_SHORT).show();
        }
        else if (TextUtils.isEmpty(distance_noplive))
        {
            Toast.makeText(this, "Please , Write the distance and current no of people relation also ", Toast.LENGTH_SHORT).show();
        }
        else {
            StoreProductInfo();
        }
    }


    private void StoreProductInfo() {
        mProProgress.setTitle("Adding New Product");
        mProProgress.setMessage("Please wait while we are adding new product !");
        mProProgress.setCanceledOnTouchOutside(false);
        mProProgress.show();
        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate=currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime=currentTime.format(calendar.getTime());
        productRandomKey=saveCurrentDate+saveCurrentTime;
        final StorageReference filepath=ProductImageRefs.child(ImageUri.getLastPathSegment()+ productRandomKey + ".jpg" );
        final UploadTask uploadTask=filepath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message= e.toString();
                Toast.makeText(UploadtActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                mProProgress.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(UploadtActivity.this, "Product Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                Task<Uri> uriTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();

                        }
                        downloadImageUrl=filepath.getDownloadUrl().toString();
                        return filepath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                        {
                            downloadImageUrl=task.getResult().toString();
                            Toast.makeText(UploadtActivity.this, "got Product Image Saved to Database Successfully", Toast.LENGTH_SHORT).show();
                            storeData();
                        }
                    }
                });
            }
        });
    }

    private void storeData( ) {

        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageUrl);
        productMap.put("Category",CategoryName);
        productMap.put("Address",Price);
        productMap.put("PName",PName);
        productMap.put("contact",contactS);
        productMap.put("distance",distanceS);
        productMap.put("ContactTracing",realtimeS);
        productMap.put("nopive",nopS);
        productMap.put("NOPBefore",nopBeforeS);
        productMap.put("NOPPresent",nopPresentS);
        productMap.put("NOTotal",totalSeatsS);
        productMap.put("emp1Name",emp1nameS);
        productMap.put("emp1design",emp1desigS);
        productMap.put("emp1number",emp1numberS);
        productMap.put("emp2Name",emp2nameS);
        productMap.put("emp2design",emp2desigS);
        productMap.put("emp2number",emp2numberS);
        productMap.put("emp3Name",emp3nameS);
        productMap.put("emp3design",emp3desigS);
        productMap.put("emp3number",emp3numberS);
        productMap.put("emp4Name",emp4nameS);
        productMap.put("emp4design",emp4desigS);
        productMap.put("emp4number",emp4numberS);
        productMap.put("emp5Name",emp5nameS);
        productMap.put("emp5design",emp5desigS);
        productMap.put("emp5number",emp5numberS);
        productMap.put("MaskEmp",empMaskS);
        productMap.put("SanitFreqs",sanitFreqS);
        productMap.put("distance_noplive",distance_noplive);
        firebaseFirestore.collection("Restaurants").document(productRandomKey).set(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mProProgress.dismiss();
                    Toast.makeText(UploadtActivity.this, "User Data is Stored Successfully", Toast.LENGTH_LONG).show();
                    Intent mainIntent = new Intent(UploadtActivity.this, MainActivity.class);
                    startActivity(mainIntent);
                    finish();

                } else {

                    String error = task.getException().getMessage();
                    Toast.makeText(UploadtActivity.this, "(FIRESTORE Error) : " + error, Toast.LENGTH_LONG).show();

                }
                mProProgress.dismiss();

            }
        });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String,Object> productMap=new HashMap<>();
        productMap.put("pid",productRandomKey);
        productMap.put("date",saveCurrentDate);
        productMap.put("time",saveCurrentTime);
        productMap.put("description",Description);
        productMap.put("image",downloadImageUrl);
        productMap.put("Category",CategoryName);
        productMap.put("Address",Price);
        productMap.put("PName",PName);
        productMap.put("contact",contactS);
        productMap.put("distance",distanceS);
        productMap.put("ContactTracing",realtimeS);
        productMap.put("nopive",nopS);
        productMap.put("NOPBefore",nopBeforeS);
        productMap.put("NOPPresent",nopPresentS);
        productMap.put("NOTotal",totalSeatsS);
        productMap.put("emp1Name",emp1nameS);
        productMap.put("emp1design",emp1desigS);
        productMap.put("emp1number",emp1numberS);
        productMap.put("emp2Name",emp2nameS);
        productMap.put("emp2design",emp2desigS);
        productMap.put("emp2number",emp2numberS);
        productMap.put("emp3Name",emp3nameS);
        productMap.put("emp3design",emp3desigS);
        productMap.put("emp3number",emp3numberS);
        productMap.put("emp4Name",emp4nameS);
        productMap.put("emp4design",emp4desigS);
        productMap.put("emp4number",emp4numberS);
        productMap.put("emp5Name",emp5nameS);
        productMap.put("emp5design",emp5desigS);
        productMap.put("emp5number",emp5numberS);
        productMap.put("MaskEmp",empMaskS);
        productMap.put("SanitFreqs",sanitFreqS);
        productMap.put("distance_noplive",distance_noplive);
        ProductsRefs.child(productRandomKey).updateChildren(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    startActivity(new Intent(UploadtActivity.this, UploadtActivity.class));
                    mProProgress.dismiss();
                    Toast.makeText(UploadtActivity.this, "Product is added successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mProProgress.dismiss();
                    String message=task.getException().toString();
                    Toast.makeText(UploadtActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void openGallery() {
        Intent galleryintent=new Intent();
        galleryintent.setAction(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,gallerypic);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==gallerypic && resultCode==RESULT_OK && data!=null)
        {
            ImageUri=data.getData();
            InputProductImage.setImageURI(ImageUri);
        }
    }

}