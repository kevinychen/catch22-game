
class Turn
{
	final double x, z;
	final double angle, prevAngle;
	final int barrierY;
	final int imageNum;
	
	Turn(double x_, double z_, double angle_, double prevAngle_)
	{
		this(x_, z_, angle_, prevAngle_, -1);
	}
	
	Turn(double x_, double z_, double angle_, double prevAngle_, int barrierY_)
	{
		x = x_;
		z = z_;
		angle = angle_;
		prevAngle = prevAngle_;
		barrierY = barrierY_;
		imageNum = (int)(Math.random() * 20);
	}
	
	public String toString()
	{
		return String.format("%.2f %.2f %.2f %.2f", x, z, angle, prevAngle);
	}
}