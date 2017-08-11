package com.ivy.hatfiled.appui;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class NewHatMap extends Activity {
    private final static String CRAZYAT_ACTION = "android.intent.action.CRAYIT_ACTION";//open next activity
    private String mDataPath;
    public static float density;
    private int mScreenWidth;
    private int mScreenHeight;

    //控件对象
    private MapControl pMapControl;
    //**************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_hat_map);
        DisplayMetrics dm = new DisplayMetrics();// 获取屏幕宽高
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth  = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        this.density = dm.density;
        mDataPath = Environment.getExternalStorageDirectory().toString() + "/CDTile/";
        onInitView();//控件初始化
        pMapControl.setDataPath(mDataPath);
        Intent sIntent = new Intent();
        sIntent.setAction("android.intent.action.LOGIN_SERVICE");
        startService(sIntent);
    }

    private void onInitView(){
        pMapControl = (MapControl) findViewById(R.id.MyMapControl);
    }

    //-----------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        SubMenu sub;
        menu.add(1, 11, 0, R.string.SEARCH_MENU).setIcon(R.drawable.search);
        sub = menu.addSubMenu(2, 2, 0, R.string.BOOKMARKS_MENU).setIcon(R.drawable.bookmarks);
        sub.add(2, 21, 0, R.string.BOOKMARK_ADD_MENU);
        sub.add(2, 22, 1, R.string.BOOKMARKS_VIEW_MENU);
        menu.add(3, 31, 0, R.string.MY_ACCOUNT_MENU).setIcon(R.drawable.account);
        menu.add(4, 41, 0, R.string.MY_LOCATION_MENU).setIcon(R.drawable.location_menu);
        menu.add(5, 51, 0, R.string.MANAGE_TRACK_MENU).setIcon(R.drawable.track_manage);
        menu.add(6, 61, 0, R.string.MORE_SETTING_MENU).setIcon(R.drawable.moresetting);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case 11:
                break;
            case 21:
                break;
            case 22:
                break;
            case 31:
                Intent pIntent = new Intent();
                pIntent.setAction(NewHatMap.CRAZYAT_ACTION);
                startActivity(pIntent);
                break;
            case 41:
                break;
            case 51:
                break;
            case 61:
        }
        return false;
    }

}

//    -----------------------------------------------------------------------------
//    private SQLiteDatabase MyDb;
//    private String[] edit_express = new String[]{"3","4","5"};
//    private int[] choice_icon = new int[]{R.drawable.editpoint,R.drawable.line,R.drawable.area};
//    private SimpleAdapter simpleAdapter;
//    List<Map<String, Object>> ListItems = new ArrayList<Map<String, Object>>();
//    for(int i= 0; i<choice_edit.length;i++){
//        Map<String, Object> ListItem = new HashMap<String, Object>();
//        ListItem.put("icon",choice_icon[i]);
//        ListItem.put("choice",choice_edit[i]);
//        ListItem.put("express",edit_express[i]);
//        ListItems.add(ListItem);
//    }

//    SimpleAdapter simpleAdapter = new SimpleAdapter(this, ListItems, R.layout.simple_item,
//            new String[]{"icon","choice","express"},
//            new int[]{R.id.icon, R.id.choice, R.id.express});
//            for (int i = m_TilerLocation[0] - 1; i <= m_TilerLocation[2] + 1; i++)
//                for (int j = m_TilerLocation[1] - 1; j <= m_TilerLocation[3] + 1; j++) {
//                    pCur = MyDb.rawQuery("select location_png from level_" + m_Levels + " where location = ?", new String[]{i + "," + j});
//                    if (pCur.getCount() > 0) {
//                        pCur.moveToFirst();
//                        bitMapByteArray = pCur.getBlob(0);
//                        bitMap = BitmapFactory.decodeByteArray(bitMapByteArray, 0, bitMapByteArray.length);
//                        ListItem.put(i + "," + j, bitMap);
//                    }
//                }
//

//    private void  ShowLayersControl(){
//
//        final AlertDialog.Builder builder2 = new AlertDialog.Builder(this)
//                .setTitle("图层列表")
//                .setIcon(R.mipmap.ic_launcher)
//                .setMultiChoiceItems(items, show, new DialogInterface.OnMultiChoiceClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
//
//                });
//        setButtonExit(builder2)
//                .create()
//                .show();
//    }
//        private AlertDialog.Builder setButtonExit(AlertDialog.Builder builder) {
//            return builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                }
//            });
//        }