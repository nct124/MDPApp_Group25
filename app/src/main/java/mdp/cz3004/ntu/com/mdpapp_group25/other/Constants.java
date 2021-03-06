package mdp.cz3004.ntu.com.mdpapp_group25.other;

/**
 * Created by n on 31/8/2017.
 */

public class Constants {
    // Message types sent from the RpiBluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    //tag for finalMDF
    public static final String MDF1tag = "mdf1";
    public static final String MDF2tag = "mdf2";
}
