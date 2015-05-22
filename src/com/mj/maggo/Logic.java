package com.mj.maggo;

import java.util.ArrayList;
import java.util.Random;

import com.mj.euclid.Dot;
import com.mj.euclid.Line;

import android.graphics.Canvas;
import android.graphics.Path;
import android.util.Log;

public class Logic {

	static Random rn;

	static {
		rn = new Random();
	}

	public static float standardize(float touch) {
		if(touch>20 && touch<100) {touch=60;} else 
			if(touch>200 && touch<280) {touch=240;} else 
				if(touch>380 && touch<460) {touch=420;} else
				{touch=0;}

		return touch;

	}

	public static void drawDot(Canvas c, Dot dot) {
		c.drawCircle(dot.getX(), dot.getY(), dot.getRadius(), dot.getColor());
	}

	public static void drawDotMoving(Canvas c, Dot dot, float px, float py) {
		c.drawCircle(px, py, Dot.RADIUS_BIG, dot.getColor());

	}


	public static void initOccupiableList(ArrayList<Integer> occupiable) {
		occupiable.add(M.A); occupiable.add(M.B); occupiable.add(M.C);
		occupiable.add(M.D); occupiable.add(M.E); occupiable.add(M.F);
		occupiable.add(M.G); occupiable.add(M.H); occupiable.add(M.I);
	}

	public static boolean checkWinner(ArrayList<Integer> occupied, ArrayList<Integer> occupiedx, int pid) {

		Log.e("mj", "checking winner");
		if (pid == Maggo.PLAYER_HUMAN ) 
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

	public static int bestPlaceToPut(ArrayList<Integer> occupied,
			ArrayList<Integer> occupiedx, ArrayList<Integer> occupiable) {

		Random rn = new Random();

		//wahi center dot
		if (occupiable.contains(Integer.valueOf(M.E))) { return M.E; }

		int s = occupied.size();

		//prevent him from scoring, filling the obvious
		int bcp;
		if (s >= 2) {
			bcp =  complementPosition(occupied, s);
			if (occupiable.contains(Integer.valueOf(bcp))) { return bcp;}	
		}

		if (s == 3) {
			return anyFromThese(occupiable, rn);
		}


		return anyFromThese(occupiable, rn);
	}


	private static int complementPosition(ArrayList<Integer> occupied, int s) {
		return complementPosition(occupied.get(s-1), occupied.get(s-2));
	}

	private static int complementPosition(Integer positionA, Integer positionB) {
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


	private static int anyFromThese(ArrayList<Integer> occupiable, Random rn) {
		Log.e("Mj", "Played randomly");
		return (occupiable.size() > 0) ? occupiable.get(rn.nextInt(occupiable.size()-1)) : 90080;
	}

	private static int genius(int i, int j) {
		return M.SUM_OF_COORDS - i - j;
	}

	private static boolean theyAreInDiagonal(int x1, int y1, int x2, int y2) {
		return (y2 - y1)*(M.CX - x2) == (x2 - x1)*(M.CY -y2);
	}


	public static void findReachableNeighbours(int node, ArrayList<Integer> occupiable, ArrayList<Integer> reachable) {

		int[] neighbours = M.getNeighbours(node);
		for (int x : neighbours ) {
			if (occupiable.contains(Integer.valueOf(x))) {
				reachable.add(Integer.valueOf(x));
			}
		}

	}


	public static Line calculateBestMove(Line path, ArrayList<Integer> occupiable,
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
			M.logger("Near "+winning_start+">"+i+" : "+r_neighbors.toString());
			//find reachable neigbours and save them in r_neighbours

			if (r_neighbors.isEmpty()) { continue; }
			//no neighbors, go to next in your dots

			just_start = winning_start;
			just_end = r_neighbors.get(0);
			//if can't win just play these

			winning_end = complementPosition(occupiedx.get((i+1)%3), occupiedx.get((i+2)%3)); 
			M.logger("Complement pos for "+winning_start+" = "+winning_end);


			if ( r_neighbors.contains(Integer.valueOf(winning_end))) { break; }	
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


}
