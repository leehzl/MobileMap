package com.lilynlee.mobilemap.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.lilynlee.mobilemap.R;

public class ArcMenu extends ViewGroup implements View.OnClickListener{
    private static final int POS_LEFT_TOP = 0;
    private static final int POS_LEFT_BOTTOM = 1;
    private static final int POS_RIGHT_TOP = 2;
    private static final int POS_RIGHT_BOTTOM = 3;

    /*菜单位置的枚举类*/
    public enum Position{
        LEFT_TOP,LEFT_BOTTOM,RIGHT_TOP,RIGHT_BOTTOM
    }

    /*菜单状态的枚举类*/
    public enum Status{
        OPEN,CLOSE
    }
    private Position mPosition = Position.RIGHT_BOTTOM;     //初始化位置为右下角
    private Status mCurrentStatus = Status.CLOSE;                  //初始化菜单状态是关闭状态
    private int mRadius;                                    //子菜单的半径
    private View mCButton;                                  //菜单的主按钮
    private OnMenuItemCliceListener mMenuItemClickListener;
    /**
     * 点击子菜单按钮的回调函数
     */
    public interface OnMenuItemCliceListener{
        void onClick(View view,int position);
    }

    public void setOnMenuItemClickListener(OnMenuItemCliceListener mMenuItemClickListener) {
        this.mMenuItemClickListener = mMenuItemClickListener;
    }

    public ArcMenu(Context context) {
        this(context,null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //设置半径的默认值，设置为100dp
        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100
                , getResources().getDisplayMetrics());

        //获取自定义属性的值
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs
                , R.styleable.ArcMenu,defStyleAttr,0);
        int position =  a.getInt(R.styleable.ArcMenu_position,POS_RIGHT_BOTTOM);
        switch(position){
            case POS_LEFT_TOP:
                mPosition = Position.LEFT_TOP;
                break;
            case POS_LEFT_BOTTOM:
                mPosition = Position.LEFT_BOTTOM;
                break;
            case POS_RIGHT_TOP:
                mPosition = Position.RIGHT_TOP;
                break;
            case POS_RIGHT_BOTTOM:
                mPosition = Position.RIGHT_BOTTOM;
                break;
            default:
                break;
        }

