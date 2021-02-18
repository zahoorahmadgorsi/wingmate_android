package com.app.wingmate.widgets;

import android.view.View;

import androidx.viewpager.widget.ViewPager;

public class FadePageTransformer implements ViewPager.PageTransformer {
        public void transformPage(View view, float position) {
            view.setTranslationX(view.getWidth() * -position);

            if (position <= -1.0F || position >= 1.0F) {
                view.setAlpha(0.0F);
            } else if (position == 0.0F) {
                view.setAlpha(1.0F);
            } else {
                view.setAlpha(1.0F - Math.abs(position));
            }
        }
    }