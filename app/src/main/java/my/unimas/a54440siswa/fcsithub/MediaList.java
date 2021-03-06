package my.unimas.a54440siswa.fcsithub;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MediaList extends AppCompatActivity {

    String UserId;
    ImageView IVLogout,IVSearch ;
    ImageView IVback;
    TextView UserName;
    EditText ETSearch;
    Toolbar toolbar;
    CircleImageView CVProfileImage;
    Animation RightToLeft;
    RecyclerView RVMedia;
    DatabaseReference mediaRefer;

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    FirebaseUser user;
    MediaRecyclerViewAdapter mediaAdapter;
    List<Media> lstMedia ;
    DatabaseReference mediaRef;


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medialist);
        mAuth = FirebaseAuth.getInstance();

        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RightToLeft = AnimationUtils.loadAnimation(this,R.anim.rightleft);
        ETSearch = findViewById(R.id.mediaedittext);
        IVback =  findViewById(R.id.IVback);
        IVLogout =  findViewById(R.id.IVLogout);
        CVProfileImage = findViewById(R.id.CVProfile);
        IVSearch =  findViewById(R.id.IVsearch);
        UserName= findViewById(R.id.username);


        IVSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ETSearch.setAnimation(RightToLeft);
                ETSearch.setVisibility(View.VISIBLE);
            }
        });


        RVMedia = findViewById(R.id.recyclerview_media);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MediaList.this, HomeActivity.class));
                }
            }
        };

        IVback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ETSearch.clearFocus();
                ETSearch.setVisibility(View.GONE);
                finish();
                Intent homeactivity = new Intent (MediaList.this, HomeActivity.class);
                startActivity(homeactivity);

            }
        });


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MediaList.this, SignIn.class));
                }
            }
        };

        IVLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
            }
        });

        /* ------------------------------ Firebase Elements --------------------------------------*/
        user = mAuth.getCurrentUser();
        UserId= user.getUid();
        mediaRefer = FirebaseDatabase.getInstance().getReference();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference mediaRef =storageReference.child("profilepic/" +UserId+".jpg");
        /*----------------------------------------------------------------------------------------*/


        GlideApp.with(this /* context */)
                .load(mediaRef)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        CVProfileImage.setVisibility(View.GONE);
                        return false;
                    }
                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        CVProfileImage.setVisibility(View.VISIBLE);
                        return false;
                    }
                })
                .into(CVProfileImage);

        ETSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());

            }
            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });



        lstMedia = new ArrayList<>();

        mediaRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("Users").child(UserId).child("userName").getValue(String.class);
                UserName.setText(userName);
                String mediaid[] = new String[20];
                String postusername[] = new String[20];
                String post[] = new String[20];
                String posttime[] = new String[20];
                String postdate[] = new String[20];
                String userid[] = new String[30];

                lstMedia.clear();
                if (dataSnapshot.exists()) {

                    int i = 1;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child("Media").getChildren()) {
                        mediaid[i]= dataSnapshot1.getKey();
                        postusername[i]=dataSnapshot.child("Media").child(mediaid[i]).child("PostUserName").getValue(String.class);
                        post[i]=dataSnapshot.child("Media").child(mediaid[i]).child("Post").getValue(String.class);
                        posttime[i]=dataSnapshot.child("Media").child(mediaid[i]).child("PostTime").getValue(String.class);
                        postdate[i]=dataSnapshot.child("Media").child(mediaid[i]).child("PostDate").getValue(String.class);
                        userid[i]=dataSnapshot.child("Media").child(mediaid[i]).child("PostUserId").getValue(String.class);

                        lstMedia.add(new Media(post[i],postusername[i],posttime[i],postdate[i], mediaid[i], userid[i]));
                        i++;
                    }
                }else{
                    RVMedia.setVisibility(View.GONE);
                }
                mediaAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });

        mediaAdapter = new MediaRecyclerViewAdapter(this,lstMedia);
        RVMedia.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,true));
        RVMedia.setAdapter(mediaAdapter);

    }

    public void filter(final String text ) {
        final List <Media> newlstMedia = new ArrayList<>();
        mediaRefer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("Users").child(UserId).child("userName").getValue(String.class);
                UserName.setText(userName);
                String mediaid[] = new String[20];
                String postusername[] = new String[20];
                String post[] = new String[20];
                String posttime[] = new String[20];
                String postdate[] = new String[20];
                String userid[] = new String[30];

                newlstMedia.clear();
                if (dataSnapshot.exists()) {
                    int i = 1;
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.child("Media").getChildren()) {
                        mediaid[i]= dataSnapshot1.getKey();
                        postusername[i]=dataSnapshot.child("Media").child(mediaid[i]).child("PostUserName").getValue(String.class);
                        post[i]=dataSnapshot.child("Media").child(mediaid[i]).child("Post").getValue(String.class);
                        posttime[i]=dataSnapshot.child("Media").child(mediaid[i]).child("PostTime").getValue(String.class);
                        postdate[i]=dataSnapshot.child("Media").child(mediaid[i]).child("PostDate").getValue(String.class);
                        userid[i]=dataSnapshot.child("Media").child(mediaid[i]).child("PostUserId").getValue(String.class);

                        if (postusername[i].toLowerCase().contains(text.toLowerCase())) {
                            newlstMedia.add(new Media(post[i],postusername[i],posttime[i],postdate[i], mediaid[i], userid[i]));
                        }
                        i++;
                    }
                }else{
                    RVMedia.setVisibility(View.GONE);
                }
                mediaAdapter.filterList(newlstMedia);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });
    }




}