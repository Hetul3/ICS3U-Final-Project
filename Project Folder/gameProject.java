//Hetul, spaceinvaders esc game the untitled folder.zip just had my original which had inner methods, this one has seperate methods
package Test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.text.AttributeSet.FontAttribute;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import hsa2.GraphicsConsole;


public class gameProject implements ActionListener{
	public static void main(String[] args) {
		new gameProject().run();
	}

	static final int WINW = 600; 
	static final int WINH = 600; 
	static int MAXLASERS = 3;		//max lasers that are allowed on screen, right now they're set to one for balancing reasons since the player would just waste all of them
	static int MAXBOMBS = 1;		//max bombs that are allowed on screen
	int bombs = 3;					//number of bombs that the player has access to
	int lasercount = 170;			//number of lasers they have
	int deaths = 0;					//keeps track of your deaths
	boolean bombexplode = false;
	boolean running = true;			//the thing that runs
	boolean alive = true;			//is the player alive
	boolean invincible = false;		//is the player in invincibility mode
	int level = 1;					//which level you are in
	GraphicsConsole gc = new GraphicsConsole(WINW,WINH);
	GraphicsConsole start = new GraphicsConsole(WINW, WINH);	//intro screen
	Player player = new Player(WINW/2, WINH-150);
	ArrayList<Projectile> laserList = new ArrayList<Projectile>();
	ArrayList<Bomb> bombList = new ArrayList<Bomb>();
	
	final static int TIMERSPEED = 20;
	Timer bombTimer = new Timer(TIMERSPEED, this);
	Timer shieldTimer = new Timer(TIMERSPEED, this);
	double shieldSeconds = 0.0;
	double bombSeconds = 0.0;
	
	int enemies = 1;
	ArrayList<Enemy> enemyList = new ArrayList<Enemy>();

	//main game loop
	void run() {
		BufferedImage shipImg = loadImage("ship.png");
		BufferedImage laserImg = loadImage("laserbullet.png");
		BufferedImage bombImg = loadImage("Bomb.png");
		BufferedImage enemyImg = loadImage("enemy.png");
		BufferedImage explodeImg = loadImage("explode.png");
		BufferedImage beImg = loadImage("bombExplode.png");
		BufferedImage shieldImg = loadImage("shield.png");
		BufferedImage bkImg = loadImage("spaceBackground.png");
		BufferedImage bk2Img = loadImage("spaceBackground2.png");
		introScreen();
		for(int i = 0; i < enemies; i++) {		//makes the enemies on the game
			enemyList.add(new Enemy());
		}
		while(running) {
			if(invincible) gc.setTitle("Level: " + level + " : Invincibility: " + (int) (4.0 - shieldSeconds));
			else gc.setTitle("Level: " + level);
			ending();
			moveLaser();
			movePlayer();
			drawGraphics(shipImg, laserImg, bombImg, enemyImg, beImg, shieldImg, bkImg, bk2Img);
			shoot();
			enemyPath();
			checkShoot();
			shieldTime();
			checkDeath();
			checkLevel();
			deadEvent(explodeImg);
			gc.sleep(3);
		}
	}
	
	private void introScreen() {
		gc.setVisible(false);
		start.setAntiAlias(true);
		start.setTitle("Welcome Captain!");
		start.enableMouse();
		Font introTitle = new Font("arial", Font.BOLD, 35);
		Font introFont = new Font("arial", Font.BOLD, 20);
		start.setBackgroundColor(Color.BLACK);
		start.clear();
		start.setFont(introTitle);
		start.setColor(Color.WHITE);
		start.drawString("Welcome Captain to the Centurium", 10, 50);
		start.setFont(introFont);
		start.drawString("The Glorg have been infaltrating and multiplying", 65, 80);
		start.drawString("We have limited resources so don't waste them", 67, 110);
		start.drawString("WASD to move, O for bombs, P for laser", 90, 140);
		
		button();
		gc.setVisible(true);
		start.setVisible(false);
		
	}

	private void button() {
		Font startButton = new Font("copper", Font.BOLD, 40);
		
		Rectangle btn = new Rectangle(40, 500, 530, 50);
		start.setColor(Color.WHITE);
		start.drawRect(btn.x, btn.y, btn.width, btn.height);
		start.setFont(startButton);
		start.drawString("Click Anywhere to Start", 62, 538);
		
		while (true) {
			if(start.getMouseClick() > 0) {
				return;
			}
			start.sleep(1);
		}
	}

