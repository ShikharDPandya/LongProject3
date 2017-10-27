package cs6301.g40;
//import cs6301.g40.Graph;

import java.util.*;

class DFSVerticesComp implements Comparator<DFS.DFSVertex>
{
    public int compare(DFS.DFSVertex v1,DFS.DFSVertex v2)
    {
        if(v1.finishTime<v2.finishTime)
            return 1;
        else if(v1.finishTime>v2.finishTime)
            return -1;
        else
            return 0;
    }
}


public class StronglyConnectedComps
{
    ArrayList<Graph.Vertex> SortedByFinishedTime;

    public StronglyConnectedComps()
    {
        SortedByFinishedTime = new ArrayList<>();
    }

    public StronglyConnectedComps(ArrayList<DFS.DFSVertex> DFSVertices, DFS d)
    {
        SortedByFinishedTime = new ArrayList<>();
        Collections.sort(DFSVertices,new DFSVerticesComp());
        for(DFS.DFSVertex v1:DFSVertices)
        {
            Graph.Vertex v = d.g.v[v1.name];
            SortedByFinishedTime.add(v);
        }
    }

    public static DFS.DFSVertex findMinFinishTime(DFS v)
    {
        DFS.DFSVertex min = v.node[0];
        //min.finishTime=Integer.MAX_VALUE;
        for (DFS.DFSVertex v1:v.DFS_Vertices)
        {
            if(v1.finishTime<min.finishTime)
                min=v1;
        }
        return min;
    }




    public int findStronglyConnectedComponents(DFS d, int numComps)
    {
//        while(!d.DFS_Vertices.isEmpty())
//        {
//            SortedByFinishedTime.push(findMinFinishTime(d));
//            d.DFS_Vertices.remove(findMinFinishTime(d));
//        }

        /*for (DFS.DFSVertex v1:d.DFS_Vertices)
        {
            v1.seen=false;
        }*/
        d.resetSeen();

        for(Graph.Vertex v1:SortedByFinishedTime)
        {
            if(!d.node[v1.getName()].seen)
            {
                numComps++;
                d.findDFS(v1,true);
            }
        }
        return numComps;
    }



    public static int findStronglyConnectedComponents(Graph g, int numComps, Iterator vertexIterator, ArrayList<Iterator<Graph.Edge>> edgeIterators, ArrayList<Iterator<Graph.Edge>> revEdgeIterators)
    {
        //Graph xg = new Graph(g);
        DFS D1 = new DFS(g,vertexIterator,edgeIterators,revEdgeIterators);
        D1.findDFS(g.v[0],false);
        StronglyConnectedComps scc= new StronglyConnectedComps(D1.DFS_Vertices,D1);
        return scc.findStronglyConnectedComponents(D1,numComps);
    }

    /*public static int findStronglyConnectedComponents( DMSTGraph dg, int numComps)
    {
        DFS D1 = new DFS(dg);
        D1.findDFS(dg.DMSTVertices[0],false);
        return findStronglyConnectedComponents(D1,numComps);
    }*/


    public static void main(String [] args)
    {
        int index=0,numComps=0;
        Scanner in = new Scanner(System.in);
        Graph g = new Graph(5);
        System.out.println("Enter Graph:-");
        g = g.readDirectedGraph(in);
        DMSTGraph dg= new DMSTGraph(g);
        //XGraph xg = new XGraph(g);
        //DFS D1 = new DFS(g,);
        //D1.findDFS(g.v[0],false);
        //numComps=findStronglyConnectedComponents(D1,numComps,);
        System.out.println("Number of Componenets:" + numComps);
        System.out.println("--------------DFS Path--------------");
        //for (DFS.DFSVertex v:D1.DFS_Vertices)
        {
           // System.out.println(v.originalVertex.name);
        }
    }

}
