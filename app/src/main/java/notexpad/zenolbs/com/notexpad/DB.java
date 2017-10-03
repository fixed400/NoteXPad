package notexpad.zenolbs.com.notexpad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {

    public static final String FIELD_ROWID = "_id";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_BODY = "body";
    public static final String FIELD_DATE = "date";


    private static final String L_TAG = "DATABASE";
    private SelfDBHelper selfDbHelper;


    private SQLiteDatabase mDb;

    private static final String DATABASE_CREATE =
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null, body text not null, date text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 1;

    private final Context a_context;

    private static class SelfDBHelper extends SQLiteOpenHelper {

        SelfDBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(L_TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will deleted all deprecate data");
            db.execSQL("DROP TABLE IF EXISTS notes");
            onCreate(db);
        }
    }

    public DB(Context context) {
        this.a_context = context;
    }
    public DB open() throws SQLException {


        selfDbHelper = new SelfDBHelper(a_context); //private DatabaseHelper mDbHelper; //класс, являющийся наследником для SQLiteOpenHelper
        mDb = selfDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        selfDbHelper.close();
    }

    public long createNote(String title, String body, String date) {
        ContentValues initialValues = new ContentValues();
        initialValues.put(FIELD_TITLE, title); // положить
        initialValues.put(FIELD_BODY, body);
        initialValues.put(FIELD_DATE, date);

        return mDb.insert(DATABASE_TABLE, null, initialValues); //ставить в таблицу
    }

    public boolean deleteNote(long rowId) {

        return mDb.delete(DATABASE_TABLE, FIELD_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor getAllContents() {

        String orderBy = FIELD_ROWID + " DESC";
        return mDb.query(DATABASE_TABLE,
                new String[] {FIELD_ROWID, FIELD_TITLE, FIELD_BODY,FIELD_DATE},
                null, null, null, null,
                orderBy);
    }

    public Cursor fetchByName(String inputText) throws SQLException {
        Log.w(L_TAG, inputText);
        Cursor mCursor = null;
        if (inputText == null  ||  inputText.length () == 0)  {
            mCursor = mDb.query(DATABASE_TABLE, new String[] {FIELD_ROWID,
                            FIELD_TITLE, FIELD_BODY, FIELD_DATE},
                    null, null, null, null, null);

        }else {
            mCursor = mDb.query(true, DATABASE_TABLE, new String[] {FIELD_ROWID,
                            FIELD_TITLE, FIELD_BODY, FIELD_DATE},
                    FIELD_TITLE + " like '%" + inputText + "%'", null,
                    null, null, null, null);
        }
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getOne(long rowId) throws SQLException {

        Cursor _idCursor = mDb.query(true,
                DATABASE_TABLE,
                new String[] {FIELD_ROWID, FIELD_TITLE, FIELD_BODY,FIELD_DATE},
                FIELD_ROWID + "=" + rowId,
                null, null, null, null, null);
        if (_idCursor != null) {
            _idCursor.moveToFirst();
        }
        return _idCursor;

    }

    public boolean updateNote(long rowId, String title, String body,String date) {
        ContentValues args = new ContentValues();
        args.put(FIELD_TITLE, title);
        args.put(FIELD_BODY, body);

        args.put(FIELD_DATE, date);

        return mDb.update(DATABASE_TABLE, args, FIELD_ROWID + "=" + rowId, null) > 0;
    }


}
