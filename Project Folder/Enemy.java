package Test;

import java.awt.Rectangle;

class Enemy extends Rectangle {
	int health = 1;
	double vx = Math.random()*2+0.5;
	double vy = Math.random()*2+0.5;
	Enemy() {
		width = height = 50;
		x = (int)(Math.random() * (gameProject.WINW - width));
		y = (int)(Math.random() * (gameProject.WINH - width - 200));	//enemies spawn higher up so you don't insta die and have some time to react them coming down
	}
}