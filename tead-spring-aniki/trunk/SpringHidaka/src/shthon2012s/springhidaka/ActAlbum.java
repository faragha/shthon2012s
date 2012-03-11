package shthon2012s.springhidaka;

import java.io.File;

import shthon2012s.springhidaka.Utils.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class ActAlbum extends Activity {

	private Context ctx;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.album);

		//ランダムに生成する
		Utils.createDrawbleCollection();

		ctx = this.getApplicationContext();

		testFileSearch();

		//どっかきめたフォルダに保存したの読んでgridviewとか？
		//ファイル名を時刻＋セリフにしちゃえば管理楽なんじゃね
		// ↑あー、らくだわ。
	}

	protected void GoPicture(String imagePath) {
		Intent it = new Intent(ctx, ActPicture.class);

		it.putExtra("filepath", imagePath);
		it.putExtra("saveflag", false);

		startActivity(it);
	}

	private void testFileSearch(){

		File dir = new File( Utils.getAnikiDir() );
		final File[] files = dir.listFiles();

		final String[] str_items;
		str_items = new String[files.length + 1];
		for (int i = 0; i < files.length; i++) {
		    File file = files[i];
		    str_items[i] = file.getName();
		}

		GoPicture( Utils.getAnikiDir() + "/" + str_items[0]);
	}
}
