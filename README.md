# FlyBanner
支持无限循环的广告栏控件

###Demo
![](image/gif1.gif)

###Gradle

	dependencies {
  		compile 'com.recker.flybanner:flybanner:1.1'      
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
