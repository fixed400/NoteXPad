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

public class ContentsListActivity extends ListActivity {

    private static String LOG_TAG = "ContentsListActivity";



    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;
    private static final int DELETE_ID = Menu.FIRST;
    public static TextView text1;

    private DB workDbHelper;

    LanguageTranslate cur_l = new LanguageTranslate() ;
    SimpleCursorAdapter simpleCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.contents_list);

        View inflatedView = getLayoutInflater().inflate(R.layout.title_item, null);
        text1 = (TextView) inflatedView.findViewById(R.id.text1);

        workDbHelper = new DB(this);
        workDbHelper.open();

        registerForContextMenu(getListView());
        Button add_note = (Button)findViewById(R.id.add_note_btn);
        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNote();
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        getNote();
    }
    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_notes_list, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:

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
        startActivityForResult(item_note, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

            Intent i = new Intent(this, NoteEditor.class);
            i.putExtra(DB.FIELD_ROWID, id);
            Log.d("ContentList_LOG","DB.FIELD_ROWID == "+ DB.FIELD_ROWID+" id == "+id);
            startActivityForResult(i, ACTIVITY_EDIT);

    }

    private void getNote() {

        Cursor notesCursor = workDbHelper.getAllContents();
        startManagingCursor(notesCursor);

        String[] from = new String[]{DB.FIELD_TITLE, DB.FIELD_DATE};
        int[] to = new int[]{R.id.text1, R.id.date_row};

         if(PreferencesActivity.isBigText){
             simpleCursorAdapter =
                    new SimpleCursorAdapter(this, R.layout.title_item2, notesCursor, from, to);
            setListAdapter(simpleCursorAdapter);
         }else{
             simpleCursorAdapter =
                    new SimpleCursorAdapter(this, R.layout.title_item, notesCursor, from, to);
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
                    getNote();
                }else {
                    simpleCursorAdapter.getFilter().filter(s.toString());
                }
            }

        });

        simpleCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {

            public Cursor runQuery(CharSequence constraint) {
                return workDbHelper.fetchByName(constraint.toString());
            }

        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                workDbHelper.deleteNote(info.id);
                getNote();

                return true;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Log.e(LOG_TAG, " onActivityResult "+requestCode+" , "+resultCode);

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

        if(workDbHelper!=null) workDbHelper.close();


    }

}

