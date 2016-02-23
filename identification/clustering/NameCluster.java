package clustering;

import clustering.hierarchy.HierCluster;
import clustering.hierarchy.HierCore;

import java.util.ArrayList;

/**
 * Created by leisun on 15/11/9.
 */
public class NameCluster {
    String LastName;



    ArrayList<HierCluster> clusters=new ArrayList<>();


    NameCluster(String LastName){
        this.LastName=LastName;
    }

    public void addInstance(HierCluster c) {
        clusters.add(c);
    }

    public void ClusterByHier(Double threshold,SimMatrix simMatrix){
        HierCore hc=new HierCore();
        hc.setEps(threshold);
        hc.buildCluster(clusters,simMatrix);
        clusters=hc.get_Clusters();
    }

}
