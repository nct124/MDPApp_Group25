package mdp.cz3004.ntu.com.mdpapp_group25.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import mdp.cz3004.ntu.com.mdpapp_group25.R;
import mdp.cz3004.ntu.com.mdpapp_group25.other.Constants;
import mdp.cz3004.ntu.com.mdpapp_group25.other.MazeCanvas;
import mdp.cz3004.ntu.com.mdpapp_group25.other.RpiBluetoothService;


public class MainActivity extends AppCompatActivity implements SensorEventListener {
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Key names received from the BluetoothCommandService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    //Auto/Manual
    Handler updateHandler;
    boolean update = false;
    int delay = 3000;//millisecond
    Runnable updateRunnable;

    //incoming/outgoing msg
    private ArrayAdapter<String> incomingmsg;
    private ArrayAdapter<String> outgoingmsg;

    //menu
    Menu menu;

    Toolbar deviceListMenu;
    // Name of the connected device
    private String mConnectedDeviceName = null;
    private String status = "Nil";
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for Bluetooth Command Service
    private RpiBluetoothService mCommandService = null;
    //context
    private Context mContext;

    // for accelerometer
    boolean motion = false;
    private float xValuesaved, yValuesaved, zValuesaved;
    private float xValue, yValue, zValue;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    public Vibrator v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
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
        MazeCanvas maze = (MazeCanvas)findViewById(R.id.maze);
        maze.invalidate();
        RadioGroup rp = (RadioGroup)findViewById(R.id.points);
        rp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                MazeCanvas maze = (MazeCanvas)findViewById(R.id.maze);
                TextView ctv = (TextView) findViewById(R.id.coorTV);
                TextView etv = (TextView) findViewById(R.id.errorTV);
                if(checkedId==findViewById(R.id.sp_button).getId()){
                    maze.rgIndex = maze.CP;
                }else if(checkedId==findViewById(R.id.wp_button).getId()){
                    maze.rgIndex = maze.WP;
                }else if(checkedId==findViewById(R.id.gp_button).getId()){
                    maze.rgIndex = maze.GP;
                }
                maze.setCoorTextView(ctv);
                maze.setErrorTextView(etv);
                Toast.makeText(mContext,"index:"+maze.rgIndex,Toast.LENGTH_LONG).show();
            }
        });
        ImageButton up = (ImageButton)findViewById(R.id.UpButton);
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MazeCanvas maze = (MazeCanvas)findViewById(R.id.maze);
                if(maze.sp!=null){
                    int number = maze.sp.toSingleArray();
                    switch(maze.direction){
                        case 0://N
                            number+=15;
                            break;
                        case 1://E
                            number+=1;
                            break;
                        case 2://S
                            number-=15;
                            break;
                        case 3://W
                            number-=1;
                            break;
                    }
                    status = "forward";
                    sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.forward),getString(R.string.forward)));
                    //maze.updateCP(number,maze.direction);
                    Toast.makeText(getApplicationContext(), "Forward", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Current position is not set", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ImageButton left = (ImageButton)findViewById(R.id.LeftButton);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MazeCanvas maze = (MazeCanvas)findViewById(R.id.maze);
                if(maze.sp!=null){
                    sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.turn_left),getString(R.string.turn_left)));
                    //maze.updateCP(maze.sp.toSingleArray(),maze.direction-1);
                    status = "turn left";
                    Toast.makeText(getApplicationContext(), "Turn Left", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Current position is not set", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageButton right = (ImageButton)findViewById(R.id.RightButton);
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MazeCanvas maze = (MazeCanvas)findViewById(R.id.maze);
                if(maze.sp!=null){
                    sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.turn_right),getString(R.string.turn_right)));
                    //maze.updateCP(maze.sp.toSingleArray(),maze.direction+1);
                    status = "turn right";
                    Toast.makeText(getApplicationContext(), "Turn Right", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Current position is not set", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*ImageButton back = (ImageButton)findViewById(R.id.BackButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MazeCanvas maze = (MazeCanvas)findViewById(R.id.maze);
                if(maze.sp!=null){
                    sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.back),getString(R.string.back)));
                    //maze.updateCP(maze.sp.toSingleArray(),maze.direction+2);
                    Toast.makeText(getApplicationContext(), "Turn Back", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Current position is not set", Toast.LENGTH_SHORT).show();
                }
            }
        });*/
        //remove later
        ((Button)findViewById(R.id.sendTxtBtn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText(((EditText)findViewById(R.id.sendTxtbox)).getText().toString());
            }
        });
        updateHandler = new Handler();
        updateRunnable = new Runnable(){
            public void run(){
                //do something
                sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.request_arena),getString(R.string.request_arena)));
                if(update){
                    updateHandler.postDelayed(this, delay);
                }
            }
        };
        incomingmsg = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        outgoingmsg = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        ListView incomingmsgListView = (ListView) findViewById(R.id.incomingmsg);
        incomingmsgListView.setAdapter(incomingmsg);
        ListView outgoingmsgListView = (ListView) findViewById(R.id.outgoingmsg);
        outgoingmsgListView.setAdapter(outgoingmsg);

        //String part1 = "FFC07F80FF01FE03FFFFFFF3FFE7FFCFFF9C7F38FE71FCE3F87FF0FFE1FFC3FF87FF0E0E1C1F";
        //String part2 = "00000100001C80000000001C0000080000060001C00000080000";
        //maze.updateMaze(part1,part2);

        // for accelerometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            // fail! we dont have an accelerometer!
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    public void sendText(String text){
        if(mCommandService.getState()!= mCommandService.STATE_CONNECTED){
            Toast.makeText(this,"Not Connected",Toast.LENGTH_LONG).show();
        }else {
            // Check that there's actually something to send
            if(text.length() > 0) {
                // Get the message bytes and tell the BluetoothChatService to write
                byte[] send = text.getBytes();
                mCommandService.write(send);
                updateLog(text,true);
            }
        }
    }
    private void receiveText(String text){
        Log.d("MAZE",text);
        updateLog(text,false);
    }
    private void updateLog(String text,boolean send){
        if(send){
            outgoingmsg.add(text);
        }else{
            incomingmsg.add(text);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        xValue = sensorEvent.values[0];
        yValue = sensorEvent.values[1];
        zValue = sensorEvent.values[2];
        if(motion){
            float xdiff = xValue-xValuesaved;
            float ydiff = yValue-yValuesaved;
            float zdiff = zValue-zValuesaved;
            if(xdiff<-6){//right
                sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.turn_right),getString(R.string.turn_right)));
                v.vibrate(50);
            }
            if(xdiff>6){//left
                sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.turn_left),getString(R.string.turn_left)));
                v.vibrate(50);
            }
            if(zdiff>6){//forward
                sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.forward),getString(R.string.forward)));
                v.vibrate(50);
            }
        }
        /*xValue = sensorEvent.values[0];
        yValue = sensorEvent.values[1];
        zValue = sensorEvent.values[2];

        if (yValue < 5 && yValue > 4.75) { //if (yValue > 4.5 && yValue < 5 && zValue > 8.5) {
            sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.forward),getString(R.string.forward)));
            v.vibrate(50);
        }
        if (xValue > 5 && xValue < 5.25) { //if (xValue > 5 && xValue < 5.5) {
            sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.turn_left),getString(R.string.turn_left)));
            v.vibrate(50);
        }
        if (xValue < -5 && xValue > -5.25) { //if (xValue < -5 && xValue > -5.5) {
            sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.turn_right),getString(R.string.turn_right)));
            v.vibrate(50);
        }*/

    }

    // do not delete
    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // not in use
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
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (mCommandService != null) {
            if (mCommandService.getState() == RpiBluetoothService.STATE_NONE) {
                mCommandService.start();
            }
        }
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mCommandService != null)
            mCommandService.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
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
            case R.id.manualBtn:
                if(update==false){//Manual->Auto//if(menu.findItem(R.id.manualBtn).getTitle().equals(getString(R.string.updateMode))){
                    updateHandler.postDelayed(updateRunnable,delay);
                    menu.findItem(R.id.manualBtn).setTitle(getString(R.string.updateMode));
                    menu.findItem(R.id.refreshBtn).setVisible(false);
                    update = true;
                }else{//Auto->Manual
                    updateHandler.removeCallbacks(updateRunnable);
                    menu.findItem(R.id.manualBtn).setTitle("Auto");
                    menu.findItem(R.id.refreshBtn).setVisible(true);
                    update = false;
                }
                return true;
            case R.id.logBtn:
                if(findViewById(R.id.linearOne).getVisibility()==View.VISIBLE){
                    findViewById(R.id.linearOne).setVisibility(View.GONE);
                    findViewById(R.id.relativeOne).setVisibility(View.GONE);
                    findViewById(R.id.logUI).setVisibility(View.VISIBLE);
                    menu.findItem(R.id.logBtn).setTitle("MainUI");
                }else{
                    findViewById(R.id.linearOne).setVisibility(View.VISIBLE);
                    findViewById(R.id.relativeOne).setVisibility(View.VISIBLE);
                    findViewById(R.id.logUI).setVisibility(View.GONE);
                    menu.findItem(R.id.logBtn).setTitle("Log");
                }
                return true;
            case R.id.reconfigBtn:
                //SHARED PREFERENCE BUTTON
                Intent intent = new Intent(mContext, StringCommandsActivity.class);
                startActivity(intent);
                return true;
            case R.id.refreshBtn:
                //manual refresh
                sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.request_arena),getString(R.string.request_arena)));
                return true;
            case R.id.exploreBtn:
                if(menu.findItem(R.id.exploreBtn).getTitle().equals(getString(R.string.startExplore))){//hasnt start exploring
                    sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.start_explore),getString(R.string.start_explore)));
                    menu.findItem(R.id.exploreBtn).setTitle(getString(R.string.stopExplore));
                }else{
                    sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.stop_explore),getString(R.string.stop_explore)));
                    menu.findItem(R.id.exploreBtn).setTitle(getString(R.string.startExplore));
                }
                //menu.findItem(R.id.manualBtn).setTitle("Auto");
                //sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.start_explore),getString(R.string.start_explore)));
                return true;
            case R.id.sspBtn:
                sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.start_shortest),getString(R.string.start_shortest)));
                return true;
            case R.id.motionBtn:
                if(motion==false){
                    motion = true;
                    xValuesaved = xValue;
                    yValuesaved = yValue;
                    zValuesaved = zValue;
                    menu.findItem(R.id.motionBtn).setTitle(getString(R.string.controller));
                }else{
                    motion = false;
                    menu.findItem(R.id.motionBtn).setTitle(getString(R.string.motion));
                }
                //menu.findItem(R.id.manualBtn).setTitle("Auto");
                //sendText(getApplicationContext().getSharedPreferences(getString(R.string.mdp_key),Context.MODE_PRIVATE).getString(getString(R.string.start_explore),getString(R.string.start_explore)));
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
                            deviceListMenu.setTitle("Status: "+status+"("+mConnectedDeviceName+")");
                            updateHandler.postDelayed(updateRunnable, delay);
                            update = true;
                            menu.findItem(R.id.manualBtn).setTitle(getString(R.string.updateMode));
                            break;
                        case RpiBluetoothService.STATE_CONNECTING:
                            deviceListMenu.setTitle("Connecting..");
                            break;
                        case RpiBluetoothService.STATE_LISTEN:

                        case RpiBluetoothService.STATE_NONE:
                            deviceListMenu.setTitle("Not connected to any device");
                            updateHandler.removeCallbacks(updateRunnable);
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
                    String [] msgArr = readMessage.split(",");
                    String mdf1 = msgArr[3];
                    String mdf2 = msgArr[4];
                    int cpcoor = Integer.parseInt(msgArr[1]);
                    int cpdirection = Integer.parseInt(msgArr[2]);
                    //status = msgArr[0];
                    deviceListMenu.setTitle("Status: "+status+"("+mConnectedDeviceName+")");
                    ((MazeCanvas)findViewById(R.id.maze)).updateMaze(mdf1,mdf2);
                    ((MazeCanvas)findViewById(R.id.maze)).updateCP(cpcoor,cpdirection);

                    break;
            }
        }
    };
    // END - DO NOT EDIT
}
