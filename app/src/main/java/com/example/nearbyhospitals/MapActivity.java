package com.example.nearbyhospitals;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.HwLocationType;
import com.huawei.hms.site.api.model.NearbySearchRequest;
import com.huawei.hms.site.api.model.NearbySearchResponse;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends Activity implements OnMapReadyCallback {
    private MapView mMapView;
    private Marker mMarker;
    private SearchService searchService;
    Switch switchbutton;
    MarkerOptions options;
    HuaweiMap hmap;
    ArrayList<Site> marray = new ArrayList();
    private double mLatitude, mLongitude;
    public int radius;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mLatitude = getIntent().getExtras().getDouble("lat");
        mLongitude = getIntent().getExtras().getDouble("long");
        radius = getIntent().getExtras().getInt("radius");

        Log.v("Nithya", "checked " + mLatitude);
        Log.v("Nithya", "checked " + mLongitude);
        Log.v("Nithya", "checked " + radius);

        switchbutton = (Switch) findViewById(R.id.swith_hosp_pharmcy);
        mMapView = findViewById(R.id.mapview_mapviewdemo);

        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle("MapViewBundleKey");
        }

        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        switchbutton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {
                {
                    Toast.makeText(MapActivity.this, "check changed" + ischecked, Toast.LENGTH_SHORT).show();
                    try {
                        if (ischecked) {
                            Log.v("Nithya", "checked" + ischecked);
                            searchService("Hosptial");


                        } else {
                            Log.v("Nithya", "not checked" + ischecked);
                            searchService("Pharmacy");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hmap = huaweiMap;
        hmap.setMyLocationEnabled(true);

        try {
            searchService("Pharmacy");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }
        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    public void searchService(String query) throws UnsupportedEncodingException {
        searchService = SearchServiceFactory.create(MapActivity.this, URLEncoder.encode("CwEAAAAA3pIlbmtVTY1yVHFPicpUwA4f5Xn6ZOrhKE+bpxPUZJ0CWTfE30A0liSIYxZ4YX7qjGvC/ACXovgcokq0TrMZrQ0UgoE=", "UTF-8"));

        // Create a request body.
        NearbySearchRequest request = new NearbySearchRequest();
        Coordinate location = new Coordinate(mLatitude, mLongitude); // HEre i have hardcoded my location , u need to set ur location that u get from location kit here
        request.setLocation(location);
        request.setQuery(query);
        request.setRadius(radius);
        if (query.equals("Hospital")) {
            request.setHwPoiType(HwLocationType.GENERAL_HOSPITAL);
        }
        if (query.equals("Pharmacy")) {
            request.setHwPoiType(HwLocationType.PHARMACY);
        }
        request.setPageIndex(1);
        request.setPageSize(5);
        request.setStrictBounds(false);
        // Create a search result listener.
        SearchResultListener<NearbySearchResponse> resultListener = new SearchResultListener<NearbySearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(NearbySearchResponse results) {
                if (results == null || results.getTotalCount() <= 0) {
                    return;
                }
                List<Site> sites = results.getSites();
                if (sites == null || sites.size() == 0) {
                    return;
                }
                for (Site site : sites) {
                    Log.i("Nithya", String.format("siteId: '%s', name: %s\r\n", site.getSiteId(), site.getName()));
                    Toast.makeText(MapActivity.this, "siteId: '%s', name: %s\r\n" + site.getSiteId() + site.getName(), Toast.LENGTH_SHORT).show();
                    marray.add(site);
                }
                addmarker(marray);
            }

            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
                Log.i("Nithya", "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
            }
        };
        // Call the nearby place search API.
        searchService.nearbySearch(request, resultListener);
    }


    private void addmarker(ArrayList<Site> poiarray) {
        hmap.clear();
        hmap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(mLatitude, mLongitude), 8)); // Here again pass the  lat and lon value that i got from  location kit

        for (int m = 0; m < poiarray.size(); m++) {
            Log.v("Nithya", "Site name" + poiarray.get(m).name + "address" + poiarray.get(m).formatAddress + "loc--lat" + poiarray.get(m).getLocation().getLat() + "loc--lon" + poiarray.get(m).getLocation().getLng());
            if (poiarray.get(m).poi.equals("Hospital")) {

                options = new MarkerOptions().position(new LatLng(poiarray.get(m).getLocation().getLat()
                        , poiarray.get(m).getLocation().getLng())).icon(BitmapDescriptorFactory.fromResource(R.drawable.hospitalicon)).title(poiarray.get(m).name).clusterable(true);

            } else {
                options = new MarkerOptions().position(new LatLng(poiarray.get(m).getLocation().getLat()
                        , poiarray.get(m).getLocation().getLng())).icon(BitmapDescriptorFactory.fromResource(R.drawable.pharmacyicon)).title(poiarray.get(m).name).clusterable(true);
            }
            hmap.setMarkersClustering(true);
            hmap.addMarker(options);
        }
        marray.clear();
    }
}