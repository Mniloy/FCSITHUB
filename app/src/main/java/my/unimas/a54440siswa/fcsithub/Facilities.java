package my.unimas.a54440siswa.fcsithub;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;


public class Facilities extends AppCompatActivity {

    ImageView IVLogout;
    ImageView IVback, IVSearch;
    String UserId;
    TextView UserName;
    CircleImageView CVProfile;


    FirebaseAuth mAuth;
    FirebaseUser user;
    FirebaseAuth.AuthStateListener mAuthListener;


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
        setContentView(R.layout.activity_facilities);
        mAuth = FirebaseAuth.getInstance();

        IVback =  findViewById(R.id.IVback);
        IVLogout =  findViewById(R.id.IVLogout);
        IVLogout.setVisibility(View.INVISIBLE);
        IVSearch =  findViewById(R.id.IVsearch);
        IVSearch.setVisibility(View.INVISIBLE);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();
        UserId= user.getUid();
        UserName =findViewById(R.id.username);

        rootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String userName = dataSnapshot.child("Users").child(UserId).child("userName").getValue(String.class);
                UserName.setText(userName);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Hello", "Failed to read value.", error.toException());
            }
        });



        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(Facilities.this, HomeActivity.class));
                }
            }
        };

        IVback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent homeactivity = new Intent (Facilities.this, HomeActivity.class);
                startActivity(homeactivity);
            }
        });


       /* mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(Facilities.this, SignIn.class));
                }
            }
        };

        IVLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
            }
        }); */


    }

}
