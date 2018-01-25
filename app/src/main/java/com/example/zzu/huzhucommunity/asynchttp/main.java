package com.example.zzu.huzhucommunity.asynchttp;

import android.os.Handler;
import android.os.Message;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by do_pc on 2018/1/25.
 *
 */

public class main {
    private static final main ourInstance = new main();
    private asyncHttpCallback callback;
    private static final int GET_NEW_RESOURCE = 10301;
    private static final int GET_REQUEST = 10302;
    private static final int GET_RESOURCE_BY_TYPE = 10303;

    /**
     * 外部调用类方法，获得单体实例
     *
     * @return 单体实例
     */
    public static main getOurInstance() {
        return ourInstance;
    }

    /**
     * 私有构造方法
     */
    private main() {

    }

    public asyncHttpCallback getCallback() {
        return this.callback;
    }

    public void getNewResource(final String times, final asyncHttpCallback cBack) {
        try {
            if (times != null && cBack != null) {
                this.callback = cBack;
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(3000);
                RequestParams params = new RequestParams();
                params.put("times", times);
                String path = "http://139.199.38.177/huzhu/php/getNewResource.php";
                client.post(path, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                        //判断状态码
                        if (i == 200) {
                            //获取结果
                            try {
                                String result = new String(bytes, "utf-8");
                                Message message = new Message();
                                message.what = GET_NEW_RESOURCE;
                                message.obj = result;
                                handler.sendMessage(message);
                                cBack.onSuccess(i);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            cBack.onError(i);
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        cBack.onError(i);
                    }
                });
            } else {
                throw new Exception("参数传递错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getRequest(final String times, final asyncHttpCallback cBack) {
        try {
            if (times != null && cBack != null) {
                this.callback = cBack;
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(3000);
                RequestParams params = new RequestParams();
                params.put("times", times);
                String path = "http://139.199.38.177/huzhu/php/getRequest.php";
                client.post(path, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                        //判断状态码
                        if (i == 200) {
                            //获取结果
                            try {
                                String result = new String(bytes, "utf-8");
                                Message message = new Message();
                                message.what = GET_REQUEST;
                                message.obj = result;
                                handler.sendMessage(message);
                                cBack.onSuccess(i);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            cBack.onError(i);
                        }
                    }

                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        cBack.onError(i);
                    }
                });
            } else {
                throw new Exception("参数传递错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getResourceByType(final String resourceType, final String times, final asyncHttpCallback cBack) {
        try {
            if (resourceType != null && times != null && cBack != null) {
                this.callback = cBack;
                AsyncHttpClient client = new AsyncHttpClient();
                client.setTimeout(3000);
                RequestParams params = new RequestParams();
                params.put("resourceType", resourceType);
                params.put("times", times);
                String path = "http://139.199.38.177/huzhu/php/getResourceByType.php";
                client.post(path, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                        //判断状态码
                        if (i == 200) {
                            //获取结果
                            try {
                                String result = new String(bytes, "utf-8");
                                Message message = new Message();
                                message.what = GET_RESOURCE_BY_TYPE;
                                message.obj = result;
                                handler.sendMessage(message);
                                cBack.onSuccess(i);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            cBack.onError(i);
                        }
                    }
                    @Override
                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                        cBack.onError(i);
                    }
                });
            } else {
                throw new Exception("参数传递错误");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            String Response = message.toString();
            switch (message.what) {
                case GET_NEW_RESOURCE:
                    try {
                        JSONObject userObject = new JSONObject(Response);
                        int code = userObject.getInt("status");
                        //TODO 判断返回状态码&将返回数据写进本地数据库
                        callback.onSuccess(code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case GET_REQUEST:
                    try {
                        JSONObject userObject = new JSONObject(Response);
                        int code = userObject.getInt("status");
                        callback.onSuccess(code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                case GET_RESOURCE_BY_TYPE:
                    try {
                        JSONObject userObject = new JSONObject(Response);
                        int code = userObject.getInt("status");
                        callback.onSuccess(code);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    });
}
