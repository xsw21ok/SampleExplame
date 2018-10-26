package com.sample.explame;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import com.sample.explame.widget.MaskHighLightView;

public class MainActivity extends AppCompatActivity {

	private MaskHighLightView mMaskHighLightView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.tv_hello).getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			boolean measure;
			@Override
			public void onGlobalLayout() {
				if(measure){
					return;
				}
				DisplayMetrics displayMetrics = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
				int width = displayMetrics.widthPixels;
				int height = displayMetrics.heightPixels;
				View parent = getWindow().getDecorView();
				if (parent instanceof FrameLayout) {
					final FrameLayout viewParent = (FrameLayout) parent;
					View hintView = LayoutInflater.from(getApplicationContext()).inflate(R.layout
							.trade_holding_mask_layout, null);
					hintView.findViewById(R.id.tv_mask_i_know).setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							viewParent.removeView(mMaskHighLightView);
						}
					});
					FrameLayout.LayoutParams hintViewParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
							FrameLayout.LayoutParams.WRAP_CONTENT);
					hintViewParams.topMargin = getStatusBarHeight(getApplicationContext()) + 200;
					int padding = 40;
					float radius = 40f;
					mMaskHighLightView = new MaskHighLightView.Builder(getApplicationContext())
							.setPadding(padding)
							.setRadius(radius)
							.setTargetView(findViewById(R.id.tv_hello))
							.setHintView(hintView)
							.setHintViewLayoutParams(hintViewParams)
							.setMaskRange(width, height)
							.create();
					mMaskHighLightView.show();
					viewParent.addView(mMaskHighLightView);
					measure = true;
				}
			}
		});
	}

	private int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}
