package com.mj.maggo;

import android.graphics.Paint;

public class Dot {
	private int sekoNumber;
	private float x;
	private float y;
	private int position;
	private int player_id;
	private Paint color;
	
	public Dot(int position, Paint color, int player_id) {
		this.setPosition(position);
		this.color = color;
		this.player_id = player_id;
		
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}

	public Paint getColor() {
		return color;
	}

	public void setColor(Paint color) {
		this.color = color;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int pos) {
		this.position = pos;
		this.setX((int)pos/1000);
		this.setY((int)pos%1000);
	}

	public int getSekoNumber() {
		return sekoNumber;
	}
	
	public int getPlayerId() {
		return player_id;
	}
	
	public boolean isAt(int pos) {
		return pos == position;
	}

}
