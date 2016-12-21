package com.wohuizhong.client.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.util.UriUtil;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.zhy.utils.L;

import java.io.File;


public class FrescoUtil {
    public static final String TAG = "FrescoUtil";

    private FrescoUtil() {
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * https://github.com/facebook/fresco/issues/603
     */
    public static boolean isImageDownloaded(String url) {
        if (StringUtil.isEmpty(url)) {
            return false;
        }
        Uri loadUri = Uri.parse(url);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri), null);
        return ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey) || ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey);
    }

    //return file or null
    public static File getCachedImageOnDisk(Uri loadUri) {
        File localFile = null;
        if (loadUri != null) {
            CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(ImageRequest.fromUri(loadUri), null);
            if (ImagePipelineFactory.getInstance().getMainDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getMainDiskStorageCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            } else if (ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().hasKey(cacheKey)) {
                BinaryResource resource = ImagePipelineFactory.getInstance().getSmallImageDiskStorageCache().getResource(cacheKey);
                localFile = ((FileBinaryResource) resource).getFile();
            }
        }
        return localFile;
    }

    public static SimpleDraweeView newCircleImageView(Context context) {
        SimpleDraweeView draweeView = new SimpleDraweeView(context);
        RoundingParams roundingParams = new RoundingParams();
        roundingParams.setRoundAsCircle(true);
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(context.getResources());
        GenericDraweeHierarchy hierarchy = builder.setRoundingParams(roundingParams).build();
        draweeView.setHierarchy(hierarchy);
        return draweeView;
    }

    /*public static SimpleDraweeView newClickableSimpleDraweeView(final Context context, int width, int marginRight, final long uid) {
        SimpleDraweeView iv = newCircleImageView(context);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                DensityUtils.dp2px(context, width),
                DensityUtils.dp2px(context, width));
        lp.setMargins(DensityUtils.dp2px(context, marginRight), 0, 0, 0);
        iv.setLayoutParams(lp);

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(UiCommon.newIntentViewUser(context, uid));
            }
        });

        setImageByUrl(iv, ApiTools.getImgUrlAvator(uid));
        return iv;
    }*/

    //放在setImageUri后面调用，否则设置无效
    public static void setOverlay(Context context, SimpleDraweeView drawee, int overlayResId, int padding) {
        InsetDrawable drawable = null;

        if (overlayResId > 0) {
            drawable = new InsetDrawable(context.getResources().getDrawable(overlayResId), padding);
        }
        drawee.getHierarchy().setControllerOverlay(drawable);
    }

    public static void setOverlay(Context context, SimpleDraweeView drawee, int overlayResId, Rect padding) {
        InsetDrawable drawable = null;

        if (overlayResId > 0) {
            drawable = new InsetDrawable(context.getResources().getDrawable(overlayResId),
                    padding.left, padding.top, padding.right, padding.bottom);
        }
        drawee.getHierarchy().setControllerOverlay(drawable);
    }

    public static void setLocalGif(SimpleDraweeView draweeView, int gifResId) {
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(gifResId))
                .build();

        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setOldController(draweeView.getController())
                .setImageRequest(ImageRequest.fromUri(uri))
                .setAutoPlayAnimations(true);
        draweeView.setController(controller.build());
    }

    // hexColor format: #3286
    public static void setPlaceholderColor(SimpleDraweeView draweeView, String hexColor) {
        L.v(TAG, "setPlaceholderColor() " + hexColor);
        if (StringUtil.isEmpty(hexColor) || ! hexColor.startsWith("#")) return;
        draweeView.getHierarchy().setPlaceholderImage(new ColorDrawable(Color.parseColor(hexColor)));
    }

    public static void setImageResource(SimpleDraweeView draweeView, int resId) {
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(resId))
                .build();
        draweeView.setImageURI(uri);
    }

    public static void setImageByUrl(SimpleDraweeView v, String url) {
        if (StringUtil.isEmpty(url)) return;
        v.setImageURI(Uri.parse(url));
    }

    /*public static void setAvatarImage(SimpleDraweeView iv, long uid) {
        setImageByUrl(iv, ApiTools.getImgUrlAvator(uid));
    }

    public static void setAvatarImageClickable(final Context context, SimpleDraweeView iv, final long uid) {
        setImageByUrl(iv, ApiTools.getImgUrlAvator(uid));
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(UiCommon.newIntentViewUser(context, uid));
            }
        });
    }

    public static void setTopicImage(SimpleDraweeView iv, long tid) {
        setImageByUrl(iv, ApiTools.getImgUrlTopic(tid));
    }*/

    public static Bitmap getBitmapFromCache(Context context, ImageRequest imageRequest) {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();

        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchImageFromBitmapCache(imageRequest, context);
        try {
            CloseableReference<CloseableImage> imageReference = dataSource.getResult();
            if (imageReference != null) {
                try {
                    CloseableBitmap image = (CloseableBitmap) imageReference.get();
                    Bitmap loadedImage = image.getUnderlyingBitmap();
                    if (loadedImage != null) {
                        return loadedImage;
                    }
                } finally {
                    CloseableReference.closeSafely(imageReference);
                }
            }
        } finally {
            dataSource.close();
        }

        L.e(TAG, "getBitmapFromCache() failed!");
        return null;
    }

    /**
     * 考虑低分图片，用于大图显示场景
     * @param highResUrl: 七牛url且带了image query参数
     */
    private static ImageRequest setImageByUrl0(SimpleDraweeView drawee,
                                               String highResUrl,
                                               String lowResUrl,
                                               int resizeWidth,
                                               int resizeHeight,
                                               BaseControllerListener<ImageInfo> listener)
    {
        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(highResUrl))
                .setAutoRotateEnabled(true);

        if (resizeWidth > 0 && resizeHeight > 0) {
            requestBuilder.setResizeOptions(new ResizeOptions(resizeWidth, resizeHeight));
        }
        ImageRequest request = requestBuilder.build();

        PipelineDraweeControllerBuilder controllerBuilder = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(drawee.getController())
                .setAutoPlayAnimations(true);

        if (listener != null) {
            controllerBuilder.setControllerListener(listener);
        }

        if (! StringUtil.isEmpty(lowResUrl) && ! isImageDownloaded(highResUrl)) {
            controllerBuilder.setLowResImageRequest(ImageRequest.fromUri(Uri.parse(lowResUrl)));
        }

        drawee.setController(controllerBuilder.build());
        return request;
    }

    /*public static void setImageByUrlFitScreenAndLowRes200(SimpleDraweeView drawee, String url, BaseControllerListener<ImageInfo> listener) {
        if (StringUtil.isEmpty(url)) return;
        setImageByUrl0(drawee,
                QiniuResizer.getUrlFitScreen(url),
                QiniuResizer.getUrlAtWidth(url, 200),
                -1, -1, listener);
    }

    public static void setImageByUrlFitScreenAndLowRes400(SimpleDraweeView drawee, String url, BaseControllerListener<ImageInfo> listener) {
        if (StringUtil.isEmpty(url)) return;
        setImageByUrl0(drawee,
                QiniuResizer.getUrlFitScreen(url),
                QiniuResizer.getUrlAtWidth(url, 400),
                -1, -1, listener);
    }*/

    public static ImageRequest setImageByUrl(SimpleDraweeView drawee, String url, BaseControllerListener<ImageInfo> listener) {
        if (StringUtil.isEmpty(url)) return null;
        return setImageByUrl0(drawee, url, null, -1, -1, listener);
    }

    public static ImageRequest setImageByUrl(SimpleDraweeView v, String url, int resizeWidth, int resizeHeight) {
        return setImageByUrl0(v, url, null, resizeWidth, resizeHeight, null);
    }

    public static ImageRequest setImageByUrl(SimpleDraweeView v, String url, int resizeWidth, int resizeHeight, BaseControllerListener<ImageInfo> listener) {
        return setImageByUrl0(v, url, null, resizeWidth, resizeHeight, listener);
    }

    /*public static ImageRequest setImageByUrlAtWidth(SimpleDraweeView v, String url, int width, BaseControllerListener<ImageInfo> listener) {
        if (StringUtil.isEmpty(url)) return null;
        return setImageByUrl(v, QiniuResizer.getUrlAtWidth(url, width), listener);
    }*/
}
