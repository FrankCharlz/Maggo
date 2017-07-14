package com.mj.maggo;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.mj.euclid.Dot;
import com.mj.euclid.Line;

import java.util.ArrayList;
import java.util.Random;

public class Board {

    public static int INTERVAL = 60;
    private final int width;
    private int X1, X2, X3, Y1, Y2, Y3;
    private int A, B, C, D, E, F, G, H, I;
    private int CX, CY;
    private int SUM_OF_COORDS;

    private Random random =  new Random();

    Board(int width) {
        this.width = width;
        init();
    }

    private void init() {

        int start_xy = 60; //padding
        int boardWidth = width - 2*start_xy; //padding
        int interval = boardWidth/2;
        INTERVAL = interval; //// TODO: 14-Jul-17 logical 


        int BASE_POINTS[] = {start_xy, start_xy+interval, start_xy + 2*interval};

        X1 = BASE_POINTS[0];
        X2 = BASE_POINTS[1];
        X3 = BASE_POINTS[2];

        Y1 = BASE_POINTS[0];
        Y2 = BASE_POINTS[1];
        Y3 = BASE_POINTS[2];

        //Board points names
        A = makePoint(X1,Y1);
        B = makePoint(X2,Y1);
        C = makePoint(X3,Y1);

        D = makePoint(X1,Y2);
        E = makePoint(X2,Y2);
        F = makePoint(X3,Y2);

        G = makePoint(X1,Y3);
        H = makePoint(X2,Y3);
        I = makePoint(X3,Y3);

        //Important points
        CX = X2;
        CY = Y2;
        SUM_OF_COORDS = 3*(start_xy + interval);
    }

    /***
     * THE BOARD
     * ABC
     * DEF
     * GHI
     */

    private int[] getNeighbours(Integer node) {
        //should find algorithm
        if (node == A ) { return new int[] {E, B, D};}
        else if (node == B ) { return new int[] {E, A, C};}
        else if (node == C ) { return new int[] {E, B, F};}
        else if (node == D ) { return new int[] {E, A, G};}
        else if (node == E ) { return new int[] {A,B,C,D,F,G,H,I};}
        else if (node == F ) { return new int[] {E, C, I};}
        else if (node == G ) { return new int[] {E, D, H};}
        else if (node == H ) { return new int[] {E, G, I};}
        else if (node == I ) { return new int[] {E, H, F};}
        else return new int[]{};

    }

    void choraBodi(Canvas c, Paint p1) {
        c.drawLine(X1, Y1, X3 , Y1, p1);
        c.drawLine(X1, Y1, X1 , Y3, p1);
        c.drawLine(X1, Y1, X3 , Y3, p1);

        c.drawLine(X2, Y1, X2 , Y3, p1);
        c.drawLine(X1, Y2, X3 , Y2, p1);

        c.drawLine(X3, Y3, X3 , Y1, p1);
        c.drawLine(X1, Y3, X3 , Y3, p1);
        c.drawLine(X1, Y3, X3 , Y1, p1);

    }


    private  int makePoint(int x, int y) {
        return 1000*x + y;
    }

    public static  void logger(Object o) {
        Log.e("maggo: ", o.toString());
    }


    public void initOccupiableList(ArrayList<Integer> occupiable) {
        occupiable.add(A); occupiable.add(B); occupiable.add(C);
        occupiable.add(D); occupiable.add(E); occupiable.add(F);
        occupiable.add(G); occupiable.add(H); occupiable.add(I);
    }

    private int genius(int i, int j) {
        return SUM_OF_COORDS - i - j;
    }

    private boolean theyAreInDiagonal(int x1, int y1, int x2, int y2) {
        return (y2 - y1)*(CX - x2) == (x2 - x1)*(CY -y2);
    }


    public void findReachableNeighbours(int node, ArrayList<Integer> occupiable, ArrayList<Integer> reachable) {

        int[] neighbours = getNeighbours(node);
        for (int x : neighbours ) {
            if (occupiable.contains(x)) {
                reachable.add(x);
            }
        }

    }


    int bestPlaceToPut(ArrayList<Integer> occupied,
                       ArrayList<Integer> occupiedx, ArrayList<Integer> occupiable) {

        Random rn = new Random();

        //wahi center dot
        if (occupiable.contains(E)) { return E; }

        int s = occupied.size();

        //prevent him from scoring, filling the obvious
        int bcp;
        if (s >= 2) {
            bcp =  complementPosition(occupied, s);
            if (occupiable.contains(bcp)) { return bcp;}
        }

        if (s == 3) {
            return anyFromThese(occupiable, rn);
        }


        return anyFromThese(occupiable, rn);
    }



