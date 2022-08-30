package com.ezviz.demo.videotalk;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import com.ezviz.sdk.videotalk.meeting.EZRtcParam;
import com.ezviz.sdk.videotalk.sdk.EZRtc;

import java.util.ArrayList;
import java.util.List;

import ezviz.ezopensdk.R;

//@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class EZVideoMeetingService extends Service {

    public static final int SCREEN_RECORDER_CREATE = 1;
    public static final int CONFLUENCE_INIT = 2;

    public static final String SCREEN_PARAM_TYPE = "type";
    public static final String SCREEN_PARAM_CODE = "result_code";
    public static final String SCREEN_PARAM_REQUEST = "request_code";
    public static final String SCREEN_PARAM_DATA = "data";


    public static final String LAUNCH_FROM_NOTIFICATION = "launch_from_notification";

    private EZRtc mEZRtc;

    //用于存储Activity退出后的数据
    private StoredData storedData = new StoredData();

    public static class StoredData{
        public List<EZClientInfo> mClientList = new ArrayList<>();
        public String mAppId;
        public int mRoomId;
        public String userId;
        public EZRtcParam.NetQuality selfNetQuality = EZRtcParam.NetQuality.BAV_NETWORK_QUALITY_UNKNOWN;
        public String sharedUserId;

        //自己当前的状态
        public boolean videoState;
        public boolean smallVideoState;
        public boolean audioState;
        public boolean shareSwitch;
    }

    class MyBinder extends Binder{

        public EZRtc getRtc(){
            return mEZRtc;
        }

        public StoredData getData(){
            return storedData;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            int type = intent.getIntExtra(SCREEN_PARAM_TYPE, -1);
            switch (type){
                case SCREEN_RECORDER_CREATE:
                    createScreenRecorder(intent);
                    break;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(true);
        }
    }


    private void createScreenRecorder(Intent intent){
        int resultCode = intent.getIntExtra(SCREEN_PARAM_CODE, -1);
        int requestCode = intent.getIntExtra(SCREEN_PARAM_REQUEST, -1);
        Intent data = intent.getParcelableExtra(SCREEN_PARAM_DATA);
        mEZRtc.onActivityResult(requestCode, resultCode, data);
    }

}
