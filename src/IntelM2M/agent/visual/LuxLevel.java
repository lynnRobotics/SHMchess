package IntelM2M.agent.visual;


public class LuxLevel {
   private static int level;

   public LuxLevel(double lux) {
       transformLuxLevel(lux);
   }
/*old version*/
//   public static int transformLuxLevel(double lux) {
//       if(lux >=0 && lux <= 2)
//           level = 1;
//       else if( lux >= 2 && lux <= 5)
//           level = 2;
//       else if( lux >= 5 && lux <= 10)
//           level = 3;
//       else if( lux >= 10 && lux <= 20)
//           level = 4;
//       else if( lux >= 20 && lux <= 30)
//           level = 5;
//       else if(lux >= 30 && lux <= 50)
//           level = 6;
//       else if(lux >= 50 && lux <= 75)
//           level = 7;
//       else if(lux >= 75 && lux <= 100)
//           level = 8;
//       else if(lux >= 100 && lux <= 150)
//           level = 9;
//       else if(lux >= 150 && lux <= 200)
//           level = 10;
//       else if(lux >= 200 && lux <= 500)
//           level = 11;
//       else if(lux >= 500 && lux <= 750)
//           level = 12;
//       else if(lux >= 750 && lux <= 1000)
//           level = 13;
//       else if(lux >= 1000 && lux <= 1500)
//           level = 14;
//       else if(lux >= 1500 && lux <= 2000)
//           level = 15;
//       else if(lux >= 2000 && lux <= 3000)
//           level = 16;
//       else if(lux >= 3000 && lux <= 5000)
//           level = 17;
//       else if(lux >= 5000 && lux <= 7500)
//           level = 18;
//       else
//           level = 19;
//
//       return level;
//   }
   
   public static int transformLuxLevel(double lux) {
	   if(lux >=0 && lux <= 2.3)
           level = 1;
       else if( lux >= 0.9 && lux <= 5.5)
           level = 2;
       else if( lux >= 4.7 && lux <= 11)
           level = 3;
       else if( lux >= 9.5 && lux <= 21)
           level = 4;
       else if( lux >= 19 && lux <= 32)
           level = 5;
       else if(lux >= 29 && lux <= 52.5)
           level = 6;
       else if(lux >= 48 && lux <= 77.5)
           level = 7;
       else if(lux >= 72.5 && lux <= 105)
           level = 8;
       else if(lux >= 97.5 && lux <= 155)
           level = 9;
       else if(lux >= 142 && lux <= 210)
           level = 10;
       else if(lux >= 195 && lux <= 320)
           level = 11;
       else if(lux >= 290 && lux <= 525)
           level = 12;
       else if(lux >= 480 && lux <= 775)
           level = 13;
       else if(lux >= 725 && lux <= 1025)
           level = 14;
       else if(lux >= 975 && lux <= 2000)
           level = 15;
  

       return level;
   }

   public static int getLuxLevel() {
       return level;
   }
}
