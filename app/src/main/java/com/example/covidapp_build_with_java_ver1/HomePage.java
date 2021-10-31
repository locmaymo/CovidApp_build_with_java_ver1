package com.example.covidapp_build_with_java_ver1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class HomePage extends AppCompatActivity {
    private ImageView learnMoreImage;

    //final String Corona_Url = "https://disease.sh/v3/covid-19/all";
    final String Corona_Url = "https://disease.sh/v3/covid-19/countries/";
    final long MIN_TIME = 5000;
    final float MIN_DISTANCE = 1000;
    final int REQUEST_CODE = 101;

    String Location_Provider = LocationManager.GPS_PROVIDER;

    TextView CriticalCase, DeathsCase, Recovered, ActiveCases;
    ImageView CoronaIcon;

//    RelativeLayout mCityFinder;


    LocationManager mLocationManager;
    LocationListener mLocationListner;
    private Button btnSalir;

    private FirebaseAuth mAuth;

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        learnMoreImage = (ImageView) findViewById(R.id.imageView);

        learnMoreImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePage.this, Symptom.class);
                startActivity(intent);
            }
        });
        Recovered = findViewById(R.id.Recovered);
        DeathsCase = findViewById(R.id.DeathsCase);
//        CoronaIcon = findViewById(R.id.CoronaIcon);
//        mCityFinder = findViewById(R.id.cityFinder);
        CriticalCase = findViewById(R.id.TotalCases);
        ActiveCases = findViewById(R.id.ActiveCases);

        bottomNavigationView  = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId())
                {
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_search:
                        startActivity(new Intent(getApplicationContext(),cityFinder.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.nav_symptom:
                        startActivity(new Intent(getApplicationContext(),Symptom.class));
                        overridePendingTransition(0,0);
                        return true;

                }
                return false;
            }
        });

//        mCityFinder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(HomePage.this, cityFinder.class);
//                startActivity(intent);
//            }
//        });
        btnSalir = findViewById(R.id.btnSalir);

        mAuth = FirebaseAuth.getInstance();

        btnSalir.setOnClickListener(view -> {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));

        });

    }
    @Override
    protected void onResume() {
        super.onResume();
        Intent mIntent=getIntent();
        String city= mIntent.getStringExtra("City");
        if(city!=null)
        {
            getCoronaDataForNewCity(city);
        }
        else
        {
            getCoronaDataForCurrentLocation();
        }


    }
    private void getCoronaDataForNewCity(String city)
    {
        letsdoSomeNetworking(city);
    }

    private void getCoronaDataForCurrentLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListner = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                letsdoSomeNetworking("VietNam");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
            return;
        }
        mLocationManager.requestLocationUpdates(Location_Provider, MIN_TIME, MIN_DISTANCE, mLocationListner);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if(requestCode==REQUEST_CODE)
        {
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(HomePage.this,"Locationget Succesffully",Toast.LENGTH_SHORT).show();
                getCoronaDataForCurrentLocation();
            }
            else
            {
                //user denied the permission
            }
        }


    }
    // private  void letsdoSomeNetworking(RequestParams params)
    private  void letsdoSomeNetworking(String city)
    {
        AsyncHttpClient client = new AsyncHttpClient();
        String newUrl = Corona_Url+city;
        client.get(newUrl,new JsonHttpResponseHandler()
        {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                Toast.makeText(HomePage.this,"Data Get Success",Toast.LENGTH_SHORT).show();

                CoronaData coronaData = CoronaData.fromJson(response);

                updateUI(coronaData);


                // super.onSuccess(statusCode, headers, response);
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                //super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });



    }
    private  void updateUI(CoronaData coronaData){


        Recovered.setText(coronaData.getRecoveredCase());
        CriticalCase.setText(coronaData.getmCriticalCase());
        DeathsCase.setText(coronaData.getDeathsCase());
        ActiveCases.setText(coronaData.getmActive());
        //    CoronaIcon.setImageResource(resourceID);

    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mLocationManager!=null)
        {
            mLocationManager.removeUpdates(mLocationListner);
        }
    }


}