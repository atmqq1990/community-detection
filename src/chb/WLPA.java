package chb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

public class WLPA {
	
	public int nodenum;
	
	public LinkedList neighborlist[];
	
	ReadData rd;
	
	HashMap weightmap[];
	
	HashMap oldmap[];
	HashMap newmap[];
	
	public int edgenum ;
	public double totaledgeweight;
	
	public double strength[];
		
//	double theta;
	
	double avg_edgeweight ;
	
	public WLPA(String file)
	{
//		this.theta = theta;
		
		rd = new ReadData(file);
		nodenum = rd.nodenum;
		
		oldmap = new HashMap [nodenum];
		for(int i=0;i<nodenum;i++)
			oldmap[i] = new HashMap<Integer,Double>();
		
		newmap = new HashMap [nodenum];
		for(int i=0;i<nodenum;i++)
			newmap[i] = new HashMap<Integer,Double>();
					
		neighborlist = rd.neighborlist;
		
		weightmap = rd.weightmap;
		
		edgenum = rd.edgenum;
		
		totaledgeweight = rd.totaledgeweight;
		
		avg_edgeweight = totaledgeweight / edgenum;
		
		strength = rd.strength;
					
	}
	
	
	public void normalize(HashMap<Integer,Double> map)
	{
		double sum = 0;
		
		for(Integer in : map.keySet())
		{
			sum += map.get(in);
		}
		
		for(Integer in : map.keySet())
		{
			map.put(in, map.get(in)/sum);
		}
	}
		
		
	public void initlabelweight()
	{
		for(int i=0;i<nodenum;i++)
		{
//			oldmap[i].put(i, 1.0);
		}
	}
	
	
	public boolean  updatenodelabel(int nodeid)
	{			
		
		for(int i=0;i<neighborlist[nodeid].size();i++)
		{
			int neighbor_index = (Integer)neighborlist[nodeid].get(i);
			
			for(Object o: oldmap[neighbor_index].keySet())
			{
				Integer in = (Integer)o;
			//	double neighbor_label_weight = (Double)oldmap[neighbor_index].get(in);
				
				if(newmap[nodeid].containsKey(in))
				{
					double value1 = (Double)newmap[nodeid].get(in);
					double value2 = (Double)weightmap[nodeid].get(neighbor_index);
			//		newmap[nodeid].put(in, value1+ neighbor_label_weight * Math.log(value2+1.0)/Math.log(avg_edgeweight+1.0));
					newmap[nodeid].put(in, value1 + value2);
				}
				else
				{			
					double value2 = (Double)weightmap[nodeid].get(neighbor_index);
			//		newmap[nodeid].put(in,  neighbor_label_weight * Math.log(value2+1.0)/Math.log(avg_edgeweight+1.0));
					newmap[nodeid].put(in,  value2);
				}
				
			}
			
		}
		
		//比较之前先单位化
	//	normalize(newmap[nodeid]);
								
		LinkedList<Integer> toremove = new LinkedList<Integer>();
						
		double maxweight = 0;
	//	int maxin = -1;
		for(Object o : newmap[nodeid].keySet())
		{
			if((Double)newmap[nodeid].get(o) > maxweight)
			{
				maxweight = (Double)newmap[nodeid].get(o);
	//			maxin = (Integer)o;
			}							
		}
		
		for(Object o : newmap[nodeid].keySet())
		{
			
	//		Integer in = (Integer)o;
			double value1 = (Double)newmap[nodeid].get(o);
					
			if(Math.abs(value1 - maxweight) > 0.1)
			{
	
				toremove.add((Integer)o);			
			}						
		}
		
		
		for(int i=0;i<toremove.size();i++)
		{
			Integer in = toremove.get(i);
			newmap[nodeid].remove(in);
		}
				
		normalize(newmap[nodeid]);
		
		boolean res = equ(newmap[nodeid].keySet(),oldmap[nodeid].keySet());
		
		oldmap[nodeid].clear();
		oldmap[nodeid].putAll(newmap[nodeid]);
		
		newmap[nodeid].clear();
		
		return res;
				
	}
	
	
	public boolean equ(Set set1, Set set2)
	{
		if(set1.size() != set2.size())
			return false;
		for(Object o: set1)
		{
			if(!set2.contains(o))
				return false;
		}
		
		return true;
	}
	
	
	public void action()
	{
		initlabelweight();
		
		boolean finished = false;
		
		int iteration = 1;
		while(!finished  && iteration < 50)
		{
		//	System.out.println("iteration: "+iteration);
			iteration++;
			
			finished = true;
			
			
		//	System.out.println("iterating: ");
			for(int i=0;i<nodenum;i++)
			{
				int node_id = i;
				
				if(!updatenodelabel(node_id))
				{
					
					finished = false;
					
				}
				
			}
									
		}
		
	}
	
	
	public void show()
	{
		HashMap<Integer,Integer>  remainlabels = new HashMap<Integer,Integer>();
		
		int index = 0;
		for(int i=0;i<nodenum;i++)
		{
			for(Object o: oldmap[i].keySet())
			{
				Integer in = (Integer)o;
				if(!remainlabels.containsKey(in))
				{
					remainlabels.put(in, index);
					index++;
				}
			}
		}
		
		int communitynumber = index;
		Community com [] = new Community[communitynumber];
		
		
		for(int i=0;i<communitynumber;i++)
			com[i] = new Community();
		
		for(int i=0;i<nodenum;i++)
		{
			for(Object o : oldmap[i].keySet())
			{
				Integer in = (Integer)o;
				int list_index = remainlabels.get(in);
				com[list_index].nodelist.add(i);
			}
		}
		
		LinkedList<Community> finalcommunitylist = new LinkedList<Community>();
		for(int i=0;i<communitynumber;i++)
		{
			
			boolean caninsert = true;
			for(int j=0;j<finalcommunitylist.size();j++)
				if(same(com[i],finalcommunitylist.get(j)))
					caninsert = false;
			
			if(caninsert)
				finalcommunitylist.add(com[i]);
			
		}
		
		
		for(int i=0;i<finalcommunitylist.size();i++)
		{
			System.out.print("community "+i+"(nodenumbers: "+(finalcommunitylist.get(i).nodelist.size()+")")+":   ");
			for(int j=0;j<finalcommunitylist.get(i).nodelist.size();j++)
				System.out.print((finalcommunitylist.get(i).nodelist.get(j))+" ");
			System.out.println();
			
		}
		
		System.out.println("totaledgeweight : "+ totaledgeweight);
		
		double q = calculate_Q(finalcommunitylist);
		System.out.printf("%.3f\t",q);
		
	//	for(int i=0;i<nodenum;i++)
	//		System.out.print(strength[i]+" ");
		
	
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
	
	
	
	public static void main(String args[])
	{
		double thetas[] = {0.01};	
		
		for(int i=0;i<thetas.length;i++)
		{
			WLPA owlpa = new WLPA("data2/new_auth.txt");
			
			owlpa.action();
			
			owlpa.show();
		}
			
	}
	

}
