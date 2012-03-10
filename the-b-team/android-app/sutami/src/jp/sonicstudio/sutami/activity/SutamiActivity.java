package jp.sonicstudio.sutami.activity;

import jp.sonicstudio.sutami.R;
import jp.sonicstudio.sutami.R.layout;
import android.app.Activity;
import android.os.Bundle;

public class SutamiActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}