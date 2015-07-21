package chb;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.*;

public class CliquePercolationMethod {
	
	int nodenum = 0;                              //节点数目
	
	int communitynumber = 0;                     //社团数目	
	
	ReadData rd;
	
	int k = 3;
	
	boolean visited [];
	
//	int cliquenumber = 0;
	
//	List<Clique> cliqueslist = new ArrayList<Clique>();
	
	ArrayList belong[] ;              //该节点属于哪几个社区
	
	LinkedList<Integer> neighborlist[];
	
	LinkedList<Community> communitylist = new LinkedList<Community>();
	
	public CliquePercolationMethod(String file)
	{
		rd = new ReadData(file);
		nodenum = rd.nodenum;
		
		belong = new ArrayList [nodenum];
		for(int i=0;i<nodenum;i++)
			belong[i] = new ArrayList<Integer>();
		
		neighborlist = new LinkedList[nodenum];
		for(int i=0;i<nodenum;i++)
			neighborlist[i] = new LinkedList<Integer>();
		
		visited = new boolean[nodenum];
		for(int i=0;i<nodenum;i++)
			visited[i] = false;
		
		neighborlist = rd.neighborlist;
				
	}
	
	
	public void action()
	{
		int visitednumber = 0;
		
		int ind = 0;
		
		while(visitednumber < nodenum)
		{
			int i = 0;
			while(visited[i])
				i++;
			
			visited[i] = true;
			visitednumber++;
			
			LinkedList<Integer> comnodelist = new LinkedList<Integer>();
			comnodelist.add(i);
			
			boolean found = false;
			
			for(int j=0;j<neighborlist[i].size();j++)
			{
				int neighbor_j = neighborlist[i].get(j);
				
				for(int m=j+1;m<neighborlist[i].size();m++)
				{
					int neighbor_m = neighborlist[i].get(m);
					if(neighborlist[neighbor_j].contains(neighbor_m))
					{
						found = true;
						comnodelist.add(neighbor_j);
						comnodelist.add(neighbor_m);
						if(visited[neighbor_j] == false)
						{
							visitednumber++;
							visited[neighbor_j] = true;
						}
						
						if(visited[neighbor_m] == false)
						{
							visitednumber++;
							visited[neighbor_m] = true;
						}
						break;
					}
					
				}
				
				if(found == true)
					break;
				
			}
			
			
			if(found == false)
			{
				for(int j=0;j<comnodelist.size();j++)
				{
					int node_index = comnodelist.get(j);
					belong[node_index].add(ind);
				}
				
				ind++;
				
			}
			else
			{
				
				for(int j=0;j<nodenum;j++)
					if(!comnodelist.contains(j))
					{
						int adjacentnumber = 0;
						
						for(int m=0;m<comnodelist.size();m++)
						{
							int node_index = comnodelist.get(m);
							if(neighborlist[node_index].contains(j))
							{
								adjacentnumber++;
							}
							
							if(adjacentnumber >= k-1)
								break;
							
						}
						
						if(adjacentnumber >= k-1  )
						{
							comnodelist.add(j);
							if( visited[j] == false)
							{	
								visitednumber++;
								visited[j] = true;
							}
						}
						
						
					}
				
				
				
				for(int j=0;j<comnodelist.size();j++)
				{
					int node_index = comnodelist.get(j);
					belong[node_index].add(ind);
				}
				
				ind++;
								
			}
			
			
			Community community = new Community();
			community.nodelist.addAll(comnodelist);
			communitylist.add(community);
		
		}
			
		
	}
	
	
	public void show()
	{
		for(int i=0;i<communitylist.size();i++)
		{
			System.out.print("submodularity "+i +": ");
			System.out.print(communitylist.get(i));
		}
		
		
	}
	
	
	public static void main(String args[])
	{
		
		CliquePercolationMethod cpm = new CliquePercolationMethod("graph.txt");
		
		cpm.action();
		
		cpm.show();
		
				
	}

}
