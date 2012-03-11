package shthon2012s.springhidaka;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import shthon2012s.springhidaka.Utils.DBG;
import shthon2012s.springhidaka.Utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class ActAlbum extends Activity {

	private Context ctx;
	private GridView gv;
	File[] files;
	private List<String> imgList = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.albumgrid);

		gv = (GridView) findViewById(R.id.gv);
		// ランダムに生成する
		// Utils.createDrawbleCollection();

		ctx = this.getApplicationContext();

		files = getFiles();
		if (files == null) {

			DBG.LogOut(3, "ActAlbum", "files:null");
		} else {
			DBG.LogOut(3, "ActAlbum", ":" + files.length);
			for (int i = 0; i < files.length; i++) {
				imgList.add(files[i].getAbsolutePath());
			}
			gv.setAdapter(new ImageAdapter(ctx));
			gv.setOnItemClickListener(new GridViewOnClick());
		}

		// testFileSearch();

		// どっかきめたフォルダに保存したの読んでgridviewとか？
		// ファイル名を時刻＋セリフにしちゃえば管理楽なんじゃね
		// ↑あー、らくだわ。
	}

	 protected void GoPicture(String imagePath) {
	 Intent it = new Intent(ctx, ActPicture.class);

	 it.putExtra("filepath", imagePath);
	 it.putExtra("saveflag", false);

	 startActivity(it);
	 }
	//
	// private void testFileSearch(){
	//
	// File dir = new File( Utils.getAnikiDir() );
	// final File[] files = dir.listFiles();
	//
	// final String[] str_items;
	// str_items = new String[files.length + 1];
	// for (int i = 0; i < files.length; i++) {
	// File file = files[i];
	// str_items[i] = file.getName();
	// }
	//
	// GoPicture( Utils.getAnikiDir() + "/" + str_items[0]);
	// }

	private File[] getFiles() {

		DBG.LogOut(3, "ActAlbum", "getFiles:" + Utils.getAnikiDir());
		File dir = new File(Utils.getAnikiDir());
		return dir.listFiles();
	}

	public class ImageAdapter extends BaseAdapter {
		Context mContext;

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

		@Override
		public View getView(int i, View convertView, ViewGroup viewgroup) {
			ImageView imageView;
			if (convertView == null) {
				// imageViewを作成
				imageView = new ImageView(mContext);
				imageView.setLayoutParams(new GridView.LayoutParams(100, 100));
				imageView.setAdjustViewBounds(true);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
				imageView.setPadding(8, 8, 8, 8);

			} else {
				imageView = (ImageView) convertView;
			}
			// ビットマップ変換
			File f = new File(imgList.get(i));
			BitmapFactory.Options bmpOp = new BitmapFactory.Options();
			bmpOp.inSampleSize = 50;
			imageView.setImageBitmap(BitmapFactory.decodeFile(f.getPath(),
					bmpOp));
			return imageView;
		}
	}
	public class GridViewOnClick implements OnItemClickListener {

	    public void onItemClick(AdapterView parent, View v, int position, long id) {
	    	GoPicture(imgList.get(position));
	    }
	}
}
