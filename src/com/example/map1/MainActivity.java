package com.example.map1;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Dialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements 
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{

	private static final int GPS_ERRORDIALOG_REQUEST = 9001;
	GoogleMap mMap;
	Marker marker;
	Circle shape1;
	
	private static final double SATTLE_LAT=47.60621,
	SATTLE_LNG=-122.33207,
	SYDNEY_LAT=-33.867487, 
	SYDNEY_LNG=151.20699;
	
	private static final float DEFAULTZOOM=5; 
	@SuppressWarnings("unused")
	private static final String LOGTAG="Maps";
	
	LocationClient mLocationClient;
	Marker marker1;
	Marker marker2;
	Polyline line;
	ArrayList<Marker> markers = new ArrayList<Marker>();
	static final int POLYGON_POINTS=3;
	Polygon shape;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (servicesOK()) {
			
			setContentView(R.layout.activity_map);
			
		if (initMap()) {
			Toast.makeText(this, "Ready to map!", Toast.LENGTH_SHORT).show();
			//gotoLocation(SATTLE_LAT,SATTLE_LNG,DEFAULTZOOM);
			//mMap.setMyLocationEnabled(true);
			mLocationClient= new LocationClient(this, this, this);
			mLocationClient.connect();
			
			
			
		}	
		else{
			Toast.makeText(this, " map not available!", Toast.LENGTH_SHORT).show();
		}
			
			
		}
		else {
			setContentView(R.layout.activity_main);
		}
		
	}

	public boolean servicesOK() {
		int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		
		if (isAvailable == ConnectionResult.SUCCESS) {
			return true;
		}
		else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
			Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
			dialog.show();
		}
		else {
			Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
		}
		return false;
	}
	
	
	private void gotoLocation(double lat, double lng,
			float zoom) {
// TODO Auto-generated method stub
		
		LatLng ll = new LatLng(lat,lng);
		CameraUpdate update= CameraUpdateFactory.newLatLngZoom(ll, zoom);
		mMap.moveCamera(update); 
		
	}
	
	private void  gotoLocation(double lat, double lng){
		
		LatLng ll = new LatLng(lat,lng);
		CameraUpdate update= CameraUpdateFactory.newLatLng(ll);
		mMap.moveCamera(update); 
		
		
	}
	private boolean initMap(){
		if(mMap==null){
			SupportMapFragment mapFrag=
			(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
			mMap= mapFrag.getMap();
			
			if (mMap!= null) {
				mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
					
					@Override
					public View getInfoWindow(Marker arg0) {
						
						return null;
					}
					
						@Override
					public View getInfoContents(Marker marker) {
						View v = getLayoutInflater().inflate(R.layout.info_window,null);
						TextView tvLocality = (TextView) v.findViewById(R.id.tv_locality);
						TextView tvLat = (TextView) v.findViewById(R.id.tv_lat);
						TextView tvLng = (TextView) v.findViewById(R.id.tv_lng);
						TextView tvSnippet = (TextView) v.findViewById(R.id.tv_snippet);
						
						LatLng ll=marker.getPosition();
						
						tvLocality.setText(marker.getTitle());
						tvLat.setText("Latitude: "+ ll.latitude);
						tvLng.setText("Longitude: "+ ll.longitude);
						tvSnippet.setText(marker.getSnippet());
						
						return v;
				
						
					} 
				});
				
				mMap.setOnMapLongClickListener(new OnMapLongClickListener() {
					
					@Override
					public void onMapLongClick(LatLng ll) {
						// TODO Auto-generated method stub
						
						Geocoder gc= new Geocoder(MainActivity.this);
						List<Address> list= null;
						
				
						
					
					  try {
						list= gc.getFromLocation(ll.latitude,ll.longitude,1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return;
					}
						
					 Address add= list.get(0);
					 MainActivity.this.setMarker(add.getLocality(), add.getCountryName(), 
							ll.latitude  , ll.longitude);
						
						
					}
				});
				mMap.setOnMarkerClickListener(new OnMarkerClickListener() {
					
					@Override
					public boolean onMarkerClick(Marker arg0) {
						// TODO Auto-generated method stub
						String msg= marker.getTitle()+ " ( " + marker.getPosition().latitude+","+ marker.getPosition().longitude+ ")";
						Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
						return false;
					}
				});
				
		   mMap.setOnMarkerDragListener(new OnMarkerDragListener() {
			
			@Override
			public void onMarkerDragStart(Marker arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onMarkerDragEnd(Marker marker) {
				Geocoder gc= new Geocoder(MainActivity.this);
				List<Address> list= null;
				
		
			LatLng ll =marker.getPosition();
			
			  try {
				list= gc.getFromLocation(ll.latitude,ll.longitude,1);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return;
			}
				
			 Address add= list.get(0);
			 marker.setTitle(add.getLocality());
			 marker.setSnippet(add.getCountryName());
			 marker.showInfoWindow();
		
				
				
				
			}
			
			@Override
			public void onMarkerDrag(Marker arg0) {
				// TODO Auto-generated method stub
				
			}
		}) ;
				
			}
					
		} 
		return (mMap!= null);
		
	}
		
	public void geoLocate(View v) throws IOException {

		EditText et = (EditText) findViewById(R.id.editText1);
		String location = et.getText().toString();
		if (location.length() == 0) {
			Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show();
			return;
		}

		hideSoftKeyboard(v);
		
		Geocoder gc = new Geocoder(this);
		List<Address> list = gc.getFromLocationName(location, 1);
		Address add = list.get(0);
		String locality = add.getLocality();
		Toast.makeText(this, locality, Toast.LENGTH_LONG).show();

		double lat = add.getLatitude();
		double lng = add.getLongitude();

		gotoLocation(lat, lng, DEFAULTZOOM); 
		if (marker!=null) {
			marker.remove();
		}
		MarkerOptions options = new MarkerOptions()
		.title(locality)
		.position(new LatLng(lat, lng));
         marker=mMap.addMarker(options);
         
         setMarker(locality, add.getCountryName(), lat, lng);
		
	}
	
	private void hideSoftKeyboard(View v) {
		InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
   
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.mapTypeNone:
			mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
			break;
		case R.id.mapTypeNormal:
			mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			break;
		case R.id.mapTypeSatellite:
			mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			break;
		case R.id.mapTypeTerrain:
			mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
			break;
		case R.id.mapTypeHybrid:
			mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			break;
		case R.id.gotoCurrentLocation:
			gotoCurrentLocation();
			break;
		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}
	
	protected void gotoCurrentLocation() {
		Location currentLocation = mLocationClient.getLastLocation();
		if (currentLocation == null) {
			Toast.makeText(this, "Current location isn't available", Toast.LENGTH_SHORT).show();
		}
		else {
			LatLng ll = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
			CameraUpdate update = CameraUpdateFactory.newLatLngZoom(ll, DEFAULTZOOM);
			mMap.animateCamera(update);
		}
	}
   
  private void setMarker(String locality, String country, double lat, double lng) {
	  LatLng ll= new LatLng(lat, lng);
	   
		if (marker != null) {
			marker.remove();
		} 

		MarkerOptions options = new MarkerOptions()	
			.title(locality)
			.position(ll)
			.anchor(.5f, .5f)
			.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_plaidmarker))
			.draggable(true);
		//.icon(BitmapDescriptorFactory.defaultMarker(
			//	BitmapDescriptorFactory.HUE_ORANGE));//
		if(country.length()>0){
			options.snippet(country);
		} 
		
		if (marker!=null){
			removeEverything();
		}
		//markers.add(mMap.addMarker(options));
		marker = mMap.addMarker(options);
		
		shape1 = drawCircle(ll);
		
		
		// to draw polygon
	/*	if(markers.size()== POLYGON_POINTS){
			removeEverything();
			
		}
		if (markers.size()== POLYGON_POINTS) {
			drawPolygon();*/
			
		}
	//to draw line
		/*if (marker1==null){
			marker1=mMap.addMarker(options);
		}
		else if (marker2==null){
			marker2=mMap.addMarker(options);
			drawline(); 
		}
		else{
			removeEverything();
			marker1=mMap.addMarker(options);
		}*/
		
		
	
  
  /* draw a line
   
     private void drawline() {
	// TODO Auto-generated method stub
    	 PolylineOptions options= new PolylineOptions()
    	 .add(marker1.getPosition())
    	 .add(marker2.getPosition())
    	 .color(Color.BLUE)
    	 .width(5);
    	  line = mMap.addPolyline(options);
    	 
    	// mMap.addPolyline(options);
    	 
	
}

	/*private void removeEverything() {
		marker1.remove();
		marker1 = null;
		marker2.remove();
		marker2 = null;
		line.remove();
	} */

  
  

