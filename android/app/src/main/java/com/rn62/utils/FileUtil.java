package com.rn62.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class FileUtil {

    public static void write(InputStream inputStream, OutputStream outputStream) throws IOException {
        try {
            byte[] buff = new byte[2048];
            int len;
            while ((len = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
                outputStream.flush();
            }
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }


    /**
     * 解压压缩包
     *
     * @param zipFilePath 压缩包文件路径
     * @param outFilePath 解压后的目标目录
     * @throws Exception
     */
    public static void unZip(String zipFilePath, String outFilePath) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFilePath));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outFilePath + File.separator + szName);
                folder.mkdirs();
            } else {
                Log.e("file", outFilePath + File.separator + szName);
                File file = new File(outFilePath + File.separator + szName);
                if (!file.exists()) {
                    Log.e("file", "Create the file:" + outFilePath + File.separator + szName);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }


    /**
     * SD开是否可用
     */
    public static boolean existSDCard() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }


    /**
     * 获取存储目录（有外部存储就取外部存储，否则取内置存储器）
     */
    public static File getStorageDirectory(Context context) {
        File externalStorageDirectory;
        if (FileUtil.existSDCard()) {
            externalStorageDirectory = Environment.getExternalStorageDirectory();
        } else {

            externalStorageDirectory = context.getFilesDir();
        }
        return externalStorageDirectory;
    }


    /**
     * 获取临时文件
     */
    public static File getTempFolder(Context context) {
        File tempFolder;
        String tempFolderName = "temp";
        if (FileUtil.existSDCard()) {
            tempFolder = new File(Environment.getExternalStorageDirectory(), tempFolderName);
        } else {
            tempFolder = new File(context.getFilesDir(), tempFolderName);
        }
        if (!tempFolder.exists()) {
            if (!tempFolder.mkdirs()) {
                Log.e("", "创建文件夹失败！");
            }
        }
        return tempFolder;
    }


    /*
     * Uri转String
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

}
