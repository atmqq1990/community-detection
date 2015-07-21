package chb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;


public class InkClustering {

	private double evilParameter ;

	private int seedNodeID = 1;
	
	int nodenum;
	ReadData rd;
	public LinkedList<Integer> neighborlist[];
	public ArrayList<Edge> edges;
	
	public ArrayList<Edge> edgelist[];
	
	LinkedList<Community> finalcommunitylist;
	
	HashMap weightmap[];
    public double totaledgeweight;
	
	public double strength[];
	double avg_edgeweight ;
	
	public int edgenum ;
	
	public InkClustering(String file,double evilParameter)
	{
		this.evilParameter = evilParameter;	
		
		rd = new ReadData(file);
		nodenum = rd.nodenum;
								
		neighborlist = rd.neighborlist;
		
		edges = rd.edges;
		
		edgelist = rd.edgelist;	
		finalcommunitylist = new LinkedList<Community>();
		
        weightmap = rd.weightmap;
		
		edgenum = rd.edgenum;
		
		totaledgeweight = rd.totaledgeweight;
		
		avg_edgeweight = totaledgeweight / edgenum;
		
		strength = rd.strength;
	}
	
	private ArrayList<Edge> getEdges(int sourceNode)
	{
		ArrayList<Edge> res = new ArrayList<Edge>();
		
		return edgelist[sourceNode];		
	}
	
		
	private HashSet<Edge> searchForCoreEdgeSet() {
        HashSet<Edge> coreEdgeSet = new HashSet<Edge>();


        HashSet<Edge> noCoreEdgeSet = new HashSet<Edge>();

        for (Edge e : edges) {

            if (noCoreEdgeSet.contains(e)) {
                continue;
            }

            int sourceNode = e.getSource();
            int targetNode = e.getTarget();

            double eTriMeasure =  e.getTotal();

            boolean isCoreEdge = true;
            for (Edge ne : getEdges(sourceNode)) {
                if (coreEdgeSet.contains(ne)) {
                    isCoreEdge = false;
                    noCoreEdgeSet.add(e);
                    break;
                }

                double neTriMeasure =  ne.getTotal();
                if (neTriMeasure > eTriMeasure) {
                    isCoreEdge = false;
                    noCoreEdgeSet.add(e);
                    break;
                } else {
                    noCoreEdgeSet.add(ne);
                }
            }
            if (isCoreEdge) {
                for (Edge ne : getEdges(targetNode)) {

                    if (coreEdgeSet.contains(ne)) {
                        isCoreEdge = false;
                        noCoreEdgeSet.add(e);
                        break;
                    }

                    double neTriMeasure =  ne.getTotal();
                    if (neTriMeasure > eTriMeasure) {
                        isCoreEdge = false;
                        noCoreEdgeSet.add(e);
                        break;
                    } else {
                        noCoreEdgeSet.add(ne);
                    }
                }
            }
            if (isCoreEdge) {
                coreEdgeSet.add(e);
            }
        }
        /*
        System.out.println("In Search For Core Edge Set");
        for (Edge e : coreEdgeSet) {
            System.out.print(e.getId() + " ");
        }
        System.out.println();
        */
        return coreEdgeSet;
    } 
	 
