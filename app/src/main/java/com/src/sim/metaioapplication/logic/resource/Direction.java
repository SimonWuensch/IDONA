package com.src.sim.metaioapplication.logic.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.src.sim.metaioapplication.logic.resource.LocationObject.Kind;

public class Direction {

	public static String LEFTHAND = "lefthand";
	public static String RIGHTHAND = "righthand";

	public static String LEFT = "<-";
	public static String RIGHT = "->";
	public static String BACKWARDS = "v";
	public static String FORWARDS = "^";

	private String direction;
	private int distance;
	private int trackerID;
	private Map<String, Map<LocationObject, Integer>> locationObjects;
	Map<Kind, List<LocationObject>> locationObjectMap;

	public Direction(Tracker tracker, int distance, String direction) {
		this.direction = direction;
		this.distance = distance;
		this.trackerID = tracker.getId();
		initLocationObjectMap();
	}

	public Direction(String direction) {
		this.direction = direction;
		initLocationObjectMap();
	}

	public Direction() {
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getTrackerID() {
		return trackerID;
	}

	public void setTrackerID(int trackerID) {
		this.trackerID = trackerID;
	}

	public Map<String, Map<LocationObject, Integer>> getLocationObjects() {
		return locationObjects;
	}

	public void setLocationObjects(Map<String, Map<LocationObject, Integer>> locationObjects) {
		this.locationObjects = locationObjects;
	}

	public Map<Kind, List<LocationObject>> getLocationObjectMap() {
		return locationObjectMap;
	}

	public void setLocationObjectMap(Map<Kind, List<LocationObject>> locationObjectMap) {
		this.locationObjectMap = locationObjectMap;
	}

	private void initLocationObjectMap() {
		locationObjects = new HashMap<String, Map<LocationObject, Integer>>();
		locationObjectMap = new HashMap<Kind, List<LocationObject>>();
		locationObjects.put(LEFTHAND, new HashMap<LocationObject, Integer>());
		locationObjects.put(RIGHTHAND, new HashMap<LocationObject, Integer>());
	}

	public String getHandside(LocationObject object) {
		for (LocationObject lObjectValue : locationObjects.get(LEFTHAND).keySet()) {
			if (lObjectValue.getDescription().equals(object.getDescription())) {
				return LEFTHAND;
			}
		}
		for (LocationObject lObjectValue : locationObjects.get(RIGHTHAND).keySet()) {
			if (lObjectValue.getDescription().equals(object.getDescription())) {
				return RIGHTHAND;
			}
		}
		throw new NullPointerException("Direction contains not the LocationObject [" + object.getDescription() + "]");
	}

	public int getCountPosition(LocationObject lObject) {
		Object[] array = locationObjects.get(getHandside(lObject)).entrySet().toArray();
		Arrays.sort(array, new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return ((Map.Entry<LocationObject, Integer>) o1).getValue().compareTo(
						((Map.Entry<LocationObject, Integer>) o2).getValue());
			}
		});
		for (int i = 0; i < array.length; i++) {
			if (((Map.Entry<LocationObject, Integer>) array[i]).getKey().equals(lObject)) {
				return i + 1;
			}
		}
		return -1;
	}

	public int getDistanceToLocationObject(LocationObject lObject) {
		String handSide = getHandside(lObject);
		for (LocationObject lObjectValue : locationObjects.get(handSide).keySet()) {
			if (lObjectValue.getDescription().equals(lObject.getDescription())) {
				return locationObjects.get(handSide).get(lObjectValue);
			}
		}
		throw new NullPointerException("Direction contains not the LocationObject [" + lObject.getDescription() + "]");
	}

	public void setTracker(Tracker startTracker, Tracker endTracker, int distance, String oppositeDirection) {
		this.trackerID = endTracker.getId();
		this.distance = distance;

		Direction endToStartDirection = new Direction(startTracker, distance, oppositeDirection);

		for (LocationObject lObject : locationObjects.get(LEFTHAND).keySet()) {
			endToStartDirection.addLocationObject(lObject, distance - locationObjects.get(LEFTHAND).get(lObject),
					RIGHTHAND);
		}
		for (LocationObject lObject : locationObjects.get(RIGHTHAND).keySet()) {
			endToStartDirection.addLocationObject(lObject, distance - locationObjects.get(RIGHTHAND).get(lObject),
					LEFTHAND);
		}

		endTracker.addDirection(endToStartDirection);
	}

	public boolean hasTracker() {
		return trackerID > 0 ? true : false;
	}

	public void addLocationObjectToLeft(LocationObject lObject, int distance) {
		addLocationObject(lObject, distance, LEFTHAND);
	}

	public void addLocationObjectToRight(LocationObject lObject, int distance) {
		addLocationObject(lObject, distance, RIGHTHAND);
	}

	private void addLocationObject(final LocationObject lObject, int distance, String handside) {
		locationObjects.get(handside).put(lObject, distance);
		if (!locationObjectMap.containsKey(lObject.getKind())) {
			locationObjectMap.put(lObject.getKind(), new ArrayList<LocationObject>() {
				private static final long serialVersionUID = 1L;
				{
					add(lObject);
				}
			});
		} else {
			locationObjectMap.get(lObject.getKind()).add(lObject);
		}
	}

	// TODO delete
	public String printLocationObjectList() {
		StringBuilder builder = new StringBuilder();

		Map<LocationObject, Integer> leftObjects = locationObjects.get(LEFTHAND);
		Map<LocationObject, Integer> rightObjects = locationObjects.get(RIGHTHAND);

		if (!leftObjects.isEmpty() && !rightObjects.isEmpty())
			builder.append(", ");

		if (!leftObjects.isEmpty())
			builder.append(" Left: ");

		for (LocationObject leftObject : leftObjects.keySet()) {
			builder.append(leftObject.toString() + " pos: " + getCountPosition(leftObject) + " dis: "
					+ getDistanceToLocationObject(leftObject));

			if (leftObjects.size() - 1 != getCountPosition(leftObject)) {
				builder.append(", ");
			}
		}

		if (!rightObjects.isEmpty())
			builder.append(" Right: ");

		for (LocationObject rightObject : rightObjects.keySet()) {
			builder.append(rightObject.toString() + " pos: " + getCountPosition(rightObject) + " dis: "
					+ getDistanceToLocationObject(rightObject));

			if (rightObjects.size() - 1 != getCountPosition(rightObject)) {
				builder.append(", ");
			}
		}

		return builder.toString();
	}
}
