package notexpad.zenolbs.com.notexpad;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import notexpad.zenolbs.com.notexpad.settings.PreferencesActivity;
import notexpad.zenolbs.com.notexpad.util.LockActivity;
/*
ListActivity расширяет Activity, чтобы упростить использование списков.
Например, у вас уже будет предопределенный метод, обрабатывающий нажатие на элемент списка
*/

/* TO DO

В самой заметке можно поставить пароль
если это так
то прредложет установить пароль в диалоговом окне
после чего пароль сохранится в базе данных или файле или sharePreferences
а в списке заметок в на данной заметке появится значек замка

при нажатии на данную заметку появится диалоговое окно с запросом пароля
если пароль правельный отобразит заметку

++ можно дополнительно включить шифрование Заметок для SqlLite - просто шифрует БД
 */

//!!! ПРОВЕРИТЬ ЧТО БУДЕТ ЕСЛИ НЕТУ ЗАМЕТОК!!!!

//!!! если делать создать пароль для  заметок то оно будет работать по  id
///для етого нужно добавить еще одно поле в БД или Collection - но это плохое решение лучше в БД!!!

// пофиксить работу с базой данных - применить после запросов к БД dbHelper.close();
public class ContentsListActivity extends ListActivity {

    private static String LOG_TAG = "ContentsListActivity";

    int version_sdk = Build.VERSION.SDK_INT;

    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int DELETE_ID = Menu.FIRST;
    static SharedPreferences mySharedPreferences;
    public static TextView text1;

    public static boolean textThick ;
    public static String listChooserItem;
    public static String listChooserItem2;


    private int mNoteNumber = 1;
    public Context context;

    private DB workDbHelper; //class project

    //String lv = getResources().getConfiguration().locale.getLanguage();
    LanguageTranslate cur_l = new LanguageTranslate() ;


    private EditText result;
    private TextView resultText;
    //final EditText userinput = new EditText(context);
      //      builder.setView(userinput);
    //String inputValue="123"; // in not dife wiil be null



    // Search EditText
    EditText inputSearch;

    SimpleCursorAdapter simpleCursorAdapter;

   // private DB dbHelper;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contents_list);//xml

       // EditText myFilter = (EditText) findViewById(R.id.searchFilter);

       // text1 = (TextView) findViewById(R.id.text1);
       // text1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
       // text1.setTextColor(Color.GREEN);     // on a null object reference    потому что не загржен непосредственно этот под макет
        //который используется адаптером
        //------------------------define View layout ------------------
        /*
        If you want to access a view in another layout (not the active layout),
         then you can inflate the layout you want to use and access it that way.
         */
        //
        View inflatedView = getLayoutInflater().inflate(R.layout.title_item, null);
        text1 = (TextView) inflatedView.findViewById(R.id.text1);
       // text1.setText("Hello!");
        //text1.setTextColor(Color.GREEN);
        //text1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
        //-----------------------------------------------------------

        //---------------------------FOR SEARCH--------------------------------
        //dbHelper = new DB(this);
        //dbHelper.open();

        workDbHelper = new DB(this);
        workDbHelper.open();

        //Clean all data

       // dbHelper.deleteAllCountries();

        //Add some data

       // dbHelper.insertSomeCountries();

        //Generate ListView from SQLite Database

       // displayListView();
        //-----------------------------------------------------------

       // context = getApplicationContext();

       // workDbHelper = new DB(this);
       // workDbHelper.open();

