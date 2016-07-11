package vn.k2t.traficjam.database;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataBaseHelper extends SQLiteOpenHelper {

	private static String DATABASE_PATH = "/data/data/vn.k2t.traficjam/databases/";
	private static String DATABASE_NAME = "traffic.sqlite";
	public static int DATABASE_VERSION = 1;
	private String TAG = "TAG";
	private SQLiteDatabase myDataBase;
	private final Context myContext;

	private static final String COLUMN_UID = "uid";
	private static final String COLUMN_NAME = "name";
	private static final String COLUMN_EMAIL = "email";
	private static final String COLUMN_AVATAR = "avatar";
	private static final String COLUMN_UIDPROVIDER = "uidProvider";
	private static final String COLUMN_RANK = "rank";
	private static final String COLUMN_LOCATION = "location";

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.myContext = context;
		checkAndCopyDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		checkAndCopyDatabase();
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	@Override
	public synchronized void close() {
		if (myDataBase != null)
			myDataBase.close();
		super.close();
	}

	public void checkAndCopyDatabase() {
		boolean dbExist = checkDataBase();
		if (dbExist) {
			Log.d(TAG, "database already exist!");
		} else {
			this.getReadableDatabase();
			try {
				copyDataBase();
			} catch (IOException e) {
				Log.d(TAG, "Error copying database");
			}
		}
	}

	private boolean checkDataBase() {
		SQLiteDatabase checkDB = null;
		try {
			String myPath = DATABASE_PATH + DATABASE_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null,
					SQLiteDatabase.OPEN_READWRITE);
		} catch (SQLiteException e) {

		}
		if (checkDB != null) {
			checkDB.close();
		}

		return checkDB != null ? true : false;
	}

	private void copyDataBase() throws IOException {
		InputStream myInput = myContext.getAssets().open(DATABASE_NAME);
		String outFileName = DATABASE_PATH + DATABASE_NAME;
		OutputStream myOutput = new FileOutputStream(outFileName);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public SQLiteDatabase openDataBase() throws SQLException {
		String myPath = DATABASE_PATH + DATABASE_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
		return myDataBase;
	}


		
	/**
	 * 
	 * @param sql
	 * @throws SQLException
	 */
	public void ExeSQLData(String sql) throws SQLException {
		myDataBase.execSQL(sql);
	}

	public Cursor QueryData(String query) throws SQLException {
		return myDataBase.rawQuery(query, null);
	}
}
