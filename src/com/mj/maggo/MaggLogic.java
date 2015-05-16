package com.mj.maggo;

import java.util.ArrayList;
import java.util.Random;

import android.graphics.Path;
import android.util.Log;

public class MaggLogic {
	
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

	public static void initOccupiableList(ArrayList<Integer> occupiable) {
		occupiable.add(M.A); occupiable.add(M.B); occupiable.add(M.C);
		occupiable.add(M.D); occupiable.add(M.E); occupiable.add(M.F);
		occupiable.add(M.G); occupiable.add(M.H); occupiable.add(M.I);
	}

	public static boolean checkWinner(ArrayList<Integer> occupied, ArrayList<Integer> occupiedx, int pid) {

		Log.e("mj", "checking winner");
		return  areCollinear(occupied) || areCollinear(occupiedx);
		/*
		if (pid == 1 ) 
			return areCollinear(occupied);
		else 
			return areCollinear(occupiedx);	
		 */
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
		int s = occupied.size();

		if (s == 3) {
			return anyFromThese(occupiable, rn);
		}

		//wahi center dot
		if (occupiable.contains(Integer.valueOf(M.E))) { return M.E; }


		//prevent him from scoring, filling the obvious
		int bcp;
		if (s >= 2) {
			bcp =  complementPosition(occupied, s);
			if (bcp != 0 ) { return bcp;}
			
		}


		return anyFromThese(occupiable, rn);
	}


	private static int complementPosition(ArrayList<Integer> occupied, int s) {
		//case X equals
		if( occupied.get(s-1)/1000 == occupied.get(s-2)/1000 ) {
			return (occupied.get(s-1)/1000)*1000 + genius(occupied.get(s-1)%1000, occupied.get(s-2)%1000);
		}

		//case Y equals
		if( occupied.get(s-1)%1000 == occupied.get(s-2)%1000 ) {
			return 1000*genius(occupied.get(s-1)/1000, occupied.get(s-2)/1000) + occupied.get(s-1)%1000;
		}

		//case DIAGONAL
		if (theyAreInDiagonal( occupied.get(s-1)/1000, occupied.get(s-1)%1000, 
				occupied.get(s-2)/1000, occupied.get(s-2)%1000) )
		{
			return 1000*genius(occupied.get(s-1)/1000, occupied.get(s-2)/1000) +
					genius(occupied.get(s-1)%1000, occupied.get(s-2)%1000);

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


	public static void findNeighbours(int currentDotId, ArrayList<Integer> occupiable, ArrayList<Integer> reachable) {

		if (isMiddleSidePoint(currentDotId)) 
		{
			for (int item : occupiable){
				if (inRangeX(item, currentDotId) ) {
					reachable.add(item);

				}
			}
		}
		else 
		{
			for (int item : occupiable){
				if (inRange(item, currentDotId) ) {
					reachable.add(item);

				}
			}


		}

	}

	private static boolean isMiddleSidePoint(int point) {
		return (point == M.B || point == M.D ||point == M.F ||point == M.F);
	}

	public static boolean inRange(int item, int point) {
		int dx = Math.abs(item/1000 - point/1000);
		int dy = Math.abs(item%1000 - point%1000);

		return (dx == M.INTERVAL && dy <=M.INTERVAL) || (dy == M.INTERVAL && dx <=M.INTERVAL );
	}

	public static boolean inRangeX(int item, int point) {
		int dx = Math.abs(item/1000 - point/1000);
		int dy = Math.abs(item%1000 - point%1000);

		return (dx == M.INTERVAL && dy ==  0 ) || (dy == M.INTERVAL && dx == 0 );
	}

	public static Line calculateBestMove(Line path, ArrayList<Integer> occupiable,
			ArrayList<Integer> occupiedx, ArrayList<Integer> occupied) {
		
		int start = occupiedx.get(rn.nextInt(3));
		int end = occupiable.get(rn.nextInt(3));
	
		path.reset();
		path.startAt(start);
		path.endAt(end);
		path.make();
		
		return path;
	}



}
