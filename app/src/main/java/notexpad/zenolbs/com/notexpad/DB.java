package notexpad.zenolbs.com.notexpad;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
-----------------------------------------------------------------------------------
Простой базы данных заметок помощник доступа класса. Определяет основные операции CRUD
  * Для примера блокнота, и дает возможность перечислить все заметки, а также
  * Восстановить или изменить определенный сведению.
  *
  * Это была улучшена от первой версии этого руководства по
  * добавление обработки ошибок лучше, а также с помощью возвращения курсора, а не
  * использования коллекцию внутренних классов (которая является менее масштабируемым и не
  рекомендуется *).
  * /

/ * Это не оригинальный код, который блокнот упражнения предусмотрено.
  *
  * Я сделал некоторые редактирования на этот код, добавив параметр даты на нем.
от Атора
  * /


 */

public class DB {

    public static final String FIELD_ROWID = "_id";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_BODY = "body";
    public static final String FIELD_DATE = "date";


    private static final String L_TAG = "DATABASE";
    private SelfDBHelper selfDbHelper; //класс, являющийся наследником для SQLiteOpenHelper

  /*
   * Для управления базой данных SQLite существует класс SQLiteDatabase.
   * В классе SQLiteDatabase определены методы query(), insert(), delete() и update() для чтения,
   *  добавления, удаления, изменения данных.
   *  Кроме того, метод execSQL() позволяет выполнять любой допустимый код на языке SQL
   */


    private SQLiteDatabase mDb; //SQLiteDatabase.(stock class) Содержит методы для работы с данными – т.е. вставка, обновление, удаление и чтение.

    /**
     * Database creation sql statement
     База данных SQL заявление создание
     */
    private static final String DATABASE_CREATE =
            "create table notes (_id integer primary key autoincrement, "
                    + "title text not null, body text not null, date text not null);";

    private static final String DATABASE_NAME = "data";
    private static final String DATABASE_TABLE = "notes";
    private static final int DATABASE_VERSION = 1;

    private final Context a_context;
    //====================================== inside class ===============================
    private static class SelfDBHelper extends SQLiteOpenHelper {
        //конструктор
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
//=====================================================================================

    public DB(Context context) {
        this.a_context = context;
    }



    //throws - бросает  аналог исключения try{}cath{}final  -- обработка исключения вызывающим методом
    public DB open() throws SQLException {

        // инициализация конструктором
        selfDbHelper = new SelfDBHelper(a_context); //private DatabaseHelper mDbHelper; //класс, являющийся наследником для SQLiteOpenHelper
        //Метод getWritableDatabase выполняет подключение к базе данных и возвращает нам объект SQLiteDatabase для работы с ней.
        mDb = selfDbHelper.getWritableDatabase(); // private DatabaseHelper mDbHelper; //класс, являющийся наследником для SQLiteOpenHelper
        return this;
    }

    public void close() {
        selfDbHelper.close();
    }


    //----------------------------------------------------------------------------------

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
    	/*
    	 * distinct -	true if you want each row to be unique, false otherwise.
           table	 - The table name to compile the query against.

    	columns – список полей, которые мы хотим получить

    	selection – строка условия WHERE
    	selectionArgs – массив аргументов для selection. В selection можно использовать знаки ? , а которые будут заменены этими значениями.
    	groupBy - группировка
    	having – использование условий для агрегатных функций
    	orderBy - сортировка

    	*/
        //distinct,table,columns ,selection ,selectionArgs,  groupBy, having, orderBy

        //String orderBy = null;
        //String orderBy = "DESC";
        //String orderBy = "ASC";
        //String orderBy = "KEY_ROWID = DESC";
        String orderBy = FIELD_ROWID + " DESC";


        return mDb.query(DATABASE_TABLE,
                new String[] {FIELD_ROWID, FIELD_TITLE, FIELD_BODY,FIELD_DATE},
                null, null, null, null,
                orderBy);
    }

//--------------------------------------------------------------------------------
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

    //----------------------------------------------------------------------------------

    public boolean updateNote(long rowId, String title, String body,String date) {
        ContentValues args = new ContentValues();
        args.put(FIELD_TITLE, title);
        args.put(FIELD_BODY, body);

        //This lines is added for personal reason
        //Этой линии добавляется по личным причинам
        args.put(FIELD_DATE, date);

        //One more parameter is added for data
        //Еще один параметр добавляется для данных
        return mDb.update(DATABASE_TABLE, args, FIELD_ROWID + "=" + rowId, null) > 0;
    }


}
