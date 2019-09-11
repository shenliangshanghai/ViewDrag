package view.shen.com.viewdemo.dragdemo;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.ViewDragHelper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import view.shen.com.viewdemo.R;

/**
 * @author shenliang
 * @date 2019/7/29
 * @desc
 */
public class SlideUpPanelLayout extends ViewGroup {

    private int mMainViewId;
    private int mSlideViewId;
    public View mMainView;
    public View mSlideView;
    private int mPanelDefaultHeight;
    private int widthSize;
    private int heightSize;
    private ViewDragHelper viewDragHelper;
    private int mPanelTop;
    private int mScrollableViewId;
    private View mScrollableView;
    private float mDownY;
    private int leftMargin;
    private float lastTop;

    private int slideTop;

    public SlideUpPanelLayout(Context context) {
        super(context);
    }

    public SlideUpPanelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SlideUpPanelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public SlideUpPanelLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SlideUpPanelLayout);
        mMainViewId = typedArray.getResourceId(R.styleable.SlideUpPanelLayout_mainView_id, -1);
        mSlideViewId = typedArray.getResourceId(R.styleable.SlideUpPanelLayout_slideView_id, -1);
        mScrollableViewId = typedArray.getResourceId(R.styleable.SlideUpPanelLayout_can_scroll_id, -1);
        mPanelDefaultHeight = (int) typedArray.getDimension(R.styleable.SlideUpPanelLayout_panelHeight, -1);
        typedArray.recycle();
        viewDragHelper = ViewDragHelper.create(this, new SlideUpPanelDragCallback());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mMainView = findViewById(mMainViewId);
        mSlideView = findViewById(mSlideViewId);
        mScrollableView = findViewById(mScrollableViewId);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        widthSize = MeasureSpec.getSize(widthMeasureSpec);
        heightSize = MeasureSpec.getSize(heightMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            int childWidth = 0;
            int childHeight = 0;
            View child = getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            childWidth = widthSize - layoutParams.leftMargin - layoutParams.rightMargin;
            if (child == mMainView) {
                childHeight = heightSize - mPanelDefaultHeight;
            } else {
                childHeight = heightSize;
            }
            int childHeightMeasureSpec;
            int childWidthMeasureSpec;
            if (layoutParams.height == LayoutParams.MATCH_PARENT) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.EXACTLY);
            } else if (layoutParams.height == LayoutParams.WRAP_CONTENT) {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(childHeight, MeasureSpec.AT_MOST);
            } else {
                childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.height, MeasureSpec.EXACTLY);
            }
            if (layoutParams.width == LayoutParams.MATCH_PARENT) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.EXACTLY);
            } else if (layoutParams.width == LayoutParams.WRAP_CONTENT) {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidth, MeasureSpec.AT_MOST);
            } else {
                childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(layoutParams.width, MeasureSpec.EXACTLY);
            }
            measureChild(child, childWidthMeasureSpec, childHeightMeasureSpec);
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int measuredChildHeight = child.getMeasuredHeight();
            int measuredChildWidth = child.getMeasuredWidth();
            if (child == mMainView) {
                child.layout(0, 0, measuredChildWidth, measuredChildHeight);
            } else if (child == mSlideView) {
                mPanelTop = getMeasuredHeight() - mPanelDefaultHeight;
                lastTop = mPanelTop;
                MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
                leftMargin = layoutParams.leftMargin;
                if (changed) {
                    slideTop = mPanelTop;
                }
                child.layout(leftMargin, slideTop, measuredChildWidth + leftMargin, slideTop + measuredChildHeight);
            }
        }
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams p) {
        return new MarginLayoutParams(p);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        float lastY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownY = ev.getY();
                viewDragHelper.processTouchEvent(ev);
                break;
            default:
                float scrollableViewScrollPosition = getScrollableViewScrollPosition();
                if ((lastY - mDownY > 0 && scrollableViewScrollPosition == 0) || (mSlideView.getTop() > 0 && mSlideView.getTop() < mPanelTop)) {
                    return true;
                }
        }
        return false;
    }

    public float getScrollableViewScrollPosition() {
        if (mScrollableView instanceof NestedScrollView) {
            return this.mScrollableView.getScrollY();
        } else if (mScrollableView instanceof RecyclerView) {
            RecyclerView recyclerView = (RecyclerView) this.mScrollableView;
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            View child = layoutManager.getChildAt(0);
            return child.getTop();
        }
        return 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public class SlideUpPanelDragCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View view, int i) {
            return view == mSlideView;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (top < 0) {
                return 0;
            } else if (top > mPanelTop) {
                return mPanelTop;
            } else {
                return top;
            }
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            Log.e("clampHorizontal", leftMargin + "");
            return leftMargin;
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (yvel < 0) {
                viewDragHelper.smoothSlideViewTo(mSlideView, 0, 0);
            } else {
                viewDragHelper.smoothSlideViewTo(mSlideView, 100, mPanelTop);
            }
            ViewCompat.postInvalidateOnAnimation(SlideUpPanelLayout.this);
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            float deltaTop = top - lastTop;
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            float offset = ((float) (top - mPanelTop)) / mPanelTop;
            float translateY = (offset) * 200;
            mMainView.setTranslationY(translateY);
            MarginLayoutParams layoutParams = (MarginLayoutParams) mSlideView.getLayoutParams();
            slideTop = top;
            if ((layoutParams.leftMargin > 0 && deltaTop < 0) || (layoutParams.leftMargin < 100 && deltaTop > 0)) {
                layoutParams.leftMargin = layoutParams.rightMargin = (int) (100 + offset * 100f);
                mSlideView.setLayoutParams(layoutParams);
            }
            lastTop = top;
        }
    }
}
