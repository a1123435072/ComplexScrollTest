package com.itheima.complexscrolltest;

import android.content.Context;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Scroller;

/**
 * Created by huaqing on 2017/2/9.
 */

public class HeadScrollBehavior extends CoordinatorLayout.Behavior<RecyclerView> {

	private View dependency;
	private final Scroller mScroller;

	public HeadScrollBehavior(Context context, AttributeSet attrs) {
		super(context, attrs);
		//android中处理滚动的类
		mScroller = new Scroller(context);
	}

	//确定当前控件依赖的是哪一个子视图


	@Override
	public boolean layoutDependsOn(CoordinatorLayout parent, RecyclerView child, View dependency) {
		if (dependency.getId() == R.id.iv_head) {
			this.dependency = dependency;
			return true;
		}
		return super.layoutDependsOn(parent, child, dependency);
	}

	@Override
	public boolean onDependentViewChanged(CoordinatorLayout parent, RecyclerView child, View dependency) {
//		Log.i("test", "onDependentViewChanged");
		//让RecyclerView在y方向上平移一段距离
		child.setTranslationY(dependency.getHeight() + dependency.getTranslationY());
		//计算图片平移的百分比
		float percent = Math.abs(dependency.getTranslationY() / (dependency.getHeight() - finalHeight));
//		Log.i("test", "percent:" + percent);
		dependency.setScaleX(1 + percent);
		dependency.setScaleY(1 + percent);
		dependency.setAlpha(1 - percent);
		return true;
	}

	//在嵌套滚动将要开始的时候,一般用于判断滚动方向
	@Override
	public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {
//		Log.i("test","onStartNestedScroll");
		return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
	}

	private int finalHeight = 50;

	//在嵌套滚动之前执行,这个方法在onStartNestedScroll之后执行
	//dy:y方向上移动的距离,指的是在单位时间内手指移动的距离
	//consumed:是一个数组,消耗的x和y距离
	//0元素:表示x方向系统消耗的距离
	//1元素:表示y方向消耗的距离
	@Override
	public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dx, int dy, int[] consumed) {
		super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
//		Log.i("test","onNestedPreScroll");
//		Log.i("test", "dy:" + dy);

		//手指向下移动dy<=0,向上移动dy>0
		if (dy <= 0) {
			return;
		}

		float newTranslationY = dependency.getTranslationY() - dy;
		//计算出最小平移的y距离
		float minTranslationY = -(dependency.getHeight() - finalHeight);
		if (newTranslationY > minTranslationY) {
			dependency.setTranslationY(newTranslationY);
			//在图片折叠的情况下,
			// 1,不允许RecyclerView自身处理滚动
			consumed[1] = dy;
			// 2,只能和图片一起向上平移
		}
	}

	//dyUnconsumed:当RecyclerView消耗掉y方向的距离,则dyUnConsumed不等于0
	@Override
	public void onNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
		super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
//		Log.i("test","onNestedScroll");
//		Log.i("test","dyUnconsumed:"+dyUnconsumed);
		if (dyUnconsumed >= 0) {
			return;
		}
		float newTranslationY = dependency.getTranslationY() - dyUnconsumed;
		if (newTranslationY < 0) {
			dependency.setTranslationY(newTranslationY);
		}

	}

	//定义boolean,判断是否处于自动滚动中
	private boolean isScrolling=false;
	//Fling:快速滑动,猛动
	//velocityY:表示快速滑动松开手瞬间y方向的速度,向上为正,向下为负
	@Override
	public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, RecyclerView child, View target, float velocityX, float velocityY) {
		Log.i("test","velocityY:"+velocityY);
		if(!isScrolling){
			return startExpandOrClose(velocityY);
		}
		Log.i("test","onNestedPreFling");
		return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY);
	}

	//onStopNestedScroll:父布局停止嵌套滚动,执行一次,自身控件停止嵌套滚动执行一次
	@Override
	public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, RecyclerView child, View target) {
		super.onStopNestedScroll(coordinatorLayout, child, target);
		startExpandOrClose(0);
		Log.i("test","onStopNestedScroll");
	}

	//在滚动被子视图接收的时候执行
	@Override
	public void onNestedScrollAccepted(CoordinatorLayout coordinatorLayout, RecyclerView child, View directTargetChild, View target, int nestedScrollAxes) {
		super.onNestedScrollAccepted(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes);
		Log.i("test","onNestedScrollAccepted");
		//停止Scroller的滚动
		mScroller.abortAnimation();
	}

	private boolean startExpandOrClose(float velocityY){
		//获取松开手瞬间,图片已经平移的距离
		float translationY = dependency.getTranslationY();
		float upFinalTranslationY=-(dependency.getHeight()-finalHeight);
		float downFinalTranslationY=0;
		//定义boolean值确定是否是向闭合的状态滚动
		boolean isClose=false;
		if(Math.abs(velocityY)<=800){
			//判断松开手时已经平移的位置和已经平移位置与向上平移位置的差值的绝对值进行比较
			if(Math.abs(translationY) < Math.abs(translationY-upFinalTranslationY)){
				isClose=false;
			}else{
				isClose=true;
			}
		}else{
			if(velocityY>0){
				//从松开手的瞬间位置自动滚动到完全闭合的位置
				isClose=true;
			}else{
				//从松开手的瞬间位置自动滚动到完全展开的位置
				isClose=false;
			}
		}
		//确定滚动的目标点
		float targetTranslationY=isClose?upFinalTranslationY:downFinalTranslationY;
		int startY= (int) translationY;
		int dy= (int) (targetTranslationY-translationY);
		mScroller.startScroll(0,startY,0,dy);
		handler.post(flingRunnable);
		isScrolling=true;
		return true;
	}


	private Handler handler=new Handler();

	private Runnable flingRunnable=new Runnable() {
		@Override
		public void run() {
			//Scroller滚动的原理:
			//是一帧一帧向前滚动的,滚动的过程中,要不断的计算是否滚动到目标,如果未滚动到,则继续滚动
			//判断Scroller是否已经滚动到目标位置
			//这个方法还可以判断是否有下一个新的小目标点
			if(mScroller.computeScrollOffset()){
				//getCurrY:指获取下一个新的位置
				dependency.setTranslationY(mScroller.getCurrY());
				handler.post(this);
			}else{
				isScrolling=false;
			}
		}
	};
}
