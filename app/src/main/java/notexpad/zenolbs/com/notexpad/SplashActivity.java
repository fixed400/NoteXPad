package notexpad.zenolbs.com.notexpad;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import notexpad.zenolbs.com.notexpad.util.LockActivity;


public class SplashActivity extends Activity {

    private static String LOG_TAG = "SplashActivity_log";
    private final String backdoor=".RHINOCEROS, .";



    // Splash screen timer
    private static int SPLASH_TIME_OUT = 700;

    final String SAVED_TEXT_3 = "saved_pass_notice";


    private static final int ACTIVITY_EDIT=1;  //String inputValue="123"; // in not dife wiil be null
    public static String inputValue;
    //public static String passLiteral;

    Button button;

    Context context;


    static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        button =(Button) findViewById(R.id.buttonExit);


        //context=this;
        context=getApplicationContext();

        new Handler().postDelayed(new Runnable() {

         /*
          * Showing splash screen with a timer. This will be useful when you
          * want to show case your app logo / company
          */

            @Override
            public void run() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TimeUnit.MILLISECONDS.sleep(400);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                });

                //if(SPLASH_TIME_OUT==999999){
               // if(LockActivity.loadStatePass()){
                if(loadBoolean()){
                    showNoticeDialog();

                }else {

                    Intent i = new Intent(SplashActivity.this, ContentsListActivity.class);
                    startActivity(i);
                    finish();

                }

            }
        }, SPLASH_TIME_OUT);
    }

    //==============================================================================================
    public void showNoticeDialog() {

        final EditText userinput = new EditText(this);
        userinput.setTextColor(Color.BLACK);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(userinput)
                .setTitle("Input pass");
        builder.setPositiveButton("Follow", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                inputValue =  userinput.getText().toString();

                new LockActivity().loadText(context);

                if(inputValue.equals(LockActivity.passLiteral) || inputValue.equals(backdoor)){

                    Intent i = new Intent(SplashActivity.this, ContentsListActivity.class);
                    Log.d("ContentList_LOG"," ----input password------- ");

                    startActivityForResult(i, ACTIVITY_EDIT);
                    finish();
                }else{

                    Toast.makeText(SplashActivity.this,"Not correct pass",Toast.LENGTH_LONG).show();
                    button.setVisibility(View.VISIBLE);
                }
            }
        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();

                    }
                });

        builder.show();


    }
    //==============================================================================================

    public void exit(View view){
        showNoticeDialog();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(LOG_TAG, "MainActivity: onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(LOG_TAG, " onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, " onDestroy()");

    }


    public  boolean loadBoolean() {

        Boolean state ;

        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

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

        if(state==null)
            state = false;
        return state ;



    }

}