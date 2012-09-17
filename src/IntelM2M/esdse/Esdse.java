package IntelM2M.esdse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import s2h.platform.node.PlatformMessage;
import s2h.platform.node.Sendable;

import IntelM2M.datastructure.AppNode;
import IntelM2M.datastructure.EnvStructure;
import IntelM2M.datastructure.RelationTable;
import IntelM2M.epcie.Epcie;
import IntelM2M.epcie.GAinference;
import IntelM2M.epcie.erc.GaEscGenerator;
import IntelM2M.exp.ExpRecorder;
import IntelM2M.preference.PreferenceAgent;
import IntelM2M.preference.PreferenceAgent.PreferenceModel;
import IntelM2M.test.SimulatorTest;
/**
 * 
 * @author Mao  (2012.06)
 */
public class Esdse {
	
	private PreferenceAgent pr= new PreferenceAgent();
	
	private ArrayList<AppNode> eusAggregation(GAinference gaInference,String envContext){

		
		ArrayList<String> gaInferResultList=gaInference.gaInferResultList;
		/*get eus*/
		ArrayList<AppNode> eusAggregationList= new ArrayList<AppNode>();
		/*infer 有可能沒有東西*/
		if(gaInferResultList.size()==0){
			/*copy app from appList*/
			Map<String,AppNode> appList=EnvStructure.appList;
			Set<String> keySet= appList.keySet();
			for(String str:keySet){
				AppNode app= appList.get(str);
				AppNode newApp=app.copyAppNode(app);
				eusAggregationList.add(newApp);
			}
			
			
			//update EUS from sensorReading
			Map<String, ArrayList<String>> sensorStatus=EnvStructure.sensorStatus;
			String [] sensorName=(String[])sensorStatus.keySet().toArray(new String[0]);
			String[] split = envContext.split("#");
			String []sensorContext=split[0].split(" ");
			
			

			
		}else{
			for(String str:gaInferResultList){
				for(GaEscGenerator gaEsc: gaInference.GaEscList){
					boolean containKey=gaEsc.actAppList.containsKey(str);
					if(containKey){
						ArrayList<AppNode> tmpList=gaEsc.actAppList.get(str).appList;
						for(AppNode tmp:tmpList){
							AppNode app=tmp.copyAppNode(tmp);

							int same=-1;
							for(int i=0;i<eusAggregationList.size();i++){
								if(eusAggregationList.get(i).appName.equals(app.appName)){
									same=i;
								}
							}
							
							if(same<0){
								eusAggregationList.add(app);
							}else{
								int newPriority=getPriority(app);
								int oldPriority=getPriority(eusAggregationList.get(same));
								if(newPriority<oldPriority){
									eusAggregationList.get(same).state=app.state;
									eusAggregationList.get(same).escType=app.escType;
									eusAggregationList.get(same).confidence=app.confidence;
								}
							}
							
						}
					}
				}
			}
		} 
		
		
		//update EUS from sensorReading
		Map<String, ArrayList<String>> sensorStatus=EnvStructure.sensorStatus;
		String [] sensorName=(String[])sensorStatus.keySet().toArray(new String[0]);
		String[] split = envContext.split("#");
		String []sensorContext=split[0].split(" ");
		
		
		for(AppNode eus:eusAggregationList){
			for(int i=0;i<sensorName.length;i++){
				if(eus.appName.equals(sensorName[i])){
					eus.envContext=sensorContext[i];
				}
			}
		}
		
		return eusAggregationList;
	}
	

	
	private int getPriority(AppNode app){
		if(app.state.equals("on") && app.escType.equals("explicit")){
			return 1;
		}else if(app.state.equals("on") && app.escType.equals("implicit")){
			return 2;
		}else if(app.state.equals("standby") && app.escType.equals("explicit")){
			return 3;
		}else if(app.state.equals("standby") && app.escType.equals("implicit")){
			return 4;
		}else if(app.state.equals("off") && app.escType.equals("explicit")){
			return 5;
		}else if(app.state.equals("off") && app.escType.equals("implicit")){
			return 6;
		}
		return 7;
	}
	private void eusDispatch(ArrayList<AppNode> eusList,GAinference gaInference){
		/*get single activity set*/
		Set <String> actInferResultSet= gaInference.actInferResultSet;

		/*get act location list*/
		Set <String> actLocation= new HashSet<String>();
		
		Map<String, RelationTable> actAppList= EnvStructure.actAppList;
		Set<String> actRoomSet=actAppList.keySet();
		
		for(String str:actInferResultSet){
			for(String str2:actRoomSet){
				if(str2.contains(str)){
					String location= str2.split("_")[0];
					actLocation.add(location);
				}
			}
		}
		//
          for(AppNode app:eusList){
			/* 設定冷氣
			 * 這行可能有bug
			 * 不在家的活動要拿掉冷氣:GoOut
			 * */ 
			if(app.comfortType.equals("thermal") && app.global && !actInferResultSet.contains("GoOut")){
				app.agentName="thermal";
			}
			
			else if(app.state.equals("on") && app.comfortType.equals("visual")){
				for(String str:actLocation){
					if(app.location.equals(str) ){
						app.agentName="visual";
					}
				}
			}else if(app.state.equals("on") && app.comfortType.equals("thermal")){
				for(String str:actLocation){
					if(app.location.equals(str)){
						app.agentName="thermal";
					}
				}
			}else{
				app.agentName="ap";
			}
		}
	}
	

	

	
	public void processForSimulator(Epcie epcie,String read,String read2,String updatedRead){
		
		
		/*Infer GA*/
		String processRead=SimulatorTest.rawDataPreprocessing(updatedRead);
		epcie.gaInferenceForSimulator(processRead);
		
		
		/*1. EUS aggregation*/

		ArrayList<AppNode> eusList=eusAggregation(epcie.gaInference,read);/*這邊怪怪，我沒有用process的read function裡面自己有process*/
		ArrayList<AppNode> decisionList=null;
		/*infer 有可能沒有結果*/
		if(epcie.gaInference.gaInferResultList.size()==0){
			decisionList=eusList;
		}else{
			
			/*2. EUS dispatch*/

			eusDispatch(eusList,epcie.gaInference);/*update eusList*/
					
			/*3 optimization*/
			Optimizer op= new Optimizer();
			decisionList= op.getOptDecisionList(eusList, epcie.gaInference);
		}

		
		/*update decision for lastRead*/
		/*todo2 :check if error*/
		SimulatorTest.updateDecisionForMchess(decisionList);
		SimulatorTest.setLastDataForMchess(read2,decisionList);
		

		
		/*5 record result*/
		ExpRecorder.exp.processEXP(read, read2, epcie, decisionList, eusList);

		
		
	}
	

	
	
