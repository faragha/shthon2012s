package jp.co.shthon2012.ikemen_hunter3;

import jp.co.shthon2012.ikemen_counterattack.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
	private Context mContext;

	// プレイヤー
	private Player mPlayer;
	private int mPlayerWidth;
	private int mPlayerHeight;

	// イケメン
	private Ikemen mIkemen;
	private int mIkemenWidth;
	private int mIkemenHeight;

	// ブサメン
	private Busamen[] mBusamen;
	private int mBusamenWidth;
	private int mBusamenHeight;
	private final int BUSAMEN_NUM = 5;

	//初期のディレイ値(ms)
	private final int INIT_DELAY = 700;
	private int mDelayCount = 0;
	private int mDelayNum = 0;

	//score
	private int mScore=0;
	private int GET_POINT = 100;
	private Paint mScorePaint;
	private int SCORE_COLOR = Color.WHITE;
	private int SCORE_SIZE = 20;
	private int mScorePosX;
	private int mScorePosY;

	//GAME OVER
	private Paint mFinalPaint;
	private int FINAL_COLOR = Color.RED;
	private int FINAL_SIZE = 80;
	static private boolean mIsFinish = false;

	// 傾きセンサー用の変数
	private SensorManager mSensorManager;
	private float mPitch;

	// SurfaceView用の変数
	private SurfaceHolder mHolder;
	private Thread mThread;

	static MediaPlayer mMediaPlayer;
	static MediaPlayer mGameOverMediaPlayer;


    public MainSurfaceView(Context context) {
	    super(context);
	    initialize(context);
    }

    public MainSurfaceView(
    	Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	    initialize(context);
    }

    public MainSurfaceView(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    initialize(context);
    }

    private void initialize(Context context) {
    	mContext = context;
	    mIsFinish = false;

        // メディアプレイヤーの作成
        mMediaPlayer = MediaPlayer.create(mContext, R.raw.bgm);
        mGameOverMediaPlayer = MediaPlayer.create(mContext, R.raw.gameover);

        // ループ再生の設定
        mMediaPlayer.setLooping(true);
        mMediaPlayer.start();

		// 傾きを初期化
    	mPitch = 0;

		// SensorManagerを取得
		mSensorManager = (SensorManager)context.getSystemService(
				Context.SENSOR_SERVICE);

		// イベントハンドラを登録
		mSensorManager.registerListener(
			new SensorEventListener() {
				@Override
				public void onAccuracyChanged(
					Sensor sensor, int accuracy) {
				}

				@Override
				public void onSensorChanged(
					SensorEvent event) {
					// 傾きを更新
					mPitch = event.values[SensorManager.DATA_X];
				}
			},
			mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
			SensorManager.SENSOR_DELAY_GAME);

		// SurfaceHolderを作成
		mHolder = getHolder();
	    mHolder.addCallback(this);
	    mHolder.setFixedSize(getWidth(), getHeight());

	    // score描画用のPaint
	    mScorePaint = new Paint();
	    mScorePaint.setColor(SCORE_COLOR);
	    mScorePaint.setTextSize(SCORE_SIZE);

	    //GAME OVER描画用のPaint
	    mFinalPaint = new Paint();
	    mFinalPaint.setColor(FINAL_COLOR);
	    mFinalPaint.setTextSize(FINAL_SIZE);

	    //
	    mBusamen = new Busamen[BUSAMEN_NUM];

	}

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
    	// 戻るボタンが押されたとき
        if(e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // ボタンが離されたとき
            if (e.getAction() == KeyEvent.ACTION_UP) {
            	mMediaPlayer.stop();
            }
        }

       return super.dispatchKeyEvent(e);
    }

	@Override
	public void surfaceChanged(
			SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
	    mThread = new Thread(this);
	    mThread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	    mThread = null;
	}

	Canvas mCanvas;
	@Override
	public void run() {
		mCanvas = null;
	    Paint p = new Paint();
	    p.setColor(Color.WHITE);

	    //scoreの描画位置
	    mScorePosX = getWidth() - 100;
	    mScorePosY = 20;

	    Drawable backGround = mContext.getResources().getDrawable(R.drawable.background);
	    Rect backGroundRect = new Rect(0, 0, getWidth(), getHeight());
	    backGround.setBounds(backGroundRect);

	    while (!mIsFinish) {
	    	// 0.01秒のwaitを入れる
	    	int sleepTime = 10;
	    	try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}

	        try {
	        	// canvasを取得
	        	mCanvas = mHolder.lockCanvas();

		        if (mCanvas != null) {
		        	// canvasを塗りつぶす
			        mCanvas.drawRect(0, 0, getWidth(), getHeight(), p);
			        backGround.draw(mCanvas);

			        // 初回描画時にプレイヤーを作成
					if (mPlayer == null) {
				    	// 表示範囲を取得
				    	Rect rect = new Rect(0, 0, getWidth(), getHeight());
						// プレイヤーを作成
				    	mPlayer = new Player(mContext.getResources().getDrawable(R.drawable.player), rect);

				    	mPlayerWidth = mPlayer.getPlayerWidth();
				    	mPlayerHeight = mPlayer.getPlayerHeight();
					}

			        // 初回描画時にイケメンを作成
					if (mIkemen == null) {
				    	// 表示範囲を取得
						Rect rect = new Rect(0, 0, getWidth(), getHeight());

						// プレイヤーを作成
				    	mIkemen = new Ikemen(mContext, rect);

				    	mIkemenWidth = mIkemen.getPlayerWidth();
				    	mIkemenHeight = mIkemen.getPlayerHeight();
					}

			        // 初回描画時にブサメンを作成
					if (mBusamen[0] == null) {
				    	// 表示範囲を取得
						Rect rect = new Rect(0, 0, getWidth(), getHeight());
						for(int i=0;i<BUSAMEN_NUM;i++){
							// ブサメンを作成
					    	mBusamen[i] = new Busamen(mContext, rect);
						}
						mBusamenWidth = mBusamen[0].getPlayerWidth();
				    	mBusamenHeight = mBusamen[0].getPlayerHeight();
					}

					// プレイヤーを移動・描画
					mPlayer.move(mPitch);
					mPlayer.draw(mCanvas);

					// イケメンを移動・描画
					mIkemen.move();
					mIkemen.draw(mCanvas);
					if(isClash(mPlayer, mIkemen)){
						mScore += GET_POINT;
						mIkemen.restartIkemen();
					}

					// ブサメンを移動・描画
					for(int i=0;i<BUSAMEN_NUM;i++){
						//最初の出現にWaitを設ける
						if(i < mDelayNum ){
							mBusamen[i].move();
							mBusamen[i].draw(mCanvas);
						}else{
							mDelayCount+=sleepTime;
							if(mDelayCount >= INIT_DELAY ){
								mDelayCount = 0;
								mDelayNum++;
							}
						}
						if(isClash(mPlayer, mBusamen[i])){
							setGameOverFlg();
						}
					}

					if(mIsFinish){
						viewGameOver();
					}
					mCanvas.drawText( "Score:" + mScore,mScorePosX, mScorePosY, mScorePaint);
		        }
	        } finally {
		        if (mCanvas != null) {
		        	mHolder.unlockCanvasAndPost(mCanvas);
		        }
	        }
    	}
	}

	private boolean isClash(Player pl, Ikemen ik){
		Rect ikRect = ik.getIkemenRect();
		Rect plRect = pl.getPlayerRect();

		//playerの位置取得
		int plLeft = plRect.left;
		int plRight = plLeft + mPlayerWidth;
		int plTop = plRect.bottom;
		int plBottom = plTop + mPlayerHeight;

		//イケメンの位置取得
		int ikLeft = ikRect.left;
		int ikRight = ikLeft + mIkemenWidth;
		int ikTop = ikRect.bottom;
		int ikBottom = ikTop + mIkemenHeight;

		if((plLeft <= ikLeft && ikLeft <= plRight)
				|| (plLeft <= ikRight && ikRight <= plRight)){
			if((plTop <= ikBottom && ikBottom <= plBottom)
				|| (plTop <= ikTop && ikTop <= plBottom)){
				return true;
			}
		}

		return false;
	}

	private boolean isClash(Player pl, Busamen bs){
		Rect bsRect = bs.getBusamenRect();
		Rect plRect = pl.getPlayerRect();

		//playerの位置取得
		int plLeft = plRect.left;
		int plRight = plLeft + mPlayerWidth;
		int plTop = plRect.bottom;
		int plBottom = plTop + mPlayerHeight;

		//ブサメンの位置取得
		int bsLeft = bsRect.left;
		int bsRight = bsLeft + mBusamenWidth;
		int bsTop = bsRect.bottom;
		int bsBottom = bsTop + mBusamenHeight;

		if((plLeft <= bsLeft && bsLeft <= plRight)
				|| (plLeft <= bsRight && bsRight <= plRight)){
			if((plTop <= bsBottom && bsBottom <= plBottom)
				|| (plTop <= bsTop && bsTop <= plBottom)){
				return true;
			}
		}

		return false;
	}

	static public void setIkemenMiss(){
		mIsFinish = true;
	}

	public void setGameOverFlg(){
		mIsFinish = true;
	}

	static public boolean getGameOverFlg(){
		return mIsFinish;
	}

	private void viewGameOver(){
		mIsFinish = true;
		mCanvas.drawText( "GAME OVER", 80, getHeight()/2, mFinalPaint);
		mMediaPlayer.stop();
		mGameOverMediaPlayer.start();
	}

	static public void stopBGM(){
		if(mMediaPlayer != null){
			mMediaPlayer.stop();
		}
	}

}
