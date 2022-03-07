package com.example.maps;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.maps.Slider.SliderActivity;
import com.example.maps.model.ApiCall;
import com.example.maps.model.ModelApi;
import com.example.maps.modelflickr.FlickrApiCall;
import com.example.maps.modelflickr.modelFlickr;
import com.example.maps.modelflickr.modelPhoto;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    public void getAddress(double lat, double lng) {
        try {

            Geocoder geo = new Geocoder(this.getApplicationContext(), Locale.getDefault());
            List<Address> addresses = geo.getFromLocation(lat, lng, 1);
            if (addresses.isEmpty()) {
                Toast.makeText(this, "No s’ha trobat informació", Toast.LENGTH_LONG).show();
            } else {
                if (addresses.size() > 0) {
                    String msg =addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " + addresses.get(0).getCountryName();

                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    ApiThread api =new ApiThread(lat,lng);
                    api.execute();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("https://www.flickr.com/services/rest/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    FlickrApiCall apiCall = retrofit.create(FlickrApiCall.class);
                    Call<modelFlickr> call = apiCall.getData(lat, lng);
                    call.enqueue(new Callback<modelFlickr>(){
                        @Override
                        public void onResponse(Call<modelFlickr> call, Response<modelFlickr> response) {
                            Log.i("testApi", "---------->");
                            if(response.code()!=200){
                                Log.i("testApi", "checkConnection");
                                return;
                            }

                            ArrayList<modelPhoto> lista = response.body().getPhotos().getPhoto();
                            //ArrayList<String> images = modelFlickr.getPhotos(lista);
                            ArrayList<String> urls = new ArrayList<String>();
                            for (int i = 0; i<5;i++){
                                String url = "https://live.staticflickr.com/"+lista.get(i).getServer()+"/"+lista.get(i).getId()+"_"+lista.get(i).getSecret()+".jpg";
                                Log.i("testApi", url);
                                urls.add(url);
                            }
                            Log.i("testApi", response.body().getStat() + " - " + response.body().getPhotos().getPhoto().get(0).getTitle());
                            Intent intent = new Intent(getApplicationContext(), SliderActivity.class);
                            intent.putExtra("urls",urls);
                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<modelFlickr> call, Throwable t) {

                        }
                    });

                }
            }
        }
        catch(Exception e){
            Toast.makeText(this, "No Location Name Found", Toast.LENGTH_LONG).show();
        }
    }


}

