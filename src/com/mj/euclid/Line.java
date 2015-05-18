package com.mj.euclid;

import com.mj.maggo.M;

public class Line {

	private float y_intercept;
	private float slope;
	private int pointA, startX, startY;
	private int pointB, endX, endY;
	private boolean valid;
	private boolean infinity_slope;
	private boolean zero_slope;
	private int length;

	public Line(int sp, int ep) {
		setStartPoint(sp);
		setEndPoint(ep);
	}

	public Line() {
	}

	public void setStartPoint(int sp) {
		pointA = sp;
		startX = sp/1000;
		startY = sp%1000;
	}

	public void  setEndPoint(int ep) {
		pointB = ep;
		endX = ep/1000;
		endY = ep%1000;
	}

	public void make() {
		this.slope = solveSlope();
		this.length = solveLength();
		y_intercept = startY - slope*startX;
		valid = true;
	}

	private float solveSlope() {
		int dx = startX - endX;
		int dy = startY - endY;

		if ( dx == 0 ) {
			infinity_slope = true;
			return Integer.MAX_VALUE;
		} 
		else  if ( dy == 0) {
			zero_slope = true;
			return 0;
		}
		else {
			return  dy/(float)dx;
		}

	}

	public void reset() {
		pointA = pointB = startX = startY = endX = endY = 0;
		slope = y_intercept = 0f;
		valid = zero_slope = zero_slope = false;

	}

	public float getX(float y){
		return  (y - y_intercept)/slope;
	}

	public float getY(float x){
		return  slope*x + y_intercept;
	}


	public float getInterceptY() {
		return y_intercept;
	}

	public Integer getStartPoint() {
		return Integer.valueOf(pointA);
	}

	public Integer getEndPoint() {
		return Integer.valueOf(pointB);
	}

	public boolean isValid() {
		return valid;
	}

	@Override
	public String toString() {
		return pointA+" to "+pointB;
	}

	public float getSpeed(int parameter) {
		return (float) (1.84*Math.exp((parameter)/(length)));
	}

	public Integer getPoint(int parameter) {
		if (infinity_slope) {
			//vertical line, x constant....
			if (endY < startY) parameter = -parameter;
			return startX*1000 + startY+parameter;
			
		} 
		else if (zero_slope) {
			//horizontal, y constant...
			if (endX < startX) parameter = -parameter;
			return (startX+parameter)*1000 + startY;
		}
		
		else {
			//add parameter to both x,y
			if (endX < startX) parameter = -parameter;
			return (startX+parameter)*1000 + (int)getY(startX+parameter);
		}

	}
	
	private int solveLength() {
		if (infinity_slope || zero_slope) {
			return M.INTERVAL;
		} else {
			//diagonal
			return (int)(1.414*M.INTERVAL);
		}
		
	}


}
