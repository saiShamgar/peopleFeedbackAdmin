package com.shamgar.peoplefeedbackadmin.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shamgar.peoplefeedbackadmin.R;

import java.util.ArrayList;
import java.util.List;

public class SpammedRecyclerAdapter extends RecyclerView.Adapter<SpammedRecyclerAdapter.SpammedViewHolder> {

    private Context context;
    private ArrayList<String> images;
    private ArrayList<String> keys;
    private ArrayList<String> spammedBY;
    private ArrayList<String> users;
    private ArrayList<String> tagsId;

    public SpammedRecyclerAdapter(Context context, ArrayList<String> images, ArrayList<String> keys, ArrayList<String> spammedBY, ArrayList<String> users, ArrayList<String> tagId) {
        this.context=context;
        this.images=images;
        this.keys=keys;
        this.spammedBY=spammedBY;
        this.users=users;
        this.tagsId=tagId;
    }

    @NonNull
    @Override
    public SpammedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.spammed_custom_layout,parent,false);
        return new SpammedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SpammedViewHolder holder, final int position) {

        Glide.with(context).load(images.get(position)).into(holder.spamImage);
        holder.spammedBytxt.setText("Spammed by "+spammedBY.get(position)+" people");
        holder.spammedBytxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                 final ArrayList<String> sp = new ArrayList<>();
                Query numofFol =  FirebaseDatabase.getInstance().getReference().child("Posts")
                        .child(keys.get(position));
                ValueEventListener valueEventListener1 = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            final AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                            builder.setTitle("Spammed by");
                            builder.setCancelable(false);

                            for (DataSnapshot innerChild:dataSnapshot.child("Spam").getChildren()){
                                Log.e("spammed by ",innerChild.getKey());
                                sp.add(innerChild.getKey());
                            }

                            builder.setMessage(sp.toString());
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    builder.setCancelable(true);
                                }
                            });
                            AlertDialog alertDialog= builder.create();
                            alertDialog.show();
                        }
                        else {
                            Toast.makeText(context," no data ",Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                };
                numofFol.addValueEventListener(valueEventListener1);
            }
        });

        holder.deletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder=new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle("Deleting the Post");
                builder.setMessage("Are you sure, you want to delete this post");
                builder.setCancelable(false);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase.getInstance().getReference().child("Posts")
                                .child(keys.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Toast.makeText(context,"post deleted",Toast.LENGTH_SHORT).show();


                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        builder.setCancelable(true);
                    }
                });

               AlertDialog alertDialog= builder.create();
                alertDialog.show();
            }
        });


    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public class SpammedViewHolder extends RecyclerView.ViewHolder{
        private ImageView spamImage;
        private Button deletePost;
        private TextView spammedBytxt;
        public SpammedViewHolder(View itemView) {
            super(itemView);
            spamImage=itemView.findViewById(R.id.spamImage);
            spammedBytxt=itemView.findViewById(R.id.spammedBytxt);
            deletePost=itemView.findViewById(R.id.deletePost);
        }
    }
}
