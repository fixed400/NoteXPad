package notexpad.zenolbs.com.notexpad;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import notexpad.zenolbs.com.notexpad.settings.PreferencesActivity;
import notexpad.zenolbs.com.notexpad.util.LockActivity;

import static notexpad.zenolbs.com.notexpad.settings.PreferencesActivity.loadSizeText;
import static notexpad.zenolbs.com.notexpad.settings.PreferencesActivity.loadStyleText;
import static notexpad.zenolbs.com.notexpad.settings.PreferencesActivity.setColorTxt;
import static notexpad.zenolbs.com.notexpad.settings.PreferencesActivity.setTextBold;

//import android.support.v7.widget.ShareActionProvider;
//import android.support.v7.widget.ShareActionProvider;


//public class NoteEditor extends AppCompatActivity{
public class NoteEditor extends Activity{



    int version_sdk = Build.VERSION.SDK_INT;
    public static int numTitle = 1;
    public static String curDate = "";
    public static String curText = "";

    public static EditText mTitleText;
    public static EditText mBodyText;
    public static TextView mDateText;

    private Long mRowId;
    public static Long idNote;


    private Cursor note_item; //note - заметки
    Intent mShareIntent;
    private ShareActionProvider mShareActionProvider;
    // private EditText edittext = null; //вот это поле

    private DB modeDbHelper;  //Aдаптер объявление

    LanguageTranslate cur_l = new LanguageTranslate() ;


    ///----------------Preferences -- предпочтения
    // performance - производительность
    static SharedPreferences mySharedPreferences;
    private TextView text1;

    public static boolean textThick ;
    public static String listChooserItem;
    public static String listChooserItem2;

    public Context context;

    //задает начальную установку параметров при инициализации активности
    //при реализации этого методов необходимо всегда сначала вызывать версию этого метода из суперкласса
    //В объект Bundle можно записать параметры, динамическое состояние активности как пары имя-значение.
    // savedInstanceState. Сохранение данных Activity при повороте экрана
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        modeDbHelper = new DB(this);////Aдаптер Инициализация объекта ,Контекстом (доступ к ресурсам) может быть сама активность (this)
        modeDbHelper.open();

        setContentView(R.layout.one_edit_item); // установив свой ​​собственный макет вид с setContentView ()
        setTitle(R.string.app_name); // указать название title

        context = getApplicationContext();

        mTitleText = (EditText) findViewById(R.id.title);  //title
        mTitleText.setTextColor(Color.BLACK);
        mBodyText = (EditText) findViewById(R.id.body);     // контетнт
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

        */
        //----------------------------
        /*
        mBodyText.setTextColor(Color.BLUE);
        // https://stackoverflow.com/questions/3203694/custom-fonts-in-android
       // mBodyText.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
       // mBodyText.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD));
       // mBodyText.setTypeface(Typeface.create("sans-serif-light", Typeface.ITALIC));
       // mBodyText.setTypeface(Typeface.create("sans-serif-light", Typeface.BOLD_ITALIC));
        mBodyText.setTypeface(Typeface.create("monospace", Typeface.NORMAL));

        mBodyText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 55.f);
        //mBodyText.setHeight(54);

        */
        String body_share = mBodyText.getText().toString(); //коннтент для bodyshare
        mDateText = (TextView) findViewById(R.id.notelist_date); //дата

        long msTime = System.currentTimeMillis();  //извлечь время
        Date curDateTime = new Date(msTime);       //извлечь дату
        //перевод даты

        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y"+" "+"HH:mm");
        curDate = formatter.format(curDateTime);

        mDateText.setText("" + curDate);

        // id cтроки
        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(DB.FIELD_ROWID);
        if (mRowId == null) {
            //Extras - дополнительно,
            //Retrieves a map of extended data from the intent.
            //рус.Получает карту расширенных данных из намерениях.
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(DB.FIELD_ROWID)
                    : null;
        }

        fillingTableFields(); //  вызов метод для заполнения полей,populate -заселять

        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");

        //mShareIntent.putExtra(Intent.EXTRA_TEXT, "From me to you, this text is new.");

