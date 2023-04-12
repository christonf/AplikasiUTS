package com.example.aplikasiuts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
//    Button btLocation;
    TextView latitude, longitude, countryName, locality, address;
    FusedLocationProviderClient fusedLocationProviderClient;
    BottomNavigationView bottomNav;
    private Boolean isFragmentDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Variable
        //btLocation = findViewById(R.id.bt_location);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        countryName = findViewById(R.id.country_name);
        locality = findViewById(R.id.locality);
        address = findViewById(R.id.address);
        bottomNav = findViewById(R.id.bottom_nav);



        bottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                switch (item.getItemId()) {
                    case R.id.maps:
                        SimpleFragment simpleFragment = new SimpleFragment();
                        fragmentTransaction.replace(R.id.frame_container, simpleFragment).addToBackStack(null).commit();

                        LinearLayout layout1 = (LinearLayout) findViewById(R.id.linear);
                        layout1.setVisibility(View.GONE);
                        return true;
                    case R.id.home:
                        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.frame_container)).commit();

                        LinearLayout layout2 = (LinearLayout) findViewById(R.id.linear);
                        layout2.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.tes:
                        CompassFragment compassFragment = new CompassFragment();
                        fragmentTransaction.replace(R.id.frame_container, compassFragment).addToBackStack(null).commit();

                        LinearLayout layout3 = (LinearLayout) findViewById(R.id.linear);
                        layout3.setVisibility(View.GONE);
                        return true;
                }
                return false;
            }
        });

        //Init btLocation
//        btLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!isFragmentDisplayed) {
//                    displayFragment();
//                }
//                else {
//                    closeFragment();
//                }
//            }
//        });
        getLocation();
    }

//    public void displayFragment() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        SimpleFragment simpleFragment = SimpleFragment.newInstance();
//
//        fragmentTransaction.add(R.id.frame_container, simpleFragment).addToBackStack(null).commit();
//
//        isFragmentDisplayed =true;
////        btLocation.setText(R.string.go_back);
//        latitude.setVisibility(View.GONE);
//        longitude.setVisibility(View.GONE);
//        countryName.setVisibility(View.GONE);
//        locality.setVisibility(View.GONE);
//        address.setVisibility(View.GONE);
//        t1.setVisibility(View.GONE);
//        t2.setVisibility(View.GONE);
//        t3.setVisibility(View.GONE);
//        t4.setVisibility(View.GONE);
//        t5.setVisibility(View.GONE);
//        label.setVisibility(View.GONE);
//    }
//
//    public void closeFragment() {
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        SimpleFragment simpleFragment = (SimpleFragment) fragmentManager.findFragmentById(R.id.frame_container);
//
//        if (simpleFragment != null) {
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.remove(simpleFragment).commit();
//        }
//
////        btLocation.setText(R.string.show_maps);
//        isFragmentDisplayed = false;
//        latitude.setVisibility(View.VISIBLE);
//        longitude.setVisibility(View.VISIBLE);
//        countryName.setVisibility(View.VISIBLE);
//        locality.setVisibility(View.VISIBLE);
//        address.setVisibility(View.VISIBLE);
//        t1.setVisibility(View.VISIBLE);
//        t2.setVisibility(View.VISIBLE);
//        t3.setVisibility(View.VISIBLE);
//        t4.setVisibility(View.VISIBLE);
//        t5.setVisibility(View.VISIBLE);
//        label.setVisibility(View.VISIBLE);
//    }

    private void getLocation() {
        //Init fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //When permission granted
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    //Init location
                    Location location = task.getResult();
                    if (location != null) {
                        //Init geoCoder
                        Geocoder geocoder = new Geocoder(MainActivity.this,
                                Locale.getDefault());
                        //Init address list
                        try {
                            List<Address> addresses = geocoder.getFromLocation(
                                    location.getLatitude(), location.getLongitude(), 1
                            );
                            //Set latitude on TextView
                            latitude.setText(getString(R.string.none) + addresses.get(0).getLatitude());
                            //Set longitude on TextView
                            longitude.setText(getString(R.string.none) + addresses.get(0).getLongitude());
                            //Set country name on TextView
                            String cn = addresses.get(0).getCountryName();
                            countryName.setText(cn);
                            //Set locality on TextView
                            String loc = addresses.get(0).getLocality();
                            locality.setText(loc);
                            //Set address on TextView
                            String adr = addresses.get(0).getAddressLine(0);
                            address.setText(adr);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }else {
            //When permission denied
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle options menu item clicks here.
        int id = item.getItemId();
        if (id == R.id.language) {
            Intent languageIntent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
            startActivity(languageIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}