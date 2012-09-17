package IntelM2M.algo;

import java.io.FileWriter;
import java.util.ArrayList;

import IntelM2M.epcie.GaGenerator;

public class Prior {

    static final double WatchingTVThreshold=0.1;
    static final double ComeBackThreshold=0.1;
    static final double ChattingThreshold=0.1;
	 static  public ArrayList<String>  priorForInference( ArrayList<String> rawFromDBN , int humanNumber){		
			/*Prior Knowledge: �����v�en��������*/
			while(rawFromDBN.size()>humanNumber){
				int lowestIndex=0;
				double preProb=1;
				for(int i=0; i<rawFromDBN.size();i++){
					String []str=rawFromDBN.get(i).split(" ");
					double prob= Double.parseDouble(str[1]);
					if(prob<preProb){
						lowestIndex=i;
						preProb=prob;
					}
				}
				rawFromDBN.remove(lowestIndex);
			}
				
			
			return rawFromDBN;		
	   }
	 
	 static  public ArrayList<String>  priorForInferenceGA( ArrayList<String> rawFromDBN , int humanNumber,GaGenerator GA){		
			/*Prior Knowledge: �����v�en��������*/
			while(rawFromDBN.size()>humanNumber){
				int lowestIndex=0;
				double preProb=1;
				for(int i=0; i<rawFromDBN.size();i++){
					String []str=rawFromDBN.get(i).split(" ");
					double prob= Double.parseDouble(str[1]);
					if(prob<preProb){
						lowestIndex=i;
						preProb=prob;
					}
				}
				rawFromDBN.remove(lowestIndex);
			}
			
			/*�p�G�uinfer �X all Sleeping�P��L���� �N�אּ  Sleeping*/	
//			Boolean haveAllSleep=false;
//			Boolean haveOther=false;
//			int allSleepIndex=0;
//			for(int i=0;i<rawFromDBN.size();i++){
//				String []str=rawFromDBN.get(i).split(" ");
//				ArrayList<String> memberList=GA.getGroupMember(str[0]);
//				for(String str2:memberList){
//					if(str2.equals("AllSleeping") ){
//						haveAllSleep=true;
//						allSleepIndex=i;
//					}
//
//				}		
//			}
//			if(rawFromDBN.size()>1){
//				haveOther=true;
//			}
//			if(haveAllSleep && haveOther){
//				rawFromDBN.remove(allSleepIndex);
//				
//				String str=GA.getGID("Sleeping").get(0);
//				rawFromDBN.add(str+" "+0.00);
//				
//			}
			
			return rawFromDBN;		
	   }
	 
	    public ArrayList<String>  priorForInference2( ArrayList<String> rawFromDBN, int humanNumber){
			
			for(int i=0;i < rawFromDBN.size();i++){
				String []str=rawFromDBN.get(i).split(" ");
				/*Prior Knowledge: �h�����|�P�ɵo�ͪ����� ex: GoOut*/
				if(str[0].equals("GoOut") && rawFromDBN.size()>1){
					rawFromDBN.remove(i);
				}
				/*Prior Knowledge: �h��threshold�L�C������*/
				double prob= Double.parseDouble(str[1]);
				if(str[0].equals("WatchingTV") && prob<WatchingTVThreshold){
					rawFromDBN.remove(i);
				}
				else if(str[0].equals("ComeBack") && prob<ComeBackThreshold){
					rawFromDBN.remove(i);
				}
				else if(str[0].equals("Chatting") && prob<ChattingThreshold){
					rawFromDBN.remove(i);
				}
				
			}		
			/*play Kinect watchTV ���@��*/
			if(rawFromDBN.size()>=2){
				boolean watchTV=false;
				boolean playKinect=false;
				int watchTVIndex=0;
				for(int i=0;i<rawFromDBN.size();i++){
					String []str=rawFromDBN.get(i).split(" ");
					if(str[0].equals("WatchingTV")){
						watchTV=true;
						watchTVIndex=i;
					}
					if(str[0].equals("PlayingKinect")){
						playKinect=true;
					}
				}
				if(watchTV && playKinect){
					rawFromDBN.remove(watchTVIndex);
				}
			}
			/*�p�G�uinfer �X sleeping �� all Sleeping �N���� all Sleeping*/
//			Boolean haveSleep=false;
//			Boolean haveOther=false;
//			int sleepIndex=0;
//			for(int i=0;i<rawFromDBN.size();i++){
//				String []str=rawFromDBN.get(i).split(" ");
//				if(str[0].equals("Sleeping") ){
//					haveSleep=true;
//					sleepIndex=i;
//				}
//				else if(str[0].equals("AllSleeping")){
//					haveSleep=true;
//					
//				}
//				else{
//					haveOther=true;
//				}			
//			}
//			if(haveSleep && !haveOther){
//				rawFromDBN.remove(sleepIndex);
//				
//			}

			/*Prior Knowledge: �����v�en��������*/
			while(rawFromDBN.size()>humanNumber){
				int lowestIndex=0;
				double preProb=1;
				for(int i=0; i<rawFromDBN.size();i++){
					String []str=rawFromDBN.get(i).split(" ");
					double prob= Double.parseDouble(str[1]);
					if(prob<preProb){
						lowestIndex=i;
						preProb=prob;
					}
				}
				rawFromDBN.remove(lowestIndex);
			}
				
			
			return rawFromDBN;
	    }
	    
	    static public void priorForTrainingData(String rawData,String activity,FileWriter writer){
	    	
			try {
				String[] split = rawData.split("#");
				String[] split2 = split[1].split(" ");
				Boolean thisAct = false;
				for (String str : split2) {
					if (str.equals(activity))
						thisAct = true;
				}
				
				if (thisAct == true) {
					writer.write(split[0] + activity + "\r\n");
				}
//				else if (activity.equals("WatchingTV")&& split[1].contains("PlayingKinect")) {
//					/*Do Nothing*/
//					return;
//				} 
				else {
					writer.write(split[0] + "OtherActivity\r\n");
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
	    }
	    
	    static public void priorForTrainingData(String rawData,String activity,FileWriter writer,GaGenerator GA){
	    	
			try {
				String[] split = rawData.split("#");
				String[] split2 = split[1].split(" ");
				Boolean thisAct = false;
				for (String str : split2) {
					if (str.equals(activity))
						thisAct = true;
				}
				if (thisAct == true) {
					writer.write(split[0] + activity + "\r\n");
					
					if(GA.gaList.get(activity).actMemberList.contains("WatchingTV")){
						for(int i=0;i<7;i++){
							writer.write(split[0] + activity + "\r\n");
						}
					}
					else	if(GA.gaList.get(activity).actMemberList.contains("GoOut")){
						for(int i=0;i<8;i++){
							writer.write(split[0] + activity + "\r\n");
						}
					}
					else	if(GA.gaList.get(activity).actMemberList.contains("Sleeping")){
						for(int i=0;i<3;i++){
							writer.write(split[0] + activity + "\r\n");
						}
					} 
					
					return;
				}
//				else if (GA.gaList.get(activity).actMemberList.contains("WatchingTV")) {
//					for(String str:split2){
//						if(GA.gaList.get(str).actMemberList.contains("PlayingKinect")){
//							/*Do Nothing*/
//							return;
//						}
//					}
//				} 
				
				writer.write(split[0] + "OtherActivity\r\n");
				
			} catch (Exception e) {
				// TODO: handle exception
			}
	    }
}