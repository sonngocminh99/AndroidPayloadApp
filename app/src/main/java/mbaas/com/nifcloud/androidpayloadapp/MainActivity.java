package mbaas.com.nifcloud.androidpayloadapp;

import android.Manifest;
import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nifcloud.mbaas.core.NCMB;
import com.nifcloud.mbaas.core.NCMBInstallation;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    TextView _pushId;
    TextView _richurl;
    public static String APP_KEY = "2bfb444423219ff54256bbe41ff270c5d8c3e81eaa3121c18603363e99b0b673";
    public static String CLIENT_KEY = "2e0167555ae06b73a73a8b2ef1ea9614d566b17cb7c0d191da80797221088bf2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            JSONObject tmpBlank = new JSONObject("{'No key':'No value'}");
            ListView lv = (ListView) findViewById(R.id.lsJson);
            if (lv != null) {
                lv.setAdapter(new ListAdapter(this, tmpBlank));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //**************** APIキーの設定とSDKの初期化 **********************
        NCMB.initialize(this.getApplicationContext(), APP_KEY, CLIENT_KEY);
        final NCMBInstallation installation = NCMBInstallation.getCurrentInstallation();

        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= 32) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
                return;
            ActivityResultLauncher<String> launcher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), isGranted -> {
                        Toast.makeText(this, "Push notification permission is allowed", Toast.LENGTH_SHORT).show();
                    }
            );
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //**************** ペイロード、リッチプッシュを処理する ***************
        Intent intent = getIntent();

        //プッシュ通知IDを表示
        _pushId = (TextView) findViewById(R.id.txtPushid);
        String pushid = intent.getStringExtra("com.nifcloud.mbaas.PushId");
        _pushId.setText(pushid);

        //RichURLを表示
        _richurl = (TextView) findViewById(R.id.txtRichurl);
        String richurl = intent.getStringExtra("com.nifcloud.mbaas.RichUrl");
        _richurl.setText(richurl);

        //プッシュ通知のペイロードを表示
        if (intent.getStringExtra("com.nifcloud.mbaas.Data") != null) {
            try {
                JSONObject json = new JSONObject(intent.getStringExtra("com.nifcloud.mbaas.Data"));
                if (json != null) {
                    ListView lv = (ListView) findViewById(R.id.lsJson);
                    lv.setAdapter(new ListAdapter(this, json));
                }
            } catch (JSONException e) {
                //エラー処理
            }
        }
        intent.removeExtra("com.nifcloud.mbaas.RichUrl");
    }
}