       // getNote();	//заполняет список
        // регистрацию меню
        // метод registerForContextMenu. На вход ему передается View и это означает,
        //что для этой View необходимо создавать контекстное меню.
        //Если не выполнить этот метод, контекстное меню для View создаваться не будет.
        registerForContextMenu(getListView()); //Get the activity's list view
        Button add_note = (Button)findViewById(R.id.add_note_btn); //Добавить запись
        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNote(); //вызов метода
            }
        });




        //========load pass
       // new LockActivity().loadText(context);



    }

    @Override
    protected void onResume(){
        super.onResume();

        //----------------Settings
        // text1.setText("Hello!");
        //text1.setTextColor(Color.GREEN);
        //text1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
        Log.d("ContentsList_LOG","-----onResume-----------");
        getNote();

    }
    @Override
    protected void onPause(){
        super.onPause();

        //----------------Settings
        // text1.setText("Hello!");
        //text1.setTextColor(Color.GREEN);
        //text1.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24.f);
       // getNote();
        Log.d("ContentsList_LOG","-----onPause-----------");

    }

    //----------menu---------------
	/*
	onCreateOptionsMenu() метод инициирует первое появление меню на экране
	и принимает в качестве параметра объект Menu (для старых устройств).
	Вы можете сохранить ссылку на меню и использовать ее в любом месте кода,
	 пока метод onCreateOptionsMenu() опять не будет вызван.
	 Вам необходимо всегда использовать реализацию этого обработчика из родительского класса,
	 потому как она при необходимости автоматически включает в меню дополнительные системные пункты.
	 В новых устройствах метод вызывается при создании активности. Метод должен возвращать значение true, чтобы меню было видимым на экране.
	*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notes_list, menu);

        return true;
    }



    //onOptionsItemSelected(). Метод распознает пункт, выбранный пользователем, через MenuItem
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                //----------------------------------------------------------------------------------

                //language
                //languageT();

                AlertDialog.Builder dialog = new AlertDialog.Builder(ContentsListActivity.this);
                dialog.setTitle("About");
                dialog.setMessage( languageLocale() );
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
                startActivity(i_encrypt);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String languageLocale(){

        String lv = getResources().getConfiguration().locale.getLanguage();

        String res;

        if(lv.equals("ru") || lv.equals("ua"))
        {
            res =  cur_l.uLanguageTm() ;
        }else{
            res =  cur_l.wLanguageTm();
        }


        return  res;
    }



    private void createNote() {
        Intent item_note = new Intent(this, NoteEditor.class);
        startActivityForResult(item_note, ACTIVITY_CREATE);  // Для отправки используеться startActivityForResult().
    }

/*
ListView берет содержимое для отображения через адаптер. Адаптер расширяется классом BaseAdapter
и отвечает за модель данных для списка и за расположение этих данных в его элементах

В Андроиде есть два стандартных адаптера: ArrayAdapter и CursorAdapter.
ArrayAdapter управляет данными, основанными на массивах или списках,
*/
//======= ViewHOLDER ===========
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);



            Intent i = new Intent(this, NoteEditor.class);
            i.putExtra(DB.FIELD_ROWID, id);
            Log.d("ContentList_LOG","DB.FIELD_ROWID == "+ DB.FIELD_ROWID+" id == "+id);
            startActivityForResult(i, ACTIVITY_EDIT);



    }





    //заполнение данные
    private void getNote() {
        Log.d("ContentsList_LOG","-----getNote()-----------");
        // Get all of the notes from the database and create the item list
        // рус. Получить все ноты из базы данных и создать список элементов
        Cursor notesCursor = workDbHelper.getAllContents();
        startManagingCursor(notesCursor);


        String[] from = new String[]{DB.FIELD_TITLE, DB.FIELD_DATE};
        int[] to = new int[]{R.id.text1, R.id.date_row};

        // Now create an array adapter and set it to display using our row
        //Теперь создайте адаптер массива и установите его, чтобы отобразить с помощью нашего строку

        //!!! Лучше сделать aдаптер
         if(PreferencesActivity.isBigText){
             Log.d("ContentsList_LOG","-----PreferencesActivity.isBigText-------"+PreferencesActivity.isBigText);
             simpleCursorAdapter =
                    new SimpleCursorAdapter(this, R.layout.title_item2, notesCursor, from, to); //Google рекомендует использовать LoaderManager (api 11)
            setListAdapter(simpleCursorAdapter);
         }else{
             Log.d("ContentsList_LOG","-----PreferencesActivity.isBigText----"+PreferencesActivity.isBigText);
             simpleCursorAdapter =
                    new SimpleCursorAdapter(this, R.layout.title_item, notesCursor, from, to); //Google рекомендует использовать LoaderManager (api 11)
            setListAdapter(simpleCursorAdapter);
        }




        //-------filter-----------------

        final EditText myFilter = (EditText) findViewById(R.id.searchFilter);

        myFilter.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {


            }

            public void beforeTextChanged(CharSequence s,
                                          int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s,
                                      int start, int before, int count) {

                if (count < before) {
                    // We're deleting char so we need to reset the adapter data
                  //  getAllContents();
                    getNote();

                }else {
                    simpleCursorAdapter.getFilter().filter(s.toString());
                }
               // simpleCursorAdapter.getFilter().filter(s.toString());
            }

        });



        simpleCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {

            public Cursor runQuery(CharSequence constraint) {

                return workDbHelper.fetchByName(constraint.toString());

            }

        });



    }

        //------------------------------

    //onCreateContextMenu(). В данный метод можно добавлять пункты меню
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
        //menu.add(0, DELETE_ID, 0, "Удалить запись");

    }

    //This hook is called whenever an item in a context menu is selected.
    //Этот хук вызывается, когда выбран элемент в контекстном меню.
    //Удаление пункта меню
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                workDbHelper.deleteNote(info.id);
                getNote();
              //  workDbHelper.close();


                return true;
        }
        return super.onContextItemSelected(item);
    }

    //результат приходит в метод onActivityResult.   - возврат с  NoteEditor
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.e(LOG_TAG, " onActivityResult "+requestCode+" , "+resultCode);
       // getNote();

    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, " onDestroy()");

        //java.lang.RuntimeException: Unable to resume activity
        // {notexpad.zenolbs.com.notexpad/notexpad.zenolbs.com.notexpad.ContentsListActivity}:
        // java.lang.IllegalStateException: trying to requery an already closed cursor
        // android.database.sqlite.SQLiteCursor@623446e

        if(workDbHelper!=null) workDbHelper.close();
        //if(dbHelper!=null) dbHelper.close();

    }

}

