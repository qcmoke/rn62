package com.rn62.modules;

import android.content.Context;

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

public class RNJSMainService {

    public static String checkAndUpdateJsBundleFile(Context ctx, String currentJsBundleFileVersion, String versionUrl) {
        //调接口查询是否要更新jsBundle文件
        try {
            ResponseBody responseBody = request(versionUrl);
            JSONObject versionInfo = new JSONObject(responseBody.string());
            String androidBundleVersion = versionInfo.getString("androidBundleVersion");
            String androidBundleUrl = versionInfo.getString("androidBundleUrl");
            int compareVersion = VersionUtil.compareVersion(androidBundleVersion, currentJsBundleFileVersion);
            //存在内部存储中
            File file = new File(ctx.getFilesDir(), "apexCommon/assets");
            if (!file.exists()) {
                boolean mkdirs = file.mkdirs();
                if (!mkdirs) {
                    return null;
                }
            }
            File bundleFile = new File(file, "index.android.bundle");
            if (compareVersion <= 0 || !bundleFile.exists()) {
                return null;
            }
            //下载并替换本地jsBundle文件
            responseBody = request(androidBundleUrl);
            InputStream inputStream = responseBody.byteStream();
            FileUtil.write(inputStream, new FileOutputStream(bundleFile));
            return androidBundleUrl;
        } catch (Exception e) {
            return null;
        }
    }

    private static ResponseBody request(String url) throws IOException {
        OkHttpClient okHttpClient = OkHttpClientProvider.getOkHttpClient();
        Response response = okHttpClient.newCall(new Request.Builder().get().url(url).build()).execute();
        boolean successful = response.isSuccessful();
        if (!successful) {
            throw new RuntimeException(response.message());
        }
        return response.body();
    }

}
