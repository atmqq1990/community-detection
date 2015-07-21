package chb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;


public class BMLPA {
	
	int nodenum = 0;                              //节点数目
	
	ReadData rd;
	
	double p ;
	
	ArrayList belong[] ;              //该节点属于哪几个社区
	
	int degree[];
	
	LinkedList neighborlist[];
	
	HashMap oldmap[];
	HashMap newmap[];
	
	LinkedList<Community> communitylist = new LinkedList<Community>();
	
	public double strength[];
	
	public double totaledgeweight;
	
	public HashMap weightmap[];
	
	
	public BMLPA(String file,double p)
	{
		this.p = p;		
		rd = new ReadData(file);
		nodenum = rd.nodenum;
		
		degree = new int[nodenum];
		for(int i=0;i<nodenum;i++)
			degree[i] = 0;
		
		belong = new ArrayList [nodenum];
		for(int i=0;i<nodenum;i++)
			belong[i] = new ArrayList<Integer>();
		
		
		oldmap = new HashMap[nodenum];
		for(int i=0;i<nodenum;i++)
			oldmap[i] = new HashMap<Integer,Double>();
		
		newmap = new HashMap[nodenum];
		for(int i=0;i<nodenum;i++)
			newmap[i] = new HashMap<Integer,Double>();
		
		neighborlist = rd.neighborlist;
		
		strength = rd.strength;
		
		totaledgeweight = rd.totaledgeweight;
		
		weightmap = rd.weightmap;
		
		degree = rd.degree;
				
	}
	
	
	public void roughcores()
	{
		
		int vset[] = new int [nodenum];
		for(int i=0;i<nodenum;i++)
			vset[i] = i;
		
		boolean free[] = new boolean[nodenum];
		for(int i=0;i<nodenum;i++)
			free[i] = true;
		
		for(int i=0;i<nodenum;i++)
			for(int j=i+1;j<nodenum;j++)
				if(degree[vset[i]] < degree[vset[j]])
				{
					int temp = vset[i];
					vset[i] = vset[j];
					vset[j] = temp;
				}
		
		for(Integer i: vset)
		{
			HashMap<Integer,Double> core = new HashMap<Integer,Double>();
			if(degree[i] >=3  && free[i]==true)
			{
				int max = -1;
				int index = -1;
				for(int k=0;k<neighborlist[i].size();k++)
				{
					int j = (Integer)neighborlist[i].get(k);
					if(degree[j] > max )
					{
						max = degree[j];
						index = j;
					}
					
				}
				
				if(free[index] == true)
				{
					
					core.put(i, 1.0);
					core.put(index, 1.0);
					
			//		free[i] = false;
			//		free[index] = false;
					
					LinkedList<Integer> commneiber =new LinkedList<Integer>();
					
					for(int k=nodenum-1;k>=0;k--)
					{
						int node = vset[k];
						if(neighborlist[i].contains(node) && neighborlist[index].contains(node))
						{
							commneiber.add(node);
						}
						
						
					}
					
					
					while(commneiber.size()!=0)
					{
						int h = commneiber.getFirst();
						
						core.put(h, 1.0);
				//		free[h] = false;
						
						LinkedList<Integer> toremove = new LinkedList<Integer>();
						for(int m=0;m<commneiber.size();m++)
						{
							int cc = commneiber.get(m);
							if(!neighborlist[h].contains(cc))
							{
								toremove.add(cc);
							}
						}
						commneiber.removeAll(toremove);
						
						commneiber.remove(new Integer(h));
											
					}
					
					normalize(core);
			//		System.out.println("core : "+ core);
									
				}
				
			}
			
			if(core.size() >= 3)
			{
				oldmap[i] = core;
		//		System.out.println("core2 : "+ core);
		//		for(Integer ik : core.keySet())
			//		free[ik] = false;
				
			}
		
		}
		
				
	}
	
	
	public void action()
	{
		roughcores();

		HashMap<Integer,Integer> oldmin = new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> min = new HashMap<Integer,Integer>();
		boolean finished = false;
		
		
		while(!finished)
		{
			finished = true;
			
			for(int i=0;i<nodenum;i++)
			{
				propagate_bbc(i,oldmap,newmap);
			}
			
			if(id(oldmap).equals(id(newmap)))
			{
			//	 min = mc(min,count(newmap));
				min = mc(count(oldmap),count(newmap));
			}
			else
			{
				min = count(newmap);
			}
			
			if( !min.equals(oldmin))
			{
				for(int i=0;i<nodenum;i++)
				{
					oldmap[i].clear();
					oldmap[i].putAll(newmap[i]);
				}
				
				oldmin.clear();
				oldmin.putAll(min);
				
				finished = false;
			}
						
		}
		
		
		HashMap<Integer,HashSet<Integer>> coms = new HashMap<Integer,HashSet<Integer>>();
		HashMap<Integer,HashSet<Integer>> sub = new HashMap<Integer,HashSet<Integer>>();
		
		
		for(int x=0;x<nodenum;x++)
		{
			HashSet<Integer> ids = id(x,oldmap);
			
			for(Integer in : ids)
			{
				if(coms.containsKey(in) && sub.containsKey(in))
				{
					HashSet<Integer> set = new HashSet<Integer>(); 
					set.addAll(coms.get(in));
					set.add(x);
					coms.remove(in);
					coms.put(in,set);
					
					HashSet<Integer> intersect = intersection(sub.get(in),ids);
					sub.remove(in);
					sub.put(in, intersect);
				}
				
				else
				{
					
					HashSet<Integer> set = new HashSet<Integer>();
					set.add(x);
					coms.put(in, set);
					sub.put(in, ids);
					
				}
				
			}
	
		}
		
		
		int communitynumber = coms.size();
		
		/*
		int communityindex = 0;
		
		System.out.println("there are totally  "+ communitynumber + " communities.");
		for(Integer in : coms.keySet())
		{
			System.out.println("subcommunity " + communityindex+" :");
			for(Integer node : coms.get(in))
			{
				System.out.print((node+1) + " ");
				belong[node].add(communityindex);
			}
			
			System.out.println();
			communityindex++;
		}
		*/
		
		
		Community com [] = new Community [communitynumber];
		for(int i=0;i<communitynumber;i++)
			com[i] = new Community();
		
		int index =0;
		for(Integer in : coms.keySet())
		{
			for(Integer node : coms.get(in))
			{
			
				com[index].nodelist.add(node);
			}
			
			index++;
		}
		
		
		for(int i=0;i<communitynumber;i++)
		{
			
			boolean caninsert = true;
			for(int j=0;j<communitylist.size();j++)
				if(same(com[i],communitylist.get(j)))
					caninsert = false;
			
			if(caninsert)
				communitylist.add(com[i]);
			
		}
		
	}
	
	
	public boolean same(Community com1, Community com2)
	{
		if(com1.nodelist.size() != com2.nodelist.size())
			return false;
		
		for(int i=0;i<com1.nodelist.size();i++)
			if(!com2.nodelist.contains(com1.nodelist.get(i)))
				return false;
		
		return true;
	}
	
	
	public HashSet<Integer> intersection(HashSet<Integer> set1, HashSet<Integer> set2)
	{
		HashSet<Integer> set = new HashSet<Integer>();

		for(Integer in : set1)
		{
			if(set2.contains(in))
				set.add(in);
		}
		
		return set;
	}
	
	
	public void propagate_bbc(int x, HashMap<Integer,Double> source[], HashMap<Integer,Double> dest[])
	{
		if(dest[x].size()!=0)
			dest[x].clear();
		
		for(int j=0;j<neighborlist[x].size();j++)
		{
			int y = (Integer)neighborlist[x].get(j);
			
			for(Integer in : source[y].keySet())
			{
				double b = source[y].get(in);
				
				if(dest[x].containsKey(in))
				{
					dest[x].put(in, dest[x].get(in)+b);
				}
				else
					dest[x].put(in, b);
				
			}
						
		}
		
		
		int neighborsize = neighborlist[x].size();
		for(Integer in : dest[x].keySet())
		{
			dest[x].put(in, dest[x].get(in)/neighborsize);
		}
				
		double bmax = 0;
		for(Integer in : dest[x].keySet())
		{
			double b = dest[x].get(in);
			if( b > bmax)
				bmax = b;
		}
		
		HashSet<Integer> toremove = new HashSet<Integer>();
		for(Integer in : dest[x].keySet())
		{
			double b = dest[x].get(in);
			if(b/bmax < p)
			{
			//	dest[x].remove(in);
				toremove.add(in);
				
			}
			
		}
		
		for(Integer in : toremove)
			dest[x].remove(in);
				
		normalize(dest[x]);
				
	}
	
	
	
