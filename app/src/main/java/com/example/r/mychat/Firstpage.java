package com.example.r.mychat;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Firstpage extends AppCompatActivity {


    private Toolbar mtoolbar;
    private  ListView listview;
    private ArrayList<String> list = new ArrayList<>();
    private EditText editgp;
    private FirebaseAuth mFirebaseAuth,mFirebasAuth;
    private FirebaseUser mFirebaseUser;
    private DatabaseReference myref;
    private DatabaseReference myref2;
    private String oldTxtReceivedData;
    private DatabaseReference databaseReference,muserrf;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FirebaseRemoteConfig mRemoteConfig;
    RelativeLayout lLayout;
    ImageView btngp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firstpage);
        mFirebasAuth = FirebaseAuth.getInstance();


        if ((mFirebasAuth.getCurrentUser() == null)) {
            Toast.makeText(Firstpage.this, "failed", Toast.LENGTH_SHORT).show();
        } else {
            muserrf = FirebaseDatabase.getInstance().getReference().child("userrdata").child(mFirebasAuth.getCurrentUser().getUid());
        }

        lLayout = (RelativeLayout) findViewById(R.id.ww);

        mtoolbar = (Toolbar) findViewById(R.id.chatbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Chit-Chat");
        //RemoteConfig
        HashMap<String, Object> defaults = new HashMap<>();
        defaults.put("toolbar_color", "#E8EAF6");
        mRemoteConfig = FirebaseRemoteConfig.getInstance();
        mRemoteConfig.setDefaults(defaults);
        FirebaseRemoteConfigSettings remoteConfigSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(true)
                .build();
        mRemoteConfig.setConfigSettings(remoteConfigSettings);
        ///
        editgp = (EditText) findViewById(R.id.edtgp);
        btngp = (ImageView) findViewById(R.id.btncreategp);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new SectionpageAdapteer(getSupportFragmentManager()));

        tabLayout = (TabLayout) findViewById(R.id.tablay);
        tabLayout.setupWithViewPager(viewPager);


        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        myref = database.getReferenceFromUrl("https://mychat-8e4d9.firebaseio.com/Group");
        myref2 = database.getReferenceFromUrl("https://mychat-8e4d9.firebaseio.com/");
        databaseReference = database.getReferenceFromUrl("https://mychat-8e4d9.firebaseio.com/usersname");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        checksign();
        if ((mFirebasAuth.getCurrentUser() == null)) {
            Toast.makeText(Firstpage.this, "failed", Toast.LENGTH_SHORT).show();
        } else {

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    oldTxtReceivedData = "";
                    final ArrayList<String> mychatlist = new ArrayList<String>();

                    String email = user.getUid();

                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        if (oldTxtReceivedData == null) {
                            oldTxtReceivedData = " ";
                        }
                        if (child.getValue().toString().contains(email)) {

                            oldTxtReceivedData = child.getValue().toString();
                            oldTxtReceivedData = oldTxtReceivedData.replace("{", "");
                            oldTxtReceivedData = oldTxtReceivedData.replace("}", "");
                            String[] temp;

                            temp = oldTxtReceivedData.split("=");

                            mychatlist.add(temp[1]);

                        }
                    }

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });
        }

        btngp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = user.getUid();

                Map messagemap = new HashMap();
                messagemap.put("message", "Logged in");
                messagemap.put("seen", false);
                messagemap.put("type", "text");
                messagemap.put("time", ServerValue.TIMESTAMP);
                messagemap.put("from", email);


                myref2.child("GroupChat").child(email).push().child("gpname").setValue(editgp.getText().toString());
                myref.child(editgp.getText().toString()).push().updateChildren(messagemap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                    }
                });
            }
        });
        fetchRemoteConfigValues();
        tabLayout.getTabAt(1).setIcon(R.drawable.group);
        tabLayout.getTabAt(2).setIcon(R.drawable.news);
day_color();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow(); // in Activity's onCreate() for instance
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

    }
