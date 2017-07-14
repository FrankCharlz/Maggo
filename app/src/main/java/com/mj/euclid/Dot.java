package com.mj.euclid;

import android.graphics.Paint;

import com.mj.maggo.Board;

public class Dot {

    //Constants
    public static float RADIUS_BIG = 40f;
    public static float RADIUS = 30f;
    public static final float SPEED = 3;

    private int sekoNumber;
    private float x;
    private float y;
    private float radius = RADIUS;
    private int position;
    private int player_id;
    private Paint color;

    public Dot(int position, Paint color, int player_id, int boardSize) {
        this.setPosition(position);
        this.color = color;
        this.player_id = player_id;
        RADIUS = (1f * boardSize) / 20;
        RADIUS_BIG = RADIUS * 4.0f / 3;
        Board.logger("Created new dot: "+this.toString());

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
        this.setX(pos /1000);
        this.setY(pos %1000);
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

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public float getBigRadius() {
        return RADIUS_BIG;
    }

    @Override
    public String toString() {
        return "player = "+this.player_id+"\n"+
                "position = "+this.position+"\n"+
                "radius = "+RADIUS+"\n";

    }
}