	//in the case you die and asks you if you want to continue or close the game
	void deadEvent(BufferedImage explodeImg) {
		if(alive == false) {
			deaths++;
			gc.drawImage(explodeImg, player.x ,player.y, player.width + 20, player.height + 40);
			int reset = JOptionPane.showConfirmDialog(null, "Your Crew Needs You Capatain\nYou've died " + deaths + "\nKeep Going?", null, JOptionPane.YES_NO_OPTION);
			if(reset == 0) {
				alive = true;
				invincible = true;
			}
			else {
				running = false;
				gc.close();
				System.exit(0);
			}
		}
	}

	//when you finish all 10 levels, it gives you are points as calculated through the amount of lasers and bombs you have left and how many 
	//times you died. Asks you if you want to play again from the beginning or close the game
	private void ending() {
		if(level == 11 || (bombs == 0 && lasercount == 0)) {
			while(enemyList.size() > 1) {
				enemyList.remove(0);
			}
			int points = (lasercount*5 + bombs*15)/(deaths+1);
			String dialog = "You Ran out of Resources";
			if(level == 11) dialog = "You Finished!";
			int reset = JOptionPane.showConfirmDialog(null, dialog + "\nYour Score is " + points + "\nPlay Again", null, JOptionPane.YES_NO_OPTION);	
			if(reset == 1) {
				running = false;
				gc.close();
				System.exit(0);
			}
			else {
				level = 1;
				deaths = 0;
				bombs = 3;
				lasercount = 120;
			}
		}
	}

	//if you decide to keep playing when you died, you are given a period of invincibility
	private void shieldTime() {
		if(invincible) {
			shieldTimer.start();
			if(shieldSeconds > 3.0) {
				shieldTimer.stop();
				shieldSeconds = 0;
				invincible = false;
			}
		}
	}
	
	//checks the level and updates the level after you finish the previous level
	private void checkLevel() {
		if(enemyList.size() == 0) {
			level++;
			enemies = level;
			for(int i = 0; i < laserList.size(); i++) {
				laserList.remove(i);
				MAXLASERS++;
			}
			gc.showDialog(11-level + " levels left", "Level "+level);
			for(int i = 0; i < enemies; i++) {
				enemyList.add(new Enemy());
			}
			player.x = WINW/2 - player.width;
			player.y = WINH - 50 - player.height;
			invincible = false;
		}
	}
	
	//sets up the timers and their seconds
	public void actionPerformed(ActionEvent ev) {
		bombSeconds+=TIMERSPEED/1000.0;
		shieldSeconds+=TIMERSPEED/1000.0;
	}
	
	//checks if you have died through intersecting an enemy
	private void checkDeath() {
		for(int i = 0; i < enemyList.size(); i++) {
			Enemy e = enemyList.get(i);
			if(e.health <= 0) enemyList.remove(i);
			if(invincible == false) {
				if(e.intersects(player)) alive = false;
			}
		}
	}

	//Checks if your laser shot hit the enemy
	//in the case when the bomb hits an enemy, it will expand its hitbox to hit more, change its sprite, and start a timer to only keep it 
	//up temporarily
	private void checkShoot() {
		for(int i = 0; i < laserList.size(); i++) {
			Projectile p = laserList.get(i);
			for(int j = 0; j < enemyList.size(); j++) {
				Enemy e = enemyList.get(j);
				if(p.intersects(e)) {
					e.health--;
					laserList.remove(p);
					MAXLASERS++;
				}
			}
		}
		
		for(int i = 0; i < bombList.size(); i++) {
			Bomb b = bombList.get(i);
			for(int j = 0; j < enemyList.size(); j++) {
				Enemy e = enemyList.get(j);
				if(b.intersects(e)) {
					e.health--;
					b.width = 150;
					b.height = 150;
					b.speed = 0;
					bombexplode = true;		//used to check which sprite to make the bomb later
					bombTimer.start();
				}
				if(bombSeconds > 0.25) {
					bombList.remove(i);
					MAXBOMBS++;
					bombexplode = false;
					bombTimer.stop();
					bombSeconds = 0;
				}
			}
		}
	}
	
	//the method that tells how the enemies should move
	private void enemyPath() {
		for(Enemy e : enemyList) {
			e.x +=e.vx;
			e.y += e.vy;
			
			if(e.x > (WINW - e.width)) {
				e.vx*=-1;
				e.x = WINW - e.width;
			}
			if(e.x < 0) {
				e.vx*=-1;
				e.x = 0;
			}
			if(e.y > (WINH - e.height)) {
				e.vy*=-1;
				e.y = WINH - e.height;
			}
			if(e.y <= 0) {
				e.vy*=-1;
				e.y = 5;
			}
		}
		
	}

