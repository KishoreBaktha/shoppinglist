package com.example.kishorebaktha.shoppinglist4;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class addshop extends AppCompatActivity {
    EditText shopname, rating, timing;
    private LocationManager locationManager;
    DatabaseReference databaseReference;
    Double Longitude, Latitude;
    private static int SIGN_IN_CODE = 1;
    private LocationListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addshop);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("shops").push();
        shopname = (EditText) findViewById(R.id.shopcount);
        rating = (EditText) findViewById(R.id.rating2);
        timing = (EditText) findViewById(R.id.timing);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Longitude = location.getLongitude();
                Latitude = location.getLatitude();
                Map<String, Object> map = new HashMap<>();
                map.put("name", shopname.getText().toString());
                map.put("latitude", Latitude.toString());
                map.put("longitude", Longitude.toString());
                map.put("rating", rating.getText().toString());
                map.put("timing", timing.getText().toString());
                databaseReference.setValue(map);
                locationManager.removeUpdates(listener);
                locationManager = null;
                Toast.makeText(getApplicationContext(), "Inserted", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),Newitem.class);
                startActivity(intent);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };
    }


    public void addshop2(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 1000, 10, listener);
    }
}
