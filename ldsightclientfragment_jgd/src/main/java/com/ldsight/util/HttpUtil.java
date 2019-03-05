package com.ldsight.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * $USER_NAME on 2018/6/29.
 * 功能: http访问工具类
 */
public class HttpUtil {

    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    //   public static BlockingQueue<String> cookieStore;
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<String, List<Cookie>>();

    private static OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                @Override
                public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {

                    cookieStore.put(httpUrl.host(), list);
                }
                @Override
                public List<Cookie> loadForRequest(HttpUrl httpUrl) {
                    List<Cookie> cookies = cookieStore.get(httpUrl.host());

                   /* if(cookies==null){
                        Log.e("xxxx", "没加载到cookie");
                    }*/
                    return cookies != null ? cookies : new ArrayList<Cookie>();
                }
            })
            .build();


    /**
     * HttpURLConnection 方式访问网络
     *
     * @param address  访问地址
     * @param listener 回调监听器
     * @return
     */
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        // 回调onFinish方法
                        listener.onFinish(response.toString());
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    if (listener != null) {
                        // 回调 Error 方法
                        listener.Error(e);
                    }
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }


    /**
     * OkHttp 方式访问网络
     *
     * @param address  访问地址
     * @param callback 回调监听器
     * @return 使用说明：
     */
    public static void sendHttpRequest(final String address, final okhttp3.Callback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client =  new OkHttpClient();
                Request request = new Request.Builder().url(address).build();
                client.newCall(request).enqueue(callback);

            }
        }).start();
    }

    /**
     * OkHttp 方式访问网络
     *
     * @param address     访问地址
     * @param requestBody 请求的参数
     * @param callback    回调监听器
     */
    public static void sendHttpRequest(final String address, final okhttp3.Callback callback, final RequestBody requestBody) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(address).post(requestBody).build();
                client.newCall(request).enqueue(callback);

            }
        }).start();
    }

    /**
     * OkHttp 方式携带sookie以post方式访问网络
     *
     * @param address  访问地址
     * @param callback 回调监听器
     * @return 使用说明：
     */
    public static void sendSookiePostHttpRequest(final String address, final okhttp3.Callback callback, final RequestBody requestBody) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client =  getOkHttpClient();
                Request request = new Request.Builder().post(requestBody).url(address).build();
                client.newCall(request).enqueue(callback);

            }
        }).start();
    }


    /**
     * OkHttp 方式携带cookie访问网络
     *
     * @param address  访问地址
     * @param callback 回调监听器
     * @return 使用说明：
     */
    public static void sendGetSookieHttpRequest(final String address, final okhttp3.Callback callback) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client =  getOkHttpClient();
                Request request = new Request.Builder().url(address).build();
                client.newCall(request).enqueue(callback);

            }
        }).start();
    }



    /**
     * 回调函数
     */
    public interface HttpCallbackListener {

        /**
         * 根据返回内容执行具体逻辑
         *
         * @param response
         */
        public void onFinish(String response);

        /**
         * 连接异常
         *
         * @param e
         */
        public void Error(Exception e);
    }



    public class PersistenceCookieJar implements CookieJar {

        List<Cookie> cache = new ArrayList<Cookie>();

        //Http请求结束，Response中有Cookie时候回调
        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {

            //内存中缓存Cookie
            cache.addAll(cookies);
        }

        //Http发送请求前回调，Request中设置Cookie
        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            //过期的Cookie
            List<Cookie> invalidCookies = new ArrayList<Cookie>();
            //有效的Cookie
            List<Cookie> validCookies = new ArrayList<Cookie>();

            for (Cookie cookie : cache) {

                if (cookie.expiresAt() < System.currentTimeMillis()) {
                    //判断是否过期
                    invalidCookies.add(cookie);
                } else if (cookie.matches(url)) {
                    //匹配Cookie对应url
                    validCookies.add(cookie);
                }
            }

            //缓存中移除过期的Cookie
            cache.removeAll(invalidCookies);

            //返回List<Cookie>让Request进行设置
            return validCookies;
        }
    };
}
