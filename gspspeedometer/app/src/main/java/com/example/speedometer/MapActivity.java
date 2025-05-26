package com.example.speedometer;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;
import androidx.fragment.app.FragmentActivity;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private List<LocationPoint> locationPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);


        locationPoints = (List<LocationPoint>) getIntent().getSerializableExtra("locations");


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);  // Bu satırda DataBinding kullanılmıyor.
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        if (locationPoints == null || locationPoints.isEmpty()) {
            Toast.makeText(this, "Konum bilgisi yok.", Toast.LENGTH_SHORT).show();
            return;
        }


        PolylineOptions polylineOptions = new PolylineOptions()
                .width(8)
                .color(Color.BLUE);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();


        for (LocationPoint point : locationPoints) {
            LatLng latLng = new LatLng(point.latitude, point.longitude);
            polylineOptions.add(latLng);
            builder.include(latLng);
        }


        mMap.addPolyline(polylineOptions);


        LatLngBounds bounds = builder.build();
        int padding = 100;
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
    }
}
