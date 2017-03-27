package com.wohuizhong.client.app.http;


import android.content.Context;
import android.os.Build;

import com.example.jzyu.weplantplayground.BuildConfig;
import com.wohuizhong.client.app.util.Consts;
import com.zhy.utils.L;
import com.zhy.utils.NetUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.POST;

//import retrofit2.http.GET;

public class Api {
    public static final String TAG = Api.class.getSimpleName();
    public static final long SECONDS_ONE_DAY = 24 * 60 * 60;

    private static final String HEADER_X_APP_VER = "App-Version";
    private static final String HEADER_USER_AGENT = "User-Agent";

    private Context appContext;
    private Cache cache;
    private ApiInfo[] apiInfoArray = new ApiInfo[CacheType.values().length];

    private enum CacheType {
        NONE,   // no cache
        ONLY,   // read from cache only, if no cache, callback with response code=504
        SHORT,  // cache short time (24h)
        ONE_MINUTE,  // cache one minute
        LONG,   // cache long time (4 week)
    }

    private static class ApiInfo {
        Retrofit retrofit;
        WeplantService service;
        Interceptor interceptor;
        CacheType cacheType;
    }

    private final static List<String> apiPathsAnnotationTokenNone = parseAnnotationTokenNone(WeplantService.class);

    private Retrofit createRetrofit(Interceptor interceptor, boolean isNetworkInterceptor) {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder()
                .cache(createCache())
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                //.writeTimeout(10, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false);

        if (isNetworkInterceptor) {
            okHttpBuilder.addNetworkInterceptor(interceptor);
        } else {
            okHttpBuilder.addInterceptor(interceptor);
        }

        /*if (BuildConfig.DEBUG) {
            // Stetho没有ReleaseStub，这里用Class.forName + newInstance方式动态创建对象，
            // 这样代码在debug和release都可以编译
            try {
                Class clsStethoInterceptor = Class.forName("com.facebook.stetho.okhttp3.StethoInterceptor");
                okHttpBuilder.addNetworkInterceptor((Interceptor) clsStethoInterceptor.newInstance());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }*/

        return new Retrofit.Builder()
                .client(okHttpBuilder.build())
                .addConverterFactory(FastjsonConverterFactory.create())
                .baseUrl(Consts.API_URL_BASE)
                .build();
    }

