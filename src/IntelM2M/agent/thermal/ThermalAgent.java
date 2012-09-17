package IntelM2M.agent.thermal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import IntelM2M.datastructure.AppNode;
import IntelM2M.datastructure.EnvStructure;
import IntelM2M.datastructure.RelationTable;
import IntelM2M.epcie.GAinference;
import IntelM2M.epcie.GaGenerator;
import IntelM2M.esdse.Optimizer;
import IntelM2M.exp.ExpRecorder;

public class ThermalAgent {
	final double initConstraint=1.0;
	final double secConstraint=2.0;
	
	//final double initConstraint=2.0;
	//final double secConstraint=2.5;
	public Map<String, ArrayList<AppNode>>  getActAppList(ArrayList<AppNode> decisionList,GAinference gaInference){
		
		/*get single activity from GA*/
		Set<String> singleAct= gaInference.actInferResultSet;
		
		/*build appliance list for each single activity*/
		
		/*get appliance at same location with act*/
		//get location list
		ArrayList<String> locationList=getActLocationList(singleAct);
		//insert appliance into actAppList based on locationList

		Map<String, ArrayList<AppNode>> actAppList = new LinkedHashMap<String, ArrayList<AppNode>>();
		int i=0;
		for(String act:singleAct){
			ArrayList<AppNode> appList= new ArrayList<AppNode>();
			for(AppNode app:decisionList){
				
				if(app.global){
					appList.add(app);
				}
				else if(locationList.get(i).equals(app.location)){
					appList.add(app);
				}
			}
			actAppList.put(act, appList);
		i+=1;
		}

		
		return actAppList;
	}
	public ArrayList<String> getActLocationList(Set<String>singleAct){
		ArrayList<String> locationList= new ArrayList<String>();
		
		Set<String> location_actSet = EnvStructure.actAppList.keySet(); //record all activity
		for(String act:singleAct){
			for(String location_act:location_actSet){
				if(location_act.contains(act)){
					String location=location_act.split("_")[0];
					locationList.add(location);
				}
			}
			
		}
		if(locationList.size()!=singleAct.size()){
			return null;
		}else{
			return locationList;
		}
	}
	/*to be delete*/
//	public Map<String, ArrayList<AppNode>> getActAppList2( Set<String> singleAct, ArrayList<String> locationList,ArrayList<AppNode> decisionList){
//		Map<String, ArrayList<AppNode>> actAppList = new LinkedHashMap<String, ArrayList<AppNode>>();
//		int i=0;
//		for(String act:singleAct){
//			ArrayList<AppNode> appList= new ArrayList<AppNode>();
//			for(AppNode app:decisionList){
//				if(locationList.get(i).equals(app.location)){
//					appList.add(app.copyAppNode(app));
//				}
//			}
//			actAppList.put(act, appList);
//		i+=1;
//		}
//		return actAppList;
//	}
	
//	public Map<String, ArrayList<AppNode>> removeNotOnApp(Map<String, ArrayList<AppNode>> actAppList,GAinference gaInference){
//		
//		Map<String, RelationTable> ga0ActAppList=gaInference.GaEscList.get(0).actAppList;
//		Map<String, RelationTable> ga0ActAppList2= new HashMap <String, RelationTable>();
//		GaGenerator ga0Generator=gaInference.GaGeneratorList.get(0);
//		Set<String> ga0ActAppListKey=ga0ActAppList.keySet();
//		for(String gaName:ga0ActAppListKey){
//			String actName=ga0Generator.getGroupMember(gaName).get(0);
//			RelationTable tmp=ga0ActAppList.get(actName);
//			ga0ActAppList2.put(actName, tmp);
//			
//		}
//		
//		Set<String> actAppListKey=actAppList.keySet();
//		Set<String> ga0Key=ga0ActAppList2.keySet();
//		
//		for(String actName:actAppListKey){
//
//			ArrayList<AppNode> appList=actAppList.get(actName);
//			ArrayList<AppNode> eusList=ga0ActAppList2.get(actName).appList;
//
//			ArrayList<AppNode> newAppList= new ArrayList<AppNode>();
//		
//			for(AppNode app:appList){
//				for(AppNode eus:eusList){
//					if(eus.appName.equals(app.appName)){
//						if(!eus.state.equals("off")){
//							newAppList.add(app);
//						}
//					}
//				}
//			}
//			
//			actAppList.put(actName, newAppList);
//				
//			
//		}
//		
//		return actAppList;
//		
//	}
	
