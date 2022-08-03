package com.sil.ucubesampleapplication.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sil.R;
import com.sil.ucubesampleapplication.customview.LoadingSpinner;

public class CustomDialog extends Dialog {
    private TextView txtDescription;
    Context context;
    LoadingSpinner imgSpinner;

    public CustomDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_end_cash_drawer);
        initialize();

    }

    private void initialize() {
        imgSpinner = findViewById(R.id.imgSpinner);
        txtDescription = findViewById(R.id.txtDescription);
        if (getWindow() != null) {
            getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            setCancelable(false);
        }
    }

    public void setMessage(String msg) {
        txtDescription.setText(msg);
    }

}
