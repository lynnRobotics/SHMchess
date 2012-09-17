package IntelM2M.esdse;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import IntelM2M.agent.ap.APAgent;
import IntelM2M.agent.thermal.ThermalAgent;
import IntelM2M.agent.visual.VisualAgent;
import IntelM2M.datastructure.AppNode;
import IntelM2M.datastructure.EnvStructure;
import IntelM2M.epcie.GAinference;
import IntelM2M.exp.ExpRecorder;

public class Optimizer {
	


	
	static public void updateState(ArrayList<AppNode> appList, String candidate){
		String [] stateArr=candidate.split(",");
		
		for(int i=0;i< stateArr.length; i++){
			String state=stateArr[i];
			appList.get(i).envContext=state;
		}
	}
	
	static public double calEnergyConsumption (ArrayList<AppNode> decionList){
		double amp=0;
		/*for debug*/
		
		/**/
		try {
			Map<String,AppNode> appList=EnvStructure.appList;

			
			for(AppNode app: decionList){
				
 				AppNode rawApp= appList.get(app.appName);
 				
 				if(!rawApp.ampere.containsKey(app.envContext)){
 					int aa=0;
 					aa++;
 				}else{
 					amp+= rawApp.ampere.get(app.envContext);
 					
 				}
 				
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
		return amp;
		
	}
	
	static public double calEnergyConsumptionForSimulator (ArrayList<AppNode> decionList){
		double amp=0;
		/*for debug*/
		int duration=ExpRecorder.exp.getDuration();
		/**/
		try {
			Map<String,AppNode> appList=EnvStructure.appList;

			
			for(AppNode app: decionList){
				
 				AppNode rawApp= appList.get(app.appName);
 				
 				if(!rawApp.ampere.containsKey(app.envContext)){
 					int aa=0;
 					aa++;
 				}else{
 					amp+= (rawApp.ampere.get(app.envContext)*110*duration*5/(60*1000));
 					
 				}
 				
				
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			
			e.printStackTrace();
		}
		
		return amp;
		
	}
	
	/*not finish*/
	  public ArrayList<String> buildCandidateList(ArrayList<AppNode> candidateAppList){
		/*判斷每個appliance有幾個state*/
		ArrayList<ArrayList<String>> applianceState=new ArrayList<ArrayList<String>>();
		for(AppNode app:candidateAppList){
			Set <String>stateSet=app.ampere.keySet();
			ArrayList <String>stateList = new ArrayList<String>();
			for(String str:stateSet){
				stateList.add(str);
			}
			applianceState.add(stateList);
		}
		/*然 後找出所有的排列組合*/
		ArrayList<String> candidateList=new ArrayList<String>();
		String []strArr= new String[applianceState.size()];

		
		iterative(0,applianceState,candidateList,strArr);

		return candidateList;
		
	}
	
	  private void iterative(int i,ArrayList<ArrayList<String>> applianceState,ArrayList<String> candidateList,String[] strArr){
		  /*agent 有可能沒有負責到任何電器*/
		  if(applianceState.size()==0){
			  return;
		  }else{
			  for(int j=0;j<applianceState.get(i).size();j++){
					strArr[i]=applianceState.get(i).get(j);
					if((i+1)<applianceState.size()){
						iterative(i+1,applianceState,candidateList,strArr);
					}else{
						String ans="";
						for(String str:strArr){
							ans+=str+",";
						}
						candidateList.add(ans);
					}
				}
		  }

	}
	

	public ArrayList<AppNode> getOptDecisionList(ArrayList<AppNode> eusList,GAinference gaInference){
		/*for debug*/
		int t=0,v=0,ap=0;
		for(AppNode app:eusList){
			if(app.agentName.equals("thermal")){
				t+=1;
			}else if(app.agentName.equals("visual")){
				v+=1;
			}else if(app.agentName.equals("ap")){
				ap+=1;
			}
		}
		
		/*thermal evaluation*/
		ThermalAgent ta=new ThermalAgent();
		ArrayList <AppNode>thermalList=ta.getOptThermalList(eusList,gaInference);
		/*visual evaluation*/
		VisualAgent va=new VisualAgent();
		ArrayList <AppNode>visualList=va.getOptVisualList(eusList,gaInference);
		/*ap evaluation*/
		APAgent apA= new APAgent();
		ArrayList <AppNode>apList= apA.getOptApList(eusList, gaInference);
		/*combine*/
		ArrayList<AppNode>decisionList=new ArrayList<AppNode>();
		decisionList.addAll(thermalList);
		decisionList.addAll(visualList);
		decisionList.addAll(apList);
		
		

		
		/*for experiment record total consumption*/

		/*exp record end*/
		return decisionList;
	}

}
