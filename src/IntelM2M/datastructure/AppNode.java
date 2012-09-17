package IntelM2M.datastructure;

import java.util.LinkedHashMap;
import java.util.Map;

public class AppNode {
	public String appName="";   // appliance name /*getAppList*/
	/*eus*/
	public String state="";    //eus: on | off | standby
	public double confidence=0; //eus
	public String escType=""; //eus: explicit or implicit
	
	/* for esdse*/
	public String envContext="";  /*real environment context*/
	public String agentName="";   /*eus dispatch*/
	public String comfortType=""; //thermal,visual,ap  /*getAppList*/
	public String location="";/*getActAppList*/
	public Boolean global=false;
	public double priority=0;   /*appliance preference*/
	
	/*for experiment*/
	public Boolean haveAPControlFromOn=false; /*有沒有被ap agent 控制*/
	public Map<String, Double> ampere=  new LinkedHashMap<String, Double>(); /*getAppList*/
		
		public AppNode(String name)
		{
			appName = name;
			
			
		}
		public AppNode copyAppNode(AppNode tmp){
			AppNode app= new AppNode();

			app.appName=tmp.appName;
			app.state=tmp.state;
			app.confidence=tmp.confidence;
			app.escType=tmp.escType;			
			app.location=tmp.location;
			app.comfortType=tmp.comfortType;
			/*Notice : This is not clone*/
			app.ampere=tmp.ampere;
			app.global=tmp.global;
			app.agentName=tmp.agentName;
			app.envContext=tmp.envContext;
			app.haveAPControlFromOn=tmp.haveAPControlFromOn;
			
			return app;
		}
		public AppNode(){
			
		}
}
