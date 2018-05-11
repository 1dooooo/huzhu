package com.example.zzu.huzhucommunity.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.zzu.huzhucommunity.R;
import com.example.zzu.huzhucommunity.adapters.CommonRequestAdapter;
import com.example.zzu.huzhucommunity.adapters.CommonResourcesAdapter;
import com.example.zzu.huzhucommunity.adapters.CommonViewPagerAdapter;
import com.example.zzu.huzhucommunity.asynchttp.AsyncHttpCallback;
import com.example.zzu.huzhucommunity.asynchttp.Main;
import com.example.zzu.huzhucommunity.asynchttp.UserProfile;
import com.example.zzu.huzhucommunity.commonclass.ActivitiesCollector;
import com.example.zzu.huzhucommunity.commonclass.MyApplication;
import com.example.zzu.huzhucommunity.commonclass.NewRequestItem;
import com.example.zzu.huzhucommunity.commonclass.NewResourceItem;
import com.example.zzu.huzhucommunity.commonclass.Utilities;
import com.example.zzu.huzhucommunity.dataclass.Request;
import com.example.zzu.huzhucommunity.dataclass.Resource;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import static com.example.zzu.huzhucommunity.asynchttp.Main.GET_NEW_RESOURCE;
import static com.example.zzu.huzhucommunity.asynchttp.Main.GET_REQUEST;
import static com.example.zzu.huzhucommunity.asynchttp.Main.GET_RESOURCE_BY_TYPE;

/**
 * 登录成功后的主界面
 */
public class MainActivity extends BaseActivity implements AsyncHttpCallback {
    public static final String PUBLISH_TYPE = "PUBLISH_TYPE";
    private static long lastTimeBackPressed = 0;

    private static final int REFRESHING_RES = 3;
    private static final int REFRESHING_REQ = 2;