        mShareIntent.putExtra(Intent.EXTRA_TEXT, body_share);

    }//end onCreate

    //--------------------- редактирование
    /*
    Appcompat Custom Widgets. Чтобы поддерживать такие функции, как тонирование,
    библиотека appcompat автоматически загружает специальные приложения appcompat для встроенных виджетов.
     Однако это не работает для ваших собственных пользовательских представлений.
     Вместо того, чтобы напрямую расширять классы android.widget,
     вместо этого вы должны расширить один из классов делегатов в файле android.support.v7.widget.AppCompat.
     */
    public static class LineEditText extends EditText{
        // we need this constructor for LayoutInflater
        // конструктор для LayoutInflater
        //AttributeSet - Сборник атрибутов, как найдено, связанные с меткой в XML документа.
        public LineEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            mRect = new Rect();    //прямоугольник
            mPaint = new Paint();  // краски
            // mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            //  mPaint.setColor(Color.BLACK);
            mPaint.setStyle(Paint.Style.STROKE);
            // mPaint.setColor(Color.BLACK);
            mPaint.setColor(Color.parseColor("#d2e2db"));

        }

        private Rect mRect;
        private Paint mPaint;

        //рисование
        @Override
        protected void onDraw(Canvas canvas) {

            int height = getHeight(); //высота
            int line_height = getLineHeight(); //высота линии

            int count = height / line_height; //расчет количества линий

            if (getLineCount() > count)
                count = getLineCount();

            Rect r = mRect;
            Paint paint = mPaint;
            int baseline = getLineBounds(0, r);

            for (int i = 0; i < count; i++) {

                canvas.drawLine(r.left, baseline + 1, r.right, baseline + 1, paint);
                baseline += getLineHeight();

                super.onDraw(canvas);
            }

        }
    }

    //onSaveInstanceState() - метод  позволяет сохранить данные
    //Параметр метода outState является объектом класса Bundle и позволяет хранить различные типы в формате "ключ-значение".
    //out- из
    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        saveState();// вызов метода сохраниения
        outState.putSerializable(DB.FIELD_ROWID, mRowId);

    }

    // метод onPause(), используемый для сохранения пользовательских настроек активности и подготовиться к прекращению взаимодействия с пользователем.
    //при реализации этого методов необходимо всегда сначала вызывать версию этого метода из суперкласса
    @Override
    protected void onPause() {
        super.onPause();
        saveState(); // вызов метода сохраниения
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillingTableFields();

        //----------------Settings
        loadPref(context);
    }

    //============================= Settings ===============================
    public static void loadPref(Context context) {
        mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        setTextBold( mySharedPreferences );
        loadStyleText(mySharedPreferences);
        loadSizeText(mySharedPreferences );
        setColorTxt(mySharedPreferences);

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_item_edit, menu);
        //-----------------------Share------------------------------
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);

        //!!! Реализоват передачу Share !!!
        if (version_sdk >= 14) {
            // Fetch and store ShareActionProvider
            // @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)

            mShareActionProvider = (ShareActionProvider) item.getActionProvider();
            //.....................................................................
            // Find the MenuItem that we know has the ShareActionProvider
            // MenuItem item = menu.findItem(R.id.menu_item_share);

            // Get its ShareActionProvider
            //mShareActionProvider = (ShareActionProvider) item.getActionProvider();

            // Получение данных в onCreate
            // Connect the dots: give the ShareActionProvider its Share Intent
            if (mShareActionProvider != null) {
                // mShareActionProvider.setShareIntent(mShareIntent);
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }
        //*/
        //-------------------------------------------------------------
        //  // Return true so Android will know we want to display the menu
        return true;

    }

    // Create and return the Share Intent
    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        // this data send as (how)"share message"
        //shareIntent.putExtra(Intent.EXTRA_TEXT, "http://droiddevguide.blogspot.ae/");
        //shareIntent.putExtra(Intent.EXTRA_TEXT, "testing  my  text 2015");
        //shareIntent.putExtra(Intent.EXTRA_TEXT, body_share);
        shareIntent.putExtra(Intent.EXTRA_TEXT, mBodyText.getText().toString());

        //mBodyTextShare = (EditText) findViewById(R.id.body);     // контетнт
        //shareIntent.putExtra(Intent.EXTRA_TEXT, mBodyTextShare.getText().toString());
        return shareIntent;
    }
    //Обработчиком является Activity, а метод зовется onOptionsItemSelected().
    //На вход ему передается пункт меню, который был нажат
		/* начиная с Android 3
			можно добавить атрибут android:onClick в ресурсах меню,
		 и  не нужно будет использовать вызов метода onOptionsItemSelected()
		*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        //------------------------------
        switch (item.getItemId()) {
            case R.id.menu_about:
                //----------------------------------------------------------------------------------

                //language
                //languageT();

                AlertDialog.Builder dialog = new AlertDialog.Builder(NoteEditor.this);
                dialog.setTitle("About");
                dialog.setMessage(new ContentsListActivity().languageLocale());
                dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
                dialog.show();
                //-----------------------------------------------------------------------------------------

                return true;

            case R.id.menu_settings:
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_encrypt:
                Intent i_encrypt = new Intent(this, LockActivity.class);
                // i_encrypt.putExtra(DB.FIELD_ROWID, id);
                i_encrypt.putExtra("note_id",idNote);
                //get
                /*
                  // id cтроки
        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(DB.FIELD_ROWID);
        if (mRowId == null) {
            //Extras - дополнительно,
            //Retrieves a map of extended data from the intent.
            //рус.Получает карту расширенных данных из намерениях.
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(DB.FIELD_ROWID)
                    : null;
        }
                 */
                startActivity(i_encrypt);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    } // END boolean onOptionsItemSelected


/*
    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            // min api 14
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }
*/

    //сохраниение сосотояния
    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if(mRowId == null){
            long id = modeDbHelper.createNote(title, body, curDate);
            if(id > 0){
                mRowId = id;
            }else{
                Log.e("saveState","failed to create note");
            }
        }else{
            if(!modeDbHelper.updateNote(mRowId, title, body, curDate)){
                Log.e("saveState","failed to update note");
            }
        }
    }

    // заполнения полей
    private void fillingTableFields() {
        if (mRowId != null) {
            note_item = modeDbHelper.getOne(mRowId);
            startManagingCursor(note_item); // устаревший метод но рабочий
            mTitleText.setText(note_item.getString(
                    note_item.getColumnIndexOrThrow(DB.FIELD_TITLE)));
            mBodyText.setText(note_item.getString(
                    note_item.getColumnIndexOrThrow(DB.FIELD_BODY)));
            curText = note_item.getString(
                    note_item.getColumnIndexOrThrow(DB.FIELD_BODY));
            stopManagingCursor(note_item);
        }
    }

    public String translate(){

        String lv = getResources().getConfiguration().locale.getLanguage();
        String res;

        if(lv.equals("ru") || lv.equals("ua"))
        {
            res =  cur_l.uLanguageTe() ;
        }else{
            res =  cur_l.wLanguageTe();
        }

        return  res;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LOG_NoteEditor", " onDestroy()");
        /*
        if(note_item!=null){
            note_item.close();
        }
        */

    }


}