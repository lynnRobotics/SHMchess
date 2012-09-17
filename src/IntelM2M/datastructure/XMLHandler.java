package IntelM2M.datastructure;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import IntelM2M.agent.visual.VisualComfortTable;





public class XMLHandler {
	
	//File xml= new File(".\\_input_data\\new.xml");
	File xml= new File(".\\_input_data\\simulator\\simulator.xml");
	
	public void XMLHandler(){

	}
	
	public ArrayList<String> getActList(){
		 ArrayList<String> actList = new ArrayList<String>();
		try{
  			SAXReader saxReader = new SAXReader();
  			Document document = saxReader.read(xml);
  			List list=document.selectNodes("/metaData/activityList/type");
  			Iterator iter=list.iterator();
  			while(iter.hasNext()){
  				Element actElement=(Element)iter.next();
  				String activity=actElement.attributeValue("id");
  				actList.add(activity);
  			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return actList;
	}
	
	public Map<String, ArrayList<String>> getSensorStatus(){
		Map<String, ArrayList<String>> sensorList = new LinkedHashMap<String, ArrayList<String>>();
		
		try{
  			SAXReader saxReader = new SAXReader();
  			Document document = saxReader.read(xml);
  			
  			List list=document.selectNodes("/metaData/sensorList/type");
  			Iterator iter=list.iterator();
  			while(iter.hasNext()){
  				Element typeElement=(Element)iter.next();
  		
  				List list2= typeElement.selectNodes("sensor");
  				Iterator iter2=list2.iterator();
  				while(iter2.hasNext()){
  					Element sensorElement=(Element)iter2.next();  					
  					String sensorName = sensorElement.element("name").getText();
  					String[] sensorStatus = sensorElement.element("status").getText().split(" ");
  					
  					ArrayList<String> tmp = new ArrayList<String>();
 					for(int i = 0; i < sensorStatus.length; i ++)
 						tmp.add(sensorStatus[i]);
 					sensorList.put(sensorName, tmp);
  		          
  				}
  				
  			}
  			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return sensorList;
	}
	
    public Map<String, SensorNode> getSensorList()
  	{  		 
    	 Map<String, SensorNode> sensorList= new LinkedHashMap<String, SensorNode>();;
  		try
  		{
  			SAXReader saxReader = new SAXReader();
  			Document document = saxReader.read(xml);
  			List list=document.selectNodes("/metaData/sensorList/type");
  			Iterator iter=list.iterator();
  			while(iter.hasNext()){
  				Element typeElement=(Element)iter.next();
  				String type=typeElement.attributeValue("id");
  				List list2= typeElement.selectNodes("sensor");
  				Iterator iter2=list2.iterator();
  				while(iter2.hasNext()){
  					Element sensorElement=(Element)iter2.next();
  					String sensorID=sensorElement.attributeValue("id");
  					
  					String sensorName = sensorElement.element("name").getText();
  					String[] sensorStatus = sensorElement.element("status").getText().split(" ");
  		           	String[] sensorThreshold = sensorElement.element("threshold").getText().split(" ");
  		           	double[] sensorThres = new double[sensorThreshold.length];
  		           	for(int i = 0; i < sensorThreshold.length; i ++)
  		           		sensorThres[i] = Double.parseDouble(sensorThreshold[i]);
  		           	
  		           	SensorNode tmp = new SensorNode(sensorName, type, sensorThres, sensorStatus);
  		          sensorList.put(type + "_" + sensorID, tmp);
  				}
  				
  			}
  		      		 
  		 }
  		 catch (Exception e)
  		 {
  			 e.printStackTrace();
  			 
  		 }
  		return sensorList;
  	}
	
    public Map<String,AppNode> getAppList(){
    	 Map<String,AppNode> appList=new LinkedHashMap<String, AppNode>();
		try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(xml);
 			List list=document.selectNodes("/metaData/sensorList/type");
  			Iterator iter=list.iterator();
  			while(iter.hasNext()){
  				Element typeElement=(Element)iter.next();
  				String type=typeElement.attributeValue("id");
  				if(type.equals("socketmeter")||type.equals("light")){
  	  				List list2= typeElement.selectNodes("sensor");
  	  				Iterator iter2=list2.iterator();
  	  				while(iter2.hasNext()){
  	  					Element sensorElement=(Element)iter2.next();
  	  					
  	  					String sensorName = sensorElement.element("name").getText();
  	  					String[] ampere= sensorElement.element("ampere").getText().split(" ");
  	  				    String comfortType=sensorElement.element("comfort_type").getText();
  	  				
  	  					AppNode app= new AppNode();
  	  					app.appName=sensorName;
  	  					app.comfortType=comfortType;

  	  					if(sensorElement.element("switch")!=null){/*判斷有無switch 標籤*/
  	  						String [] switchNum= sensorElement.element("switch").getText().split(" ");
  	  						String [] switchAmpere= sensorElement.element("switch_ampere").getText().split(" ");
  	  						if(ampere.length==2){
  	  							app.ampere.put("off", Double.parseDouble(ampere[0]));
  	  						}else if(ampere.length==3){
  	  	  						app.ampere.put("off", Double.parseDouble(ampere[0]));
  	  	  						app.ampere.put("standby", Double.parseDouble(ampere[1]));
  	  						}
  	  						for(int i=0;i<switchNum.length;i++){
  	  							app.ampere.put("on_"+switchNum[i], Double.parseDouble( switchAmpere[i]));
  	  						}
  	  						
  	  					}else
  	  					if(ampere.length==2){
  	  						app.ampere.put("off", Double.parseDouble(ampere[0]));
  	  						app.ampere.put("on", Double.parseDouble(ampere[1]));
  	  					}else if(ampere.length==3){
  	  						app.ampere.put("off", Double.parseDouble(ampere[0]));
  	  						app.ampere.put("standby", Double.parseDouble(ampere[1]));
  	  						app.ampere.put("on", Double.parseDouble(ampere[2]));
  	  					}
  	  					appList.put(sensorName, app);
  	  		         
  	  				}
  				}

  				
  			}
  			/*set location information*/
  			 list = document.selectNodes("/metaData/relation/location" );
			 iter = list.iterator();
		       while(iter.hasNext()){
		    	   Element roomElement =(Element)iter.next();
		    	   String roomName=roomElement.attributeValue("id");
		    	   Element appElement = roomElement.element("appliance");
		    	   String []appliances= appElement.getText().split(" ");
		    	   
		    	   if(roomName.equals("global")){
			    	   for(String str:appliances){
			    		   appList.get(str).location=roomName;
			    		   appList.get(str).global=true;
			    	   }  
		    	   }else{

			    	   for(String str:appliances){
			    		   AppNode app=appList.get(str);
			    		   app.location=roomName;
			    		   app.global=false;
			    	   }  
		    	   }

		       }
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		return appList;
    }
	
	public Map<String, RelationTable>  getActAppList(){
		 Map<String, RelationTable> tableList= new LinkedHashMap<String, RelationTable>();
		 try {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(xml);
			 List list = document.selectNodes("/metaData/relation/location" );
			 Iterator iter = list.iterator();
		       while(iter.hasNext()){
		              Element roomElement =(Element)iter.next();
		              // get location
		              String roomName=roomElement.attributeValue("id");
		              //get activity 

		               Element actElement = roomElement.element("activity");
		              
		               if(actElement==null){ /*global tag*/

		               }else{
			               String []activities= actElement.getText().split(" ");
			               for(String str:activities){		            	   	            	 	            	   

				               RelationTable table = new RelationTable();	
				              
				              /*修改 不要只放 appliances陣列的app 要放全部的app*/ 
				               Map<String,AppNode> appList=EnvStructure.appList;
				               Set<String> keySet= appList.keySet();
				               for(String str2:keySet){
				            	   AppNode app=appList.get(str2);
				            	   /*考慮是否要用copy*/
				            	   /*add app into table*/
				            	   AppNode newApp=app.copyAppNode(app);
				            	   table.appList.add(newApp);
				               }

			            	   //get Intensity
			            	   Node node=document.selectSingleNode("/metaData/activityList/type[@id='"+str+"']/intensity[1]");		            	 
			            	   table.intensity =Double.parseDouble( node.getText());
				               //add table to tableList
			            	   
			            	   tableList.put(roomName + "_" + str, table);      		            	   
			               }
		             }

		              
		      }
		     
		} catch (Exception e) {
			// TODO: handle exception
		}
		  return tableList;
	}
	
	public Map<String, VisualComfortTable> getVisualComfortTableList(){
		Map<String, VisualComfortTable> tableList= new LinkedHashMap<String, VisualComfortTable>();
		try{
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(xml);
			List list = document.selectNodes("/metaData/comfort_table/visual/location" );
			Iterator iter = list.iterator();
			while(iter.hasNext()){
				Element roomElement =(Element)iter.next();
				String roomName=roomElement.attributeValue("id");
				Element appElement = roomElement.element("appliance");
				String []appliances= appElement.getText().split(" ");
				Element relationElement = roomElement.element("relation");
				String []relation= relationElement.getText().split(";");
				VisualComfortTable vct= new VisualComfortTable(appliances,relation);
				tableList.put(roomName, vct);
			}
		}catch(Exception e){
			
		}
		return tableList;
	}
	
	public void getThermalComfortTable(){
		
	}
	
}
