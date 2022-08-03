package com.sil.ucubesampleapplication.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.sil.R;


/**
 * Created by tanvi.hirare on 17-05-2017.
 */


public class LoadingSpinner extends AppCompatImageView {

    private static final int IMAGE_RESOURCE_ID = R.drawable.ic_spinner;

    public LoadingSpinner(Context context) {
        super(context, null);
    }

    public LoadingSpinner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LoadingSpinner(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setImageResource(IMAGE_RESOURCE_ID);
        startAnimation();
    }


    public void startAnimation() {
        startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate));
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        if (visibility == View.VISIBLE) {
            startAnimation();
        } else {
            clearAnimation();
        }
    }
}

