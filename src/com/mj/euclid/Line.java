package com.mj.euclid;

import com.mj.maggo.M;

public class Line {

	public static final int HORIZONTAL = 0xff2;
	public static final int VERTICAL = 0xfd00b;
	private static final int DIAGONAL = 0xf0ab;

	private float y_intercept;
	private float slope;
	private int pointA, startX, startY;
	private int pointB, endX, endY;
	private boolean valid;
	private int length;
	private int orientation;

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
		this.y_intercept = startY - slope*startX;
		this.valid = true;
	}

	private float solveSlope() {
		int dx = startX - endX;
		int dy = startY - endY;

		if ( dx == 0 ) {
			orientation = VERTICAL;
			return Integer.MAX_VALUE;
		} 
		else  if ( dy == 0) {
			orientation = HORIZONTAL;
			return 0;
		} 
		else {
			orientation = DIAGONAL;
			return  dy/(float)dx;
		}

	}

	public void reset() {
		pointA = pointB = startX = startY = endX = endY = 0;
		slope = y_intercept = 0f;
		valid =  false;

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

		switch (orientation) {
		case HORIZONTAL:
			if (endX < startX) parameter = -1*parameter;
			return (startX+parameter)*1000 + startY;


		case VERTICAL:
			if (endY < startY) parameter = -1*parameter;
			return startX*1000 + startY+parameter;


		case DIAGONAL:
			if (endX < startX) parameter = -1*parameter;
			return (startX+parameter)*1000 + (int)getY(startX+parameter);

		default:
			return 0;
		}
	}

	private int solveLength() {
		if( orientation == DIAGONAL)
			return M.INTERVAL;
		else
			return (int)(1.4142*M.INTERVAL);
	}

	public int getLength() {
		return length;
	}

}


  
