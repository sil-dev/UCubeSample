package com.sil.ucubesample;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kalpesh.krecyclerviewadapter.KRecyclerViewAdapter;
import com.kalpesh.krecyclerviewadapter.KRecyclerViewHolder;
import com.kalpesh.krecyclerviewadapter.KRecyclerViewHolderCallBack;
import com.kalpesh.krecyclerviewadapter.KRecyclerViewItemClickListener;
import com.sil.ucubesample.utils.Constants;
import com.sil.ucubesample.viewholder.BluetoothHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ScanActivity extends AppCompatActivity {

    private static final String TAG = ScanActivity.class.getSimpleName();
    private RecyclerView recylerview;
    KRecyclerViewAdapter kRecyclerViewAdapter;
    private ArrayList<BluetoothDevice> bluetoothDevices;
    Button scanBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        recylerview = findViewById(R.id.recylerview);
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
                BluetoothManager manager = (BluetoothManager) this.getSystemService(BLUETOOTH_SERVICE);
                if (manager != null) {
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                    List<BluetoothDevice> connectedBTDevice = new ArrayList<>();
                    if (pairedDevices.size() > 0) {
                        for (BluetoothDevice bt : pairedDevices) {
                            if (bt.getType() == 3) {
                                connectedBTDevice.add(bt);
                            }
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