	public void processForSimulatorWithPR_new(Epcie epcie,String read,String read2,String updatedRead){
		
		/*更新READ的資料*/
		/* 我上次"關掉的電器中"，如果"確認是上次的noise"，且也是"這次的noise"，
		 那我就直接重這次的noise中刪除這些電器，且加入我的turn off 陣列中*/
		
		
		/*Infer GA*/

		
		String processRead=SimulatorTest.rawDataPreprocessing(updatedRead); //去掉電器的狀態，例如on_19後面的_19會被去掉
		epcie.gaInferenceForSimulator(processRead);
		
		/*1. EUS aggregation*/
		ArrayList<AppNode> eusList=null;
		ArrayList<AppNode> decisionList=null;
		
		/*infer 結果可能為空*/
		if(epcie.gaInference.gaInferResultList.size()==0){
		   eusList=eusAggregation(epcie.gaInference,read);
		   decisionList=eusList;
		}else if(pr.inferPRmodel(epcie.gaInference)==null){
			/*1.EUS aggregation*/
			eusList=eusAggregation(epcie.gaInference,read);
			/*2. EUS dispatch*/
			eusDispatch(eusList,epcie.gaInference);/*update eusList*/
			/*3 optimization*/
			Optimizer op= new Optimizer();
			decisionList= op.getOptDecisionList(eusList, epcie.gaInference);
			/*這邊可能有錯，檢查decisionList和gaInference是否有被update*/	
			/*todo4 : chek if error*/
			pr.buildPRModel(decisionList,epcie.gaInference);
		}else{
			PreferenceModel prM=pr.inferPRmodel(epcie.gaInference);
			pr.updateInferResult(epcie.gaInference, prM);
			/*1.EUS aggregation*/
			eusList=eusAggregation(epcie.gaInference,read);
			/*2. EUS dispatch*/
			eusDispatch(eusList,epcie.gaInference);/*update eusList*/
			/*3 optimization*/
			Optimizer op= new Optimizer();
			decisionList= op.getOptDecisionList(eusList, epcie.gaInference);
		}
		

		
		/*update decision for lastRead*/
		/*todo2 :check if error*/
		SimulatorTest.updateDecisionForMchess(decisionList);
		SimulatorTest.setLastDataForMchess(read2,decisionList);

		
		/*5 record result*/
		ExpRecorder.exp.processEXP(read, read2, epcie, decisionList, eusList);
		
		/*update prModel with feedback*/
		pr.userFeedback_new(epcie.gaInference,read, read2);
		
		
	}
	
public void processForRealTime(Epcie epcie, PlatformMessage message, Sendable sender){
		
		/*更新READ的資料*/
		/* 我上次"關掉的電器中"，如果"確認是上次的noise"，且也是"這次的noise"，
		 那我就直接重這次的noise中刪除這些電器，且加入我的turn off 陣列中*/
		
		
		/*Infer GA*/

		
		//String processRead=SimulatorTest.rawDataPreprocessing(updatedRead); //去掉電器的狀態，例如on_19後面的_19會被去掉
		epcie.gaInferenceForRealTime(message, sender);
		
		/*1. EUS aggregation*/
		ArrayList<AppNode> eusList=null;
		ArrayList<AppNode> decisionList=null;
		
		/*infer 結果可能為空*/
//		if(epcie.gaInference.gaInferResultList.size()==0){
//		   eusList=eusAggregation(epcie.gaInference,read);
//		   decisionList=eusList;
//		}else if(pr.inferPRmodel(epcie.gaInference)==null){
//			/*1.EUS aggregation*/
//			eusList=eusAggregation(epcie.gaInference,read);
//			/*2. EUS dispatch*/
//			eusDispatch(eusList,epcie.gaInference);/*update eusList*/
//			/*3 optimization*/
//			Optimizer op= new Optimizer();
//			decisionList= op.getOptDecisionList(eusList, epcie.gaInference);
//			/*這邊可能有錯，檢查decisionList和gaInference是否有被update*/	
//			/*todo4 : chek if error*/
//			pr.buildPRModel(decisionList,epcie.gaInference);
//		}else{
//			PreferenceModel prM=pr.inferPRmodel(epcie.gaInference);
//			pr.updateInferResult(epcie.gaInference, prM);
//			/*1.EUS aggregation*/
//			eusList=eusAggregation(epcie.gaInference,read);
//			/*2. EUS dispatch*/
//			eusDispatch(eusList,epcie.gaInference);/*update eusList*/
//			/*3 optimization*/
//			Optimizer op= new Optimizer();
//			decisionList= op.getOptDecisionList(eusList, epcie.gaInference);
//		}
		

		
		/*update decision for lastRead*/
		/*todo2 :check if error*/
		//SimulatorTest.updateDecisionForMchess(decisionList);
		//SimulatorTest.setLastDataForMchess(read2,decisionList);

		
		/*5 record result*/
		//ExpRecorder.exp.processEXP(read, read2, epcie, decisionList, eusList);
		
		/*update prModel with feedback*/
		//pr.userFeedback_new(epcie.gaInference,read, read2);
		
		
	}

}
