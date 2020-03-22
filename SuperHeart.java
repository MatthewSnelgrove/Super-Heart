//Matthew Snelgrove, Freeman Huang
//January 17, 2019
//Jupiter has let loose his minions and lain a curse upon the land. Time only move, if you move. It is up to Cupid to defeat Jupiter's armies and restore balance to the universe.


package superHeart;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;


public class SuperHeart extends JPanel implements Runnable, KeyListener, MouseListener{


	private static final long serialVersionUID = 1L;
	//Prevent tearing and flicker by drawing to image then copying image.
	BufferedImage  bufferedImage;

	//Reference to handler used throughout
	Handler handler;
	Thread thread1;
	//Initial parameters
	int level = 0;
	double gameSpeed = 0.1;
	int FPS = 60;
	//Mouse pos global. Getting mouse pos from action event was slightly inaccurate.
	int[] mousePos = {0, 0};

	//Stats for arrows and enemies.
	//(string type, int speed, int lifetime, int width, int height, int pierce, Image sprite)
	ArrowStats playerBasicArrow = new ArrowStats("basic", 15, 600, 70, 35, 0, new ImageIcon("basicArrow.png").getImage());
	ArrowStats playerShotgunArrow = new ArrowStats("shotgun", 10, 350, 60, 30, 0, new ImageIcon("shotgunArrow.png").getImage());
	ArrowStats playerSniperArrow = new ArrowStats("sniper", 50, 9999, 50, 25, 1, new ImageIcon("sniperArrow.png").getImage());
	ArrowStats playerRicochetArrow = new ArrowStats("ricochet", 13, 1000, 70, 35, 3, new ImageIcon("ricochetArrow.png").getImage());

	ArrowStats enemyBasicArrow = new ArrowStats("basic", 10, 900, 70, 35, 0, new ImageIcon("enemyBasicArrow.png").getImage());
	ArrowStats enemyShotgunArrow = new ArrowStats("shotgun", 8, 300, 60, 30, 0, new ImageIcon("enemyShotgunArrow.png").getImage());
	ArrowStats enemySniperArrow = new ArrowStats("sniper", 15, 2000, 50, 25, 0, new ImageIcon("enemySniperArrow.png").getImage());


	//(ArrowStats arrowStats, int speed, int width, int height, int prefDistance, double fireRate, Image sprite)
	EnemyStats enemy1Stats = new EnemyStats(enemyBasicArrow, 5, 50, 50, 250, 400, new ImageIcon("enemy1.png").getImage());
	EnemyStats enemy2Stats = new EnemyStats(enemyBasicArrow, 2, 40, 40, 300, 300, new ImageIcon("enemy2.png").getImage());
	EnemyStats enemy3Stats = new EnemyStats(enemyBasicArrow, 10, 45, 45, 350, 250, new ImageIcon("enemy3.png").getImage());
	EnemyStats enemy4Stats = new EnemyStats(enemyShotgunArrow, 5, 75, 75, 1, 100, new ImageIcon("enemy4.png").getImage());
	EnemyStats enemy5Stats = new EnemyStats(enemySniperArrow, 2, 30, 30, 9999, 400, new ImageIcon("enemy5.png").getImage());
	EnemyStats enemy6Stats = new EnemyStats(enemyShotgunArrow, 0, 50, 50, 250, 300, new ImageIcon("enemy1.png").getImage());

	//Arrays containing stats to access when builing maps
	ArrowStats[] playerArrowStats = new ArrowStats[] {playerBasicArrow, playerShotgunArrow, playerSniperArrow, playerRicochetArrow};
	ArrowStats[] enemyArrowStats = new ArrowStats[] {enemyBasicArrow, enemyShotgunArrow, enemySniperArrow};
	EnemyStats[] enemyStats = new EnemyStats[] {enemy1Stats, enemy2Stats, enemy3Stats, enemy4Stats, enemy5Stats, enemy6Stats};

	//Constructor
	public SuperHeart() {
		thread1 = new Thread(this);
		thread1.start();
	}

	//Set initial conditions when thread starts
	private void initialize() {

		handler = new Handler();
		bufferedImage = new BufferedImage(1080, 720, BufferedImage.TYPE_INT_RGB);
		newLevel(level);

	}


