package com.example.nearbyhospitals;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.common.ResolvableApiException;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.location.LocationSettingsResponse;
import com.huawei.hms.location.LocationSettingsStatusCodes;
import com.huawei.hms.location.SettingsClient;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.LocationType;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "HospitalLocationActivity";
    private double longtitude, latitude;
    private TextView distanceID, yourLatitude, yourLongtitude;
    private ImageView searchHospitalImage;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private SettingsClient settingsClient;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private SearchService searchService;
    private ArrayList<String> hospitalTitle;
    private ArrayList<String> hospitalLat;
    private ArrayList<String> hospitalLong;
    private LocationAvailability locationAvailability;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hospitalTitle = new ArrayList<>();
        hospitalLat = new ArrayList<>();
        hospitalLong = new ArrayList<>();

        distanceID = findViewById(R.id.distanceID);
        yourLatitude = findViewById(R.id.yourLatitude);
        yourLongtitude = findViewById(R.id.yourLongtitude);

        checkPermissions();
        getLocationData();

        searchHospitalImage = findViewById(R.id.searchHospitalImage);
        searchHospitalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (distanceID.getText().toString().equals("")) {
                    Toast.makeText(getApplication(), "Please enter a value", Toast.LENGTH_SHORT).show();
                } else {
                    int desiredRadius = Integer.parseInt(distanceID.getText().toString()) * 1000; //Convert the value to the int
                    findHospitalsBySiteKit(desiredRadius); // Use Site Kit
                    sendHospitalData(); // Show on Map Kit
                }
            }
        });
    }

    private void checkPermissions() {
        //Check Permissions
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "SDK < 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
            }
        }

        if (requestCode == 2) {
            if (grantResults.length > 2 && grantResults[2] == PackageManager.PERMISSION_GRANTED
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION successful");
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply ACCESS_BACKGROUND_LOCATION  failed");
            }
        }
    }

    private void getLocationData() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MainActivity.this);//create a fusedLocationProviderClient
        settingsClient = LocationServices.getSettingsClient(MainActivity.this);  //create a settingsClient
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);  // set the interval for location updates, in milliseconds.
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // set the priority of the request
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    List<Location> locations = locationResult.getLocations();
                    if (!locations.isEmpty()) {
                        for (Location location : locations) {
                            longtitude = location.getLongitude(); // Get your Latitude
                            latitude = location.getLatitude(); // Get your Longitude
                            yourLatitude.setText(latitude + ""); // Set your Latitude
                            yourLongtitude.setText(longtitude + ""); // Set your Longitude
                        }
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                if (locationAvailability != null) {
                    boolean flag = locationAvailability.isLocationAvailable();
                    Log.i(TAG, "onLocationAvailability isLocationAvailable:" + flag);
                } else {
                    Log.i(TAG, "onLocationAvailability isLocationAvailable:" + "faulty");
                }
            }
        };

        requestLocationUpdatesWithCallback();
    }

    private void requestLocationUpdatesWithCallback() {
        try {
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();
            locationAvailability = new LocationAvailability();
            Log.i(TAG, "Location Availability: " + locationAvailability);
            Log.i(TAG, "Location Status: " + locationAvailability.getLocationStatus());
            Log.i(TAG, "Wifi Status: " + locationAvailability.getWifiStatus());
            Log.i(TAG, "Is Location Available: " + locationAvailability.isLocationAvailable());

            //check devices settings before request location updates.
            settingsClient.checkLocationSettings(locationSettingsRequest)
                    .addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                        @Override
                        public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                            Log.i(TAG, "check location settings success");
                            //request location updates
                            fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.i(TAG, "requestLocationUpdatesWithCallback onSuccess");

                                }
                            })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(Exception e) {
                                            Log.e(TAG, "requestLocationUpdatesWithCallback onFailure:" + e.getMessage());
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Log.e(TAG, "checkLocationSetting onFailure:" + e.getMessage());
                            int statusCode = ((ApiException) e).getStatusCode();
                            switch (statusCode) {
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    try {
                                        ResolvableApiException rae = (ResolvableApiException) e;
                                        rae.startResolutionForResult(MainActivity.this, 0);
                                    } catch (IntentSender.SendIntentException sie) {
                                    }
                                    break;
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "requestLocationUpdatesWithCallback exception:" + e.getMessage());
        }
    }

    private void findHospitalsBySiteKit(int desiredRadius){
        searchService = SearchServiceFactory.create(this, "API key"); //Create searchService Object
        final Coordinate location = new Coordinate(latitude,longtitude); // User location
        NearbySearchRequest nearbySearchRequest = new NearbySearchRequest(); // Create a nearbySearchRequest
        nearbySearchRequest.setLocation(location); // Set user location
        nearbySearchRequest.setQuery("Hospital"); // Set query hospital
        nearbySearchRequest.setPageSize(20);
        nearbySearchRequest.setRadius(desiredRadius);
        nearbySearchRequest.setPoiType(LocationType.HOSPITAL);

        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                List<Site> sites = results.getSites();
                if (results == null || results.getTotalCount() <= 0 || sites == null || sites.size() <= 0) {
                    return;
                }
                for (Site site : sites) {
                    hospitalTitle.add(site.getName());
                    hospitalLat.add(site.getLocation().getLat()+""); //Converting the values to String
                    hospitalLong.add(site.getLocation().getLng()+""); //Converting the values to String
                }

            }
            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
                Log.i("TAG", "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };
        searchService.nearbySearch(nearbySearchRequest, resultListener);
    }

    private void sendHospitalData() {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        intent.putExtra("locationClicked", false);
        intent.putExtra("lat", latitude);
        intent.putExtra("long", longtitude);
        intent.putStringArrayListExtra("hospitalTitle", hospitalTitle);
        intent.putStringArrayListExtra("hospitalLat", hospitalLat);
        intent.putStringArrayListExtra("hospitalLong", hospitalLong);
        startActivity(intent);
        //Clear lists for future searchs.
        hospitalTitle.clear();
        hospitalLat.clear();
        hospitalLong.clear();
    }
}