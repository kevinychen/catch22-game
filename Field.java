import java.util.*;
import java.awt.*;

class Field
{
	static final int FRAME_WIDTH = 600;
	static final int FRAME_HEIGHT = 800;
	static final int WIDTH = 400;
	static final int HEIGHT = 600;
	static final int START_X = 100;
	static final int START_Y = 100;
	
	static final int MAX_SIGHT = 3000;
	static final double SPEED = 2.0;
	static final int TURN_BUFFER = 400;
	static final int PASS_BUFFER = 200;
	static final int NUM_TURNS = 50;
	
	static final double BARRIER_PROB = .1;
	static final int BARRIER_HEIGHT = 150;
	static final int BARRIER_BUFFER = 3;
	
	static final int START_MISSIONS = 10;
	static final int MISSION_LENGTH = 20;
	static final int MISSION_BUFFER = 5;
	
	Screen screen;
	
	double playerX, playerY, playerZ;
	double turn;
	
	int numMissions;
	int neededMissions;
	int missionBuffer;
	int barrierBuffer;
	
	int turnIndex;
	Turn[] centerHinges;
	
	boolean up, down, left, right;
	boolean crashed;
	
	Field(Screen screen_)
	{
		screen = screen_;
		
		playerX = 0.0;
		playerY = 500.0;
		playerZ = 1.0;
		turn = 0.0;
		
		numMissions = 0;
		neededMissions = START_MISSIONS;
		
		turnIndex = 0;
		centerHinges = new Turn[NUM_TURNS];
		centerHinges[0] = centerHinges[1] = new Turn(0, 0, 0, 0);
		for (int i = 2; i < NUM_TURNS; i++)
			createTurn(i);
		
		up = down = left = right = false;
		crashed = false;
	}
	
	int playerX()
	{
		return (int)playerX;
	}
	
	int playerY()
	{
		return (int)playerY;
	}
	
	int playerZ()
	{
		return (int)playerZ;
	}
	
	Polygon region(int index)
	{
		Polygon region = new Polygon();
		addPoint2D(region, leftHinge(index));
		addPoint2D(region, leftHinge(index + 1));
		addPoint2D(region, rightHinge(index + 1));
		addPoint2D(region, rightHinge(index));
		return region;
	}
	
	Point2D leftHinge(int index)
	{
		Turn turn = centerHinges[index];
		double angle = (turn.angle + turn.prevAngle) / 2 + Math.PI / 2;
		double x = turn.x - WIDTH / 2 * Math.sin(angle);
		double z = turn.z - WIDTH / 2 * Math.cos(angle);
		return new Point2D(x, z);
	}
	
	Point2D rightHinge(int index)
	{
		Turn turn = centerHinges[index];
		double angle = (turn.angle + turn.prevAngle) / 2 + Math.PI / 2;
		double x = turn.x + WIDTH / 2 * Math.sin(angle);
		double z = turn.z + WIDTH / 2 * Math.cos(angle);
		return new Point2D(x, z);
	}
	
	void act()
	{
		if (crashed) return;
		
		if (missionBuffer == 0)
			setMissions();
		
		playerZ += SPEED * Math.cos(turn);
		playerX += SPEED * Math.sin(turn);
		
		if (!region(1).contains(playerX, playerZ))
		{
			for (int i = 0; i < NUM_TURNS - 1; i++)
				centerHinges[i] = centerHinges[i + 1];
			createTurn(NUM_TURNS - 1);
			
			if (!region(1).contains(playerX, playerZ))
				crash();
			
			turnIndex++;
			if (missionBuffer > 0)
				missionBuffer--;
			if (barrierBuffer > 0)
				barrierBuffer--;
		}
		if (playerY + 100 >= centerHinges[3].barrierY
			&& playerY + 100 <= centerHinges[3].barrierY + BARRIER_HEIGHT)
			{
				crash();
			}
		
		if (up && playerY > START_Y + 50)
			playerY--;
		if (down && playerY < START_Y + HEIGHT - 150)
			playerY++;
		if (left)
			turn -= .005;
		if (right)
			turn += .005;
	}
	
	void crash()
	{
		crashed = true;
		screen.showMessage(36, 300, "YOU LOSE.");
		Soundtrack.play("explosion.wav");
	}
	
	private void createTurn(int index)
	{
		double dist = 100;
		double newTurn = (index == NUM_TURNS - 1 && Math.random() < .1 ? Math.random() * 2 - 1 : 0);
		Turn prev = centerHinges[index - 1];
		double newX = prev.x + dist * Math.sin(prev.angle);
		double newZ = prev.z + dist * Math.cos(prev.angle);
		int barrierY = (index == NUM_TURNS - 1 && barrierBuffer == 0 && Math.random() < BARRIER_PROB ?
			(int)(Math.random() * (HEIGHT - BARRIER_HEIGHT)) + START_Y : -1);
		if (barrierY != -1)
			barrierBuffer = BARRIER_BUFFER;
		centerHinges[index] = new Turn(newX, newZ, prev.angle + newTurn, prev.angle, barrierY);
	}
	
	private void setMissions()
	{
		if (turnIndex == 0)
		{
			screen.showMessage(24, 40, "YOU NEED " + neededMissions + " MISSIONS");
			missionBuffer = MISSION_BUFFER;
		}			
		if ((turnIndex + 3) % MISSION_LENGTH == 0)
		{
			numMissions++;
			screen.showMessage(24, 40, "YOU HAVE " + numMissions, "OUT OF " + neededMissions + " MISSIONS");
			missionBuffer = MISSION_BUFFER;
		}		
		if (Math.random() < .001 * Math.pow(.1, neededMissions - numMissions - 3))
		{
			neededMissions += 5;
			screen.showMessage(24, 40, "THE NUMBER OF MISSIONS", "HAS BEEN RAISED TO " + neededMissions);
			missionBuffer = MISSION_BUFFER;
		}
	}
	
	private void addPoint2D(Polygon po, Point2D p)
	{
		po.addPoint((int)p.x, (int)p.z);
	}
}