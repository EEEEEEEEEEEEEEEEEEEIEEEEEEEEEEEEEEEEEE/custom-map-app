package com.ivy.hatfiled.appui;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.preference.DialogPreference;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.view.View.OnClickListener;

import com.ivy.hatfiled.otherui.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class AccountPanel extends ActionBarActivity implements OnClickListener {

    private String InputName = null;
    private String InputPass = null;
    private String InputTest = null;//验证码
    private String InputDbName = null;
    private String SdPath = null;

    private TextView NameText;
    private ImageView LoginSate;
    private Button pGoBack;
    private CircleImageView Header;
    private Button InputPng;
    private LinearLayout AccountInfo;
    private LoginService.MyBinder binder;
    private Intent sIntent = new Intent();

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (LoginService.MyBinder)service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_panel);

        pGoBack = (Button) findViewById(R.id.GoBack);
        Header = (CircleImageView) findViewById(R.id.UHeader);
        NameText = (TextView) findViewById(R.id.UName);
        InputPng = (Button) findViewById(R.id.input);
        LoginSate = (ImageView)findViewById(R.id.loginState);
        AccountInfo = (LinearLayout) findViewById(R.id.accountControl);

        pGoBack.setOnClickListener(this);
        Header.setOnClickListener(this);
        InputPng.setOnClickListener(this);
        AccountInfo.setOnClickListener(this);

        sIntent.setAction("android.intent.action.LOGIN_SERVICE");
        bindService(sIntent, conn, Service.BIND_AUTO_CREATE);
        //启动一个线程监视登录状态
        new Thread(){
            @Override
            public void run(){
                if(binder!=null)
                    if (binder.GetLoginState()=="offline") {
                        NameText.setText("未登录");
                        LoginSate.setImageResource(R.drawable.offline);
                        Header.setImageResource(R.drawable.headernullgray);
                    } else {
                        NameText.setText(binder.GetName());
                        Header.setImageResource(R.drawable.headernullblue);
                        switch (binder.GetLoginState()) {
                            case "online":
                                LoginSate.setImageResource(R.drawable.online);
                                break;
                            case "busy":
                                LoginSate.setImageResource(R.drawable.busy);
                                break;
                        }
                    }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(pGoBack)) {
            onBackPressed();
            return;
            //  ComponentName pComp = new ComponentName(AccountPanel.this, NewHatMap.class);
            //  Intent pIntent = new Intent();
            //  pIntent.setComponent(pComp);
            //  startActivity(pIntent);
        } else if (v.equals(Header)) {
            //更换头像对话框......
            return;
        } else if (v.equals(InputPng)) {
            DbNameView(v);
            return;
        } else if (v.equals(AccountInfo)) {
            if (binder.GetLoginState()!="offline") {
                new AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("确定退出登录？")
                        .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                binder.SetData("offline",null,null);
                                NameText.setText("未登录");
                                LoginSate.setImageResource(R.drawable.offline);
                                Header.setImageResource(R.drawable.headernullgray);
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .create()
                        .show();
            }else{
                loginView(v);
            }
        }
    }

    @Override
    public  void onDestroy(){
        super.onDestroy();
        unbindService(conn);
    }

    public void loginView(View source){
        final TableLayout pLogin = (TableLayout)getLayoutInflater().inflate(R.layout.loginlayout,null);
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.u_header)
                .setTitle("登陆对话框")
                .setView(pLogin)
                .setPositiveButton("登陆", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputName = ((EditText) pLogin.findViewById(R.id.UserName)).getText().toString();
                        InputPass = ((EditText) pLogin.findViewById(R.id.UserPass)).getText().toString();
                        if (InputName.length() == 0){
                            binder.SetData("offline",null,null);
                            LoginSate.setImageResource(R.drawable.offline);
                            Header.setImageResource(R.drawable.headernullgray);
                        }
                        else{
                            binder.SetData("online", InputName, InputPass);
                            NameText.setText(InputName);
                            LoginSate.setImageResource(R.drawable.online);
                            Header.setImageResource(R.drawable.headernullblue);
                        }
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }
    public void DbNameView(View source){
        final TableLayout pDbName = (TableLayout)getLayoutInflater().inflate(R.layout.db_name,null);
        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("新数据库名")
                .setView(pDbName)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputDbName = ((EditText) pDbName.findViewById(R.id.DbName)).getText().toString();
                        new Thread(){
                            @Override
                            public void run(){
                                SdPath = Environment.getExternalStorageDirectory().toString();
                                InputRasterToDB(SdPath+"/CDTile",SdPath+"/MapData/",InputDbName);
                            }
                        }.start();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create()
                .show();
    }

    public void InputRasterToDB(String RasterPath,String DBPath,String DbName){
        SQLiteDatabase MyDB = null;
        MyDB = SQLiteDatabase.openOrCreateDatabase(DBPath+DbName+".db3",null);
        String level=null,row=null,col=null,table_name=null,location =null;
        int levels,rows,cols;
        File root = new File(RasterPath);
        File[] currentFilesLevel = root.listFiles();
        File[] currentFilesRow = null;
        File[] currentFilesCol = null;
        levels = currentFilesLevel.length;
        ContentValues values = new ContentValues();
        ByteArrayOutputStream os;
        Bitmap bmp = null;
        for(int i=0; i<levels;i++) {
            if (currentFilesLevel[i].isDirectory()) {
                level = currentFilesLevel[i].getName();
                table_name = "level_" + level;
                MyDB.execSQL("create table "+ table_name +"( _id integer primary key autoincrement,location varchar(50),location_png blob )");
                currentFilesRow = currentFilesLevel[i].listFiles();
                rows = currentFilesRow.length;
                for (int j = 0; j < rows; j++) {
                    if (currentFilesRow[j].isDirectory()) {
                        row = currentFilesRow[j].getName();
                        currentFilesCol = currentFilesRow[j].listFiles();
                        cols = currentFilesCol.length;
                        for (int k = 0; k < cols; k++) {
                            if (!currentFilesCol[k].isDirectory()) {
                                col = currentFilesCol[k].getName();
                                if (col.endsWith(".png")) {
                                    os = new ByteArrayOutputStream();
                                    bmp = BitmapFactory.decodeFile(currentFilesCol[k].getPath(), null);
                                    bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                                    location = row + "," + col.substring(0, col.length() - 4);
                                    values.put("location", location);
                                    values.put("location_png", os.toByteArray());
                                    MyDB.insert(table_name, null, values);
                                    values.clear();
                                    os = null;
                                }
                            }
                        }
                    }
                }
            }
        }
        MyDB.close();
    }
}
