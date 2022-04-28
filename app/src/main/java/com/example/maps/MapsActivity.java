package com.example.maps;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;

import com.example.maps.Slider.SliderActivity;
import com.example.maps.Slider.SlidingAdapter;
import com.example.maps.model.ApiCall;
import com.example.maps.model.ModelApi;
import com.example.maps.modelflickr.FlickrApiCall;
import com.example.maps.modelflickr.modelFlickr;
import com.example.maps.modelflickr.modelPhoto;
import com.example.maps.modelflickr.modelPhotos;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.maps.databinding.ActivityMapsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private modelPhotos photo;
    private ArrayList<modelPhoto> photos = new ArrayList<modelPhoto>();
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> secrets = new ArrayList<>();
    private ArrayList<String> servers = new ArrayList<>();
    private ArrayList<String> urls = new ArrayList<>();
    private String baseImageUrl = "https://live.staticflickr.com/";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://www.flickr.com/services/rest/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final GoogleMap map) {
                enableMyLocation();

                // When map is clicked
                map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        Log.d("TAG", "onMapClick latitud and longitud: "+latLng);
                        // Creating a marker
                        MarkerOptions markerOptions = new MarkerOptions();

                        // Setting the position for the marker
                        markerOptions.position(latLng);

                        // Setting the title for the marker.
                        // This will be displayed on taping the marker
                        markerOptions.title(getAddress(latLng.latitude, latLng.longitude));

                        // Clears the previously touched position
                        mMap.clear();

                        // Animating to the touched position
                        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                        // Placing a marker on the touched position
                        mMap.addMarker(markerOptions);

                        FlickrApiCall apiCall = retrofit.create(FlickrApiCall.class);
                        Call<modelFlickr> call = apiCall.getData(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));

                        call.enqueue(new Callback<modelFlickr>(){
                            @Override
                            public void onResponse(Call<modelFlickr> call, Response<modelFlickr> response) {
                                if(response.code()!=200){
                                    Log.i("testApi", "checkConnection");
                                    return;
                                }

                                photo = response.body().getPhotos();
                                if (photo.getPhoto().size()!=0) {
                                    for (int i=0; i < 10; i++) {
                                        if (photo.getPhoto().get(i) != null) {
                                            photos.add(photo.getPhoto().get(i));
                                            ids.add(photos.get(i).getId());
                                            servers.add(photos.get(i).getServer());
                                            secrets.add(photos.get(i).getSecret());
                                            urls.add(baseImageUrl + servers.get(i) +"/"+ ids.get(i) +"_"+ secrets.get(i)+".jpg");
                                        }
                                        Log.i("testApi", urls.get(i).toString());
                                    }
                                }
                                if (urls != null) {
                                    ViewPager mPager = findViewById(R.id.vpager);
                                    mPager.setAdapter(new SlidingAdapter(MapsActivity.this, urls));
                                }
                            }

                            @Override
                            public void onFailure(Call<modelFlickr> call, Throwable t) {

                            }
                        });

                        ApiThread apiT = new ApiThread(latLng.latitude, latLng.longitude);
                        apiT.execute();
                    }
                });
            }
        });
    }

    public String getAddress(double lat, double lng) {
        try {
            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lng, 1);
            if (addresses.isEmpty()) {
                Toast.makeText(this, "No s’ha trobat informació", Toast.LENGTH_LONG).show();
            } else {
                if (addresses.size() > 0) {
                    String msg =addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();

                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    return msg;
                }
            }
        }
        catch(Exception e){
            Toast.makeText(this, "No Location Name Found", Toast.LENGTH_LONG).show();
        }
        return "No Location Name Found";
    }

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(41.3879, 2.16992);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d("lat", "onMapClick -->" + latLng);
                Log.d("lng", "onMapClick -->" + latLng.longitude);
                getAddress(latLng.latitude,latLng.longitude);
            }
        });
    }
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }


    public boolean onMyLocationButtonClick() {
        return false;
    }

    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }


}

