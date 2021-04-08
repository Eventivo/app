package com.raredevz.eventivo.User;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raredevz.eventivo.Ac_Splash;
import com.raredevz.eventivo.Account.VerifyNumber;
import com.raredevz.eventivo.Chat.Constants;
import com.raredevz.eventivo.Chat.SettingsAPI;
import com.raredevz.eventivo.Helper.AlertMessage;
import com.raredevz.eventivo.Helper.Cons;
import com.raredevz.eventivo.Helper.User;
import com.raredevz.eventivo.Helper.UserStatus;
import com.raredevz.eventivo.Helper.Venue;
import com.raredevz.eventivo.Helper.VenueAdapter_Horizontal;
import com.raredevz.eventivo.R;

import java.util.ArrayList;
import java.util.List;

public class Ac_Home extends AppCompatActivity implements VenueAdapter_Horizontal.ItemClickListener {

    NavigationView navigationView;

    ArrayList<String> punjabCities;
    Spinner spCities;

    FirebaseAuth firebaseAuth;

    RecyclerView ry_suggested_venues, ry_nearby_venues;


    DatabaseReference Dref;

    List<Venue> suggestedvenueList, nearbyVenueList;
    DrawerLayout drawer;
    EditText txtSearch;
    VenueAdapter_Horizontal adapter;

