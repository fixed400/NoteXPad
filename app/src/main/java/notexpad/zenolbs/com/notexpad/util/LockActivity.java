package notexpad.zenolbs.com.notexpad.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import notexpad.zenolbs.com.notexpad.R;

public class LockActivity extends Activity implements View.OnClickListener {

    SharedPreferences sPref;

    final String SAVED_TEXT = "saved_pass_notice";
    private static String TAG = "LockActivity_log";

    EditText etText;
    TextView textView;
    Button btnSave, btnReset;
    CheckBox checkBox;


    boolean usePass, newPassAccept;


    boolean appearance;
    Boolean display;


    static final String SAVED_TEXT_2 = "saved_pass_notice";


    //static SharedPreferences sharedPreferences;
    private SharedPreferences sharedPreferences;

    //public static SharedPreferences.Editor editor;
    private   SharedPreferences.Editor editor;

    public static String passLiteral;
    //private  String passLiteral;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);


        textView = (TextView) findViewById(R.id.e1);

        etText = (EditText) findViewById(R.id.editTextDialogUserInput);
        btnSave = (Button) findViewById(R.id.button_accept);
        checkBox = (CheckBox) findViewById(R.id.checkBox);


        sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreferences.edit();
         /*
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(SAVED_TEXT, etText.getText().toString());
        ed.commit();
        Toast.makeText(this, "Text saved", Toast.LENGTH_SHORT).show();
        */

        checkBox.setOnClickListener(this);

    }

    @Override
    protected void onResume(){
         super.onResume();

        usePass = loadStatePass();

        if(checkBox.isChecked()||usePass){

            textView.setText("on password");
            etText.setVisibility(View.VISIBLE);
            checkBox.setChecked(true);
            btnSave.setEnabled(true);
        }else{

            textView.setText("off password");
            etText.setVisibility(View.INVISIBLE);
            btnSave.setEnabled(false);

        }

    }



    void saveStatePass(boolean state) {

        editor = sharedPreferences.edit();
        editor.putBoolean("my_pakage",  state);
        editor.commit();
        //or
        //editor.apply();
    }



    public  boolean loadStatePass() {

        Boolean state ;

        try {
            state = sharedPreferences.getBoolean("my_pakage", false);
        } catch (NullPointerException nil){
            nil.getMessage();
            state = null;
        }
        catch (Exception e){
            e.printStackTrace();
            state = null;
        }
        Log.w(TAG,"Text was loaded ");

        if(state==null)
            state = false;
        return state ;
        //==============================================

    }



    // анализируем, какая кнопка была нажата. Всего один метод для всех кнопок
    @Override
    public void onClick(View view){
        usePass = checkBox.isChecked();
            switch(view.getId()) {

                case R.id.checkBox:
                    if (usePass){
                        textView.setText("on password");
                        etText.setVisibility(View.VISIBLE);
                        btnSave.setEnabled(true);
                       // saveStatePass(true); //- задействовать старые пароль (прошлый)
                        Toast.makeText(this, "Пароль обезопасит ваши данные", Toast.LENGTH_SHORT).show();
                    }else {
                        textView.setText("off password");
                        etText.setVisibility(View.INVISIBLE);
                        btnSave.setEnabled(false);
                        saveStatePass(false);

                        Toast.makeText(this, "Свободный доступ к заметкам", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
    }


    public void btnAccept(View view) {

        // попытка  отфильтровать символы
        String a =etText.getText().toString();
        String str =etText.getText().toString();

      //  if (a.trim().replaceAll("\\s+", " ").equalsIgnoreCase(b.trim().replaceAll("\\s+", " "))) {
            // Strings equivalent
      //  }
        str.replaceAll(" ", "");// не работатет

       // String str = a;
       //  str.replaceAll(" ", "");
         //str.replaceAll(" ", "");
        // str.replaceAll("(?U)[\\pP\\s]", "");
        str.toLowerCase(); // не работатет -   разобратся

        String b = str;

        str.trim();

       if(str.equals("")||str.contains(".")||str.contains(",")||str.equals("^\\s+") ){


           if(str.contains(".")||str.contains(",")||str.contains("  ")||str.contains(" ")) {
               Toast.makeText(this, "Not aviable sign!", Toast.LENGTH_SHORT).show();


           }else {
               Toast.makeText(this, "Field must be not empty!", Toast.LENGTH_SHORT).show();
           }

        }
        else {
            saveText();
            newPassAccept =true;
            alertWarning();

        }
    }



    void saveText() {

        Toast.makeText(this, "Pass was saved", Toast.LENGTH_SHORT).show();
        //--------------
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SAVED_TEXT, etText.getText().toString());
        //editor.apply(); // новый способ
        editor.commit();
        saveStatePass(true);
    }


   public  void loadText(Context context) {

       SharedPreferences sharedPreferences = PreferenceManager
               .getDefaultSharedPreferences(context);
       //sharedPreferences  = getPreferences(MODE_PRIVATE);
       passLiteral = sharedPreferences.getString(SAVED_TEXT,"");
       //----------------
       Toast.makeText(context, "Text was loaded", Toast.LENGTH_SHORT).show();
    }


    /*
    // !!! НЕ УДАЛЯТЬ (ЭТО ВТОРОЙ ВАРИАНТ ОБРАБОКИ СОХРАНЕНИЯ ПАРОЛЯ МЕСТО ЯВНОЙ КНОКИ)
    @Override
    public void onBackPressed() {

        if(etText.getText().toString().equals("")) {
            finish();
        }else {

            new AlertDialog.Builder(this)
                    .setTitle("Сохранить Ваш пароль ?")
                    .setMessage(etText.getText().toString())
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface arg0, int arg1) {
                            LockActivity.super.onBackPressed();
                            saveText();
                            newPassAccept =true;
                            finish();
                        }
                    }).create().show();
        }

    }
    */

    @Override
    protected void onDestroy() {
        super.onDestroy();

        passWarning();


    }

    private void passWarning() {
     // установка опции реагирования перед выходом на последующее отображение пароля
        if(!newPassAccept){

            if(loadStatePass()){
                saveStatePass(true);
            }else{
                saveStatePass(false);
            }

            Toast.makeText(this, "!Pass Warning - New password not saved!", Toast.LENGTH_SHORT).show();
        }


    }

    public void  alertWarning() {
        AlertDialog.Builder  mAlertDialog = new AlertDialog.Builder(this);

        mAlertDialog.setTitle("Ваш пароль")
                .setMessage(etText.getText().toString())
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
               .show();


    }



}
