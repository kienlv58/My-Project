package vn.k2t.traficjam.untilitis;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utilities {
    public Context mContext;


    public Utilities(Context mContext) {
        super();
        this.mContext = mContext;

    }


    /**
     * *******************************************************************************************************
     */
    //Check network status
    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            return true;
        } else {
            return false;
        }
    }

    //Check device
    public String typeOfDevice() {
        String device = "";
        TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        if (manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE) {
            device = "TABLET";
        } else {
            device = "MOBILE";
        }
        return device;
    }

    /**
     * *********************************************************************************************************
     */

    //Share Link to social network
    public void share_link(String fileShare) {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, fileShare);
        // intent.putExtra(android.content.Intent.EXTRA_STREAM,fileShare);
        ((Activity) mContext).startActivityForResult(Intent.createChooser(intent,
                "Chọn ứng dụng cần chia sẻ"), 100);
    }

    /**
     * *********************************************************************************************************
     */
    //Convert milisecond to time - format H:i:s
    public String milisecondToTime(long milisecond) {
        long second = (milisecond / 1000) % 60;
        long minute = (milisecond / (1000 * 60)) % 60;
        long hour = (milisecond / (1000 * 60 * 60)) % 24;

        String time = String.format("%02d:%02d:%02d", hour, minute, second);
        return time;
    }

    /**
     * *********************************************************************************************************
     */
    //Canculate Date
    public static String currentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(calendar.getTime());
        return formattedDate;
    }

    public static float convertRadius(Location a, Location b) {
        return a.distanceTo(b);
    }

    public static final double LOCAL_PI = 3.1415926535897932385;

    static double ToRadians(double degrees) {
        double radians = degrees * LOCAL_PI / 180;
        return radians;
    }

    public static double DirectDistance(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75;
        double dLat = ToRadians(lat2 - lat1);
        double dLng = ToRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(ToRadians(lat1)) * Math.cos(ToRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        double meterConversion = 1609.00;
        return dist * meterConversion;
    }

    /**
     * *************************************************************************************************
     */
//    public void Share(Activity activity, int keyShare) {
//        List<Intent> targetShareIntents = new ArrayList<Intent>();
//        Intent shareIntent = new Intent();
//        shareIntent.setAction(Intent.ACTION_SEND);
//        shareIntent.setType("text/plain");
//        List<ResolveInfo> resInfos = activity.getPackageManager().queryIntentActivities(shareIntent, 0);
//        if (!resInfos.isEmpty()) {
//            for (ResolveInfo resInfo : resInfos) {
//                String packageName = resInfo.activityInfo.packageName;
//                if (packageName.contains("com.facebook.katana")) {
//                    Intent intent = new Intent();
//                    intent.setComponent(new ComponentName(packageName, resInfo.activityInfo.name));
//                    intent.setAction(Intent.ACTION_SEND);
//                    intent.setType("text/plain");
//                    intent.putExtra(Intent.EXTRA_TEXT, AppConstants.APP_LINK);
//                    intent.setPackage(packageName);
//                    targetShareIntents.add(intent);
//                }
//                if (packageName.contains("com.google.android.gm")) {
//                    Intent intent = new PlusShare.Builder(activity)
//                            .setType("text/plain")
//                            .setText("Welcome to KQTT")
//                            .setContentUrl(Uri.parse(AppConstants.APP_LINK))
//                            .getIntent();
//                    targetShareIntents.add(intent);
//                }
//            }
//            if (!targetShareIntents.isEmpty()) {
//                Intent chooserIntent = Intent.createChooser(targetShareIntents.remove(0), "Chọn ứng dụng cần chia sẻ");
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetShareIntents.toArray(new Parcelable[]{}));
//                activity.startActivityForResult(chooserIntent, keyShare);
//            } else {
//                Toast.makeText(mContext, mContext.getResources().getString(R.string.no_app_share), Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }

    //    public static String md5(String s) {
//        try {
//
//            // Create MD5 Hash
//            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
//            digest.update(s.getBytes());
//            byte messageDigest[] = digest.digest();
//
//            // Create Hex String
//            StringBuffer hexString = new StringBuffer();
//            for (int i = 0; i < messageDigest.length; i++)
//                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
//            return hexString.toString();
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//        return "";
//
//    }
    public static String md5(String pass) {
        String password = null;
        MessageDigest mdEnc;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
            mdEnc.update(pass.getBytes(), 0, pass.length());
            pass = new BigInteger(1, mdEnc.digest()).toString(16);
            while (pass.length() < 32) {
                pass = "0" + pass;
            }
            password = pass;
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        }
        return password;
    }

    public boolean getSpecialCharacterCount(String s) {
        Pattern p = Pattern.compile("[^A-Za-z0-9]");
        Matcher m = p.matcher(s);
        return m.find();
    }

    public String getAddressFromLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(mContext);

        String address = "";
        try {
            address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0).getAddressLine(0);
        } catch (IOException e) {
        }

        return address;
    }

}