public  void day_color(){
    SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
    Date d = new Date();
    String dayOfTheWeek = sdf.format(d);

    switch (dayOfTheWeek.toLowerCase()) {
        case ("monday"):
            tabLayout.setTabTextColors(Color.BLACK,Color.WHITE);
            tabLayout.setSelectedTabIndicatorColor(Color.YELLOW);
            tabLayout.setBackground(new ColorDrawable(0xFF4285F4));
            mtoolbar.setBackgroundColor(Color.parseColor("#4285f4"));
            // lLayout.setBackgroundColor(color3);//backround
            // viewPager.setBackgroundColor(color2);


            break;
        case ("tuesday"):
           tabLayout.setTabTextColors(Color.BLACK,Color.WHITE);
    tabLayout.setSelectedTabIndicatorColor(Color.YELLOW);
    tabLayout.setBackground(new ColorDrawable(0xFF4285F4));
    mtoolbar.setBackgroundColor(Color.parseColor("#4285f4"));
    // lLayout.setBackgroundColor(color3);//backround
    // viewPager.setBackgroundColor(color2);

            break;

        case ("wednesday"):
            tabLayout.setTabTextColors(Color.BLACK,Color.WHITE);
    tabLayout.setSelectedTabIndicatorColor(Color.YELLOW);
    tabLayout.setBackground(new ColorDrawable(0xFF4CAF50));
    mtoolbar.setBackgroundColor(Color.parseColor("#4CAF50"));
    // lLayout.setBackgroundColor(color3);//backround
    // viewPager.setBackgroundColor(color2);


            break;
        case ("friday"):
            tabLayout.setTabTextColors(Color.BLACK,Color.WHITE);
    tabLayout.setSelectedTabIndicatorColor(Color.YELLOW);
    tabLayout.setBackground(new ColorDrawable(0xFFFFC107));
    mtoolbar.setBackgroundColor(Color.parseColor("#FFC107"));
    // lLayout.setBackgroundColor(color3);//backround
    // viewPager.setBackgroundColor(color2);

            break;
        case ("saturday"):
            tabLayout.setTabTextColors(Color.BLACK,Color.WHITE);
            tabLayout.setSelectedTabIndicatorColor(Color.YELLOW);
            tabLayout.setBackground(new ColorDrawable(0xFFFF5722));
            mtoolbar.setBackgroundColor(Color.parseColor("#FF5722"));
            // lLayout.setBackgroundColor(color3);//backround
            // viewPager.setBackgroundColor(color2);


            break;


    }



}
    public void checksign() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {
            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser u=FirebaseAuth.getInstance().getCurrentUser();
        if(u==null && u.getUid().isEmpty()){
            startActivity(new Intent(Firstpage.this, MainActivity.class));
            finish();
        }else {
            muserrf.child("online").setValue(true);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

        muserrf.child("online").setValue(false);
        muserrf.child("lastseen").setValue(ServerValue.TIMESTAMP);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.mlogout) {

            FirebaseAuth.getInstance().signOut();
            checksign();

        }
        if (item.getItemId() == R.id.btnallusers) {

            Intent intent = new Intent(Firstpage.this, AllUsersactivity.class);
            startActivity(intent);
        }


        if (item.getItemId() == R.id.menuaccsetting) {
            Intent intent = new Intent(Firstpage.this, SettingActivity.class);
            //intent.putExtra("position", chatWith);
            startActivity(intent);



        }/*if (item.getItemId() == R.id.location) {
            Intent intent = new Intent(Firstpage.this, FetchAddressIntentService.class);
            //intent.putExtra("position", chatWith);
            startActivity(intent);



        }*/

        return true;

    }

    private void setToolbarColor() {
        boolean isholiday = mRemoteConfig.getBoolean("is_holiday");
        int color = isholiday ? Color.parseColor(mRemoteConfig.getString("color_toolbar")) :
                ContextCompat.getColor(this, R.color.colorAccent);
        tabLayout.setTabTextColors(Color.BLACK,Color.WHITE);
        tabLayout.setSelectedTabIndicatorColor(Color.YELLOW);
        tabLayout.setBackground(new ColorDrawable(color));
        mtoolbar.setBackgroundColor(color);
        int color2 = isholiday ? Color.parseColor(mRemoteConfig.getString("color_pager")) :
                ContextCompat.getColor(this, R.color.colorAccent);
        int color3 = isholiday ? Color.parseColor(mRemoteConfig.getString("background_main")) :
                ContextCompat.getColor(this, R.color.colorAccent);

        lLayout.setBackgroundColor(color3);//backround
        viewPager.setBackgroundColor(color2);
//statusbar
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         if (android.os.Build.VERSION.SDK_INT >= 21) {
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }

    }

    private void fetchRemoteConfigValues() {
        long cacheExpiration = 3600;

        //expire the cache immediately for development mode.
        if (mRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            // task successful. Activate the fetched data
                            mRemoteConfig.activateFetched();
                           // setToolbarColor();
                        } else {
                            //task failed
                        }
                    }
                });
    }
}