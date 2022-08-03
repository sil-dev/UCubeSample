package com.sil.ucubesampleapplication.viewholder;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.kalpesh.krecyclerviewadapter.KRecyclerViewHolder;
import com.sil.R;

public class BluetoothHolder extends KRecyclerViewHolder {

    TextView name;

    public BluetoothHolder(View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name_container);
    }

    @Override
    protected void setSelected(@NonNull Context context, boolean selected) {
        super.setSelected(context, selected);
        // This method is called whenever the holder is selected/unselected.
        if (selected) {
            // Selected
        } else {
            // Unselected
        }
    }

    @Override
    protected void setData(@NonNull Context context, @NonNull Object itemObject) {
        super.setData(context, itemObject);
        // This method is called automatically by the adapter.
        // override this method and set your data here...
        // Check the type of itemObject
        if (itemObject instanceof BluetoothDevice) {
            BluetoothDevice bt = (BluetoothDevice) itemObject;
            name.setText(bt.getName());
        }
    }


}
