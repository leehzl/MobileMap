package com.lilynlee.mobilemap.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lilynlee.mobilemap.R;

import java.util.List;

/**
 * Created by admin on 2016/8/5.
 */
public class ViewPagerIndicator extends LinearLayout {
    private static final int COUNT_DEFAULT_TAB = 2; //可见数量默认为2个
    private static final int COLOR_TEXT_NORMAL = 0x77ffffff;
    private static final int COLOR_TEXT_HIGHLIGHT = 0xffffffff;

    private List<String> mTabTitles;    //tab的标题

    private int mTabVisibleCount;       //页面中可以见的Tab数量

    private Paint mPaint;               //三角形图案
    private Path mPath;                 //三角形的三条边
    private int mTrangleWidth;         //三角形的宽度
    private int mTrangleHeight;         //三角形的高度
    private static final float RADIO_TRIANGLE_WIDTH = 1/5f; //三角形的高度在tag中的比例为 1/5
    private int mInitTranslationX;      //三角形的初始化的偏移位置
    private int mTranslationX;          //三角型的移动时候的位置

    /*ViewPager相关*/
    private ViewPager mViewPager;

    /*如果用户想使用PageChange的监听接口，但是我们在代码中已经实现了这个接口了，
    * 所以我们需要创造一个接口，让用户可以回调*/
    public interface PageOnChangeListener{
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);
        public void onPageSelected(int position);
        public void onPageScrollStateChanged(int state);
    }

    /*总结：
    * 我们需要实现一个接口，提供给外部进行设置，以便外部可以对listener可以进行重写监听
    * 最后可以调用indecator.setOnPageChangeListener方法可以对该监听进行实现
    * 最后需要在这里已经实现的接口函数设置回调
    * if(mPageOnChangeListener != null){
           mPageOnChangeListener.onPageScrolled(position,positionOffset,positionOffsetPixels);
      }
    * */

    public PageOnChangeListener mPageOnChangeListener;
    public void setOnPageChangeListener(PageOnChangeListener listener){
        this.mPageOnChangeListener = listener;
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        /*获取自定义属性,获取可见的Tab数量*/
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_visible_tab_count,COUNT_DEFAULT_TAB);
        if (mTabVisibleCount<0){mTabVisibleCount = COUNT_DEFAULT_TAB;}

        a.recycle();
        /*初始化画笔*/
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);                   //设置画笔的颜色为白色
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setPathEffect(new CornerPathEffect(3));  //设置三角形的角不那么尖锐
    }

    /**
     * 当加载完xml文件时候启动该方法
     * 在该方法中判断VisibleCount的个数。然后给每个Tab设置宽度
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        int child_count = getChildCount();      //获得子元素的个数
        if(child_count == 0){
            return;
        }
        for (int i =0;i<child_count;i++){
            View view = getChildAt(i);
            LinearLayout.LayoutParams lp = (LayoutParams) view.getLayoutParams();
            lp.weight = 0;
            lp.width = getScreenWidth()/mTabVisibleCount;
            view.setLayoutParams(lp);
        }
        setTabItemClickEvent();
    }

    /**
     * 获取屏幕的宽度
     * @return 屏幕宽度
     */
    private int getScreenWidth() {
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrice = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrice);
        return outMetrice.widthPixels;
    }

    public ViewPagerIndicator(Context context) {
        this(context,null);
    }

    /**
     * 控件当宽高发生变化时候就会调用该方法
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mTrangleHeight = (int)(h*RADIO_TRIANGLE_WIDTH);             //设置三角形的高度为TextView的1/5
        mTrangleWidth = mTrangleHeight*2;                           //设置三角形的底边为高度的2倍
        mInitTranslationX = w/mTabVisibleCount/2-(mTrangleWidth/2); //三角形的初始化偏移位置为每个Tag的中间，往左偏移1/2个三角形宽度
        initTrangle();
    }

    /**
     * 初始化三角形,高度为底部长度的1/2
     */
    private void initTrangle() {
        mPath  = new Path();
        mPath.moveTo(0,0);
        mPath.lineTo(mTrangleWidth,0);
        mPath.lineTo(mTrangleWidth/2,-mTrangleHeight);
        mPath.close();
    }

    /**
     * 三角形的绘制
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mInitTranslationX+mTranslationX,getHeight());
        canvas.drawPath(mPath,mPaint);

        canvas.restore();
        super.dispatchDraw(canvas);
    }

    /**
     * 当手指平移的时候，触发该函数，使三角形也跟着移动
     * @param position
     * @param offset
     */
    public void scroll(int position, float offset) {
        int tabWidth = getWidth()/mTabVisibleCount;        //获取一个Tab的宽度
        mTranslationX = (int) (tabWidth*(offset+position));

        /*tab移动，当tab移动到最后一个的时候*/
        if (position>=(mTabVisibleCount-2)&&offset>0&&getChildCount()>mTabVisibleCount){
            if(mTabVisibleCount != 1){
                this.scrollTo((position-(mTabVisibleCount-2))*tabWidth+(int)(tabWidth*offset),0);
            }else{
                this.scrollTo(position*tabWidth+(int)(tabWidth*offset),0);
            }
        }
        invalidate();
    }

    public void setTabItemTitles(List<String> titles){
        if (titles != null&titles.size()>0){
            this.removeAllViews();
            mTabTitles = titles;
            for (String title : mTabTitles){
                addView(generateTextView(title));
            }
            setTabItemClickEvent();
        }

    }

    /**
     * 根据title创建TextView
     * @param title TextView的内容
     * @return 创建的TextView
     */
    private View generateTextView(String title) {
        TextView tv = new TextView(getContext());
        LinearLayout.LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        lp.width = getScreenWidth()/mTabVisibleCount;
        tv.setText(title);
        tv.setGravity(Gravity.CENTER);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        tv.setTextColor(COLOR_TEXT_NORMAL);
        tv.setLayoutParams(lp);
        tv.setPadding(0,10,0,14);
        return tv;
    }

    /**
     * 设置Tab可见个数
     * @param count
     */
    public void setVisibleTabCount(int count){
        mTabVisibleCount = count;
    }

    /**
     * 设置关联的ViewPager
     */
    public void setViewPager(ViewPager viewPager, final int pos){
        mViewPager = viewPager;

        /*监听平移ViewPager动作*/
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //      tabWidth*positionOffset + position*tabWidth
                scroll(position,positionOffset);
                if(mPageOnChangeListener != null){
                    mPageOnChangeListener.onPageScrolled(position,positionOffset,positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if(mPageOnChangeListener != null){
                    mPageOnChangeListener.onPageSelected(position);
                }
                HighlightTextView(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(mPageOnChangeListener != null){
                    mPageOnChangeListener.onPageScrollStateChanged(state);
                }
            }
        });

        mViewPager.setCurrentItem(pos);
        HighlightTextView(pos);
    }

    private void ResetTextViewColor(){
        for (int i = 0;i<getChildCount();i++){
            View view = getChildAt(i);
            ((TextView)view).setTextColor(COLOR_TEXT_NORMAL);
        }
    }

    /**
     * 对选中的TextView的文本高亮显示，其他TextVIew的颜色设置为一般颜色
     * @param pos 选中的TextView位置
     */
    private void HighlightTextView(int pos){
        ResetTextViewColor();
        View view = getChildAt(pos);
        if (view instanceof TextView){
            ((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHT);
        }
    }

    /**
     * 设置Tab的点击事件
     */
    private void setTabItemClickEvent(){
        int child_count = getChildCount();
        for (int i = 0;i<child_count;i++){
            final int j = i;
            View view = getChildAt(j);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mViewPager.setCurrentItem(j);
                }
            });
        }
    }
}
