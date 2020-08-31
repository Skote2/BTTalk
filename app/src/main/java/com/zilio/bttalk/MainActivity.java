// This is a simple Bluetooth app intended to allow for basic point to point chat capability.
// I'm basing this code off of the documentation and the template 'BluetoothChat' (which is on API version 28 so I can't use it 1 to 1 on my android 7 device)
package com.zilio.bttalk;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    // Constants
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            setBluetoothPermissions();
        }
    }

    /////////////
    // Buttons //
    /////////////

    //exit
    public void onClickLeft(View view){
        finishAndRemoveTask();
    }

    //retry permissions
    public void onClickRight(View view){
        setBluetoothPermissions();
    }

    ///////////////////////
    // Utility Functions //
    ///////////////////////

    // If succeeds moves to connection page
    private void setBluetoothPermissions(){
        //Check if bluetooth is supported and get the Adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) finishAndRemoveTask();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else
            startActivity(new Intent(this, activity_connect.class));
    }

//    @Override
//    protected void onActivityResult(int request, int result, Intent intent){
//        super.onActivityResult(request, result, intent);
//
//        if (request == REQUEST_ENABLE_BT){
//            if (result == RESULT_OK)
//                startActivity(new Intent(this, activity_connect.class));
////            switch (result) {
////                case RESULT_OK:
////                    startActivity(new Intent(this, activity_connect.class));
////                    break;
////                case RESULT_CANCELED:
////                    //sit on page and wait for an answer (do nothing)
////                    break;
////            }
//        }
//    }
}