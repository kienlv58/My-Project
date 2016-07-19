package vn.k2t.traficjam.untilitis;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import vn.k2t.traficjam.R;


/**
 * Created by root on 17/05/2016.
 */
public class CommonMethod {
    private static CommonMethod commonMethod;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Context context;

    public static CommonMethod getInstance(Context mContext) {
        if (commonMethod == null) {
            commonMethod = new CommonMethod(mContext);
        }
        return commonMethod;
    }

    public CommonMethod(Context context) {
        this.context=context;
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(context));
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(R.drawable.bg_user_profile)
                .showImageForEmptyUri(R.drawable.bg_user_profile)
                .showImageOnFail(R.drawable.bg_user_profile).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public void loadImage(String link, ImageView avatar) {
        imageLoader.displayImage(link, avatar, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {

            }
        });
    }
}
