package com.shamgar.peoplefeedbackadmin;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shamgar.peoplefeedbackadmin.models.Posts;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AddPostActivity extends AppCompatActivity {

    private int GALLERY = 1, CAMERA = 2;
    private boolean permissionCheck=false;
    private Bitmap bmp;

    private ImageView uploadedImage;
    private EditText imageDescription;
    private Button uploadButton,adminPostSubmitButton;
    private Spinner spinnerState,spinnerDistrict,spinnerConstituency;
    private ArrayAdapter stateAdapter,districtAdapter,constituencyAdapter;
    private ArrayList<String> state=new ArrayList<>();
    private ArrayList<String> districts=new ArrayList<>();
    private ArrayList<String> constituency=new ArrayList<>();
    private String currentState,currentDistrict,currentConstituency;

    private String imageId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_post);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!permissionCheck){
            requestMultiplePermissions();
        }
        uploadedImage=findViewById(R.id.uploadedImage);
        uploadButton=findViewById(R.id.uploadButton);
        spinnerState=findViewById(R.id.spinnerState);
        spinnerDistrict=findViewById(R.id.spinnerDistrict);
        spinnerConstituency=findViewById(R.id.spinnerConstituency);
        adminPostSubmitButton=findViewById(R.id.adminPostSubmitButton);
        imageDescription=findViewById(R.id.imageDescription);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        getStates();

        adminPostSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentState ==null || currentDistrict==null || currentConstituency == null){
                    Log.e("empty","empty");
                }
                else {
                    if(currentState=="Select state" || currentDistrict=="Select district" || currentConstituency=="Select constituency"){
                        Log.e("note","please check fields");
                        return;
                    }
                    if (bmp!=null){
                        pushImageToFirebase();
                    }
                    else {
                        Log.e("note","please add image");
                        Toast.makeText(getApplicationContext(),"please add image",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    private void postintoSingleConstituency(String postKeyddf) {
        FirebaseDatabase.getInstance().getReference().child("india")
                .child(currentState)
                .child(currentDistrict)
                .child("constituancy")
                .child(currentConstituency)
                .child("PostID")
                .child(postKeyddf)
                .setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(AddPostActivity.this,"posted in single con",Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void pushImageToFirebase() {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        imageId = FirebaseDatabase.getInstance().getReference().push().getKey();
        imageId = "images/"+imageId+".jpg";
        // Create a reference to 'images/mountains.jpg'
        StorageReference mountainImagesRef = storageRef.child(imageId);

        // Get the data from an ImageView as bytes
        uploadedImage.setDrawingCacheEnabled(true);
        uploadedImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) uploadedImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mountainImagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(AddPostActivity.this, "success image posted", Toast.LENGTH_SHORT).show();
//                Log.e("gggg", "sssssssd");

                //get the url for the image
                getUrlForDownload();
            }
        });
    }

    private void getUrlForDownload() {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl("gs://peoplesfeedback-124ba.appspot.com/");
        final StorageReference pathReference = storageRef.child(imageId);

        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                // Toast.makeText(CameraActivity.this, "" + pathReference.getDownloadUrl(), Toast.LENGTH_SHORT).show();
                Log.e("url", "" + uri.toString());

                Date now=new Date();

                Posts posts=new Posts("+919642542514","17.7431926","83.3062349","",uri.toString(),"",imageDescription.getText().toString(),"","","","","","");
                postIntoFirebase(posts);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void postIntoFirebase(Posts posts) {
        final String postKey = FirebaseDatabase.getInstance().getReference().push().getKey();
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postKey).setValue(posts).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AddPostActivity.this,"Success Post",Toast.LENGTH_SHORT).show();

                    if (currentState=="All"){
                        postIntoAllStates(postKey);
                    }

                    if (currentDistrict=="All" && currentState!="All" && currentState!="Select state"){
                        Log.e("note","posted in all districts");
                        postedInAllDistricts(currentState,postKey);
                    }

                    if (currentConstituency=="All" && currentState!="All" && currentState!="Select state" &&
                            currentDistrict!="All" && currentDistrict!="Select district"){
                        Log.e("note","posted in all constituencies");

                        postedInAllConstituencies(currentState,currentDistrict,postKey);
                    }

                    if (currentConstituency!="Select constituency" &&currentConstituency!="All" && currentState!="All" && currentState!="Select state" &&
                            currentDistrict!="All" && currentDistrict!="Select district"){
                        Log.e("note","posted in single constituency");

                        postintoSingleConstituency(postKey);
                    }
                }
            }
        });

    }

    private void postIntoAllStates(final String postKey) {
        Query query= FirebaseDatabase.getInstance().getReference().child("States");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot states:dataSnapshot.getChildren()){
                        Log.e("states",states.getKey());
                        for (DataSnapshot districts: states.child("MLA").child("district").getChildren()){
                            Log.e("districts",districts.getKey());
                            for (DataSnapshot constituencies:districts.child("Constituancy").getChildren()){
                                Log.e("constituencies",constituencies.getKey());
                                FirebaseDatabase.getInstance().getReference().child("india")
                                        .child(states.getKey())
                                        .child(districts.getKey())
                                        .child("constituancy")
                                        .child(constituencies.getKey())
                                        .child("PostID")
                                        .child(postKey)
                                        .setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(AddPostActivity.this,"posted in state con",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);

    }

    private void postedInAllDistricts(final String state, final String key) {
        Query query= FirebaseDatabase.getInstance().getReference().child("States");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot states:dataSnapshot.getChildren()){
                        Log.e("states",states.getKey());
                        if(states.getKey().equalsIgnoreCase(state)){
                            for (DataSnapshot districts: states.child("MLA").child("district").getChildren()){
                                Log.e("districts",districts.getKey());
                                for (DataSnapshot constituencies:districts.child("Constituancy").getChildren()){
                                    Log.e("constituencies",constituencies.getKey());
                                    FirebaseDatabase.getInstance().getReference().child("india")
                                            .child(currentState)
                                            .child(districts.getKey())
                                            .child("constituancy")
                                            .child(constituencies.getKey())
                                            .child("PostID")
                                            .child(key)
                                            .setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(AddPostActivity.this,"posted in all districts con",Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }

                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void postedInAllConstituencies(final String State, final String District, final String postKey) {

        Query query= FirebaseDatabase.getInstance().getReference().child("States");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot states:dataSnapshot.getChildren()){
                        Log.e("states",states.getKey());
                        if(states.getKey().equalsIgnoreCase(State)){
                            for (DataSnapshot districts: states.child("MLA").child("district").getChildren()){
                                Log.e("districts",districts.getKey());
                                if (districts.getKey().equalsIgnoreCase(District)){
                                    for (DataSnapshot constituencies:districts.child("Constituancy").getChildren()){
                                        Log.e("constituencies",constituencies.getKey());
                                        FirebaseDatabase.getInstance().getReference().child("india")
                                                .child(currentState)
                                                .child(currentDistrict)
                                                .child("constituancy")
                                                .child(constituencies.getKey())
                                                .child("PostID")
                                                .child(postKey)
                                                .setValue("1").addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(AddPostActivity.this,"posted in all con",Toast.LENGTH_SHORT).show();

                                            }
                                        });
                                    }
                                }


                            }
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void getDistricts(final String currentState) {
        if (currentState.equalsIgnoreCase("Select state") || currentState.equalsIgnoreCase("All")){
            districts.clear();
            constituency.clear();
        }
        Query query= FirebaseDatabase.getInstance().getReference().child("States")
                .child(currentState).child("MLA").child("district");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    districts.clear();
                    districts.add("Select district");
                    districts.add("All");
                    for (DataSnapshot states:dataSnapshot.getChildren()){
                        Log.e("states",states.getKey());
                        districts.add(states.getKey());
                    }

                    districtAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,districts);
                    districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDistrict.setAdapter(districtAdapter);

                    spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            currentDistrict=parent.getSelectedItem().toString();
                            getConstituency(currentState,currentDistrict);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void getConstituency(String state, String currentDistrict) {
        if (currentDistrict.equalsIgnoreCase("Select district") || currentDistrict.equalsIgnoreCase("All") ){
            constituency.clear();
        }
        Query query= FirebaseDatabase.getInstance().getReference().child("States")
                .child(state).child("MLA").child("district").child(currentDistrict).child("Constituancy");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    constituency.clear();
                    constituency.add("Select constituency");
                    constituency.add("All");
                    for (DataSnapshot states:dataSnapshot.getChildren()){
                        Log.e("states",states.getKey());
                        constituency.add(states.getKey());
                    }
                    constituencyAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,constituency);
                    constituencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerConstituency.setAdapter(constituencyAdapter);

                    spinnerConstituency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            currentConstituency=adapterView.getSelectedItem().toString();
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void getStates(){
        Query query= FirebaseDatabase.getInstance().getReference().child("States");
        ValueEventListener valueEventListener=new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if (dataSnapshot.exists()){
                  state.clear();
                  state.add("Select state");
                  state.add("All");
                  for (DataSnapshot states:dataSnapshot.getChildren()){
                      Log.e("states",states.getKey());
                      state.add(states.getKey());
                  }
                  stateAdapter= new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,state);
                  stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                  spinnerState.setAdapter(stateAdapter);

                  spinnerState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                      @Override
                      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                          currentState=parent.getSelectedItem().toString();
                          getDistricts(currentState);
                      }

                      @Override
                      public void onNothingSelected(AdapterView<?> parent) {

                      }
                  });
              }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        query.addValueEventListener(valueEventListener);
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            permissionCheck=true;
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                            permissionCheck=false;
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    //Upload Image
    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        if (permissionCheck){
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            startActivityForResult(galleryIntent, GALLERY);
        }else {
            requestMultiplePermissions();
        }

    }

    private void takePhotoFromCamera() {
        if (permissionCheck){
            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA);
        }else {
            requestMultiplePermissions();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), contentURI);
                    Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    uploadedImage.setImageBitmap(bmp);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

        } else if (requestCode == CAMERA) {
            bmp = (Bitmap) data.getExtras().get("data");
            uploadedImage.setImageBitmap(bmp);
            Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();

        }
    }
}
