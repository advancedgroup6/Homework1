package swati.example.com.messageme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.observation.region.beacon.BeaconRegion;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.estimote.coresdk.service.BeaconManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class InboxActivity extends AppCompatActivity implements GetMsgAsyncTask.MessageDetails {

    private ArrayList<Message> msgList;
    private RecyclerView mRecyclerView;
    private RecyclerAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private BeaconManager beaconManager;
    private BeaconRegion region;
    private static final Map<String, String> AISLE_BY_BEACONS;
    private static Map<String, Double> lastReceivedBeacons;
    private static Beacon lastBeacon;
    private static Beacon currentBeacon;
    private static int time;
    private static int noofTimesDifferent;

    static {
        Map<String, String> placesByBeacons = new HashMap<>();
        placesByBeacons.put("15212:31506", "Region1");
        placesByBeacons.put("48071:25324", "Region2");
        placesByBeacons.put("26535:44799", "Region3");
        AISLE_BY_BEACONS = Collections.unmodifiableMap(placesByBeacons);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.TOKEN_PREFS, MODE_PRIVATE);
        final String username = sharedPref.getString("Username",null);
        final String token = sharedPref.getString("JWTToken",null);
        new GetMsgAsyncTask(InboxActivity.this).execute(MainActivity.IPAdress+"getMessages/",username,token,"getMsgs");
        beaconManager = new BeaconManager(this);
        beaconManager.setForegroundScanPeriod(2000,2000);
        time = -2;
        noofTimesDifferent = 0;
        beaconManager.setRangingListener(new BeaconManager.BeaconRangingListener() {

            @Override
            public void onBeaconsDiscovered(BeaconRegion beaconRegion, List<Beacon> beacons) {
                time = time +2;
                Log.d("Time:",time+"");
                boolean skipAddToMap = false;
                if (!beacons.isEmpty()) {
                    Log.d("Beacons:",beacons.size()+"");
                    Beacon nearestBeacon = beacons.get(0);
                    if(lastReceivedBeacons == null){
                        lastReceivedBeacons = new HashMap<String, Double>();
                        lastBeacon = nearestBeacon;
                        currentBeacon = nearestBeacon;
                    } else {
                        //nearest == current
                      if(nearestBeacon.getMajor() == currentBeacon.getMajor() && nearestBeacon.getMinor() == currentBeacon.getMinor()){

                      } else {
                          //Nearest != Current

                          if(nearestBeacon.getMajor() == lastBeacon.getMajor() && nearestBeacon.getMinor() == lastBeacon.getMinor()){
                              noofTimesDifferent = noofTimesDifferent+1;
                              skipAddToMap = true;
                          } else {
                              noofTimesDifferent = 1;
                              lastBeacon = nearestBeacon;
                          }

                          //Check distance and Switch when new beacon is same twice
                          if(noofTimesDifferent>=2) {
                              Double distance = getDistance(nearestBeacon.getMeasuredPower(),nearestBeacon.getRssi());
                              Double lastNearsetDistance = lastReceivedBeacons.get(nearestBeacon.getMajor()+nearestBeacon.getMinor()+"");
                              if(distance!=null && lastNearsetDistance!=null){
                                  Double changedDistance = Math.abs(lastNearsetDistance-distance);
                                  if(changedDistance > 1){
                                      time = 0;
                                      noofTimesDifferent = 0;
                                      currentBeacon = nearestBeacon;
                                  }
                              }
                          }
                        }
                    }
                    if(time>9){
                        String region = currentBeacon.getMajor()+""+currentBeacon.getMinor();
                        Log.d("Region:",region);
                        new GetMsgAsyncTask(InboxActivity.this).execute(MainActivity.IPAdress,username,token,"updateMsgs",region);
                        time=0;
                    }
                    if(!skipAddToMap) {
                        for(Beacon beacon : beacons){
                            Log.d("Beacons:",beacon.getMajor()+"::"+beacon.getMinor());
                            lastReceivedBeacons.put(beacon.getMajor()+beacon.getMinor()+"",getDistance(beacon.getMeasuredPower(),beacon.getRssi()));
                        }
                    }

                }
            }
        });

        //region = new BeaconRegion("ranged region", UUID.fromString("11111111-1111-1111-1111-111111111111"), null, null);
        region = new BeaconRegion("ranged region", UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);

        ImageButton composeBtn = (ImageButton) findViewById(R.id.btnCompose);
        composeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InboxActivity.this,ComposeActivity.class);
                startActivity(intent);
            }
        });
    }

    private double getDistance(int measuredPower, int rssi) {
        return Math.pow(10d, ((double) measuredPower - rssi) / (10 * 2));
    }

    private void productsNearBeacon(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (AISLE_BY_BEACONS.containsKey(beaconKey)) {
        }
    }

    public void setMessageDetails(ArrayList<Message> details){
        if(details!=null && details.size()>0){
            mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            mLinearLayoutManager = new LinearLayoutManager(this);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mAdapter = new RecyclerAdapter(details);
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.setLayoutManager(mLinearLayoutManager);
        } else{
            Toast.makeText(InboxActivity.this,"No messages to display.",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public Context getContext() {
        return null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }

    public void login(){
        Toast.makeText(InboxActivity.this,"User session expired. Please login to continue.",Toast.LENGTH_SHORT).show();
        SharedPreferences settings = this.getSharedPreferences(MainActivity.TOKEN_PREFS,Context.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("Username",null);
        prefEditor.putString("JWTToken",null);
        prefEditor.commit();
        Intent intent = new Intent(InboxActivity.this,MainActivity.class);
        startActivity(intent);
    }




}