        mRadius = (int) a.getDimension(R.styleable.ArcMenu_radius
                , TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,100
                        , getResources().getDisplayMetrics()));
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        for (int i = 0;i<count;i++){
            //测量每一个子布局
            measureChild(getChildAt(i),widthMeasureSpec,heightMeasureSpec);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed){
            layoutCButton();
            int count = getChildCount();
            for(int i = 0;i<count-1;i++){
                View child = getChildAt(i+1);
                child.setVisibility(View.GONE);
                int child_left = (int) (mRadius*Math.sin(Math.PI/2/(count-2)*i));
                int child_top = (int) (mRadius*Math.cos(Math.PI/2/(count-2)*i));
                int child_width = child.getMeasuredWidth();
                int child_height = child.getMeasuredHeight();

                //如果菜单位置在底部  左下  右下
                if (mPosition == Position.LEFT_BOTTOM||mPosition == Position.RIGHT_BOTTOM){
                    child_top = getMeasuredHeight() - child_height - child_top;
                }

                //如果菜单位置在右边 右上 右下
                if (mPosition == Position.RIGHT_TOP||mPosition == Position.RIGHT_BOTTOM){
                    child_left = getMeasuredWidth() - child_width - child_left;
                }

                child.layout(child_left,child_top,child_left+child_width,child_top+child_height);
            }
        }
    }

    /**
     * 定位主菜单按钮
     */
    private void layoutCButton(){

        mCButton = getChildAt(0);
        mCButton.setOnClickListener(this);
        int left = 0;
        int top = 0;
        int width = mCButton.getMeasuredWidth();
        int height = mCButton.getMeasuredHeight();

        switch (mPosition){
            case LEFT_TOP:
                left = 0;
                top = 0;
                break;
            case LEFT_BOTTOM:
                left = 0;
                top = getMeasuredHeight()-height;
                break;
            case RIGHT_TOP:
                left = getMeasuredWidth()-width;
                top = 0;
                break;
            case RIGHT_BOTTOM:
                left = getMeasuredWidth() - width;
                top = getMeasuredHeight() - height;
                break;
            default:
                break;
        }

        mCButton.layout(left,top,left+width,top+width);
    }

    @Override
    public void onClick(View view) {
        mCButton = findViewById(R.id.menu_button);
        if (mCButton == null){
            mCButton = getChildAt(0);
        }

        rotateCButton(view, 0f, 360f, 300);  //实现点击主按钮，主按钮会旋转一圈的动画

        toggleMenu(300);                        //切换菜单的打开，闭合

    }

    /**
     * 切换菜单的打开，闭合
     * @param duration 菜单打开/闭合时间
     */
    public void toggleMenu(int duration) {
        //为menuItem添加平移动画和旋转动画
        int count = getChildCount();
        for (int i = 0;i<count-1;i++){
            final View child_view = getChildAt(i+1);
            child_view.setVisibility(View.VISIBLE);
            //动画结束：0，0

            //动画起始
            int child_left = (int) (mRadius*Math.sin(Math.PI/2/(count-2)*i));
            int child_top = (int) (mRadius*Math.cos(Math.PI/2/(count-2)*i));

            int x_flag = 1;     //x坐标加还是减
            int y_flag = 1;     //y坐标加还是减

            //如果菜单在左边，那么动画起始位置的x坐标是要减的
            if (mPosition == Position.LEFT_TOP||mPosition == Position.LEFT_BOTTOM){
                x_flag = -1;
            }
            //如果菜单在上边，那么动画起始位置的y坐标是要减的
            if (mPosition == Position.LEFT_TOP||mPosition == Position.RIGHT_TOP){
                y_flag = -1;
            }

            //设置menuItem动画：平移动画+旋转动画
            AnimationSet anim_set = new AnimationSet(true);

            //设置平移动画
            Animation tran_anim = null;

            if (mCurrentStatus == Status.CLOSE){
                //当前菜单状态是关闭，则设置打开的动画
                tran_anim = new TranslateAnimation(x_flag*child_left,0,y_flag*child_top,0);
                child_view.setClickable(true);
                child_view.setFocusable(true);
            }else{
                //当前菜单状态是打开，则设置关闭动画
                tran_anim = new TranslateAnimation(0,x_flag*child_left,0,y_flag*child_top);
                child_view.setClickable(false);
                child_view.setFocusable(false);
            }
            tran_anim.setFillAfter(true);
            tran_anim.setDuration(duration);
            tran_anim.setStartOffset((i*100)/count);
            /**
             * 监听动画结束，比如在关闭菜单操作动画结束时候，menuItem设置为不可见
             *                 打开菜单操作。动画结束时候，menuItem设置为可见
             */
            tran_anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if(mCurrentStatus == Status.CLOSE){
                        child_view.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            //旋转动画
            RotateAnimation rotate_anim = new RotateAnimation(0,720
                    , Animation.RELATIVE_TO_SELF
                    , 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotate_anim.setDuration(duration);  //设置旋转时间
            rotate_anim.setFillAfter(true);     //旋转完回到原来的位置

            //将动画添加到animationset中，一定要先加旋转动画，再加平移动画
            anim_set.addAnimation(rotate_anim);
            anim_set.addAnimation(tran_anim);
            child_view.startAnimation(anim_set);
            final int position = i+1;
            child_view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mMenuItemClickListener != null){
                        mMenuItemClickListener.onClick(view,position);
                        menuItemAnim(position-1);
                        changeStatus();
                    }
                }
            });
        }
        changeStatus();

    }

    /**
     *  改变当前状态
     */
    private void changeStatus() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE?Status.OPEN:Status.CLOSE);
    }

    /**
     * 实现点击主按钮，主按钮会旋转一圈的动画
     * @param view 主按钮View对象
     * @param start 动画旋转起始角度
     * @param end   动画旋转结束角度
     * @param duration  动画旋转时间
     */
    private void rotateCButton(View view, float start, float end, int duration) {
        RotateAnimation anim = new RotateAnimation(start,end, Animation.RELATIVE_TO_SELF
                , 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);          //设置动画属性：旋转角度，旋转的中心
        anim.setDuration(duration);                                 //设置旋转时间
        anim.setFillAfter(true);                                    //设置旋转完成之后就停止在原处
        view.startAnimation(anim);
    }

    /**
     * 添加menuItem点击动画
     * 当点击子菜单时候，点击的按键放大，其他按键缩小的动画
     * @param position 点击的按键所属位置
     */
    private void menuItemAnim(int position){
        for (int i = 0;i<getChildCount()-1;i++){
            View child_view = getChildAt(i+1);
            if (i== position){
                //如果是我们点击的按键
                child_view.startAnimation(scaleBigAnim(300));
            }else{
                //如果不是我们点击的按键
                child_view.startAnimation(scaleSmallAnim(300));
            }
            child_view.setClickable(false);
            child_view.setFocusable(false);
        }
    }

    /**
     * 当前点击的item设置变小，并且透明度降低的动画
     * @param duration 动画持续时间
     * @return 返回动画对象
     */
    private Animation scaleSmallAnim(int duration) {
        AnimationSet animation_set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,0.0f,1.0f,0.0f
                ,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f,0.0f);
        animation_set.addAnimation(scaleAnimation);
        animation_set.addAnimation(alphaAnimation);
        animation_set.setDuration(duration);
        animation_set.setFillAfter(true);
        return animation_set;
    }

    /**
     * 当前点击的item设置变大，并且透明度降低的动画
     * @param duration 动画持续时间
     * @return 返回动画对象
     */
    private Animation scaleBigAnim(int duration) {
        AnimationSet animation_set = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f,4.0f,1.0f,4.0f
                ,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f,0.0f);
        animation_set.addAnimation(scaleAnimation);
        animation_set.addAnimation(alphaAnimation);
        animation_set.setDuration(duration);
        animation_set.setFillAfter(true);
        return animation_set;
    }

    /**
     * 判断当前的菜单状态是否打开
     * @return 打开返回true，关闭返回false
     */
    public boolean isMenuOpen(){
        return mCurrentStatus == Status.OPEN;
    }
}
