package jp.shigepon.ikemenhunter;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class IkemenHunterService extends Service {

	private KeyguardManager keyguardmanager;
	private KeyguardLock keyguardlock;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		keyguardmanager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		keyguardlock = keyguardmanager.newKeyguardLock("IkemenHunter");
		keyguardlock.disableKeyguard();
		IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				Intent activityIntent = new Intent(context, IkemenHunterActivity.class);
				activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				activityIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				context.startActivity(activityIntent);
			}
		}, filter);
	}
}