	//shooting the laser and bomb, makes sure that only 3 lasers or 1 bomb can be present on screen at one time
	void shoot() {
		if(invincible == false) {
			long now = System.currentTimeMillis();
			if(now - Projectile.lastShot < Projectile.shotDelay || MAXLASERS <= 0) return;
			if(now - Bomb.lastShot < Bomb.shotDelay || MAXBOMBS <= 0) return;
			Bomb.lastShot = now;
			Projectile.lastShot = now;
			if(lasercount > 0) {
				if(gc.isKeyDown('O') && now - Projectile.lastShot < Projectile.shotDelay) {
					laserList.add(new Projectile(player));
					MAXLASERS--;
					lasercount--;
				}
			}
			if(bombs > 0) {
				if(gc.isKeyDown('P') && now - Bomb.lastShot < Bomb.shotDelay) {
					bombList.add(new Bomb(player));
					MAXBOMBS--;
					bombs--;
				}
			}
		}
	}
	
	//shoots the laser and bomb out of the player
 	void moveLaser() {
		for(int i = 0; i < laserList.size(); i++) {
			Projectile p = laserList.get(i);
			if(p.y < 0) {
				laserList.remove(i);
				MAXLASERS++;
			}
			else p.y-=p.speed;
		}
		for(int i = 0; i < bombList.size(); i++) {
			Bomb q = bombList.get(i);
			if(q.y < 0) {
				bombList.remove(i);
				MAXBOMBS++;
			}
			else q.y-=q.speed;
		}
	}

 	//moves the player and makes it so that when going off the screen you come back on the other side
	void movePlayer() {
		//use WASD
		//there is a bug where if you are holding down whatever key before dying, the game 
		//will think you are still holding it and move you in that direction. The 
		//way to fix it is through pressing that key again. Neither of us were able to figure a soluion to this
		//you can test this out through this code, the game still thinks you are pressing down the button
		/* if(gc.isKeyDown('W')) {
		 * 		player.y-=player.speed;
		 * 		System.out.println("W");
		 * 		System.out.println("N");
		 * }
		 * Removing the option page in the deadEvent and making the player invincible and alive without the option page 
		 * removes this problem. Personally I don't think it is a large problem but if it feels like that then you can remove the 
		 * options in the deadEvent to make the player invincible without a pop-up and the bug won't happen
		 * */
		if (gc.isKeyDown('W')) player.y-=player.speed;
		if (gc.isKeyDown('S')) player.y+=player.speed;
		if (gc.isKeyDown('A')) player.x-=player.speed;
		if (gc.isKeyDown('D')) player.x+=player.speed;
		
		if(player.y < -player.height) player.y = WINH;
		if(player.y > WINH) player.y = 1;
		if(player.x > WINW) player.x = 1;
		if(player.x < -player.width) player.x = WINW;

	}

	//draws all the sprite images
	void drawGraphics(BufferedImage ship, BufferedImage laser, BufferedImage bomb, BufferedImage enemy, BufferedImage be, BufferedImage shield, BufferedImage background, BufferedImage background2) {
		synchronized (gc) {
			gc.clear();
			if(level%2 != 0) gc.drawImage(background, 0, 0, 1000, 1000);
			else gc.drawImage(background2, 0, 0, 1000, 1000);
			if(alive && invincible == false) {		//basic ship sprite
				gc.drawImage(ship, player.x,player.y, player.width, player.height);
			}
			else if(alive && invincible) {			//a shield sprite when the player is invincible
				gc.drawImage(shield, player.x,player.y, player.width + 15, player.height + 15);
			}
			gc.setColor(Color.BLACK);
			for(Bomb z : bombList) {				//makes the bomb either bomb sprite or explosion sprite depending on if it has hit
				if(bombexplode == false) {
					gc.drawImage(bomb, z.x, z.y - z.height, z.width, z.height);
				}
				if(bombexplode) {
					gc.drawImage(be, z.x, z.y - z.height, z.width, z.height);
				}
			}
			for(Projectile z : laserList) {			//laser sprite
				gc.drawImage(laser, z.x, z.y - z.height, z.width, z.height);
			}
			for(Enemy z : enemyList) {				//enemy sprite
				gc.drawImage(enemy, z.x, z.y, z.width, z.height);
			}
			for(int i = 0; i < bombs; i++) {
				gc.drawImage(bomb, i*50, WINH - 50, 20, 40);
			}
			gc.drawImage(laser, WINW-100, WINH-75, 35, 50);
			gc.setColor(Color.WHITE);
			gc.drawString("x" + lasercount, WINW - 50, WINH - 50);
		}
	}
	
	//loading images
	static BufferedImage loadImage(String filename) {
		BufferedImage img = null;			
		try {
			img = ImageIO.read(new File(filename));
		} catch (IOException e) {
			System.out.println(e.toString());
			JOptionPane.showMessageDialog(null, "An image failed to load: " + filename , "ERROR", JOptionPane.ERROR_MESSAGE);
		}		
		return img;
	}

} 