    //Refer: http://www.jianshu.com/p/ab41007f95c5
    private static void cancelCallsWithTag(Retrofit retrofit, Object tag) {
        if (retrofit == null || tag == null) {
            return;
        }

        OkHttpClient okHttpClient = null;
        if (retrofit.callFactory() instanceof OkHttpClient) {
            okHttpClient = (OkHttpClient) retrofit.callFactory();
        }
        if (okHttpClient == null) return;

        synchronized (okHttpClient.dispatcher().getClass()) {
            for (okhttp3.Call call : okHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    L.e(TAG, "cancelCall! url=" + call.request().url());
                    call.cancel();
                }
            }

            for (okhttp3.Call call : okHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    L.e(TAG, "cancelCall! url=" + call.request().url());
                    call.cancel();
                }
            }
        }
    }

    private static List<String> parseAnnotationTokenNone(Class<?> cls) {
        List<String> apiPathList = new ArrayList<>();

        for (Method m : cls.getDeclaredMethods()) {
            ACCESS_TOKEN_NONE tokenNone = m.getAnnotation(ACCESS_TOKEN_NONE.class);
            if (tokenNone != null) {
                POST post = m.getAnnotation(POST.class);
                GET get = m.getAnnotation(GET.class);

                String apiPath = get != null ? get.value() : post.value();
                if (apiPath.contains("{")) {
                    // 丢弃 {} 包裹的参数列表
                    apiPath = apiPath.substring(0, apiPath.indexOf("{"));
                }
                apiPathList.add(apiPath);
            }
        }

        return apiPathList;
    }


    private static Request.Builder makeNewRequestBuilder(Interceptor.Chain chain) {
        Request original = chain.request();
        /*HttpUrl originalHttpUrl = original.url();

        // url param: add token or not
        boolean apiNeedToken = true;
        for (String apiPath : apiPathsAnnotationTokenNone) {
            if (originalHttpUrl.url().toString().contains(apiPath)) {
                apiNeedToken = false;
            }
        }

        HttpUrl.Builder urlBuilder = originalHttpUrl.newBuilder();
        if (apiNeedToken) {
            urlBuilder.addEncodedQueryParameter("token",
                    ApiTools.getInstance().getAccessToken());
        }
        HttpUrl url = urlBuilder.build();*/

        // http request header: add customize header
        String headerAppVer = String.format(Locale.getDefault(), "%s", BuildConfig.VERSION_NAME);
        String userAgentStr = String.format(Locale.getDefault(),
                "Dalvik, OS=Android; Device=[ApiLevel=%d; Brand=%s; Model=%s], VerName=%s, VerCode=%d",
                Build.VERSION.SDK_INT,
                Build.BRAND,
                Build.MODEL,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE);

        return original.newBuilder()
                .url(original.url())
                .method(original.method(), original.body())
                .addHeader("Connection", "close")
                .addHeader(HEADER_X_APP_VER, headerAppVer)
                .removeHeader(HEADER_USER_AGENT)
                .addHeader(HEADER_USER_AGENT, userAgentStr);
    }

    private Cache createCache() {
        if (cache == null) {
            File httpCacheDirectory = new File(appContext.getCacheDir(), "responses");
            cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        }
        return cache;
    }

    public WeplantService _get(final CacheType cacheType) {
        ApiInfo info = apiInfoArray[cacheType.ordinal()];

        if (info == null) {
            info = new ApiInfo();
            apiInfoArray[cacheType.ordinal()] = info;
            boolean isNetworkInterceptor = false;

            switch (cacheType) {
                case NONE:
                    info.interceptor = new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = makeNewRequestBuilder(chain).build();
                            L.d(TAG, "url = " + request.url());
                            return chain.proceed(request);
                        }
                    };
                    break;

                case ONLY:
                    info.interceptor = new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request = makeNewRequestBuilder(chain)
                                    .cacheControl(CacheControl.FORCE_CACHE)
                                    .build();
                            L.d(TAG, "FORCE_CACHE, url = " + request.url());
                            return chain.proceed(request);
                        }
                    };
                    break;

                case ONE_MINUTE:
                case LONG:
                case SHORT:
                    isNetworkInterceptor = true;
                    info.interceptor = new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            Request request;

                            if (NetUtils.isConnected(appContext)) {
                                request = makeNewRequestBuilder(chain)
                                        .build();
                                L.d(TAG, "url = " + request.url());
                            } else {
                                request = makeNewRequestBuilder(chain)
                                        .cacheControl(CacheControl.FORCE_CACHE)
                                        .build();
                                L.d(TAG, "FORCE_CACHE, url = " + request.url());
                            }

                            Response response = chain.proceed(request);

                            long seconds = SECONDS_ONE_DAY;
                            if (cacheType == CacheType.LONG) {
                                seconds = 4 * 7 * SECONDS_ONE_DAY;
                            } else if (cacheType == CacheType.ONE_MINUTE) {
                                seconds = 60;
                            }

                            if (request.method().equalsIgnoreCase("GET")) {
                                return response.newBuilder()
                                        .header("Cache-Control", "max-age=" + seconds)
                                        .build();
                            } else {
                                return response;
                            }
                        }
                    };
                    break;
            }

            info.cacheType = cacheType;
            info.retrofit = createRetrofit(info.interceptor, isNetworkInterceptor);
            info.service = info.retrofit.create(WeplantService.class);
        }

        return info.service;
    }

    public static WeplantService get() {
        return instance._get(CacheType.NONE);
    }
    public static WeplantService getCacheOnly() {
        return instance._get(CacheType.ONLY);
    }
    /*public static WeplantService getCacheOneMinute() {
        return _get(CacheType.ONE_MINUTE);
    }*/
    public static WeplantService getCacheShort() {
        return instance._get(CacheType.SHORT);
    }
    /*public static WeplantService getCacheLong() {
        return _get(CacheType.LONG);
    }*/

    public static void clearCache() {
        if (instance.cache != null) {
            try {
                instance.cache.evictAll();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void cancelCallsWithTag(Object tag) {
        for (ApiInfo apiInfo: instance.apiInfoArray) {
            if (apiInfo != null) {
                cancelCallsWithTag(apiInfo.retrofit, tag);
            }
        }
    }

    private static Api instance;

    private Api(Context appContext) {
        this.appContext = appContext;
    }

    public static void init(Context appContext) {
        if (instance == null) {
            instance = new Api(appContext);
        }
    }
}