    LocationManager mLocationManager;
    Location mLocation;
    Button btnAccount;

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            mLocation=location;
            sortNearBy();
        }
        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private void sortNearBy(){
        try {
            if (nearbyVenueList!=null){
                if (mLocation!=null){
//                    Collections.sort(nearbyVenueList, new Comparator<Venue>() {
//                        @Override
//                        public int compare(Venue o1, Venue o2) {
//                            return Float.compare((float)mLocation.distanceTo(o1.getLocation().toGoogleLocation()),(float)mLocation.distanceTo(o2.getLocation().toGoogleLocation()));
//                        }
//                    });
                 //   Toast.makeText(this, "Ordered!", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e){}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //region Drawer_Navigation
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txtSearch = findViewById(R.id.txtSearch);

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{ Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},101);

        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100000,
                    10000, mLocationListener);
        }catch (Exception e){}


        drawer = (DrawerLayout) findViewById(R.id.drawer_home);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView=findViewById(R.id.nav_view);

        firebaseAuth=FirebaseAuth.getInstance();

        View view =navigationView.getHeaderView(0);
        btnAccount=view.findViewById(R.id.btnAccount);
        btnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cons.otp_destination= Cons.otp_destination_user;
                startActivity(new Intent(getApplicationContext(), VerifyNumber.class));
            }
        });

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth mfirebaseAuth) {
                if (mfirebaseAuth.getCurrentUser()!=null){
                    navigationView = (NavigationView) findViewById(R.id.nav_view);
                    Menu nav_Menu = navigationView.getMenu();
                    nav_Menu.findItem(R.id.btnDrawerAddVenue).setVisible(false);
                    nav_Menu.findItem(R.id.btnDrawerLogout).setVisible(true);
                }else {
                    navigationView = (NavigationView) findViewById(R.id.nav_view);
                    Menu nav_Menu = navigationView.getMenu();
                    nav_Menu.findItem(R.id.btnDrawerLogout).setVisible(false);
                    nav_Menu.findItem(R.id.btnDrawerAddVenue).setVisible(true);
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId()==R.id.btnDrawerHome){
                    drawer.closeDrawer(GravityCompat.START);

                }
                if (item.getItemId()==R.id.btnDrawerSearchVenue){
                    drawer.closeDrawer(GravityCompat.START);
                    txtSearch.requestFocus();
                }
                if (item.getItemId()==R.id.btnDrawerAddVenue){
                    Cons.otp_destination=Cons.otp_destination_admin;
                    startActivity(new Intent(getApplicationContext(), VerifyNumber.class));
                }else if (item.getItemId()==R.id.btnDrawerTermsAndCondition){
                    startActivity(new Intent(getApplicationContext(),Ac_TermsAndCondition.class));
                }else if (item.getItemId()==R.id.btnDrawerAboutUs){
                    startActivity(new Intent(getApplicationContext(),Ac_AboutUs.class));
                }else if (item.getItemId()==R.id.btnDrawerMyBooking){
                    if (firebaseAuth.getCurrentUser()!=null){
                        startActivity(new Intent(getApplicationContext(),Ac_MyBookings.class));
                    }else{
                        Cons.otp_destination=Cons.otp_destination_user;
                        startActivity(new Intent(getApplicationContext(), VerifyNumber.class));
                    }

                }else if (item.getItemId()==R.id.btnDrawerLogout){
                    if (firebaseAuth.getCurrentUser()!=null){
                        firebaseAuth.signOut();
                        Toast.makeText(Ac_Home.this, "You're logged out!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), Ac_Splash.class));
                    }else {
                        Cons.otp_destination=Cons.otp_destination_user;
                        startActivity(new Intent(getApplicationContext(), VerifyNumber.class));
                    }
                }
                return true;
            }
        });
        //endregion

        Dref= FirebaseDatabase.getInstance().getReference();
        Dref.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    if (firebaseAuth.getCurrentUser()!=null){
                        User manager=snapshot.child(firebaseAuth.getUid()).getValue(User.class);
                        //Toast.makeText(Ac_Home.this, ""+manager.getName(), Toast.LENGTH_SHORT).show();

                       // if (manager!=null){
                           // btnAccount.setVisibility(View.GONE);
                            //Toast.makeText(Ac_Home.this, ""+manager.getName(), Toast.LENGTH_SHORT).show();
                            btnAccount.setText(manager.getName());
                            btnAccount.setVisibility(View.VISIBLE);
                            btnAccount.setClickable(false);


                            SettingsAPI set;
                            set = new SettingsAPI(Ac_Home.this);
                            set.addUpdateSettings(Constants.PREF_MY_ID, FirebaseAuth.getInstance().getUid());
                            set.addUpdateSettings(Constants.PREF_MY_NAME, manager.getName());
                            set.addUpdateSettings(Constants.PREF_MY_DP, manager.getImageUrl());
                       // }

                       // Toast.makeText(Ac_Home.this, ""+manager.getStatus(), Toast.LENGTH_SHORT).show();

                        if (manager.getStatus().equals(UserStatus.Disabled)){
                            AlertMessage.showMessage(Ac_Home.this,"Your account has been disabled by the admin!");
                            try {
                                firebaseAuth.signOut();
                                startActivity(new Intent(getApplicationContext(), Ac_Splash.class));
                                finish();
                            }catch (Exception e){}
                        }
                    }
                }catch (Exception e){

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //region PunjabCities
        punjabCities=new ArrayList<>();
        punjabCities.add("Sahiwal"   );
        punjabCities.add("Arifwala" );
        punjabCities.add("Okara"   );
        punjabCities.add("Renala Khurd"   );
        punjabCities.add("Pakpattan"   );
        punjabCities.add("Pattoki"   );


        //endregion


        spCities=findViewById(R.id.spCities);
        ry_suggested_venues=findViewById(R.id.ry_suggested_venues);
        ry_nearby_venues=findViewById(R.id.ry_nearby_venues);

        ArrayAdapter cadapter=new ArrayAdapter(Ac_Home.this,R.layout.li_sp_simple,punjabCities);
        spCities.setAdapter(cadapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        LinearLayoutManager nlayoutManager = new LinearLayoutManager(this);
        nlayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        ry_suggested_venues.setLayoutManager(layoutManager);

        ry_nearby_venues.setLayoutManager(nlayoutManager);

        DividerItemDecoration dividerItemDecoration=new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL);
        dividerItemDecoration.setDrawable(getDrawable(R.drawable.divider_ry_venue));
        ry_suggested_venues.addItemDecoration(dividerItemDecoration);
        ry_nearby_venues.addItemDecoration(dividerItemDecoration);




        suggestedvenueList =new ArrayList<>();
        adapter=new VenueAdapter_Horizontal(Ac_Home.this, suggestedvenueList);

        Dref.child(Cons.node_venue).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                suggestedvenueList =new ArrayList<>();
                nearbyVenueList=new ArrayList<>();
                for (DataSnapshot city:snapshot.getChildren()){
                    for (DataSnapshot v:city.getChildren()){
                        Venue venue=v.getValue(Venue.class);
                        venue.setId(v.getKey());
                        suggestedvenueList.add(venue);
                        if (city.getKey().equals(spCities.getSelectedItem().toString())){
                            nearbyVenueList.add(venue);
                        }
                    }
                }
                sortNearBy();
                 adapter=new VenueAdapter_Horizontal(Ac_Home.this, suggestedvenueList);

                VenueAdapter_Horizontal nearByadapter=new VenueAdapter_Horizontal(Ac_Home.this, nearbyVenueList);
                ry_suggested_venues.setAdapter(adapter);
                ry_nearby_venues.setAdapter(nearByadapter);

                adapter.setClickListener(Ac_Home.this);
                nearByadapter.setClickListener(Ac_Home.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }



    @Override
    public void onItemClick(View view, Venue venue) {
        Intent i=new Intent(Ac_Home.this,Ac_Venue_Details.class);
        i.putExtra("venue",venue);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // startActivity(new Intent(getApplicationContext(), Ac_Home.class));
        //finish();
    }
}