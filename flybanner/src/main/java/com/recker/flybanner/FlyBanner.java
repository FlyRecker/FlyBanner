package com.recker.flybanner;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by recker on 16/4/17.
 */
public class FlyBanner extends RelativeLayout {

    private static final int RMP = LayoutParams.MATCH_PARENT;

    private static final int RWP = LayoutParams.WRAP_CONTENT;

    private static final int LWC = LinearLayout.LayoutParams.WRAP_CONTENT;

    private static final int WHAT_AUTO_PLAY = 1000;

    private LinearLayout mPointRealContainerLl;

    private ViewPager mViewPager;
    //本地图片资源
    private List<Integer> mImages;
    //网络图片资源
    private List<String> mImageUrls;
    //是否是网络图片
    private boolean mIsImageUrl = false;

    private ViewPagerScroller mScroller;
    //是否可以自动播放
    private boolean mAutoPlayAble = true;
    //是否正在播放
    private boolean mIsAutoPlaying = false;
    //自动播放时间
    private int mAutoPalyTime = 5000;
    //页面切换时间
    private int mPageChangeDuration = 400;

    //当前页面位置
    private int mCurrentPositon;
    //指示点位置
    private PointPosition mPointPosition = PointPosition.CENTER;

    private int mPointDrawableResId = R.drawable.selector_bgabanner_point;
    //指示容器背景
    private Drawable mPointContainerBackgroundDrawable;

    private Handler mAutoPlayHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mCurrentPositon++;
            mViewPager.setCurrentItem(mCurrentPositon);
            mAutoPlayHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPalyTime);
        }
    };

    public FlyBanner(Context context) {
        this(context, null);
    }

    public FlyBanner(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlyBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        //设置指示器背景
        mPointContainerBackgroundDrawable = new ColorDrawable(Color.parseColor("#44aaaaaa"));
        //添加ViewPager
        mViewPager = new ViewPager(context);
        addView(mViewPager, new LayoutParams(RMP, RMP));

        //初始化ViewPager滑动速度
//        initPageChangeDuration();
        //设置指示器容器
        RelativeLayout pointContainerRl = new RelativeLayout(context);
        if (Build.VERSION.SDK_INT >= 16) {
            pointContainerRl.setBackground(mPointContainerBackgroundDrawable);
        } else {
            pointContainerRl.setBackgroundDrawable(mPointContainerBackgroundDrawable);
        }
        //设置内边距
        pointContainerRl.setPadding(0, 10, 0, 10);
        //设定指示器容器布局及位置
        LayoutParams pointContainerLp = new LayoutParams(RMP, RWP);
        pointContainerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        addView(pointContainerRl, pointContainerLp);

        mPointRealContainerLl = new LinearLayout(context);
        mPointRealContainerLl.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams pointRealContainerLp = new LayoutParams(RWP, RWP);
        pointContainerRl.addView(mPointRealContainerLl, pointRealContainerLp);
        //设置布局位置
        if (mPointPosition == PointPosition.CENTER) {
            pointRealContainerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        } else if (mPointPosition == PointPosition.LEFT) {
            pointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        } else if (mPointPosition == PointPosition.RIGHT) {
            pointRealContainerLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        }
    }

    private void initPageChangeDuration() {
        try {
            Field scroller = ViewPager.class.getDeclaredField("mScroller");
            scroller.setAccessible(true);
            mScroller = new ViewPagerScroller(mViewPager.getContext());
            scroller.set(mViewPager, mScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void setImages(List<Integer> images) {
        //加载本地图片
        mIsImageUrl = false;
        this.mImages = images;
        //初始化ViewPager
        initViewPager();
    }

    public void setImagesUrl(List<String> urls) {
        //加载网络图片
        mIsImageUrl = true;
        this.mImageUrls = urls;
        //初始化ViewPager
        initViewPager();
    }

    private void initViewPager() {
        //添加指示点
        addPoints();
        //设置ViewPager
        FlyPageAdapter adapter = new FlyPageAdapter();
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        //跳转到首页
        mViewPager.setCurrentItem(1, false);
        //开始轮播
        startAutoPlay();
    }


    private int toRealPosition(int position) {
        int realPosition;
        if (mIsImageUrl) {
            realPosition = (position - 1) % mImageUrls.size();
            if (realPosition < 0)
                realPosition += mImageUrls.size();
        } else {
            realPosition = (position - 1) % mImages.size();
            if (realPosition < 0)
                realPosition += mImages.size();
        }

        return realPosition;
    }

    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            if (mIsImageUrl) {
                mCurrentPositon = position % (mImageUrls.size() + 2);
            } else {
                mCurrentPositon = position % (mImages.size() + 2);
            }
            switchToPoint(toRealPosition(mCurrentPositon));
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                int current = mViewPager.getCurrentItem();
                int lastReal = mViewPager.getAdapter().getCount()-2;
                if (current == 0) {
                    mViewPager.setCurrentItem(lastReal, false);
                } else if (current == lastReal+1) {
                    mViewPager.setCurrentItem(1, false);
                }
            }
        }
    };

    private class FlyPageAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            if (mIsImageUrl)
                return mImageUrls.size() + 2;

            return mImages.size()+2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            ImageView imageView = new ImageView(getContext());
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(toRealPosition(position));
                    }
                }
            });
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            if (mIsImageUrl) {
                Picasso.with(getContext())
                        .load(mImageUrls.get(toRealPosition(position)))
                        .into(imageView);
            } else {
                imageView.setImageResource(mImages.get(toRealPosition(position)));
            }
            container.addView(imageView);

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View)object);
        }
    }


    /**
     * 添加指示点
     */
    private void addPoints() {
        mPointRealContainerLl.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LWC, LWC);
        lp.setMargins(10, 10, 10, 10);
        ImageView imageView;
        if (mIsImageUrl) {
            for (int i = 0; i < mImageUrls.size(); i++) {
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(lp);
                imageView.setImageResource(mPointDrawableResId);
                mPointRealContainerLl.addView(imageView);
            }
        } else {
            for (int i = 0; i < mImages.size(); i++) {
                imageView = new ImageView(getContext());
                imageView.setLayoutParams(lp);
                imageView.setImageResource(mPointDrawableResId);
                mPointRealContainerLl.addView(imageView);
            }
        }
        switchToPoint(0);
    }

    /**
     * 切换指示器
     * @param currentPoint
     */
    private void switchToPoint(final int currentPoint) {
        for (int i = 0; i < mPointRealContainerLl.getChildCount(); i++) {
            mPointRealContainerLl.getChildAt(i).setEnabled(false);
        }
        mPointRealContainerLl.getChildAt(currentPoint).setEnabled(true);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mAutoPlayAble) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    stopAutoPlay();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_OUTSIDE:
                    startAutoPlay();
                    break;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 开始播放
     */
    public void startAutoPlay() {
        if (mAutoPlayAble && !mIsAutoPlaying) {
            mIsAutoPlaying = true;
            mAutoPlayHandler.sendEmptyMessageDelayed(WHAT_AUTO_PLAY, mAutoPalyTime);
        }
    }

    /**
     * 停止播放
     */
    public void stopAutoPlay() {
        if (mAutoPlayAble && mIsAutoPlaying) {
            mIsAutoPlaying = false;
            mAutoPlayHandler.removeMessages(WHAT_AUTO_PLAY);
        }
    }

    /**
     * 定义指示点位置
     */
    public enum PointPosition {//中，右，左
        CENTER,
        RIGHT,
        LEFT
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
