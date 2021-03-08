package com.app.wingmate.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.app.wingmate.R;
import com.app.wingmate.models.QuestionOption;
import com.app.wingmate.models.UserAnswer;
import com.irozon.sneaker.Sneaker;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.app.wingmate.utils.AppConstants.ERROR;
import static com.app.wingmate.utils.AppConstants.INFO;
import static com.app.wingmate.utils.AppConstants.NORMAL;
import static com.app.wingmate.utils.AppConstants.PARAM_CURRENT_LOCATION;
import static com.app.wingmate.utils.AppConstants.PARAM_OPTIONS_OBJ_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_MANDATORY_ARRAY;
import static com.app.wingmate.utils.AppConstants.PARAM_USER_OPTIONAL_ARRAY;
import static com.app.wingmate.utils.AppConstants.SHORT_TITLE_AGE;
import static com.app.wingmate.utils.AppConstants.SHORT_TITLE_NATIONALITY;
import static com.app.wingmate.utils.AppConstants.SUCCESS;
import static com.app.wingmate.utils.AppConstants.WARNING;

public class Utilities {


    public static final double MILE = 1609.34;
    public static final String MILE_NOTATIONS = "miles";

    public static DecimalFormat REAL_FORMATTER = new DecimalFormat("0.00");

    public static AlertDialog.Builder alertDialogBuilder;
    public static AlertDialog alert;

