package com.sil.ucubesampleapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kalpesh.krecyclerviewadapter.KRecyclerViewAdapter;
import com.kalpesh.krecyclerviewadapter.KRecyclerViewHolder;
import com.kalpesh.krecyclerviewadapter.KRecyclerViewHolderCallBack;
import com.kalpesh.krecyclerviewadapter.KRecyclerViewItemClickListener;
import com.sil.R;
import com.sil.ucubesampleapplication.utils.Constants;
import com.sil.ucubesampleapplication.viewholder.BluetoothHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ScanActivity extends AppCompatActivity {
    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @RequiresApi(api = Build.VERSION_CODES.S)
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
    };
    private static final String TAG = ScanActivity.class.getSimpleName();
    private RecyclerView recylerview;
    KRecyclerViewAdapter kRecyclerViewAdapter;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    Button scanBtn;

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            if (Build.VERSION.SDK_INT >= 31) {
                ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
            }
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }


    public boolean checkPermission() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int ForthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ForthPermissionResult == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        recylerview = findViewById(R.id.recylerview);
        if (!checkPermission()) {
            requestBlePermissions(ScanActivity.this,100);
        }
        scanBtn = findViewById(R.id.start_scan);

        bluetoothDevices = new ArrayList<>();

        kRecyclerViewAdapter = new KRecyclerViewAdapter(this, bluetoothDevices, new KRecyclerViewHolderCallBack() {
            @Override
            public KRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_item_layout, parent, false);
                return new BluetoothHolder(layoutView);
            }

            @Override
            public void onHolderDisplayed(@NonNull KRecyclerViewHolder kRecyclerViewHolder, int position) {

            }
        }, new KRecyclerViewItemClickListener() {
            @Override
            public void onRecyclerItemClicked(@NonNull KRecyclerViewHolder kRecyclerViewHolder, @NonNull Object device, int i) {
                if (device instanceof BluetoothDevice) {
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) device;
                    callConfirmationDialog(bluetoothDevice);
                }
            }
        });
        recylerview.setAdapter(kRecyclerViewAdapter);
        recylerview.setLayoutManager(new LinearLayoutManager(this));
        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBluetoothPairedList();
            }
        });
    }

    private void callConfirmationDialog(final BluetoothDevice bluetoothDevice) {
        final AlertDialog.Builder localBuilder = new AlertDialog.Builder(ScanActivity.this);
        localBuilder.setTitle("Select Device");
        localBuilder.setMessage("Are you sure to select " + bluetoothDevice.getName() + " ?");
        localBuilder.setCancelable(false);
        localBuilder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (findDeviceByMacAddress(bluetoothDevice.getAddress()) == null) {
                    Log.e(TAG, "onClick: NULL");
                } else {
                    Log.e(TAG, "onClick: NOT_NULL");
                    BluetoothDevice bd = findDeviceByMacAddress(bluetoothDevice.getAddress());
                    Log.e(TAG, "onClick: getBondState : " + bd.getBondState());
                    Log.e(TAG, "onClick: getAddress : " + bd.getAddress());
                    Log.e(TAG, "onClick: getType : " + bd.getType());
                    Log.e(TAG, "onClick: getUuids : " + bd.getUuids());
                }
                openNextActivity(bluetoothDevice);
            }
        });
        localBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        localBuilder.show();
    }

    /**
     * Helper method that finds a device given its mac address.
     *
     * @param macAddress the mac address of the device.
     * @return a {@code BluetoothDevice} object if it was found. Returns null otherwise.
     */
    public BluetoothDevice findDeviceByMacAddress(String macAddress) {
        for (BluetoothDevice device : getPairedDevices()) {
            if (device.getAddress().equalsIgnoreCase(macAddress)) {
                return device;
            }
        }
        return null;
    }

    /**
     * Returns all the paired devices on this device.
     *
     * @return an ArrayList of the paired devices.
     */
    public ArrayList<BluetoothDevice> getPairedDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ArrayList<BluetoothDevice> devices = new ArrayList<BluetoothDevice>();
        Set<BluetoothDevice> bonds = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : bonds) {
            devices.add(device);
        }
        return devices;
    }

    private void openNextActivity(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "openNextActivity: " + bluetoothDevice.getName());
        Intent intent = new Intent(ScanActivity.this, MainActivity.class);
        intent.putExtra(Constants.BLUETOOTH_ADDRESS, bluetoothDevice.getAddress());
        startActivity(intent);
    }

    private void openBluetoothPairedList() {
        try {
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
                showToast("Device does not support Bluetooth");
            } else if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled :)
                showToast("Kindly enable Bluetooth");
            } else {
                BluetoothManager manager = (BluetoothManager) getApplicationContext().getSystemService(BLUETOOTH_SERVICE);
                if (manager != null) {
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    List<BluetoothDevice> connectedBTDevice = new ArrayList<>();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice bt : pairedDevices) {
                            //if (bt.getType() == 3) {
                                connectedBTDevice.add(bt);
                           // }
                        }
                    }

                    if (connectedBTDevice.size() > 0) {
                        openBluetoothSelectionDialog(connectedBTDevice);
                    } else {
                        showToast("No Device Found.\nKindly Connect to New Device");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openBluetoothSelectionDialog(List<BluetoothDevice> bluetoothDevice) {
        this.bluetoothDevices.clear();
        this.bluetoothDevices.addAll(bluetoothDevice);
        if (kRecyclerViewAdapter != null) {
            kRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}
