package jp.shigepon.ikemenhunter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class IkemenHunterReceiver extends BroadcastReceiver {	
	@Override
    public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (Intent.ACTION_BOOT_COMPLETED == action) {
			Intent serviceIntent = new Intent(context, IkemenHunterService.class);
			context.startService(serviceIntent);
		}
    }
}
