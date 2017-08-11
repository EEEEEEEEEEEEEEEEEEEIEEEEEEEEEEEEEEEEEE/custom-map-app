package com.ivy.hatfiled.gis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IMapTools {
    //members
    //function
    public  IMapTools(){}
    //线要素裁剪
    public List CreateSplitLine(List Points,float TileSize,float scale,double[] map_bound) {
        float pCurr[], pNext[],NewPoint[],CachePoint[];
        float xNext, yNext, xCurr, yCurr;
        int a, b, c, d, i, m, n, k, size;
        boolean isSet = false;
        size = Points.size() - 1;
        List Line = new ArrayList();
        List CacheLine = new ArrayList();
        pNext = (float[]) Points.get(0);
        for (i = 0; i < size; i++) {
            pCurr = (float[]) Points.get(i);
            pNext = (float[]) Points.get(i + 1);
            xCurr = (float) (pCurr[0] - map_bound[0]);
            yCurr = (float) (pCurr[1] - map_bound[1]);
            xNext = (float) (pNext[0] - map_bound[0]);
            yNext = (float) (pNext[1] - map_bound[1]);
            Line.add(pCurr);
            if (xCurr <= xNext && yCurr < yNext) {
                a = (int) (xCurr / scale / TileSize) + 1;
                b = (int) Math.ceil(xNext / scale / TileSize) - 1;
                c = (int) (yCurr / scale / TileSize) + 1;
                d = (int) Math.ceil(yNext / scale / TileSize) - 1;
                //无交点(a>b && c>d)
                if (a > b && c <= d) {//y方向有从c到d的交点c++
                    for (n = c; n <= d; n++) {
                        NewPoint = new float[3];
                        NewPoint[1] = (float)map_bound[1]+n * scale * TileSize;
                        NewPoint[0] = (float)map_bound[0]+xCurr + (xNext - xCurr) * (n * scale * TileSize - yCurr) / (yNext - yCurr);
                        NewPoint[2] = 1;
                        Line.add(NewPoint);
                    }
                }else if (a <= b && c > d) {//x方向有从a到b的交点a++
                    for (m = a; m <= b; m++) {
                        NewPoint = new float[3];
                        NewPoint[0] = (float)map_bound[0]+m * scale * TileSize;
                        NewPoint[1] = (float)map_bound[1]+yCurr + (yNext - yCurr) * (m * scale * TileSize - xCurr) / (xNext - xCurr);
                        NewPoint[2] = 1;
                        Line.add(NewPoint);
                    }
                }else if (a <= b && c <= d) {//x方向有从a到b的交点a++,y方向有从c到d的交点c++
                    for (m = a; m <= b; m++) {//x方向
                        NewPoint = new float[3];
                        NewPoint[0] = (float)map_bound[0]+m * scale * TileSize;
                        NewPoint[1] = (float)map_bound[1]+yCurr + (yNext - yCurr) * (m * scale * TileSize - xCurr) / (xNext - xCurr);
                        NewPoint[2] = 1;
                        CacheLine.add(NewPoint);
                    }
                    for (n = c; n <= d; n++) {//y方向
                        isSet = false;
                        NewPoint = new float[3];
                        NewPoint[1] = (float)map_bound[1]+n * scale * TileSize;
                        NewPoint[0] = (float)map_bound[0]+xCurr + (xNext - xCurr) * (n * scale * TileSize - yCurr) / (yNext - yCurr);
                        NewPoint[2] = 1;
                        for (k = 0; k < CacheLine.size(); k++) {
                            CachePoint = (float[]) CacheLine.get(k);
                            if (NewPoint[1] < CachePoint[1]) {
                                CacheLine.set(k, NewPoint);
                                isSet = true;
                                break;
                            }
                        }
                        if (!isSet) CacheLine.add(NewPoint);
                    }
                    Line.addAll(CacheLine);
                    CacheLine.clear();
                }
            } else if (xCurr < xNext && yCurr >= yNext) {
                a = (int) (xCurr / scale / TileSize) + 1;
                b = (int) Math.ceil(xNext / scale / TileSize) - 1;
                c = (int) Math.ceil(yCurr / scale / TileSize) - 1;
                d = (int) (yNext / scale / TileSize) + 1;
                //无交点(a>b && c<d)
                if (a > b && c >= d) {//y方向有从c到d的交点,c--
                    for (n = c; n >= d; n--) {
                        NewPoint = new float[3];
                        NewPoint[1] = (float)map_bound[1]+n * scale * TileSize;
                        NewPoint[0] = (float)map_bound[0]+xCurr + (xNext - xCurr) * (n * scale * TileSize - yCurr) / (yNext - yCurr);
                        NewPoint[2] = 1;
                        Line.add(NewPoint);
                    }
                }else if (a <= b && c < d) {//x方向有从a到b的交点,a++
                    for (m = a; m <= b; m++) {
                        NewPoint = new float[3];
                        NewPoint[0] = (float)map_bound[0]+m * scale * TileSize;
                        NewPoint[1] = (float)map_bound[1]+yCurr + (yNext - yCurr) * (m * scale * TileSize - xCurr) / (xNext - xCurr);
                        NewPoint[2] = 1;
                        Line.add(NewPoint);
                    }
                }else if (a <= b && c >= d) {//x方向从a到b,y方向从c到d,a++,c--
                    for (m = a; m <= b; m++) {//x方向
                        NewPoint = new float[3];
                        NewPoint[0] = (float)map_bound[0]+m * scale * TileSize;
                        NewPoint[1] = (float)map_bound[1]+yCurr + (yNext - yCurr) * (m * scale * TileSize - xCurr) / (xNext - xCurr);
                        NewPoint[2] = 1;
                        CacheLine.add(NewPoint);
                    }
                    for (n = c; n >= d; n--) {//y方向
                        isSet = false;
                        NewPoint = new float[3];
                        NewPoint[1] = (float)map_bound[1]+n * scale * TileSize;
                        NewPoint[0] = (float)map_bound[0]+xCurr + (xNext - xCurr) * (n * scale * TileSize - yCurr) / (yNext - yCurr);
                        NewPoint[2] = 1;
                        for (k = 0; k < CacheLine.size(); k++) {
                            CachePoint = (float[]) CacheLine.get(k);
                            if (NewPoint[1] > CachePoint[1]) {
                                CacheLine.set(k, NewPoint);
                                isSet = true;
                                break;
                            }
                        }
                        if (!isSet) CacheLine.add(NewPoint);
                    }
                    Line.addAll(CacheLine);
                    CacheLine.clear();
                }
            } else if (xCurr >= xNext && yCurr > yNext) {
                a = (int) Math.ceil(xCurr / scale / TileSize) - 1;
                b = (int) (xNext / scale / TileSize) + 1;
                c = (int) Math.ceil(yCurr / scale / TileSize) - 1;
                d = (int) (yNext / scale / TileSize) + 1;
                //无交点(a<b && c<d)
                if (a < b && c >= d) {//y方向有从c到d的交点,c--
                    for (n = c; n >= d; n--) {
                        NewPoint = new float[3];
                        NewPoint[1] = (float)map_bound[1]+n * scale * TileSize;
                        NewPoint[0] = (float)map_bound[0]+xCurr + (xNext - xCurr) * (n * scale * TileSize - yCurr) / (yNext - yCurr);
                        NewPoint[2] = 1;
                        Line.add(NewPoint);
                    }
                }else if (a >= b && c < d) {//x方向有从a到b的交点,a--
                    for (m = a; m >= b; m--) {
                        NewPoint = new float[3];
                        NewPoint[0] = (float)map_bound[0]+m * scale * TileSize;
                        NewPoint[1] = (float)map_bound[1]+yCurr + (yNext - yCurr) * (m * scale * TileSize - xCurr) / (xNext - xCurr);
                        NewPoint[2] = 1;
                        Line.add(NewPoint);
                    }
                }else if (a >= b && c >= d) {//x方向从a到b,y方向从c到d,a--,c--
                    for (m = a; m >= b; m--) {//x方向
                        NewPoint = new float[3];
                        NewPoint[0] = (float)map_bound[0]+m * scale * TileSize;
                        NewPoint[1] = (float)map_bound[1]+yCurr + (yNext - yCurr) * (m * scale * TileSize - xCurr) / (xNext - xCurr);
                        NewPoint[2] = 1;
                        CacheLine.add(NewPoint);
                    }
                    for (n = c; n >= d; n--) {//y方向
                        isSet = false;
                        NewPoint = new float[3];
                        NewPoint[1] =(float)map_bound[1]+ n * scale * TileSize;
                        NewPoint[0] = (float)map_bound[0]+xCurr + (xNext - xCurr) * (n * scale * TileSize - yCurr) / (yNext - yCurr);
                        NewPoint[2] = 1;
                        for (k = 0; k < CacheLine.size(); k++) {
                            CachePoint = (float[]) CacheLine.get(k);
                            if (NewPoint[1] > CachePoint[1]) {
                                CacheLine.set(k, NewPoint);
                                isSet = true;
                                break;
                            }
                        }
                        if (!isSet) CacheLine.add(NewPoint);
                    }
                    Line.addAll(CacheLine);
                    CacheLine.clear();
                }
            } else if (xCurr > xNext && yCurr <= yNext) {
                a = (int) Math.ceil(xCurr / scale / TileSize) - 1;
                b = (int) (xNext / scale / TileSize) + 1;
                c = (int) (yCurr / scale / TileSize) + 1;
                d = (int) Math.ceil(yNext / scale / TileSize) - 1;
                //无交点(a<b && c>d)
                if (a < b && c <= d) {//y方向有从c到d的交点,c++
                    for (n = c; n <= d; n++) {
                        NewPoint = new float[3];
                        NewPoint[1] = (float)map_bound[1]+n * scale * TileSize;
                        NewPoint[0] = (float)map_bound[0]+xCurr + (xNext - xCurr) * (n * scale * TileSize - yCurr) / (yNext - yCurr);
                        NewPoint[2] = 1;
                        Line.add(NewPoint);
                    }
               }else if (a >= b && c > d) {//x方向有从a到b的交点,a--
                    for (m = a; m >= b; m--) {
                        NewPoint = new float[3];
                        NewPoint[0] = (float)map_bound[0]+m * scale * TileSize;
                        NewPoint[1] = (float)map_bound[1]+yCurr + (yNext - yCurr) * (m * scale * TileSize - xCurr) / (xNext - xCurr);
                        NewPoint[2] = 1;
                        Line.add(NewPoint);
                    }
                }else if (a >= b && c <= d) {//x方向从a到b,y方向从c到d,a--,c++
                    for (m = a; m >= b; m--) {//x方向
                        NewPoint = new float[3];
                        NewPoint[0] = (float)map_bound[0]+m * scale * TileSize;
                        NewPoint[1] = (float)map_bound[1]+yCurr + (yNext - yCurr) * (m * scale * TileSize - xCurr) / (xNext - xCurr);
                        NewPoint[2] = 1;
                        CacheLine.add(NewPoint);
                    }
                    for (n = c; n <= d; n++) {//y方向
                        isSet = false;
                        NewPoint = new float[3];
                        NewPoint[1] = (float)map_bound[1]+n * scale * TileSize;
                        NewPoint[0] = (float)map_bound[0]+xCurr + (xNext - xCurr) * (n * scale * TileSize - yCurr) / (yNext - yCurr);
                        NewPoint[2] = 1;
                        for (k = 0; k < CacheLine.size(); k++) {
                            CachePoint = (float[]) CacheLine.get(k);
                            if (NewPoint[1] < CachePoint[1]) {
                                CacheLine.set(k, NewPoint);
                                isSet = true;
                                break;
                            }
                        }
                        if (!isSet) CacheLine.add(NewPoint);
                    }
                    Line.addAll(CacheLine);
                    CacheLine.clear();
                }
            }//两点重合了，无网格相交
        }
        Line.add(pNext);//对Line进行划分
        return Line;
    }
    public Map<String, Object> CutSplitLine(List Lines,float TileSize,float scale,double[] map_bound){
        Map<String, Object> TileLines = new HashMap<String, Object>();
        List CachePoints = new ArrayList();
        Polyline CacheLine,TileLine;
        FeatureLayer CacheLayer;
        List sLine;
        int size,length,row,col,CurrFID;
        float xCenter,yCenter;
        float[] pCurr,pMbr;
        length = Lines.size();
        for(int j=0;j<length;j++) {
            CacheLine = (Polyline)Lines.get(j);
            sLine =  CreateSplitLine(CacheLine.GetPoints(),TileSize,scale,map_bound);
            CurrFID = CacheLine.GetFID();
            size = sLine.size();
            for (int i = 0; i < size; i++) {
                pCurr = (float[]) sLine.get(i);
                if (pCurr[2] == 1) {
                    CachePoints.add(pCurr);
                    pMbr = calpmbr(CachePoints);
                    xCenter = (float) ((pMbr[0] + pMbr[2]) / 2 - map_bound[0]);
                    yCenter = (float) ((pMbr[1] + pMbr[3]) / 2 - map_bound[1]);
                    row = (int) (yCenter / scale / TileSize);
                    col = (int) (xCenter / scale / TileSize);
                    if(TileLines.containsKey(col + "," + row)){
                        ((FeatureLayer)TileLines.get(col + "," + row)).addLineFeature(CurrFID,CachePoints);
                    }else {
                        CacheLayer = new FeatureLayer(col + "," + row,2);
                        CacheLayer.addLineFeature(CurrFID,CachePoints);
                        TileLines.put(col + "," + row, CacheLayer);
                    }
                    CachePoints.clear();
                    CachePoints.add(pCurr);
                } else CachePoints.add(pCurr);
            }
        }
        return TileLines;
    }
    private float[] calpmbr(List points){
        float point[] = (float[]) points.get(0);
        float mbr[] = {point[0],point[1],point[0],point[1]};//xmin,ymin,xmax,ymax
        for(int i=1;i<points.size();i++){
            point = (float[]) points.get(i);
            if(mbr[0] > point[0] ) mbr[0] = point[0];
            if(mbr[1] > point[1] ) mbr[1] = point[1];
            if(mbr[2] < point[0] ) mbr[2] = point[0];
            if(mbr[3] < point[1] ) mbr[3] = point[1];
        }
        return mbr;
    }
}
