import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;

class Catch22
{
	public static void main(String ... bobby) throws Exception
	{
		JFrame frame = new FullScreenFrame();		
		Screen screen = new Screen();
		frame.add(screen);
		frame.setVisible(true);
		
		Soundtrack.play("GameMusic.mid");
		screen.intro();
		
		frame.addKeyListener(new MyListener(screen.field));
		frame.setFocusable(true);
		
		while (true)
		{
			screen.field.act();
			frame.repaint();
			Thread.sleep(2);
			
			if (screen.gameOver)
			{
				Thread.sleep(3000);
				System.exit(0);
			}
		}
	}
}
