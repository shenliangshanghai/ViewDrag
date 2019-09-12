# ViewDrag
仿高德地图底部拖拽控件

### 效果图
![图1](https://raw.githubusercontent.com/shenliangshanghai/ViewDrag/master/gif/1.gif)
![图2](https://raw.githubusercontent.com/shenliangshanghai/ViewDrag/master/gif/2.gif)

### 相关知识
#### 1、相关知识点涉及
      1.1、自定义view流程onMeasure,onLayout
      1.2、自定义属性
      1.3、ViewDragHepler类的使用（重点）
      1.4、事件分发
      1.5、自定义设置LayoutParams类型
      1.6、几个坐标相关属性之间的关系(x,y)、（rawX、rawY）、translateX  translateY、left  top right  bottom、scrollX  scrollY
#### 2、ViewGroup的子view实例化在onFinshInflate 中
       构造方法----->onFinshInflate()------>onMeasure()------>onLayout()
#### 3、onMeasure的注意点
##### 3.1、对于ViewGroup自身来说主要方法有：
```java
           int  widthMode=MeasureSpec.getMode(widthMeasureSpec);
           int  widthSize=MeasureSpec.getSize(widthMeasureSpec);
```
* 根据不同的Mode得到、设置不同size
* 需要用到setMeasureDimension(widthSize,heightSize);
* atMost模式需要注意,如果不处理atMost这种模式的话，即使你设置Warp_content,也是跟Math_parent是一个效果

##### 3.2、对于ChildView来说
* 需要调用measureChild 或者measureChildren对子view 进行测量
* 不直接使用measureChildren处理，使用measureChild 对每个子view 单独进行测量的话
* 根据LayoutParams得到mode，size,然后使用`int measureSpec=MeasureSpec.makeSpec(size,mode)`生成对应的测量工具

#### 4、onLayout注意点
* 摆放位置可能涉及到Margin等其他参数
##### 4.1、如何获取Margin 值，这里需要使用到LayoutParams，但是继承ViewGroup情况下的VIewGroup.LayoutParams，只能得到width、height,无法得到Margin值，这里就涉及到定义当前LayoutParams 的类型
##### 4.2、自定义LayoutParams 的类型涉及到的方法
```java
                 protected LayoutParams  generateDefaultLayoutParams(){
                          return new MarginLayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
                 }

                 protect LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp){
                          return new MarginLayoutParams(lp);
                 }

                 public LayoutParams generateLayoutParams(AttributeSet attr){
                          return new MarginLayoutParams(getContext(),attr);
                 }
```

#### 5、自定义属性
*  两种
##### 5.1、获取android自带属性
          int attr[] =new int[]{android.R.attr.gravity,android.R.attr.layout_weight};
          TypedArray ta=context.obtainStyleAttributes(attributeSet,attr);
          float attr1=ta.getFloat(int index(数组下标),float defaultValue);
##### 5.2、获取自定义属性
          TypedArray ta=context.obtainStyleAttributes(attributeSet,R.styleable.属性数组名);
          float attr1=ta.getFloat(int index(R.styleable.属性数组名_属性名),float defaultValue);


#### 6、ViewDragHelper的使用
##### 6.1、实例化
```java
ViewDragHelper viewDragHelper  =   ViewDragHelper.create(ViewGroup parent,ViewDragHelper.Callback callback);
```
##### 6.2、与事件分发相关的
###### 6.2.1、public boolean shouldInterceptTouchEvent(MotionEvent ev);  是否拦截事件,在无事件冲突的情况下,直接使用
```java
public boolean onInterceptTouchEvent(MotionEvent ev){
       return viewDragHelper.shouldInterceptTouchEvent(ev);
}
```
###### 6.2.2、public void processTouchEvent(MotionEvent ev); 代替TouchEvent事件中的处理逻辑，内部调用Callback中的方法
```java
public boolean TouchEvent(MotionEvent ev){
      viewDragHelper.processEvent(ev);
      return true;
}
```
###### 6.3、ViewDragHelper.Callback相关
###### 6.3.1、public boolean tryCaptureView(View child,int pointerId);根据手指触碰的位置处的View判断是否拦截事件,一般可写为
```java
public boolean tryCaptureView(View child,int pointerId){
       return  child==触发拦截事件的view
}
```
###### 6.3.2、public int clampViewPositionHorizontal(View view,int left,int dx)
* 返回手指拖动过程中，left的位置，手指一旦离开屏幕将不再调用该方法

###### 6.3.3、public int clampViewPositionVertical(View view,int top,int dy)
* 返回手指拖动过程中，top的位置，手指一旦离开屏幕将不再调用该方法

###### 6.3.4、public int getViewHorizontalDragRange(View child)
* 返回横向滑动最大距离差，该值与`shouldInterceptTouchEvent`返回结果有关，该值不大于TouchSlop(最小滑动距离)的话，`shouldInterceptTouchEvent()`一直为false

###### 6.3.5、public int getViewHorizontalDragRange(View child)
* 返回竖向滑动最大距离差，该值与shouldInterceptTouchEvent返回结果有关，该值不大于TouchSlop(最小滑动距离)的话，shouldInterceptTouchEvent()一直为false

###### 6.3.6、public void onViewRelease(View releaseChild,float xVel,float yVel);
* 手指放开时候回调，用于处理惯性滑动

###### 6.3.7、public void  onViewPositionChanged(View changedView, int left,int top,float dx,float dy);
* 整个滑动过程（包括惯性滑动）位置改变都会调用

###### 6.3.8、public void smoothSlidViewTo(View child,int finalLeft, int finalTop);一般用在onViewRelease方法中指定某个View滑动到某个位置,这是一个不断刷新的过程
```java
public void computeScroll(){
       if(viewDragHelper.continueSettling(true)){
          ViewCompat.postInvalidateOnAnimation(this);
       }
}
```
* continueSettling会一直回调mCallback.onViewPositionChanged

###### 6.3.9、public void settleCapturedViewAt(int finalLeft,int finalTop)
* 与3.8类似，但是指定了CapturedView

###### 6.4、shouldInterceptTouchEvent伪代码
```java
public boolean shouldInterceptTouchEvent(MotionEvent ev){
      case MotionEvent.ACTION_DOWN:
          初始化一些参数，这里State为IDLE
        break;
      case MotionEvent.ACTION_MOVE:
          boolean checkTouchSlop=(mCallback.getViewVerticalDragRange(child) > 0 &&Math.abs(dy)>mTouchSlop)||
          (mCallback.getViewHorizontalDragRange(child) > 0 &&Math.abs(dx)>mTouchSlop);
          if(toCapture != null && checkTouchSlop&&mCallback.tryCaptureView()){
             setDragState(STATE_DRAGGING);
          }
        break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
             setDragState(STATE_IDLE);
        break
        return mDragState==STATE_DRAGGING;
      }
```
###### 6.5、processTouchEvent伪代码
```java
public void processTouchEvent(MotionEvent ev){
      case MotionEvent.ACTION_DOWN:
           if(mCallback.tryCaptureView()){
              setDragState(STATE_DRAGGING);
           }
          break;
      case MotionEvent.ACTION_MOVE:
          if (mDragState == STATE_DRAGGING) {
              if (dx != 0) {
                  clampedX = mCallback.clampViewPositionHorizontal(mCapturedView, left, dx);
                  ViewCompat.offsetLeftAndRight(mCapturedView, clampedX - oldLeft);
              }
              if (dy != 0) {
                clampedY = mCallback.clampViewPositionVertical(mCapturedView, top, dy);
                ViewCompat.offsetTopAndBottom(mCapturedView, clampedY - oldTop);
              }
              if (dx != 0 || dy != 0) {
                final int clampedDx = clampedX - oldLeft;
                final int clampedDy = clampedY - oldTop;
                mCallback.onViewPositionChanged(mCapturedView, clampedX, clampedY,clampedDx, clampedDy);
              }
          }
          break;
      case MotionEvent.ACTION_UP:
          if (mDragState == STATE_DRAGGING) {
              mCallback.onViewReleased(mCapturedView, xvel, yvel);
              setDragState(STATE_IDLE);
          }
          break;
}
```
#### 7、几个坐标相关属性之间的关系
##### 7.1、MotionEvent事件中：
           rawx,rawy:相对于屏幕坐标点
           x,y:相对于当前控件的坐标

##### 7.2、 View位置定位中：
           x,y 是View左上角在父布局中的坐标
           left ,top,right,bottom:分别是左边，上边，右边，底部距离x轴，y轴的距离
           translateX/Y:左上角相对于父布局的偏移量

           x=left+translateX;
           y=top+translateY;

           scrollX，scrollY：内容的偏移，与left，x都没有关系，注意：正负号方向与坐标系方向相反
