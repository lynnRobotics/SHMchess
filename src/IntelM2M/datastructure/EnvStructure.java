package IntelM2M.datastructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import IntelM2M.agent.visual.VisualComfortTable;


public class EnvStructure {
	
	
	public static  ArrayList<String> activityList; 
	public static Map<String, ArrayList<String>> sensorStatus; //<name,on standby..> 
	public static  Map<String, SensorNode> sensorList; //<type_id,SensorNode>
	public static Map<String, RelationTable> actAppList ;// key ex: hallway_GoOut
	public static HashSet<String> roomList ;
	public static  Map<String, String> sensorState;
	public static Map<String,AppNode> appList;
    public static boolean[] actPreState;
    public static Map<String, VisualComfortTable> visualComfortTableList;

    static{
    	read();
    }
	
	public static void read() {
		XMLHandler xml = new XMLHandler();
		activityList = xml.getActList();
		sensorList = xml.getSensorList();
		sensorStatus = xml.getSensorStatus();
		appList = xml.getAppList();
		actAppList = xml.getActAppList();
		roomList = buildRoomList();
		actPreState = buildactPreState();
		sensorState = buildSensorState();
		visualComfortTableList = xml.getVisualComfortTableList();
	}
	public EnvStructure(){

	}
	
	static HashSet<String> buildRoomList(){
    	/*Build room list  */
	    Set<String> acts = actAppList.keySet(); //record all activity
	    HashSet<String> roomList = new HashSet<String>(); //record all  room
	    for(String act:acts)
	    {
	    	String[] split = act.split("_");
	    	roomList.add(split[0]);
	    }
	    return roomList;
	}
	
	static boolean[] buildactPreState(){
		boolean[] actPreState = new boolean[activityList.size()];
		 Arrays.fill(actPreState, false);
		return actPreState;
	}
	
	static Map<String, String> buildSensorState(){
		Map<String, String> sensorState= new HashMap<String, String>();
		return sensorState;
	}

}
