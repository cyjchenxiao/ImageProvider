package com.qiyi.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;

import com.qiyi.imageprovider.ImageProviderApi;
import com.qiyi.imageprovider.R;
import com.qiyi.imageprovider.base.IImageCallback;
import com.qiyi.imageprovider.base.IImageProvider;
import com.qiyi.imageprovider.base.ImageRequest;
import com.qiyi.imageprovider.base.ImageRequest.ImageType;
import com.qiyi.imageprovider.logic.ImageProvider;

public class App extends Activity {
    private static final String TAG = "ImageProvider/App";
    private IImageProvider mImageProvider = ImageProviderApi.getImageProvider();
    private ImageView mOriginalImage;
    private ImageView mProcessedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initImageProvider(this);
        testLoadImage();
    }

    private void initViews() {
        mOriginalImage = (ImageView) findViewById(R.id.image_original);
//        mProcessedImage = (ImageView) findViewById(R.id.image_processed);
    }

    private void initImageProvider(Context context) {
//    	ImageProvider.get().setCacheMode(CacheMode.CACHE_FIRST);
//    	ImageProvider.get().setPingbackIsPermit(false);
        ImageProvider.get().initialize(context, "");
        ImageProvider.get().setEnableScale(true);
        ImageProvider.get().setPingbackIsPermit(false);
    }

    private void testLoadImage() {
        PerformanceProfiler.startProfiling("testLoadImage");
//        String url = "http://d.pan.iqiyi.com/download/7207?authtype=passport&authtoken=afgbAOfTXwb1TLf3NcEiUFx04gxXWJhPgWv0zzNbnKnaYm4cb";
//        String url = "http://d.pan.iqiyi.com/download/2702?authtype=passport&authtoken=2bm1giRBSxxzZMwbqri5aoqjGUh1fbIm1f81ovZdO14YrNEm442";
//        String url = "http://pic0.qiyipic.com/common/lego/20150313/a4c5e0ab68584e05a38a7ae5b2d330e0.jpg";
//        String url = "http://preview.openapi.iqiyi.com/ugcpreview/15df564d3f68a6d8ece1311a66814805.jpeg?ip=10.11.75.150";
//        String url = "http://preview.openapi.iqiyi.com/ugcpreview/15df564d3f68a6d8ece1311a66814805.jpeg";
//        String url = "http://preview.openapi.iqiyi.com/ugcpreview/c53a15581c23f3018d1846abce11c869.jpeg";
//        String url = "http://mmbiz.qpic.cn/mmbiz/JRORMFV15lEtEsbX528jB1lDQWdW2ayqO17CoqTVbVawrjPLIz2QkktMtice9bZCFC0pja638dM9zY9icOUKibzuw/0";
//        String url = "http://pic5.qiyipic.com/common/lego/20150331/8cfccc8b9f654404aa0883cae55dc1c6.jpg";
        String url2 = "http://fujian.86516.com/forum/201209/28/16042484m9y9izwbrwuixj.jpg";
        String url3 = "http://www.pp3.cn/uploads/allimg/111128/1414302040-9.jpg";
        String url4 = "http://www.pp3.cn/uploads/allimg/111110/15594T106-10.jpg";
        String url5 = "http://img5.duitang.com/uploads/item/201407/30/20140730152939_zPFKy.thumb.700_0.jpeg";
        String url6 = "http://img2.3lian.com/2014/f5/158/d/86.jpg";
        String url7 = "http://images.99pet.com/InfoImages/wm600_450/1d770941f8d44c6e85ba4c0eb736ef69.jpg";
        String url8 = "http://images.99pet.com/InfoImages/wm600_450/ef48d0d8e8f64172a28b9451fc5a941d.jpg";
        String url9 = "http://img2.3lian.com/2014/f5/158/d/87.jpg";
        String url10 = "http://p18.qhimg.com/t0144d6a0802f22be4f.jpg";
        String url11 = "http://ww2.sinaimg.cn/mw600/c6c1d258jw1e5r59ttpkcj20gu0gsmyh.jpg";
        String url12 = "http://ww1.sinaimg.cn/mw600/bce7ca57gw1e4rg0coeqqj20dw099myu.jpg";
//        String url = "http://pic4.qiyipic.com/common/lego/20150401/ea58eb8ecdfa4fc5a87b040ab7b659c89.jpg";
//        String url = "http://pic.33.la/20121105bzjf/376.jpg";
//        String url = "http://d.pan.ptqy.gitv.tv/share/?id=2badb624d3212bd6df36abb0521f1394&token=Wz2g43TjaDKclw4_NmKGDdJiBO0AsAUiAt7Xrd-Xj8uKlFBByLNOAizpmmXAzt_y_YidSVklGJ8MddHHwxPEJQocbf60NUUKSiAD1A5JTZ86TEz6OleB6BvrNJ1p8zmSolZHzeQzOY_lCeL6qKFG20NFfPOTeSDIe8aBBXqUA7MlrTwZxcHR10jLgHeWBAQV";
//        String url = "http://d.pan.ptqy.gitv.tv/share/?id=69ad8bec1dbdc2e49d486022ca8e6a47&token=V_fAEakwCgRk-9ZT2WF1EgmO0IPh9lsUGJne7MuEwTnv80a6wC62an_0CSgej3kc723eDr5gbQb1KAjuJFLRQQEbvsoYjSO1kr6884N_KvnFjQiGnlMULxg_-x0e7zQaaTk7QBCXjsBuouevpTK_dkZogWninV6Di1AewEm8LiT8uRB1zBadldB8U2_ooLhLh8Z5WeRf31DtayJe5tGxkQ";

//        mImageProvider.setDecodeConfig(Config.RGB_565);
//        mImageProvider.setDecodeConfig(Config.ARGB_4444);
        // original image (left)
//        String url = "";
//        ImageRequest origRequest = new ImageRequest(url);
//        origRequest.setTargetWidth(1000);
//        origRequest.setTargetHeight(250);
//        origRequest.setScaleType(ScaleType.CENTER_INSIDE);
//        origRequest.setScaleType(ScaleType.CENTER_CROP);
        //        origRequest.setDecodeConfig(Bitmap.Config.ARGB_8888);
//        origRequest.setRoundCornerSpecs(new CornerSpec(Corner.TOP_LEFT, 50));
//        origRequest.setTargetWidth(264);
//        origRequest.setTargetHeight(145);
//        origRequest.setScaleType(ScaleType.CENTER_INSIDE);
//        mImageProvider.loadImage(origRequest, new IImageCallback() {
//            @Override
//            public void onSuccess(ImageRequest imageRequest, final Bitmap bitmap) {
//                Log.d(TAG, "loadImage original onSuccess: url=" + imageRequest.getUrl()
//                        + ", bitmap=" + LogUtils.printBitmap(bitmap));
//                PerformanceProfiler.stopProfiling();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mOriginalImage.setImageBitmap(bitmap);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(ImageRequest imageRequest, Exception ex) {
//                Log.d(TAG, "loadImage original onFailure: url=" + imageRequest.getUrl());
//                PerformanceProfiler.stopProfiling();
//            }
//        });
        
        List<ImageRequest> requestList = new ArrayList<ImageRequest>();
//        requestList.add(new ImageRequest(url));
//        requestList.add(new ImageRequest(url2));
//        requestList.add(new ImageRequest(url3));
        
        
//        ImageRequest a = new ImageRequest(url11);
//        a.setSavePath("/mnt/sdcard/cyj/");
//        ImageRequest b = new ImageRequest(url12);
//        b.setSavePath("/mnt/sdcard/cyj/");
//        ImageRequest c = new ImageRequest(url6);
//        c.setSavePath("/data/data/com.qiyi.imageprovider/cyj/");
//        ImageRequest d = new ImageRequest(url9);
//        d.setSavePath("/data/data/com.qiyi.imageprovider/cyj/");
//        requestList.add(a);
//        requestList.add(b);
//        requestList.add(c);
//        requestList.add(d);
        
        
        
        requestList.add(new ImageRequest(url2));
        requestList.add(new ImageRequest(url3));
        requestList.add(new ImageRequest(url4));
        requestList.add(new ImageRequest(url5));
        requestList.add(new ImageRequest(url6));
        requestList.add(new ImageRequest(url7));
        requestList.add(new ImageRequest(url8));
        requestList.add(new ImageRequest(url9));
        requestList.add(new ImageRequest(url10));
        requestList.add(new ImageRequest(url11));
        requestList.add(new ImageRequest(url12));
//        requestList.add(new ImageRequest(""));
//        requestList.add(new ImageRequest(null));
//        requestList.add(new ImageRequest(url));
        
//        mImageProvider.loadImages(requestList, new IImageCallback() {
//            @Override
//            public void onSuccess(ImageRequest imageRequest, final Bitmap bitmap) {
//                runOnUiThread(new Runnable() {
//					@Override
//					public void run() {
//						mOriginalImage.setImageBitmap(bitmap);
//					}
//				});
//            }
//            
//            @Override
//            public void onFailure(ImageRequest imageRequest, Exception ex) {
//                Log.d(TAG, "loadImage original onFailure: url=" + imageRequest.getUrl());
//            }
//        });
        
        
        ImageRequest request = new ImageRequest(url6);
        request.setImageType(ImageType.ROUND);
        request.setRadius(10);
        mImageProvider.loadImage(request, new IImageCallback() {
			
			@Override
			public void onSuccess(ImageRequest imageRequest, final Bitmap bitmap) {
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						mOriginalImage.setImageBitmap(bitmap);				
					}
				});
			}
			
			@Override
			public void onFailure(ImageRequest imageRequest, Exception ex) {
				
			}
		});
        
