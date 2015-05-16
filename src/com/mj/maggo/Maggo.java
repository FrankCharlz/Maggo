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
	public ArrayList<Dot> dots;

	//very important for storage
	private int current_player=0;
	private  ArrayList<Integer> occupiable, occupied, occupiedx, reachable;
	private int[] wazi, zake, zangu;
	private int zangu_counter=0, zake_counter=0;

	private Paint board_color, dot_1_color, dot_2_color, colors[], o_color, r_color;
	public boolean touched, nichore;
	private int sekos = 0;
	private float radius = 28f; 
	private float radius_moving = 36f;
	protected boolean moving = false;

	//used for drawing moving dot...
	private float dpx, dpy;
	private float px, py;
	private int currentDotId;
	private Line route;

	public String[] players = { "Mago", "AI" };

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

		dots = new ArrayList<Dot>(6);

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
					for (Dot dot : dots) {
						c.drawCircle(dot.getX(), dot.getY(), radius, dot.getColor());
					}
				}

				if (moving) {

					for (int x : reachable) {
						c.drawCircle( x/1000, x%1000, radius_moving, r_color);
					}

					for (Dot dot : dots) {
						if(dot.getId()==currentDotId) {
							c.drawCircle(px, py, radius_moving, dot.getColor());
							continue;
						} //skip currentDot in the main stream.. then draw a single . get only its color ;
						c.drawCircle(dot.getX(), dot.getY(), radius, dot.getColor());
					}

					if (current_player == PLAYER_AI) {
						//c.drawPath(route, o_color);
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
		// TODO Auto-generated method stub

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
							dots.add(new Dot(sekos, px, py, id, colors[current_player]));
							occupiable.remove(Integer.valueOf(id));
							occupied.add(Integer.valueOf(id));
							sekos++;
							togglePlayer();

							tv.setText(px+":"+py+" sekos="+sekos);
						} else {tv.setText("Position is ocupied");}
					}
					
					if (current_player == PLAYER_AI) { 
						//engine's turn
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						int bp = Logic.bestPlaceToPut(occupied, occupiedx, occupiable);
						dots.add(new Dot(sekos, bp/1000, bp%1000, bp, colors[current_player]));
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

				if ( sekos==6 ) {
					moving=true;

					if (current_player == PLAYER_AI) {
						AIplay();

					}
					else {
						currentDotId = id;
						Logic.findNeighbours(currentDotId, occupiable, reachable);

					}




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
							if (d.getId()==id) {
								d.setX(dpx);
								d.setY(dpy);
								d.setId(uid);
								break;
							}
						}

						occupied.remove(Integer.valueOf(id));
						occupied.add(Integer.valueOf(uid));

						occupiable.remove(Integer.valueOf(uid));
						occupiable.add(Integer.valueOf(id));
						togglePlayer();
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
		try {
			t.join(3000);//was 3000
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	public void AIplay() {
		route = Logic.calculateBestMove(route, occupiable, occupiedx, occupied);
		M.logger(route);

		for (Dot d : dots) {
			if (d.getId()==route.getStartPoint()) {
				d.setX(route.getEndPoint()/1000);
				d.setY(route.getEndPoint()%1000);
				d.setId(route.getEndPoint());
				break;
			}
		}

		occupiedx.remove(route.getStartPoint());
		occupiedx.add(route.getEndPoint());

		occupiable.remove(route.getEndPoint());
		occupiable.add(route.getStartPoint());


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
			dots.clear();
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
		tv.setText(players[current_player]+"'s turn");
	}

	public Dot getDotById(int id) {
		int i = 0;
		for (Dot dot : dots) {
			if (dot.getId() == id) {
				break;
			}
			i++;
		}
		return dots.get(i);
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

		//initializing arrays
		wazi =  new int[3];
		zake = new int[3];
		zangu = new int[3];
		for (int i = 0; i<3; i++) {
			wazi[i] = 0;
			zake[i] = 0;
			zangu[i] = 0;

		}

	}


}