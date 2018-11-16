package mbaas.com.nifcloud.androidpayloadapp;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.messaging.RemoteMessage;
import com.nifcloud.mbaas.core.NCMBFirebaseMessagingService;

import org.json.JSONException;
import org.json.JSONObject;


public class CustomFcmListenerService extends NCMBFirebaseMessagingService {

    private static final String TAG = "FcmService";
    private static final int REQUEST_RESULT = 0;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Bundle data = getBundleFromRemoteMessage(remoteMessage);
        //ペイロードデータの取得
        if (data.containsKey("com.nifcloud.mbaas.Data")) {
            try {
                JSONObject json = new JSONObject(data.getString("com.nifcloud.mbaas.Data"));
            } catch (JSONException e) {
                //エラー処理
            }
        } else if (data.containsKey("com.nifcloud.mbaas.PushId")) {
            String pushid = data.getString("com.nifcloud.mbaas.PushId");
            Log.d(TAG, pushid);
        } else if (data.containsKey("com.nifcloud.mbaas.RichUrl")) {
            String url = data.getString("com.nifcloud.mbaas.RichUrl");
            Log.d(TAG, url);
        }

        //デフォルトの通知
        super.onMessageReceived(remoteMessage);
    }

}
