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
//import static notexpad.zenolbs.com.notexpad.NoteEditor.mDateText;

public class PreferencesActivity extends PreferenceActivity {


    static SharedPreferences sharedPreferences;
   public static SharedPreferences.Editor editor;



    public static boolean textThick ;
    public static String listChooserItem;
    public static String listChooserItem2;

    public static boolean isBigText;


    private int mNoteNumber = 1;
    public Context context;


    final String CURSIVE ="cursive";
    final String SANS_SERIF_LIGHT ="sans-serif-light";
    final String MONOSPACE ="monospace";


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

    /*
    int typeface = Typeface.NORMAL;
    int typeface = Typeface.BOLD;
    int typeface = Typeface.ITALIC;
    int typeface = Typeface.BOLD_ITALIC;
    */

    /*
    float size = 10.f;
    float size = 14.f;
    float size = 18.f;
    float size = 24.f;
    */





   //EditText editText = new NoteEditor().mBodyText;


        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

          //  sharedPreferences = getPreferences(MODE_PRIVATE);

            sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            editor = sharedPreferences.edit();


        }


            // https://stackoverflow.com/questions/14343903/what-is-the-equivalent-of-androidfontfamily-sans-serif-light-in-java-code

        /*
        These are the list of default font family used, use any of this by replacing the double quotation string "sans-serif-medium"

        FONT FAMILY                    TTF FILE

        1  casual                      ComingSoon.ttf
        2  cursive                     DancingScript-Regular.ttf
        3  monospace                   DroidSansMono.ttf
        4  sans-serif                  Roboto-Regular.ttf
        5  sans-serif-black            Roboto-Black.ttf
        6  sans-serif-condensed        RobotoCondensed-Regular.ttf
        7  sans-serif-condensed-light  RobotoCondensed-Light.ttf
        8  sans-serif-light            Roboto-Light.ttf
        9  sans-serif-medium           Roboto-Medium.ttf
        10  sans-serif-smallcaps       CarroisGothicSC-Regular.ttf
        11  sans-serif-thin            Roboto-Thin.ttf
        12  serif                      NotoSerif-Regular.ttf
        13  serif-monospace            CutiveMono.ttf


            editText.setTextColor(Color.BLUE);
            // https://stackoverflow.com/questions/3203694/custom-fonts-in-android
            // mBodyText.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            // mBodyText.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
            // mBodyText.setTypeface(Typeface.create("sans-serif-light", Typeface.ITALIC));
            // mBodyText.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD_ITALIC));
            editText.setTypeface(Typeface.create("monospace", Typeface.NORMAL));
            editText.setTypeface(Typeface.create("cursive", Typeface.NORMAL));

            editText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 55.f);
            //mBodyText.setHeight(54);
            */


    //============================
    // try  it https://developer.android.com/training/basics/data-storage/shared-preferences.html?hl=ru
    // - http://www.fandroid.info/sharedpreferences-sohranenie-dannyh-v-postoyannoe-hranilishhe-android/
    // try -  MODE_MULTI_PROCESS — несколько процессов совместно используют один файл SharedPreferences.
    //   Caused by: java.lang.NullPointerException:
    // Attempt to invoke interface method 'android.content.SharedPreferences$Editor android.content.SharedPreferences.edit()' on a null object reference
    // ВНИМАНИЕ! Все модификаторы кроме MODE_PRIVATE в настоящий момент объявлены deprecated и не рекомендуются к использованию в целях безопасности.
   public static void saveStatePass(boolean state) {
       // need init context -  "java.lang.String android.content.Context.getPackageName()"

       //SharedPreferences.Editor editor = sharedPreferences.edit();
        //Editor editor = sharedPreferences.edit();
         editor = sharedPreferences.edit();
        editor.putBoolean(SAVED_TEXT,  state);
        editor.commit();

    }

   public static boolean loadStatePass() {

       boolean state;
       return state  = sharedPreferences.getBoolean(SAVED_TEXT,false);
        //etText.setText(savedText);

    }
    //============================



    public static void setTextBold(SharedPreferences sharedPreferences ){
        //boolean getBoolean(String key, boolean defValue);
        boolean my_checkbox_preference = sharedPreferences.getBoolean("textThickness", false);

        textThick=my_checkbox_preference;

        if(textThick){
            //new NoteEditor().mBodyText.setTypeface(Typeface.create("monospace", Typeface.BOLD));
            mBodyText.setTypeface(Typeface.create(getStyle(), Typeface.BOLD));
            mTitleText.setTypeface(Typeface.create(getStyle(), Typeface.BOLD));

            setTypeface(1);
        }else{
            //new NoteEditor().mBodyText.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            mBodyText.setTypeface(Typeface.create(getStyle(), Typeface.NORMAL));
            mTitleText.setTypeface(Typeface.create(getStyle(), Typeface.NORMAL));
            setTypeface(0);
        }
    }



    public static void loadStyleText(SharedPreferences sharedPreferences) {

        //loadPref();
        String listValue = sharedPreferences.getString("textFontPref", "не выбрано");

        listChooserItem =listValue;

        switch (listValue){
            case "1" :
                /// animId =  R.anim.scaling_increase;
                setStyle(1);
                mBodyText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                mTitleText.setTypeface(Typeface.create(getStyle(), getTypeface()));


                break;
            case "2":
                // animId =  R.anim.scaling_protrusion;
                setStyle(2);
                mBodyText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                mTitleText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                break;
            case "3":
                // animId =  R.anim.scaling_descent;
                setStyle(3);
                mBodyText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                mTitleText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                break;
            case "4":
                // animId =  R.anim.scaling_descent;
                setStyle(4);
                mBodyText.setTypeface(Typeface.create(getStyle(), getTypeface()));
                mTitleText.setTypeface(Typeface.create(getStyle(), getTypeface()));

                break;
        }

    }
    public static void loadSizeText(SharedPreferences sharedPreferences) {

        //loadPref();
        String listValue = sharedPreferences.getString("textSizePref", "не выбрано");

        listChooserItem2 =listValue;

        switch (listValue){
            case "1" :
                // animId =  R.anim.scaling_increase;
                mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10.f);
                mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10.f);
                isBigText=false;

                break;
            case "2":
                // animId =  R.anim.scaling_protrusion;
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
                // animId =  R.anim.scaling_descent;
                mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
                mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
                isBigText=true;
                break;
            case "5":
                // animId =  R.anim.scaling_descent;
                mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32.f);
                mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
                isBigText=true;
                break;
            case "6":
                // animId =  R.anim.scaling_descent;
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
                // animId =  R.anim.scaling_increase;
               // mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10.f);
                mBodyText.setTextColor(Color.BLACK);
                mTitleText.setTextColor(Color.BLACK);

                break;
            case "2":
                // animId =  R.anim.scaling_protrusion;
               // mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18.f);
                mBodyText.setTextColor(Color.RED);
                mTitleText.setTextColor(Color.RED);
                break;
            case "3":
                // animId =  R.anim.scaling_descent;
               // mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
                mBodyText.setTextColor(Color.parseColor("#aa29ae38"));
                mTitleText.setTextColor(Color.parseColor("#aa29ae38"));
                break;
            case "4":
                // animId =  R.anim.scaling_descent;
                //mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 32.f);
                mBodyText.setTextColor(Color.BLUE);
                mTitleText.setTextColor(Color.BLUE);
                break;

        }

    }

    ///============================= Settings ===============================
    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


}
