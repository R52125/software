package server;

public class Configuration
{
	private int defaultWinterTemp = 22;
	private int defaultSummerTemp = 28;
	private String winter = "winter";
	private String summer = "summer";
	private String stop = "stop";
	private String low = "low";
	private String medium = "medium";
	private String high = "high";

	
	public int getdefaultWinterTemp()
	{
		return defaultWinterTemp;
	}
	
	public int getdefaultSummerTemp()
	{
		return defaultSummerTemp;
	}
	
	public String getWinter()
	{
		return winter;
	}
	
	public String getSummer()
	{
		return summer;
	}
	
	public String getStop()
	{
		return stop;
	}
	
	public String getLow()
	{
		return low;
	}
	
	public String getMedium()
	{
		return medium;
	}
	
	public String getHigh()
	{
		return high;
	}
}