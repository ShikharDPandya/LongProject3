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
    public DFS(Graph g, Iterator vertexIterator, ArrayList<Iterator<Graph.Edge>> edgeIterators,ArrayList<Iterator<Graph.Edge>> revEdgeIterators)
    {
        super(g);
        vertexInGraph=vertexIterator;
        int counter = 0;
        DFS_Vertices = new ArrayList<>();
        Cycle_Vertices=new ArrayList<>();
        //SortedByFinishedTime = new LinkedList<>();
        node = new DFSVertex[g.n];
        timeCounter=0;

        initializeNodes(vertexIterator,edgeIterators,revEdgeIterators);

    }

    void initializeNodes(Iterator<Graph.Vertex> Vit,ArrayList<Iterator<Graph.Edge>> edgeIterators,ArrayList<Iterator<Graph.Edge>> revEdgeIterators)
    {
        Iterator<Graph.Vertex> copyIterator = Vit;
        Graph.Vertex v;
        while (copyIterator.hasNext())
        {
            v = copyIterator.next();
            Graph.Edge e;
            Iterator<Graph.Edge> Eit = edgeIterators.get(v.getName());
            Iterator<Graph.Edge> revEit = revEdgeIterators.get(v.getName());
            node[v.getName()] = new DFSVertex(v.getName(),Eit,revEit);
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
            //this.originalVertex=v;
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
        for(int i = 0; i < DMSTGraph.currentSizeOfGraph;i++)
        {
            node[i].setIterators(edgeIterators.get(i),reverseEdgeIterators.get(i));
        }
    }
    void resetSeen(Iterator<Graph.Vertex> Vit)
    {
        //Iterator<Graph.Vertex> savePrev= vertexInGraph;
        Graph.Vertex vCurr;
        while(Vit.hasNext())
        {
            vCurr = Vit.next();
            node[vCurr.getName()].seen=false;
        }
        //vertexInGraph = savePrev;
    }

    void resetSeen()
    {
        //Iterator<Graph.Vertex> savePrev= vertexInGraph;
        Graph.Vertex vCurr;
        while(vertexInGraph.hasNext())
        {
            vCurr = vertexInGraph.next();
            node[vCurr.getName()].seen=false;
        }
        //vertexInGraph = savePrev;
    }

    public void findCycle(Iterator<Graph.Vertex> Vit)
    {
        Graph.Vertex vCurr;
        //Iterator<Graph.Vertex> savePrev = vertexInGraph;
        while(Vit.hasNext())
        {
            vCurr = Vit.next();

            if (!node[vCurr.getName()].seen)
            {
                if(findCycle(vCurr))
                    break;
            }
            resetSeen(vertexInGraph);
        }
        //vertexInGraph = savePrev;
    }

    public boolean findCycle(Graph.Vertex v1)
    {
        Graph.Edge eCurr;
        //Iterator<Graph.Edge> savePrevEdgeIterator = this.node[v1.getName()].edgeInAdj;
        while (this.node[v1.getName()].edgeInAdj.hasNext())
        {
            eCurr = this.node[v1.getName()].edgeInAdj.next();
            if(!this.getVertex(eCurr.otherEnd(v1)).seen)
            {
                Cycle_Vertices.add(this.node[v1.getName()]);
                findCycle(eCurr.otherEnd(v1));
                //this.node[v1.getName()].edgeInAdj = savePrevEdgeIterator;
            }
            else
                return true;
        }
        return false;
    }

    public void findDFS(Iterator<Graph.Vertex> Vit,boolean ifFlipped)
    {
        Graph.Vertex vCurr;
        while(Vit.hasNext())
        {
            vCurr = Vit.next();

            if (!node[vCurr.getName()].seen)
            {
                findDFS(vCurr,ifFlipped);
            }
        }
    }

    public void findDFS( Graph.Vertex v1,boolean ifFlippedGraph)
    {
        this.timeCounter++;
        this.node[v1.getName()].seen=true;
        this.DFS_Vertices.add(this.node[v1.getName()]);
        this.node[v1.getName()].startTime=timeCounter;
        if(!ifFlippedGraph)
        {
            Graph.Edge eCurr;
            Iterator<Graph.Edge> savePrevEdgeIterator =this.node[v1.getName()].edgeInAdj;
            while (this.node[v1.getName()].edgeInAdj.hasNext())
            {
                eCurr = this.node[v1.getName()].edgeInAdj.next();
                if(!this.getVertex(eCurr.otherEnd(v1)).seen)
                {
                    findDFS(eCurr.otherEnd(v1),false);
                }
            }
            this.timeCounter++;
            this.node[v1.getName()].finishTime = this.timeCounter;
            this.node[v1.getName()].edgeInAdj = savePrevEdgeIterator;
        }
        else
        {
            Graph.Edge eCurr;
            Iterator<Graph.Edge> savePrevRevEdgeIterator = this.node[v1.getName()].edgeInRevAdj;
            while (this.node[v1.getName()].edgeInRevAdj.hasNext())
            {
                eCurr = this.node[v1.getName()].edgeInRevAdj.next();
                if(!this.getVertex(eCurr.otherEnd(v1)).seen)
                {
                    findDFS(eCurr.otherEnd(v1),true);
                }
            }
            this.timeCounter++;
            this.node[v1.getName()].finishTime = this.timeCounter;
            this.node[v1.getName()].edgeInRevAdj = savePrevRevEdgeIterator;
        }
    }


    /*
    public void findDFS( DFSVertex v1,boolean ifFlippedGraph)
    {
        this.timeCounter++;
        v1.seen=true;
        this.DFS_Vertices.add(v1);
        v1.startTime=timeCounter;
        if(!ifFlippedGraph)
        {
            for (Graph.Edge e : v1.originalVertex.adj)
            {
                if (this.getVertex(e.otherEnd(this.xg.getVertex(v1.originalVertex.name + 1))).seen)
                {
                    continue;
                }
                else
                {
                    findDFS(this.getVertex(e.otherEnd(this.xg.getVertex(v1.originalVertex.name + 1))),false);
                }
            }
            this.timeCounter++;
            v1.finishTime = this.timeCounter;
        }
        else
        {
            for (Graph.Edge e : v1.originalVertex.revAdj)
            {
                if (this.getVertex(e.otherEnd(this.xg.getVertex(v1.originalVertex.name + 1))).seen)
                {
                    continue;
                } else
                {
                    findDFS(this.getVertex(e.otherEnd(this.xg.getVertex(v1.originalVertex.name + 1))),true);
                }
            }
            this.timeCounter++;
            v1.finishTime = this.timeCounter;
        }
    }
    */
}