package shthon2012s.springhidaka.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.lang.reflect.Field;

import shthon2012s.springhidaka.DialNumber;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;

public class Utils {
	public static void delete(File f) {
		if (f.exists() == false) {
			return;
		}
		if (f.isFile()) {
			f.delete();
		}
		if (f.isDirectory()) {
			File[] files = f.listFiles();
			for (int i = 0; i < files.length; i++) {
				delete(files[i]);
			}
			f.delete();
		}
	}

	public static boolean existsFile(String fileName) {
		return (new File(fileName)).exists();
	}

	public static ArrayList<DialNumber> getAll(Activity act) {

		ArrayList<DialNumber> datas = new ArrayList<DialNumber>();
		ContentResolver resolver = act.getContentResolver();
		Cursor cPhone;
		String contactsPhone;
		Cursor c = resolver.query(
				// Contacts.CONTENT_URI,
				ContactsContract.Contacts.CONTENT_URI, new String[] {
						Contacts._ID, Contacts.LOOKUP_KEY,
						ContactsContract.Contacts.DISPLAY_NAME,
						ContactsContract.Contacts.HAS_PHONE_NUMBER }, null,
				null, Contacts.DISPLAY_NAME);

		if (c.moveToFirst()) {
			do {

				if (Integer
						.parseInt(c.getString(c
								.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

					long id = c.getLong(0);
					String contactsId = String.valueOf(id);

					String displayName = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
					displayName = c.getString(c.getColumnIndex(displayName));

					cPhone = resolver
							.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
									new String[] { ContactsContract.CommonDataKinds.Phone.DATA1 },
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID
											+ " = ? ",
									new String[] { contactsId }, null);
					while (cPhone.moveToNext()) {
						contactsPhone = cPhone.getString(0);
						datas.add(new DialNumber(displayName, contactsPhone));

					}
					cPhone.close();
				}

			} while (c.moveToNext());
		}
		c.close();

		return datas;
	}


	private static ArrayList<Integer> frameList;
	private static ArrayList<Integer> copy1List;
	private static ArrayList<Integer> copy2List;
	private static ArrayList<Integer> copy3List;

	public static void createDrawbleCollection() {

		// 実体
		frameList = new ArrayList<Integer>();
		copy1List = new ArrayList<Integer>();
		copy2List = new ArrayList<Integer>();
		copy3List = new ArrayList<Integer>();

		// Rクラスの全ての内部クラスを取得
		Class<?>[] classes = shthon2012s.springhidaka.R.class.getClasses();
		for (Class<?> cls : classes) {
			// 内部クラスがdrawbleならコレクションを作る
			if (cls.getSimpleName().equals("drawable")) {
				Field[] fields = cls.getFields();
				String name;
				for (Field field : fields) {
					try {
						name = field.getName();
						if(name.startsWith("frame")){	//frame前方一致
							//コレクションに格納
							frameList.add((Integer) field.get(name));
						}
						if(name.startsWith("copy1")){
							//コレクションに格納
							copy1List.add((Integer) field.get(name));
						}
						if(name.startsWith("copy2")){
							//コレクションに格納
							copy2List.add((Integer) field.get(name));
						}

						if(name.startsWith("copy3")){
							//コレクションに格納
							copy3List.add((Integer) field.get(name));
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static int getDrawableFrameId(){
		Random rand = new Random();
		return frameList.get(rand.nextInt(frameList.size()));
	}
	public static int getDrawableCopy1Id(){
		Random rand = new Random();
		return copy1List.get(rand.nextInt(copy1List.size()));
	}
	public static int getDrawableCopy2Id(){
		Random rand = new Random();
		return copy2List.get(rand.nextInt(copy2List.size()));
	}
	public static int getDrawableCopy3Id(){
		Random rand = new Random();
		return copy3List.get(rand.nextInt(copy3List.size()));
	}
}
