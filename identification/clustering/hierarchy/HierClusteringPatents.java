package clustering.hierarchy;

import base.patentCluster;

import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import clustering.patentClustering;

import java.util.ArrayList;

/**
 * Created by leisun on 15/9/29.
 */
public class HierClusteringPatents extends patentClustering
{


    ArrayList<HierCluster> hier_clusters;

    public HierClusteringPatents(ArrayList<Integer> shuffleIndex) {
        super();
        this.shuffleIndex=shuffleIndex;
        this.clusteringType="Hierarchical Clustering";
    }



    /**
     * run clustering on the patents
     */
    public void Cluster()
    {

        HierCore hc=new HierCore();
        hc.setEps(this.threshold);

        try {

            hc.set_NumClusters(this.number_Cluster);

            hc.buildCluster(patents,new CosDistance(),this.shuffleIndex);



            clusters.clear();

            hier_clusters=hc.get_Clusters();

            for(int i=0;i<hc.numberOfClusters();i++)
            {
                patentCluster temp=new patentCluster();
                temp.setSerial(i);
                for(Integer j:hier_clusters.get(i).getPatentsIndex())
                {
                    temp.addPatent(patents.get(j));
                }
                clusters.add(temp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Clustering based on a specific function
     * @param distance distance function
     */
    public void Cluster(AbstractDistance distance) {



        if (!initilization) {
            logger.error("Clustering method: no patents are initialized");
            return;
        }



        HierCore hc=new HierCore();

        hc.setEps(this.threshold);

        hc.setpCorrelation(this.pCorrelation);

        try {



            hc.set_NumClusters(this.number_Cluster);

            hc.buildCluster(patents,distance,this.shuffleIndex);

            clusters.clear();

            hier_clusters=hc.get_Clusters();

            clustersIndex.clear();

            for(HierCluster cluster:this.hier_clusters) {
                this.clustersIndex.add(cluster);
            }

           // System.out.println(hier_clusters.size());


            for(int i=0;i<hc.numberOfClusters();i++)
            {
                patentCluster temp=new patentCluster();
                temp.setSerial(i);
                for(Integer j:hier_clusters.get(i).getPatentsIndex())
                {
                    temp.addPatent(patents.get(j));
                }
                clusters.add(temp);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<HierCluster> getHier_clusters() {
        return this.hier_clusters;
    }
}