	public ArrayList<Double>  getPmvList(Map<String, ArrayList<AppNode>> actAppList){
	
		//更據活動取得活動量
		ArrayList<Double> intensityList=getActIntensityList(actAppList);
		
		//活動量 溫度 風量 取得pmv
		ArrayList<Double> pmvList= new ArrayList<Double>();
		
		Set<String> actSet= actAppList.keySet();
		int i=0;
		for(String act:actSet){
			ArrayList<AppNode> appList=actAppList.get(act);
			
			double intensity=intensityList.get(i);
			double temp=getTempFromAppList(appList);
			double vel=getVelFromAppList(appList);
			
			PMVCalculate pc= new PMVCalculate(intensity,temp,vel);
			double pmv=pc.getPMVandPPD()[0];
			
			pmvList.add(pmv);
			i+=1;
		}
		
		/**/
		return pmvList;
	}
	public ArrayList<Double> getActIntensityList(Map<String, ArrayList<AppNode>> actAppList){
		ArrayList<Double> intensityList= new ArrayList<Double>();
		Set<String> singleAct=actAppList.keySet();
		Map<String, RelationTable> act_location=EnvStructure.actAppList;
		Set<String> keySet=act_location.keySet();
		for(String act:singleAct){
			for(String str:keySet){
				if(str.contains(act)){
					double tmp=EnvStructure.actAppList.get(str).intensity;
					intensityList.add(tmp);
				}
			}
//			double tmp=EnvStructure.actAppList.get(act).intensity;
//			intensityList.add(tmp);
		}
		return intensityList;
		
	}
	
	public double getTempFromAppList(ArrayList<AppNode> appList){
		double temp=0;
		for(AppNode app:appList){
			/*這邊沒有彈性*/
			if(app.appName.equals("current_AC_livingroom")  && app.envContext.split("_").length>1){
				 temp=Double.parseDouble(app.envContext.split("_")[1]);
			}
		}
		return temp;
	}
	
	public double getVelFromAppList(ArrayList<AppNode> appList){
		String fanStatus="";
		
		for(AppNode app:appList){
			/*這邊沒有彈性*/
			if(!app.appName.equals("current_AC_livingroom")){
				if(  app.envContext.split("_").length>1){
					fanStatus=app.envContext.split("_")[1];
				}else {
					fanStatus=app.envContext.split("_")[0];
				}
			}

		}
		double vel=0;
		if(fanStatus.equals("1")){
			vel=1;
		}else if(fanStatus.equals("2")){
			vel=0.5;
		}else if(fanStatus.equals("3")){
			vel=0.3;
		}else if(fanStatus.equals("off")){
			vel=0;
		}
		
		return vel;
		
	}
	
	
	/*input: decision array, inferResult*/
	public ArrayList<Double> getComfortArray(ArrayList<AppNode> decisionList,GAinference gaInference){
		
		
		/*getThermalApp*/
	//	ArrayList<AppNode> thermalAppList=getThermalApp(decisionList);
		
		/*  act-app List  by location*/
		Map<String, ArrayList<AppNode>> actAppList=getActAppList(decisionList,gaInference);
		
		/*cal PMV for each act-app*/
		ArrayList<Double> pmvList=getPmvList(actAppList);
		
		/*return comfort array*/
		return pmvList;
		
	}
	
	
	/*optimization*/
	
	public ArrayList <AppNode> getOptThermalList(ArrayList<AppNode> eusList,GAinference gaInference){
		/*get thermal List*/
		ArrayList<AppNode> thermalAppList= new ArrayList<AppNode>();
		ArrayList<AppNode> thermalRawList= new ArrayList<AppNode> ();
		for(AppNode app:eusList){
			if(app.agentName.equals("thermal")){
				AppNode app2= app.copyAppNode(app);
				thermalAppList.add(app2);
				AppNode app3= app.copyAppNode(app);
				thermalRawList.add(app3);
			}
		}
		/*thermal App list有可能是空的*/
		if(thermalAppList.size()==0){

			
			
			return new ArrayList<AppNode>();
		}else {
			/*從eusList中和thermal有關的電氣，找出所有的狀態排列組合*/
			Optimizer op=new Optimizer();
			/*thermal App list有可能是空的*/
			ArrayList<String> candidateList=op.buildCandidateList(thermalAppList);
			

			int iterateCounter=0;
			ArrayList<AppNode> bestAnswer=null;
			while(bestAnswer==null || bestAnswer.size()==0){
				bestAnswer=thermalIterate(candidateList, thermalAppList,thermalRawList,gaInference,iterateCounter);
				iterateCounter++;
			}

			return bestAnswer;
		}
		

		
	}
	