//        mImageProvider.loadFiles(requestList, new IFileCallback() {
//			
//			@Override
//			public void onSuccess(ImageRequest imageRequest, String saveUrl) {
//				LogUtils.d(TAG, ">>>>> filePath:" + saveUrl);
//			}
//			
//			@Override
//			public void onFailure(ImageRequest imageRequest, Exception ex) {
//				LogUtils.e(TAG, ">>>>> onFailure, url: " + imageRequest.getUrl());
//				LogUtils.e(TAG, ">>>>> onFailure, savePath: " + imageRequest.getSavePath());
//			}
//		});
        
//        mImageProvider.loadImages(requestList, new ICocosCallback() {
//			
//			@Override
//			public void onSuccess(ImageRequest imageRequest, String filePath) {
//				LogUtils.d(TAG, ">>>>> filePath:" + filePath);
//			}
//			
//			@Override
//			public void onFailure(ImageRequest imageRequest, Exception ex) {
//				LogUtils.e(TAG, ">>>>> onFailure, url: " + imageRequest == null ? "" : imageRequest.getUrl());
//			}
//		});
        
        
//        // processed image (right)
//        ImageRequest processedRequest = new ImageRequest(url);
//        processedRequest.setDecodeConfig(Bitmap.Config.ARGB_4444);
//        processedRequest.setRoundCornerSpecs(new CornerSpec(Corner.BOTTOM_LEFT, 100));
//        mImageProvider.loadImage(processedRequest, new IImageCallback() {
//            @Override
//            public void onSuccess(ImageRequest imageRequest, final Bitmap bitmap) {
//                Log.d(TAG, "loadImage processed onSuccess: url=" + imageRequest.getUrl()
//                        + ", bitmap=" + LogUtils.printBitmap(bitmap));
//                PerformanceProfiler.stopProfiling();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mProcessedImage.setImageBitmap(bitmap);
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(ImageRequest imageRequest, Exception ex) {
//                Log.d(TAG, "loadImage processed onFailure: url=" + imageRequest.getUrl());
//                PerformanceProfiler.stopProfiling();
//            }
//        });
    }

    private static class PerformanceProfiler {
        private static String sName;
        private static AtomicLong sTime = new AtomicLong();

        public static synchronized void startProfiling(String name) {
            sName = name;
            sTime.set(SystemClock.uptimeMillis());
        }

        public static synchronized void stopProfiling() {
            sTime.set(SystemClock.uptimeMillis() - sTime.get());
            Log.d(TAG, "[profile result]: name=" + sName + ", time=" + sTime.get());
        }
    }

}
