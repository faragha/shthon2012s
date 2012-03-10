package shthon2012s.springhidaka;

import java.util.ArrayList;

import shthon2012s.springhidaka.Utils.DBG;
import shthon2012s.springhidaka.Utils.Utils;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ActCallList extends Activity {

	// call list
	private Context ctx;
	private ArrayList<DialNumber> alldata;

	private String phoneNumber;

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
		phoneNumber=pn;
		//ここからAPI

	}
	private void SendSMS(){
		SmsManager smsManager = SmsManager.getDefault();
		String destinationAddress = phoneNumber;

		String text = "写真が届きました。リンクとかなんかそんなんいろいろ。";

		smsManager.sendTextMessage(destinationAddress, null, text, null, null);
	}
}
