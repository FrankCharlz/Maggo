package com.mj.maggo;

public class Line {
	
	private float y_intercept;
	private float slope;
	private int pointA, startX, startY;
	private int pointB, endX, endY;
	private boolean valid;
	
	public Line(int sp, int ep) {
		startAt(sp);
		endAt(ep);
	}
	
	public Line() {
	}

	public void startAt(int sp) {
		pointA = sp;
		startX = sp/1000;
		startY = sp%1000;
	}
	
	public void  endAt(int ep) {
		pointB = ep;
		endX = ep/1000;
		endY = ep%1000;
	}
	
	public void make() {
		slope = getSlope();
		y_intercept = startY - slope*startX;
		valid = true;
	}
	
	public float getSlope() {
		int dx = startX - endX;
		
		if ( dx == 0 )
			return 9999;
		else 
			return (startY - endY) / (float)dx;
		
	}
	
	public void reset() {
		pointA = pointB = startX = startY = endX = endY = 0;
		slope = y_intercept = 0f;
		valid = false;
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

	public int getStartPoint() {
		return pointA;
	}

	public int getEndPoint() {
		return pointB;
	}

	public boolean isValid() {
		return valid;
	}
	
	@Override
	public String toString() {
		return pointA+" to "+pointB;
	}
	
	

}
