package chb;
import java.util.*;

public class Community {
	
	public LinkedList<Integer> nodelist = new LinkedList<Integer>();

	public String toString()
	{
		String ss = "";
		
		for(int i=0;i<nodelist.size();i++)
		{
			int node_index = nodelist.get(i);
			ss = ss + (node_index+1) + " ";
		}
		
		ss += "\n";
		
		return ss;
	}
	
	
	public boolean equals(Object O)
	{
		if(this == O)
			return true;
		if( ! (O instanceof Community))
			return false;
		
		Community com = (Community)O;
		
		
		
		if(nodelist.size() != com.nodelist.size())
			return false;
		
		for(int i=0;i<nodelist.size();i++)
		{
			int value = nodelist.get(i);
			if(!com.nodelist.contains(value))
				return false;
		}
		
		return true;
	}
	
	
}
