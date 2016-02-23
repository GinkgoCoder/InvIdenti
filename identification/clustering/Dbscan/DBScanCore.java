package clustering.Dbscan;

import base.patent;
import clustering.SimMatrix;
import clustering.distancefunction.AbstractDistance;

import java.util.ArrayList;

/**
 * Created by leisun on 15/11/25.
 */
public class DBScanCore {

    int minpts=1;
    double radius;
    SimMatrix simMatrix;
    boolean[] corePts;
    boolean[] signPts;
    private ArrayList<DBCluster> clusters=new ArrayList<>();

    DBScanCore(){

    }

    public void setMinpts(int minpts) {
        this.minpts=minpts;
    }

    public void setRadius(double radius) {
        this.radius=radius;
    }

    /**
     * Run the patent clustering
     * @param patents the arraylist of the patents
     * @param distance the distance function
     */
    public void buildCluster(ArrayList<patent> patents, AbstractDistance distance,ArrayList<Integer> shuffleIndex)
    {
        double start=System.currentTimeMillis();
        this.simMatrix=new SimMatrix(patents,distance);
        //this.simMatrix.storeMatrix("distanceMatrix.txt");
//        this.simMatrix=new SimMatrix("distanceMatrix5000.txt");
     //   this.simMatrix.setShuffledIndex(shuffleIndex);
        double end=System.currentTimeMillis();
        System.out.println("Reading Matrix Time:"+(end-start));

        initilize(patents.size());

        for(int i=0;i<patents.size();i++) {
            if(corePts[i]&&!signPts[i]) {
                DBCluster var0=new DBCluster();
                var0.addInstances(expandCluster(i));
                this.clusters.add(var0);
            }
        }

        for(int i=0;i<patents.size();i++) {
            if(!signPts[i]) {
                DBCluster var0=new DBCluster();
                var0.addInstance(i);
                this.clusters.add(var0);
            }
        }

    }

    public ArrayList<DBCluster> get_Clusters()
    {

        return this.clusters;
    }


    public void initilize(int size) {
        corePts=new boolean[size];
        signPts=new boolean[size];

        signAllCorePts();

        for(boolean var1:signPts) {
            var1=false;
        }
    }


    public void signAllCorePts(){
        int corenumber=0;
        for(int i=0;i<this.simMatrix.getSimMatrix().size();i++) {
            int temp=0;
            for(int j=0;j<simMatrix.getSimMatrix().size();j++) {
                if (i!=j&&simMatrix.getSimbetweenPatents(i,j)>=this.radius) {
                    temp++;
                }
            }
            if (temp>=minpts) {
                corePts[i]=true;
                corenumber++;
            }
        }
        System.out.println("Core number: "+corenumber);
    }


    public ArrayList<Integer> expandCluster(int i) {
        ArrayList<Integer> pts=new ArrayList<>();
        ArrayList<Integer> temp=getNeighbours(i);
        ArrayList<Integer> expand=new ArrayList<>();

        for(Integer var0:temp) {
            if(!signPts[var0]) {
                pts.add(var0);
                if (corePts[var0]) expand.add(var0);
                signPts[var0]=true;
            }
        }

        for(Integer var1:expand) {
            pts.addAll(expandCluster(var1));
        }

        return pts;
    }

    public ArrayList<Integer> getNeighbours(int i) {
        ArrayList<Integer> neighbours=new ArrayList<>();
        for(int j=0;j<simMatrix.getSimMatrix().size();j++) {
            if (i!=j&&simMatrix.getSimbetweenPatents(i,j)>radius) {
                neighbours.add(j);
            }
        }
        return neighbours;
    }
}
