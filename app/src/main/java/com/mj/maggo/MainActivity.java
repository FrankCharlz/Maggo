package com.mj.maggo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mj.euclid.Dot;
import com.mj.euclid.Line;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements Runnable, SurfaceHolder.Callback {

	private static final int MENU_SETTINGS = 0x0f;
	public static final int PLAYER_AI = 0x000000001;
	public static final int PLAYER_HUMAN = 0x000000;
	
	private TextView tv;
    private SurfaceHolder holder;
	private Thread t;

	//very important for storage
	private int current_player = PLAYER_HUMAN;
	private Dot[] dots = new Dot[6];
	private int sekos = 0, dots_iterator = 0;
	private  ArrayList<Integer> occupiable = new ArrayList<Integer>(9);
	private  ArrayList<Integer> occupied = new ArrayList<Integer>(3);
	private  ArrayList<Integer> occupiedx = new ArrayList<Integer>(3);
	private  ArrayList<Integer> reachable = new ArrayList<Integer>(3);//max reachable is 3

	private Paint board_color, dot_1_color, dot_2_color, colors[], o_color, r_color;
	public boolean touched, nichore;
	protected boolean moving = false, game_over = false;

	//used for drawing moving dot...
	private float dpx, dpy;
	private float px, py;
	private int currentDotId;
	private Line route;

	public String[] players = { "Mago", "AI" };
	private boolean movingDot;
	private int movingDotIndex;
	private int parameter;
	private boolean somebodyWon;
    private Board board;
    private SurfaceView surface;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.maggo_layout);

        //calculating screen width
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        int upana = ( width < height) ? width : height;
        //upana = 700;
        Board.logger("width : "+upana);

        //initializing board with width
        board = new Board(upana);


		tv = (TextView) findViewById(R.id.textView1);
        surface = (SurfaceView) findViewById(R.id.surfaceView1);
		surface.setOnTouchListener(new BoardTouchListener());

		holder = surface.getHolder();
		holder.addCallback(this);

		t = new Thread(this);

		this.init();

	}


	@Override
	public void run() {
		while (nichore) {
			if(holder.getSurface().isValid()) {
				Canvas c = holder.lockCanvas();

				c.drawColor(Color.WHITE);
				board.choraBodi(c, board_color);

				if (!moving) {

					for (dots_iterator = 0; dots_iterator < sekos; dots_iterator++) {
						Board.drawDot(c, dots[dots_iterator]);
					}
				}

				if (moving) {
					for (int pos : reachable) {
						c.drawCircle( pos/1000, pos%1000, Dot.RADIUS_BIG, r_color);
					}

					for (Dot dot : dots) {
						if(dot.isAt(currentDotId)) {
							//skip currentDot in the main stream.. then draw a single . get only its color ;
							Board.drawDotMoving(c, dot, px, py);
							continue;
						} 
						Board.drawDot(c, dot);
					}

				}

				if (movingDot) {
					parameter += route.getSpeed(parameter);
					dots[movingDotIndex].setPosition(route.getPoint(parameter));

					if (parameter >= route.getLength()) { 
						dots[movingDotIndex].setRadius(Dot.RADIUS);
						dots[movingDotIndex].setPosition(route.getEndPoint());
						Board.logger("Was moving dot index : "+ movingDotIndex);
						Board.logger("Done pretty moving");
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

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
                Board.logger("Sekos : "+sekos);
                Board.logger("Touched at :"+event.getX() + ", "+event.getY());
				px = board.standardize(event.getX());
				py = board.standardize(event.getY());
				id = (int)(1000*px + py);

				if (sekos < 6) {
					//remember sekos start at 0;
					if (current_player == PLAYER_HUMAN && px>0 && py>0) {
						//still adding dots...
						if(occupiable.contains(id)) {
							dots[sekos] = new Dot(id, colors[current_player], PLAYER_HUMAN, board.getUpana());
							occupiable.remove(Integer.valueOf(id));
							occupied.add(id);
							sekos++;
							togglePlayer();

							tv.setText(px+":"+py+" sekos="+sekos);
						} else {tv.setText("Position is ocupied");}
					}

					if (current_player == PLAYER_AI) { 
						int bp = board.bestPlaceToPut(occupied, occupiedx, occupiable);
						dots[sekos] = new Dot(bp, colors[current_player], PLAYER_AI, board.getUpana());
						occupiable.remove(Integer.valueOf(bp));
						occupiedx.add(bp);
						sekos++;
						togglePlayer();
					}

					if (sekos == 6) {
						//testing that one time that six is reached
						Board.logger("Sixth dot");
					}

				}

				if ( sekos == 6 && current_player == PLAYER_HUMAN) {
					if (occupied.contains(id)) {
						moving=true;
						currentDotId = id;
						board.findReachableNeighbours(currentDotId, occupiable, reachable);
                        tv.setText("Move it");
					} else {
						tv.setText("That is not yours");
					}
				}
				
				if (game_over) {
					tv.setText("GAME OVER");
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
					dpx = board.standardize(event.getX());
					dpy = board.standardize(event.getY());
					uid = (int)(1000*dpx + dpy);


					if(reachable.contains(uid)) {
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
						occupied.add(uid);

						occupiable.remove(Integer.valueOf(uid));
						occupiable.add(id);

						//let the AI play
						togglePlayer();
						AIplay();
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

		route = board.calculateBestMove(route, occupiable, occupiedx, occupied);
		Board.logger(route.toString());

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

		Board.logger("AI has finished playing");
		togglePlayer();
	}


	private void moveDotOverLine(Dot dot, Line path, int index) {
		dot.setRadius(Dot.RADIUS_BIG);
		movingDotIndex = index;
		parameter = 0;
		movingDot = true;

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item =  menu.add(Menu.NONE, MENU_SETTINGS, Menu.NONE, "Settings");
		item.setIcon(ContextCompat.getDrawable(this, R.drawable.ic_autorenew_white_24dp));
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS); 
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {

		case MENU_SETTINGS:
			resetGame();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void resetGame() {
		sekos = 0;
		moving = touched = game_over = false;
		occupiable.clear();
		occupied.clear();
		occupiedx.clear();
		board.initOccupiableList(occupiable);
        surface.setOnTouchListener(new BoardTouchListener()); //// TODO: 14-Jul-17 bomb!!!

	}


	public void togglePlayer() {
        //will always toggle except kama kuna winner
        int previous_player = current_player; //after this function you will be the PREVIOUS PLAYER
        current_player = (current_player == PLAYER_AI) ? PLAYER_HUMAN : PLAYER_AI;
        tv.setText(String.format("%s's turn", players[current_player]));

        //check if there us a winner
		if (sekos == 6) {
			somebodyWon = Board.checkWinner(occupied, occupiedx, previous_player);
			if(somebodyWon) {
                //previous player is the winner
				//board_color.setColor(colors[current_player].getColor());
				Board.logger(players[previous_player]+" won");
				game_over = true;
                Toast.makeText(this, players[previous_player]+" won", Toast.LENGTH_SHORT).show();
                tv.setText(String.format("%s's turn", players[previous_player]));
                surface.setOnTouchListener(null);

			}
		}
	}

	private void init() {
		board_color = new Paint();
		board_color.setColor(Color.LTGRAY);
		board_color.setAlpha(200);
		board_color.setStyle(Style.FILL_AND_STROKE);
		board_color.setStrokeWidth(2);

		dot_1_color = new Paint();
		dot_1_color.setARGB(200, 232, 190, 99);
		dot_1_color.setStyle(Style.FILL_AND_STROKE);
		dot_1_color.setStrokeWidth(1);

		dot_2_color = new Paint();
		dot_2_color.setARGB(200, 112, 222, 229);
		dot_2_color.setStyle(Style.FILL_AND_STROKE);
		dot_2_color.setStrokeWidth(1);

		o_color = new Paint();
		o_color.setARGB(20, 22, 22, 29);
		o_color.setStyle(Style.FILL_AND_STROKE);
		o_color.setStrokeWidth(1);

		r_color = new Paint();
		r_color.setARGB(30, 212, 222, 229);
		r_color.setStyle(Style.FILL_AND_STROKE);
		r_color.setStrokeWidth(1);

		colors = new Paint[2];
		colors[0] = dot_1_color;
		colors[1] = dot_2_color;


		board.initOccupiableList(occupiable);
		route = new Line();

	}


}
