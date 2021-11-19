package com.example.nearbyhospitals;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.maps.CameraUpdate;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.CameraPosition;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private MapView mMapView;
    private HuaweiMap hmap;
    private Marker mMarker;
    private ArrayList<String> hospitalTitle;
    private ArrayList<String> hospitalLat;
    private ArrayList<String> hospitalLong;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private double mLatitude, mLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        hospitalLong = new ArrayList<>();
        hospitalLat = new ArrayList<>();
        hospitalTitle = new ArrayList<>();
        getDataFromHospitalLocationActivity();

        mMapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
    }

    private void getDataFromHospitalLocationActivity() {
        mLatitude = getIntent().getExtras().getDouble("lat");
        mLongitude = getIntent().getExtras().getDouble("long");
        hospitalTitle = getIntent().getStringArrayListExtra("hospitalTitle");
        hospitalLat = getIntent().getStringArrayListExtra("hospitalLat");
        hospitalLong = getIntent().getStringArrayListExtra("hospitalLong");
    }

    public void onMapReady(HuaweiMap huaweiMap) {
        Log.d(TAG, "onMapReady: ");

        hmap = huaweiMap;
        hmap.setMyLocationEnabled(true); //Enabling Location
        hmap.setMapType(HuaweiMap.MAP_TYPE_NORMAL);
        hmap.setMaxZoomPreference(15); //Zoom Preferences
        hmap.setMinZoomPreference(5); //Zoom Preferences
        CameraPosition build = new CameraPosition.Builder()
                .target(new LatLng(mLatitude, mLongitude))
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory
                .newCameraPosition(build);
        hmap.animateCamera(cameraUpdate);

        //Drawing the nearest hospitals on the map
        for (int i = 0; i < hospitalTitle.size(); i++) {
            MarkerOptions options = new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(hospitalLat.get(i)),
                            Double.parseDouble(hospitalLong.get(i))))
                    .title(hospitalTitle.get(i))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.newhospitalicon)); // An icon for hospitals
            mMarker = hmap.addMarker(options);
            mMarker.showInfoWindow();
        }

        hmap.setOnMarkerClickListener(new HuaweiMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                return false;
            }
        });
    }
}