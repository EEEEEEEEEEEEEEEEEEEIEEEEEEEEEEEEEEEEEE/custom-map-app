package com.ivy.hatfiled.appui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

public class MapEditPanel{
    public static void show( Context context,final MapControl mapcontrol,int featureType){
        final PopupWindow EditTools;
        RelativeLayout cutsplitlayout = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.edit_feature,null);
        cutsplitlayout.findViewById(R.id.pointEdit).setEnabled(false);
        cutsplitlayout.findViewById(R.id.lineEdit).setEnabled(false);
        cutsplitlayout.findViewById(R.id.shapeEdit).setEnabled(false);
        EditTools  = new PopupWindow(cutsplitlayout,230,230);
        switch (featureType){
            case 1:
                cutsplitlayout.findViewById(R.id.pointEdit).setEnabled(true);
                cutsplitlayout.findViewById(R.id.pointEdit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mapcontrol.setGesture(1);
                }
            });
                break;
            case 2:
                cutsplitlayout.findViewById(R.id.lineEdit).setEnabled(true);
                cutsplitlayout.findViewById(R.id.lineEdit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapcontrol.setGesture(2);
                    }
                });
                break;
            case 3:
                cutsplitlayout.findViewById(R.id.shapeEdit).setEnabled(true);
                cutsplitlayout.findViewById(R.id.shapeEdit).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mapcontrol.setGesture(3);
                    }
                });
                break;
        }

        cutsplitlayout.findViewById(R.id.panEdit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mapcontrol.setGesture(0);
            }
        });

        cutsplitlayout.findViewById(R.id.closeEdit).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditTools.dismiss();
                mapcontrol.setGesture(0);
            }
        });
        EditTools.showAtLocation(mapcontrol, Gravity.NO_GRAVITY, 10, 60);
    }
}
