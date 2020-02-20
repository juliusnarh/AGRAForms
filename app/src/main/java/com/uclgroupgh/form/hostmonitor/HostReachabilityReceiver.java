package com.uclgroupgh.form.hostmonitor;

import android.util.Log;

import net.gotev.hostmonitor.HostMonitorBroadcastReceiver;
import net.gotev.hostmonitor.HostStatus;

/**
 * Created by Junior on 1/6/2018.
 */

public class HostReachabilityReceiver extends HostMonitorBroadcastReceiver {
    @Override
    public void onHostStatusChanged(HostStatus status) {
        Log.i("HostReachability", status.toString());
    }
}