    private static final int LOAD_RES_IMAGE = 11;
    private static final int LOAD_REQ_IMAGE = 12;
    private static final String TAG = "MainActivity";

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case LOAD_REQ_IMAGE:
                case LOAD_RES_IMAGE:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    int ind = msg.arg1;
                    if (msg.what == LOAD_REQ_IMAGE){
                        requestItems.get(ind).addItemThumbnail(bitmap);
                        requestAdapter.notifyItemChanged(ind);
                    }
                    else {
                        resourceItems.get(ind).addItemThumbil(bitmap);
                        resourceAdapter.notifyItemChanged(ind);
                    }
                    break;
            }
            return false;
        }
    });
    private ImageButton resourceButton;
    private ImageButton requestButton;

    private SwipeRefreshLayout resSwipeRefreshLayout, requestSwipeRefreshLayout;

    private RecyclerView newResourceRecyclerView;
    private ArrayList<NewResourceItem> resourceItems = new ArrayList<>();
    private CommonResourcesAdapter resourceAdapter;

    private RecyclerView newRequestRecyclerView;
    private ArrayList<NewRequestItem> requestItems = new ArrayList<>();
    private CommonRequestAdapter requestAdapter;

    private ViewPager mainViewPager;
    private ArrayList<View> pagerViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_layout);

        setSwipeToFinishOff();

        resourceButton = findViewById(R.id.MainActivity_resource_button);
        requestButton = findViewById(R.id.MainActivity_request_button);

        newResourceRecyclerView = new RecyclerView(this);
        newResourceRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        resourceAdapter = new CommonResourcesAdapter(resourceItems, this);
        newResourceRecyclerView.setAdapter(resourceAdapter);
        resSwipeRefreshLayout = new SwipeRefreshLayout(this);
        resSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshResource(REFRESHING_RES);
            }
        });
        resSwipeRefreshLayout.addView(newResourceRecyclerView);
        pagerViews.add(resSwipeRefreshLayout);

        newRequestRecyclerView = new RecyclerView(this);
        newRequestRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        requestAdapter = new CommonRequestAdapter(requestItems, this);
        newRequestRecyclerView.setAdapter(requestAdapter);
        requestSwipeRefreshLayout = new SwipeRefreshLayout(this);
        requestSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshResource(REFRESHING_REQ);
            }
        });
        requestSwipeRefreshLayout.addView(newRequestRecyclerView);
        pagerViews.add(requestSwipeRefreshLayout);

        mainViewPager = findViewById(R.id.MainActivity_view_pager);
        mainViewPager.setAdapter(new CommonViewPagerAdapter(pagerViews));
        mainViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                changePagerButton(position);
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        addListener(R.id.MainActivity_resource_button);
        addListener(R.id.MainActivity_resource_text_view);
        addListener(R.id.MainActivity_request_button);
        addListener(R.id.MainActivity_request_text_view);
        addListener(R.id.MainActivity_head_button);
        addListener(R.id.MainActivity_publish_button);
        addListener(R.id.MainActivity_message_button);
        addListener(R.id.MainActivity_search_text_view);

        initUserHeadImage();
        initList();
    }

    /**
     * 初始化列表项
     */
    public void initList(){
        Main.getOurInstance().getRequest("1", this);
        Main.getOurInstance().getRequest("2", this);
        Main.getOurInstance().getRequest("3", this);
        Main.getOurInstance().getRequest("4", this);
        Main.getOurInstance().getNewResource("1", this);
        Main.getOurInstance().getNewResource("2", this);
        Main.getOurInstance().getNewResource("3", this);
        Main.getOurInstance().getNewResource("4", this);
    }

    /**
     * 获取用户头像
     */
    public void initUserHeadImage(){
        Bitmap bitmap = Utilities.GetLoginUserHeadBitmapFromSP();
        if (bitmap == null){
            UserProfile.getOurInstance().getImageBitmapByUrl(Utilities.GetLoginUserHeadUrl(), new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    if (msg.what == UserProfile.GET_IMAGE_BITMAP_BY_URL){
                        Bitmap bitmap = (Bitmap) msg.obj;
                        if (bitmap != null){
                            ((ImageView) findViewById(R.id.MainActivity_head_button)).setImageBitmap(bitmap);
                            Utilities.SaveLoginUserHeadBitmap(bitmap);
                        }
                    }
                    return false;
                }
            }));
        }
        else {
            ((ImageView) findViewById(R.id.MainActivity_head_button)).setImageBitmap(bitmap);
        }
    }

    /**
     * 下拉刷新时执行
     */
    public void refreshResource(int which){
        if (which == REFRESHING_REQ) {
            requestItems.clear();
            Main.getOurInstance().getRequest("1", this);
            Main.getOurInstance().getRequest("2", this);
            Main.getOurInstance().getRequest("3", this);
            Main.getOurInstance().getRequest("4", this);
        }
        else if (which == REFRESHING_RES){
            resourceItems.clear();
            Main.getOurInstance().getNewResource("1", this);
            Main.getOurInstance().getNewResource("2", this);
            Main.getOurInstance().getNewResource("3", this);
            Main.getOurInstance().getNewResource("4", this);
        }
    }

    /**
     * 加载资源，请求之后加载每一项的(缩略)图片
     */
    public void loadResItemImage(){
        for (int i = 0; i < resourceItems.size(); i++){
            final int ind = i;
            final String tmpUrl = resourceItems.get(i).getItemImageUrl();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap;
                    try {
                        URL url = new URL(tmpUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setConnectTimeout(2000);
                        InputStream in = con.getInputStream();
                        bitmap = BitmapFactory.decodeStream(in);
                        Message message = new Message();
                        message.obj = bitmap;
                        message.what = LOAD_RES_IMAGE;
                        message.arg1 = ind;
                        handler.sendMessage(message);
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }
    public void loadReqItemImage(){
        for (int i = 0; i < requestItems.size(); i++){
            final int ind = i;
            final String tmpUrl = requestItems.get(i).getItemImageUrl();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap;
                    try {
                        URL url = new URL(tmpUrl);
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setConnectTimeout(2000);
                        InputStream in = con.getInputStream();
                        bitmap = BitmapFactory.decodeStream(in);
                        Message message = new Message();
                        message.obj = bitmap;
                        message.what = LOAD_REQ_IMAGE;
                        message.arg1 = ind;
                        handler.sendMessage(message);
                        in.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    /**
     * ViewPager切换即MainActivity中页面改变时调用
     * @param position 切换到的页面对应的下标
     */
    public void changePagerButton(int position){
        if(position == 0){
            resourceButton.setImageResource(R.drawable.resource_yellow);
            requestButton.setImageResource(R.drawable.request_gray);
            newResourceRecyclerView.setVisibility(View.VISIBLE);
            newRequestRecyclerView.setVisibility(View.GONE);
        }
        else if(position == 1) {
            resourceButton.setImageResource(R.drawable.resource_gray);
            requestButton.setImageResource(R.drawable.request_yellow);
            newResourceRecyclerView.setVisibility(View.GONE);
            newRequestRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 为每个控件添加监听器
     * @param res 控件ID
     */
    public void addListener(final int res){
        findViewById(res).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (res){
                    case R.id.MainActivity_resource_button:
                    case R.id.MainActivity_resource_text_view:
                        mainViewPager.setCurrentItem(0);
                        break;
                    case R.id.MainActivity_request_button:
                    case R.id.MainActivity_request_text_view:
                        mainViewPager.setCurrentItem(1);
                        break;
                    case R.id.MainActivity_head_button:
                        UserProfileActivity.startMe(MainActivity.this);
                        break;
                    case R.id.MainActivity_publish_button:
                        ImageButton publishButton = findViewById(R.id.MainActivity_publish_button);
                        Animation animation = new RotateAnimation(0.0f, 90,
                                publishButton.getWidth() / 2, publishButton.getHeight() / 2);
                        animation.setDuration(150);
                        animation.setFillAfter(true);
                        publishButton.startAnimation(animation);
                        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                        dialog.setItems(R.array.PublishType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0)
                                    PublishNewActivity.startMe(MainActivity.this, PublishNewActivity.PUBLISH_NEW_RESOURCE);
                                else if (which == 1)
                                    PublishNewActivity.startMe(MainActivity.this, PublishNewActivity.PUBLISH_NEW_REQUEST);
                            }
                        });
                        dialog.show();
                        break;
                    case R.id.MainActivity_message_button:
                        MessagesActivity.startMe(MainActivity.this);
                        break;
                    case  R.id.MainActivity_search_text_view:
                        SearchActivity.startMe(MainActivity.this);
                        break;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        long curTime = GregorianCalendar.getInstance().getTimeInMillis();
        long exitInterval = 2000;
        if (curTime - lastTimeBackPressed <= exitInterval){
            ActivitiesCollector.finishAll();
        }
        else {
            Toast.makeText(MyApplication.getContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            lastTimeBackPressed = GregorianCalendar.getInstance().getTimeInMillis();
        }
    }

    public static void startMe(Context context){
        context.startActivity(new Intent(context, MainActivity.class));
    }

    /**
     * 回调成功函数
     * @param statusCode 返回状态
     * @param mp 存储需要传递数据的哈希表，若无数据则 mp 为 null
     * @param requestCode 请求码，标识同一个activity的不同网络请求
     */
    @Override
    public void onSuccess(int statusCode, HashMap<String, String> mp, int requestCode) {
        switch (requestCode){
            case GET_REQUEST:
                List<Request> list = Utilities.getRequest(mp);
                if (list != null) {
                    for (Request request: list) {
                        requestItems.add(NewRequestItem.TransferToMe(request));
                    }
                    requestAdapter.notifyDataSetChanged();
                    findViewById(R.id.MainActivity_progress_bar).setVisibility(View.INVISIBLE);
                    if (requestSwipeRefreshLayout.isRefreshing())
                        requestSwipeRefreshLayout.setRefreshing(false);
                    loadReqItemImage();
                }
                break;
            case GET_NEW_RESOURCE:
                List<Resource> resList = Utilities.getResource(mp);
                if (resList != null) {
                    for (Resource resource : resList){
                        resourceItems.add(NewResourceItem.TransferToMe(resource));
                    }
                    resourceAdapter.notifyDataSetChanged();
                    findViewById(R.id.MainActivity_progress_bar).setVisibility(View.INVISIBLE);
                    if (resSwipeRefreshLayout.isRefreshing())
                        resSwipeRefreshLayout.setRefreshing(false);
                    loadResItemImage();
                }
                break;
            case GET_RESOURCE_BY_TYPE:
                break;
        }
    }

    @Override
    public void onError(int statusCode) {
        Toast.makeText(MyApplication.getContext(), Utilities.TOAST_NET_WORK_ERROR, Toast.LENGTH_SHORT).show();
    }
}
