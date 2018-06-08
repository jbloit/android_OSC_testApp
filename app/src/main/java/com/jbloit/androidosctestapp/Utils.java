package com.jbloit.androidosctestapp;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;

import java.io.IOException;
import java.net.InetAddress;

/**
 * Created by julien on 6/8/18.
 */

public class Utils {
    Context mContext;
    public Utils(Context mContext) {
        this.mContext = mContext;
    }

    // From https://stackoverflow.com/questions/2993874/android-broadcast-address?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa

    public InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) (broadcast >> (k * 8));
        return InetAddress.getByAddress(quads);
    }
}
