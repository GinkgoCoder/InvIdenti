package clustering;

import base.patentCluster;
import clustering.distancefunction.AbstractDistance;
import clustering.hierarchy.HierCluster;

import java.util.ArrayList;

/**
 * Created by leisun on 15/11/9.
 */
public class invClustering extends patentClustering{

    private SimMatrix simMatrix;
    ArrayList<NameCluster> nameClusters=new ArrayList<>();
    public ArrayList<String> patentsID;

    public void Cluster(AbstractDistance d) {
        /**
         * Name clustering with 1.0 threshold
         */
        initialize();

        simMatrix = new SimMatrix(patents, d);
        NameHierClustering var0 = new NameHierClustering(nameClusters);
        var0.clusteiring();
        nameClusters = mergeNameCluster(var0.getClusters());


        for (NameCluster c : this.nameClusters) {
            c.ClusterByHier(this.threshold, simMatrix);
        }

        /**
         * Name clustering with other threshold
         */

        var0 = new NameHierClustering(nameClusters);
        var0.setThreshold(0.8);
        var0.clusteiring();
        nameClusters = mergeNameCluster(var0.getClusters());

        for (NameCluster c : this.nameClusters) {
            c.ClusterByHier(this.threshold, simMatrix);
        }



        int i = 0;
        for (NameCluster var1 : nameClusters) {
            for (HierCluster c : var1.clusters) {
                clustersIndex.add(c);
                patentCluster temp = new patentCluster();
                temp.setSerial(i);
                for (Integer j : c.getPatentsIndex()) {
                    temp.addPatent(patents.get(j));
                }
                clusters.add(temp);
                i++;
            }

        }
    }



    public ArrayList<NameCluster> mergeNameCluster(ArrayList<ArrayList<Integer>> clusters){
        ArrayList<NameCluster> result=new ArrayList<>();
        for(ArrayList<Integer> var0:clusters) {
            for(int i=1;i<var0.size();i++) {
                nameClusters.get(var0.get(0)).clusters.addAll(nameClusters.get(var0.get(i)).clusters);
            }
            result.add(nameClusters.get(var0.get(0)));
        }
       return result;
    }


    public void initialize() {
        nameClusters.clear();
        for(int i=0;i<this.patents.size();i++) {
            NameCluster var0=new NameCluster(this.patents.get(i).getAuthor());
            HierCluster var1=new HierCluster();
            var1.addInstance(i);
            var0.addInstance(var1);
            nameClusters.add(var0);
        }
    }



}
