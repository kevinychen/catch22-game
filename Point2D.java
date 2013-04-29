
class Point2D
{
	double x, z;
	
	Point2D(double x_, double z_)
	{
		x = x_;
		z = z_;
	}
	
	public String toString()
	{
		return String.format("(%.2f, %.2f)", x, z);
	}
}