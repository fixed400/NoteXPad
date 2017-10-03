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

public class NoteEditor extends Activity{

    int version_sdk = Build.VERSION.SDK_INT;

    public static String curDate = "";
    public static String curText = "";

    public static EditText mTitleText;
    public static EditText mBodyText;
    public static TextView mDateText;

    private Long mRowId;
    public static Long idNote;


    private Cursor note_item;
    Intent mShareIntent;
    private ShareActionProvider mShareActionProvider;
    static SharedPreferences mySharedPreferences;

    private DB modeDbHelper;



    public Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        modeDbHelper = new DB(this);
        modeDbHelper.open();

        setContentView(R.layout.one_edit_item);
        setTitle(R.string.app_name);

        context = getApplicationContext();

        mTitleText = (EditText) findViewById(R.id.title);
        mTitleText.setTextColor(Color.BLACK);
        mBodyText = (EditText) findViewById(R.id.body);

        String body_share = mBodyText.getText().toString();
        mDateText = (TextView) findViewById(R.id.notelist_date);

        long msTime = System.currentTimeMillis();
        Date curDateTime = new Date(msTime);

        SimpleDateFormat formatter = new SimpleDateFormat("d'/'M'/'y"+" "+"HH:mm");
        curDate = formatter.format(curDateTime);

        mDateText.setText("" + curDate);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(DB.FIELD_ROWID);
        if (mRowId == null) {

            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(DB.FIELD_ROWID)
                    : null;
        }

        fillingTableFields();

        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");

        mShareIntent.putExtra(Intent.EXTRA_TEXT, body_share);

    }//end onCreate


    public static class LineEditText extends EditText{
        public LineEditText(Context context, AttributeSet attrs) {
            super(context, attrs);
            mRect = new Rect();
            mPaint = new Paint();
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(Color.parseColor("#d2e2db"));

        }

        private Rect mRect;
        private Paint mPaint;

        //рисование
        @Override
        protected void onDraw(Canvas canvas) {

            int height = getHeight();
            int line_height = getLineHeight();

            int count = height / line_height;

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

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(DB.FIELD_ROWID, mRowId);

    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillingTableFields();

        loadPref(context);
    }
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

        getMenuInflater().inflate(R.menu.menu_item_edit, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);

        if (version_sdk >= 14) {

            mShareActionProvider = (ShareActionProvider) item.getActionProvider();
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareIntent());
            }
        }
        return true;
    }

    private Intent createShareIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mBodyText.getText().toString());

        return shareIntent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_about:

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

                return true;

            case R.id.menu_settings:
                Intent i = new Intent(this, PreferencesActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_encrypt:
                Intent i_encrypt = new Intent(this, LockActivity.class);
                i_encrypt.putExtra("note_id",idNote);

                startActivity(i_encrypt);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

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


    private void fillingTableFields() {
        if (mRowId != null) {
            note_item = modeDbHelper.getOne(mRowId);
            startManagingCursor(note_item);
            mTitleText.setText(note_item.getString(
                    note_item.getColumnIndexOrThrow(DB.FIELD_TITLE)));
            mBodyText.setText(note_item.getString(
                    note_item.getColumnIndexOrThrow(DB.FIELD_BODY)));
            curText = note_item.getString(
                    note_item.getColumnIndexOrThrow(DB.FIELD_BODY));
            stopManagingCursor(note_item);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("LOG_NoteEditor", " onDestroy()");

    }


}