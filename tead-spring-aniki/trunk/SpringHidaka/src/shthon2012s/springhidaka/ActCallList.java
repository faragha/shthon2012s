package shthon2012s.springhidaka;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;

import shthon2012s.springhidaka.Utils.DBG;
import shthon2012s.springhidaka.Utils.Params;
import shthon2012s.springhidaka.Utils.Utils;
import shthon2012s.springhidaka.WebApis.AsyncTaskSample;
import shthon2012s.springhidaka.WebApis.UploadAsyncTask;
import shthon2012s.springhidaka.WebApis.UploadAsyncTaskCallback;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActCallList extends Activity implements UploadAsyncTaskCallback {

	// call list
	private AQuery aq;
	private Context ctx;
	private ArrayList<DialNumber> alldata;

	private String phoneNumber;
	public static final String SMS_RECIPIENT_EXTRA = "com.example.android.apis.os.SMS_RECIPIENT";

	public static final String ACTION_SMS_SENT = "com.example.android.apis.os.SMS_SENT_ACTION";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addresslist);

		ctx = this.getApplicationContext();
		LayoutInflater inflater = LayoutInflater.from(ctx);
		LinearLayout a_ll = (LinearLayout) findViewById(R.id.LLsc);

		DialNumber d = null;
		alldata = Utils.getAll(this);
		for (int i = 0; i < alldata.size(); i++) {
			d = alldata.get(i);
			View aLine = inflater.inflate(R.layout.addressline, null);
			TextView t1 = (TextView) aLine.findViewById(R.id.tv_1);
			TextView t2 = (TextView) aLine.findViewById(R.id.tv_2);
			final String pn = d.getNumber();
			t1.setText(d.getName());
			t2.setText(pn);
			aLine.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					SendTo(pn);
				}
			});
			a_ll.addView(aLine);
		}
	}

	protected void SendTo(String pn) {
		DBG.LogOut(3, "To", ":" + pn);
		phoneNumber = pn;
		// ここからAPI

		String path = Params.sdPath + Params.folder + Params.tmpfile;
		UploadAsyncTask post = new UploadAsyncTask(this, this);
		post.execute(path);
		// おわったらSMS

	}

	private void SendSMS(String url) {
		DBG.LogOut(3, "ActCallList", ":SendSMS:" + url);
		SmsManager smsManager = SmsManager.getDefault();
		registerReceiver(sms_bc, new IntentFilter(ACTION_SMS_SENT));
		smsManager.sendTextMessage(phoneNumber, null, "写真が届きました。 " + url,
				PendingIntent.getBroadcast(ActCallList.this, 0, new Intent(
						ACTION_SMS_SENT), 0), null);

	}

	protected void BackToTop() {
		// 終わったらダイアログとかだしてTOP戻るとか？
		unregisterReceiver(sms_bc);
		Toast.makeText(getApplicationContext(), "送信しました", Toast.LENGTH_SHORT)
				.show();
		Intent it = new Intent(ctx, ActSplash.class);
		startActivity(it);
		finish();
	}

	@Override
	public void onSuccessUploadImage(String url) {
		DBG.LogOut(3, "ActCallList", ":onSuccessUploadImage");

		url = "http://api.bitly.com/v3/shorten?longUrl=" + url
				+ "&login=kaakaa&apiKey=R_8e279e29e758e6191d67a44b109cbdbd";
		aq = new AQuery(this);
		aq.ajax(url, JSONObject.class, this, "jsonCallback");

		// http://api.bitly.com/v3/shorten?longUrl=https://play.google.com/store&login=kaakaa&apiKey=R_8e279e29e758e6191d67a44b109cbdbd
	}

	public void jsonCallback(String url, JSONObject json, AjaxStatus status) {
		if (json != null) {

			try {
				json = json.getJSONObject("data");
				url = json.getString("url");

				SendSMS(url);
			} catch (JSONException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}

		} else {
			DBG.LogOut(3, "jsonCallback", ":ajax error");
		}
	}

	@Override
	public void onFailedUploadImage(int statusCode) {
		DBG.LogOut(3, "ActCallList", ":onFailedUploadImage");
		Toast.makeText(getApplicationContext(),
				"Failed, ResponseCode:" + Integer.toString(statusCode),
				Toast.LENGTH_SHORT).show();

	}

	private BroadcastReceiver sms_bc = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String message = "";
			boolean error = true;
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				message = "Message sent!";
				error = false;
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				message = "Error.";
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				message = "Error: No service.";
				break;
			case SmsManager.RESULT_ERROR_NULL_PDU:
				message = "Error: Null PDU.";
				break;
			case SmsManager.RESULT_ERROR_RADIO_OFF:
				message = "Error: Radio off.";
				break;
			}

			if (error) {
				DBG.LogOut(3, "ActCallList", ":BroadcastReceiver:" + message);

				Toast.makeText(getApplicationContext(), "失敗しちゃったごめんね",
						Toast.LENGTH_SHORT).show();
			} else {
				BackToTop();
			}
		}

	};
}
