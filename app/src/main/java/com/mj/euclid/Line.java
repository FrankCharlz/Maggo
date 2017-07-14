package com.mj.euclid;

import com.mj.maggo.Board;

public class Line {

	private static final int HORIZONTAL = 0xff2;
	private static final int VERTICAL = 0xfd00b;
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
		return "Line from"+pointA+" to "+pointB;
	}

	public double getSpeed(int parameter) {
		return 12 + 6*Math.exp(parameter/length);
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
		int r = getEndPoint() - getStartPoint();
		if( orientation == DIAGONAL)
			return (int)(1.4142* Board.INTERVAL);
		else
			return Board.INTERVAL;
	}

	public int getLength() {
		return length;
	}


}


  
