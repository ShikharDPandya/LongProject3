
// Starter code for LP3
// Do not rename this file or move it away from cs6301/g??

// change following line to your group number
package cs6301.g40;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;

import cs6301.g40.Graph.Vertex;
import cs6301.g40.Graph.Edge;

import static cs6301.g40.DMSTGraph.createDMSTComponent;
import static cs6301.g40.DMSTGraph.currentSizeOfGraph;

public class LP3
{
    static int VERBOSE = 0;

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in;
        if (args.length > 0) {
            File inputFile = new File(args[0]);
            in = new Scanner(inputFile);
        } else {
            in = new Scanner(System.in);
        }
	if(args.length > 1) {
            VERBOSE = Integer.parseInt(args[1]);
        }


	    int start = in.nextInt();  // root node of the MST
        Graph g = Graph.readDirectedGraph(in);
	    Vertex startVertex = g.getVertex(start);
	    List<Edge> dmst = new ArrayList<>();

        Timer timer = new Timer();
	int wmst = directedMST(g, startVertex, dmst);
        timer.end();

	System.out.println(wmst);
        if(VERBOSE > 0) {
	    System.out.println("_________________________");
            for(Edge e: dmst) {
                System.out.print(e);
            }
	    System.out.println();
	    System.out.println("_________________________");
        }
        System.out.println(timer);

    }

    /** TO DO: List dmst is an empty list. When your algorithm finishes,
     *  it should have the edges of the directed MST of g rooted at the
     *  start vertex.  Edges must be ordered based on the vertex into
     *  which it goes, e.g., {(7,1),(7,2),null,(2,4),(3,5),(5,6),(3,7)}.
     *  In this example, 3 is the start vertex and has no incoming edges.
     *  So, the list has a null corresponding to Vertex 3.
     *  The function should return the total weight of the MST it found.
     */  
    public static int directedMST(Graph g, Vertex start, List<Edge> dmst)
    {
        int numComps=0;
        ArrayList<DMSTGraph.DMSTEdge> minEdgesToNewComp = new ArrayList<>();
        XGraph xg = new XGraph(g);
        DMSTGraph dg = new DMSTGraph(xg);
        do {
            dg = create0Edges(start.getName(),dg);
            DFS D1 = new DFS(dg,dg.iterator(),dg.returnEdgeIterators(),dg.returnReverseEdgeIterators());
            D1.resetVertexIterator(dg.iterator());
            if(D1.findDFS(dg.iterator(),false) == 1)
                break;
            StronglyConnectedComps scc= new StronglyConnectedComps(D1.DFS_Vertices,dg);
            D1.resetProps();
            numComps = 0;
            D1.resetIterators(dg.iterator(),dg.returnEdgeIterators(),dg.returnReverseEdgeIterators());
            numComps= scc.findStronglyConnectedComponents(D1,numComps);
            if(numComps == 1)
                break;
            else
            {
                DMSTGraph.sizeOfContGraphs.add(numComps);
                //System.out.println("Sizes of graphs" + DMSTGraph.sizeOfContGraphs);
                DMSTGraph.DMSTEdge[][] minEdgesComp = new DMSTGraph.DMSTEdge[numComps][numComps];
                ArrayList<DMSTGraph.DMSTVertex> newComponentVertices = new ArrayList<>();
                for (int i = 0; i < scc.comps.size(); i++)
                {
                    for (DFS.DFSVertex v:scc.comps.get(i))
                    {
                        for (DMSTGraph.DMSTEdge e:dg.DMSTVertices[v.name].DMSTAdj)
                        {
                            int oppVertexcompNumber = D1.node[e.otherEnd(dg.DMSTVertices[v.name]).getName()-D1.numDisabledVertices].componentNumber;
                            if(scc.comps.get(i).contains(D1.node[e.otherEnd(dg.DMSTVertices[v.name]).getName()-D1.numDisabledVertices]))
                                continue;
                            else
                            {
                                if(minEdgesComp[i][oppVertexcompNumber]!=null && minEdgesComp[i][oppVertexcompNumber].augmentedWeight!=0)
                                {
                                    if(minEdgesComp[i][oppVertexcompNumber].augmentedWeight > e.augmentedWeight)
                                    {
                                        minEdgesComp[i][oppVertexcompNumber] = e;
                                        if(e.augmentedWeight == 0)
                                        {
                                            break;
                                        }
                                    }
                                }
                                else if(minEdgesComp[i][oppVertexcompNumber] == null)
                                {
                                    minEdgesComp[i][oppVertexcompNumber]=e;
                                }
                            }
                        }
                    }
                    ArrayList<DMSTGraph.DMSTVertex> dmstComps=new ArrayList<>();
                    for (DFS.DFSVertex v: scc.comps.get(i))
                    {
                        dmstComps.add(dg.DMSTVertices[v.name]);
                    }
                    newComponentVertices.add(createDMSTComponent(dmstComps,currentSizeOfGraph+i));
                }

                for (int i = 0; i < numComps; i++)
                {
                    for(int j = 0 ; j < numComps; j++)
                    {
                        if(i==j)
                            continue;
                        if(minEdgesComp[i][j]!=null)
                            minEdgesToNewComp.add(minEdgesComp[i][j]);
                    }

                    dg.addDMSTVertex(newComponentVertices.get(i),minEdgesToNewComp,newComponentVertices);
                    minEdgesToNewComp.clear();
                }
            }
        }while (numComps != 1);
        //dg.printEdges();

        return 0;
    }

    //Todo: Write a function to check if all vertices are accessible from the root

    public static DMSTGraph create0Edges(int startVertexIndex,DMSTGraph dg)
    {

        for (int i = 0; i < DMSTGraph.currentSizeOfGraph; i++)
        {
            if(i==startVertexIndex || dg.DMSTVertices[i].revDMSTAdj.isEmpty()||dg.DMSTVertices[i].disabled)
                continue;
            Iterator<DMSTGraph.DMSTEdge> it = dg.DMSTVertices[i].revDMSTAdj.iterator();
            while(it.hasNext())
            {
                DMSTGraph.DMSTEdge e1 = it.next();
                if(e1.augmentedWeight!=0)
                    e1.augmentedWeight-=dg.DMSTVertices[i].minEdge;
            }
        }
        return dg;
    }

}