	private Edge getEdge( int source, int target)
	{
		for(int i=0;i<edgelist[source].size();i++)
		{
			if(edgelist[source].get(i).getSource() == source && edgelist[source].get(i).getTarget() == target)
				return edgelist[source].get(i);
			
			if(edgelist[source].get(i).getSource() == target && edgelist[source].get(i).getTarget() == source)
				return edgelist[source].get(i);
		}
	
		return null;
	}
	
	
	 private HashSet<Integer> expand_by_edge_v2( Edge seed, HashMap<Integer, ArrayList<Integer>> assignedNodeSet,int clusterId) {
	        /* Init */
	//        System.out.println("Build Community :" + clusterId + " BY EDGE :" + seed.getId());
	     
	        
	        /* outer Edge Density */
	        double clusterMeasure;

	        double clusterEdgeTriMeasureTotal =  seed.getTotal();
	        double outerEdgeTriMeasureTotal = 0.0;

	       int source_seed = seed.getSource();
	       int target_seed = seed.getTarget();

	        if(assignedNodeSet.containsKey(source_seed)) {
	            assignedNodeSet.get(source_seed).add(clusterId);
	        } else {
	            ArrayList<Integer> newClusterList = new ArrayList<Integer>();
	            newClusterList.add(clusterId);
	            assignedNodeSet.put(source_seed, newClusterList);
	        }
	        if(assignedNodeSet.containsKey(target_seed)) {
	            assignedNodeSet.get(target_seed).add(clusterId);
	        } else {
	            ArrayList<Integer> newClusterList = new ArrayList<Integer>();
	            newClusterList.add(clusterId);
	            assignedNodeSet.put(target_seed, newClusterList);
	        }
	        /* C B D S */
	        HashSet<Integer> cSet = new HashSet<Integer>();
	        if (neighborlist[source_seed].size() == 1) {
	            cSet.add(source_seed);
	        }
	        if (neighborlist[target_seed].size() == 1) {
	            cSet.add(target_seed);
	        }

	        /* Border Node Set(Belong to Cluster) */
	        /* Node : OutDegree */
	        HashMap<Integer, Integer> bSet = new HashMap<Integer, Integer>();
	        if (!cSet.contains(source_seed)) {
	            bSet.put(source_seed, neighborlist[source_seed].size() - 1);
	        }
	        if (!cSet.contains(target_seed)) {
	            bSet.put(target_seed, neighborlist[target_seed].size() - 1);
	        }

	        /* Shell Node Set(not Belong to Cluster) */
	        /* Node : OutDegree(To Unkown) */
	        HashMap<Integer, Integer> sSet = new HashMap<Integer, Integer>();

	        for (Integer n : neighborlist[source_seed] ) {
	            if (!cSet.contains(n) && !bSet.containsKey(n)) {
	                if (sSet.containsKey(n)) {
	                    sSet.put(n, sSet.get(n) - 1);
	                } else {
	                    sSet.put(n, neighborlist[n].size() - 1);
	                }
	                outerEdgeTriMeasureTotal += getEdge(n, source_seed).getTotal();
	            }
	        }

	        for (Integer n : neighborlist[target_seed] ) {
	            if (!cSet.contains(n) && !bSet.containsKey(n)) {
	                if (sSet.containsKey(n)) {
	                    sSet.put(n, sSet.get(n) - 1);
	                } else {
	                    sSet.put(n, neighborlist[n].size() - 1);
	                }
	                outerEdgeTriMeasureTotal += getEdge(n, target_seed).getTotal();
	            }
	        }

	        clusterMeasure = clusterEdgeTriMeasureTotal * 2.0 / Math.pow(2.0 * clusterEdgeTriMeasureTotal + outerEdgeTriMeasureTotal, evilParameter);

	        /* Init End */
	        /* Main Loop */
	        boolean continueLoop = true;

	        while (continueLoop) {
	            HashMap<Integer, Double> rankSSet = new HashMap<Integer, Double>();

	            HashMap<Integer, Double> newClusterTriMeasureTotalSet = new HashMap<Integer, Double>();
	            HashMap<Integer, Double> newOuterTriMeasureTotalSet = new HashMap<Integer, Double>();

	            for (Integer n : sSet.keySet()) {
	                int nodeId = n;

	                double inEdgeTriMeasure = 0;
	                double outEdgeTriMeasure = 0;

	                boolean belongToCoreSet = true;
	                for (Integer nn : neighborlist[n]) {

	                    Edge e = getEdge(n, nn);
	                    double eTriMeasure = (Double) e.getTotal();

	                    if (cSet.contains(nn) || bSet.containsKey(nn)) {
	                        inEdgeTriMeasure += eTriMeasure;
	                    } else {
	                        outEdgeTriMeasure += eTriMeasure;
	                        belongToCoreSet = false;
	                    }
	                }

	                /* calculate new cluster measure */
	                //double newClusterDensity = (clusterEdgeTriMeasureTotal + inEdgeTriMeasure) / (bSet.size() + cSet.size() + 1.0);
	                double newClusterDensity = (clusterEdgeTriMeasureTotal + inEdgeTriMeasure);
	                double newBSetSize = belongToCoreSet ? bSet.size() : (bSet.size() + 1.0);
	                //double newOuterDensity = (outerEdgeTriMeasureTotal - inEdgeTriMeasure + outEdgeTriMeasure) / (bSet.size() + cSet.size() + 1.0);
	                double newOuterDensity = (outerEdgeTriMeasureTotal - inEdgeTriMeasure + outEdgeTriMeasure);
	                /* !!!!!!!!!!!!!!!!!!!! for all outer edges triMeasure sum ~= 0 !!!!!!!!!!!!!!!!!!!!!!!! */
	                if (newOuterDensity < 0.001) {
	                    newOuterDensity = 0.001;
	                }
	                /* !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! */

	                double newClusterMeasure = newClusterDensity * 2.0 / Math.pow(2.0 * newOuterDensity + newClusterDensity, evilParameter);

	                newClusterTriMeasureTotalSet.put(nodeId, clusterEdgeTriMeasureTotal + inEdgeTriMeasure);
	                newOuterTriMeasureTotalSet.put(nodeId, outerEdgeTriMeasureTotal - inEdgeTriMeasure + outEdgeTriMeasure);
	                rankSSet.put(nodeId, newClusterMeasure);
	            }

	            
	            /* get The Best Shell Node */
	            int bestNodeId = -1;
	            double bestMeasure = Double.MIN_NORMAL;
	            for (int nodeId : rankSSet.keySet()) {
	                if (rankSSet.get(nodeId) > bestMeasure) {
	                    bestMeasure = rankSSet.get(nodeId);
	                    bestNodeId = nodeId;
	                }
	            }

	            //System.out.println("(BestNode) : " + bestNodeId);
	            if (bestMeasure > clusterMeasure) {
	                /* put the new node into cluster and update the sets */
	                int bestNode = bestNodeId;
	                
	                if(assignedNodeSet.containsKey(bestNode)) {
	                    assignedNodeSet.get(bestNode).add(clusterId);
	                } else {
	                    ArrayList<Integer> newClusterList = new ArrayList<Integer>();
	                    newClusterList.add(clusterId);
	                    assignedNodeSet.put(bestNode, newClusterList);
	                }

	                double outEdge = sSet.get(bestNode);

	                clusterMeasure = bestMeasure;
	                clusterEdgeTriMeasureTotal = newClusterTriMeasureTotalSet.get(bestNodeId);
	                outerEdgeTriMeasureTotal = newOuterTriMeasureTotalSet.get(bestNodeId);

	                if (sSet.get(bestNode) == 0) {
	                    cSet.add(bestNode);
	                } else {
	                    bSet.put(bestNode, (int) outEdge);
	                }
	                sSet.remove(bestNode);

	                for (Integer n : neighborlist[bestNode] ) {
	                    if (bSet.containsKey(n)) {
	                        int nOuterEdgeCount = bSet.get(n);
	                        if (nOuterEdgeCount <= 1) {
	                            bSet.remove(n);
	                            cSet.add(n);
	                        } else {
	                            bSet.put(n, nOuterEdgeCount - 1);
	                        }
	                    } else if (sSet.containsKey(n)) {
	                        int nOuterEdgeCount = sSet.get(n);
	                        sSet.put(n, nOuterEdgeCount - 1);
	                    } else {
	                        sSet.put(n, neighborlist[n].size() - 1);
	                    }
	                }

	            } else {
	                /* finish this shit */
	                continueLoop = false;
	            }
	        }

	        HashSet<Integer> dSet = new HashSet<Integer>();
	        dSet.addAll(cSet);
	        dSet.addAll(bSet.keySet());

	        return dSet;
	  }
	
	
	 public void execute() {
	      
	

	        for (Edge e : edges) {

	            int source = e.getSource();
	            int target = e.getTarget();

	            HashSet<Integer> source_NSet = new HashSet<Integer>();
	            HashSet<Integer> target_NSet = new HashSet<Integer>();

	            for (Integer n : neighborlist[target] ) {
	                target_NSet.add(n);
	            }
	            for (Integer n : neighborlist[source] ) {
	                source_NSet.add(n);
	            }

	            HashSet<Integer> unions = new HashSet<Integer>();
	            for (Integer n : source_NSet) {
	                if (target_NSet.contains(n)) {
	                    unions.add(n);
	                }
	            }

	            int max_neighborCount = (source_NSet.size() > target_NSet.size()) ? source_NSet.size() : target_NSet.size();

	            /* if no Triangle contains This Edge, set triNum = 0.1 */
	            double triNum = (unions.isEmpty()) ? 0.1 : (double)unions.size();
	            double inkShitTestMeasure = triNum / (double) (max_neighborCount - 1.0);

	            e.setTotal( ((int) (inkShitTestMeasure * 10000.0)) / 10000.0);

	           
	        }

//	        System.out.println("---------------------------Lets This Shit Begin--------------------------------------");

	        HashSet<Edge> coreEdgeSet = searchForCoreEdgeSet();
	        HashMap<Edge, Double> rankCoreEdgeMap = new HashMap<Edge, Double>();

	        for (Edge e : coreEdgeSet) {
	            int weight = neighborlist[e.getSource()].size() + neighborlist[e.getTarget()].size();
	            //rankCoreEdgeMap.put(e, (Double)e.getAttributes().getValue("Total"));
	            rankCoreEdgeMap.put(e, (double)weight);
	            
	        }

	        ArrayList<Map.Entry<Edge, Double>> sortedList = new ArrayList<Map.Entry<Edge, Double>>();
	        sortedList.addAll(rankCoreEdgeMap.entrySet());
	        Collections.sort(sortedList, new Comparator<Map.Entry<Edge, Double>>() {

	            @Override
	            public int compare(Map.Entry<Edge, Double> o1, Map.Entry<Edge, Double> o2) {
	                return (int) (o2.getValue() - o1.getValue());
	            }
	        });

	//        System.out.println("Sort Core Edge Set");
	//        for (Map.Entry<Edge, Double> e : sortedList) {
	//            System.out.print("[" + e.getKey().getId() + "] :" + e.getValue());
	//        }
	//        System.out.println();
	        //Edge seedEdge = this.searchForCoreEdge(graphModel, seed, null, null);
	        
	        HashMap<Integer, ArrayList<Integer>> assignedNodeSet = new HashMap<Integer, ArrayList<Integer>>();

	        ArrayList<HashSet<Integer>> caonima = new ArrayList<HashSet<Integer>>();
	        int index = 0;
	        for (Map.Entry<Edge, Double> e : sortedList) {
	            Edge coreEdge = e.getKey();

	            int sourceNode_coreEdge = coreEdge.getSource();
	            int targetNode_coreEdge = coreEdge.getTarget();

	            if (assignedNodeSet.containsKey(sourceNode_coreEdge) || assignedNodeSet.containsKey(targetNode_coreEdge)) {
	                /* */
	            } else {
	                HashSet<Integer> cluster = expand_by_edge_v2( coreEdge, assignedNodeSet, index);
	                caonima.add(cluster);

	                ++ index;
	            }
	        }
	        
	        /* Handle Un Assigned Nodes */
	        HashMap<Integer,Integer> unAssignedNodeSet = new HashMap<Integer,Integer>();
	        for (int n=0;n<nodenum;n++) {
	            if (!assignedNodeSet.containsKey(n)) {
	                unAssignedNodeSet.put(n, neighborlist[n].size());
	            }
	        }

	        ArrayList<Map.Entry<Integer, Integer>> unAssignedNodeSortedList = new ArrayList<Map.Entry<Integer, Integer>>();
	        unAssignedNodeSortedList.addAll(unAssignedNodeSet.entrySet());
	        Collections.sort(unAssignedNodeSortedList, new Comparator<Map.Entry<Integer, Integer>>() {

	            @Override
	            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
	                return (int) (o2.getValue() - o1.getValue());
	            }
	        });
	        for (Map.Entry<Integer, Integer> entry : unAssignedNodeSortedList) {
	            /* sorted by node Degree */
	            int n = entry.getKey();
	            
	            if(!unAssignedNodeSet.containsKey(n)) {
	                continue;
	            }
	            
	            /* Stand Alone Complete */
	            if(neighborlist[n].size() == 0) {
	                HashSet<Integer> standAloneCluster = new HashSet<Integer>();
	                standAloneCluster.add(n);
	                caonima.add(standAloneCluster);
	                unAssignedNodeSet.remove(n);
	            }
	            
	            boolean hasUnAssignedNeighbor = false;
	            HashSet<Integer> newCluster = new HashSet<Integer>();
	            newCluster.add(n);
	            for(Integer nn : neighborlist[n]) {
	                if(unAssignedNodeSet.containsKey(nn)) {
	                    /* unAssigned Node n contain UnAssignedNode Neighbors */
	                    hasUnAssignedNeighbor = true;
	                    newCluster.add(nn);
	                }
	                
	                if(assignedNodeSet.containsKey(nn) && assignedNodeSet.get(nn).size() == 1) {
	                    caonima.get(assignedNodeSet.get(nn).get(0)).add(n);
	                }
	            }
	            
	            if(hasUnAssignedNeighbor) {
	                caonima.add(newCluster);
	                for(Integer nn : newCluster) {
	                    unAssignedNodeSet.remove(nn);
	                }
	            } else {
	                unAssignedNodeSet.remove(n);
	            }
	        }
	        
