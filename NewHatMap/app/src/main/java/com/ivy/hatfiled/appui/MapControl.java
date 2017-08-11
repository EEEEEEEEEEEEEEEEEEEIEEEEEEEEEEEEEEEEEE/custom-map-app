package com.ivy.hatfiled.appui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ivy.hatfiled.gis.FeatureLayer;
import com.ivy.hatfiled.gis.GeoPaint;
import com.ivy.hatfiled.gis.IMapTools;
import com.ivy.hatfiled.gis.TilesLayer;
import com.ivy.hatfiled.popchoice.InputNewLayerNameWindow;
//import com.ivy.hatfiled.popchoice.MultiChoicePopWindow;
import com.ivy.hatfiled.popchoice.MultiChoicePopWindow;
import com.ivy.hatfiled.popchoice.SingleChoicePopWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MapControl extends RelativeLayout{
    //控制文本显示的变量;
    private String mCopyrightText;
    private int mCopyrightTextColor;
    private int mCopyrightTextSize;
    private Paint mCopyrightTextPaint;
    private Rect mCopyrightTextBound;
    //***********************************************

    //内置控件
    private ImageButton mZoomIn;
    private ImageButton mZoomOut;
    private ImageButton mMyPosition;
    private ImageButton mLayersBtn;
    private ImageButton mEditBtn;

    private MultiChoicePopWindow mLayersShowPopWindow;
    private SingleChoicePopWindow mLayerEditPopWindow;
    private List<String> mAllLayersNames;
    private List<String> mFeatureLayersNames;

    // ***********************************************
    //地图框架相关变量
    private String mDataPath;
    private float density;
    private int mLevel = 10;
    private int mTilesRange[] = {0, 0, 0, 0};//{x1,y1,x2,y2}，当前瓦片范围
    private float mPointsDistance[] = {0, 0};//两点间屏幕距离
    private float mPointsCenter[] = {0, 0};//两点中心点
    private float mMoveBeforeAfter[] = {0, 0, 0, 0};//移动前后的两点
    private float mDeviantPixel[] = {0, 0};//起始瓦片偏移
    private double mMapScale = 0;
    private double mCenterLatLong[] = {104.06, 30.67};
    private double mCenterPosition[] = {0, 0};
    private double mMapStart[] = {0, 0};
    private double mControlSize[]= {0,0};
    private double mControlBounds[] = {0, 0, 0, 0};//{x1,y1,x2,y2}
    private double mMaxMapBounds[] = {-20037508.3427892, -20037508.3427892, 20037508.3427892, 20037508.3427892};//{x1,y1,x2,y2}
    //***********************************************

    //绘图相关变量
    private List mFeatureLayers;//整幅图层
    private List mTileLayers;//瓦片式图层
    private TilesLayer mCacheLayer;//临时瓦片图层
    private FeatureLayer mFeatureLayer;//临时要素图层
    private FeatureLayer mEditingLayer;//要素编辑图层
    private IMapTools mMapTools = new IMapTools();
    private int mGesture = 0;//地图手势操作
    private Paint pPaint,lPaint,sPaint,slPaint,cPaint;//绘图画笔
    private Path mPath;//绘图路径（点坐标串）
    private float newGeoPoint[] = {0, 0, 0};
    private List pointsList = new ArrayList();//存放点迹

    //***********************************************
    public MapControl(Context context) {
        this(context, null);//调用下一个构造函数
    }
    public MapControl(Context context, AttributeSet attrs){
        super(context, attrs);//调用构造函数
        mFeatureLayers = new ArrayList();//初始化图层链表
        mTileLayers = new ArrayList();//
        mCacheLayer = new TilesLayer("底图",0);//新建栅格底图层，绘制的时候更新图层内容
        mTileLayers.add(mCacheLayer);
        mMapScale = 20037508.3427892 / 128 / Math.pow(2, mLevel);//级别转分辨率
        mCenterPosition = ConventToMercator(mCenterLatLong);   //控件中心坐标
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mControlSize[0] = this.getMeasuredWidth();
        mControlSize[1] = this.getMeasuredHeight();
        this.density = NewHatMap.density;
        mControlBounds = GetShowMapZone(mCenterPosition, mControlSize, mMapScale);
        mTilesRange = CalTilerLocation(mControlBounds, mMaxMapBounds, mMapScale);
        mMapStart = CalStartTilerLocation(mMaxMapBounds, mTilesRange, mMapScale);
        mDeviantPixel = CalStartScreenLocation(mMapStart, mControlBounds, mMapScale);
        InitBtns();
    }
    @Override
    protected void onDraw(Canvas canvas) {//super.onDraw(canvas);
        for (int i=0;i<mTileLayers.size();i++){
            mCacheLayer = (TilesLayer)mTileLayers.get(i);
            if (mCacheLayer.GetLayerType() == 0 && mCacheLayer.GetLayerIsShow()=="true") {//如果是栅格类型的图层
                mCacheLayer.drawRasterTiles(canvas, mTilesRange, 256, mDeviantPixel, mDataPath + mLevel + "/");
            }
            if (mCacheLayer.GetLayerType() == 2 && mCacheLayer.GetLayerIsShow()=="true") {//如果是矢量瓦片类型的图层
                mCacheLayer.drawFeatureTiles(canvas,mTilesRange, mControlSize,mCenterPosition,mMapScale, lPaint);
            }
        }
        for (int i=0;i<mFeatureLayers.size();i++) {
            mFeatureLayer = (FeatureLayer) mFeatureLayers.get(i);
            if (mFeatureLayer.GetLayerType() == 1 && mFeatureLayer.GetLayerIsShow()=="true") {//如果是矢量点类型的图层
                mFeatureLayer.drawPointFeatures(canvas, mControlSize, mCenterPosition, mMapScale, pPaint);
            }else if (mFeatureLayer.GetLayerType() == 2 &&  mFeatureLayer.GetLayerIsShow()=="true") {//如果是矢量线类型的图层
                mFeatureLayer.drawLineFeatures(canvas, mControlSize, mCenterPosition, mMapScale, lPaint);
            }else if (mFeatureLayer.GetLayerType() == 3 &&  mFeatureLayer.GetLayerIsShow()=="true") {//如果是矢量面类型的图层
                mFeatureLayer.drawSurfaceFeatures(canvas, mControlSize, mCenterPosition, mMapScale, sPaint, slPaint);
            }
        }
        if (mGesture == 2) {
            canvas.drawPath(mPath, cPaint);
        }
        if (mGesture == 3) {
            canvas.drawPath(mPath, cPaint);
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getPointerCount() == 1){
            if(mGesture == 0)       MoveWithOneFinger(event);
            else if(mGesture == 1) DrawPointWithOneFinger(event);
            else if(mGesture == 2) DrawPolyLineWithOneFinger(event);
            else if(mGesture == 3) DrawPolygonWithOneFinger(event);
        }else if(event.getPointerCount() >1) ZoomWithTwoFinger(event);
        return true;
    }
    public void InitBtns(){
        mZoomIn = new ImageButton(getContext());
        mZoomOut = new ImageButton(getContext());
        mMyPosition = new ImageButton(getContext());
        mLayersBtn = new ImageButton(getContext());
        mEditBtn = new ImageButton(getContext());
        mZoomIn.setBackgroundResource(R.drawable.zoom_in_btn);
        mZoomOut.setBackgroundResource(R.drawable.zoom_out_btn);
        mMyPosition.setBackgroundResource(R.drawable.position_btn);
        mLayersBtn.setBackgroundResource(R.drawable.layers_btn);
        mEditBtn.setBackgroundResource(R.drawable.edittools_btn);
        addView(mZoomIn);
        addView(mZoomOut);
        addView(mMyPosition);
        addView(mLayersBtn);
        addView(mEditBtn);
        int x,y,s,d;
        d=(int)(5 * density);
        s=(int)(40 * density);
        x=(int)mControlSize[0];
        y=(int)mControlSize[1];
        MarginLayoutParams margin = new MarginLayoutParams(mZoomIn.getLayoutParams());
        margin.setMargins(x - s - 3 * d, y - s - 3 * d, x - 3 * d, y - 3 * d);
        RelativeLayout.LayoutParams BtnParams = new RelativeLayout.LayoutParams(margin);
        mZoomOut.setLayoutParams(BtnParams);
        margin.setMargins(x - s - 3 * d, y - s - 13 * d, x - 3 * d, y - 13 * d);
        BtnParams = new RelativeLayout.LayoutParams(margin);
        mZoomIn.setLayoutParams(BtnParams);
        margin.setMargins(4 * d, y - 11 * d, 4 * d, y - 5 * d);
        BtnParams = new RelativeLayout.LayoutParams(margin);
        mMyPosition.setLayoutParams(BtnParams);
        margin.setMargins(x - s - 3 * d, 12 * d, x - 3 * d, s + 12 * d);
        BtnParams = new RelativeLayout.LayoutParams(margin);
        mLayersBtn.setLayoutParams(BtnParams);
        margin.setMargins(x - s - 3 * d, s + 14 * d, x - 3 * d, 2 * s + 14 * d);
        BtnParams = new RelativeLayout.LayoutParams(margin);
        mEditBtn.setLayoutParams(BtnParams);
        mZoomIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnlarge();
            }
        });
        mZoomOut.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                setNarrow();
            }
        });
        mMyPosition.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        mLayersBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InitLayersShowPopWindow();
                mLayersShowPopWindow.show(true);
            }
        });
        mEditBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InitLayerEditPopWindow();
                mLayerEditPopWindow.show(true);
            }
        });
    }

    //初始化MultiPopWindow
    private void InitLayersShowPopWindow(){

        final int TileLayersCount,FeatureLayersCount;
        List<String> mLayersIsView = new ArrayList<String>();
        mAllLayersNames = new ArrayList<String>();

        TilesLayer tLayer;
        TileLayersCount = mTileLayers.size();
        for(int j=0;j<TileLayersCount;j++) {
            tLayer = (TilesLayer) (mTileLayers.get(j));
            mAllLayersNames.add(tLayer.GetLayerName());
            mLayersIsView.add(tLayer.GetLayerIsShow());
        }

        FeatureLayer mLayer;
        FeatureLayersCount = mFeatureLayers.size();
        for(int i=0;i<FeatureLayersCount;i++) {
            mLayer = (FeatureLayer) (mFeatureLayers.get(i));
            mAllLayersNames.add(mLayer.GetLayerName());
            mLayersIsView.add(mLayer.GetLayerIsShow());
        }

        boolean booleans[] = new boolean[mLayersIsView.size() ];
        for(int i=0;i<mLayersIsView.size();i++) {
            if (mLayersIsView.get(i) == "true")  booleans[i]=true;
            if (mLayersIsView.get(i) == "false") booleans[i]=false;
        }
        View mRootView =findViewById(R.id.MyMapControl);
        mLayersShowPopWindow = new MultiChoicePopWindow(getContext(), mRootView, mAllLayersNames, booleans);
        mLayersShowPopWindow.setTitle("图层列表");
        mLayersShowPopWindow.setOnOKButtonListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean[] selItems = mLayersShowPopWindow.getSelectItem();
                int size = selItems.length;
                for (int i = 0; i < TileLayersCount; i++) {
                    if (selItems[i]) {
                        ((TilesLayer) mTileLayers.get(i)).setLayerIsShow("true");
                    }else{
                        ((TilesLayer) mTileLayers.get(i)).setLayerIsShow("false");
                    }
                }
                for (int i = 0; i < FeatureLayersCount; i++) {
                    if (selItems[i+TileLayersCount]) {
                        ((FeatureLayer) mFeatureLayers.get(i)).setLayerIsShow("true");
                    }else{
                        ((FeatureLayer) mFeatureLayers.get(i)).setLayerIsShow("false");
                    }
                }
                invalidate();
            }
        });
    }
    //初始化SinglePopWindow
    private void InitLayerEditPopWindow(){
        mFeatureLayersNames = new ArrayList<String>();
        FeatureLayer pLayer;
        for(int i=0;i<mFeatureLayers.size();i++){
            pLayer = (FeatureLayer)(mFeatureLayers.get(i));
            mFeatureLayersNames.add(pLayer.GetLayerName());
        }
        mFeatureLayersNames.add("新建一个图层");
        final View mRootView =findViewById(R.id.MyMapControl);
        mLayerEditPopWindow = new SingleChoicePopWindow(getContext(), mRootView, mFeatureLayersNames);
        mLayerEditPopWindow.setTitle("选择可编辑图层");
        final InputNewLayerNameWindow mNewLayerNameFrm = new InputNewLayerNameWindow(getContext());
        mLayerEditPopWindow.setOnOKButtonListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int selItem = mLayerEditPopWindow.getSelectItem();// Toast.makeText(getContext(), "selItem = " + selItem,Toast.LENGTH_SHORT).show();
                if(-1 != selItem) {
                    if (mFeatureLayersNames.get(selItem) == "新建一个图层") {
                        mNewLayerNameFrm.showNewLayerNameFrm(getContext(), (MapControl) mRootView);//图层名输入窗口-------
                    }
                    else {
                        setEditLayer(mFeatureLayersNames.get(selItem), -1);
                        MapEditPanel EditTools = new MapEditPanel();
                        EditTools.show(getContext(), (MapControl) mRootView,mGesture);
                    }
                }else{
                    Toast.makeText(getContext(), "没有选取要编辑的图层",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    //单点触摸移动
    private void MoveWithOneFinger(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mMoveBeforeAfter[0] = event.getX();
                mMoveBeforeAfter[1] = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveBeforeAfter[2] = event.getX();
                mMoveBeforeAfter[3] = event.getY();
                setNewPos(mMoveBeforeAfter[2] - mMoveBeforeAfter[0], mMoveBeforeAfter[3] - mMoveBeforeAfter[1]);
                mMoveBeforeAfter[0] = mMoveBeforeAfter[2];
                mMoveBeforeAfter[1] = mMoveBeforeAfter[3];
                invalidate();
                break;
        }
    }
    //两点触摸缩放
    private void ZoomWithTwoFinger(MotionEvent event){
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_POINTER_DOWN:
                mPointsDistance[0] = DistanceBetweenTwoPoint(event);
                mPointsCenter = CenterOfTwoPoint(event);
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mPointsDistance[1] = DistanceBetweenTwoPoint(event);
                if (mPointsDistance[1] - mPointsDistance[0] > 50){
                    mLevel += 1;
                    if(mLevel>15) mLevel = 15;
                }else if(mPointsDistance[0] - mPointsDistance[1] > 50){
                    mLevel -= 1;
                    if(mLevel<0 ) mLevel = 0;
                }
                FigureChangeMapSize();
                ((TilesLayer)(mTileLayers.get(0))).ClearTiles();
                invalidate();
                break;
        }
    }
    //----------------------变量调用函数------------------------
    public List GetFeatureLayers(){
        return mFeatureLayers;
    }
    //设置编辑图层
    public void setEditLayer(String LayerName,int LayerType){
        setGesture(LayerType);
        boolean isFindLayer = false;
        for(int i=0;i<mFeatureLayers.size();i++){
            if(((FeatureLayer)(mFeatureLayers.get(i))).GetLayerName() == LayerName){
                mEditingLayer = (FeatureLayer)(mFeatureLayers.get(i));
                setGesture(mEditingLayer.GetLayerType());
                isFindLayer = true;
            }
        }
        if(isFindLayer == false){
            mEditingLayer = new FeatureLayer(LayerName,mGesture);
            mFeatureLayers.add(mEditingLayer);
        }
        mEditingLayer.setLayerEdit(true);
        mPath = new Path();
        switch (mGesture){
            case 1:
                setMapPaint(new GeoPaint(getContext()).CreatePen("default_point"));
                break;
            case 2:
                setMapPaint(new GeoPaint(getContext()).CreatePen("red_line"),
                        new GeoPaint(getContext()).CreatePen("default_line"));
                break;
            case 3:
                setMapPaint(new GeoPaint(getContext()).CreatePen("red_surface"),
                        new GeoPaint(getContext()).CreatePen("default_surface"),
                        new GeoPaint(getContext()).CreatePen("default_outline"));
                break;
        }
    }
    //--------------------------------------------------------

    //----------------------编辑功能区------------------------
    //设置绘图手势操作选项
    public void setGesture(int gesture){
        mGesture = gesture;
    }
    //设置画笔,初始化绘图缓存
    public void setMapPaint(Paint paint){
        pPaint = paint;
    }
    public void setMapPaint(Paint paint1,Paint paint2){
        cPaint = paint1;
        lPaint = paint2;
    }
    public void setMapPaint(Paint paint1,Paint paint2,Paint paint3){
        cPaint = paint1;
        sPaint = paint2;
        slPaint = paint3;
    }
    //绘点
    private void DrawPointWithOneFinger(MotionEvent event){
        float StartX=0, StartY=0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                StartX = event.getX();
                StartY = event.getY();
                newGeoPoint = ConventPointToGeo(StartX,StartY,0);
                break;
            case MotionEvent.ACTION_UP:
                mEditingLayer.addPointFeature(mEditingLayer.GetTotalFeatures(),newGeoPoint );
                invalidate();
                break;
        }
    }
    //绘线
    private void DrawPolyLineWithOneFinger(MotionEvent event){
        float StartX=0, StartY=0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                StartX = event.getX();
                StartY = event.getY();
                mPath.moveTo(StartX,StartY);
                invalidate();
                newGeoPoint = ConventPointToGeo(StartX,StartY,0);
                pointsList.add(newGeoPoint);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                StartX = event.getX();
                StartY = event.getY();
                invalidate();
                newGeoPoint = ConventPointToGeo(StartX,StartY,0);
                pointsList.add(newGeoPoint);
                break;
            case MotionEvent.ACTION_UP:
                invalidate();
                mEditingLayer.addLineFeature(mEditingLayer.GetTotalFeatures(), pointsList);
                pointsList.clear();
                mPath.reset();
                break;
        }
    }
    //绘面
    private void DrawPolygonWithOneFinger(MotionEvent event){
        float StartX=0, StartY=0;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                StartX = event.getX();
                StartY = event.getY();
                mPath.moveTo(StartX,StartY);
                invalidate();
                newGeoPoint = ConventPointToGeo(StartX,StartY,0);
                pointsList.add(newGeoPoint);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                StartX = event.getX();
                StartY = event.getY();
                invalidate();
                newGeoPoint = ConventPointToGeo(StartX,StartY,0);
                pointsList.add(newGeoPoint);
                break;
            case MotionEvent.ACTION_UP:
                mPath.close();
                invalidate();
                mEditingLayer.addSurfaceFeature(mEditingLayer.GetTotalFeatures(), pointsList);
                pointsList.clear();
                mPath.reset();
                break;
        }
    }
    //-------------------------------------------------------------

    //-------------------------图层分片----------------------------
    public void CutToTiles(){
        List lines = mEditingLayer.GetFeatureClass();
        Map<String, Object> Tiles = mMapTools.CutSplitLine(lines, 256, (float) mMapScale, mMaxMapBounds);
        mCacheLayer = new TilesLayer("底图",2);
        mCacheLayer.addTiles(Tiles);
        mTileLayers.add(mCacheLayer);
        invalidate();
    }
    //两触摸点之间的屏幕距离
    private float DistanceBetweenTwoPoint(MotionEvent event){
        float x = event.getX(1) - event.getX(0);
        float y = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(x*x + y*y);
    }
    //两触摸点的中心像素点
    private float[] CenterOfTwoPoint(MotionEvent event){
        float xy[] = {0, 0};
        xy[0] = (event.getX(1) + event.getX(0))/2;
        xy[1] = (event.getY(1) + event.getY(0))/2;
        return xy;
    }
    //经纬度转墨卡托，输入{经度，纬度}
    public double[] ConventToMercator(double[] LatLong) {
        double ceny, center[] = {0, 0};
        center[0] = LatLong[0] * 20037508.3427892 / 180;
        ceny = Math.log(Math.tan((90 - LatLong[1]) * Math.PI / 360)) / (Math.PI / 180);
        center[1] = ceny * 20037508.3427892 / 180;
        return center;
    }
    //地图控件区域地理坐标范围
    public double[] GetShowMapZone(double[] center_xy, double[] control_wh, double map_scale) {
        double ViewSizes[] = {0, 0, 0, 0};
        ViewSizes[0] = center_xy[0] - map_scale * control_wh[0] / 2;
        ViewSizes[1] = center_xy[1] - map_scale * control_wh[1] / 2;
        ViewSizes[2] = center_xy[0] + map_scale * control_wh[0] / 2;
        ViewSizes[3] = center_xy[1] + map_scale * control_wh[1] / 2;
        return ViewSizes;
    }
    //地图控件区域瓦片行列范围
    public int[] CalTilerLocation(double[] view_xy, double[] map_zone, double map_scale) {
        int TilerXYZone[] = {0, 0, 0, 0};
        TilerXYZone[0] = (int) (((view_xy[0] - map_zone[0]) / map_scale) / 256);
        TilerXYZone[1] = (int) (((view_xy[1] - map_zone[1]) / map_scale) / 256);
        TilerXYZone[2] = (int) (((view_xy[2] + map_zone[2]) / map_scale) / 256);
        TilerXYZone[3] = (int) (((view_xy[3] + map_zone[3]) / map_scale) / 256);
        return TilerXYZone;
    }
    //地图控件左上角瓦片行列
    public double[] CalStartTilerLocation(double[] map_zone, int[] tiler_zone, double map_scale) {
        double TilerStart[] = {0, 0};
        TilerStart[0] = map_zone[0] + (tiler_zone[0] * 256 * map_scale);
        TilerStart[1] = map_zone[1] + (tiler_zone[1] * 256 * map_scale);
        return TilerStart;
    }
    //起始瓦片与地图控件左上角像素偏移
    public float[] CalStartScreenLocation(double[] StartTiler_location, double[] view_xy, double map_scale) {
        float screen_location[] = {0, 0};
        screen_location[0] = (int) (-(view_xy[0] - StartTiler_location[0]) / map_scale);
        screen_location[1] = (int) (-(view_xy[1] - StartTiler_location[1]) / map_scale);
        return screen_location;
    }
    // 中心缩小图片（按钮）
    public void setNarrow() {
        mLevel -= 1;
        if(mLevel<0 ) mLevel = 0;
        ChangeMapSize();
        ((TilesLayer)(mTileLayers.get(0))).ClearTiles();
        invalidate();
    }
    // 中心放大图片（按钮）
    public void setEnlarge() {
        mLevel += 1;
        if(mLevel>15 ) mLevel = 15;
        ChangeMapSize();
        ((TilesLayer)(mTileLayers.get(0))).ClearTiles();
        invalidate();
    }
    // 中心缩放地图
    public void ChangeMapSize() {
        mMapScale = 20037508.3427892 / 128 / Math.pow(2, mLevel);
        mControlBounds = GetShowMapZone(mCenterPosition, mControlSize, mMapScale);
        mTilesRange = CalTilerLocation(mControlBounds, mMaxMapBounds, mMapScale);
        mMapStart = CalStartTilerLocation(mMaxMapBounds, mTilesRange, mMapScale);
        mDeviantPixel = CalStartScreenLocation(mMapStart, mControlBounds, mMapScale);
    }
    //手势缩放地图
    public void FigureChangeMapSize() {
        float PointLocation[] = {0, 0 };
        PointLocation[0] =(float)(mCenterPosition[0] - (mControlSize[0]/2-mPointsCenter[0])*mMapScale);
        PointLocation[1] =(float)(mCenterPosition[1] - (mControlSize[1]/2-mPointsCenter[1])*mMapScale);
        mMapScale = 20037508.3427892 / 128 / Math.pow(2, mLevel);
        mCenterPosition[0] =(float)(PointLocation[0] + (mControlSize[0]/2-mPointsCenter[0])*mMapScale);
        mCenterPosition[1] =(float)(PointLocation[1] + (mControlSize[1]/2-mPointsCenter[1])*mMapScale);
        mControlBounds = GetShowMapZone(mCenterPosition, mControlSize, mMapScale);
        mTilesRange = CalTilerLocation(mControlBounds, mMaxMapBounds, mMapScale);
        mMapStart = CalStartTilerLocation(mMaxMapBounds, mTilesRange, mMapScale);
        mDeviantPixel = CalStartScreenLocation(mMapStart, mControlBounds, mMapScale);
    }
    //屏幕点x,y,(z)转墨卡托
    public float[] ConventPointToGeo(float x,float y,float z){
        float Geo[] = {0,0,0};
        Geo[0] = (float) (mCenterPosition[0] + (x - mControlSize[0]/2)*mMapScale);
        Geo[1] = (float) (mCenterPosition[1] + (y- mControlSize[1]/2)*mMapScale);
        return Geo;
    }
    // 平移-上下左右
    public void setNewPos(float disx, float disy) {
        mCenterPosition[0] -= disx * mMapScale;
        mCenterPosition[1] -= disy * mMapScale;
        mControlBounds = GetShowMapZone(mCenterPosition, mControlSize, mMapScale);
        mTilesRange = CalTilerLocation(mControlBounds, mMaxMapBounds, mMapScale);
        mMapStart = CalStartTilerLocation(mMaxMapBounds, mTilesRange, mMapScale);
        mDeviantPixel = CalStartScreenLocation(mMapStart, mControlBounds, mMapScale);
    }
    //设置数据目录
    public void setDataPath(String path){
        mDataPath = path;
    }

}
