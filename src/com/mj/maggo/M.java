package com.mj.maggo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class M {
	
	//these determines everything
	public static final int WIDTH = 360;
	public static final int START_XY = 60;
	
	
	public static final int INTERVAL = WIDTH/2;
	public static final int BASE_POINTS[] = {START_XY, START_XY+INTERVAL, START_XY+2*INTERVAL};
	
	public static final int X1 = BASE_POINTS[0];
	public static final int X2 = BASE_POINTS[1];
	public static final int X3 = BASE_POINTS[2];
	
	public static final int Y1 = BASE_POINTS[0];
	public static final int Y2 = BASE_POINTS[1];
	public static final int Y3 = BASE_POINTS[2];
	
	//Board points names
	public static final int A = makePoint(X1,Y1);
	public static final int B = makePoint(X2,Y1);
	public static final int C = makePoint(X3,Y1);
	
	public static final int D = makePoint(X1,Y2);
	public static final int E = makePoint(X2,Y2);
	public static final int F = makePoint(X3,Y2);
	
	public static final int G = makePoint(X1,Y3);
	public static final int H = makePoint(X2,Y3);
	public static final int I = makePoint(X3,Y3);
	
	//Important points
	public static final int CX = X2;
	public static final int CY = Y2;
	public static final int SUM_OF_COORDS = 3*(START_XY + INTERVAL);
	


	public static void choraBodi(Canvas c, Paint p1) {
		
		c.drawLine(X1, Y1, X3 , Y1, p1);
		c.drawLine(X1, Y1, X1 , Y3, p1);
		c.drawLine(X1, Y1, X3 , Y3, p1);
		
		c.drawLine(X2, Y1, X2 , Y3, p1);
		c.drawLine(X1, Y2, X3 , Y2, p1);

		c.drawLine(X3, Y3, X3 , Y1, p1);
		c.drawLine(X1, Y3, X3 , Y3, p1);
		c.drawLine(X1, Y3, X3 , Y1, p1);
		
	}


	private static int makePoint(int x, int y) {
		return 1000*x + y;
	}
	
	public static void  logger(Object o) {
		Log.e("MJ", o.toString());
	}
	
}
