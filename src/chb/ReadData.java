package chb;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class ReadData {
	
	public int nodenum;	
	
	public int edgenum = 0;
	public double totaledgeweight=0;
	public LinkedList<Integer> neighborlist[];
	public HashMap weightmap[];
	
	public double strength[];
	public int degree[];
	
	public ArrayList<Edge> edges = new ArrayList<Edge>();
	
	public ArrayList<Edge> edgelist[];
	
	public ReadData(String filename)
	{
		
		 try
			{
				FileReader fr = new FileReader(filename);
				BufferedReader reader = new BufferedReader(fr);
				String line;
				line = reader.readLine();
				String atts[] = line.split("\\s+");
				nodenum = Integer.parseInt(atts[0]);
					
			//	System.out.println("nodenum: "+nodenum);
				
			//	System.out.println("reading: "+filename);
				
				neighborlist = new LinkedList [nodenum];
				for(int i=0;i<nodenum;i++)
					neighborlist[i] = new LinkedList<Integer>();
				
				weightmap = new HashMap [nodenum];
				for(int i=0;i<nodenum;i++)
					weightmap[i] = new HashMap<Integer,Double>();
				
				strength = new double[nodenum];
				for(int i=0;i<nodenum;i++)
					strength[i] = 0;
				
				degree = new int[nodenum];
				for(int i=0;i<nodenum;i++)
					degree[i] = 0;
				
				edgelist = new ArrayList[nodenum];
				for(int i=0;i<nodenum;i++)
					edgelist[i]=new ArrayList<Edge>();
				
				int linenumber = 0;
				
				String atts2[];
				int nodeid1;
				int nodeid2;
				double edgeweight;
				while((line = reader.readLine())!=null)
				{
					linenumber++;
			//		System.out.println("reading line: "+linenumber);
					atts2 = line.split("\\s+");
					
					nodeid1 = Integer.parseInt(atts2[0]);
					nodeid2 = Integer.parseInt(atts2[1]);
					
					edgeweight = Double.parseDouble(atts2[2]);
										
					weightmap[nodeid1].put(nodeid2, edgeweight);
					weightmap[nodeid2].put(nodeid1, edgeweight);
					
					
					//	if(Math.abs(edgeweight)>0.0001)
						{
						
							if(!neighborlist[nodeid1].contains(nodeid2))
							{
								edgenum++;
								totaledgeweight += edgeweight;
								strength[nodeid1] += edgeweight;
								strength[nodeid2] += edgeweight;
								degree[nodeid1]++;
								degree[nodeid2]++;
								neighborlist[nodeid1].add(nodeid2);
								neighborlist[nodeid2].add(nodeid1);
								
								Edge newed = new Edge(nodeid1,nodeid2);
								edges.add(newed);
								edgelist[nodeid1].add(newed);
								edgelist[nodeid2].add(newed);
							}
						}
					
				}
				
				
	//			System.out.println("nodenum: "+nodenum);
	//			System.out.println("edgenum: "+edgenum);
	//			System.out.println();
				
				reader.close();
				fr.close();
			}
		 catch(IOException e)
			{
				System.out.println("Can not find the file "+filename);
			}
		
	}
	
	
	public static void main(String args[])
	{
		String filename[] = {"modified_new_astro-ph.txt","modified_new_cond-mat.txt","modified_new_netsience.txt",
				"modified_new_train.txt","new_auth.txt","new_dolphins.txt","new_football.txt","new_geom.txt",
				"new_jazz.txt","new_karate.txt","new_lesmis.txt","modified_kingjames.txt","modified_new_cond-mat-2003.txt",
				"modified_new_cond-mat-2005.txt","modified_new_hep-th.txt","new_celegansneural.txt","new_windsurfers.txt"};
	
		for(int i=0;i<filename.length;i++)
		{
			ReadData rd = new ReadData(filename[i]);
		}
		
	}

}
