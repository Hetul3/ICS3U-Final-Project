package Test;

import java.awt.Rectangle;

class Bomb extends Rectangle {
	static long lastShot;
	static int shotDelay = 100;
	int speed = 1;
	Bomb(Player p) {
		width = 15;
		height = 25;
		y = p.y;
		x = p.x+p.width/2;
	}
}