package cs6301.g40;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

public class DFS extends GraphAlgorithm<DFS.DFSVertex>
{
    Iterator<Graph.Vertex> vertexInGraph;
    ArrayList<DFSVertex> DFS_Vertices;
    ArrayList<DFSVertex> Cycle_Vertices;
    int timeCounter;
    int numDisabledVertices;
    public DFS(Graph g, Iterator<Graph.Vertex> vertexIterator, ArrayList<Iterator<Graph.Edge>> edgeIterators,ArrayList<Iterator<Graph.Edge>> revEdgeIterators)
    {
        super(g);
        vertexInGraph=vertexIterator;
        int counter = 0;
        DFS_Vertices = new ArrayList<>();
        Cycle_Vertices=new ArrayList<>();
        node = new DFSVertex[edgeIterators.size()]; // initialised to the number of active components
        timeCounter=0;
        numDisabledVertices = DMSTGraph.currentSizeOfGraph-edgeIterators.size();
        initializeNodes(vertexIterator,edgeIterators,revEdgeIterators);
    }

    void initializeNodes(Iterator<Graph.Vertex> Vit,ArrayList<Iterator<Graph.Edge>> edgeIterators,ArrayList<Iterator<Graph.Edge>> revEdgeIterators)
    {
        Graph.Vertex v;
        int counter = 0;
        while (Vit.hasNext())
        {
            v = Vit.next();
            Graph.Edge e;
            Iterator<Graph.Edge> Eit = edgeIterators.get(counter);
            Iterator<Graph.Edge> revEit = revEdgeIterators.get(counter);
            node[counter++] = new DFSVertex(v.getName(),Eit,revEit);
        }
    }

    public static class DFSVertex
    {
        Iterator<Graph.Edge> edgeInAdj;
        Iterator<Graph.Edge> edgeInRevAdj;
        int name;
        boolean seen;
        int startTime;
        int finishTime;
        int componentNumber;

        /**
         * Constructor for Graph
         *
         * @param  : int - number of vertices

         */

        public DFSVertex(int n,Iterator adj,Iterator revAdj)
        {
            name=n;
            edgeInAdj = adj;
            edgeInRevAdj = revAdj;
            seen = false;
            startTime = 0;
            finishTime = 0;
            componentNumber=0;
        }


        public void setIterators(Iterator<Graph.Edge> edgeIterator,Iterator<Graph.Edge> revEdgeIterator)
        {
            edgeInAdj = edgeIterator;
            edgeInRevAdj = revEdgeIterator;
        }
    }


    public void resetIterators(Iterator<Graph.Vertex> Vit,ArrayList<Iterator<Graph.Edge>> edgeIterators,ArrayList<Iterator<Graph.Edge>> reverseEdgeIterators)
    {
        vertexInGraph = Vit;
        for(int i = 0; i < edgeIterators.size();i++)
        {
            //if(node[i]!=null)
                node[i].setIterators(edgeIterators.get(i),reverseEdgeIterators.get(i));
        }
    }

    public void resetVertexIterator(Iterator<Graph.Vertex> Vit)
    {
        vertexInGraph = Vit;
    }


    void resetSeen()
    {

        for(int i = 0; i < node.length; i++)
        {
            node[i].seen=false;
        }
    }

    public void resetProps()
    {
        this.timeCounter=0;
        this.DFS_Vertices.clear();

        for(int i = 0; i < node.length; i++)
        {
            node[i].seen=false;
            node[i].startTime=0;
            node[i].finishTime = 0;
            node[i].componentNumber=0;
        }
    }

    public void findCycle(Iterator<Graph.Vertex> Vit)
    {
        Graph.Vertex vCurr;
        while(Vit.hasNext())
        {
            boolean foundCycle=false;
            vCurr = Vit.next();

            if (!node[vCurr.getName()-numDisabledVertices].seen)
            {
                foundCycle=findCycle(vCurr);
                if(foundCycle)
                    break;
            }
            resetSeen();
            if(!foundCycle)
                Cycle_Vertices.clear();
        }
    }

    public boolean findCycle(Graph.Vertex v1)
    {
        Graph.Edge eCurr;
        DFSVertex df1 = this.node[v1.getName()-numDisabledVertices];
        df1.seen=true;
        while (df1.edgeInAdj.hasNext())
        {
            eCurr = df1.edgeInAdj.next();
            if(!this.node[(eCurr.otherEnd(v1)).getName()-numDisabledVertices].seen)
            {
                Cycle_Vertices.add(df1);
                if(findCycle(eCurr.otherEnd(v1)))
                    return true;
                //this.node[v1.getName()].edgeInAdj = savePrevEdgeIterator;
            }
            else
            {
                Cycle_Vertices.add(df1);
                Cycle_Vertices.add(this.node[(eCurr.otherEnd(v1)).getName()-numDisabledVertices]);
                return true;
            }

            Cycle_Vertices.remove(Cycle_Vertices.size()-1);
        }
        return false;
    }

    public int findDFS(Iterator<Graph.Vertex> Vit,boolean ifFlipped)
    {
        Graph.Vertex vCurr;
        int i = 0;
        while(Vit.hasNext())
        {
            vCurr = Vit.next();

            if (!node[vCurr.getName()-numDisabledVertices].seen)
            {
                findDFS(vCurr,ifFlipped);
                i++;
            }
        }
        return i;
    }


    public ArrayList<DFS.DFSVertex> DFSOnRevGraph( Graph.Vertex v1,ArrayList<DFS.DFSVertex> comp, int compNum)
    {
        DFSVertex df1 = this.node[v1.getName()-numDisabledVertices];
        df1.componentNumber=compNum;
        df1.seen=true;
        Graph.Edge eCurr;
        comp.add(df1);
        while (df1.edgeInRevAdj.hasNext())
        {
            eCurr = df1.edgeInRevAdj.next();
            if(!this.node[(eCurr.otherEnd(v1)).getName()-numDisabledVertices].seen)
            {
                comp=DFSOnRevGraph(eCurr.otherEnd(v1),comp,compNum);
            }
        }
        return comp;
    }

    public void findDFS( Graph.Vertex v1,boolean ifFlippedGraph)
    {
        DFSVertex df1 = this.node[v1.getName()-numDisabledVertices];
        this.timeCounter++;
        df1.seen=true;
        this.DFS_Vertices.add(df1);
        df1.startTime=timeCounter;
        if(!ifFlippedGraph)
        {
            Graph.Edge eCurr;
            while (df1.edgeInAdj.hasNext())
            {
                eCurr = df1.edgeInAdj.next();
                if(!this.node[(eCurr.otherEnd(v1)).getName()-numDisabledVertices].seen)
                {
                    findDFS(eCurr.otherEnd(v1),false);
                }
            }
            this.timeCounter++;
            df1.finishTime = this.timeCounter;
        }
        else
        {
            Graph.Edge eCurr;
            while (df1.edgeInRevAdj.hasNext())
            {
                eCurr = df1.edgeInRevAdj.next();
                if(!this.node[(eCurr.otherEnd(v1)).getName()-numDisabledVertices].seen)
                {
                    findDFS(eCurr.otherEnd(v1),true);
                }
            }
            this.timeCounter++;
            df1.finishTime = this.timeCounter;
            //this.node[v1.getName()].edgeInRevAdj = savePrevRevEdgeIterator;
        }
    }
}