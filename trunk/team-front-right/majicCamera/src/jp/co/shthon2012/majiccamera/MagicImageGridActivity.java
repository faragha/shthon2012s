package jp.co.shthon2012.majiccamera;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.co.shthon2012.majiccamera.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class MagicImageGridActivity extends Activity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createLayout();
    }

	private GridView gridImg;
    List<String> imgList = new ArrayList<String>();
    private void createLayout(){
    	setContentView(R.layout.gridview);
    	gridImg = (GridView)findViewById(R.id.gridImg);

        // SDカードのFileを取得
        File file = new File(Environment.getExternalStorageDirectory(), "/DCIM/Camera/");
        List<String> dirList = new ArrayList<String>();
        dirList.add(file.getPath());
        imgList = new ArrayList<String>();

        // SDカード内のファイルを検索。
        int m = 0;
        int n = 0;

        while(dirList.size() > m){
            File subDir = new File(dirList.get(m));
            String subFileName[] = subDir.list();
            n = 0;
            while(subFileName.length > n){
                File subFile = new File(subDir.getPath() + "/" + subFileName[n]);
                if(subFile.getName().endsWith("jpg") || subFile.getName().endsWith("JPG")){
                	String imgFilePath =subDir.getPath() + "/" + subFileName[n];
                	imgList.add(imgFilePath);
                }
                n++;
            }
            m++;
        }

        gridImg.setAdapter(new ImageAdapter(this));

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
    	// 戻るボタンが押されたとき
        if(e.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // ボタンが離されたとき
            if (e.getAction() == KeyEvent.ACTION_UP) {
            	finish();
            	Intent intent;
                intent = new Intent(this, DietcameraActivity.class);
                startActivity(intent);
                return true;
            }
        }

       return super.dispatchKeyEvent(e);
    }

    public class ImageAdapter extends BaseAdapter implements OnClickListener {
        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imgList.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(220, 220));
                imageView.setAdjustViewBounds(true);
                imageView.setPadding(10, 10, 10, 10);
            } else {
                imageView = (ImageView) convertView;
            }
            imageView.setOnClickListener(this);
            imageView.setId(position);
            File f = new File(imgList.get(position));
            BitmapFactory.Options bmOp = new BitmapFactory.Options();
            imageView.setImageBitmap(BitmapFactory.decodeFile(f.getPath(),bmOp));
            return imageView;
        }

        public void onClick(View v) {
            Intent intent;
            intent = new Intent(mContext, MajicPreviewActivity.class);
            intent.putExtra("imgPath", imgList.get(v.getId()));
            startActivity(intent);
        }

        private Context mContext;
    }
}
