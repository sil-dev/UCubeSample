package com.sil.ucubesdk;/*
 * Copyright (C) 2016, YouTransactor. All Rights Reserved.
 *
 * Use of this product is contingent on the existence of an executed license
 * agreement between YouTransactor or one of its sublicensee, and your
 * organization, which specifies this software's terms of use. This software
 * is here defined as YouTransactor Intellectual Property for the purposes
 * of determining terms of use as defined within the license agreement.
 */

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;

import com.sil.ucubesdk.rpc.IConnexionManager;
import com.sil.ucubesdk.rpc.MyConst;
import com.sil.ucubesdk.rpc.RPCManager;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author gbillard on 5/25/16.
 */
public class BluetoothConnexionManager extends BroadcastReceiver implements IConnexionManager {

    private String deviceAddr;
    private BluetoothSocket socket;
    public boolean isRegistered;
    UCubeCallBacks uCubeCallBacks;

    public BluetoothConnexionManager() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (!adapter.isEnabled()) {
            adapter.enable();
        }
    }

    public static boolean isBluetoothOn() {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        return adapter.isEnabled();
    }

    public boolean connect() {
        if (deviceAddr == null) {
            return false;
        }

        LogManager.debug(BluetoothConnexionManager.class.getSimpleName(), "connect to " + deviceAddr);

        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }

        BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddr);

        try {
//			socket = device.createRfcommSocketToServiceRecord(BT_UUID);
            Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class});
            socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));

            if (socket != null) {
                socket.connect();
                RPCManager.getInstance().start(socket.getInputStream(), socket.getOutputStream());
                return true;
            }
            return false;
        } catch (Exception e) {
            LogManager.debug(BluetoothConnexionManager.class.getName(), "connect device error", e);
            if (uCubeCallBacks != null) {
                try {
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

                    if (!adapter.isEnabled()) {
                        MyConst.setJSONResponse(getJSONObject("CONNECTION ERROR", 102, "Bluetooth Disconnected."));
                    }
                } catch (Exception excep) {
                    excep.printStackTrace();
                }
            }
            if (socket != null && socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }

            return false;
        }
    }

    public boolean canConnect(String deviceAddr) {
        if (deviceAddr == null) {
            return false;
        }

        try {
            LogManager.debug(BluetoothConnexionManager.class.getSimpleName(), "connect to " + deviceAddr);

            if (socket == null) {
                BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddr);

                //socket = device.createRfcommSocketToServiceRecord(BT_UUID);
                Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class});
                socket = (BluetoothSocket) m.invoke(device, Integer.valueOf(1));

            }

            if (socket != null && socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                    return false;
                }
            }

            if (socket != null) {
                socket.connect();
                //RPCManager.getInstance().start(socket.getInputStream(), socket.getOutputStream());
                return true;
            }
            return false;
        } catch (Exception e) {
            LogManager.debug(BluetoothConnexionManager.class.getName(), "connect device error", e);
            if (uCubeCallBacks != null) {
                try {
                    BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

                    if (!adapter.isEnabled()) {
                        MyConst.setJSONResponse(getJSONObject("CONNECTION ERROR", 102, "Bluetooth Disconnected."));
                    }
                } catch (Exception excep) {
                    excep.printStackTrace();
                }
            }
            if (socket != null && socket.isConnected()) {
                try {
                    socket.close();
                } catch (IOException ignored) {
                }
            }

            return false;
        }
    }

    public boolean disconnect() {
        if (socket != null && socket.isConnected()) {
            try {
                socket.close();
                socket = null;
                return true;
            } catch (IOException ignored) {
                return false;
            }
        }
        return false;
    }

    public boolean isInitialized() {
        return StringUtils.isNoneBlank(deviceAddr);
    }

    public void initialize(SharedPreferences settings, UCubeCallBacks uCubeCallBacks) {
        this.uCubeCallBacks = uCubeCallBacks;
        setDeviceAddr(settings.getString("BT_deviceMacAddr", null));
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        switch (intent.getAction()) {
            case BluetoothAdapter.ACTION_STATE_CHANGED:
                if (BluetoothAdapter.getDefaultAdapter().isEnabled()) {
                    connect();
                }
                break;
        }
    }

    public String getDeviceAddr() {
        return deviceAddr;
    }

    public void setDeviceAddr(String deviceAddr) {
        this.deviceAddr = deviceAddr;
    }

    public static BluetoothConnexionManager getInstance() {
        return INSTANCE;
    }

    private static final BluetoothConnexionManager INSTANCE = new BluetoothConnexionManager();

    public Intent register(Context context, IntentFilter filter) {
        try {
            return !isRegistered
                    ? context.registerReceiver(this, filter)
                    : null;
        } finally {
            isRegistered = true;
        }
    }

    public boolean unregister(Context context) {
        // additional work match on context before unregister
        // eg store weak ref in register then compare in unregister
        // if match same instance
        return isRegistered
                && unregisterInternal(context);
    }

    private boolean unregisterInternal(Context context) {
        context.unregisterReceiver(this);
        isRegistered = false;
        return true;
    }

    private JSONObject getJSONObject(String message, int responseCode, String responseJson) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Msg", message);
            jsonObject.put("ResponseCode", responseCode);
            jsonObject.put("Response", responseJson);
            return jsonObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
