package cs6301.g40;

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
    ArrayList<ArrayList<DFS.DFSVertex>> comps;
    public StronglyConnectedComps()
    {
        SortedByFinishedTime = new ArrayList<>();
        comps=new ArrayList<>();
    }

    public StronglyConnectedComps(ArrayList<DFS.DFSVertex> DFSVertices, DMSTGraph dg)
    {
        SortedByFinishedTime = new ArrayList<>();
        comps=new ArrayList<>();
        Collections.sort(DFSVertices,new DFSVerticesComp());
        for(DFS.DFSVertex v1:DFSVertices)
        {
            Graph.Vertex v = dg.DMSTVertices[v1.name];
            SortedByFinishedTime.add(v);
        }
    }

    public int findStronglyConnectedComponents(DFS d, int numComps)
    {
        for(Graph.Vertex v1:SortedByFinishedTime)
        {
            DFS.DFSVertex df1 = d.node[v1.getName()-d.numDisabledVertices];
            if(df1 == null)
            {
                System.out.println("No node for the input Graph.Vertex"+ v1.getName());
                return -1;
            }
            if(!df1.seen)
            {
                numComps++;
                comps.add(d.DFSOnRevGraph(v1,new ArrayList<>(),numComps-1));
            }
        }
        return numComps;
    }

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
