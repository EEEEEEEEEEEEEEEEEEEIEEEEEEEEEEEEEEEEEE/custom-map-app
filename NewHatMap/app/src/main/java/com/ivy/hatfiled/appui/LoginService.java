package com.ivy.hatfiled.appui;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class LoginService extends Service {
    public LoginService() {}
    private String UserName = null;
    private String UserPass = null;
    private String LoginState = "offline";
    private boolean exit = false;
    private MyBinder binder = new MyBinder();

    public class MyBinder extends Binder{
        public void SetData(String state, String str1,String str2){
            LoginState = state;
            UserName = str1;
            UserPass = str2;
        }
        public String GetLoginState(){
            return LoginState;
        }
        public String GetName(){
            return UserName;
        }
        public String GetPass(){
            return UserPass;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override
    public void onCreate(){
        super.onCreate();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return START_STICKY;
    }
    @Override
    public boolean onUnbind(Intent intent){
        return true;
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        this.exit = true;
    }
}
