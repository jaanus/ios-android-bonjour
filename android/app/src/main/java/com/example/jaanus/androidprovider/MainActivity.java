package com.example.jaanus.androidprovider;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;

import fi.iki.elonen.NanoHTTPD;


public class MainActivity extends ActionBarActivity {

    private int PORT;
    private MyHTTPD server;
    private NsdServiceInfo serviceInfo;
    private NsdManager mNsdManager;

    private NsdManager.RegistrationListener mRegistrationListener = new NsdManager.RegistrationListener() {

        @Override
        public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
            // Save the service name.  Android may have changed it in order to
            // resolve a conflict, so update the name you initially requested
            // with the name Android actually used.
            String mServiceName = NsdServiceInfo.getServiceName();
            Log.d("WAT", "Registered service");
        }

        @Override
        public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Registration failed!  Put debugging code here to determine why.
            Log.d("WAT", "Failed to register service");
        }

        @Override
        public void onServiceUnregistered(NsdServiceInfo arg0) {
            // Service has been unregistered.  This only happens when you call
            // NsdManager.unregisterService() and pass in this listener.
        }

        @Override
        public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
            // Unregistration failed.  Put debugging code here to determine why.
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Integer a = 3 + 4;
        Log.d("WAT", "Message " + Integer.toString(a));
        Log.d("WAT", "My ip address is " + getLocalIpAddress());

        ServerSocket mServerSocket = null;
        try {
            mServerSocket = new ServerSocket(0);
            // Store the chosen port.
            PORT = mServerSocket.getLocalPort();
            mServerSocket.close();

            serviceInfo  = new NsdServiceInfo();

            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            Log.d("WAT", "port is " + Integer.toString(PORT));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // register the bonjour service
        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setServiceName("jktestandroid");
        serviceInfo.setServiceType("_jktest._tcp.");
        serviceInfo.setPort(PORT);

        mNsdManager = (NsdManager)this.getApplicationContext().getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
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

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("WAT", "Attempting to start myHTTPD");

//        TextView textIpaddr = (TextView) findViewById(R.id.ipaddr);
//        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
//        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();
//        final String formatedIpAddress = String.format("%d.%d.%d.%d", (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
//                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
//        textIpaddr.setText("Please access! http://" + formatedIpAddress + ":" + PORT);

        try {
            server = new MyHTTPD();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (server != null)
            server.stop();
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if ((inetAddress instanceof Inet4Address) && !inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WAT LOG", ex.toString());
        }
        return null;
    }

    private class MyHTTPD extends NanoHTTPD {
        public MyHTTPD() throws IOException {
            super(PORT);
        }

        @Override
        public Response serve(String uri, Method method, Map<String, String> headers, Map<String, String> parms,
                              Map<String, String> files) {
//            final StringBuilder buf = new StringBuilder();
//            for (Map.Entry<String, String> kv : headers.entrySet())
//                buf.append(kv.getKey()).append(" : ").append(kv.getValue()).append("\n");
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
////                    hello.setText(buf);
//                    Log.d("WAT", "I don’t know wtf this is: " + buf);
//                }
//            });

            final String html = "Hello, world, from Android. Running at http://" + getLocalIpAddress();
            return new NanoHTTPD.Response(html);
        }
    }
}

