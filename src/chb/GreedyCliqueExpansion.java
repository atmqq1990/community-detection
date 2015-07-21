package chb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;


public class GreedyCliqueExpansion {
	
	int nodenum = 0;                              //节点数目
			
	ReadData rd;
	
	Random rand;
	
	double afa;
	
	double distancethreshold = 0.25;
	
	LinkedList neighborlist[];
	
	LinkedList<Community> communitylist ;
	
	LinkedList<Community> seeds;
	
	HashMap weightmap[];
	
	double strength[];
	
	double totaledgeweight;
	
	boolean visited[];
	
	public GreedyCliqueExpansion(String file,double aa)
	{
		rd = new ReadData(file);
		nodenum = rd.nodenum;
		
		afa = aa;
		
		visited = new boolean[nodenum];
		for(int i=0;i<nodenum;i++)
			visited[i] = false;
		
		communitylist = new LinkedList<Community>();
		seeds = new LinkedList<Community>();
		
		rand = new Random(100);
		
		neighborlist = rd.neighborlist;
		
		weightmap = rd.weightmap;
		
		strength = rd.strength;
		
		totaledgeweight = rd.totaledgeweight;
						
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
	
	
	public LinkedList<Integer> intersection(LinkedList a , LinkedList b)
	{
		LinkedList<Integer> res = new LinkedList<Integer>();
		for(int i=0;i<a.size();i++)
			if(b.contains(a.get(i)))
				res.add((Integer)a.get(i));
		
		return res;
	}
	
	
	public LinkedList<Integer> union(LinkedList a, LinkedList b)
	{
		LinkedList<Integer> res = new LinkedList<Integer>();
		
		for(Object i: a)
			res.add((Integer)i);
		
		for(Object i : b)
		{
			if(!res.contains((Integer)i))
				res.add((Integer)i);
		}
		
		return res;
		
	}
	
	
	public void BronKerbosch2(LinkedList<Integer> R, LinkedList<Integer> P, LinkedList<Integer> X)
	{
		if(P.size() ==0  && X.size()==0)
		{	
		//	System.out.println(R);
			Community com = new Community();
			com.nodelist.addAll(R);
			if(R.size() >=4)
			{
				
				boolean insert = true;
				for(int i=0;i<seeds.size();i++)
					if(distance(com,seeds.get(i)) < 0.75)
					{
						insert = false;
						break;
					}
				if(insert)
					seeds.add(com);
			}
			return ;
		}
		else
		{	
			LinkedList<Integer> pux = union(P,X);
			int index = rand.nextInt(pux.size());
			int u = pux.get(index);
			
			LinkedList<Integer> temp = new LinkedList<Integer>();
			temp.addAll(P);
			temp.removeAll(neighborlist[u]);
			
			for(Integer v : temp)
			{
			    LinkedList<Integer> newR = new LinkedList<Integer>();
				newR.addAll(R);
				newR.add(v);
				LinkedList<Integer> newP = intersection(P,neighborlist[v]);
				LinkedList<Integer> newX = intersection(X,neighborlist[v]);
				
				BronKerbosch2(newR,newP,newX);
				
				P.remove(new Integer(v));
				X.add(v);
				
			}
		
		
		}
	}
	
	
	public void find_seeds()
	{
		LinkedList<Integer> R = new LinkedList<Integer>();
		
		LinkedList<Integer> P = new LinkedList<Integer>();
		for(int i=0;i<nodenum;i++)
			P.add(i);
		
		LinkedList<Integer> X = new LinkedList<Integer>();
		
		BronKerbosch2(R,P,X);
		
	//	System.out.println("seeds size(): " + seeds.size());
	//	for(int i=0;i<seeds.size();i++)
	//		System.out.println(seeds.get(i));
	}
	
	
	public Community expand(Community seed)
	{
		Community res = new Community();
		
		LinkedList<Integer> nodelist = seed.nodelist;
		
		double fitness[] = new double[nodenum];
		for(int i=0;i<nodenum;i++)
			fitness[i] = -10;
		
		for(int i=0;i<seed.nodelist.size();i++)
			visited[seed.nodelist.get(i)] = true;
		
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
			{
				res.nodelist.addAll(nodelist);
	//			System.out.println("res: "+res);
				return res;
			}
			else
			{
				nodelist.add(index);
				visited[index] = true;
				for(int i=0;i<cliqueneighborlist.size();i++)
				{
					int nei = cliqueneighborlist.get(i);
					fitness[nei] = -10;
					
				}
			}
		}
		
		
	}
	
	
	public double distance(Community com1, Community com2)
	{
		LinkedList<Integer> inter = intersection(com1.nodelist,com2.nodelist);
		
		int minsize = com1.nodelist.size();
		if(minsize > com2.nodelist.size())
			minsize = com2.nodelist.size();
		
		return 1.0 - inter.size()*1.0/minsize;
		
	}
	
	
	public void action()
	{
		find_seeds();
	//	System.out.println("seeds: "+seeds);
		int index=1;
		for(Community seed : seeds)
		{
	//		System.out.println("expanding seed :"+index);
			index++;
			boolean allvisited = true;
			for(int i=0;i<seed.nodelist.size();i++)
				if(visited[seed.nodelist.get(i)] == false)
				{	
					allvisited = false;
					break;
				}
			
			if(allvisited)
				continue;
			
			Community cc = expand(seed);
			
			communitylist.add(cc);
			
			
			if(communitylist.size() == 0)
				communitylist.add(cc);
			else
			{
				boolean caninsert = true;
				
				for(int i=0;i<communitylist.size();i++)
				{
					Community com = communitylist.get(i);
					if(distance(cc,com) <= distancethreshold)
						caninsert = false;
				}
				
				if(caninsert)
					communitylist.add(cc);
				
			}
			
			
		}
		
	//	System.out.println("final communitylist:");
	//	System.out.println(communitylist);
	}
	
	
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
		
	//	System.out.println("final communitylist:");
	//	System.out.println(communitylist);
		
		
	}
	
	public static void main(String args[])
	{
		double thetas[] = {1.0};	
		String filename[] = {
				"data2/modified_new_oclinksw.txt"};
		
		for(int j=0;j<filename.length;j++)
		{
			System.out.println("doing "+filename[j]);
			for(int i=0;i<thetas.length;i++)
			{
				GreedyCliqueExpansion gce = new GreedyCliqueExpansion(filename[j],thetas[i]);
				
				gce.action();
				
				gce.show();
			//	System.out.println("\n");
			}
			System.out.println("\n");
		}
	}
	
	

}