private Circle drawCircle(LatLng ll) {
	// TODO Auto-generated method stub
	  CircleOptions options = new CircleOptions()
       .center(ll)
       .radius(1000)
       .fillColor(0x3300ff)
       .strokeColor(Color.BLUE)
       .strokeWidth(3);
	return mMap.addCircle(options);
}

private void removeEverything() {
	// TODO Auto-generated method stub
	marker.remove();
	marker= null;
	shape1.remove();
	shape1=null;
	
	
}
/* draw polygon */
 /* private void drawPolygon(){
		PolygonOptions options = new PolygonOptions()
		.fillColor(0x33000ff)
		.strokeWidth(3)
		.strokeColor(Color.BLUE);
		
		for (int i = 0; i < POLYGON_POINTS; i++) {
		    options.add(markers.get(i).getPosition());
			
		}
		shape = mMap.addPolygon(options);
		
	}
	
	
	private void removeEverything(){
		 
		
		for(Marker marker: markers){
			marker.remove();
		}
		markers.clear();
		shape.remove();
		shape=null;
		
	} */
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
		Toast.makeText(this,"Connected to location Service",Toast.LENGTH_SHORT).show();
		LocationRequest request= LocationRequest.create();
		request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		request.setInterval(5000);
		request.setFastestInterval(1000);
		mLocationClient.requestLocationUpdates(request, this);
	}
	@Override
	public void onDisconnected() {
	
		}
	@Override
	public void onLocationChanged(Location location) {
		
		String msg ="Location:" + location.getLatitude() + ","+ location.getLongitude();
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();	
	}
	
	

}

