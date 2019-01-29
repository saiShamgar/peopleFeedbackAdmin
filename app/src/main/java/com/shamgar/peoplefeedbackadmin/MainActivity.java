package com.shamgar.peoplefeedbackadmin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.service.autofill.Dataset;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ForwardingListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shamgar.peoplefeedbackadmin.Adapters.SpammedRecyclerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private RecyclerView spamRecyclerView;
    private SpammedRecyclerAdapter spammedRecyclerAdapter;

    private ArrayList<String> images=new ArrayList<>();
    private ArrayList<String> Users=new ArrayList<>();
    private ArrayList<String> keys=new ArrayList<>();
    private ArrayList<String> spammedBY=new ArrayList<>();
    private ArrayList<String> tagId=new ArrayList<>();


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Getting data");
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        mAuth=FirebaseAuth.getInstance();
        gettingSpammedKeys();
    }

    private void gettingSpammedKeys() {
        //getting num of followers
        Query numofFol =  FirebaseDatabase.getInstance().getReference().child("Posts");
        ValueEventListener valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    progressDialog.dismiss();
                    images.clear();
                    keys.clear();
                    Users.clear();
                    tagId.clear();
                    for (DataSnapshot child:dataSnapshot.getChildren()){
                        if (child.hasChild("Spam")){
                            if (child.child("Spam").getChildrenCount()>0){
                                //Log.e("spam keys",child.getKey());
                                //Log.e("spammed by count", String.valueOf(dataSnapshot.child(child.getKey()).child("Spam").getChildrenCount()));
                                keys.add(child.getKey());
                                spammedBY.add(String.valueOf(dataSnapshot.child(child.getKey()).child("Spam").getChildrenCount()));
                                //Log.e("image",child.child("imageUrl").getValue().toString());
                                images.add(child.child("imageUrl").getValue().toString());
                                tagId.add(child.child("tagId").getValue().toString());
                                Users.add(child.child("user").getValue().toString());
//                                for (DataSnapshot innerChild:child.child("Spam").getChildren()){
//                                   // Log.e("users",innerChild.getKey());
//                                }
                            }
                        }
                    }
                    spamRecyclerView=findViewById(R.id.spamRecyclerView);
                    spammedRecyclerAdapter=new SpammedRecyclerAdapter(getApplicationContext(),images,keys,spammedBY,Users,tagId);
                    spamRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    spamRecyclerView.setHasFixedSize(true);
                    spamRecyclerView.setAdapter(spammedRecyclerAdapter);
                    spammedRecyclerAdapter.notifyDataSetChanged();
                }
                else {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext()," no data ",Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();

                    Toast.makeText(getApplicationContext()," no spammed data ",Toast.LENGTH_SHORT).show();
            }
        };
        numofFol.addValueEventListener(valueEventListener1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.dashboard_menu_items,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         super.onOptionsItemSelected(item);

         if (item.getItemId()==R.id.AddPost){
             Intent addpost=new Intent(MainActivity.this,AddPostActivity.class);
             startActivity(addpost);
         }

        if (item.getItemId()==R.id.signOut){
            mAuth.signOut();
            Intent addpost=new Intent(MainActivity.this,LoginPage.class);
            startActivity(addpost);
            finish();
        }
         return true;
    }
}
