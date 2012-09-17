package IntelM2M.mchess;

import s2h.platform.annotation.MessageFrom;
import s2h.platform.annotation.UPnP;
import s2h.platform.node.LogicNode;
import s2h.platform.node.NodeRunner;
import s2h.platform.node.PlatformMessage;
import s2h.platform.node.PlatformTopic;
import IntelM2M.epcie.Epcie;
import IntelM2M.esdse.Esdse;
import IntelM2M.test.SimulatorTest;

/**
 * MCHESS
 * @author Mao  (2012.06)
 */

@MessageFrom(PlatformTopic.RAW_DATA)
@UPnP
public class Mchess extends LogicNode{

	Epcie epcie;
	Esdse esdse;

	
    public Mchess()
    {
      super();
      epcie= new Epcie();
      esdse= new Esdse();
      //sysProcForSimulator();
      realTimeSysProc();
    }
    
  
  public void sysProcForSimulator(){
	  	epcie.buildModel();
		
	  	/*simulate sensor data*/
		SimulatorTest test= new SimulatorTest();
		test.simulatorTesting(epcie,esdse); 
		
  }

    
    public void realTimeSysProc(){
    	epcie.buildModel();
    	
    }

	@SuppressWarnings("unchecked")
	protected void processMessage(PlatformMessage message)
	{
		//DBN.inference(message,getSender()); //old
		esdse.processForRealTime(epcie, message, getSender());
	}

	public static void main(String[] args)
    {
		new NodeRunner(Mchess.class).execute();
    }
}
