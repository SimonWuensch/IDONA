package com.src.sim.metaioapplication.metaio;


import android.app.Activity;
import android.util.Log;
import com.metaio.sdk.jni.IGeometry;
import com.metaio.sdk.jni.IMetaioSDKCallback;
import com.metaio.sdk.jni.TrackingValues;
import com.metaio.sdk.jni.TrackingValuesVector;
import com.src.sim.metaioapplication.dialog.DialogManager;
import com.src.sim.metaioapplication.logic.WayAlgorithm;
import com.src.sim.metaioapplication.logic.resource.Direction;
import com.src.sim.metaioapplication.logic.resource.LocationObject;
import com.src.sim.metaioapplication.logic.resource.Marker;
import com.src.sim.metaioapplication.ui.activitiy.navigation.ARNavigationActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CallBackHandler extends IMetaioSDKCallback {

    private Activity activity;

    private List<Integer> idList;
    private Map<Integer, List<IGeometry>> geometryMap;
    private Map<Integer, Marker> markerMap;

    private int currentMarkerID;
    private LocationObject locationObject;
    private boolean isFirstStart = true;
    private DialogManager dialogManager = null;

    public CallBackHandler(Activity activity, Map<Integer, Marker> markerMap, Map<Integer, List<IGeometry>> geometryMap){
        this.geometryMap = geometryMap;
        this.markerMap = markerMap;
        this.idList = new ArrayList<Integer>();
        this.activity = activity;
        this.dialogManager = new DialogManager(activity);
    }

    public void updateLocationObject(LocationObject locationObject){
        Log.d(CallBackHandler.class.getSimpleName(), "LocationObject changened to " + locationObject.toString());
        this.locationObject = locationObject;
        this.dialogManager = new DialogManager(activity);
        resetIDList();
    }

    @Override
    public void onTrackingEvent(TrackingValuesVector trackingValues) {
         if (trackingValues.size() >= 1 && !isFirstStart && locationObject != null) {
             TrackingValues value = trackingValues.get(0);
             int systemId = value.getCoordinateSystemID();
             Log.d(CallBackHandler.class.getSimpleName(), "Got Marker with id " + systemId);

             Marker marker = markerMap.get(systemId);

             if(marker != null ) {
                 currentMarkerID = systemId;
                 if(!idList.contains(systemId)) {
                     Log.d(CallBackHandler.class.getSimpleName(), "ID is not in IDList " + systemId);
                     com.src.sim.metaioapplication.asynctask.WayAlgorithm.calculateWay(this, new WayAlgorithm(markerMap), marker, locationObject);
                 }
                 Direction direction = marker.getDirectionToLocationObject(locationObject);
                 if(marker.getDirectionToLocationObject(locationObject) != null){
                     dialogManager.aimFoundDialog(direction, locationObject);
                 }
             }
         }
         isFirstStart = false;
    }

    public void updateGeometryRotation(int systemId, Direction direction){
        IGeometry geometry;
        String arrow = direction.getArrow().getArrow();

        if(arrow.equals(Direction.ARROWNORMAL)){
            geometry = geometryMap.get(systemId).get(0);
            geometryMap.get(systemId).get(1).setVisible(false);
            geometryMap.get(systemId).get(2).setVisible(false);
        }else if(arrow.equals(Direction.ARROWCURVE)){
            geometry = geometryMap.get(systemId).get(1);
            geometryMap.get(systemId).get(0).setVisible(false);
            geometryMap.get(systemId).get(2).setVisible(false);
        }else if(arrow.equals(Direction.ARROWTURN)){
            geometry = geometryMap.get(systemId).get(2);
            geometryMap.get(systemId).get(0).setVisible(false);
            geometryMap.get(systemId).get(1).setVisible(false);
        }else{
            throw new NullPointerException("Arrow does not exist! - " + arrow + ".");
        }

        geometry.setVisible(true);
        geometry.setRotation(direction.getArrow().getGeometryRotation());
        Log.d(ARNavigationActivity.class.getSimpleName(), "Geometry [" + systemId + "] set to Rotation " + direction.getArrow().name());
    }

    public IGeometry getCurrentIGeometry(){
        List<IGeometry> geometries =  geometryMap.get(currentMarkerID);
        if(geometries != null)
            for(IGeometry geometry : geometries){
                if(geometry.isVisible()) {
                    return geometry;
                }
            }
        throw new NullPointerException("No visible IGeometry found.");
    }

    public void resetIDList(){
        idList = new ArrayList<Integer>();
    }

    public void addIDToIDList(int id){
        idList.add(id);
    }
}