	private ArrayList<Double> relaxConstraint( Set<String>actInferResultSet,ArrayList<Double> constraintList, int counter){
		Map<String,Integer> priorityList = new HashMap<String,Integer>();
		priorityList.put("GoOut", 17);
		priorityList.put("ComeBack", 16);
		priorityList.put("WatchingTV", 3);
		priorityList.put("PlayingKinect", 6);
		priorityList.put("Chatting", 5);
		priorityList.put("ReadingBook", 4);
		priorityList.put("Cleaning", 10);
		priorityList.put("Cooking", 13);
		priorityList.put("WashingDishes", 9);
		priorityList.put("Laundering", 15);
		priorityList.put("Studying", 7);
		priorityList.put("Sleeping", 1);
		priorityList.put("ListeningMusic", 8);
		priorityList.put("UsingPC", 2);
		priorityList.put("TakingBath", 14);
		priorityList.put("UsingRestroom", 12);
		priorityList.put("BrushingTooth", 11);

		Map<String,Integer> inferPriorityList = new HashMap<String,Integer>();
		
		for(String str: actInferResultSet){
			inferPriorityList.put(str,  priorityList.get(str));
		}
		
		List<Map.Entry<String,Integer>> listData= new ArrayList<Map.Entry<String,Integer>> (inferPriorityList.entrySet());
		
		Collections.sort(listData,new Comparator< Map.Entry<String,Integer>>(){
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {    
                return (o2.getValue() - o1.getValue());    
            }    
        }); 

		
		for(int i=0;i<actInferResultSet.size();i++){
			constraintList.add(initConstraint);
		}
		
		int i=0;
		for(Map.Entry<String, Integer> entry:listData){	
			String key=entry.getKey();
			int j=0;
			for(String str:actInferResultSet){
				if(key.equals(str)){
					break;
				}
				j++;
			}	
			if(i==0){
				constraintList.set(j, 1+0.7*counter);
			}else if(i==1){
				constraintList.set(j, 1+0.3*counter);
			}
			i++;
		}
		
		return constraintList;
	}
	
	private ArrayList<AppNode>thermalIterate(ArrayList<String> candidateList,ArrayList<AppNode> thermalAppList,ArrayList<AppNode> thermalRawList,GAinference gaInference,int counter){
		
		/*原始環境的pmv和power consumption*/

		ArrayList<Double> rawPmvList= getComfortArray(thermalRawList, gaInference);
		double rawAmp= Optimizer.calEnergyConsumption(thermalRawList);
		


		ThermalSolution ts= new ThermalSolution();
		
		ArrayList<Double> constraintList= new ArrayList<Double>();
		
	
		if(counter==0){
			/*initial constraint array*/
			for(int i=0;i<gaInference.actInferResultSet.size();i++){
				constraintList.add(initConstraint);
			}
		}else{

			
			/*調整constraint*/ 
			
			/*方法一*/
			for(int i=0;i<gaInference.actInferResultSet.size();i++){
				constraintList.add(secConstraint);
			}
			/*方法二 根據活動的priority*/
			//relaxConstraint(gaInference.actInferResultSet,constraintList,counter);
			
			/* for experiment record conflict的次數*/
			ExpRecorder.exp.setThermalConflictCount();
			/*exp record end*/
		}


		
		/*iterate*/
		for(String candidate:candidateList){
			/*先寫死，沒有彈性*/
			String []split=candidate.split(",");
			if(split[0].equals("off") || split[0].equals("standby")){
				continue;
			}
			Optimizer.updateState(thermalAppList, candidate);
			
			ArrayList<Double> pmvList=getComfortArray(thermalAppList,gaInference);
			double amp=Optimizer.calEnergyConsumption(thermalAppList);
			/*判斷pmvList是否全部都在constraint內，*/
			boolean flag=true;
			for(int i=0;i<pmvList.size();i++){
				double pmv=pmvList.get(i);
				double constraint=constraintList.get(i);
				if(Math.abs(pmv)>constraint){
					flag=false;
					break;
				}
			}
			if(flag){
				ts=thermalListEvaluation(thermalAppList,pmvList,amp,ts,rawPmvList,rawAmp);
			}else{
				
			}

		}
		
	
		
		return ts.solution;
	}
	
	private ThermalSolution thermalListEvaluation(ArrayList<AppNode> thermalAppList,ArrayList<Double> pmvList, double amp,ThermalSolution ts,ArrayList<Double> rawPmvList,double rawAmp){
				if(ts.setFlag==false){
					ts.copy(thermalAppList, pmvList, amp);
					ts.setFlag=true;
				}else{
					/*evaluate best answer的條件*/ 
					if(amp<ts.totalAmp){
						ts.copy(thermalAppList, pmvList, amp);
						
					}
	            }
				return ts;


   }
	
	class ThermalSolution{
		ArrayList<AppNode> solution= new ArrayList<AppNode>();
		ArrayList<Double> solutionPMV=new ArrayList<Double>();
		double totalAmp=0;
		Boolean setFlag=false;
		public void copy(ArrayList<AppNode> thermalAppList,ArrayList<Double> pmvList, double amp){
			solution.clear();
			solutionPMV.clear();
			for(AppNode app:thermalAppList){
				solution.add(app.copyAppNode(app));
			}
			for(Double pmv:pmvList){
				solutionPMV.add(pmv);
			}
			totalAmp=amp;
		}
		
	}

}