	//No parameters
	//No return
	//Sets gamesSpeed based on player movement. Also updates everything for position, shooting etc, and checks if level has been passed or failed.
	private void update() {
		//Change gameSpeed
		if(handler.player.moving == true) {
			gameSpeed *= 1.2;
			if(gameSpeed > 1) {
				gameSpeed = 1;
			}
		}
		else {
			gameSpeed *= 0.85;
			if(gameSpeed < 0.05) {
				gameSpeed = 0.05;
			}
		}

		//Update everything
		handler.update(gameSpeed);

		//Check win/fail
		if(handler.enemies == 0){
			level ++;
			newLevel(level);

		}

		if(handler.reset == true) {
			newLevel(level);
		}
	}

	//Takes graphics parameter
	//No return
	//Draws a white background then all gameObjects, tiles, arrowPickups onto an image then copied the image to the panel's graphics.
	private void render(Graphics g) {
		Graphics BIGraphics = bufferedImage.getGraphics();
		BIGraphics.setColor(Color.WHITE);
		BIGraphics.fillRect(-1500, -1000, 3000, 2000);
		handler.render(BIGraphics);
		g.drawImage(bufferedImage, 0, 0, null);
	}

	//Takes graphics parameter
	//No return
	//Calls render method
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		render(g);
	}

	public void keyTyped(KeyEvent e) {

	}

	//Takes KeyEvent parameter
	//No return
	//Sets tells handler which keys are pressed.
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			handler.player.keysPressed[0] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			handler.player.keysPressed[1] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			handler.player.keysPressed[2] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			handler.player.keysPressed[3] = true;
		}

	}

	//Takes KeyEvent parameter
	//No return
	//Sets tells handler which keys have been released.
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_W) {
			handler.player.keysPressed[0] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_D) {
			handler.player.keysPressed[1] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_S) {
			handler.player.keysPressed[2] = false;
		}
		if (e.getKeyCode() == KeyEvent.VK_A) {
			handler.player.keysPressed[3] = false;
		}

	}


	//No parameters
	//no return
	//Sets initial conditions then runs through game loop. Time since last frame is subtracted to get consistent FPS regardless of performance variance per frame.
	public void run() {
		initialize();

		long startTime;
		long endTime;
		while(true) {
			startTime = System.currentTimeMillis();
			update();
			repaint();
			endTime = System.currentTimeMillis();
			if (1000/ FPS - (endTime - startTime) > 0) {
				try {
					Thread.sleep((1000 / FPS) - (endTime - startTime));
				} catch (InterruptedException e) {
				}
			}
		}

	}

	//Takes String array for command-line arguments
	//No return
	//Sets up frame and panel and calls constructor.
	public static void main(String[] args) {
		JFrame frame = new JFrame ("Super Heart");
		SuperHeart myPanel = new SuperHeart ();
		myPanel.addMouseListener(myPanel);
		frame.add(myPanel);
		frame.addKeyListener(myPanel);
		frame.addKeyListener(myPanel);

		frame.setVisible(true);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setMinimumSize(new Dimension(1080, 720));
		frame.setMaximumSize(new Dimension(1080, 720));
		frame.setPreferredSize(new Dimension(1080, 720));
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);

		//new SuperHeart();
	}


	//Parameters for stats of arrow to shoot and position of mouse and int array
	//No return
	//Creates an arrow(s) that aim and move towards mouse. If currently equiped with shotgun arrows, shoot 4 arrow in a 30 degree spread.
	private void Shoot(ArrowStats arrowStats, int[] mousePos) {
		double angle = Math.atan2(mousePos[1] - handler.player.aimY, mousePos[0] - handler.player.aimX);
		if(arrowStats.arrowType.equals("shotgun")) {
			for(int i = -15; i <= 15; i += 10) {
				Arrow arrow = new Arrow("player", arrowStats, handler.player.aimX, handler.player.aimY, angle + Math.toRadians(i), handler);
			}
		}else {
			Arrow arrow = new Arrow("player", arrowStats, handler.player.aimX, handler.player.aimY, angle, handler);
		}


	}


	//Parameter for current level
	//No return
	//Resets level and creates a new level using a tilemap
	public void newLevel (int level) {
		//00: walkable tile
		//01: wall
		//10: player, arrowType
		//20: enemy1
		//21: enemy2
		//23: enemy3
		//24: enemy4
		//25: enemy5
		//30: basic
		//31: shotgun
		//32: sniper
		//33: ricochet
		handler.reset = false;
		handler.enemies = 0;
		handler.gameObjects.removeAll(handler.gameObjects);
		handler.tiles.removeAll(handler.tiles);
		handler.walls.removeAll(handler.walls);
		handler.arrowPickups.removeAll(handler.arrowPickups);
		if(level == 0) {
			int[][] map = {{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01,01, 01, 01, 01, 01, 01, 01, 01, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 10, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 25, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 } };
			drawMap(map);
		}
		if (level == 1) {

			int[][] map = {
					{01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 10, 00, 01, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01}};
			drawMap(map);

		}

		if (level == 2) {

			int[][] map = {
					{01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 10, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 01},
					{01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01}};
			drawMap(map);
		}
		if (level == 3) {

			int[][] map = {
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 20, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 23, 00, 23, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 20, 00, 20, 01, 00, 00, 00, 00, 00, 22, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 31, 01, 00, 00, 00, 00, 00, 22, 00, 00, 00, 31, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 24, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 23, 00, 00, 00, 23, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 23, 23, 00, 00, 01 },
					{ 01, 00, 12, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 32, 33, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 23, 00, 00, 32, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 23, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 23, 00, 23, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 23, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 23, 00, 00, 23, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 23, 00, 00, 01 },
					{ 01, 00, 00, 00, 21, 00, 00, 00, 00, 01, 00, 00, 23, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 23, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 23, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 } };
			drawMap(map);
		}
		if (level == 4) {

			int[][] map = {
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 00, 01, 00, 23, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 23, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 20, 00, 00, 00, 01, 00, 23, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 23, 00, 00, 00, 00, 00, 01, 00, 00, 20, 20, 20, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 13, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 01, 00, 23, 00, 00, 00, 00, 00, 01, 00, 00, 20, 20, 20, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 23, 00, 00, 00, 00, 00, 01, 00, 00, 20, 20, 20, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 01, 00, 23, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 20, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 20, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 } };
			drawMap(map);
		}
		if (level == 5) {

			int[][] map = {
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 20, 00, 31, 00, 00, 20, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 20, 00, 20, 00, 00, 00, 00, 20, 00, 00, 20, 00, 00, 20, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 01, 01, 01, 00, 00, 00, 01, 01, 01, 00, 00, 00, 31, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 10, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 20, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 01, 01, 01, 00, 00, 00, 01, 01, 01, 00, 00, 00, 01, 01, 01, 01, 01, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 20, 00, 20, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 20, 00, 32, 00, 00, 00, 00, 00, 00, 00, 20, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 } };
			drawMap(map);

		}
		if (level == 6) {

			int[][] map = {
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 20, 00, 00, 01, 00, 20, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 20, 00, 01, 00, 00, 00, 20, 00, 00, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 20, 00, 00, 01, 00, 20, 20, 00, 00, 01, 00, 00, 20, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 20, 00, 01, 00, 00, 00, 31, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 20, 00, 01 },
					{ 01, 00, 10, 00, 00, 00, 00, 31, 00, 00, 00, 00, 32, 20, 00, 00, 00, 00, 33, 00, 00, 00, 00, 00, 00, 00, 00, 00, 33, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 20, 00, 00, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 01, 01, 01, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 20, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 20, 00, 00, 01, 00, 20, 20, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 32, 00, 00, 00, 01, 00, 00, 20, 20, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 20, 00, 01, 00, 20, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 } };
			drawMap(map);

		}
		if (level == 7) {

			int[][] map = {
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 20, 00, 00, 00, 20, 20, 00, 20, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 20, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 20, 00, 00, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 20, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 00, 00, 00, 01, 00, 20, 00, 01, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 31, 00, 20, 00, 01, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 20, 00, 01, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 01, 00, 00, 31, 00, 00, 00, 00, 32, 20, 00, 00, 20, 00, 33, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 00, 20, 00, 01, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 01, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 20, 00, 00, 20, 00, 00, 00, 00, 00, 20, 00, 01, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01 },
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 20, 20, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 00, 10, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 00, 20, 00, 00, 00, 20, 00, 00, 00, 00, 00, 00, 00, 20, 00, 00, 00, 20, 00, 01 },
					{ 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01 },
					{ 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01 } };
			drawMap(map);

		}

		if (level == 8) {
			int [][] map = {{01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 01, 01, 01, 01, 01, 00, 00, 01}, 
					{01, 00, 00, 00, 01, 00, 00, 21, 22, 00, 01, 01, 00, 22, 23, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 00, 00, 01, 00, 00, 20, 21, 00, 00, 01, 00, 00, 00, 22, 00, 00, 01, 01, 00, 00, 00, 00, 00, 01, 00, 22, 21, 00, 21, 00, 01}, 
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 01, 01, 01, 00, 21, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 23, 00, 01}, 
					{01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 01, 01, 00, 00, 00, 00, 01, 01, 01, 01, 00, 00, 00, 01}, 
					{01, 00, 00, 00, 01, 01, 01, 01, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 01, 00, 00, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 01, 01, 01, 00, 00, 00, 00, 01, 00, 00, 22, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 01, 01, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 22, 00, 00, 01}, 
					{01, 00, 00, 13, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 23, 00, 00, 00, 00, 00, 01, 00, 21, 23, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 01, 00, 22, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 20, 21, 00, 00, 00, 00, 01, 00, 00, 21, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 01, 00, 21, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01}};
			drawMap(map);

		}

		if (level == 9) {
			int [][] map = {{01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 24, 24, 24, 24, 24, 24, 24, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 24, 24, 24, 24, 24, 24, 24, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 24, 24, 24, 24, 24, 24, 24, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 24, 24, 24, 24, 24, 24, 24, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 24, 24, 24, 24, 24, 24, 24, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 24, 24, 24, 24, 24, 24, 24, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 01, 01, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 20, 20, 20, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 21, 21, 21, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 22, 22, 22, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 23, 23, 23, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 24, 24, 24, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 01, 01, 01, 01, 01, 01, 01, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 01, 00, 00, 00, 33, 01, 00, 00, 00, 00, 01},
					{01, 00, 24, 24, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 23, 23, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 22, 22, 00, 01, 00, 00, 00, 00, 00, 00, 00, 32, 01, 00, 00, 00, 00, 01},
					{01, 00, 21, 21, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 20, 20, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 31, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 20, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 30, 01, 00, 00, 10, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 01},
					{01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01}};

			drawMap(map);

		}

		if (level == 10) {
			int [][] map = {{01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 20, 21, 22, 23, 24, 25, 00, 00, 00, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 20, 21, 22, 23, 24, 25, 00, 00, 00, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 00, 00, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 20, 21, 22, 23, 24, 25, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 20, 21, 22, 23, 24, 25, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 01, 00, 00, 00, 01, 00, 30, 00, 31, 00, 32, 00, 33, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 10, 00, 01}, 
					{01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 20, 21, 22, 23, 24, 25, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 01, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 00, 01}, 
					{01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01, 01,01}};
			drawMap(map);

		}
	}



	int tileWidth = 50;
	//Takes parameter for tilemap
	//No return
	//Prints and adds everything to handler. Also "moves camera" to center player.
	private void drawMap(int[][] map) {
		for(int i = 0; i < map.length; i ++) {
			for(int j = 0; j < map[i].length; j ++) {
				if(map[i][j] == 1) {
					Tile tile = new Tile(j * tileWidth, i * tileWidth, new ImageIcon("wall.png").getImage(), handler);
					handler.walls.add(tile);
				}
				else {
					if(map[i][j] != 0 ) {
						if(map[i][j] < 20){
							Player player = new Player(j * tileWidth, i * tileWidth, playerArrowStats[map[i][j] - 10], handler);
						}
						else if(map[i][j] < 30){
							Enemy enemy = new Enemy(enemyStats[map[i][j] - 20], j * tileWidth, i * tileWidth, gameSpeed, handler);
						}
						else {
							ArrowPickup arrowPickup = new ArrowPickup(playerArrowStats[map[i][j] - 30], j * tileWidth, i * tileWidth, handler);
						}
					}
					Tile tile = new Tile(j * tileWidth, i * tileWidth, new ImageIcon("tile1.png").getImage(), handler);
				}
			}
		}
		handler.cameraMove(1, 540 - handler.player.aimX, 360 - handler.player.aimY);
	}


	//Takes MouseEvent parameter
	//No return
	//Sets mouse position and shoots
	public void mousePressed(MouseEvent e) {

		mousePos[0] = e.getX();
		mousePos[1] = e.getY();

		Shoot(handler.player.arrowStats, mousePos);	
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}



}
