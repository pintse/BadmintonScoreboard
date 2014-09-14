package idv.ryan.badmintonscoreboard;

//0.9.2 修正
//1.點左邊RadioButton導致右邊分數黑色看不見的問題
//2.reset時，提示使用者確認

//TODO  程式失去焦點時，資料保存


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.LinkedHashMap;

public class Scoreboard extends Activity {
    private Button mLeftPlusBtn,mRightPlusBtn,mLeftMinusBtn,mRightMinusBtn,mResetBtn;
    private RadioButton mLeftRadioBtn, mRightRadioBtn;
    private TextView mLeftScordTextView,mRightScordTextView;
    //private RadioGroup mLeftRadioGroup, mRightRadioGroup;
    private EditText mLeftEditText, mRightEditText;
    private CheckBox mLeftCheckBox, mRightCheckBox;

    private Vibrator mVibrator;

    protected static final int MENU_ABOUT = Menu.FIRST;
    protected static final int MENU_Quit = Menu.FIRST+1;

    private static final long VIBRATOR_TIME = 50;

    private LinkedHashMap<String, String> lastStatusRecords= new LinkedHashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard);
        mLeftPlusBtn = (Button) findViewById(R.id.leftPlusBtn);
        mRightPlusBtn = (Button) findViewById(R.id.rightPlusBtn);
        mLeftMinusBtn = (Button) findViewById(R.id.leftMinusBtn);
        mRightMinusBtn = (Button) findViewById(R.id.rightMinusBtn);
        mResetBtn = (Button) findViewById(R.id.resetBtn);

        mLeftRadioBtn = (RadioButton) findViewById(R.id.leftRadioBtn);
        mRightRadioBtn = (RadioButton) findViewById(R.id.rightRadioBtn);

        mLeftScordTextView = (TextView) findViewById(R.id.leftScoreTextView);
        mRightScordTextView = (TextView) findViewById(R.id.rightScoreTextView);

        mLeftEditText = (EditText) findViewById(R.id.leftEditText);
        mRightEditText = (EditText) findViewById(R.id.rightEditText);

        mLeftCheckBox = (CheckBox) findViewById(R.id.leftCheckBox);
        mRightCheckBox = (CheckBox) findViewById(R.id.rightCheckBox);

        mVibrator = (Vibrator) getApplication().getSystemService(VIBRATOR_SERVICE);

        mLeftCheckBox.setChecked(true);
        mRightCheckBox.setChecked(true);

        operatorSetup();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private void operatorSetup(){
        mLeftPlusBtn.setOnClickListener( new Button.OnClickListener(){
            public void onClick(View v){

                recordNowStatus();

                mLeftScordTextView.setText((1 + Integer.parseInt( mLeftScordTextView.getText().toString()))+"");

                if (mLeftRadioBtn.isChecked() || (!mLeftRadioBtn.isChecked() && !mRightRadioBtn.isChecked())){  //原本就是左邊發球時，處理換位住記
                    if (mLeftCheckBox.isChecked())
                        mLeftCheckBox.setChecked(false);
                    else
                        mLeftCheckBox.setChecked(true);
                }

                mLeftScordTextView.setTextColor(Color.RED);
                mRightScordTextView.setTextColor(Color.WHITE);

                mLeftRadioBtn.setChecked(true);
                mRightRadioBtn.setChecked(false);



                mVibrator.vibrate(VIBRATOR_TIME);
            }
        } );

        mRightPlusBtn.setOnClickListener( new Button.OnClickListener(){
            public void onClick(View v){

                recordNowStatus();

                mRightScordTextView.setText((1 + Integer.parseInt( mRightScordTextView.getText().toString()))+"");

                if (mRightRadioBtn.isChecked() || (!mLeftRadioBtn.isChecked() && !mRightRadioBtn.isChecked())){  //原本就是左邊發球時，處理換位住記
                    if (mRightCheckBox.isChecked()){
                        mRightCheckBox.setChecked(false);
                    }else{
                        mRightCheckBox.setChecked(true);
                    }
                }

                mRightScordTextView.setTextColor(Color.RED);
                mLeftScordTextView.setTextColor(Color.WHITE);
                mLeftRadioBtn.setChecked(false);
                mRightRadioBtn.setChecked(true);

                mVibrator.vibrate(VIBRATOR_TIME);

            }
        } );

        mLeftMinusBtn.setOnClickListener( new Button.OnClickListener(){
            public void onClick(View v){
                if (mLeftScordTextView.getText().toString().equals("0")){
                    return;
                }
                mLeftScordTextView.setText((Integer.parseInt( mLeftScordTextView.getText().toString()) - 1)+"");

                rollbackStatus();

                recordNowStatus();

                mVibrator.vibrate(VIBRATOR_TIME);
            }
        } );

        mRightMinusBtn.setOnClickListener( new Button.OnClickListener(){
            public void onClick(View v){
                if (mRightScordTextView.getText().toString().equals("0")){
                    return;
                }
                mRightScordTextView.setText((Integer.parseInt( mRightScordTextView.getText().toString()) - 1)+"");

                rollbackStatus();

                recordNowStatus();

                mVibrator.vibrate(VIBRATOR_TIME);
            }
        } );

        mResetBtn.setOnClickListener( new Button.OnClickListener(){
            public void onClick(View v){
                mVibrator.vibrate(VIBRATOR_TIME);
                new AlertDialog.Builder(Scoreboard.this).setTitle("Confirm")
                        .setMessage("Reset ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mLeftScordTextView.setText("0");
                                    mRightScordTextView.setText("0");
                                    mLeftEditText.setText("");
                                    mRightEditText.setText("");
                                    mLeftRadioBtn.setChecked(false);
                                    mRightRadioBtn.setChecked(false);
                                    mRightScordTextView.setTextColor(Color.WHITE);
                                    mLeftScordTextView.setTextColor(Color.WHITE);
                                    mRightCheckBox.setChecked(true);
                                    mLeftCheckBox.setChecked(true);

                                    lastStatusRecords.clear();
                                    }
                            })

                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                         }).show();

            }
        } );

        mLeftRadioBtn.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v){
                mRightRadioBtn.setChecked(false);
                mLeftScordTextView.setTextColor(Color.RED);
                mRightScordTextView.setTextColor(Color.WHITE);

                mVibrator.vibrate(VIBRATOR_TIME);
            }
        } );

        mRightRadioBtn.setOnClickListener( new View.OnClickListener(){
            public void onClick(View v){
                mLeftRadioBtn.setChecked(false);
                mRightScordTextView.setTextColor(Color.RED);
                mLeftScordTextView.setTextColor(Color.WHITE);

                mVibrator.vibrate(VIBRATOR_TIME);
            }
        } );

        mLeftCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVibrator.vibrate(VIBRATOR_TIME);
            }
        });

        mRightCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVibrator.vibrate(VIBRATOR_TIME);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scoreboard, menu);
        super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_ABOUT, 0, "About...");
        menu.add(0, MENU_Quit, 0, "Exit App");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item)
     {
            super.onOptionsItemSelected(item);
            switch(item.getItemId()){
                case MENU_ABOUT:
                    new AlertDialog.Builder(Scoreboard.this).setTitle("About")
                            .setMessage("The Badminton Scoreboard Lite App Beta v0.9.2 © 2013 Chen Pintse")
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            }).show();
                    break;
                case MENU_Quit:
                    finish();
                    break;
            }
            return true;
     }

    private void recordNowStatus(){
        lastStatusRecords.put("LeftRadio",mLeftRadioBtn.isChecked()?"true":"false");
        lastStatusRecords.put("RightRadio",mRightRadioBtn.isChecked()?"true":"false");
        lastStatusRecords.put("LeftScord",mLeftScordTextView.getText().toString());
        lastStatusRecords.put("RightScord",mRightScordTextView.getText().toString());
        lastStatusRecords.put("LeftEdit",mLeftEditText.getText().toString());
        lastStatusRecords.put("RightEdit",mRightEditText.getText().toString());
        lastStatusRecords.put("LeftCheck",mLeftCheckBox.isChecked()?"true":"false");
        lastStatusRecords.put("RightCheck",mRightCheckBox.isChecked()?"true":"false");

    }

    private void rollbackStatus(){
        if (!lastStatusRecords.isEmpty()){
            mLeftRadioBtn.setChecked(lastStatusRecords.get("LeftRadio").equals("true")?true:false);
            mRightRadioBtn.setChecked(lastStatusRecords.get("RightRadio").equals("true")?true:false);
            mLeftCheckBox.setChecked(lastStatusRecords.get("LeftCheck").equals("true")?true:false);
            mRightCheckBox.setChecked(lastStatusRecords.get("RightCheck").equals("true")?true:false);
            mLeftScordTextView.setTextColor(lastStatusRecords.get("LeftRadio").equals("true")?Color.RED:Color.WHITE);
            mRightScordTextView.setTextColor(lastStatusRecords.get("RightRadio").equals("true")?Color.RED:Color.WHITE);
        }

        lastStatusRecords.clear();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d("Ryan","onStart");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d("Ryan","onRestart");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d("Ryan","onResume");
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d("Ryan","onPause");
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d("Ryan","onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("Ryan","onDestroy");
    }
}
