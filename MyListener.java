import java.awt.event.*;

class MyListener extends KeyAdapter
{
	Field field;
	
	MyListener(Field field_)
	{
		field = field_;
	}
	
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_UP)
			field.up = true;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			field.down = true;
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			field.left = true;
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			field.right = true;
			
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			field.crash();
	}
	
	public void keyReleased(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_UP)
			field.up = false;
		else if (e.getKeyCode() == KeyEvent.VK_DOWN)
			field.down = false;
		else if (e.getKeyCode() == KeyEvent.VK_LEFT)
			field.left = false;
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			field.right = false;
	}
}