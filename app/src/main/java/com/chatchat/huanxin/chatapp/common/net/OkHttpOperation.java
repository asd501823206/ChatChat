package com.chatchat.huanxin.chatapp.common.net;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.chatchat.huanxin.chatapp.common.net.exception.CallCancelException;
import com.chatchat.huanxin.chatapp.common.net.exception.EmptyBodyException;
import com.chatchat.huanxin.chatapp.common.net.exception.ErrorCodeException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

//import static com.google.common.base.Preconditions.checkNotNull;

/**
 * okhttp
 * Created by dengzm on 2018/1/9.
 */

public class OkHttpOperation {
    private static final String TAG = "OkHttpOperation";

    static final String KEY_return_type = "return_type";
    static final String KEY_return_value = "return_value";
    static final String KEY_request_id = "request_id";

    private static final int CONNECT_TIME_OUT = 4;
    private static final int IO_TIME_OUT = 8;

    private static final MediaType JSON_TYPE = MediaType.parse("application/json;charset=utf-8");

    //instance start
    private static OkHttpOperation httpOperation;

    private OkHttpOperation() {
        getUnsafeOkHttpClient();
        requestMap = new ArrayMap<>();
    }

    private void getUnsafeOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        try {
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            final SSLContext sslContext = SSLContext.getInstance("TSL");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            okHttpClient = builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(IO_TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(IO_TIME_OUT, TimeUnit.SECONDS)
                    .build();
        }catch (Exception e) {
            okHttpClient = builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                    .readTimeout(IO_TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(IO_TIME_OUT, TimeUnit.SECONDS)
                    .build();
        }
    }

    public static OkHttpOperation getInstance() {
        if (httpOperation == null) {
            httpOperation = new OkHttpOperation();
        }
        return httpOperation;
    }
    //instance end

    private OkHttpClient okHttpClient;
    private ArrayMap<String, Disposable> requestMap;

    @IntDef({Method.GET, Method.POST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Method {
        int GET = 1;
        int POST = 2;
    }

    @IntDef({PostBodyType.FORM, PostBodyType.JSON})
    @Retention(RetentionPolicy.SOURCE)
    @interface PostBodyType {
        int FORM = 1;
        int JSON = 2;
    }

    @IntDef({ReturnType.STRING, ReturnType.FILE})
    @Retention(RetentionPolicy.SOURCE)
    @interface ReturnType {
        int STRING = 1;
        int FILE = 2;
        int CACHE = 3;
    }

    interface SuccessCallBack {
        void onSuccess(String requestId, int type, String res);
    }

    interface FailedCallBack{
        void onFailed(String requestId, Throwable e);
    }

    interface LoadFileSuccessCallBack {
        void onSuccess(String requestId, String res);
    }

    interface ProgressCallBack {
        void onProgress(String requestId, String filePath, float percent);
    }

    private abstract static class HttpBuilder {

        String url;
        String requestId;
        Request.Builder okHttpBuilder;
        JSONObject headers;
        JSONObject params;

        HttpBuilder() {
            okHttpBuilder = new Request.Builder();
        }

        public HttpBuilder url(String url) {
            this.url = url;
            okHttpBuilder.url(url);
            return this;
        }

        protected HttpBuilder requestId(String requestId) {
            this.requestId = requestId;
            okHttpBuilder.tag(requestId);
            return this;
        }

        protected HttpBuilder headers(JSONObject headers) {
            this.headers = headers;
            if (headers != null) {
                Iterator<String> it = headers.keys();
                while (it.hasNext()) {
                    String key = it.next();
                    String value = headers.optString(key);
                    okHttpBuilder.header(key, value);
                }
            }
            return this;
        }

        protected HttpBuilder headers(ArrayMap<String, String> headers) {
            this.headers = new JSONObject();
            if (headers != null) {
                for (String key : headers.keySet()) {
                    try {
                        this.headers.put(key, headers.get(key));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    okHttpBuilder.header(key, headers.get(key));
                }
            }
            return this;
        }

        public HttpBuilder params(ArrayMap<String, String> params) {
            JSONObject param = new JSONObject();
            for (String key : params.keySet()) {
                try {
                    param.put(key, params.get(key));
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            this.params = param;
            return this;
        }

        public HttpBuilder params(JSONObject params) {
            this.params = params;
            return this;
        }

    }

    static class IRequestBuilder extends HttpBuilder {

        @Method
        private int method;
        @PostBodyType
        private int bodyType;

        boolean isCatheResponse;
        String cachePath;
        boolean isCacheHaveSend;

        //TODO:尚未添加
        private SuccessCallBack successCallBack;
        private FailedCallBack failedCallBack;

        IRequestBuilder() {
            super();
            bodyType = PostBodyType.FORM;
            method = Method.GET;
            isCatheResponse = false;
            isCacheHaveSend = false;
        }

        @Override
        public IRequestBuilder url(String url) {
            super.url(url);
            return this;
        }

        @Override
        protected IRequestBuilder requestId(String requestId) {
            super.requestId(requestId);
            return this;
        }

        @Override
        protected IRequestBuilder headers(JSONObject headers) {
            super.headers(headers);
            return this;
        }

        @Override
        protected IRequestBuilder headers(ArrayMap<String, String> headers) {
            super.headers(headers);
            return this;
        }

        @Override
        public IRequestBuilder params(ArrayMap<String, String> params) {
            super.params(params);
            return this;
        }

        @Override
        public IRequestBuilder params(JSONObject params) {
            super.params(params);
            return this;
        }

        protected IRequestBuilder isCacheResponse(boolean isUseCache) {
            this.isCacheResponse(isUseCache);
            return this;
        }

        protected IRequestBuilder cachePath(String path) {
            this.cachePath = path;
            return this;
        }

        protected IRequestBuilder setSuccess(SuccessCallBack callBack) {
            this.successCallBack = callBack;
            return this;
        }

        protected IRequestBuilder setFailed(FailedCallBack callBack) {
            this.failedCallBack = callBack;
            return this;
        }

        /**
         * 请求的方法 METHOD.GET 或者METHOD.POST
         * @param method 请求的方法
         */
        IRequestBuilder method(@Method int method) {
            this.method = method;
            return this;
        }

        protected IRequestBuilder postBodyType(@PostBodyType int bodyType) {
            this.bodyType = bodyType;
            return this;
        }

        public IRequestBuilder get() {
            method(Method.GET);
            return this;
        }

        public IRequestBuilder post() {
            method(Method.POST);
            return this;
        }

        protected void exec() {
            checkRequestArgs(false);
            OkHttpOperation.getInstance().request(this);
        }

        protected void checkRequestArgs(boolean isRx) {
            //TODO:guava-22.0 jar包加上后无法编译
//            url = checkNotNull(url, "url不能为空");
//            if (!isRx) {
//                successCallBack = checkNotNull(successCallBack, "必须设置请求成功回调");
//            }
//            if (method == Method.POST) {
//                params = checkNotNull(params, "post请求参数不能为空");
//            }
        }

        @Override
        public String toString() {
            return "IRequestBuilder{" +
                    "url=" + url + '\'' +
                    ", method=" + method +
                    ",requestId='" + requestId + '\'' +
                    '}';
        }

    }

    /**
     * 调用get/post请求
     * @param iRequestBuilder 请求参数
     */
    private void request(IRequestBuilder iRequestBuilder) {
        switch (iRequestBuilder.method) {
            case Method.GET:
                get(iRequestBuilder);
                break;
            case Method.POST:
                post(iRequestBuilder);
                break;
        }
    }

    /**
     * http-get方法
     */
    private void get(final IRequestBuilder builder) {
        Observable.create(new ObservableOnSubscribe<JSONObject>() {
            @Override
            public void subscribe(ObservableEmitter<JSONObject> emitter) throws Exception {
                Request request;
                if (builder.params != null) {
                    String url = builder.url + "?" + getRequestUrl(builder.params);
                    builder.url(url);
                }
                request = builder.okHttpBuilder.build();
                Response response;
                try {
                    final Call call = okHttpClient.newCall(request);
                    emitter.setDisposable(new Disposable() {
                        @Override
                        public void dispose() {
                            if (!call.isCanceled())
                                call.cancel();
                            requestMap.remove(builder.requestId);
                        }

                        @Override
                        public boolean isDisposed() {
                            return true;
                        }
                    });
                    response = call.execute();
                    if (call.isCanceled()) {
                        emitter.onError(new CallCancelException());
                    }else {
                        JSONObject res = dealWithResponse(response, builder.requestId);
                        emitter.onNext(res);
                        emitter.onComplete();
                    }
                    response.close();
                }catch (IOException | JSONException e) {
                    emitter.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        builder.successCallBack.onSuccess(builder.requestId, jsonObject.optInt("type"),
                                jsonObject.optString("result"));
                    }
                }, new Consumer<Throwable>(){
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (builder.failedCallBack != null) {
                    builder.failedCallBack.onFailed(builder.requestId, throwable);
                }
                requestMap.remove(builder.requestId);
            }
        }, new Action(){
           @Override
            public void run() throws Exception {
                requestMap.remove(builder.requestId);
            }
        }, new Consumer<Disposable>(){
           @Override
           public void accept(Disposable disposable) throws Exception {
                if (!TextUtils.isEmpty(builder.requestId)) {
                    requestMap.put(builder.requestId, disposable);
                }
           }
       });
    }

    /**
     * http-post方法
     */
    private void post(final IRequestBuilder iRequestBuilder) {
        Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(final ObservableEmitter<Response> emitter) throws Exception {
                RequestBody requestBody = null;
                switch (iRequestBuilder.bodyType) {
                    case PostBodyType.FORM:
                        requestBody = getRequestBody(iRequestBuilder.params);
                        break;
                    case PostBodyType.JSON:
                        requestBody = RequestBody.create(JSON_TYPE, iRequestBuilder.params.toString());
                        break;
                }
                final Request.Builder builder = iRequestBuilder.okHttpBuilder;
                assert requestBody != null;
                Request request = builder.post(requestBody).build();
                final Call call = okHttpClient.newCall(request);

                emitter.setDisposable(new Disposable() {
                    @Override
                    public void dispose() {
                        if (!call.isCanceled()) {
                            call.cancel();
                        }
                        requestMap.remove(iRequestBuilder.requestId);
                    }

                    @Override
                    public boolean isDisposed() {
                        return false;
                    }
                });

                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        if (!call.isCanceled()) {
                            emitter.onNext(response);
                            emitter.onComplete();
                        }else {
                            emitter.onError(new CallCancelException());
                        }
                    }
                });
            }
        }).map(new Function<Response, JSONObject>() {
            @Override
            public JSONObject apply(Response response) throws Exception {
                return dealWithResponse(response, iRequestBuilder.requestId);
            }
        }).subscribe(
                new Consumer<JSONObject>() {
                    @Override
                    public void accept(JSONObject jsonObject) throws Exception {
                        iRequestBuilder.successCallBack.onSuccess(iRequestBuilder.requestId,
                                jsonObject.optInt("type"), jsonObject.optString("result"));
                    }
                },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        if (iRequestBuilder.failedCallBack != null) {
                            iRequestBuilder.failedCallBack.onFailed(iRequestBuilder.requestId, throwable);
                        }
                        requestMap.remove(iRequestBuilder.requestId);
                    }
                },
                new Action() {
                    @Override
                    public void run() throws Exception {
                        requestMap.remove(iRequestBuilder.requestId);
                    }
                },
                new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {
                        if (!TextUtils.isEmpty(iRequestBuilder.requestId)) {
                            requestMap.put(iRequestBuilder.requestId, disposable);
                        }
                    }
                });
    }

    /**
     * 拼接get方法的url
     * @param jsonParam 参数
     * @return url
     */
    private String getRequestUrl(JSONObject jsonParam) {
        Iterator<String> keys = jsonParam.keys();
        StringBuilder url = new StringBuilder();
        int i = 0;
        while (keys.hasNext()) {
            String key = keys.next();
            String value = jsonParam.optString(key);
            if (i != 0) {
                url.append("&");
            }
            url.append(key).append("=").append(value);
            i ++;
        }
        return url.toString();
    }

    /**
     * 添加参数
     * @param params 参数
     * @return request-body
     */
    @NonNull
    private RequestBody getRequestBody(JSONObject params) {
        RequestBody requestBody;
        FormBody.Builder builder = new FormBody.Builder();
        Iterator<String> it = params.keys();
        while (it.hasNext()) {
            String key = it.next();
            builder.add(key, params.optString(key));
        }
        requestBody = builder.build();
        return requestBody;
    }

    /**
     * 解析response
     * 正确code：600
     */
    private JSONObject dealWithResponse(Response response, String requestId) throws IOException, JSONException {
        String responseMsg = TextUtils.isEmpty(response.message()) ? "" : response.message();
        if (response.isSuccessful() || response.code() == 600) {
            ResponseBody body = response.body();
            if (body != null) {
                long length = body.contentLength();
                Log.i(TAG, "dealWithResponse: body length = " + length);
                String result = body.string();
                if (TextUtils.isEmpty(result)) {
                    result = "";
                }
                JSONObject res = new JSONObject();
                if (responseMsg.equals("Cache")) {
                    res.put(KEY_return_type, ReturnType.CACHE);
                }else {
                    res.put(KEY_return_type, ReturnType.STRING);
                }
                res.put(KEY_return_value, result);
                if (!TextUtils.isEmpty(requestId)) {
                    res.put(KEY_request_id, requestId);
                }
                return res;
            }else {
                throw new EmptyBodyException();
            }
        }else {
            Log.i(TAG, "dealWithResponse: " + response.code() + response.body().string());
            throw new ErrorCodeException(response.code());
        }
    }









}