    public static String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    public static boolean isValidEmail(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static int pxToDp(Context context, int px) {
        return (int) (context.getApplicationContext().getResources().getDisplayMetrics().density * px);
    }

    public static SpannableString getStyledTitle(Context context, String title, String fontName) {
        SpannableString s = new SpannableString(title);
        s.setSpan(new TypefaceSpan(fontName), 0, s.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return s;
    }

    public static void print(String msg) {
//        if (BuildConfig.DEBUG)
        System.out.println(msg);
    }

    public static void printFaceBookKey(Context mcContext) {
        try {
            PackageInfo info = mcContext.getPackageManager().getPackageInfo(
                    mcContext.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("KeyHash......." + Base64.encodeToString(md.digest(), Base64.DEFAULT));

            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public static void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(
                view.getApplicationWindowToken(),
                InputMethodManager.SHOW_FORCED, 0);
    }

    public static String roundTwoDigits(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    @SuppressLint("SetTextI18n")
    public static void getLocationDistance(TextView distance, double lat1, double lon1, double lat2, double lon2) {
        double distance1;
        Location locationA = new Location("");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        distance1 = locationA.distanceTo(locationB);   //in meters
        Double d;
        if (distance1 < 1000) {
            d = Double.parseDouble(new DecimalFormat("00.00").format(distance1));
        } else {
            distance1 = locationA.distanceTo(locationB);   //in miles
            d = Double.parseDouble(new DecimalFormat("00.00").format(distance1));
        }
        distance.setText(Utilities.roundTwoDigits(d / Utilities.MILE));
    }

    public static String getLocationDistance(double lat1, double lon1, double lat2, double lon2) {
        double distance1;
        Location locationA = new Location("");
        locationA.setLatitude(lat1);
        locationA.setLongitude(lon1);
        Location locationB = new Location("");
        locationB.setLatitude(lat2);
        locationB.setLongitude(lon2);
        distance1 = locationA.distanceTo(locationB);   //in meters
        Double d;
        if (distance1 < 1000) {
            d = Double.parseDouble(new DecimalFormat("00.00").format(distance1));
        } else {
            distance1 = locationA.distanceTo(locationB);   //in miles
            d = Double.parseDouble(new DecimalFormat("00.00").format(distance1));
        }
        return Utilities.roundTwoDigits(d / Utilities.MILE);
    }

    public static void call(Context mContext, String phone) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        mContext.startActivity(intent);
    }

    public static int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;  //solid   app_icon
    }

    public static String capitalizedWord(String name) {
        StringBuilder sb = new StringBuilder(name);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    public static String everyFirstLetterOfWordCap(String string) {
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i] == '.' || chars[i] == '\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    public final static double AVERAGE_RADIUS_OF_EARTH_KM = 6371;

    public static double calculateDistanceInKM(double userLat, double userLng, double venueLat, double venueLng) {
        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return c;
    }

    public static double calculateDistanceInMiles(double userLat, double userLng, double venueLat, double venueLng) {
        double latDistance = Math.toRadians(userLat - venueLat);
        double lngDistance = Math.toRadians(userLng - venueLng);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(userLat)) * Math.cos(Math.toRadians(venueLat))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double miles = convertKMtoMiles(AVERAGE_RADIUS_OF_EARTH_KM * c);
        return miles;
    }

    public static double convertKMtoMiles(double round) {
        return round / 1.6;
    }

    public static double convertMilesToKM(double round) {
        return round * 1.6;
    }

    public static boolean isInternetAvailable(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            assert cm != null;
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        } else
            return false;
    }

    public static String convertBitmapToBase64(Bitmap m) throws Exception {
        Bitmap bm = ScaleBitmap(m, m.getWidth(), m.getHeight());
//        Bitmap bm = ScaleBitmap(m, 300, 75);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bao);
        byte[] ba = bao.toByteArray();
        return Base64.encodeToString(ba, Base64.URL_SAFE);
    }

    public static String convertBitmapToHalfBase64(Bitmap m) throws Exception {
        int w = m.getWidth();
        int h = m.getHeight();
        Bitmap bm = ScaleBitmap(m, w, h);
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, bao);
        byte[] ba = bao.toByteArray();
        return Base64.encodeToString(ba, Base64.URL_SAFE);
    }

    public static Bitmap ScaleDownBitmap(Bitmap originalImage, float maxImageSize, boolean filter) {
        float ratio = Math.min((float) maxImageSize / originalImage.getWidth(), (float) maxImageSize / originalImage.getHeight());
        int width = (int) Math.round(ratio * (float) originalImage.getWidth());
        int height = (int) Math.round(ratio * (float) originalImage.getHeight());

        return Bitmap.createScaledBitmap(originalImage, width, height, filter);
    }

    public static Bitmap ScaleBitmap(Bitmap originalImage, int wantedWidth, int wantedHeight) {
        Bitmap output = Bitmap.createBitmap(wantedWidth, wantedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Matrix m = new Matrix();
        m.setScale((float) wantedWidth / originalImage.getWidth(), (float) wantedHeight / originalImage.getHeight());
        canvas.drawBitmap(originalImage, m, new Paint());
        return output;
    }

    public static String getVersionCodeName(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return pInfo.versionName;
    }

    public static int getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        return pInfo.versionCode;
    }

    public static String getUUID() {
        return UUID.randomUUID().toString();
    }

    public static double roundToFourDigit(double value) {
        double digits = 1000000.0;
        double value1 = Math.round(value * digits);
        return value1 / digits;
    }

    public static void showInternetDialog(final Context context) {
        alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(context.getString(R.string.internet_not_enabled))
                .setCancelable(false)
                .setPositiveButton(R.string.enable_internet,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_WIFI_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                                dialog.cancel();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        alert = alertDialogBuilder.create();
        alert.show();
    }

    public static void showLocationDialog(final Context context) {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage(R.string.gps_disable_in_your_device)
                .setCancelable(false)
                .setPositiveButton(R.string.enable_gps,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                context.startActivity(callGPSSettingIntent);
                                dialog.cancel();
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }


    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static float pxFromDp(final Context context, final float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) throws Exception {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    // slide the view from its current position to below itself
    public void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                -view.getHeight()); // toYDelta
        animate.setDuration(500);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public static void showToast(Context context, String txt, String type) {
        Typeface typeface = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/roboto_light.ttf");
//         .setDuration(4000) // Time duration to show
//                .autoHide(true) // Auto hide Sneaker view
//                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT) // Height of the Sneaker layout
//                .setIcon(R.drawable.ic_no_connection, R.color.white, false) // Icon, icon tint color and circular icon view
//                .setTypeface(Typeface.createFromAsset(this.getAssets(), "font/" + fontName)); // Custom font for title and message
//       .setOnSneakerClickListener(this) // Click listener for Sneaker
//                .setOnSneakerDismissListener(this) // Dismiss listener for Sneaker. - Version 1.0.2
//                .setCornerRadius(radius, margin) // Radius and margin for round corner Sneaker. - Version 1.0.2
//                .sneak(R.color.colorAccent) // Sneak with background color
        switch (type) {
            case SUCCESS:
                Sneaker.with((Activity) context) // Activity, Fragment or ViewGroup
                        .setTitle("Success")
                        .setMessage(txt)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setTypeface(typeface)
                        .setCornerRadius(8)
                        .sneakSuccess();
                break;
            case ERROR:
                Sneaker.with((Activity) context) // Activity, Fragment or ViewGroup
                        .setTitle("Error!!")
                        .setMessage(txt)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setTypeface(typeface)
                        .setCornerRadius(8)
                        .sneakError();
                break;
            case WARNING:
                Sneaker.with((Activity) context) // Activity, Fragment or ViewGroup
                        .setTitle("Warning!!")
                        .setMessage(txt)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setTypeface(typeface)
                        .setCornerRadius(8)
                        .sneakWarning();
                break;
            case INFO:
            case NORMAL:
                Sneaker.with((Activity) context) // Activity, Fragment or ViewGroup
                        .setTitle("Info")
                        .setMessage(txt)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setTypeface(typeface)
                        .setCornerRadius(8)
                        .sneak(R.color.colorPrimary);
                break;
        }
    }

    public static void showToast(ViewGroup viewGroup, Context context, String txt, String type) {
        Typeface typeface = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/open_sans_regular.ttf");
        switch (type) {
            case SUCCESS:
                Sneaker.with(viewGroup) // Activity, Fragment or ViewGroup
                        .setTitle("Success")
                        .setMessage(txt)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setTypeface(typeface)
                        .setCornerRadius(8)
                        .sneakSuccess();
                break;
            case ERROR:
                Sneaker.with(viewGroup) // Activity, Fragment or ViewGroup
                        .setTitle("Error!!")
                        .setMessage(txt)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setTypeface(typeface)
                        .setCornerRadius(8)
                        .sneakError();
                break;
            case WARNING:
                Sneaker.with(viewGroup) // Activity, Fragment or ViewGroup
                        .setTitle("Warning!!")
                        .setMessage(txt)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setTypeface(typeface)
                        .setCornerRadius(8)
                        .sneakWarning();
                break;
            case INFO:
            case NORMAL:
                Sneaker.with(viewGroup) // Activity, Fragment or ViewGroup
                        .setTitle("Info")
                        .setMessage(txt)
                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                        .setTypeface(typeface)
                        .setCornerRadius(8)
                        .sneak(R.color.colorPrimary);
                break;
        }
    }

    public static void showToast(Activity viewGroup, Context context, String txt, String type) {
        LayoutInflater inflater = viewGroup.getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_custom_toast, viewGroup.findViewById(R.id.toast_layout_root));
        TextView text = layout.findViewById(R.id.title);
        text.setText(txt);
        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();


//        Typeface typeface = Typeface.createFromAsset(context.getResources().getAssets(), "fonts/open_sans_regular.ttf");
//        switch (type) {
//            case SUCCESS:
//                Sneaker.with(viewGroup) // Activity, Fragment or ViewGroup
//                        .setTitle("Success")
//                        .setMessage(txt)
//                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
//                        .setTypeface(typeface)
//                        .setCornerRadius(8)
//                        .sneakSuccess();
//                break;
//            case ERROR:
//                Sneaker.with(viewGroup) // Activity, Fragment or ViewGroup
//                        .setTitle("Error!!")
//                        .setMessage(txt)
//                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
//                        .setTypeface(typeface)
//                        .setCornerRadius(8)
//                        .sneakError();
//                break;
//            case WARNING:
//                Sneaker.with(viewGroup) // Activity, Fragment or ViewGroup
//                        .setTitle("Warning!!")
//                        .setMessage(txt)
//                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
//                        .setTypeface(typeface)
//                        .setCornerRadius(8)
//                        .sneakWarning();
//                break;
//            case INFO:
//            case NORMAL:
//                Sneaker.with(viewGroup) // Activity, Fragment or ViewGroup
//                        .setTitle("Info")
//                        .setMessage(txt)
//                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
//                        .setTypeface(typeface)
//                        .setCornerRadius(8)
//                        .sneak(R.color.colorPrimary);
//                break;
//        }
    }

    public static boolean isStringOnlyAlphabet(String str) {
        return ((!str.equals(""))
                && (str != null)
                && (str.matches("^[a-zA-Z]*$")));
    }

    public static boolean isStringOnlyNumbers(String str) {
        return ((!str.equals(""))
                && (str != null)
                && (str.matches("^[0-9+]*$")));
    }

    public static String getUserAge(ParseUser parseUser) {
        String age = "";
        if (parseUser != null) {
            List<UserAnswer> userAnswers = parseUser.getList(PARAM_USER_MANDATORY_ARRAY);
            if (userAnswers != null && userAnswers.size() > 0) {
                for (int i = 0; i < userAnswers.size(); i++) {
                    if (userAnswers.get(i).getQuestionId().getShortTitle().equals(SHORT_TITLE_AGE)) {
                        age = userAnswers.get(i).getOptionsObjArray().get(0).getOptionTitle();
                    }
                }
            }
        }
        return age;
    }

    public static String getUserNationality(ParseUser parseUser) {
        String nationality = "";
        if (parseUser != null) {
            List<UserAnswer> userAnswers = parseUser.getList(PARAM_USER_MANDATORY_ARRAY);
            if (userAnswers != null && userAnswers.size() > 0) {
                for (int i = 0; i < userAnswers.size(); i++) {
                    if (userAnswers.get(i).getQuestionId().getShortTitle().equals(SHORT_TITLE_NATIONALITY)) {
                        nationality = userAnswers.get(i).getOptionsObjArray().get(0).getOptionTitle();
                    }
                }
            }
        }
        return nationality;
    }

    public static String getDistanceBetweenUser(ParseUser parseUser) {
        String distance = "N/A";

        ParseGeoPoint myGeoPoint = ParseUser.getCurrentUser().getParseGeoPoint(PARAM_CURRENT_LOCATION);
        ParseGeoPoint userGeoPoint = parseUser.getParseGeoPoint(PARAM_CURRENT_LOCATION);

        if (myGeoPoint != null && userGeoPoint != null) {
            double dis = myGeoPoint.distanceInKilometersTo(userGeoPoint);
            distance = String.format("%.1f", dis) + " Km";
        }

        return distance;
    }

    public static int getMatchPercentage(ParseUser parseUser) {
        int percent = 0;
        List<QuestionOption> myOptions = new ArrayList<>();
        List<QuestionOption> userOptions = new ArrayList<>();
        try {
            List<UserAnswer> myUserAnswers = ParseUser.getCurrentUser().fetchIfNeeded().getList(PARAM_USER_OPTIONAL_ARRAY);
            if (myUserAnswers != null && myUserAnswers.size() > 0) {
                for (int i = 0; i < myUserAnswers.size(); i++) {
                    if (myUserAnswers.get(i).fetchIfNeeded().getList(PARAM_OPTIONS_OBJ_ARRAY) != null && myUserAnswers.get(i).fetchIfNeeded().getList(PARAM_OPTIONS_OBJ_ARRAY).size() > 0)
                        myOptions.addAll(myUserAnswers.get(i).fetchIfNeeded().getList(PARAM_OPTIONS_OBJ_ARRAY));
                }
            }
        } catch (ParseException exception) {
            exception.printStackTrace();
        }
        List<UserAnswer> otherUserAnswers = parseUser.getList(PARAM_USER_OPTIONAL_ARRAY);
        if (otherUserAnswers != null && otherUserAnswers.size() > 0) {
            for (int i = 0; i < otherUserAnswers.size(); i++) {
                if (otherUserAnswers.get(i).getOptionsObjArray() != null && otherUserAnswers.get(i).getOptionsObjArray().size() > 0)
                    userOptions.addAll(otherUserAnswers.get(i).getOptionsObjArray());
            }
        }
        double count = 0;
        for (int i = 0; i < myOptions.size(); i++) {
            for (int j = 0; j < userOptions.size(); j++) {
                if (myOptions.get(i).getObjectId().equals(userOptions.get(j).getObjectId()))
                    count++;
            }
        }
        if (myOptions.size() > 0) {
            double div = count / myOptions.size();
            double per = div * 100;
            percent = (int) per;
        }
        return percent;
    }

    public static void showGPSDialog(final Activity context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("GPS Connection Error!")
                .setIcon(R.drawable.location_icon)
                .setCancelable(false)
                .setMessage("GPS is not enabled on your device.")
//                        .setNegativeButton("No", (dialogInterface, i) -> {
//                            dialogInterface.cancel();
//                        })
                .setPositiveButton("Enable GPS", (dialogInterface, i) -> {
                    dialogInterface.cancel();
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivityForResult(intent, 1234);
                }).show();
    }
}