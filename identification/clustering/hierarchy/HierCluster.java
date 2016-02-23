package clustering.hierarchy;


import base.indexCluster;
import base.patent;
import clustering.SimMatrix;
import clustering.distancefunction.AbstractDistance;


import java.util.ArrayList;


/**
 * Created by leisun on 15/9/29.
 */
public class HierCluster extends indexCluster {

    private int centroidIndex=-1;



    public HierCluster() {

    }

    public HierCluster(HierCluster c) {
        for (int i:c.getPatentsIndex()) {
            patentsIndex.add(i);
        }
    }





    /**
     * Calculate the max distance between two clusters
     * @param simMatrix similarity matrix
     * @param c1 first patent cluster
     * @param c2 second patent cluster
     * @return the maxDistance between two patent clusters
     */
    public static double maxDitanceBetweenClusters(HierCluster c1,HierCluster c2, SimMatrix simMatrix)
    {

        //c1.computeCentroid(patents,distance);
        //c2.computeCentroid(patents,distance);
       // double maxdistance=distance.distance(patents.get(c1.getPatentsIndex().get(c1.getCentroidIndex())),patents.get(c2.getPatentsIndex().get(c2.getCentroidIndex())));
        double maxdistance=simMatrix.getSimbetweenPatents(c1.getPatentsIndex().get(0),c2.getPatentsIndex().get(0));

        double sum=0;
        for(Integer i:c1.getPatentsIndex()) {

            for(Integer j:c2.getPatentsIndex())
            {
               double temp=simMatrix.getSimbetweenPatents(i,j);
                sum+=temp;
                if (temp<maxdistance) maxdistance=temp;
            }

        }

       return maxdistance;
        //return sum/(c1.getPatentsIndex().size()*c2.getPatentsIndex().size());

    }

    public void computeCentroid(ArrayList<patent> pts,AbstractDistance distance) {
        if (this.getPatentsIndex().size()==0) this.centroidIndex=-1;
        if (this.getPatentsIndex().size()==1||this.getPatentsIndex().size()==2) this.centroidIndex=this.getPatentsIndex().get(0);
        double max=-Double.MAX_VALUE;
        this.centroidIndex=0;
        for (int i=0;i<this.getPatentsIndex().size();i++) {
            double sum=0;
            for (int j=0;j<this.getPatentsIndex().size();j++) {
                if (i!=j) sum+=distance.distance(pts.get(i),pts.get(j));
            }
            if (sum>max)
            {
                max=sum;
                this.centroidIndex=i;
            }
        }
    }

    public int getCentroidIndex() {
        return this.centroidIndex;
    }

}
