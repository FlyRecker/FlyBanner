# FlyBanner
支持无限循环的广告栏控件

###Demo
![](image/gif1.gif)

###Gradle

	dependencies {
  		compile 'com.recker.flybanner:flybanner:1.3'      
	}

###Usage


**Config in xml**

	<com.recker.flybanner.FlyBanner
		android:id="@+id/banner_1"
		android:layout_width="match_parent"
		android:layout_height="200dp"/>
		
		

**Config in java**

	/**
     * 加载本地图片
     */
    private void initLocalBanner() {
        mBannerLocal = (FlyBanner) findViewById(R.id.banner_1);

        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.img_1);
        images.add(R.drawable.img_2);
        images.add(R.drawable.img_3);
        images.add(R.drawable.img_4);
        mBannerLocal.setImages(images);

        mBannerLocal.setOnItemClickListener(new FlyBanner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                toast("点击了第"+position+"张图片");
            }
        });
    }

    /**
     * 加载网页图片
     */
    private void initNetBanner() {
        mBannerNet = (FlyBanner) findViewById(R.id.banner_2);

        List<String> imgesUrl = new ArrayList<>();
        for (int i = 0; i < mImagesUrl.length; i++) {
            imgesUrl.add(mImagesUrl[i]);
        }
        mBannerNet.setImagesUrl(imgesUrl);

        mBannerNet.setOnItemClickListener(new FlyBanner.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                toast("点击了第" + position + "张图片");
            }
        });
    }


###xml参数说明：

参数 | 说明 | 类型
--- | --- | ---
points_visibility | 指示器是否可见 | boolean
points_position | 指示器位置(左，中，右) | int
points\_container_background | 指示器容器背景 | Drawable


###方法说明：

方法 | 参数 | 说明
--- | --- | ---
setPointsIsVisible | isVisible | 指示器是否可见
setPoinstPosition | position | 指示器位置(左，中， 右)




###更新说明：

**1.3:**<br>
1、修复内存溢出问题

**1.2:**<br>
1、修改一张图片时不轮播<br>
2、新增xml和方法设置指示器，具体看参数说明

**1.1:**<br>
1、支持轮播网络图片


<!--###说明
　　　由于轮播图在开发中用得还是比较频繁的，因此花了几天来研究轮播图，目前也有很多好开的开源项目，用的人也比较多，例如：[Android-ConvenientBanner](https://github.com/saiwu-bigkoo/Android-ConvenientBanner)、[FlycoBanner](https://github.com/H07000223/FlycoBanner_Master)等等，使用的人还是挺多的，经过研究源码，发现这两者都是基于[LoopingViewPager](https://github.com/imbryk/LoopingViewPager)开发的，具体开发思路是C’, A, B, C, A‘模式来的，而C'和A'在通过缓存的形式来展示的，具体可以去看看源码，这里我就不多说了，我具体说说我遇到的问题。<br/>
　　　首先我尝试着把ViePager的Count设置为int的最大值，然后取中间值，来开发，这个方法可以实现轮播，自动轮播也是能行的，关键问题在与我们在滑动的时候体验不好，你可以参考这个项目[BannerLayout](https://github.com/dongjunkun/BannerLayout)，当然有一部分原因是我水平不够，没能解决这个滑动冲突，因此我放弃了这个思路。因此我采用了另外一个办法，即C‘, A, B, C, A'的模式，C’对应C，A‘对应A，通过障眼法来实现轮播图，-->
　　　

