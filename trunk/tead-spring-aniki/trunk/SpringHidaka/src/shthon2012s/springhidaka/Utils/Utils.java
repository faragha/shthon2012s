package shthon2012s.springhidaka.Utils;

import java.io.File;
import java.util.ArrayList;

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
}
