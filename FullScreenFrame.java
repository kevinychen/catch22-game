import java.awt.*;
import javax.swing.*;

class FullScreenFrame extends JFrame
{
	FullScreenFrame()
	{
		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension dimension = toolkit.getScreenSize();

		setResizable(false);
		setSize(dimension.width, dimension.height);
		setUndecorated(true);
		GraphicsDevice gd = getGraphicsConfiguration().getDevice();
		gd.setFullScreenWindow(this);
	}
}