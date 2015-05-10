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
import android.widget.TextView;

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
    private static final String TAG = "AndroidProvider";
    private NsdManager.RegistrationListener mRegistrationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Log.d(TAG, "onResume");
        super.onResume();
        this.startServer();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        this.stopServer();
    }

    private void startServer() {

        // create the registration listener who will be the callback object for service registration
        mRegistrationListener = new NsdManager.RegistrationListener() {

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                String mServiceName = NsdServiceInfo.getServiceName();
                Log.d(TAG, "Registered service. Actual name used: " + mServiceName);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Log.d(TAG, "Failed to register service");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d(TAG, "Unregistered service");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d(TAG, "Service unregistration failed");
                // Unregistration failed.  Put debugging code here to determine why.
            }
        };

        // obtain a free port number by binding to a socket and then unbinding again so the port is available to us
        // nicer would be to let NanoHTTPD do this and just query it for the port

        ServerSocket mServerSocket = null;
        try {
            mServerSocket = new ServerSocket(0);
            // Store the chosen port.
            PORT = mServerSocket.getLocalPort();
            mServerSocket.close();

            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            Log.d(TAG, "port is " + Integer.toString(PORT));
            Log.d(TAG, "ip is " + this.getLocalIpAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // run the server
        try {
            server = new MyHTTPD();
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // after server is started, register the bonjour service
        serviceInfo  = new NsdServiceInfo();
        serviceInfo.setServiceName("jktest_androidprovider");
        serviceInfo.setServiceType("_jktest._tcp.");
        serviceInfo.setPort(PORT);

        NsdManager mNsdManager = (NsdManager)this.getApplicationContext().getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

        // Report the address to UI
        TextView urlView = (TextView)findViewById(R.id.url);
        urlView.setText("Now listening on http://" + getLocalIpAddress() + ":" + Integer.toString(PORT) + "/");
    }

    private void stopServer() {
        // unregister the service
        NsdManager mNsdManager = (NsdManager)this.getApplicationContext().getSystemService(Context.NSD_SERVICE);
        mNsdManager.unregisterService(mRegistrationListener);

        // stop server
        if (server != null) {
            server.stop();
        }
    }

    public String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if ((inetAddress instanceof Inet4Address) && !inetAddress.isLoopbackAddress()) {
                        // I don’t know how NanoHTTPD, bonjour etc feel about ipv6 addresses
                        // So to be on the safe side, we filter to ipv4 only
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
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

            final String html = "Hello, world, from Android. Running at http://" + getLocalIpAddress() + ":" + PORT;
            return new NanoHTTPD.Response(html);
        }
    }
}

