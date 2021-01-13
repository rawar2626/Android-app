package com.example.iist;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class connectToArdunio

{
    final int RECIEVE_MESSAGE = 1;
    BluetoothAdapter mBluetoothAdapter;
    private static final String TAG = "connectToArdunio";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    BluetoothDevice dispositivo;
    BluetoothDevice mmDevice;
    private ConnectedThread mConnectedThread;
    Context mContext;
    private ConnectThread mConnectThread;
    long nano_startTime,millis_startTime;

    //connects to the device's address and checks if it's available

    public connectToArdunio(Context context) {
        Log.d(TAG, "connectToArdunio Started");
        mContext = context;

    }
    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG, "startClient: Started.");
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        //dispositivo=device;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //mProgressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth","Please Wait...",true);
        mConnectThread = new ConnectThread(device, uuid);

    }


    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            Log.d(TAG, "ConnectThread: started.");
            mmDevice = device;
            run();
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.i(TAG, "RUN mConnectThread ");

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                Log.d(TAG, "ConnectThread: Trying to create secureRfcommSocket using UUID: "+mmDevice.getUuids()[0].getUuid());
                tmp = mmDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "ConnectThread: Could not create InsecureRfcommSocket " + e.getMessage());
            }

            mmSocket = tmp;


            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
                Log.d(TAG, "run: ConnectThread connected. YEss YEssssss");
            } catch (IOException e) {
                // Close the socket
                try {
                    mmSocket.close();
                    Log.d(TAG, "run: Closed Socket.");
                } catch (IOException e1) {
                    Log.e(TAG, "mConnectThread: run: Unable to close connection in socket " + e1.getMessage());
                }
                Log.d(TAG, "run: ConnectThread: Could not connect to UUID: " + MY_UUID );
            }

            //will talk about this in the 3rd video
            connected(mmSocket,mmDevice);
        }
        public void cancel() {
            try {
                Log.d(TAG, "cancel: Closing Client Socket.");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "cancel: close() of mmSocket in Connectthread failed. " + e.getMessage());
            }
        }
    }

    private void connected(BluetoothSocket mmSocket, BluetoothDevice mmDevice) {
        Log.d(TAG, "connected: Starting.");

        if(!mmSocket.isConnected()){
            Log.d(TAG, "connected:NOT Connected ");
        }
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(mmSocket);
        mConnectedThread.start();
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(TAG, "ConnectedThread: Starting.");

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            //dismiss the progress dialog when connection is established
            if(!mmSocket.isConnected())
            {
                Log.d(TAG, "ConnectedThread: Yes IT IS NOT");
            }


            try {
                tmpIn = mmSocket.getInputStream();
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Could not Get stream " + e.getMessage() );
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void run(){
            byte[] buffer = new byte[1024];  // buffer store for the stream

            int bytes; // bytes returned from read()
            Log.d(TAG, "Entered");

            //BufferedReader br;
            //br = new BufferedReader(new InputStreamReader(mmInStream));
            // Keep listening to the InputStream until an exception occurs
            while (true) {


                // Read from the InputStream=
                try {
                    bytes = mmInStream.available();

                    if(bytes != 0) {
                        bytes = mmInStream.read(buffer);
                        //String resp = br.readLine();
                        Log.d(TAG, "InputStream No_of_bytes " + bytes);
                        long nano_endTime = System.nanoTime();
                        long millis_endTime = System.currentTimeMillis();
                        System.out.println("Time taken in nano seconds: " + (nano_endTime - nano_startTime));
                        System.out.println("Time taken in milli seconds: " + (millis_endTime - millis_startTime));
                        String incomingMessage = new String(buffer, 0, bytes);
                        //String incomingMessage=resp;
                        //Log.d(TAG, "InputStream: " + incomingMessage);
                        //Toast.makeText(this, incomingMessage ,Toast.LENGTH_LONG).show();
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss ");
                        String currentTime = "time: " + sdf.format(new Date());

                        Intent inComingMessageIntent = new Intent("incomingMessage");
                        inComingMessageIntent.putExtra("the message", incomingMessage + "\n" + currentTime);
                        LocalBroadcastManager.getInstance(mContext).sendBroadcast(inComingMessageIntent);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Call this from the main activity to send data to the remote device
        public void write(byte[] bytes) {
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: Writing to outputstream: " + text);
            try {
                System.out.println(bytes.length+"  Time taken in milli seconds: ");
                nano_startTime = System.nanoTime();
                millis_startTime = System.currentTimeMillis();
                mmOutStream.write(bytes);

            } catch (IOException e) {
                Log.e(TAG, "write: Error writing to output stream. " + e.getMessage() );
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                Log.d(TAG, "Socket closed" );

                mmSocket.close();
            } catch (IOException e) { }
        }
    }


    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;

        // Synchronize a copy of the ConnectedThread
        Log.d(TAG, "write: Write Called.");
        //perform the write
        mConnectedThread.write(out);
    }




}
