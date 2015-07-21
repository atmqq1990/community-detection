package chb;

import java.util.HashMap;
import java.util.LinkedList;

public class LocalGreedyExpanding {

	int nodenum;
	double afa = 1.0;
	boolean visited [];
	double fitness[];
	
	LinkedList neighborlist[];
	
	ReadData rd;
	
	HashMap weightmap[];
		
	int edgenum ;
	double totaledgeweight;
//	double avg_edgeweight ;
	double strength[];
	LinkedList<Community> communitylist = new LinkedList<Community>();
	
	
	public LocalGreedyExpanding(String file)
	{
		
		rd = new ReadData(file);
		nodenum = rd.nodenum;
							
		neighborlist = rd.neighborlist;
		
		weightmap = rd.weightmap;
		
		edgenum = rd.edgenum;
		
		totaledgeweight = rd.totaledgeweight;
		
//		avg_edgeweight = totaledgeweight / edgenum;
		
		strength = rd.strength;
		
		fitness = new double[nodenum];
		for(int i=0;i<nodenum;i++)
			fitness[i] = 0;
		
		visited = new boolean[nodenum];
		for(int i=0;i<nodenum;i++)
			visited[i] = false;
		
	}
	
	
	public void  expand()
	{
		double fitness[] = new double[nodenum];
		for(int i=0;i<nodenum;i++)
			fitness[i] = -10;
		for(int k=0;k<nodenum;k++)
		{
			
	//		System.out.println("k = "+ k);
			
			if(visited[k])
				continue;
			
			Community com = new Community();
			LinkedList<Integer> nodelist = new LinkedList<Integer>();
			nodelist.add(k);
			visited[k] = true;
			
			int lastinserted = k;
			
			LinkedList<Integer> frontnodelist = new LinkedList<Integer>();
			
			double R = strength[lastinserted];
			double kin  = 0;
			
			
			while(true)
			{
																	
				for(int i=0;i<neighborlist[lastinserted].size();i++)
				{
					int neighborindex = (Integer)neighborlist[lastinserted].get(i);
					
					if(!nodelist.contains(neighborindex) && !frontnodelist.contains(neighborindex))
						frontnodelist.add(neighborindex);
					
				}
								
				
				for(int i=0;i<frontnodelist.size();i++)
				{
		
					int neighbor = (Integer)frontnodelist.get(i);
				
					double deltakin=0;
					for(int j=0;j<neighborlist[neighbor].size();j++)
					{
						int ne = (Integer)neighborlist[neighbor].get(j);
						if(nodelist.contains(ne))
							deltakin += (Double)weightmap[neighbor].get(ne);
					}
					
					fitness[neighbor] = (deltakin*R - kin*strength[neighbor])/(R*(R+strength[neighbor]));
					
					
				}
				
				double maxfit = -1;
				int index = -1;
							
				
				for(int i=0;i<frontnodelist.size();i++)
				{
		
					int neighbor = (Integer)frontnodelist.get(i);
					
					if(fitness[neighbor] > maxfit)
					{
						maxfit = fitness[neighbor];
						index = neighbor;
					}
											
				}
			
								
				if(maxfit <= 0)
				{
					com.nodelist.addAll(nodelist);

					communitylist.add(com);
					break;
				}
				else
				{	
					for(int j=0;j<frontnodelist.size();j++)
						fitness[frontnodelist.get(j)] = -10;
					
					nodelist.add(index);
					visited[index] = true;
					frontnodelist.remove(new Integer(index));
					lastinserted = index;
					
					for(int j=0;j<neighborlist[index].size();j++)
					{
						int ne = (Integer)neighborlist[index].get(j);
						if(nodelist.contains(ne))
							kin += (Double)weightmap[index].get(ne);
					}
					
					R += strength[index];	
					
				}
				
				
				//看看是否需要删除一个节点
				
				
				
				
				
			}
						
			
		}
			
		
	}
	
	
	//重叠社团计算q值
	public double calculate_Q(LinkedList<Community> finalcommunitylist)
	{
		double res = 0;
		
		//记录每个节点属于几个社团
		int communitycount [] = new int[nodenum];
		for(int i=0;i<nodenum;i++)
			communitycount[i] = 0;
		
		for(int i=0;i<finalcommunitylist.size();i++)
		{
			Community com = finalcommunitylist.get(i);
			
			for(int j=0;j<com.nodelist.size();j++)
			{
				int nodeid = com.nodelist.get(j);
				communitycount[nodeid]++;
			}
				
		}
		
		
		for(int index = 0;index <finalcommunitylist.size();index++)
		{
			Community com = finalcommunitylist.get(index);
			double temp = 0;
			
			for(int index1 = 0;index1 <com.nodelist.size();index1++)
			{
				int i = com.nodelist.get(index1);
				for(int index2= 0; index2<com.nodelist.size();index2++)
				{
					double newtemp = 0;
					
					int j = com.nodelist.get(index2);
					
					if(neighborlist[i].contains(j))
						newtemp += (Double)weightmap[i].get(j) - strength[i]*strength[j]/(2*totaledgeweight);
					else
						newtemp += 0 - strength[i]*strength[j]/(2*totaledgeweight);
					
					newtemp = newtemp/(communitycount[i]*communitycount[j]);
					
					temp += newtemp;
				}

						
			}
			
			
			res += temp;
		}
		
		res = res/(2*totaledgeweight);
		
		return res;
	}
	
	
	public void show()
	{
		double q = calculate_Q(communitylist);
		System.out.println("modularity value: "+q);
		
		System.out.println("final communitylist:");
	//	System.out.println(communitylist);
		
		
	}
	
	
	public static void main(String args[])
	{
		long start=System.currentTimeMillis();
		
		LocalGreedyExpanding lge = new LocalGreedyExpanding("data2/modified_new_cond-mat-2005.txt");
		
		lge.expand();
		
		lge.show();
		
		long end = System.currentTimeMillis();
		
		System.out.println((end-start)/1000.0+"s");
		
	}
	
	
}
