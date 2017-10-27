package cs6301.g40;

import java.util.*;

//public class DMST extends GraphAlgorithm<DMST.DMSTVertex>

public class DMSTGraph extends Graph
{
    static int totalSizeOfGraph;
    static int currentSizeOfGraph;
    DMSTVertex[] DMSTVertices;


    //public DMSTVertex returnDMSTVertex(Graph.Vertex u)
    //{
      //  return this.DMSTVertices[u.getName()];
    //}

    public ArrayList<Iterator<Edge>> returnEdgeIterators()
    {
        ArrayList<Iterator<Edge>> Iterators = new ArrayList<>();
        for (int i = 0; i < currentSizeOfGraph; i++)
        {
            Iterators.add(this.DMSTVertices[i].iterator(true));
        }
        return Iterators;
    }

    public ArrayList<Iterator<Edge>> returnReverseEdgeIterators()
    {
        ArrayList<Iterator<Edge>> Iterators = new ArrayList<>();
        for (int i = 0; i < currentSizeOfGraph; i++)
        {
            Iterators.add(this.DMSTVertices[i].iterator(false));
        }
        return Iterators;
    }

    public static class DMSTVertex extends Graph.Vertex
    {
        //DMSTEdge minEdge;
        int minEdge;
        LinkedList<DMSTEdge> DMSTAdj,revDMSTAdj;
        boolean isComponent;
        DMSTVertex repVertex;
        ArrayList<DMSTVertex> memberVerticesOfComponent;
        boolean disabled;

        public DMSTVertex(Graph.Vertex u)
        {
            super(u);
            isComponent=false;
            DMSTAdj = new LinkedList<>();
            revDMSTAdj = new LinkedList<>();
            memberVerticesOfComponent = new ArrayList<>();
            minEdge=0;
            disabled=false;
        }

        public DMSTVertex(ArrayList<DMSTVertex> listOfVertices,Graph.Vertex u)
        {
            super(u);
            isComponent=true;
            DMSTAdj = new LinkedList<>();
            revDMSTAdj = new LinkedList<>();
            memberVerticesOfComponent = new ArrayList<>();
            repVertex = listOfVertices.get(0);
            for (DMSTVertex v:listOfVertices)
            {
                memberVerticesOfComponent.add(v);
            }
            minEdge=0;
        }

        boolean isDisabled() { return disabled; }

        void disable() { disabled = true; }

        //@Override
        public Iterator<Edge> iterator(boolean flippedGraph)
        {
            if(flippedGraph)
                return new DMSTVertexIterator(this);
            else
                return new ReverseDMSTVertexIterator(this);
        }

        class DMSTVertexIterator implements Iterator<Edge>
        {
            DMSTEdge cur;
            Iterator<DMSTEdge> it;
            boolean ready;

            DMSTVertexIterator(DMSTVertex u)
            {
                this.it = u.DMSTAdj.iterator();
                ready = false;
            }

            public boolean hasNext()
            {
                if(ready)
                {
                    return true;
                }

                if(!it.hasNext())
                {
                    return false;
                }
                cur = it.next();

                while((cur.isDisabled()||cur.augmentedWeight != 0) && it.hasNext())
                {
                    cur = it.next();
                }
                ready = true;
                return !cur.isDisabled();
            }

            public DMSTEdge next()
            {
                if(!ready)
                {
                    if(!hasNext())
                    {
                        throw new java.util.NoSuchElementException();
                    }
                }
                ready = false;
                return cur;
            }
        }


        class ReverseDMSTVertexIterator implements Iterator<Edge>
        {
            DMSTEdge cur;
            Iterator<DMSTEdge> it;
            boolean ready;

            ReverseDMSTVertexIterator(DMSTVertex u)
            {
                this.it = u.revDMSTAdj.iterator();
                ready = false;
            }

