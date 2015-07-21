package chb;

public class Edge {

	public int source;
	public int target;
	public double total;
	
	public Edge(int s, int t)
	{
		source = s;
		target = t;
	}
	
	
	public int getSource()
	{
		
		return source;
	}
	
	public int getTarget()
	{
		return target;
	}
	
	public double getTotal()
	{
		return total;
	}
	
	public void setTotal(double tt)
	{
		total = tt;
	}
}
