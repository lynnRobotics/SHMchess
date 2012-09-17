package IntelM2M.epcie;

import java.util.ArrayList;

import s2h.platform.node.PlatformMessage;
import s2h.platform.node.Sendable;

import IntelM2M.epcie.classifier.GaDbnClassifier;
import IntelM2M.epcie.erc.GaEscGenerator;
import IntelM2M.func.CrossValidate;
import IntelM2M.func.text2Arff;
import IntelM2M.test.SimulatorTest;

/**
 * EPCIE
 * @author Mao  (2012.06)
 */

public class Epcie {


		public ArrayList<GaGenerator> GaGeneratorList;
		public ArrayList<GaDbnClassifier> GaDbnList ;	
		public ArrayList<GaEscGenerator> GaEscList ;
		
		public GAinference gaInference;

		static final Boolean retrain=false;
		static final int trainLevel=5;
	  
	  /*simulator*/
	   String rawTrainingDataPath= ".\\_input_data\\simulator\\simulator_trainingdata3.txt";
		//String rawTrainingDataPath= ".\\_input_data\\simulator\\simulator_trainingdata4.txt";
	   
	   public Epcie()
	    {
			 GaGeneratorList=new ArrayList<GaGenerator>(); /*record GA¡@structure*/
			 GaDbnList = new ArrayList<GaDbnClassifier>(); /*record DBN for each GA*/
			 GaEscList = new ArrayList<GaEscGenerator>();  /*record ERC for each GA*/
	    }
	   public void buildModel(){

		   
		   
		    for(int i=1;i<=trainLevel;i++){
		    	/*build i layer GA*/
			    GaGenerator GA= new GaGenerator(i);/*multiple GA at layer i*/
	
			    if(i==1){
			    	GA.buildFirstGaList();
			    }
			    else 
		    	{
			    	Boolean flag=GA.buildHGA(GaDbnList.get(i-2),GaGeneratorList.get(i-2),GaEscList.get(i-2),retrain);
			    	if(!flag){
			    		break;
			    	}
			    	         	
		    	}
			    /*convert training data for GA at layer i*/
			    text2Arff.convertGaRawToArff(GA, rawTrainingDataPath);
			    
		    	/*build GA model*/
			    GaDbnClassifier GaDBN = new GaDbnClassifier();
				GaDBN.buildGaModel(GA,retrain);
				
				/*build GA¡@ERC¡@model*/
				GaEscGenerator GAESC=new GaEscGenerator(GA,retrain);
				if(i==1)
					GAESC.buildAllESC(GaDBN.classifier,  ".\\_output_results\\ESC\\_ga_esc_"+i+".txt", GA, null, null);
				else
					GAESC.buildAllESC(GaDBN.classifier,  ".\\_output_results\\ESC\\_ga_esc_"+i+".txt", GA, GaGeneratorList.get(0), GaEscList.get(0));
				
				GaDBN.allSetDefaultValue(GA);
				
				/*record ERC, ith HGA, classifier */
				GaGeneratorList.add(GA);
				GaDbnList.add(GaDBN);
				GaEscList.add(GAESC);

		    }

		    
		    /*¼g¥X hgaµ²ºc*/
			GaGenerator.writeHGA (".\\_output_results\\hga.txt", GaGeneratorList);

	   }
	   
	   public void gaInferenceForSimulator(String read){
			/*Infer GA*/
			gaInference= new GAinference(GaGeneratorList,GaDbnList,GaEscList,read);
			gaInference.buildInferResult();
			
	   }
	   
	   public void gaInferenceForRealTime(PlatformMessage message, Sendable sender){
			/*Infer GA*/
			gaInference= new GAinference(GaGeneratorList,GaDbnList,GaEscList);
			gaInference.buildInferResultForRealTime(message, sender);
			
	   }

}