            public boolean hasNext()
            {
                if(ready)
                {
                    return true;
                }

                if(!it.hasNext())
                {
                    return false;
                }
                cur = it.next();

                while((cur.isDisabled()||cur.augmentedWeight != 0) && it.hasNext())
                {
                    cur = it.next();
                }
                ready = true;
                return !cur.isDisabled();
            }

            public DMSTEdge next()
            {
                if(!ready)
                {
                    if(!hasNext())
                    {
                        throw new java.util.NoSuchElementException();
                    }
                }
                ready = false;
                return cur;
            }
        }

    }

    public static class DMSTEdge extends Graph.Edge
    {
        int augmentedWeight;
        boolean disabled;
        public DMSTEdge(DMSTVertex from, DMSTVertex to, int weight)
        {
            super(from,to,weight);
            augmentedWeight=weight;
            disabled=false;
        }

        boolean isDisabled() {
            DMSTVertex xfrom = (DMSTVertex) from;
            DMSTVertex xto = (DMSTVertex) to;
            return disabled || xfrom.isDisabled() || xto.isDisabled();
        }

        void setAugmentedWeight(int w)
        {
            this.augmentedWeight = w;
        }
    }


    /*
    DMSTVertex getVertex(XGraph.XVertex u)
    {
        return node[u.getName()];
    }
    */
    @Override
    public DMSTVertex getVertex(int n)
    {
        return this.DMSTVertices[n];
    }
    public DMSTGraph(Graph xg)
    {
        super(xg);
        int counter = 0, min = 0;
        totalSizeOfGraph =  2*xg.n;
        currentSizeOfGraph = xg.n;
        DMSTVertices = new DMSTVertex[totalSizeOfGraph];
        for(Graph.Vertex u: xg.v)
        {
            DMSTVertices[u.getName()] = new DMSTVertex(u);
            counter++;
            if(counter == currentSizeOfGraph)
                break;
        }
        counter=0;
        for (Graph.Vertex v:xg.v)
        {
            counter++;
            if(!v.adj.isEmpty())
                min=v.adj.get(0).weight;
            for (Graph.Edge e:v.adj)
            {
                Graph.Vertex u = e.otherEnd(xg.getVertex(v.getName()+1));
                //XGraph.XVertex u = xg.getVertex(u1);
                DMSTVertex vDMST = DMSTVertices[v.getName()];
                DMSTVertex uDMST = DMSTVertices[u.getName()];
                DMSTEdge edge = new DMSTEdge(vDMST,uDMST,e.weight);
                //DMSTEdge revEdge = new DMSTEdge(vDMST,uDMST,e.weight);
                vDMST.DMSTAdj.add(edge);
                if(!uDMST.revDMSTAdj.isEmpty())
                {
                    //if( uDMST.revDMSTAdj.get(uDMST.revDMSTAdj.size()-1).weight < e.weight)
                    if( uDMST.revDMSTAdj.get(0).weight < e.weight)
                        uDMST.revDMSTAdj.add(edge);
                    else
                    {
                        uDMST.minEdge = edge.augmentedWeight;
                        uDMST.revDMSTAdj.push(edge);
                    }
                }
                else
                {
                    uDMST.minEdge = edge.augmentedWeight;
                    uDMST.revDMSTAdj.push(edge);
                }

                /*
                if(min >= e.weight)
                {
                    vDMST.minEdge = edge;
                    min = e.weight;
                }
                */
            }
            if(counter == currentSizeOfGraph)
                break;
        }
    }

    @Override
    public Iterator<Vertex> iterator()
    {
        return new DMSTGraphIterator(this);
    }


    class DMSTGraphIterator implements Iterator<Vertex> {
        Iterator<DMSTVertex> it;
        DMSTVertex xcur;

        DMSTGraphIterator(DMSTGraph dg) {
            this.it = new ArrayIterator<DMSTVertex>(dg.DMSTVertices, 0, dg.size() - 1);  // Iterate over existing elements only
        }


