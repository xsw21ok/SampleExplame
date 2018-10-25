package com.sample.explame.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

public class MaskHighLightView extends FrameLayout {

	private RectF mRectF;
	//遮罩层画笔
	private Paint mBackgroundPaint;
	//透明椭圆画笔
	private Paint mPaint;

	private Bitmap mMaskBitmap;
	private Canvas mTempCanvas;

	private Builder.MaskViewParams mMaskViewParams;

	public MaskHighLightView(@NonNull Context context) {
		this(context, null);
	}

	public MaskHighLightView(@NonNull Context context, @Nullable AttributeSet attrs) {
		super(context, attrs);
	}

	private void init(Builder.MaskViewParams params) {
		mMaskViewParams = params;
		mPaint = new Paint();
		mBackgroundPaint = new Paint();
		mBackgroundPaint.setColor(mMaskViewParams.mMaskLayerColor);
		mPaint.setAntiAlias(true);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
		//关闭当前view的硬件加速
		setLayerType(LAYER_TYPE_SOFTWARE, null);
		//ViewGroup默认设定为true，会使onDraw方法不执行，如果复写了onDraw(Canvas)方法，需要清除此标记
		setWillNotDraw(false);
	}

	public void show() {
		if (mRectF != null) {
			//先绘制全屏遮罩层
			mMaskBitmap = Bitmap.createBitmap(mMaskViewParams.mMaskWidth, mMaskViewParams.mMaskHeight, Bitmap.Config.ARGB_8888);
			mTempCanvas = new Canvas(mMaskBitmap);
		}
	}

	/**
	 * 获取TargetView位置
	 */
	private void getTargetViewPosition() {
		int[] targetViewLocation = getViewPosition(mMaskViewParams.mTargetView);
		int targetViewWidth = mMaskViewParams.mTargetView.getWidth();
		int targetViewHeight = mMaskViewParams.mTargetView.getHeight();
		mRectF = new RectF(targetViewLocation[0] - mMaskViewParams.mTargetViewPaddingLeft,
				targetViewLocation[1] - mMaskViewParams.mTargetViewPaddingTop,
				targetViewLocation[0] + targetViewWidth + mMaskViewParams.mTargetViewPaddingRight,
				targetViewLocation[1] + targetViewHeight + mMaskViewParams.mTargetViewPaddingBottom);
		addView(mMaskViewParams.mHintView, mMaskViewParams.mTargetViewLayoutParams);

	}

	private int[] getViewPosition(View view) {
		int[] location = new int[2];
		view.getLocationInWindow(location);
		if (location[0] <= 0 || location[1] <= 0) {
			Rect rect = new Rect();
			view.getGlobalVisibleRect(rect);
			location[0] = rect.left;
			location[1] = rect.top + getStatusBarHeight(getContext()) + mMaskViewParams.mTitleHeight;
		}
		return location;
	}

	private int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mMaskBitmap != null) {
			mTempCanvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mBackgroundPaint);
			mTempCanvas.drawRoundRect(mRectF, mMaskViewParams.mRadius, mMaskViewParams.mRadius, mPaint);
			//绘制到画布上
			canvas.drawBitmap(mMaskBitmap, 0, 0, mBackgroundPaint);
		}
	}

	public static class Builder {

		private Context mContext;
		private MaskHighLightView mMaskHighLightView;
		private MaskViewParams mParams;

		private static class MaskViewParams {
			View mTargetView;
			View mHintView;
			FrameLayout.LayoutParams mTargetViewLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
					FrameLayout.LayoutParams.WRAP_CONTENT);
			int mTargetViewPaddingLeft;
			int mTargetViewPaddingTop;
			int mTargetViewPaddingRight;
			int mTargetViewPaddingBottom;
			float mRadius;
			//标题高度，防止getLocationInWindow值为0时进行重新取值。
			int mTitleHeight;
			//蒙版背景色
			int mMaskLayerColor = 0xcc000000;
			int mMaskWidth, mMaskHeight;
		}

		public Builder(Context context) {
			mContext = context;
			mParams = new MaskViewParams();
		}

		public Builder setTargetView(View view) {
			mParams.mTargetView = view;
			return this;
		}

		public Builder setHintView(View view) {
			mParams.mHintView = view;
			return this;
		}

		public Builder setTitleHeight(int height) {
			mParams.mTitleHeight = height;
			return this;
		}

		public Builder setTargetViewLayoutParams(FrameLayout.LayoutParams params) {
			mParams.mTargetViewLayoutParams = params;
			return this;
		}

		public Builder setPadding(int padding) {
			mParams.mTargetViewPaddingLeft = padding;
			mParams.mTargetViewPaddingTop = padding;
			mParams.mTargetViewPaddingRight = padding;
			mParams.mTargetViewPaddingBottom = padding;
			return this;
		}

		public Builder setRadius(float radius) {
			mParams.mRadius = radius;
			return this;
		}

		public Builder setMaskRange(int width, int height) {
			mParams.mMaskWidth = width;
			mParams.mMaskHeight = height;
			return this;
		}

		public MaskHighLightView create() {
			if (mMaskHighLightView == null) {
				mMaskHighLightView = new MaskHighLightView(mContext);
				mMaskHighLightView.init(mParams);
				mMaskHighLightView.getTargetViewPosition();
			}
			return mMaskHighLightView;
		}
	}
}
