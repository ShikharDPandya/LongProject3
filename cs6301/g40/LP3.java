
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
//import cs6301.g40.Timer;

public class LP3
{
    static int VERBOSE = 0;



    /*
    public LP3(Graph g)
    {
        super(g);
        node = new DMSTVertex[g.size()];
        for(Graph.Vertex u: g)
        {
            node[u.getName()] = new DMSTVertex(u);
        }
    }
    */
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
        XGraph xg = new XGraph(g);
        DMSTGraph dg = new DMSTGraph(xg);

        ArrayList<DMSTGraph.DMSTVertex> list=new ArrayList<>();
        dg=create0Edges(startVertex.getName(),dg);
        dg.printEdges();
        list=find0Cycle(dg,list);
        System.out.println(list);
/*
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
        */
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
        ArrayList<DMSTGraph.DMSTVertex> verticesInCycle = new ArrayList<>();
        ArrayList<DMSTGraph.DMSTEdge> minEdgesToNewComp = new ArrayList<>();
        XGraph xg = new XGraph(g);
        DMSTGraph dg = new DMSTGraph(xg);
        DMSTGraph.DMSTEdge min = dg.DMSTVertices[0].DMSTAdj.get(0);
        int minWeight= Integer.MAX_VALUE;
        do {
            dg = create0Edges(start.getName(),dg);
            numComps=StronglyConnectedComps.findStronglyConnectedComponents(dg,numComps,dg.iterator(),dg.returnEdgeIterators(),dg.returnReverseEdgeIterators());
            if(numComps == 1)
                break;
            else
            {
                verticesInCycle = find0Cycle(dg,verticesInCycle);
                for (DMSTGraph.DMSTVertex v:dg.DMSTVertices)
                {
                    if(!verticesInCycle.contains(v))
                    {
                        for (DMSTGraph.DMSTEdge e : v.DMSTAdj)
                        {
                            if(verticesInCycle.contains(e.otherEnd(v)) && e.augmentedWeight < minWeight )
                            {
                                min = e;
                            }
                        }
                        ListIterator<DMSTGraph.DMSTEdge> it = v.revDMSTAdj.listIterator();
                        while(it.hasNext() && !verticesInCycle.contains(it.next().otherEnd(v)))
                            ;

                    }
                    else
                    {
                        for (DMSTGraph.DMSTEdge e:v.DMSTAdj)
                        {
                            if( !verticesInCycle.contains(e.otherEnd(v))&&e.augmentedWeight == 0)
                                min=e;
                        }
                    }
                    minEdgesToNewComp.add(min);
                    minWeight=Integer.MAX_VALUE;
                }
                DMSTGraph.DMSTVertex newComp = createDMSTComponent(verticesInCycle);
                dg.addDMSTVertex(newComp,minEdgesToNewComp);
            }
        }while (numComps != 1);
        return 0;
    }

    //Todo: Write a function to check if all vertices are accessible from the root

    public static DMSTGraph create0Edges(int startVertexIndex,DMSTGraph dg)
    {

        for (int i = 0; i < DMSTGraph.currentSizeOfGraph; i++)
        {
            if(i==startVertexIndex || dg.DMSTVertices[i].revDMSTAdj.isEmpty())
                continue;
            Iterator<DMSTGraph.DMSTEdge> it = dg.DMSTVertices[i].revDMSTAdj.iterator();
            while(it.hasNext())
            {
                it.next().augmentedWeight-=dg.DMSTVertices[i].minEdge;
            }
        }
        return dg;
    }

/*
    static void findCycle(DFS d, DFS.DFSVertex v1)
    {
        d.timeCounter++;
        v1.seen=true;
        d.DFS_Vertices.add(v1);
        v1.startTime=d.timeCounter;
        for (DFS.DFSVertex V:d.node)
        {
            if(V.seen)
                continue;
            for (DMSTGraph.DMSTEdge e : v1.originalVertex.DMSTAdj)
            {
                if (d.getVertex(e.otherEnd(d.xg.getVertex(v1.originalVertex.name + 1))).seen)
                {
                    break;
                }
                else if(e.augmentedWeight==0)
                {
                    v1.seen=true;
                    d.Cycle_Vertices.add(v1);
                    findCycle(d,d.getVertex(e.otherEnd(d.xg.getVertex(v1.originalVertex.name + 1))));
                }
                else
                    v1.seen=true;
            }
            d.timeCounter++;
            v1.finishTime = d.timeCounter;
        }
    }
    */


    public static ArrayList<DMSTGraph.DMSTVertex> find0Cycle(DMSTGraph dg, ArrayList<DMSTGraph.DMSTVertex> list)
    {
        DFS d = new DFS(dg,dg.iterator(),dg.returnEdgeIterators(),dg.returnReverseEdgeIterators());
        d.resetSeen(dg.iterator());
        //d.findCycle(dg.DMSTVertices[0]);
        d.findCycle(dg.iterator());
        d.resetIterators(dg.iterator(),dg.returnEdgeIterators(),dg.returnReverseEdgeIterators());
        /*for (DFS.DFSVertex v : d.Cycle_Vertices)
        {
            list.add(v.originalVertex);
        }*/
        return list;
    }
}
