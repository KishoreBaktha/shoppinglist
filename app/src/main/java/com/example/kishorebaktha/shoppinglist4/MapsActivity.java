package com.example.kishorebaktha.shoppinglist4;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnMarkerClickListener
{
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener listener;
    Marker marker;
    SeekBar seekBar;
    TextView textView;
    private ProgressDialog progressDialog;

    public static final double PI = 3.14159265;
    public static final double deg2radians = PI/180.0;
    ArrayList<Marker> markers;
    LatLng[] locations;
    double resfir;
    String[] names;
    double Longitude, Latitude;
    Handler h;
    int countnumber=0;
   ArrayList<String> shoplist;
    int size=0;
    int index=0;
    //shoplist = i.getStringArrayListExtra("stock_list");
    ArrayList<String> latitude=new ArrayList<String>();
    ArrayList<String>longitude=new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //mMap.setOnMarkerClickListener(this);
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        textView=(TextView)findViewById(R.id.textView);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView.setText("Distance: "+String.valueOf(progress)+" kms");
                display(( progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        h = new Handler();
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Longitude = location.getLongitude();
                Latitude = location.getLatitude();
                LatLng result=new LatLng(Latitude,Longitude);
                progressDialog.dismiss();
                if(marker != null)
                    marker.remove();
                marker=mMap.addMarker(new MarkerOptions().position(result).title("hiiiiiiiiiii i m here"));
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(result));
                mMap.animateCamera( CameraUpdateFactory.zoomTo( 10.0f ) );
                locationManager.removeUpdates(listener);
                  display(0);
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
        if(mMap!=null)
        mMap.setOnMarkerClickListener(this);
    }
    @Override
    public boolean onMarkerClick(final Marker marker) {
       // Toast.makeText(getApplicationContext(),String.valueOf(index),Toast.LENGTH_SHORT).show();
        for(int i=0;i<index;i++)
        {
            if (marker.equals(markers.get(i)))
            {
                //Toast.makeText(getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(getApplicationContext(),ItemList.class);
                intent.putExtra("specificitem",search.specificitem);
                intent.putExtra("name",markers.get(i).getTitle());
                startActivity(intent);
                break;
            }
        }
        return false;
    }
    private void display(final int min) {
        // markers=null;
        mMap.clear();
        index=0;
        LatLng result=new LatLng(Latitude,Longitude);
        marker=mMap.addMarker(new MarkerOptions().position(result).title("hiiiiiiiiiii i m here"));
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.setOnMarkerClickListener(this);
        // markers = new Marker[100];
        Toast.makeText(getApplicationContext(),String.valueOf(min),Toast.LENGTH_SHORT).show();
        //shopname=new ArrayList<String>();
       // latitude=new ArrayList<String>();
        //longitude=new ArrayList<String>();
        mMap.setOnMarkerClickListener(this);
//            searchitems10(new MyCallback11() {
//                @Override
//                public void onCallback11(ArrayList<String> name, ArrayList<Double> latitude2, ArrayList<Double> longitude2) {
//                    shopname = name;
//                    latitude = latitude2;
//                    longitude = longitude2;
//                    size=shopname.size();
                    Thread t = new Thread() {
                        public void run() {

                            markers = new ArrayList<Marker>();
                            locations = new LatLng[latitude.size()];
                            names = new String[shoplist.size()];
                            for (int i = 0; i < locations.length; i++) {
                                locations[i] = new LatLng(Double.parseDouble(latitude.get(i)), Double.parseDouble(longitude.get(i)));
                                names[i] = shoplist.get(i);
                            }

                            h.post(new Runnable() {
                                @Override
                                public void run() {
                                    IconGenerator iconFactory = new IconGenerator(getApplicationContext());
                                    for (int i = 0; i < locations.length; i++) {
                                        resfir = getDistance(locations[i].latitude, locations[i].longitude, Latitude, Longitude);
                                        if (resfir <= min) {
                                            markers.add(mMap.addMarker(new MarkerOptions().position(locations[i]).title(names[i])));
                                            markers.get(index).setIcon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(names[i])));
                                            index++;
                                            //  markers[i].showInfoWindow();
                                        }
                                    }

                                }
                            });
                        }
                    };
                    t.start();
        mMap.setOnMarkerClickListener(this);
    }
    public static double getDistance(double latitude1, double longitude1, double latitude2,double longitude2)
    {

        double lat1 = latitude1 * deg2radians;
        double lat2 = latitude2 * deg2radians;
        double lon1 = longitude1 * deg2radians;
        double lon2 = longitude2 * deg2radians;
        double radd = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin((lat1 - lat2) / 2),
                2.0)
                + Math.cos(lat1)
                * Math.cos(lat2)
                * Math.pow(Math.sin((lon1 - lon2) / 2), 2.0)));
        return radd*10000;
    }
//    public void searchitems10(final MyCallback11 mycallback11) {
//        FirebaseDatabase.getInstance()
//                .getReference()
//                .child("shops")
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//                            if (shoplist.contains(snapshot.child("name").getValue().toString()))
//                            {
//                            shopname.add(snapshot.child("name").getValue().toString());
//                            latitude.add(Double.parseDouble(snapshot.child("latitude").getValue().toString()));
//                            longitude.add(Double.parseDouble(snapshot.child("longitude").getValue().toString()));
//
//                              }
//
//                        }
//                        mycallback11.onCallback11(shopname,latitude, longitude);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });
//    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
//        Intent intent=getIntent();
//        String item=intent.getStringExtra("item");
//        service=intent.getStringExtra("service");
//        if(item.equals("Select Own Location"))
//            display2();
//        else
        Intent i = getIntent();
        shoplist=i.getStringArrayListExtra("shopname");
        latitude=i.getStringArrayListExtra("latitude");
        longitude=i.getStringArrayListExtra("longitude");
        progressDialog = ProgressDialog.show(this,"Fetching user location","Please wait...",false,false);
        locationManager.requestLocationUpdates("gps", 0, 0, listener);
        mMap.setOnMarkerClickListener(this);
         Toast.makeText(getApplicationContext(),"CLICK THE MARKER TO VIEW ITEMS",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mMap!=null)
            mMap.setOnMarkerClickListener(this);
    }
}

//interface MyCallback11 {
//    void onCallback11(ArrayList<String> name, ArrayList<Double> latitude, ArrayList<Double> longitude);
//}