	public void normalize(HashMap<Integer,Double> map)
	{
		double sum = 0;
		for(Integer in: map.keySet())
		{
			sum += map.get(in);
		}
			
		for(Integer in : map.keySet())
		{
			map.put(in, map.get(in)/sum);
		}
		
	}
	
	
	public HashSet<Integer> id(HashMap<Integer,Double> map[])
	{
		HashSet<Integer> ids = new HashSet<Integer>();
		
		for(int i=0;i<nodenum;i++)
			ids.addAll(id(i,map));
		
		return ids;
	}
	
	
	public HashSet<Integer> id(int node, HashMap<Integer,Double> map[])
	{
		HashSet<Integer> ids = new HashSet<Integer>();
				
		ids.addAll(map[node].keySet());
			
		return ids;
		
	}
	
	
	public HashMap<Integer,Integer> count(HashMap<Integer,Double> map[])
	{
		HashMap<Integer,Integer> counts = new HashMap<Integer,Integer>();
		
		for(int i=0;i<nodenum;i++)
			for(Integer in:map[i].keySet())
			{
				if(counts.containsKey(in))
				{
					counts.put(in, counts.get(in)+1);
				}
				else
					counts.put(in, 1);
			}
				
		return counts;
		
	}
	
	
	public HashMap<Integer,Integer> mc(HashMap<Integer,Integer> cs1, HashMap<Integer,Integer> cs2)
	{
		HashMap<Integer,Integer> cs = new HashMap<Integer,Integer>();
		
		for(Integer in : cs1.keySet())
		{
			int min = cs1.get(in);
			if( min > cs2.get(in))
				min = cs2.get(in);
			
			cs.put(in, min);
			
		}
		
		return cs;
		
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
			/*
			for(int i=0;i<communitylist.size();i++)
			{
				System.out.print("submodularity "+i +"(nodenumber: "+communitylist.get(i).nodelist.size()+")"+": ");
				System.out.print(communitylist.get(i));
			}
			*/
			double q = calculate_Q(communitylist);
			System.out.printf("%.3f\t",q);
		}
	

	public static void main(String args[])
	{
		
		double pvalue[] = {0.3,0.4,0.5,0.55,0.6,0.65,0.7,0.75,0.9,0.85,0.9,0.95};
		
		
			for(int j=0;j<pvalue.length;j++)
			{
			
				BMLPA bmlpa = new BMLPA("graph.txt",pvalue[j]);
				
				bmlpa.action();
				
				bmlpa.show();
			}
			System.out.println("\n");
		
	}
	


}
