import java.util.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.*;

class Screen extends JPanel
{
	static final int PANEL_WIDTH = 1280;
	static final int PANEL_HEIGHT = 800;
	static final int WIDTH = 400;
	static final int HEIGHT = 600;
	static final int START_X = 440;
	static final int START_Y = 100;
	
	static final Color LIGHT_BLUE = new Color(128, 255, 255);
	static final Color BROWN = new Color(128, 64, 0);
	static final Color MUCK = new Color(128, 128, 64);
	
	static BufferedImage HOME_SCREEN;
	static BufferedImage MY_PLANE;
	static BufferedImage LEFT_PLANE;
	static BufferedImage RIGHT_PLANE;
	static int EXPLOSION_IMAGE_INDEX;
	static BufferedImage[] EXPLOSION_IMAGES;
	static Image[] FLAGS;
	static Image[][] PICTURES;
	static
	{
		try
		{
			HOME_SCREEN = ImageIO.read(new File("Home.bmp"));
			
			MY_PLANE = purify(ImageIO.read(new File("myplane.bmp")));
			LEFT_PLANE = purify(ImageIO.read(new File("leftplane.bmp")));
			RIGHT_PLANE = purify(ImageIO.read(new File("rightplane.bmp")));
			
			EXPLOSION_IMAGE_INDEX = 0;
			EXPLOSION_IMAGES = new BufferedImage[20];
			BufferedImage explosions = purify(ImageIO.read(new File("explosions.bmp")));
			int width = explosions.getWidth() / 4;
			int height = explosions.getHeight() / 5;
			for (int i = 0; i < 5; i++)
				for (int j = 0; j < 4; j++)
				{
					EXPLOSION_IMAGES[4 * i + j] = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
					
					for (int k = 0; k < width; k++)
						for (int l = 0; l < height; l++)
							EXPLOSION_IMAGES[4 * i + j].setRGB(k, l, explosions.getRGB(width * j + k, height * i + l));
				}
				
			FLAGS = new Image[100];
			BufferedImage flag = purify(ImageIO.read(new File("flag.bmp")));
			double r = .01;
			for (int i = 0; i < 100; i++)
			{
				FLAGS[i] = flag.getScaledInstance((int)(flag.getWidth() * r), (int)(flag.getHeight() * r), 0);
				r += .01;
			}
			
			File[] pictures = new File("pictures").listFiles();
			PICTURES = new Image[pictures.length][100];
			for (int i = 0; i < pictures.length; i++)
			{
				BufferedImage current = purify(ImageIO.read(pictures[i]));
				for (int j = 1; j < 40; j++)
				{
					r = (double)current.getWidth() / current.getHeight();
					PICTURES[i][j] = current.getScaledInstance((int)(5 * j * r), 5 * j, 0);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	boolean starting;
	
	BufferedImage homeImage;
	
	Field field;
	boolean gameOver;
	
	int messageTime;
	BufferedImage message;
	int startX, startY;
	
	Screen()
	{
		setBackground(Color.BLACK);
		starting = false;
		field = new Field(this);
		gameOver = false;
		messageTime = 0;
	}
	
	public void paintComponent(Graphics g)
	{		
		super.paintComponent(g);
		
		if (starting)
		{
			drawSky(g);
			drawGround(g);
			drawCliffs(g);
			drawHelicopter(g);
			
			if (messageTime > 0)
			{
				g.drawImage(message, START_X, START_Y + (HEIGHT - message.getHeight()) / 2, null);
				messageTime--;
			}
			
			refillBackground(g);
			
			printScore(g);
			
			//g.setColor(Color.WHITE);
			//g.drawString("" + field.playerY(), 0, 100);
		}
		else
		{
			if (homeImage != null)
				g.drawImage(homeImage.getScaledInstance(PANEL_WIDTH, PANEL_HEIGHT, 0), 0, 0, null);
		}
	}
	
	void intro()
	{
		double r = 4;
		while (r >= 1)
		{
			homeImage = HOME_SCREEN.getSubimage(
				(int)(HOME_SCREEN.getWidth() / 2 * (1 - 1 / r)), (int)(HOME_SCREEN.getHeight() / 2 * (1 - 1 / r)),
				(int)(HOME_SCREEN.getWidth() / r), (int)(HOME_SCREEN.getHeight() / r));
			
			repaint();
			delay(2);
			r -= .01;
		}
		delay(4000);
		starting = true;
	}
	
	void showMessage(int size, int time, String ... s)
	{
		BufferedImage image = new BufferedImage(WIDTH, (size + 10) * s.length, BufferedImage.TYPE_4BYTE_ABGR);
		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight(); j++)
				image.setRGB(i, j, 0x00ffffff);
				
		Graphics g = image.createGraphics();
		g.setColor(Color.BLUE);
		g.setFont(new Font("Times New Roman", 0, size));
		for (int i = 0; i < s.length; i++)
			g.drawString(s[i], messageStart(size, s[i]), (size + 10) * (i + 1));
		
		messageTime = time;
		message = image;
	}
	
	private int messageStart(int size, String message)
	{
		BufferedImage image = new BufferedImage(WIDTH, size + 10, BufferedImage.TYPE_4BYTE_ABGR);
		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight(); j++)
				image.setRGB(i, j, 0x00ffffff);
				
		Graphics g = image.createGraphics();
		g.setColor(Color.BLUE);
		g.setFont(new Font("Times New Roman", 0, size));
		g.drawString(message, 0, size + 9);
		
		int maxX = 0;
		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight(); j++)
				if (image.getRGB(i, j) != 0x00ffffff && i > maxX)
					maxX = i;
				
		return (WIDTH - (maxX + 10)) / 2;
	}
	
	private Point imageOf(double x, double y, double z)
	{
		double dx = x - field.playerX;
		double dz = z - field.playerZ;
		double seenX = dx * Math.cos(field.turn) - dz * Math.sin(field.turn);
		double seenZ = dx * Math.sin(field.turn) + dz * Math.cos(field.turn);
		return image(seenX, y, seenZ);
	}
	
	private Point convert(Point2D p)
	{
		double dx = p.x - field.playerX;
		double dz = p.z - field.playerZ;
		double seenX = dx * Math.cos(field.turn) - dz * Math.sin(field.turn);
		double seenZ = dx * Math.sin(field.turn) + dz * Math.cos(field.turn);
		return new Point((int)seenX, (int)seenZ);
	}
	
	private Point image(double x, double y, double z)
	{	
		double dx = WIDTH / 2 + 100;
		double x_ = dx / (Math.PI / 2) * Math.atan(x / z);
		if (z < 0)
			x_ = (x > 0 ? x + dx : x - dx);
		
		y -= field.playerY;
		double dy = (y > 0 ? START_Y + HEIGHT - field.playerY : field.playerY - START_Y) + 100;
		double y_ = dy / (Math.PI / 2) * Math.atan(y / z);
		if (z < 0)
			y_ = (y > 0 ? y + dy : y - dy);
		
		return new Point((int)(START_X + WIDTH / 2 + x_), (int)(field.playerY + y_));
	}
	
	private void drawSky(Graphics g)
	{
		g.setColor(LIGHT_BLUE);
		g.fillRect(START_X, START_Y, WIDTH, field.playerY() - START_Y);
	}
	
	private void drawGround(Graphics g)
	{
		g.setColor(MUCK);
		g.fillRect(START_X, field.playerY(), WIDTH, START_Y + HEIGHT - field.playerY());
	}

	private void drawCliffs(Graphics g)
	{
		Point[][] points = new Point[field.NUM_TURNS][4];
		for (int i = 0; i < field.NUM_TURNS; i++)
		{
			Point2D leftHinge = field.leftHinge(i);
			Point2D rightHinge = field.rightHinge(i);
			points[i][0] = imageOf(leftHinge.x, START_Y, leftHinge.z);
			points[i][1] = imageOf(rightHinge.x, START_Y, rightHinge.z);
			points[i][2] = imageOf(leftHinge.x, START_Y + HEIGHT, leftHinge.z);
			points[i][3] = imageOf(rightHinge.x, START_Y + HEIGHT, rightHinge.z);
		}
		
		for (int i = points.length - 2; i >= 0; i--)
			for (int j = 0; j < 2; j++)
			{				
				// draw cliff piece
				drawPolygon(g, new Point[] {points[i + 1][j], points[i + 1][j + 2], points[i][j + 2],
					points[i][j]}, BROWN, Color.BLACK);
				
				Turn current = field.centerHinges[i];
				
				// draw barrier if necessary
				if (current.barrierY != -1)
				{
					Point2D leftHinge = field.leftHinge(i);
					Point2D rightHinge = field.rightHinge(i);
					Point[] barrierPoints = new Point[4];
					barrierPoints[0] = imageOf(leftHinge.x, current.barrierY, leftHinge.z);
					barrierPoints[1] = imageOf(leftHinge.x, current.barrierY + Field.BARRIER_HEIGHT, leftHinge.z);
					barrierPoints[2] = imageOf(rightHinge.x, current.barrierY + Field.BARRIER_HEIGHT, rightHinge.z);
					barrierPoints[3] = imageOf(rightHinge.x, current.barrierY, rightHinge.z);
					drawPolygon(g, barrierPoints, BROWN, Color.BLACK);
						
					if (current.imageNum < PICTURES.length)
					{					
						int height = (barrierPoints[1].y - barrierPoints[0].y + barrierPoints[2].y - barrierPoints[3].y) / 2;
						int index = height / 5 > 99 ? 99 : height / 5;
						g.drawImage(PICTURES[current.imageNum][index], (2 * barrierPoints[0].x + barrierPoints[3].x) / 3, barrierPoints[0].y, null);
						//drawPicture(g, PICTURES[current.imageNum], current.x, current.barrierY, current.z);
					}
				}
				
				// draw flag if necessary
				if ((field.turnIndex + i + 1) % field.MISSION_LENGTH == 0)
					drawFlag(g, current.x, HEIGHT - 50, current.z);
			}
	}
	
	private void drawHelicopter(Graphics g)
	{
		if (field.crashed)
		{			
			if (EXPLOSION_IMAGE_INDEX < EXPLOSION_IMAGES.length)
			{
				g.drawImage(EXPLOSION_IMAGES[EXPLOSION_IMAGE_INDEX],
					START_X + (WIDTH - EXPLOSION_IMAGES[0].getWidth()) / 2, field.playerY(), null);
				
				EXPLOSION_IMAGE_INDEX++;
			}
			else
				gameOver = true;
		}
		else if (field.left)
			g.drawImage(LEFT_PLANE, START_X + (WIDTH - LEFT_PLANE.getWidth()) / 2, field.playerY(), null);
		else if (field.right)
			g.drawImage(RIGHT_PLANE, START_X + (WIDTH - RIGHT_PLANE.getWidth()) / 2, field.playerY(), null);
		else
			g.drawImage(MY_PLANE, START_X + (WIDTH - MY_PLANE.getWidth()) / 2, field.playerY(), null);
	}
	
	private void printScore(Graphics g)
	{
		String message = field.numMissions + "/" + field.neededMissions + " MISSIONS";
		int start = messageStart(48, message);
		g.setColor(Color.WHITE);
		g.setFont(new Font("Times New Roman", 0, 48));
		g.drawString(message, START_X + start, START_Y - 20);
	}
	
	private void drawPolygon(Graphics g, Point[] p, Color inside, Color border)
	{
		Polygon po = new Polygon();
		for (int i = 0; i < p.length; i++)
			addPoint(po, p[i]);
			
		g.setColor(inside);
		g.fillPolygon(po);
		
		g.setColor(border);
		for (int i = 0; i < p.length; i++)
			drawLine(g, p[i], p[(i + 1) % p.length]);
	}
	
	private void drawFlag(Graphics g, double x, double y, double z)
	{
		int z_ = convert(new Point2D(x, z)).y;
		if (z_ > 100 && z_ < 10000)
			drawImage(g, FLAGS[(int)(1000000.0 / (z_ * z_))], imageOf(x, y, z));
	}
	
	private void refillBackground(Graphics g)
	{
		g.setColor(Color.BLACK);
		
		g.fillRect(0, 0, PANEL_WIDTH, START_Y);
		g.fillRect(0, START_Y + HEIGHT, PANEL_WIDTH, PANEL_HEIGHT - HEIGHT - START_Y);
		g.fillRect(0, 0, START_X, PANEL_HEIGHT);
		g.fillRect(START_X + WIDTH, 0, PANEL_WIDTH - START_X - WIDTH, PANEL_HEIGHT);
	}
	
	private static BufferedImage purify(BufferedImage image)
	{
		BufferedImage newImage =
			new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
		
		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight(); j++)
				newImage.setRGB(i, j, image.getRGB(i, j) == 0xffffffff ? 0x00ffffff : image.getRGB(i, j));
				
		return newImage;
	}
	
	private static void addPoint(Polygon po, Point p)
	{
		po.addPoint(p.x, p.y);
	}
	
	private static void drawLine(Graphics g, Point p1, Point p2)
	{
		g.drawLine(p1.x, p1.y, p2.x, p2.y);
	}
	
	private static void drawImage(Graphics g, Image image, Point p)
	{
		g.drawImage(image, p.x, p.y, null);
	}
	
	private static void delay(int ms)
	{
		long time = System.currentTimeMillis();		
		while (System.currentTimeMillis() - time < ms);
	}
}