package com.example.yajue.mbientlabsensorapp;


import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.*;
import android.os.IBinder;
import android.util.Log;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
//import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.android.BtleService;

//import static com.mbientlab.metawear.MetaWearBoard.ConnectionStateHandler;
//import static com.mbientlab.metawear.AsyncOperation.CompletionHandler;
import android.view.View;
import android.widget.Button;

import bolts.Continuation;
import bolts.Task;


public class MainActivity extends AppCompatActivity implements ServiceConnection {

        private BtleService.LocalBinder serviceBinder;
        private static final String TAG = "MetaWear";
        private final String MW_MAC_ADDRESS= "E9:78:11:5F:58:A6"; //update with your board's MAC address
        private MetaWearBoard mwBoard;
        private Button connect;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            ///< Bind the service when the activity is created
            getApplicationContext().bindService(new Intent(this, BtleService.class),
                    this, Context.BIND_AUTO_CREATE);
            Log.i(TAG, "log test");
            connect=(Button)findViewById(R.id.connect);
            connect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "Clicked connect");
                    //mwBoard.connect(); //.connect() and .disconnect() are how we control connection state
                    mwBoard.connectAsync().continueWith(new Continuation<Void, Void>() {
                        @Override
                        public Void then(Task<Void> task) throws Exception {
                            if (task.isFaulted()) {
                                Log.i("MainActivity", "Failed to connect");
                            } else {
                                Log.i("MainActivity", "Connected");
                            }
                            return null;
                        }
                    });
                }
            });
        }

        @Override
        public void onDestroy() {
            super.onDestroy();

            ///< Unbind the service when the activity is destroyed
            getApplicationContext().unbindService(this);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ///< Typecast the binder to the service's LocalBinder class
            serviceBinder = (BtleService.LocalBinder) service;
            retrieveBoard();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) { }
        public void retrieveBoard() {
            final BluetoothManager btManager=
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            final BluetoothDevice remoteDevice=
                    btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

            // Create a MetaWear board object for the Bluetooth Device
            mwBoard= serviceBinder.getMetaWearBoard(remoteDevice);
        }
}
