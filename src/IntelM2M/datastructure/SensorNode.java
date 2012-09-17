package IntelM2M.datastructure;

public class SensorNode {
  
    	public String name;
    	public String type;
    	public double[] threshold;
    	public String[] status;
    	public String id;
    	public String dicreteValue;
    	public double rawValue;
    	//int count;
    	//double accumulated_watt;
    	
    	public SensorNode(String name, String type, double[] threshold, String[] status)
    	{
    		this.name = name;
    		this.type = type;
    		this.threshold = threshold.clone();
    		this.status = status.clone();
    		//this.count = 0;
    		//this.accumulated_watt = 0.0;
    	}

    	public SensorNode(String name, String discreteValue){
    		this.name=name;
    		this.dicreteValue=discreteValue;
    	}
    	public SensorNode(){
    		
    	}
    
}