	    //    clusters = new InkCluster[caonima.size()];
	    //    index = 0;
	    //    for(HashSet<Integer> c : caonima) {
	    //        InkCluster testCluster = new InkCluster(c.toArray(new Node[0]), index);
	    //        clusters[index] = testCluster;
	    //        ++ index;
	    //    }

	        for(HashSet<Integer> c : caonima)
	        {
	        	Community com = new Community();
	        	
	        	for(Integer nodeid : c)
	        	{
	        		com.nodelist.add(nodeid);
	        	}
	        	
	        	finalcommunitylist.add(com);
	        }
	        
	        
//	        System.out.println("---------------------------Shit End--------------------------------------");
	     //   graph.readUnlock();

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
		/*
		for(int i=0;i<finalcommunitylist.size();i++)
		{
			System.out.print("community "+i+"(nodenumbers: "+(finalcommunitylist.get(i).nodelist.size()+")")+":   ");
			for(int j=0;j<finalcommunitylist.get(i).nodelist.size();j++)
				System.out.print((finalcommunitylist.get(i).nodelist.get(j))+" ");
			System.out.println();
			
		}
		*/
		double q = calculate_Q(finalcommunitylist);
		System.out.printf("%.3f\t",q);
	}
	
	
	
	
	
	public static void main(String args[])
	{
	//	"data2/modified_new_hep-th.txt" "data2/new_geom.txt","data2/modified_new_cond-mat-1999.txt",
		double thetas[] = {0.5,0.6,0.7,0.8,0.9,1.0};	
		String filename[] = {
				"data2/modified_youtube.txt",
				"data2/modified_patents.txt"
				};
		
		for(int j=0;j<filename.length;j++)
		{
			System.out.println("doing "+filename[j]);
			for(int i=0;i<thetas.length;i++)
			{
				InkClustering ic = new InkClustering(filename[j],thetas[i]);
				
				ic.execute();
				
				ic.show();
			//	System.out.println("\n");
			}
			System.out.println("\n");
		}
	}
	
		
}
