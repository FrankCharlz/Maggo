package com.mj.maggo;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class Maggo extends ActionBarActivity implements Runnable, SurfaceHolder.Callback {

	private static final int MENU_SETTINGS = 0;
	public static final int PLAYER_AI = 1;
	public static final int PLAYER_HUMAN = 0;
	private TextView tv;
	private SurfaceView surface;
	private SurfaceHolder holder;
	private Thread t;
	private Dot[] dots = new Dot[6];

	//very important for storage
	private int current_player=0;
	private  ArrayList<Integer> occupiable, occupied, occupiedx, reachable;
	private int[] wazi, zake, zangu;

	private Paint board_color, dot_1_color, dot_2_color, colors[], o_color, r_color;
	public boolean touched, nichore;
	private int sekos = 0;
	private int dots_iterator = 0;
	protected boolean moving = false;

	//used for drawing moving dot...
	private float dpx, dpy;
	private float px, py;
	private int currentDotId;
	private Line route;

	public String[] players = { "Mago", "AI" };
	private boolean movingDot;
	private int movingDotIndex;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maggo_layout);

		tv = (TextView) findViewById(R.id.textView1);
		surface = (SurfaceView) findViewById(R.id.surfaceView1);

		surface.setOnTouchListener(new BoardTouchListener()); 

		holder = surface.getHolder();
		holder.addCallback(this);

		t = new Thread(this);

		occupiable = new ArrayList<Integer>(9);
		occupied = new ArrayList<Integer>(3);
		occupiedx = new ArrayList<Integer>(3);
		reachable = new ArrayList<Integer>();

		Logic.initOccupiableList(occupiable);

		this.init();



	}


	@Override
	public void run() {
		while (nichore) {
			if(holder.getSurface().isValid()) {
				Canvas c = holder.lockCanvas();

				c.drawColor(Color.WHITE);
				M.choraBodi(c, board_color);

				if (!moving) {

					for (dots_iterator = 0; dots_iterator < sekos; dots_iterator++) {
						Logic.drawDot(c, dots[dots_iterator]);
					}
				}

				if (moving) {

					for (int x : reachable) {
						c.drawCircle( x/1000, x%1000, Dot.RADIUS_BIG, r_color);
					}

					for (Dot dot : dots) {
						if(dot.isAt(currentDotId)) {
							Logic.drawDotMoving(c, dot, px, py);
							continue;
						} //skip currentDot in the main stream.. then draw a single . get only its color ;
						Logic.drawDot(c, dot);
					}

				}

				if (movingDot) {
					dots[movingDotIndex].setX(dpx);
					dots[movingDotIndex].setY(dpy);
					dots[movingDotIndex].setRadius(Dot.RADIUS_BIG);

					dpx += 7;//route.getSpeed(dpx);//speed depends on the route from 7 to 4.3
					dpy = route.getY(dpx);

					if (dpx >= route.getEndPoint()/1000) { 
						dots[movingDotIndex].setRadius(Dot.RADIUS);
						dots[movingDotIndex].setPosition(route.getEndPoint());
						togglePlayer();
						M.logger("Was moving dot index : "+ movingDotIndex);
						M.logger("Done pretty moving");
						movingDot = false;
					}
				}

				holder.unlockCanvasAndPost(c);
			}

			try {
				Thread.sleep(60);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}




	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		nichore = true;
		t.start();

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		nichore = false;

	}

	class BoardTouchListener implements View.OnTouchListener {

		private int id, uid;

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			//color = colors[current]
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN: 
				px = Logic.standardize(event.getX());
				py = Logic.standardize(event.getY());
				id = (int)(1000*px + py);

				if (sekos<6) {
					//remember sekos start at 0;
					if (current_player == PLAYER_HUMAN && px>0 && py>0) {
						//still adding dots...
						if(occupiable.contains(id)) {
							dots[sekos] = new Dot(id, colors[current_player], PLAYER_HUMAN);
							occupiable.remove(Integer.valueOf(id));
							occupied.add(Integer.valueOf(id));
							sekos++;
							togglePlayer();

							tv.setText(px+":"+py+" sekos="+sekos);
						} else {tv.setText("Position is ocupied");}
					}

					if (current_player == PLAYER_AI) { 
						int bp = Logic.bestPlaceToPut(occupied, occupiedx, occupiable);
						dots[sekos] = new Dot(bp, colors[current_player], PLAYER_AI);
						occupiable.remove(Integer.valueOf(bp));
						occupiedx.add(Integer.valueOf(bp));
						sekos++;
						togglePlayer();
					}

					if (sekos == 6) {
						//testing that one time that six is reached
						M.logger("Sixth dot");
					}

				}

				if ( sekos==6 && current_player == PLAYER_HUMAN) {
					moving=true;
					currentDotId = id;
					Logic.findReachableNeighbours(currentDotId, occupiable, reachable);

				}

				//end of case motion DOWN
				break;

			case MotionEvent.ACTION_MOVE:
				if (moving) {
					px = event.getX();
					py = event.getY();
				}
				break;

			case MotionEvent.ACTION_UP:
				if (moving) {
					moving=false;
					dpx = Logic.standardize(event.getX());
					dpy = Logic.standardize(event.getY());
					uid = (int)(1000*dpx + dpy);


					if(reachable.contains(Integer.valueOf(uid))) {
						//was if : occupiable.contains(id)
						//loops thru all dots to find the original dot in the list by  comparing id with....
						//the current id..refer above... ehwn gets the loops changes its x,y coords & if for new pos..
						//edits occupiables...
						for (Dot d : dots) {
							if (d.isAt(id)) {
								d.setPosition(uid);
								break;
							}
						}

						occupied.remove(Integer.valueOf(id));
						occupied.add(Integer.valueOf(uid));

						occupiable.remove(Integer.valueOf(uid));
						occupiable.add(Integer.valueOf(id));

						//let the AI play
						togglePlayer();
						AIplay();
					}

					//takes three dots tests if win.....
					if( Logic.checkWinner(occupied, occupiedx, current_player)) {
						tv.setText(players[current_player]+" wins.");
					}

				}

				reachable.clear();
				//end of case motion UP
				break;

			default:
				break;
			}
			return true;
		}


	}

	@Override
	protected void onPause() {
		nichore = false;
		try {
			t.join(3000);//was 3000
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	public void AIplay() {

		route = Logic.calculateBestMove(route, occupiable, occupiedx, occupied);
		M.logger(route);

		int index = -1;
		for (Dot d : dots) {
			index++;
			if ( d.getPlayerId() == PLAYER_HUMAN ) {
				continue;
			}

			if (d.isAt(route.getStartPoint())) {
				moveDotOverLine(d, route, index);
				break;
			}
		}

		occupiedx.remove(route.getStartPoint());
		occupiedx.add(route.getEndPoint());

		occupiable.remove(route.getEndPoint());
		occupiable.add(route.getStartPoint());

		M.logger("AI has finished playeing");
	}


	private void moveDotOverLine(Dot dot, Line path, int index) {
		dpx = dot.getX();
		dpy = dot.getY();
		movingDotIndex = index;
		movingDot = true;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuItem item =  menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings");
		item.setIcon(getResources().getDrawable(R.drawable.ic_action_settings));
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); 
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case MENU_SETTINGS:
			occupiable.clear();
			Logic.initOccupiableList(occupiable);
			occupied.clear();
			occupiedx.clear();
			moving = false;
			sekos = 0;
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}


	public void togglePlayer() {
		current_player = (current_player == PLAYER_AI) ? PLAYER_HUMAN : PLAYER_AI;
		//tv.setText(players[current_player]+"'s turn");
	}

	public Dot getDotById(int id) {
		int i = 0;
		for (Dot dot : dots) {
			if (dot.isAt(id)) {
				break;
			}
			i++;
		}
		return dots[i];
	}


	private void init() {
		board_color = new Paint();
		board_color.setColor(Color.LTGRAY);
		board_color.setAlpha(200);
		board_color.setStyle(Style.FILL_AND_STROKE);
		board_color.setStrokeWidth(2);

		dot_1_color = new Paint();
		dot_1_color.setARGB(180, 132, 190, 99);
		dot_1_color.setStyle(Style.FILL_AND_STROKE);
		dot_1_color.setStrokeWidth(1);

		dot_2_color = new Paint();
		dot_2_color.setARGB(180, 12, 222, 229);
		dot_2_color.setStyle(Style.FILL_AND_STROKE);
		dot_2_color.setStrokeWidth(1);

		o_color = new Paint();
		o_color.setARGB(20, 212, 22, 29);
		o_color.setStyle(Style.FILL_AND_STROKE);
		o_color.setStrokeWidth(1);

		r_color = new Paint();
		r_color.setARGB(30, 22, 222, 229);
		r_color.setStyle(Style.FILL_AND_STROKE);
		r_color.setStrokeWidth(1);

		colors = new Paint[2];
		colors[0] = dot_1_color;
		colors[1] = dot_2_color;

		route = new Line();

	

	}


}
