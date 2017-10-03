package notexpad.zenolbs.com.notexpad.settings;

/**
 * Created by grd on 8/5/17.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.TypedValue;

import notexpad.zenolbs.com.notexpad.R;

import static notexpad.zenolbs.com.notexpad.NoteEditor.mBodyText;
import static notexpad.zenolbs.com.notexpad.NoteEditor.mTitleText;

public class PreferencesActivity extends PreferenceActivity {


    static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;



    public static boolean textThick ;
    public static String listChooserItem;
    public static String listChooserItem2;

    public static boolean isBigText;

    private static int typeface ;
    private static String style ;

    private static void setStyle(int style) {
        switch (style){
            case 1:
                PreferencesActivity.style = "casual";
                break;
            case 2:
                PreferencesActivity.style = "cursive";
                break;
            case 3:
                PreferencesActivity.style = "sans-serif-light";
                break;
            case 4:
                PreferencesActivity.style = "monospace";
                break;
        }

    }

    private static String getStyle(){
        return PreferencesActivity.style;
    }

    private static void setTypeface(int type){

        if(type == 0){
            typeface = Typeface.NORMAL;
        }else{
            typeface = Typeface.BOLD;
        }

    }

    private static int getTypeface(){

        return typeface;
    }

    static final String SAVED_TEXT = "saved_text";

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            editor = sharedPreferences.edit();
        }

    public static void setTextBold(SharedPreferences sharedPreferences ){

        boolean my_checkbox_preference = sharedPreferences.getBoolean("textThickness", false);

        textThick=my_checkbox_preference;

        if(textThick){

            mBodyText.setTypeface(Typeface.create(getStyle(), Typeface.BOLD));
            mTitleText.setTypeface(Typeface.create(getStyle(), Typeface.BOLD));

            setTypeface(1);
        }else{

            mBodyText.setTypeface(Typeface.create(getStyle(), Typeface.NORMAL));
            mTitleText.setTypeface(Typeface.create(getStyle(), Typeface.NORMAL));
            setTypeface(0);
        }
    }

    public static void loadStyleText(SharedPreferences sharedPreferences) {


        String listValue = sharedPreferences.getString("textFontPref", "не выбрано");

        listChooserItem =listValue;

        switch (listValue){
            case "1" :

                setStyle(1);
                mBodyText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                mTitleText.setTypeface(Typeface.create(getStyle(), getTypeface()));


                break;
            case "2":

                setStyle(2);
                mBodyText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                mTitleText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                break;
            case "3":

                setStyle(3);
                mBodyText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                mTitleText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                break;
            case "4":

                setStyle(4);
                mBodyText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                mTitleText.setTypeface(Typeface.create(getStyle(), getTypeface()));

                break;
        }

    }
    public static void loadSizeText(SharedPreferences sharedPreferences) {

        String listValue = sharedPreferences.getString("textSizePref", "не выбрано");
        listChooserItem2 =listValue;

        switch (listValue){
            case "1" :

                mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10.f);
                mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10.f);
                isBigText=false;

                break;
            case "2":

                mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.f);
                mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14.f);
                isBigText=false;
                break;
            case "3":
                // animId =  R.anim.scaling_descent;
                mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
                mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
                isBigText=false;
                break;
            case "4":

                mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
                mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
                isBigText=true;
                break;
            case "5":

                mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32.f);
                mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
                isBigText=true;
                break;
            case "6":

                mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48.f);
                mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
                isBigText=true;
                break;
        }

    }

    public static void setColorTxt(SharedPreferences sharedPreferences){

        String listValue = sharedPreferences.getString("textColorPref", "не выбрано");

        listChooserItem2 =listValue;

        switch (listValue){
            case "1" :
                mBodyText.setTextColor(Color.BLACK);
                mTitleText.setTextColor(Color.BLACK);
                break;
            case "2":
                mBodyText.setTextColor(Color.RED);
                mTitleText.setTextColor(Color.RED);
                break;
            case "3":
                mBodyText.setTextColor(Color.parseColor("#aa29ae38"));
                mTitleText.setTextColor(Color.parseColor("#aa29ae38"));
                break;
            case "4":
                mBodyText.setTextColor(Color.BLUE);
                mTitleText.setTextColor(Color.BLUE);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

}
