package com.mj.maggo;

import android.graphics.Paint;

public class Dot {
	private int pos;
	private float x;
	private float y;
	private int id;
	private int player_id;
	private Paint color;
	
	public Dot(int pos, float x, float y, int id, Paint color) {
		this.pos = pos;
		this.x = x;
		this.y = y;
		this.id = id;
		this.color = color;
		
		player_id = pos & 0x1;
		
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPos() {
		return pos;
	}
	
	public int getPlayerId() {
		return player_id;
	}

}
