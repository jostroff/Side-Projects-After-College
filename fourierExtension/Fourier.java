package fourierExtension;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;

public class Fourier extends JPanel implements KeyListener, MouseListener{
	private static final long serialVersionUID = 1L;
	double[] x1, y1, x2, y2, angle;
	ArrayList<int[]> points; //These are all points that exist for given shape, 1 iteration. This adds each time.
	ArrayList<Double>[] fourierPoints; //These are all points in total Fourier fractal.
	int num; //Iterations
	boolean connected, started, paused; //Connected = whether shape is complete, started = transformation started yet
	
	public Fourier(int n, int s){
		reset(n, s);
	}
	public Fourier(){
		reset(0, 0);
	}
	/**
	 * void reset() - resets the current shape to be empty of any points.
	 * @param n - iterations.
	 * @param s - number of shape sides. Although no longer relevant for this particular, more 
	 * 		generalized transformation project, I am leaving it in as the spline 
	 * Note: Although s holds no tangible relevance here, I
	 */
	public void reset(int n, int s){
		num = n;
//		shape = s;
		connected = false;
		started = false;
		paused = true;
		points = new ArrayList<int[]>(); //To add once clicked.
	}
	/**
	 * This will start the fourier transformation from sctrach. Note
	 * @param n
	 */
	public void startFourier(int n){
		num = n;
		x1 = new double[num]; //CENTER OF X
		y1 = new double[num]; //CENTER OF Y
		x2 = new double[num]; //TAIL OF X
		y2 = new double[num]; //TAIL OF Y
		angle = new double[num]; //ANGLE
		angle[0] = 0;
		fourierPoints = new ArrayList[2]; //Total of all points in Fourier transformation.
		fourierPoints[0] = new ArrayList<Double>();
		fourierPoints[1] = new ArrayList<Double>();
		for (int i = 1; i < num; i++){
			angle[i] = 90;
		}
		//The center point.
		x1[0] = 640;
		y1[0] = 386;
	}
	//SECOND'S PERIOD IS HALF OF FIRST
	public void go(){
		JFrame frame = new JFrame("Transform");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(this);
		frame.addKeyListener(this);
		frame.addMouseListener(this);
		frame.setSize(1280, 772);
		frame.setVisible(true);
		while (true) {
			if (!paused && connected) {
				for (int i = 0; i < num; i++)
					angle[i] += .1 * Math.pow(2, i);
				
				//Note: Could be 200 * Wrap if the function's range is [-1, 1].
				x2[0] = 640 + (Wrap.cos(Math.toRadians(angle[0]), points));		  //Took out 200 * Wrap...
				y2[0] = 386 - (Wrap.sin(Math.toRadians(angle[0]), points)); 	  //Took out 200 * Wrap... Made it +.
				for (int i = 1; i < num; i++){
					x1[i] = x2[i - 1];
					y1[i] = y2[i - 1];
					
					//Note: It could also be effective to take the max, min of sides and divide that way.
					x2[i] = x1[i] + (1.5 / (Math.pow(2, i)) * 			//Took out 200/Math.pow...
							Wrap.cos(Math.toRadians(angle[i]), points));
					y2[i] = y1[i] - (1.5 / (Math.pow(2, i)) * 			//Took out 200/Math.pow...
							Wrap.sin(Math.toRadians(angle[i]), points));
				}
				if (angle[0] >= -190){
					fourierPoints[0].add(x2[num - 1]);
					fourierPoints[1].add(y2[num - 1]);
				}
			}
			repaint();
			try {
				Thread.sleep(2); //In original, it was 2.
			} catch (InterruptedException e){}
		}
	}
	public void paintComponent(Graphics g2){
		Graphics2D g = (Graphics2D)g2;
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		g.setColor(Color.pink);
		if (!connected){
			if (points.size() == 0)
				g.fillRect(0, 0, 1280, 386);
			else {
				AffineTransform old = g.getTransform();
				int pointY = points.get(points.size() - 1)[1] - 386;
				int pointX = points.get(points.size() - 1)[0] - 640;
				double theta = Math.atan((double) pointY / pointX); //atan(y / x)
				theta = (pointX < 0) ? theta - Math.PI: theta; //This is the angle at which to rotate.
				
		        //Range from quad I - III: [0, 1.5 * pi] and IV: [0, .5 * pi]...
		        g.rotate(theta, 640, 386);
		        double firstTheta = Math.atan((double) (points.get(0)[1] - 386) / (points.get(0)[0] - 640));
		        firstTheta = (points.get(0)[0] < 0) ? firstTheta - Math.PI: firstTheta;
		        g.fillRect(-640, -386, 1280 + 1280, 772); //This is the big block we fill window with.
		        g.setTransform(old);
		        
		        // Code below applies to the following:
		        //	  												  Quads I-III		Quad I						Quad III
		        if (firstTheta > -Math.PI / 2 && firstTheta < 0 && !(theta < 0 && theta <= firstTheta && theta + Math.PI > firstTheta)){
		        	//Start is in Quad I.
		        	g.rotate(firstTheta, 640, 386);
		        	g.setColor(Color.WHITE);
		        	g.fillRect(-640, -386, 1280 + 1280, 772); //x, y, width, height
		        	g.setTransform(old);
		        	
		        } else if (firstTheta > 0 && firstTheta < Math.PI / 2 && 
		        		((theta <= Math.PI / 2 && theta > -Math.PI / 2 && theta < firstTheta) || //Quads III, IV
		        		 (theta <= -Math.PI / 2 && theta > -Math.PI && theta + Math.PI > firstTheta + .05))){ //Quad II. .05 is rounding error.
//		        	System.out.println(firstTheta + ", " + theta + "__");
		        	//Start is in Quad II. I know. This is complicated and relatively dumb..
		        	g.rotate(firstTheta, 640, 386);
		        	g.setColor(Color.WHITE);
		        	g.fillRect(-640, 386, 1280 + 1280, 772);
		        	g.setTransform(old);
		        }
			}
		}
        //Things drawn here will not be rotated
		g.setColor(Color.blue); //These are the blue axes that meet at middle of screen.
		g.drawLine(0, 386, 1280, 386); 
		g.drawLine(640, 0, 640, 772);
		
		
		g.setColor(Color.black);
		
		if (started){ //This is to DRAW the actual Fourier transform.
			for (int i = 0; i < num; i++){ //These points draw the motion change at an instant.
				g.drawLine((int)x1[i], (int)y1[i], (int)x2[i], (int)y2[i]); //Clock-like line ticker.
			} for (int i = 1; i < fourierPoints[0].size() - 1; i++){ //These points show the whole history of the shape made so far.
				g.drawLine(fourierPoints[0].get(i - 1).intValue(), fourierPoints[1].get(i - 1).intValue(),
						fourierPoints[0].get(i).intValue(), fourierPoints[1].get(i).intValue()); //Shape itself.
//				g.drawLine(fourierPoints[0].get(i).intValue(), fourierPoints[1].get(i).intValue(), 640, 386); //Filled-in line ticker.
			}
		}
		//Draw all lines and points of original shape, so long as original shape isn't connected yet.
		if (!started && points.size() != 0){ //&& !connected
			int posSize = 9; //"Recently clicked" box's position size.
			g.setColor(Color.RED);
			g.fillRect(points.get(0)[0] - posSize / 2, 
					points.get(0)[1] - posSize / 2, posSize, posSize);
			if (points.size() > 1){
				g.setColor(Color.GREEN);
				g.fillRect(points.get(points.size() - 1)[0] - posSize / 2, 
						points.get(points.size() - 1)[1] - posSize / 2, posSize, posSize);
				g.setColor(Color.BLACK);
				for (int i = 0; i < points.size() - 1; i++){
					g.drawLine(points.get(i)[0], points.get(i)[1], 
							points.get(i + 1)[0], points.get(i + 1)[1]);
				}
			}
		}
		//Writing on the side.
		Font font = new Font("Princetown LET", 100, 20);
		font = new Font("Serif", 100, 20);
		g.setColor(Color.BLACK);
		g.setFont(font);
		g.drawString("Sides: " + (points.size() - (connected ? 1 : 0)), 20, 40);
		g.drawString("Iterations: " + num, 20, 70);
		font = new Font("Serif", Font.ITALIC, 14);
		g.setFont(font);
	}
	/**
	 * keyPressed() - registers which keys were pressed.
	 * 		SPACE - Resets entire shape.
	 * 		UP - Increases iterations, unless at 25. Then it goes to 1.
	 * 		DOWN - Decreases iterations, unless at 1. Then it goes to 25.
	 * 		LEFT - Pause.
	 * 		RIGHT - Resume.
	 */
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (!connected && code != KeyEvent.VK_SPACE) return; //In case user presses key before supposed to.
		if (code == KeyEvent.VK_SPACE){
			num = 0;
			reset(0, 0);
		} else if (code == KeyEvent.VK_UP) {
			if (num == 25)
				num = 1;
			else
				num++;
			started = true;
			paused = false;
			startFourier(num); //Iterations, sides
		} else if (code == KeyEvent.VK_DOWN) {
			if (num == 1)
				num = 25;
			else
				num--;
			started = true;
			paused = false;
			startFourier(num); //Iterations, sides
		} else if (code == KeyEvent.VK_LEFT && connected) { //Rewritten to fit this model.
			paused = true;
			started = true;
		} else if (code == KeyEvent.VK_RIGHT && connected) { //Rewritten to fit this model.
			paused = false;
			started = true;
		}
	}
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void mouseClicked(MouseEvent e) { //Note that positive / negative Y are inverted.
		if (!connected) {
			int[] point = {e.getX(), e.getY() - 24};
			int pointX = point[0] - 640;
			int pointY = point[1] - 386;
			
			if (points.size() == 0 && pointY >= -386 && pointY < 0){
				//Quad I - II.
				points.add(point);
				return;
			}
			if (points.size() > 0){
				int prevX = points.get(points.size() - 1)[0] - 640;
				int prevY = -(points.get(points.size() - 1)[1] - 386);
				double slopePrev = (double) prevY / prevX; //What about divide by 0 error? TO FIX.
				int zeroX = points.get(0)[0] - 640;
				int zeroY = -(points.get(0)[1] - 386);
				pointY = -pointY;
				double slopeZero = (double) zeroY / zeroX;
				
				/* 
				 * These are the point additions for valid points. You might notice that for the non-connections,
				 * the bodies all only contain points.add(point). Of course then the multiple if statements aren't
				 * needed and might even seem redundant, but I believe that, for coding purposes, this organization
				 * is helpful for readability, both from my own perspective and for others whose mathematical 
				 * endeavors might be similar. Still, it might be hard to read. But it is definitely easier this way.
				 */
				
				//NOTE: Must include "= 0" for this!
				if (zeroX > 0 && zeroY > 0 &&
						((pointY >= pointX * slopeZero && prevX >= 0 && prevY >= 0 && slopePrev < slopeZero) //Quad I
						|| (pointY >= pointX * slopeZero && prevX <= 0 && prevY <= 0 && slopePrev > slopeZero) //Quad III
						|| (pointY >= pointX * slopeZero && prevX >= 0 && prevY <= 0) //Quad IV
						)){ //Connection for Quad I.
					points.add(points.get(0));
					connected = !connected;
					startFourier(1);
				} else if (zeroX < 0 && zeroY > 0 &&
						((pointY <= pointX * slopeZero && prevX <= 0 && prevY >= 0 && slopePrev < slopeZero) //Quad II
						|| (pointY <= pointX * slopeZero && prevX >= 0 && prevY <= 0 && slopePrev > slopeZero) //Quad IV
						|| (pointY <= pointX * slopeZero && prevX >= 0 && prevY >= 0) //Quad I
						)){ //Connection for Quad II.
					points.add(points.get(0)); //Here, we see that this ELSE IF is necessary for connecteds to come first.
					connected = !connected;	   //This is because without them, invalid point creations (not connections) would replace them.
					startFourier(1);
				} else if (((pointY >= pointX * slopePrev && prevX >= 0 && prevY >= 0 && slopePrev >= slopeZero) //Quad I before circle, below after
						|| (pointY >= pointX * slopePrev && pointY < pointX * slopeZero && prevX >= 0 && prevY >= 0 && slopePrev < slopeZero)
						|| (pointY <= pointX * slopePrev && pointY < pointX * slopeZero && prevX <= 0 && prevY <= 0) //Quad III
						|| (pointY >= pointX * slopePrev && pointY < pointX * slopeZero && prevX >= 0 && prevY <= 0) //Quad IV
						) && pointX >= 0 && pointY >= 0 && zeroX >= 0 && zeroY >= 0){ //Quad I if first in I.
					points.add(point);
				} else if (((pointY >= pointX * slopePrev && slopePrev > 0) || 
						(pointY <= pointX * slopePrev && slopePrev < 0))
						&& pointX < 0 && pointY > 0 && prevY > 0 && zeroX > 0 && zeroY > 0){ //Quad II if first in I.
					points.add(point);
				} else if (((pointY >= pointX * slopePrev && pointY >= pointX * slopeZero && prevY >= 0 && prevX >= 0) //I.
						|| (pointY <= pointX * slopePrev && prevX <= 0 && prevY >= 0 && slopePrev >= slopeZero) //II, before circle.
						|| (pointY <= pointX * slopePrev && pointY >= pointX * slopeZero && prevY >= 0 && prevX <= 0 && slopePrev < slopeZero) //II
						|| (pointY >= pointX * slopePrev && pointY >= pointX * slopeZero && prevX >= 0 && prevY <= 0) //IV.
						) && pointX <= 0 && pointY >= 0 && zeroX <= 0 && zeroY >= 0){ //Quad II if first in II.
					points.add(point);
				} else if (((pointY >= pointX * slopePrev && prevX >= 0) //I or IV
						 || (pointY <= pointX * slopePrev && prevX <= 0 && prevY <= 0)) //III
						&& pointX >= 0 && pointY >= 0 && zeroX <= 0 && zeroY >= 0){ //Quad I if first in II.
					points.add(point);
				} else if (((pointY <= pointX * slopePrev && prevX <= 0) ||  
						(pointY >= pointX * slopePrev && prevX >= 0 && prevY >= 0)) &&
						pointX <= 0 && pointY <= 0){ //Quad III.
					points.add(point);
				} else if (((pointY <= pointX * slopePrev && prevX <= 0) ||
						(pointY >= pointX * slopePrev && prevY <= 0)) && 
						pointX >= 0 && pointY <= 0) { //Quad IV.
					points.add(point);
				}
			}
		}
	}
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
}