        public boolean hasNext() {
            if (!it.hasNext()) {
                return false;
            }
            xcur = it.next();
            while (xcur.isDisabled() && it.hasNext()) {
                xcur = it.next();
            }
            return !xcur.isDisabled();
        }

        public Vertex next() {
            return xcur;
        }

        public void remove() {
        }
    }

        public static DMSTVertex createDMSTComponent(ArrayList<DMSTVertex> VerticesInCycle)
    {
        Graph.Vertex v = new Graph.Vertex(currentSizeOfGraph);
        //XGraph.XVertex u = new XGraph.XVertex(v);
        DMSTVertex c = new DMSTVertex(VerticesInCycle,v);
        for (DMSTVertex v1:VerticesInCycle)
        {
            v1.disable();
        }
        c.memberVerticesOfComponent.addAll(VerticesInCycle);
        return c;
    }

     boolean addDMSTVertex(DMSTVertex v,ArrayList<DMSTEdge> edges)
     {
         if(currentSizeOfGraph == totalSizeOfGraph)
            return false;
         DMSTVertices[currentSizeOfGraph] = v;
         DMSTVertex otherVertex = new DMSTVertex(v);
         for (DMSTEdge e:edges)
         {
             if(!(v.memberVerticesOfComponent.contains(this.DMSTVertices[e.from.getName()+1])))
             {
                 otherVertex = (DMSTVertex) e.from;
                 DMSTEdge e1 = new DMSTEdge(otherVertex,v,e.weight);
                 e1.setAugmentedWeight(e.augmentedWeight);
                 otherVertex.DMSTAdj.add(e1);
                 v.revDMSTAdj.add(e);
             }
             else if(!(v.memberVerticesOfComponent.contains(this.DMSTVertices[e.to.getName()+1])))
             {
                 otherVertex = (DMSTVertex) e.to;
                 DMSTEdge e1 = new DMSTEdge(v,otherVertex,e.weight);
                 e1.setAugmentedWeight(e.augmentedWeight);
                 v.DMSTAdj.add(e1);
                 otherVertex.revDMSTAdj.add(e);
             }
         }
         currentSizeOfGraph++;
         return true;
     }

     public void printEdges()
     {
         int counter = 0;
         for (DMSTVertex v:this.DMSTVertices)
         {
             for (DMSTEdge e:v.DMSTAdj)
             {
                 System.out.println(" ("+e.from+", "+e.to+") ");
                 System.out.println("AugmentedWeight "+ e.augmentedWeight);
             }
             counter++;
             if(counter == currentSizeOfGraph)
                 break;
         }
     }

    public static void main(String[] args)
    {
        int numComps=0;
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Graph: ");
        Graph g = Graph.readDirectedGraph(in);
        //XGraph xg = new XGraph(g);
        DMSTGraph dg = new DMSTGraph(g);
        dg.printEdges();

        //numComps=StronglyConnectedComps.findStronglyConnectedComponents(dg,numComps);
        System.out.println("Number of SCC: "+numComps);
        ArrayList<DMSTVertex> dummyCycle= new ArrayList<>();
        ArrayList<DMSTEdge> dummyEdges = new ArrayList<>();
        dummyCycle.add(dg.DMSTVertices[0]);
        dummyCycle.add(dg.DMSTVertices[1]);
        dummyCycle.add(dg.DMSTVertices[2]);

        dummyEdges.add(dg.DMSTVertices[0].DMSTAdj.get(1));
        //dummyEdges.add(dg.DMSTVertices[1].DMSTAdj.get(0));
        //dummyEdges.add(dg.DMSTVertices[2].DMSTAdj.get(0));

        if(dg.addDMSTVertex(createDMSTComponent(dummyCycle),dummyEdges))
        {
            /*
            dg.DMSTVertices[0].disable();
            dg.DMSTVertices[1].disable();
            dg.DMSTVertices[2].disable();
            */
            System.out.println("Componenet Created successfully");
        }

        for (Graph.Vertex v:dg)
        {
            System.out.println(v.getName());
        }
    }
}