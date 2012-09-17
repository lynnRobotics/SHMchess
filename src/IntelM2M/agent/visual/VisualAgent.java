package IntelM2M.agent.visual;

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
import IntelM2M.epcie.GAinference;
import IntelM2M.esdse.Optimizer;
import IntelM2M.exp.ExpRecorder;

public class VisualAgent {
	
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
		//Map<String, ArrayList<AppNode>> actAppList=getActAppList2(singleAct,locationList,decisionList);
		
		Map<String, ArrayList<AppNode>> actAppList = new LinkedHashMap<String, ArrayList<AppNode>>();
		int i=0;
		for(String act:singleAct){
			ArrayList<AppNode> appList= new ArrayList<AppNode>();
			for(AppNode app:decisionList){
				if(locationList.get(i).equals(app.location)){
					appList.add(app);
				}
			}
			actAppList.put(act, appList);
		i+=1;
		}

//		/*remove appliance not "on" status with act*/
//		actAppList=removeNotOnApp(actAppList,gaInference);
		
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
	public String getActLocation(String act){
		Set<String> location_actSet = EnvStructure.actAppList.keySet(); //record all activity
		for(String location_act:location_actSet){
			if(location_act.contains(act)){
				String location=location_act.split("_")[0];
				return location;
			}
		}
		return null;
		
	}
	
//	public Map<String, ArrayList<AppNode>> getActAppList2( Set<String> singleAct, ArrayList<String> locationList,ArrayList<AppNode>decisionList ){
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
	

	
	public ArrayList<Double>  getIllList(Map<String, ArrayList<AppNode>> actAppList){
		
		ArrayList<Double> illList= new ArrayList<Double>();
		Set<String> keySet= actAppList.keySet();
		for(String str:keySet){
			String location= getActLocation(str);
			VisualComfortTable vct= EnvStructure.visualComfortTableList.get(location);
			 ArrayList<AppNode> appList=actAppList.get(str);
			//燈光的搭配組合取得lux
			double lux=vct.getLuxFromTable(appList);
			//get real lux level
			double realLuxLevel=LuxLevel.transformLuxLevel(lux);			
			//get ideal lux level
			double idealLuxLevel=IdealLuxLevel.getIdealLuxLevel(str);
			double ILL= (realLuxLevel-idealLuxLevel)/3;
			illList.add(ILL);
		}

		

		return illList;
	}
	
	public ArrayList<Double> getComfortArray(ArrayList<AppNode> decisionList,GAinference gaInference){
		
//		/*getVisualApp*/
//		ArrayList<AppNode> visualAppList=getVisualApp(decisionList);
		/*  act-app List  by location*/
		Map<String, ArrayList<AppNode>> actAppList=getActAppList(decisionList,gaInference);
		
		/*cal ILL for each act-app*/
		ArrayList<Double> illList=getIllList(actAppList);
		/*return comfort array*/
		return illList;
		
	}
	
	/*optimization*/
	public ArrayList <AppNode> getOptVisualList(ArrayList<AppNode> eusList,GAinference gaInference){
		/*get thermal List*/
		ArrayList<AppNode> visualAppList= new ArrayList<AppNode>();
		ArrayList<AppNode> visualRawList= new ArrayList<AppNode> ();
		for(AppNode app:eusList){
			if(app.agentName.equals("visual")){
				AppNode app2= app.copyAppNode(app);
				visualAppList.add(app2);
				AppNode app3= app.copyAppNode(app);
				visualRawList.add(app3);
			}
		}
		
		/*visual App list有可能是空的*/
		if(visualAppList.size()==0){

			
			
			return new ArrayList<AppNode>();
		}else {
			
			/*從eusList中和thermal有關的電氣，找出所有的狀態排列組合*/
			Optimizer op=new Optimizer();
			ArrayList<String> candidateList=op.buildCandidateList(visualAppList);
			
	
			int iterateCounter=0;
			ArrayList<AppNode> bestAnswer=null;
			while(bestAnswer==null || bestAnswer.size()==0){
				/*for debug*/
				if(bestAnswer!=null && bestAnswer.size()==0){
					int aa=0;
					aa++;
				}
				bestAnswer=visualIterate(candidateList, visualAppList,visualRawList,gaInference,iterateCounter);
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
//		for(Map.Entry<String, Integer> entry:listData){
//			System.out.println(entry.getKey()+" "+inferPriorityList.get(entry.getKey()));
//		}
		
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
	
	private ArrayList<AppNode>visualIterate(ArrayList<String> candidateList,ArrayList<AppNode> visualAppList,ArrayList<AppNode> visualRawList,GAinference gaInference,int counter){
		
		/*原始環境的pmv和power consumption*/
		//ThermalAgent ta=new ThermalAgent();
		ArrayList<Double> rawIllList= getComfortArray(visualRawList, gaInference);
		double rawAmp= Optimizer.calEnergyConsumption(visualRawList);
		
		

		
		/*best answer*/
		VisualSolution vs= new VisualSolution();

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
			ExpRecorder.exp.setVisualConflictCount();
			/*exp record end*/
		}


		
		/*iterate*/
		for(String candidate:candidateList){
			Optimizer.updateState(visualAppList, candidate);
			
			ArrayList<Double> illList=getComfortArray(visualAppList,gaInference);
			double amp=Optimizer.calEnergyConsumption(visualAppList);
			/*判斷illList是否全部都在constraint內，*/
			boolean flag=true;
			for(int i=0;i<illList.size();i++){
				double ill=illList.get(i);
				double constraint=constraintList.get(i);
				if(Math.abs(ill)>constraint){
					flag=false;
					break;
				}
			}
			if(flag){
				vs=visualListEvaluation(visualAppList,illList,amp,vs,rawIllList,rawAmp);
			}else{
				
			}
		}
		

		
		return vs.solution;
	}
	
	private VisualSolution visualListEvaluation(ArrayList<AppNode> visualAppList,ArrayList<Double> illList, double amp,VisualSolution vs,ArrayList<Double> rawIllList,double rawAmp){
				if(vs.setFlag==false){
				
					vs.copy(visualAppList, illList, amp);
					vs.setFlag=true;
				}else{
					/*evaluate best answer的條件*/ 
					if(amp<vs.totalAmp){
						vs.copy(visualAppList, illList, amp);
					}
	            }
				
				return vs;


	}
	
	class VisualSolution{
		ArrayList<AppNode> solution= new ArrayList<AppNode>();
		ArrayList<Double> solutionILL=new ArrayList<Double>();
		double totalAmp=0;
		Boolean setFlag=false;
		public void copy(ArrayList<AppNode> visualAppList,ArrayList<Double> illList, double amp){
			solution.clear();
			solutionILL.clear();
			for(AppNode app:visualAppList){
				solution.add(app.copyAppNode(app));
			}
			for(Double pmv:illList){
				solutionILL.add(pmv);
			}
			totalAmp=amp;
		}
		
	}
}
