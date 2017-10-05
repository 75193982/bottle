package com.jerei.im.timchat.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.jerei.im.timchat.utils.cache.DiskLruCacheUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/5/17.
 */
public class HttpPictureUtils {

    private static LruCache<String, Bitmap> mMemoryCache;

    static {
//        // 获取应用程序最大可用内存
        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 8;
        /**
         * 内存中最多放10找那个图片
         */
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };

    }

    /**
     * 获取网络图片
     *
     * @param url
     */
    public static void ggetAvatarBitmap(final String url, final ImageView imageView, final Context context, final int defultImage, final String tag) {


        if (TextUtils.isEmpty(url)) {
            imageView.setImageResource(defultImage);
            return;
        }

        new AsyncTask<Void, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... voids) {

                WeakReference weakReference;
                byte[] bytes = null;
                System.gc();

                Bitmap bitmap = null;
                bitmap = mMemoryCache.get(url);
                if (bitmap != null) {
                    weakReference= new WeakReference(bitmap);//弱引用
                    bitmap = null;
                    return (Bitmap) weakReference.get();
                }


                /**
                 * 先冲缓存中查找
                 */
                InputStream inputStream = DiskLruCacheUtil.readFromDiskCache(url, context);
                if (inputStream != null) {
                    try {

                        bytes = HttpPictureUtils.toByteArray(inputStream);
                        bitmap = Bytes2Bimap(bytes);

                        weakReference = new WeakReference(bitmap);//弱引用
                        bitmap = null;
                        mMemoryCache.put(url, (Bitmap) weakReference.get());
                        return (Bitmap) weakReference.get();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                URL myFileURL;
                InputStream is = null;
                ByteArrayOutputStream output = null;

                try {
                    myFileURL = new URL(url);
                    //获得连接
                    java.net.HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
                    //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
                    conn.setConnectTimeout(6000);
                    //连接设置获得数据流
                    conn.setDoInput(true);
                    //不使用缓存
                    conn.setUseCaches(false);
                    //这句可有可无，没有影响
                    //conn.connect();
                    //得到数据流
                    //数据总大小
                    int length = conn.getContentLength();
                    is = conn.getInputStream();
                    //获得byte[]
                    output = new ByteArrayOutputStream();

                    byte[] buffer = new byte[4096];
                    int n = 0;

                    while (-1 != (n = is.read(buffer))) {
                        output.write(buffer, 0, n);


                    }
                    buffer = null;
                    bytes = output.toByteArray();
                    /**
                     * 存入缓存
                     */

                    DiskLruCacheUtil.writeToDiskCache(url, bytes, context);


                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    //关闭数据流
                    if (is != null) {
                        try {
                            is.close();
                            output.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }

                Bitmap bitmap2 =null;
                try {
                    bitmap2   =  Bytes2Bimap(bytes);
                }catch (Exception e){
                    e.printStackTrace();
                    return null;
                }


                WeakReference weakReference2 = new WeakReference(bitmap2);//弱引用
                mMemoryCache.put(url, (Bitmap) weakReference2.get());
                bitmap2 = null;
                return (Bitmap) weakReference2.get();
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                Log.e("tag", tag);
                if (imageView.getTag().equals(tag)) {
                    if (bitmap == null) {
                        imageView.setImageResource(defultImage);
                        return;
                    }
                    WeakReference weakReference = new WeakReference(bitmap);//弱引用
                    imageView.setImageBitmap((Bitmap) weakReference.get());
                }


            }


            @Override
            protected void onProgressUpdate(Integer... values) {

                super.onProgressUpdate(values);

            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
            }
            /**
             * executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR); 线程将异步执行
             * execute()   线程将同步执行
             */
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


    }


    /**
     * 将输入流转换为byt[]
     *
     * @param input
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream input) throws IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buffer = new byte[4096];
        int n = 0;
        try {
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;

        }

        return output.toByteArray();
    }


    /**
     * @param b
     * @return
     */
    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }


}
