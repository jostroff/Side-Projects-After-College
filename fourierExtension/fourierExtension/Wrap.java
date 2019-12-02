package fourierExtension;
import java.util.*;

//This is the wrap class for the generalized sin(), cos() "wrapping" functions.

public class Wrap {
	//Given as radians and points in an x-y plane.
	private static final double pi = Math.PI;
	
	/**
	 * cos_sin() - return cos() or sin(), as given by String cos_or_sin = "sin" or "cos" as an argument.
	 * @angle - angle given in radians
	 * @points - points given as list of arrays, with 0 representing x coord, 1 the y coord.
	 * 
	 * Note that sin(), cos() are one function, to avoid redundancy for reused code.
	 * First, this function takes in a list of points given by the user clicks. Then it finds out where in
	 * 		the list of points its rate of change should fall to allow for changing coordinates along a polar
	 * 		coordinate system. Finally it returns the x or y coordinate based on this rate of change as well
	 * 		as the angle itself.
	 */
	public static double cos_sin(String cos_or_sin, double angle, ArrayList<int[]> points){
		if (points.size() == 0) return 0;
		//These are the angles at which the function changes to a different value per rate of change.
		angle = angle % (2 * pi);
		if (angle < 0)
			angle += 2*pi;
		double[] angleChanges = new double[points.size() - 1]; //size()
		double coordX = 0, coordY = 0;
		int currentPlace = 0; //This is the current place, within the coordinates, for angleChanges.
		int wrapChange = 0; //This is where point switches from quad IV to quad I, or where the angle wraps.
		for (int i = 0; i < points.size() - 1; i++){ //size()
			int[] point = points.get(i);
			//These are the actual points away from the center.
			int actualX = point[0] - 640, actualY = 386 - point[1];
			double pointAng;
			if (actualX != 0)
				pointAng = (double) actualY / actualX; //Note, sometimes negative.
			else
				pointAng = (double) Integer.MAX_VALUE;
			if (actualX >= 0 && actualY >= 0)
				angleChanges[i] = (pointAng != (double) Integer.MAX_VALUE ? Math.atan(pointAng) :
					(actualY > 0 ? pi/2 : -pi / 2)); //Center = (640, 386)
			else if (actualX < 0) //Works as intended.
				angleChanges[i] = pi + (pointAng != (double) Integer.MAX_VALUE ? Math.atan(pointAng) :
					(actualY > 0 ? pi/2 : -pi / 2));
			else
				angleChanges[i] = 2 * pi + (pointAng != (double) Integer.MAX_VALUE ? Math.atan(pointAng) :
					(actualY > 0 ? pi/2 : -pi / 2));
			
			//Get the current angle position in the array for seeing how to move through points.
			if (i != 0 && ((angleChanges[i - 1] < angle && angle <= angleChanges[i])
					|| (angleChanges[i - 1] > pi && angleChanges[i] < pi 
							&& angleChanges[i - 1] - 2 * pi < angle && angle < angleChanges[i])
					|| (angleChanges[i - 1] > pi && angleChanges[i] < pi 
							&& angleChanges[i - 1] < angle && angle - 2 * pi < angleChanges[i])
				))
				currentPlace = i;
			if (i != 0 && angleChanges[i - 1] > pi && angleChanges[i] < pi) {
				wrapChange = i;
			}
		}
		double velX, velY;
		if (currentPlace > 0 && cos_or_sin.equals("cos")){ //Note: Only care about distX because this is cos().
			double distX = points.get(currentPlace)[0] - points.get(currentPlace - 1)[0]; //size() - 1
			double distAngle = angleChanges[currentPlace] + (wrapChange == currentPlace ? 2 * pi : 0) - angleChanges[currentPlace - 1];
			velX = distX / distAngle;
			
			//These lines give the coordinate of the X variable, i.e. the end product of cos().
			coordX = points.get(currentPlace - 1)[0] - 640 + velX * 
						(angle + (angle < pi && wrapChange == currentPlace ? 2 * pi : 0) 
						- angleChanges[currentPlace - 1]);
		} else if (currentPlace == 0 && cos_or_sin.equals("cos")){
			double distX = points.get(0)[0] - points.get(points.size() - 2)[0]; //size() - 1
			double distAngle = angleChanges[0] + (wrapChange == 0 ? 2 * pi : 0) - angleChanges[points.size() - 2];
			velX = distX / distAngle;
			
			//These lines give the coordinate of the X variable, i.e. the end product of cos().
			coordX = points.get(points.size() - 2)[0] - 640 + velX * 
						(angle + (angle < pi && wrapChange == 0? 2 * pi : 0)
						- angleChanges[points.size() - 2]);
		}
		if (currentPlace > 0 && cos_or_sin.equals("sin")){ //Note: Only care about distY because this is sin().
			double distY = points.get(currentPlace)[1] - points.get(currentPlace - 1)[1]; //size() - 1
			double distAngle = angleChanges[currentPlace] + (wrapChange == currentPlace ? 2 * pi : 0) - angleChanges[currentPlace - 1];
			velY = distY / distAngle;

			//These lines give the coordinate of the Y variable, i.e. the end product of sin().
			coordY = 386 - points.get(currentPlace - 1)[1] - velY * 
						(angle + (angle < pi && wrapChange == currentPlace ? 2 * pi : 0) 
						- angleChanges[currentPlace - 1]);
		} else if (currentPlace == 0 && cos_or_sin.equals("sin")){
			double distY = points.get(0)[1] - points.get(points.size() - 2)[1]; //size() - 1
			double distAngle = angleChanges[0] + (wrapChange == 0 ? 2 * pi : 0) - angleChanges[points.size() - 2];
			velY = distY / distAngle;
			
			//These lines give the coordinate of the Y variable, i.e. the end product of sin().
			coordY = 386 - points.get(points.size() - 2)[1] - velY * 
						(angle + (angle < pi && wrapChange == 0 ? 2 * pi : 0)
						- angleChanges[points.size() - 2]);
		}
		//This gives back the X or Y coordinate, regardless of modifying to fit the unit circle.
		return cos_or_sin == "cos" ? coordX : coordY;
	}
	//These sin(), cos() functions are made so as to avoid inconvenience in case this is used elsewhere.
	public static double cos(double angle, ArrayList<int[]> points){ return cos_sin("cos", angle, points); }
	public static double sin(double angle, ArrayList<int[]> points){ return cos_sin("sin", angle, points); }
}
