package com.ivy.hatfiled.popchoice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

import com.ivy.hatfiled.appui.MapControl;
import com.ivy.hatfiled.appui.MapEditPanel;
import com.ivy.hatfiled.appui.R;

public class InputNewLayerNameWindow {
    protected RadioButton mPointRadio;
    protected RadioButton mLineRadio;
    protected RadioButton mSurfaceRadio;
    protected EditText mNewLayerName;
    protected View newLayerForm;
    private String pNewLayerName;
    private int pNewLayerType;
    public InputNewLayerNameWindow(Context context) {
        newLayerForm =  LayoutInflater.from(context).inflate(R.layout.newfeature,null);
        mPointRadio = (RadioButton)newLayerForm.findViewById(R.id.drawPoint);
        mLineRadio = (RadioButton)newLayerForm.findViewById(R.id.drawLine);
        mSurfaceRadio = (RadioButton)newLayerForm.findViewById(R.id.drawSurface);
        mNewLayerName = (EditText)newLayerForm.findViewById(R.id.newLayerName);
    }
    public void showNewLayerNameFrm(final Context context,final MapControl mapcontrol){
        new AlertDialog.Builder(context)
                .setIcon(R.drawable.edittools)
                .setTitle("Create NewLayerName")
                .setView(newLayerForm)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pNewLayerName = mNewLayerName.getText().toString();
                        if( mPointRadio.isChecked()) pNewLayerType=1;
                        if( mLineRadio.isChecked())  pNewLayerType=2;
                        if( mSurfaceRadio.isChecked()) pNewLayerType=3;
                        mapcontrol.setEditLayer(pNewLayerName,pNewLayerType);
                        MapEditPanel EditTools = new MapEditPanel();
                        EditTools.show(context, mapcontrol, pNewLayerType);
                    }
                })
                .create()
                .show();
    }
    public String GetNewLayerName(){
        return pNewLayerName;
    }
    public int GetNewLayerType(){
        return pNewLayerType;
    }
}
