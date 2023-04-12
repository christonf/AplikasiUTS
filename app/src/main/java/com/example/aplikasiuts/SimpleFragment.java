package com.example.aplikasiuts;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import java.util.Locale;

public class SimpleFragment extends Fragment implements SensorEventListener {
    private GoogleMap mMap;
    private SensorManager mSensorManager;
    private Sensor mSensorLight;

    public SimpleFragment() {
        // Required empty public constructor
    }

    public static SimpleFragment newInstance() {
        return new SimpleFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Initialize Sensor Manager
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // Inflate the layout for this fragment
        final View rootView =  inflater.inflate(R.layout.fragment_simple, container, false);
        // Initialize map fragment
        SupportMapFragment supportMapFragment=(SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        // Async map
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                // When map is loaded
                LatLng vokasiUGM = new LatLng(-7.774869180828544, 110.37441481432154);
                googleMap.addMarker(new MarkerOptions().position(vokasiUGM).title("SV UGM"));
                //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(vokasiUGM, 20));

                setMapLongClick(mMap);
                setPoiClick(mMap);
                enableMyLocation();
            }

            private void setMapLongClick(final GoogleMap map) {
                map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(@NonNull LatLng latLng) {
                        String text = String.format(Locale.getDefault(),
                                "Lat : %1$.5f, Long : %2$.5f",
                                latLng.latitude,
                                latLng.longitude);
                        map.addMarker(new MarkerOptions().position(latLng)
                                .title("Dropped pin")
                                .snippet(text));
                    }
                });
            }

            private void setPoiClick(final GoogleMap map) {
                map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
                    @Override
                    public void onPoiClick(@NonNull PointOfInterest pointOfInterest) {
                        Marker poiMarker = mMap.addMarker(new MarkerOptions()
                                .position(pointOfInterest.latLng)
                                .title(pointOfInterest.name));
                        poiMarker.showInfoWindow();
                    }
                });
            }

            private void enableMyLocation() {
                if (ActivityCompat.checkSelfPermission(getActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                else {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            1);
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mSensorLight != null) {
            mSensorManager.registerListener(this, mSensorLight,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        int sensorType = sensorEvent.sensor.getType();
        float currentValue = sensorEvent.values[0];

        switch (sensorType) {
            case Sensor.TYPE_LIGHT:
                if(currentValue <= 70 &&  currentValue >= 15) {
                    //Try catch map style light
                    try {
                        // Customise the styling of the base map using a JSON object defined
                        // in a raw resource file.
                        boolean successLight = mMap.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(
                                        getActivity(), R.raw.light));

                        if (!successLight) {
                            Log.e("MapsActivity", "Style parsing failed.");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.e("MapsActivity", "Can't find style. Error: ", e);
                    };
                }
                else if(currentValue <15 && currentValue > 0) {
                    //Try catch map style dark
                    try {
                        // Customise the styling of the base map using a JSON object defined
                        // in a raw resource file.
                        boolean successDark = mMap.setMapStyle(
                                MapStyleOptions.loadRawResourceStyle(
                                        getActivity(), R.raw.dark));

                        if (!successDark) {
                            Log.e("MapsActivity", "Style parsing failed.");
                        }
                    } catch (Resources.NotFoundException e) {
                        Log.e("MapsActivity", "Can't find style. Error: ", e);
                    };
                }
                break;
            default:
                //
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}