    private  int complementPosition(ArrayList<Integer> occupied, int s) {
        return complementPosition(occupied.get(s-1), occupied.get(s-2));
    }

    private  int complementPosition(Integer positionA, Integer positionB) {
        //case X equals
        if( positionA/1000 == positionB/1000 ) {
            return (positionA/1000)*1000 + genius(positionA%1000, positionB%1000);
        }

        //case Y equals
        if( positionA%1000 == positionB%1000 ) {
            return 1000*genius(positionA/1000, positionB/1000) + positionA%1000;
        }

        //case DIAGONAL
        if (theyAreInDiagonal( positionA/1000, positionA%1000,
                positionB/1000, positionB%1000) )
        {
            return 1000*genius(positionA/1000, positionB/1000) +
                    genius(positionA%1000, positionB%1000);

        }

        return 0;
    }


    private  int anyFromThese(ArrayList<Integer> occupiable, Random rn) {
        Log.e("Mj", "Played randomly");
        return (occupiable.size() > 0) ? occupiable.get(rn.nextInt(occupiable.size()-1)) : 90080;
    }


    float standardize(float touch) {
        if(touch>X1-40 && touch<X1+40) {touch=X1;} else
        if(touch>X2-40 && touch<X2+40) {touch=X2;} else
        if(touch>X3-40 && touch<X3+40) {touch=X3;} else
        {touch=0;}
        Board.logger("standard : "+touch);
        return touch;
    }

    static void drawDot(Canvas c, Dot dot) {
        c.drawCircle(dot.getX(), dot.getY(), dot.getRadius(), dot.getColor());
    }

    static void drawDotMoving(Canvas c, Dot dot, float px, float py) {
        c.drawCircle(px, py, dot.getBigRadius(), dot.getColor());

    }


    static boolean checkWinner(ArrayList<Integer> occupied, ArrayList<Integer> occupiedx, int pid) {

        Log.e("mj", "checking winner");
        if (pid == MainActivity.PLAYER_HUMAN )
            return areCollinear(occupied);
        else
            return areCollinear(occupiedx);

    }

    private static boolean areCollinear(ArrayList<Integer> list) {
        int dx1 = list.get(1)/1000 - list.get(0)/1000;
        int dx2 = list.get(2)/1000 - list.get(0)/1000;

        int dy1 = list.get(1)%1000 - list.get(0)%1000;
        int dy2 = list.get(2)%1000 - list.get(0)%1000;

        return dy1*dx2 == dx1*dy2;
    }
    


    Line calculateBestMove(Line path, ArrayList<Integer> occupiable,
                           ArrayList<Integer> occupiedx, ArrayList<Integer> occupied) {

        ArrayList<Integer> r_neighbors = new ArrayList<Integer>();

        int winning_start = occupiedx.get(0), winning_end = 0;
        int preventing_end = 0;
        int just_start = 0, just_end = 0;


        //loop for finding winning end.....
        int i;
        for (i=0; i<3; i++) {
            winning_start = occupiedx.get(i);

            findReachableNeighbours(winning_start, occupiable, r_neighbors);
            logger("Near "+winning_start+">"+i+" : "+r_neighbors.toString());
            //find reachable neigbours and save them in r_neighbours

            if (r_neighbors.isEmpty()) { continue; }
            //no neighbors, go to next in your dots

            just_start = winning_start;
            just_end = r_neighbors.get(0);
            //if can't win just play these

            winning_end = complementPosition(occupiedx.get((i+1)%3), occupiedx.get((i+2)%3));
            logger("Complement pos for "+winning_start+" = "+winning_end);


            if (r_neighbors.contains(winning_end)) { break; }
            //check if its reachable and occupiable, if true break so as 
            //end remain as end..

            r_neighbors.clear();
            //clear neigbours for new cycle
        }

        if (winning_end == 0) {
            //no winning position for all your dots
            path.reset();
            path.setStartPoint(just_start);
            path.setEndPoint(just_end);
            path.make();
        }
        else
        {
            path.reset();
            path.setStartPoint(winning_start);
            path.setEndPoint(winning_end);
            path.make();
        }

        return path;
    }


    public int getUpana() {
        return width;
    }
}
