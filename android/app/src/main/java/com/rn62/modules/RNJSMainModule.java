package com.rn62.modules;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.modules.network.OkHttpClientProvider;
import com.rn62.utils.FileUtil;
import com.rn62.utils.VersionUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RNJSMainModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    @NonNull
    @Override
    public String getName() {
        return "RNJSMainModule";
    }


    public RNJSMainModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }


    @ReactMethod
    public void updateJsBundleFile(ReadableMap readableMap, Promise promise) {
        try {
            String androidBundleUrl = readableMap.getString("androidBundleUrl");
            //存在内部存储中
            File file = new File(reactContext.getApplicationContext().getFilesDir(), "apexCommon/assets");
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (!mkdirs) {
                    promise.reject("-1", "mkdirs is fail");
                    return;
                }
            }
            File bundleFile = new File(file, "index.android.bundle");
            //下载并替换本地jsBundle文件
            ResponseBody responseBody = request(androidBundleUrl);
            InputStream inputStream = responseBody.byteStream();
            FileUtil.write(inputStream, new FileOutputStream(bundleFile));
            promise.resolve(null);
        } catch (Exception e) {
            promise.reject("-1", e.getMessage());
        }
    }

    /**
     * 检查并且更新
     */
    @ReactMethod
    public void checkAndUpdateJsBundleFile(ReadableMap readableMap, Promise promise) {
        String currentJsBundleFileVersion = readableMap.getString("currentJsBundleFileVersion");
        if (currentJsBundleFileVersion == null) {
            promise.reject("-1", "currentJsBundleFileVersion is null");
            return;
        }
        //调接口查询是否要更新jsBundle文件
        String versionUrl = readableMap.getString("versionUrl");
        try {
            ResponseBody responseBody = request(versionUrl);
            JSONObject versionInfo = new JSONObject(responseBody.string());
            String androidBundleVersion = versionInfo.getString("androidBundleVersion");
            String androidBundleUrl = versionInfo.getString("androidBundleUrl");
            int compareVersion = VersionUtil.compareVersion(androidBundleVersion, currentJsBundleFileVersion);
            //存在内部存储中
            File file = new File(reactContext.getApplicationContext().getFilesDir(), "apexCommon/assets");
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (!mkdirs) {
                    promise.reject("-1", "mkdirs is fail");
                    return;
                }
            }
            File bundleFile = new File(file, "index.android.bundle");
            if (compareVersion < 0 || !bundleFile.exists()) {
                //下载并替换本地jsBundle文件
                responseBody = request(androidBundleUrl);
                InputStream inputStream = responseBody.byteStream();
                FileUtil.write(inputStream, new FileOutputStream(bundleFile));
                promise.resolve(null);
            } else {
                promise.reject("-1", "not update");
            }
        } catch (Exception e) {
            promise.reject("-1", e.getMessage());
        }
    }


    private ResponseBody request(String url) throws IOException {
        OkHttpClient okHttpClient = OkHttpClientProvider.getOkHttpClient();
        Response response = okHttpClient.newCall(new Request.Builder().get().url(url).build()).execute();
        boolean successful = response.isSuccessful();
        if (!successful) {
            throw new RuntimeException(response.message());
        }
        return response.body();
    }

    /**
     * 重启整个APP
     */
    @ReactMethod
    public void restartAPP(Promise promise) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    ReactInstanceManager instanceManager = resolveInstanceManager();
                    instanceManager.recreateReactContextInBackground();
                } catch (Exception e) {
                    Log.e("err", e.getMessage());
                    final Activity currentActivity = getCurrentActivity();
                    if (currentActivity == null) {
                        return;
                    }
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            currentActivity.recreate();
                        }
                    });
                }
            }
        });

    }

    private ReactInstanceManager resolveInstanceManager() {
        ReactInstanceManager instanceManager;
        final Activity currentActivity = getCurrentActivity();
        if (currentActivity == null) {
            return null;
        }
        ReactApplication reactApplication = (ReactApplication) currentActivity.getApplication();
        instanceManager = reactApplication.getReactNativeHost().getReactInstanceManager();
        return instanceManager;
    }

}
