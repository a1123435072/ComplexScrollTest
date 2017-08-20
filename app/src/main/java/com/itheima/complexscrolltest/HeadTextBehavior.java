package com.itheima.complexscrolltest;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by huaqing on 2017/2/14.
 */

public class HeadTextBehavior extends CoordinatorLayout.Behavior<View> {

	private final ArgbEvaluator argbEvaluator;

	public HeadTextBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
		argbEvaluator = new ArgbEvaluator();
	}

	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
		if (dependency.getId() == R.id.iv_head) {
			return true;
		}
		return super.layoutDependsOn(parent, child, dependency);
	}

	private float initOffset = 130;
	private float collapsedOffset = 5;
	private int finalHeight = 50;
	private int initMargin=20;
	private int finalMargin=5;
	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
		//处理随着图片向上移动,当前控件也向上移动
		float percent = Math.abs(dependency.getTranslationY() / (dependency.getHeight() - finalHeight));
		Log.i("test","percent:"+percent);
		float translationY = collapsedOffset + (initOffset - collapsedOffset) * (1-percent);
		child.setTranslationY(translationY);
		int color= (int) argbEvaluator.evaluate(percent, Color.RED,Color.GREEN);
		child.setBackgroundColor(color);
		int margin= (int) (finalMargin+(initMargin-finalMargin)*(1-percent));
		CoordinatorLayout.LayoutParams params= (CoordinatorLayout.LayoutParams) child.getLayoutParams();
		params.setMargins(margin,0,margin,0);
		child.setLayoutParams(params);
		return true;
	}
}
