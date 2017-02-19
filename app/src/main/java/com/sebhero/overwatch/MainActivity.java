package com.sebhero.overwatch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.EditText;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private String url = "192.168.0.11:8080";
    NotificationCompat.Builder notification;
    private static final int uniqueID = 32134;

    private ArrayList<String> events = new ArrayList<>();
    private ArrayAdapter<String> eventAdapter;


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private WebSocketClient mWebSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the eventAdapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());



        // Set up the ViewPager with the sections eventAdapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);



//        events.add("");
        eventAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.events);

        PlaceholderFragment.setEventAdapter(eventAdapter);


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                connectWebSocket(view);
//            }
//        });

        //notifications
        notification = new NotificationCompat.Builder(this);
        notification.setAutoCancel(true);
        connectWebSocket(null);

    }

//    public void onResume() {
//        super.onResume();  // Always call the superclass method first
//
////        connectWebSocket(null);
////        eventAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, this.events);
//
////        PlaceholderFragment.setEventAdapter(eventAdapter);
//
//    }




    public void connect(View view) {
        connectWebSocket(view);
    }

    public void disconnect(View view) {
        if (mWebSocketClient != null) {
            mWebSocketClient.close();
        }
        events.clear();
        eventAdapter.notifyDataSetChanged();
    }


    private void showNotification(String text) {

        //setup notification
        notification.setSmallIcon(R.mipmap.ic_launcher);
        notification.setTicker("OverWatch Alarm!");
        notification.setWhen(System.currentTimeMillis());
        notification.setContentTitle("OverWatch Alarm");
        notification.setContentText(text);

        //go back to app
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pendingIntent);

        //build notification and send / issueit
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(uniqueID, notification.build());
    }



    private void connectWebSocket(final View view) {
        String tag = "connectWS";
        URI uri;
        String url;
        if (PlaceholderFragment.txtbIP != null) {
            url = PlaceholderFragment.txtbIP.getText().toString();

        } else {
            url = "192.168.0.10";
        }
        try {

            uri = new URI("ws://" + url+ ":8080/alarms");
            Log.e(tag, "connect to " + uri.toASCIIString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        if (view != null) {
            Snackbar.make(view, "Connect to: " + uri.toASCIIString(), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }


        mWebSocketClient = new WebSocketClient(uri, new Draft_17()) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                if (view != null) {
                    Snackbar.make(view, "Websocket success!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {


                    @Override
                    public void run() {
                        Log.i("WS got", message);
                        try {
                            if (message.contains("AlarmEvent")) {
                                String[] str = message.split("AlarmEvent");
//                            Log.i("WS got",str[0]);
//                                Log.i("WS got",str[1]);
                                JSONObject json = new JSONObject(str[1]);
                                Log.i("WS json","mag: "+json.getInt("magnetSensor"));
                                Log.i("WS json","pir: "+json.getInt("pirSensor"));
                                Log.i("WS json","timestamp: "+json.getLong("timestamp"));


                                int mag =json.getInt("magnetSensor");
                                int pir = json.getInt("pirSensor");
                                long time= json.getLong("timestamp");
                                SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
                                java.util.Date dateObj = new java.util.Date(time);
                                StringBuilder nowYYYYMMDD = new StringBuilder( dateformatYYYYMMDD.format( dateObj ) );
                                String msg = "";
                                if (mag == 1) {
                                    msg = "Magnet alarm:\t"+nowYYYYMMDD;
                                } else if (pir == 1) {
                                    msg = "PIR alarm:\t"+nowYYYYMMDD;
                                } else {
                                    msg = "Magnet and PIR alarm:\t"+nowYYYYMMDD;
                                }

                                events.add(msg);
                                eventAdapter.notifyDataSetChanged();
                                showNotification(msg);
                            } else if (message.contains(".jpg")) {
                                Log.i("WS json","pic!");
//                                imageAdapter.addImage(message);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
                if (view != null) {
                    Snackbar.make(view, "disconnected from websocket", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
                if (view != null) {
                    Snackbar.make(view, "FAILD to connect", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        };
        mWebSocketClient.connect();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
