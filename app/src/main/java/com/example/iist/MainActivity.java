package com.example.iist;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    //public static getmBluetoothConnection;
    TextView inputMessage,DeviceConnected;
    BluetoothAdapter BA;
    ToggleButton simpleToggleButton;
    Button findDevice,NewActivity;
    String s1,s2;
    Dialog showDeviceAllInformation,TextLimitDialog;
    //public ConnectionControl mBluetoothConnection;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String TGG= "MyApp";
    private ArrayAdapter<String> discoveredDevicesAdapter;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    ListView listView;
    StringBuilder message;
    RadioButton sendradioButton;

    /**
     * Broadcast Receiver that detects bond state changes (Pairing status changes)
     */
    private final BroadcastReceiver BondStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 cases:
                //case1: bonded already
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                    Log.d(TGG, "BroadcastReceiver: BOND_BONDED.");


                }
                //case2: creating a bone
                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TGG, "BroadcastReceiver: BOND_BONDING.");
                }
                //case3: breaking a bond
                if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TGG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DeviceConnected=findViewById(R.id.deivceNameConnected);
        //calling bluetooth Adapter
        BA = BluetoothAdapter.getDefaultAdapter();
        NewActivity= findViewById(R.id.sendReceive);
        //initiating find Button
        findDevice =findViewById(R.id.findButton);
        //initiate a toggle button
        simpleToggleButton =  findViewById(R.id.bluetooth_toggle);
        s1=Boolean.toString(simpleToggleButton.isChecked());
        Toast.makeText(getApplicationContext(),s1,Toast.LENGTH_SHORT).show();
        // Changing state of Toggle before start
        if(BA.isEnabled())
        { simpleToggleButton.setChecked(true);
            s1=Boolean.toString(simpleToggleButton.isChecked());
            Toast.makeText(getApplicationContext(),s1,Toast.LENGTH_SHORT).show();

            Log.d(TGG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }

        //Broadcasts when bond state changes (ie:pairing)
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(BondStatus, filter);

       // inputMessage =findViewById(R.id.Input_message);
        //message = new StringBuilder();
        //LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter("incomingMessage"));

        Log.d("END OF CODE", "outside of Main");

    }
    /*BroadcastReceiver mReceiver =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String Text=intent.getStringExtra("theMessage");
            message.setLength(0);
            message.append(Text);
            inputMessage.setText("");
            inputMessage.setText(message);
        }
    };*/
    //public void onClickSend(View v)
   // {
     //   s2 = inputMessage.getText().toString();
      //  Toast.makeText(this,s2,Toast.LENGTH_SHORT).show();
      //  mBluetoothConnection.write(bytes);
   // }
   // To change the state of Bluetooth
    public void bluetoothClick(View v)
    {
        boolean B =simpleToggleButton.isChecked();
        if(B)
        {   //enabling the bluetooth
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(this, "Turned on",Toast.LENGTH_LONG).show();
            // discovering devices
            Log.d(TGG, "btnEnableDisable_Discoverable: Making device discoverable for 300 seconds.");
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,300);
            startActivity(discoverableIntent);

        }
       else
        {  BA.disable();
            Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
        }

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void discoverDevice(View v ) {
        mBTDevices.clear();
        showDeviceAllInformation = new Dialog(MainActivity.this);
        showDeviceAllInformation.setContentView(R.layout.dialog_box);
        showDeviceAllInformation.setTitle("Bluetooth Devices");
        listView = showDeviceAllInformation.findViewById(R.id.discoverdDeviceList);
        ListView pairedList = showDeviceAllInformation.findViewById(R.id.pairedDeviceList);
        checkBTPermissions();
        // Paired Devices
        ArrayAdapter<String> pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        pairedList.setAdapter(pairedDevicesAdapter);
        // Adapater for discovering new device
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);


        Set<BluetoothDevice> pairedDevices = BA.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                pairedDevicesAdapter.notifyDataSetChanged();
            }
        } else {
            pairedDevicesAdapter.add(getString(R.string.none_paired));
            pairedDevicesAdapter.notifyDataSetChanged();

        }

        BA.startDiscovery();
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(discoveryFinishReceiver, filter);
        Log.d("Heeeee", "outside of discovery");
        //// Register for broadcasts when discovery has finished
        if (!BA.isDiscovering())
        {
            filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
            registerReceiver(discoveryFinishReceiver, filter);
            showDeviceAllInformation.show();
        }
        Log.d("Full out", "outside1111");


        //Handling listview item click event
        pairedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BA.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                connectToDevice(address);
                showDeviceAllInformation.dismiss();
            }

        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BA.cancelDiscovery();
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);
                Log.d("Inside click", " indie click");

                connectToDevice(address);
                showDeviceAllInformation.dismiss();
            }
        });



    }
    private void connectToDevice(String deviceAddress) {
        BA.cancelDiscovery();
        BluetoothDevice device = BA.getRemoteDevice(deviceAddress);
        //chatController.connect(device);
        Toast.makeText(this, device.getName(),Toast.LENGTH_LONG).show();
        Log.d(TGG, "onItemClick: deviceName = " + deviceAddress);
        Log.d(TGG, "onItemClick: You Clicked on a device.");
        String deviceName = device.getName();
        String Address = device.getAddress();
        DeviceConnected.setText(deviceName);
        Log.d(TGG, "onItemClick: deviceName = " + deviceName);
        Log.d(TGG, "onItemClick: deviceAddress = " + Address);
        //create the bond.
        //NOTE: Requires API 17+? I think this is JellyBean
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
            Log.d(TGG, "Trying to pair with " + deviceName);
            device.createBond();
        }
        Log.d(TGG, "startBTConnection: Initializing RFCOM Bluetooth Connection.");
        //mBluetoothConnection=  new ConnectionControl(MainActivity.this);
       // mBluetoothConnection.startClient(device,MY_UUID_INSECURE);
        //ChatBox.Connection(mBluetoothConnection);
        connectToArdunio M1= new connectToArdunio(MainActivity.this);
        M1.startClient(device,MY_UUID);
        ChatBox.Connection(M1);
    }


    final BroadcastReceiver discoveryFinishReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TGG , "I am logging something informational!");
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //mBTDevices.add(device);
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    discoveredDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                    Log.d("myTag", "This is my message 2");
                    Log.d("IF statement", "onReceive: " + device.getName() + ": " + device.getAddress());
                    listView.setAdapter(discoveredDevicesAdapter);
                    listView.deferNotifyDataSetChanged();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
            {
                if (discoveredDevicesAdapter.getCount() == 0) {
                    Log.d("outEND", "outside");
                    discoveredDevicesAdapter.add(getString(R.string.none_found));
                }


            }

        }

    };


    private void checkBTPermissions() {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {

                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(TGG, "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");
        }
    }

     public void sendReceiveActivity (View V)
     {     Log.d(TGG, "Getting  New activity Chat Box");
         Intent intent = new Intent(this, ChatBox.class);
         startActivity(intent);
     }


     public void textLimitSetDialog(View v)
     {
         TextLimitDialog = new Dialog(MainActivity.this);
         TextLimitDialog.setContentView(R.layout.text_limit_dialog);
         RadioGroup radioGroup;
         radioGroup =TextLimitDialog.findViewById(R.id.sendRadio);
         int selectedId = radioGroup.getCheckedRadioButtonId();
         sendradioButton = TextLimitDialog.findViewById(selectedId);

         if(selectedId==-1){
             Toast.makeText(MainActivity.this,"Nothing selected", Toast.LENGTH_SHORT).show();
         }
         else{
             Toast.makeText(MainActivity.this,sendradioButton.getText(), Toast.LENGTH_SHORT).show();
         }
     TextLimitDialog.show();
     }


}
