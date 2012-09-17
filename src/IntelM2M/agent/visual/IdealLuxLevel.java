package IntelM2M.agent.visual;

public class IdealLuxLevel {

	
	/*old version*/
//	   public static double getIdealLuxLevel(String activity) {
//	       double level=0;
//		   if(activity.equals("ComeBack"))
//	           level = 6.5;
//	       else if(activity.equals("GoOut"))
//	           level = 6.5;
//	       else if(activity.equals("Sleeping"))
//	           level = 2.5;
//	       else if(activity.equals("WatchingTV"))
//	           level = 10.5;
//	       else if(activity.equals("TakingBath"))
//	           level = 11;
//	       else if(activity.equals("UsingPC"))
//	           level = 11.5;
//	       else if(activity.equals("Laundering"))
//	           level = 10.5;
//	       else if(activity.equals("ReadingBook"))
//	           level = 12.5;
//	       else if(activity.equals("Cleaning"))
//	           level = 11;
//	       else if(activity.equals("Cooking"))
//	           level = 11;
//	       else if(activity.equals("PlayingKinect"))
//	           level = 11.5;
//	       else if(activity.equals("Chatting"))
//	           level = 11.5;
//	       else if(activity.equals("Studying"))
//	           level = 12.5;
//	       else if(activity.equals("UsingRestroom"))
//	           level = 11;
//	       else if(activity.equals("ListeningMusic"))
//	           level = 11;
//	       else if(activity.equals("BrushingTooth"))
//	           level = 11;
//	       else if(activity.equals("WashingDishes"))
//	           level = 11;
//
//
//	       return level;
//	   }
	   

	   public static double getIdealLuxLevel(String activity) {
	       double level=0;
		   if(activity.equals("ComeBack"))
	           level = 7;
	       else if(activity.equals("GoOut"))
	           level = 7;
	       else if(activity.equals("Sleeping"))
	           level = 1;
	       else if(activity.equals("WatchingTV"))
	           level = 11;
	       else if(activity.equals("TakingBath"))
	           level = 12;
	       else if(activity.equals("UsingPC"))
	           level = 12.5;
	       else if(activity.equals("Laundering"))
	           level = 11;
	       else if(activity.equals("ReadingBook"))
	           level = 14.5;
	       else if(activity.equals("Cleaning"))
	           level = 12;
	       else if(activity.equals("Cooking"))
	           level = 12;
	       else if(activity.equals("PlayingKinect"))
	           level = 11;
	       else if(activity.equals("Chatting"))
	           level = 11;
	       else if(activity.equals("Studying"))
	           level = 14.5;
	       else if(activity.equals("UsingRestroom"))
	           level = 12;
	       else if(activity.equals("ListeningMusic"))
	           level = 12;
	       else if(activity.equals("BrushingTooth"))
	           level = 12;
	       else if(activity.equals("WashingDishes"))
	           level = 12;


	       return level;
	   }
}
