package chb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class LMF {
	
	int nodenum = 0;                              //节点数目
	
	int communitynumber = 0;                     //社团数目	
	
	int communityindex = 0;
	
	ReadData rd;
	
	Random rand = new Random();
	
	double afa ;
	
	boolean visited [];
	
	double fitness[];
	
	ArrayList belong[] ;              //该节点属于哪几个社区
	
	LinkedList neighborlist[];
	HashMap weightmap[];
	
	int edgenum ;
	double totaledgeweight;
//	double avg_edgeweight ;
	double strength[];
	
	LinkedList<Community> communitylist = new LinkedList<Community>();
	
	
	public LMF(String file, double aa)
	{
		rd = new ReadData(file);
		nodenum = rd.nodenum;
		
		afa = aa;
		
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
		
		belong = new ArrayList [nodenum];
		for(int i=0;i<nodenum;i++)
			belong[i] = new ArrayList<Integer>();
					
		
	}
	
	
	public double calculate_subgraph_fitness(LinkedList<Integer> ls)
	{
		double fit = 0;
		
		double kin = 0;
		double total = 0;
		
		for(int i=0;i<ls.size();i++)
		{
			int nodeindex1 = ls.get(i);
			
			for(int j=i+1;j<ls.size();j++)
			{
				int nodeindex2 = ls.get(j);
				
				if(neighborlist[nodeindex1].contains(nodeindex2))
				{
					kin += (Double)weightmap[nodeindex1].get(nodeindex2);
				}
				
			}
			
			
			total += strength[nodeindex1];			
		}
		
		fit = kin /(Math.pow(total, afa));
		
		return fit;
		
	}
	
	
	
	public double calculate_node_fitness(LinkedList<Integer> ls , int node)
	{
		LinkedList<Integer> ls1 = new LinkedList<Integer>();
		ls1.addAll(ls);
		if(!ls.contains(node))
			ls1.add(node);
		
		LinkedList<Integer> ls2 = new LinkedList<Integer>();
		ls2.addAll(ls);
		if(ls2.contains(node))
			ls2.remove(new Integer(node));
		
		double fit1 = calculate_subgraph_fitness(ls1);
		double fit2 = calculate_subgraph_fitness(ls2);
		
		return fit1 - fit2;
		
	}
	
	private void cleanfitness()
	{
		for(int i=0;i<nodenum;i++)
			fitness[i] = 0;
	}
	
	public void detect_natural_community(int nodea)
	{
		cleanfitness();
		
		LinkedList<Integer> nodelist = new LinkedList<Integer>();
		nodelist.add(nodea);
		
		visited[nodea] = true;	
		
		while(true)
		{
			LinkedList<Integer> cliqueneighborlist = new LinkedList<Integer>();
			for(int i=0;i<nodelist.size();i++)
			{
				int nodeindex = nodelist.get(i);
				for(int j=0;j<neighborlist[nodeindex].size();j++)
				{
					int neighbor = (Integer)neighborlist[nodeindex].get(j);
					
					if(!nodelist.contains(neighbor) && !cliqueneighborlist.contains(neighbor))
						cliqueneighborlist.add(neighbor);
					
				}
				
			}
			
			
			
			for(int i=0;i<cliqueneighborlist.size();i++)
			{
				int neighbor = (Integer)cliqueneighborlist.get(i);
			
				fitness[neighbor] = calculate_node_fitness(nodelist,neighbor);
																
			}
			
			double maxfit = -1;
			int index = -1;
			
			for(int i=0;i<cliqueneighborlist.size();i++)
			{
				int neighbor = (Integer)cliqueneighborlist.get(i);
				if(fitness[neighbor] > maxfit)
				{
					maxfit = fitness[neighbor];
					index = neighbor;
				}
				
			}
			
			if(maxfit <= 0)
				break;
			
			visited[index] = true;
			belong[index].add(communityindex);
			nodelist.add(index);
			
			boolean hasnegetivefitness = true;
			
			while(hasnegetivefitness)
			{
				
				hasnegetivefitness = false;
							
				for(int i=0;i<nodelist.size();i++)
				{
					int nodeindex = nodelist.get(i);
					
					fitness[nodeindex] = calculate_node_fitness(nodelist,nodeindex); 				
				}
				
				for(int i=0;i<nodelist.size();i++)
				{
					int nodeindex = nodelist.get(i);
					
					if(fitness[nodeindex] < 0)
					{
						hasnegetivefitness = true;
						nodelist.remove(new Integer(nodeindex));
						belong[nodeindex].remove(new Integer(communityindex));
						if(belong[nodeindex].size()==0)
							visited[nodeindex] = false;
					}
					
				}
							
			}
									
		}
		
		Community community = new Community();
		community.nodelist.addAll(nodelist);
		communitylist.add(community);
		
		communityindex++;
		
	}
	
	
	
	
	public void action()
	{
								
		for(int i=0;i<nodenum;i++)
		{
			if(visited[i])
				continue;
					
			detect_natural_community(i);
		
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
		System.out.printf("%.3f\t",q);
		
		/*
		for(int i=0;i<communitylist.size();i++)
		{
			System.out.print("submodularity "+i +": ");
			System.out.print(communitylist.get(i));
		}
		*/
		
	}
	
	
	public static void main(String args[])
	{
	//	"data2/new_celegansneural.txt", "data2/new_geom.txt","data2/modified_new_hep-th.txt",
		
		double thetas[] = {0.8};	
		String filename[] = {
				
			//	"data2/modified_kingjames.txt",
				
				"data2/new_celegansneural.txt"
		};
		
		for(int j=0;j<filename.length;j++)
		{
			System.out.println("doing "+filename[j]);
			for(int i=0;i<thetas.length;i++)
			{
				LMF lmf = new LMF(filename[j],thetas[i]);
				
				lmf.action();
				
				lmf.show();
			//	System.out.println("\n");
			}
			System.out.println("\n");
		}
		
	}
	

}
