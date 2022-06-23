package Test;

import java.awt.Rectangle;

class Player extends Rectangle {		
	int speed = 2;
	Player(int x, int y) {
		this.x = x;
		this.y = y;
		width = 75;
		height = 50;			
	}
}