package chb;

import java.util.*;

public class LPA {
	
	public int nodenum;
	
	public LinkedList neighborlist[];
	
	ReadData rd;
	
	Random rand;
	
	int turns[];
	
	int label[];
	
	int adjacent[][];
	
	public LPA(String file)
	{
		rd = new ReadData(file);
		nodenum = rd.nodenum;
		
		neighborlist = new LinkedList [nodenum];
		for(int i=0;i<nodenum;i++)
			neighborlist[i] = new LinkedList<Integer>();
		
		label = new int[nodenum];
		for(int i=0;i<nodenum;i++)
			label[i] = i;
				
		turns = new int[nodenum];
		for(int i=0;i<nodenum;i++)
			turns[i] = i;
		
		rand = new Random(47);
		neighborlist = rd.neighborlist;
		
	}
	
	
	public void shuffle()
	{
		
		for(int i=0;i<nodenum/2;i++)
		{
			
			int index1 = rand.nextInt(nodenum);
			int index2 = rand.nextInt(nodenum);
			int temp;
			if(index1 != index2)
			{
				temp = turns[index1];
				turns[index1] = turns[index2];
				turns[index2] = temp;
				
			}			
			
		}
						
	}
	
	
	public int mostfrequentlabel(int nodeid)
	{
		HashMap<Integer,Integer> labelmap = new HashMap<Integer,Integer>();
		
		for(int i=0;i<neighborlist[nodeid].size();i++)
		{
			int neighbor_index = (Integer)neighborlist[nodeid].get(i);
			int neighbor_label = label[neighbor_index];
			Integer frequency = labelmap.get(neighbor_label);
			
			if(frequency == null)
			{
				labelmap.put(neighbor_label, 1);
			}
			else
			{
				labelmap.put(neighbor_label, frequency + 1);
			}
			
		}
		
		
		int maxfrequency = 0;
		int maxlabel = 0;
		
		LinkedList<Integer> candicate = new LinkedList<Integer>();
		
		for(Integer in : labelmap.keySet())
		{
			if(labelmap.get(in) > maxfrequency)
			{
				maxfrequency = labelmap.get(in);
				maxlabel = in;
				candicate.clear();
				candicate.add(in);
			}
			else if(labelmap.get(in) == maxfrequency)
			{
				candicate.add(in);
			}
			
		}
		
		
		int size = candicate.size();
		int can_index = rand.nextInt(size);
		
		return 	candicate.get(can_index);		
				
	}
	
	
	
	public void execute()
	{
		boolean finished = false;
		
		while(!finished)
		{
			finished = true;
			
			shuffle();
			
			for(int i=0;i<nodenum;i++)
			{
				int node_id = turns[i];
				int mostfrequent = mostfrequentlabel(node_id);
				if(label[node_id] != mostfrequent)
				{
					label[node_id] = mostfrequent;
					finished = false;
					
				}
				
			}
						
			
		}
		
		
	}
	
	
	public void storeandshow()
	{
		HashMap<Integer,Integer>  remainlabels = new HashMap<Integer,Integer>();
		
		int index = 0;
		for(int i=0;i<nodenum;i++)
		{
			if(!remainlabels.containsKey(label[i]))
			{
				remainlabels.put(label[i], index);
				index++;
			}
			
		}
		
		int communitynumber = index;
		LinkedList nodelist[] = new LinkedList[communitynumber];
		for(int i=0;i<communitynumber;i++)
			nodelist[i] = new LinkedList<Integer>();
		
		for(int i=0;i<nodenum;i++)
		{
			int list_index = remainlabels.get(label[i]);
			nodelist[list_index].add(i);
			
		}
		
		for(int i=0;i<communitynumber;i++)
		{
			System.out.print("community "+i+":   ");
			for(int j=0;j<nodelist[i].size();j++)
				System.out.print(((Integer)nodelist[i].get(j)+1)+" ");
			System.out.println();
			
		}
		
		
		
	}
	
	
	public static void main(String args[])
	{
		LPA lpa = new LPA("data2/new_karate.txt");
		
		lpa.execute();
		
		lpa.storeandshow();
		
		
	}
	
	
	

}
