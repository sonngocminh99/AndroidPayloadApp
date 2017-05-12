package mbaas.com.nifty.androidpayloadapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBInstallation;
import com.nifty.cloud.mb.core.NCMBQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextView _pushId;
    TextView _richurl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            JSONObject tmpBlank = new JSONObject( "{'No key':'No value'}");
            ListView lv = (ListView) findViewById(R.id.lsJson);
            if (lv != null) {
                lv.setAdapter(new ListAdapter(this, tmpBlank));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //**************** APIキーの設定とSDKの初期化 **********************
        NCMB.initialize(this.getApplicationContext(), "YOUR_APPLICATION_KEY", "YOUR_CLIENT_KEY");
        final NCMBInstallation installation = NCMBInstallation.getCurrentInstallation();

        //GCMからRegistrationIdを取得しinstallationに設定する
        installation.getRegistrationIdInBackground("ANDROID_SENDER_ID", new DoneCallback() {
            @Override
            public void done(NCMBException e) {
                if (e == null) {
                    installation.saveInBackground(new DoneCallback() {
                        @Override
                        public void done(NCMBException e) {
                            if (e == null) {
                                //保存成功
                            } else if (NCMBException.DUPLICATE_VALUE.equals(e.getCode())) {
                                //保存失敗 : registrationID重複
                                updateInstallation(installation);
                            } else {
                                //保存失敗 : その他
                            }
                        }
                    });
                } else {
                    //ID取得失敗
                }
            }
        });
        setContentView(R.layout.activity_main);
    }

    public static void updateInstallation(final NCMBInstallation installation) {
        //installationクラスを検索するクエリの作成
        NCMBQuery<NCMBInstallation> query = NCMBInstallation.getQuery();
        //同じRegistration IDをdeviceTokenフィールドに持つ端末情報を検索する
        query.whereEqualTo("deviceToken", installation.getDeviceToken());
        //データストアの検索を実行
        query.findInBackground(new FindCallback<NCMBInstallation>() {
            @Override
            public void done(List<NCMBInstallation> results, NCMBException e) {
                //検索された端末情報のobjectIdを設定
                installation.setObjectId(results.get(0).getObjectId());
                //端末情報を更新する
                installation.saveInBackground();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //**************** ペイロード、リッチプッシュを処理する ***************
        Intent intent = getIntent();

        //プッシュ通知IDを表示
        _pushId = (TextView) findViewById(R.id.txtPushid);
        String pushid = intent.getStringExtra("com.nifty.PushId");
        _pushId.setText(pushid);

        //RichURLを表示
        _richurl = (TextView) findViewById(R.id.txtRichurl);
        String richurl = intent.getStringExtra("com.nifty.RichUrl");
        _richurl.setText(richurl);

        //プッシュ通知のペイロードを表示
        if (intent.getStringExtra("com.nifty.Data") != null) {
            try {
                JSONObject json = new JSONObject(intent.getStringExtra("com.nifty.Data"));
                if (json != null) {
                    ListView lv = (ListView) findViewById(R.id.lsJson);
                    lv.setAdapter(new ListAdapter(this, json));
                }
            } catch (JSONException e) {
                //エラー処理
            }
        }
        intent.removeExtra("com.nifty.RichUrl");
    }
}
