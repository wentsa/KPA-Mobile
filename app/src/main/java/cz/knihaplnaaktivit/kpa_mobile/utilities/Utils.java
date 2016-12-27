package cz.knihaplnaaktivit.kpa_mobile.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;

import java.util.Collections;
import java.util.List;

public class Utils {

    /**
     * Formats currency.</br>
     * Examples:<br/>
     * <table>
     *     <tr><td><b>val</b></td><td><b>ret</b></td></tr>
     *     <tr><td>12</td><td>"12"</td></tr>
     *     <tr><td>12345</td><td>"12 345"</td></tr>
     *     <tr><td>123456789</td><td>"123 456 789"</td></tr>
     *     <tr><td>-15</td><td>"-15"</td></tr>
     *     <tr><td>-123456</td><td>"-123 456"</td></tr>
     * </table>
     *
     * @param val value
     */
    private static String formatCurrency(long val) {
        if (val == 0) {
            return "0";
        }

        boolean lesserThanZero = false;

        if (val < 0) {
            lesserThanZero = true;
            val = -val;
        }

        long x = val;
        boolean started = false;
        StringBuilder res = new StringBuilder();
        while (x > 0) {
            if (started) {
                res.insert(0, " ");
            }
            long toInsert = x % 1000;

            x /= 1000;

            if (x > 0) {
                res.insert(0, String.format("%3s", String.valueOf(toInsert)).replace(' ', '0'));
            } else {
                res.insert(0, String.valueOf(toInsert));
            }
            started = true;
        }
        if (lesserThanZero) {
            res.insert(0, "-");
        }
        return res.toString();
    }

    /**
     * Formats currency with shortcut.</br>
     * Examples:<br/>
     * <table>
     *     <tr><td><b>val</b></td><td><b>ret</b></td></tr>
     *     <tr><td>12</td><td>"12"</td></tr>
     *     <tr><td>12345</td><td>"12 tis"</td></tr>
     *     <tr><td>123456789</td><td>"123 mil"</td></tr>
     *     <tr><td>-15</td><td>"-15"</td></tr>
     *     <tr><td>-123456</td><td>"-123 tis"</td></tr>
     * </table>
     *
     * @param val value
     */
    private static String formatCurrencyShortcutted(int val) {
        // -253 / 253
        if(val < 1_000 && val > -1_000) {
            return String.valueOf(val);
        }
        // -1,3 tis / 1,3 tis
        if(val < 10_000 && val > -10_000) {
            return val / 1_000 + "," + Math.abs(val / 100) % 10 + " tis";
        }
        // -43 tis / 43 tis
        if(val < 1_000_000 && val > -1_000_000) {
            return val / 1_000 + " tis";
        }
        // -5,8 mil / 5,8 mil
        if(val < 10_000_000 && val > -10_000_000) {
            return val / 1_000_000 + "," + Math.abs(val / 100_000) % 10 + " mil";
        }
        // -12 mil / 12 mil
        if(val < 1_000_000_000 && val > -1_000_000_000) {
            return val / 1_000_000 + " mil";
        }
        // -2.1 mld / 2.1 mld
        return val / 1_000_000_000 + "," + Math.abs(val / 100_000_000) % 10 + " mld";
    }

    /**
     * Returns formatted currency value with appender <br/>
     * Eg.: for 123456 return "123 456 {append}"<br/>
     * @see #formatCurrency(long)
     * @param val value
     * @param appender appender
     * @return formatted currency
     */
    public static String getCurrencyFormat(long val, String appender) {
        return Utils.formatCurrency(val) + " " + appender;
    }

    /**
     * Returns formatted currency value with shortcut and appender <br/>
     * Eg.: for 123456 return "123 tis {append}"<br/>
     * @see #formatCurrencyShortcutted(int)
     * @param val value
     * @param appender appender
     * @return formatted currency
     */
    public static String getCurrencyFormatShortcutted(int val, String appender) {
        return Utils.formatCurrencyShortcutted(val) + " " + appender;
    }

    /**
     * Transforms null into empty list.
     */
    public static <T> List<T> nullToEmpty(List<T> list) {
        return list == null ? Collections.<T> emptyList() : list;
    }

    /**
     * Checks email validity
     */
    public static boolean isValidEmail(CharSequence mail) {
        return !TextUtils.isEmpty(mail) && android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches();
    }

    /**
     * Checks internet connection
     */
    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Checks if user is connected via wifi
     */
    public static boolean isWifiConnected(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = cm.getAllNetworks();
            for(Network n : networks) {
                NetworkInfo ni = cm.getNetworkInfo(n);
                if(ni != null && ni.getType() == ConnectivityManager.TYPE_WIFI && ni.isConnected()) {
                    return true;
                }
            }
        } else {
            NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if(ni != null && ni.isConnected()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Loads bitmap and scales to fit width and height
     * @param ctx context
     * @param id drawable id
     * @param width width to fit or 0 to ignore
     * @param height height to fit or 0 to ignore
     * @return scaled bitmap
     */
    private static Bitmap getScaledBitmap(Context ctx, int id, int width, int height) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(ctx.getResources(), id, bmOptions);
        int photoH = bmOptions.outHeight;
        int photoW = bmOptions.outWidth;

        // Determine how much to scale down the image
        int scaleFactor = getScaleFactor(photoW, photoH, width, height);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeResource(ctx.getResources(), id, bmOptions);
    }

    /**
     * @param path file path
     * @see #getScaledBitmap(Context, int, int, int)
     */
    public static Bitmap getScaledBitmap(String path, int width, int height) {
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, bmOptions);
        int photoH = bmOptions.outHeight;
        int photoW = bmOptions.outWidth;

        // Determine how much to scale down the image
        int scaleFactor = getScaleFactor(photoW, photoH, width, height);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(path, bmOptions);
    }

    private static int getScaleFactor(int pWidth, int pHeight, int width, int height) {
        if(height == 0 && width == 0) {
            return 1;
        } else if(width == 0) {
            return pHeight/height;
        } else if(height == 0) {
            return pWidth/width;
        } else {
            return Math.min(pHeight/height, pWidth/width);
        }
    }

    /**
     * Starts facebook application if available. Otherwise start web browser
     * @param ctx app context
     * @param fbContext part of facebook url (facebook.com/{fbContext})
     * @param facebookId facebook ID
     * @param isPage true if page, false if group
     */
    public static void visitFacebook(Context ctx, String fbContext, String facebookId, boolean isPage) {
        String facebookUrl = "https://www.facebook.com/" + fbContext;
        try {
            int versionCode = ctx.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                Uri uri = Uri.parse("fb://facewebmodal/f?href=" + facebookUrl);
                ctx.startActivity(new Intent(Intent.ACTION_VIEW, uri));
            } else {
                if(isPage) {
                    ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + facebookId)));
                } else {
                    ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("fb://group/" + facebookId)));
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(facebookUrl)));
        }
    }

    /**
     * Checks current thread and decides if it is UI thread
     */
    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

}
