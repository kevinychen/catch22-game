import java.util.*;
import java.io.*;
import sun.audio.*;
import javax.sound.midi.*;

class Soundtrack
{
	static HashMap<String, AudioStream> map = new HashMap<String, AudioStream>();
	
	static void play(String filename)
	{
		try
		{
			InputStream in = new FileInputStream(filename);
    		AudioStream currentStream = new AudioStream(in);
    		AudioPlayer.player.start(currentStream);
    		map.put(filename, currentStream);
		}
		catch (IOException e2) { e2.printStackTrace(); }
	}
	
	static void stop(String filename)
	{
		AudioPlayer.player.stop(map.get(filename));
	}
}