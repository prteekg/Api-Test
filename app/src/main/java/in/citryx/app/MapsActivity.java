package in.citryx.app;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    private GoogleApiClient mApiClientBuilder;
    private static final String TAG = "MAP_ACTIVITY", url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String key = "AIzaSyDO6J1H8DpR-mkRAgQfL9bte3S2kd7Q1G4";
    private Double mLatitude = null, mLongitude = null;
    private Location mLastLocation;
    private String type = null;
    private MapsActivity.DataFetcher dataFetcher;
    private IconFetcher iconFetcher;
    private LocationRequest mLocationRequest;
    private boolean locationEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        this.type = getIntent().getStringExtra("type");
        mapFragment.getMapAsync(this);
        if (mApiClientBuilder == null) {
            mApiClientBuilder = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) this)
                    .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onStart() {
        mApiClientBuilder.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mApiClientBuilder.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.style_json));
            if (!success)
                Log.e(TAG, "Style parsing failed.");
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Style not Found");
        }
        //LatLng sydney = new LatLng(Double.valueOf(mLatitude), Double.valueOf(mLongitude));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        fillMap();
    }

    public void fillMap() {
        Log.d(TAG, "fillmap");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.d(TAG, "Permission not granted");
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mApiClientBuilder);
        if(mLastLocation == null){
            Log.d(TAG, "mLastLocation is Null");
            mLocationRequest = LocationRequest.create();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(30 * 1000);
            mLocationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
            builder.setAlwaysShow(true);
            LocationServices.FusedLocationApi.requestLocationUpdates(mApiClientBuilder, mLocationRequest, this);
        }
        if (mLastLocation != null) {
            this.mLatitude = mLastLocation.getLatitude();
            this.mLongitude = mLastLocation.getLongitude();
            Log.i(TAG, "LAT = " + String.valueOf(mLatitude) + " LONG = " + String.valueOf(mLastLocation.getLongitude()));
            LatLng lastLatLng = new LatLng(mLatitude, mLongitude);
            mMap.addMarker(new MarkerOptions().position(lastLatLng).title("Current Location"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLatLng));
            mMap.setMinZoomPreference(10);
            if (type.equals("food")) {
                Foodie foodie = new Foodie();
                foodie.execute();
            }
            else {
                if(type.equals("railway+platform"))
                    mMap.setMinZoomPreference(11);
                else if(type.equals("bus+stops"))
                    mMap.setMinZoomPreference(15);
                dataFetcher = new DataFetcher();
                dataFetcher.execute();
                try {
                    String response = dataFetcher.get();
                    if (response != null) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                            JSONArray jsonArray = jsonObject.optJSONArray("results");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                                JSONObject geometry = (JSONObject) jsonObject1.get("geometry");
                                JSONObject location = (JSONObject) geometry.get("location");
                                Double lat = (Double) location.get("lat");
                                Double lng = (Double) location.get("lng");
                                String name = (String) jsonObject1.get("name");
                                Log.d(TAG, "lat = " + lat + " lng =" + lng + " name = " + name);
                                LatLng latLng = new LatLng(lat, lng);
                                String iconurl = (String) jsonObject1.get("icon");
                                Log.i(TAG, iconurl);
                                iconFetcher = new IconFetcher();
                                iconFetcher.execute(iconurl);
                                Bitmap icon = iconFetcher.get();
                                mMap.addMarker(new MarkerOptions().position(latLng).title(name).icon(BitmapDescriptorFactory.fromBitmap(icon)));
                            }
                        }

                    } else
                        Log.d(TAG, "NULL Response");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }else {
            Log.e(TAG, "Location Unavailable. Please Try Again");
            Toast.makeText(getApplicationContext(), "Location Unavailable. Please Try Again", Toast.LENGTH_SHORT).show();
            finish();
            //startActivity(getIntent());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.mLastLocation = location;
    }

    public class DataFetcher extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            StringBuffer result = null;
            try {
                //"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=18.4533691,73.8217453&radius=50000&type=airport&keyword=airport&rankby=prominence&key=AIzaSyDO6J1H8DpR-mkRAgQfL9bte3S2kd7Q1G4"
                URL url = new URL(MapsActivity
                        .url + "location=" + mLatitude + "," + mLongitude
                        + "&radius=" + 50000 + "&type=" + type + "&keyword=" + type + "&rankby=prominence" + "&key=" + MapsActivity.key);
                HttpURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = null;
                result = new StringBuffer();
                while ((line = bufferedReader.readLine()) != null) result.append(line);
                Log.d(TAG, result.toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result.toString();
        }
    }

    public class IconFetcher extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Log.d("iconfetcher", params[0]);
            try {
                Log.d("iconfetcher", params[0]);
                URL url = new URL(params[0]);
                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                return bmp;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class Foodie extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... params) {
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            String url = "https://developers.zomato.com/api/v2.1/geocode?lat="+mLatitude+"&lon="+mLongitude;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //Log.d(TAG, response);
                    mMap.setMinZoomPreference(15);
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray nearby_restaurants = jsonObject.optJSONArray("nearby_restaurants");
                        for(int i=0; i<nearby_restaurants.length(); i++){
                            JSONObject restarauntObject = nearby_restaurants.getJSONObject(i);
                            JSONObject restaurant = restarauntObject.getJSONObject("restaurant");
                            String name = restaurant.getString("name");
                            JSONObject location = (JSONObject) restaurant.get("location");
                            Double lat = location.getDouble("latitude");
                            Double lng = location.getDouble("longitude");
                            mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(name));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Accept:", "application/json");
                    params.put("user-key", "bfcebfc8a8c4bbc49f04009b72f4ed53");
                    return params;
                }
            };
            requestQueue.add(stringRequest);
            return null;
        }
    }
}
