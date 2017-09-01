package mdp.cz3004.ntu.com.mdpapp_group25.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import mdp.cz3004.ntu.com.mdpapp_group25.R;
import mdp.cz3004.ntu.com.mdpapp_group25.other.Constants;
import mdp.cz3004.ntu.com.mdpapp_group25.other.MazeCanvas;
import mdp.cz3004.ntu.com.mdpapp_group25.other.RpiBluetoothService;

public class MainActivity extends AppCompatActivity {
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Key names received from the BluetoothCommandService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    Toolbar deviceListMenu;
    //TableLayout msgTable;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for Bluetooth Command Service
    private RpiBluetoothService mCommandService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        //toolbar
        deviceListMenu = (Toolbar) findViewById(R.id.mainMenu);
        deviceListMenu.setTitle("List of BlueTooth Device");
        setSupportActionBar(deviceListMenu);
        //msgTable = (TableLayout) findViewById(R.id.msgTable);
        Button sendTextBtn = (Button)findViewById(R.id.sendText);
        sendTextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText textBox = (EditText)findViewById(R.id.textToBeSent);
                String txt = textBox.getText().toString();
                sendText(txt);
                textBox.setText("");
            }
        });
        MazeCanvas maze = (MazeCanvas)findViewById(R.id.maze);
        maze.invalidate();
    }

    private void sendText(String text){
        if(mCommandService.getState()!= mCommandService.STATE_CONNECTED){
            Toast.makeText(this,"Not Connected",Toast.LENGTH_LONG).show();
        }else {
            // Check that there's actually something to send
            if (text.length() > 0) {
                // Get the message bytes and tell the BluetoothChatService to write
                byte[] send = text.getBytes();
                mCommandService.write(send);
                updateTable(text,true);
            }
        }
    }
    private void receiveText(String text){
        updateTable(text,false);
    }
    private void updateTable(String text,boolean send){
        TableRow tr = new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        if(send){
            //send
            tv.setGravity(Gravity.RIGHT);
        }else{
            //receive
            tv.setGravity(Gravity.LEFT);
        }

        tr.addView(tv);
        //msgTable.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
    }


    // START - DO NOT EDIT
    @Override
    protected void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupCommand() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        }
        // otherwise set up the command service
        else {
            if (mCommandService==null)
                setupCommand();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCommandService != null) {
            if (mCommandService.getState() == RpiBluetoothService.STATE_NONE) {
                mCommandService.start();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCommandService != null)
            mCommandService.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.findBTBtn:
                Intent serverIntent = new Intent(this, BlueToothDeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(BlueToothDeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mCommandService.connect(device,true);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupCommand();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Toast.makeText(this, "BlueTooth is not enabled", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void setupCommand() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mCommandService = new RpiBluetoothService(this, mHandler);
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //when bluetooth device status change
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case RpiBluetoothService.STATE_CONNECTED:
                            deviceListMenu.setTitle("Connected to "+mConnectedDeviceName);
                            break;
                        case RpiBluetoothService.STATE_CONNECTING:
                            deviceListMenu.setTitle("Connecting..");
                            break;
                        case RpiBluetoothService.STATE_LISTEN:

                        case RpiBluetoothService.STATE_NONE:
                            deviceListMenu.setTitle("Not connected to any device");
                            break;
                    }
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                //when a msg comes in
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    receiveText(readMessage);
            }
        }
    };
    // END - DO NOT EDIT
}
