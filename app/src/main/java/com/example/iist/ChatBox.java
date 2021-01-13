package com.example.iist;

import android.app.Notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.icu.text.SimpleDateFormat;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatBox extends AppCompatActivity
{
    private static final String TAG ="Chat Box" ;
    ListView sendMessage,receivedMessage;
    ArrayAdapter<String> sendMessageAdapter;
    ArrayAdapter<String> receivedMessageAdapter;
    List<Integer> PositionToDelete = new ArrayList<Integer>();
    TextView InputMessage;
    String s1,s2;
    StringBuilder message;
    ArrayList<String> temp = new ArrayList<String>();
    static connectToArdunio mBluetoothConnection;
    int count=0;
    View child;
    NotificationManagerCompat notificationManagerCompat;
    public static void Connection(connectToArdunio Connection) {
        mBluetoothConnection=Connection;
        Log.d(TAG, "Connection received AC 2");
        if(mBluetoothConnection==null)
        {Log.d(TAG,"None");}
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatbox);
        notificationManagerCompat=NotificationManagerCompat.from(this);
        receivedMessage= findViewById(R.id.receivedMessagelist);
        sendMessage =findViewById(R.id.sendMessgeList);
        sendMessageAdapter=new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        receivedMessageAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1);
        sendMessage.setAdapter(sendMessageAdapter);
        receivedMessage.setAdapter(receivedMessageAdapter);
        InputMessage =  findViewById(R.id.input_Message);
        message = new StringBuilder();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,new IntentFilter("incomingMessage"));
       receivedMessage.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
       receivedMessage.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                View child = receivedMessage.getChildAt(position);
                if (checked==true) {
                    Log.d(TAG, "1" + "Adding " + position);
                    PositionToDelete.add(position);
                    temp.add(receivedMessageAdapter.getItem(position));
                    //sendMessage.SetItemChecked(position, true);
                    //sendMessage.setSelection(position);
                    receivedMessageAdapter.notifyDataSetChanged();
                    child.setBackground(getResources().getDrawable(R.drawable.pressed));
                    receivedMessageAdapter.notifyDataSetChanged();
                    count=count+1;
                    mode.setTitle(count+"Item selected");
                }
                else if (checked==false)
                {   PositionToDelete.remove(position);
                    temp.remove(receivedMessageAdapter.getItem(position));
                    child.setBackgroundColor(getResources().getColor(R.color.White));
                    receivedMessageAdapter.notifyDataSetChanged();
                    count=count-1;
                    mode.setTitle(count+"Item selected");
                }
            }

            @Override
            //long press
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                Log.d(TAG, "2");
                count=0;
                mode.setTitle(count+"Item selected");
                MenuInflater inflator = mode.getMenuInflater();
                inflator.inflate(R.menu.my_content_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                Log.d(TAG, "3");
                return false;
            }

            @Override
            // on selecting item form menu
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Log.d(TAG, "4");
                for (Integer number : PositionToDelete) {
                    child = receivedMessage.getChildAt(number);
                    child.setBackgroundColor(getResources().getColor(R.color.White));
                    receivedMessageAdapter.notifyDataSetChanged();

                }

                switch(item.getItemId())
                {
                    case R.id.delete_id:
                        for (String msg : temp) {
                            Log.d(TAG, "10");
                            receivedMessageAdapter.remove(msg);
                            receivedMessageAdapter.notifyDataSetChanged();
                        }
                        count = 0;
                        mode.finish();
                        return true;
                    default:
                        return false;
                }

            }
            @Override
            public void onDestroyActionMode(ActionMode mode) {
                Log.d(TAG, "5");
            }
        });


       sendMessage.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
       sendMessage.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
           @Override
           public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
               View child = sendMessage.getChildAt(position);
               if (checked==true) {
                   Log.d(TAG, "1" + "Adding " + position);
                   PositionToDelete.add(position);
                   temp.add(sendMessageAdapter.getItem(position));
                   //sendMessage.SetItemChecked(position, true);
                   //sendMessage.setSelection(position);
                   sendMessageAdapter.notifyDataSetChanged();
                   child.setBackground(getResources().getDrawable(R.drawable.pressed));
                   sendMessageAdapter.notifyDataSetChanged();
                   count=count+1;
                   mode.setTitle(count+"Item selected");
               }
               else if (checked==false)
               {   PositionToDelete.remove(position);
                   temp.remove(sendMessageAdapter.getItem(position));
                   child.setBackgroundColor(getResources().getColor(R.color.White));
                   sendMessageAdapter.notifyDataSetChanged();
                   count=count-1;
                   mode.setTitle(count+"Item selected");
               }
           }

           @Override
           //long press
           public boolean onCreateActionMode(ActionMode mode, Menu menu) {
               Log.d(TAG, "2");
               count=0;
               mode.setTitle(count+"Item selected");
               MenuInflater inflator = mode.getMenuInflater();
               inflator.inflate(R.menu.my_content_menu, menu);
               return true;
           }

           @Override
           public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
               Log.d(TAG, "3");
               return false;
           }

           @Override
           // on selecting item form menu
           public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
               Log.d(TAG, "4");
               for (Integer number : PositionToDelete) {
                   child = sendMessage.getChildAt(number);
                   child.setBackgroundColor(getResources().getColor(R.color.White));
                   sendMessageAdapter.notifyDataSetChanged();

               }

               switch(item.getItemId())
               {
                   case R.id.delete_id:
                       for (String msg : temp) {
                           Log.d(TAG, "10");
                           sendMessageAdapter.remove(msg);
                           sendMessageAdapter.notifyDataSetChanged();
                       }
                       count = 0;
                       mode.finish();
                       return true;
                   default:
                       return false;
               }

               }
           @Override
           public void onDestroyActionMode(ActionMode mode) {
               Log.d(TAG, "5");
           }
       });

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onClickSend(View v)
    {    Log.d(TAG, "1" + "Adding "  );
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss ");
        String currentTime = "time: "+sdf.format(new Date());
        s2 = InputMessage.getText().toString();
        //int g = Integer.parseInt(s2);
        sendMessageAdapter.add(s2+"\n"+currentTime);
        sendMessageAdapter.notifyDataSetChanged();
        int pos=sendMessage.getLastVisiblePosition();
        Log.d(TAG, "1" + "Adding +++ " +pos );
        //View child = sendMessage.getChildAt(pos+1);
        //child.setBackgroundColor(getResources().getColor(R.color.White));
        //sendMessageAdapter.notifyDataSetChanged();
        Toast.makeText(this,s2,Toast.LENGTH_SHORT).show();
        //addNotification();

       //String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        //create StringBuffer size of AlphaNumericString
        //StringBuilder sb = new StringBuilder(g);

        //for (int i = 0; i < g; i++) {

            //generate a random number betwee0 to AlphaNumericString variable length
          // int index
            //       = (int) (i%AlphaNumericString.length());

           // sb.append(AlphaNumericString.charAt(index));
       // }
       // s2=sb+s2+"=";
        s2=s2+"=";
        byte[] bytes ={0};
        bytes= s2.getBytes(Charset.defaultCharset());
        mBluetoothConnection.write(bytes);
        //child = sendMessage.getChildAt(pos);
        //child.setBackgroundColor(getResources().getColor(R.color.White));
    }
    BroadcastReceiver mReceiver =new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //.setBackgroundColor(getResources().getColor(R.color.White));
            String Text = intent.getStringExtra("the message");
            message.setLength(0);
            // message.append(Text+"\n");
            //SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss ");
           // String currentTime = "time: "+sdf.format(new Date());
            receivedMessageAdapter.add(Text);
            receivedMessageAdapter.notifyDataSetChanged();
            addNotification();

        }
    };


    private void addNotification() {

        Notification notify=new NotificationCompat.Builder(this, com.example.iist.Notification.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Message received")
                .setContentText("This is a test notification")
                .build();

        notificationManagerCompat.notify(1,notify);


    }



}
