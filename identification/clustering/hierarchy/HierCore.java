package clustering.hierarchy;

import base.patent;
import clustering.SimMatrix;
import clustering.distancefunction.AbstractDistance;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leisun on 15/9/29.
 */
public class HierCore {
    private int m_NumClusters=1;
    private ArrayList<HierCluster> m_Clusters=new ArrayList<>();
    protected AbstractDistance m_Distance;
    protected int current_NumClusters;
    protected boolean pCorrelation=true;
    protected boolean silCoeEnable=false;
    protected double eps=Double.MAX_VALUE;
    protected SimMatrix simMatrix;
    protected ArrayList<ArrayList<HierCluster>> resultClustering=new ArrayList<>();

    /**
     * set the number of the clusters
     * @param number the number of the cluster
     */
    public void set_NumClusters(int number) {
        this.m_NumClusters=number;
    }

    public HierCore() {

    }

    public void setpCorrelation(boolean pCorrelation) {this.pCorrelation=pCorrelation;}


    /**
     *
     * @return the number of the clusters
     */
    public int numberOfClusters()
    {
        return current_NumClusters;
    }

    public void setEps(double eps) {
        this.eps=eps;
    }

    /**
     *
     * @return the patent clusters
     */
    public ArrayList<HierCluster> get_Clusters()
    {

        return this.m_Clusters;
    }


    public void setM_Distance(AbstractDistance distance) {
        this.m_Distance=distance;
    }

    /**
     * Run the patent clustering
     * @param patents the arraylist of the patents
     * @param distance the distance function
     */
    public void buildCluster(ArrayList<patent> patents,AbstractDistance distance,ArrayList<Integer> shuffleIndex)
    {


        //this.simMatrix=new SimMatrix(patents,distance);


        double start=System.currentTimeMillis();
        this.simMatrix=new SimMatrix(patents,distance);
        //this.simMatrix.storeMatrix("distanceMatrix.txt");
//        this.simMatrix=new SimMatrix("distanceMatrix5000.txt");
  //      this.simMatrix.setShuffledIndex(shuffleIndex);
        double end=System.currentTimeMillis();
        System.out.println(end-start);

        this.m_Distance=distance;
        initializeCluster(patents);
        ArrayList<Double> silCoes=new ArrayList<>();

        current_NumClusters=m_Clusters.size();


        while(current_NumClusters>m_NumClusters)
        {
            if (!mergeCluster()) {
                break;
            } else {
                current_NumClusters=m_Clusters.size();
                double temp=this.totalAvaverageSilHouette(this.m_Clusters, simMatrix);
                resultClustering.add(cloneClusters(this.m_Clusters));
                silCoes.add(temp);


            }
        }


        if (silCoeEnable==true) {
            Double max = Collections.max(silCoes);


            this.m_Clusters = resultClustering.get(silCoes.indexOf(max));

            current_NumClusters = this.m_Clusters.size();

            System.out.println("Max SilCoe:"+max);

        }



    }

    /**
     *

     */


    public void buildCluster(ArrayList<HierCluster> m_Clusters,SimMatrix simMatrix)
    {

        this.m_Clusters=m_Clusters;

        this.simMatrix=simMatrix;

        ArrayList<Double> silCoes=new ArrayList<>();

        int current_NumClusters=m_Clusters.size();


        while(current_NumClusters>1)
        {
            if (!mergeCluster()) {
                break;
            } else {
                current_NumClusters=m_Clusters.size();
            }
        }

    }

    /**
     * initilize the clusters
     * @param patents ArrayList of the patents
     */
    public void initializeCluster(ArrayList<patent> patents)
    {
        for(int i=0;i<patents.size();i++)
        {
            HierCluster current=new HierCluster();
            current.addInstance(i);
            m_Clusters.add(current);
        }
    }

    /**
     * Merge the cluster with the max similarity

     */
    public  boolean mergeCluster()
    {
        double mostSim=HierCluster.maxDitanceBetweenClusters(this.m_Clusters.get(0),this.m_Clusters.get(1),simMatrix);
        int most_i=0;
        int most_j=1;

        for(int i=0;i<this.m_Clusters.size()-1;i++)
            for(int j=i+1;j<this.m_Clusters.size();j++)
            {
                double temp=HierCluster.maxDitanceBetweenClusters(m_Clusters.get(i), m_Clusters.get(j),simMatrix);


                if(pCorrelation) {

                    if (temp>mostSim)
                        {
                            mostSim=temp;
                            most_i=i;
                            most_j=j;
                        }
                } else {

                    if (temp<mostSim)
                    {
                        mostSim=temp;
                        most_i=i;
                        most_j=j;
                    }
                }
            }




        if (this.pCorrelation){
        if (mostSim>=eps) {
        m_Clusters.get(most_i).getPatentsIndex().addAll(m_Clusters.get(most_j).getPatentsIndex());

        m_Clusters.remove(most_j);

            return true;
        }
        else
        {
            return false;
        }
        }
        else {
            if (mostSim<=eps){

                m_Clusters.get(most_i).getPatentsIndex().addAll(m_Clusters.get(most_j).getPatentsIndex());

                m_Clusters.remove(most_j);

                return true;
            }
            else
            {

                return false;
            }

        }
    }

    public double avaerageSimilarityFromCluster (int index,HierCluster C,SimMatrix simMatrix,boolean within) {
       double result=0.0;

        if (within&&C.getPatentsIndex().size()<2) return 0;

        for (int i:C.getPatentsIndex()) {
            result+=simMatrix.getSimbetweenPatents(index,i);


        }




        return result/C.getPatentsIndex().size();
     }

    public double getSilhouetteofAPatent (int index,int clusterIndex,ArrayList<HierCluster> clusters,SimMatrix simMatrix) {


        double a = avaerageSimilarityFromCluster(index, clusters.get(clusterIndex), simMatrix,true);

        if (a==0) return 0;

        double b = Double.MAX_VALUE;

        for (int i = 0; i < clusters.size(); i++) {
            if (i != clusterIndex) {
                double temp= avaerageSimilarityFromCluster(index, clusters.get(i), simMatrix,false);

                if (temp<b) b=temp;
            }
        }




        if (b==Double.MAX_VALUE) b=0;


        return (b-a)/Math.max(a,b);
    }

    public double totalAvaverageSilHouette(ArrayList<HierCluster> clusters,SimMatrix simMatrix){
        double result=0;

        for (int i=0;i<clusters.size();i++) {

            for(int j:clusters.get(i).getPatentsIndex()) {
                result+=getSilhouetteofAPatent(j,i,clusters,simMatrix);
            }
        }

        return result/simMatrix.getSimMatrix().size();
    }

    public ArrayList<HierCluster> cloneClusters(ArrayList<HierCluster> clusters) {
        ArrayList<HierCluster> result=new ArrayList<>();

        for (HierCluster c:clusters) {
            result.add(new HierCluster(c));
        }
        return result;

    }

}
