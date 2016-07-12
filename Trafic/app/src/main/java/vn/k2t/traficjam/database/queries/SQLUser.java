package vn.k2t.traficjam.database.queries;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;

import vn.k2t.traficjam.database.DataBaseHelper;
import vn.k2t.traficjam.model.UserTraffic;


public class SQLUser {


    private DataBaseHelper databaseHelper;
    private HashMap<String, String> itemHash;
    private static final String TABLE_USER = "user";
    private static final String TABLE_USER_VIDEO = "video_user";
    private static final String TABLE_USER_SHARE = "user_share";

    private static final String COLUMN_UID = "uid";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_AVATAR = "avatar";
    private static final String COLUMN_UIDPROVIDER = "uidProvider";
    private static final String COLUMN_RANK = "rank";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";


    private static String sqlUsers = "SELECT * " + "FROM " + TABLE_USER;
    private static String sqlVideo = "SELECT * " + "FROM " + TABLE_USER_VIDEO + " where uid='%s'";
    private static String sqlShare = "SELECT * " + "FROM " + TABLE_USER_SHARE + " where uid='%s'";


    public SQLUser(Context context) {
        super();
        // TODO Auto-generated constructor stub
        databaseHelper = new DataBaseHelper(context);
    }

    public ArrayList<UserTraffic> getListUsers() {
        ArrayList<UserTraffic> users = new ArrayList<UserTraffic>();
        Cursor cursorParent = databaseHelper.getReadableDatabase().rawQuery(
                String.format(sqlUsers, "0"), null);
        if (cursorParent != null && cursorParent.getCount() != 0) {
            while (cursorParent.moveToNext()) {
                UserTraffic user = new UserTraffic();
            }
            cursorParent.close();
        }
        return users;
    }

    public UserTraffic getUser() {
        UserTraffic users = null;
        Cursor cursor = databaseHelper.getReadableDatabase().rawQuery(sqlUsers, null);
        if (cursor == null)
            return null;
        cursor.moveToFirst();
        int indexUserID = cursor.getColumnIndex(COLUMN_UID);
        int indexUserName = cursor.getColumnIndex(COLUMN_NAME);
        int indexEmail = cursor.getColumnIndex(COLUMN_EMAIL);
        int indexAvatar = cursor.getColumnIndex(COLUMN_AVATAR);
        int indeUidProvider = cursor.getColumnIndex(COLUMN_UIDPROVIDER);
        int indexRank = cursor.getColumnIndex(COLUMN_RANK);
        int indexLacation = cursor.getColumnIndex(COLUMN_LATITUDE);
        int indexLongcation = cursor.getColumnIndex(COLUMN_LONGITUDE);
        while (!cursor.isAfterLast()) {
            users = new UserTraffic(cursor.getString(indexUserID), cursor.getString(indexUserName), cursor.getString(indexAvatar), cursor.getString(indexEmail), cursor.getString(indeUidProvider), cursor.getString(indexRank), cursor.getString(indexLacation), cursor.getString(indexLongcation), null);
            cursor.moveToNext();
        }
        cursor.close();
        return users;
    }

    public long insertUser(UserTraffic mUser) {
        //myDataBase=myContext.openOrCreateDatabase(DATABASE_PATH + "/" + DATABASE_NAME, Context.MODE_PRIVATE, null);

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_UID, mUser.getUid());
        contentValues.put(COLUMN_NAME, mUser.getName());
        contentValues.put(COLUMN_EMAIL, mUser.getEmail());
        contentValues.put(COLUMN_AVATAR, mUser.getAvatar());
        contentValues.put(COLUMN_UIDPROVIDER, mUser.getUidProvider());
        contentValues.put(COLUMN_RANK, mUser.getRank());
        contentValues.put(COLUMN_LATITUDE, mUser.getLatitude());
        contentValues.put(COLUMN_LONGITUDE, mUser.getLongitude());
        long result = databaseHelper.getWritableDatabase().insert(TABLE_USER, null, contentValues);

        return result;

    }


    public void deleteUser() {
        long result = databaseHelper.getWritableDatabase().delete(TABLE_USER, null, null);
    }

    public void deleteShare(String id) {
        long result = databaseHelper.getWritableDatabase().delete(TABLE_USER_SHARE, "uid" + " =?", new String[]{String.valueOf(id)});
    }
}
