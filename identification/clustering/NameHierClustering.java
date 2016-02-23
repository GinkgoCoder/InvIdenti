package clustering;

import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

import preprocessing.IniFile;


import java.util.ArrayList;


/**
 * Created by leisun on 15/11/9.
 */
public class NameHierClustering {

    ArrayList<NameCluster> nameClusters=new ArrayList<>();

    ArrayList<ArrayList<Integer>> clusters=new ArrayList<>();

    double[][] simMatrix;

    private double threshold=1.0;

    public boolean pCorrelation=new IniFile().getPCorrelation();





    public ArrayList<ArrayList<Integer>>getClusters() {
        return clusters;
    }

    public NameHierClustering(ArrayList<NameCluster> clusters){

        this.nameClusters=clusters;

    }

    public void setThreshold(double threshold) {
        this.threshold=threshold;
    }

    public void clusteiring(){

        this.simMatrix=this.getSimMatrix();

        initializeClusters();



        int current_NumClusters=clusters.size();


        while(current_NumClusters>2)
        {
            if (!mergeCluster()) {
                break;
            } else {
                current_NumClusters=clusters.size();

            }
        }

    }


    public void initializeClusters(){
        for(int i=0;i<nameClusters.size();i++) {
            ArrayList<Integer> var0=new ArrayList<>();
            var0.add(i);
            clusters.add(var0);
        }
    }

    public double[][] getSimMatrix() {
        double [][] simMatrix=new double[nameClusters.size()][nameClusters.size()];
        for(int i=0;i<this.nameClusters.size()-1;i++) {
            for(int j=i+1;j<this.nameClusters.size();j++) {
                simMatrix[i][j]=simMatrix[j][i]=compareName(this.nameClusters.get(i).LastName,this.nameClusters.get(j).LastName);
            }
        }
        return simMatrix;
    }

    public double maxSimBetweenClusters(int index1,int index2) {
        double result=-Double.MAX_VALUE;
        for(int i=0;i<this.clusters.get(index1).size();i++) {
            for (int j=0;j<this.clusters.get(index2).size();j++) {
                double temp=simMatrix[this.clusters.get(index1).get(i)][this.clusters.get(index2).get(j)];
                if (temp>result) result=temp;
            }
        }
        return result;
    }





    public  boolean mergeCluster()
    {
        double mostSim=maxSimBetweenClusters(0,1);
        int most_i=0;
        int most_j=1;

        for(int i=0;i<clusters.size()-1;i++)
            for(int j=i+1;j<clusters.size();j++)
            {
                double temp=maxSimBetweenClusters(i,j);

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
            if (mostSim>=threshold) {
                clusters.get(most_i).addAll(clusters.get(most_j));

                clusters.remove(most_j);

                return true;
            }
            else
            {
                return false;
            }
        }
        else {
            if (mostSim<=threshold){

                clusters.get(most_i).addAll(clusters.get(most_j));

                clusters.remove(most_j);

                return true;
            }
            else
            {

                return false;
            }

        }
    }




    public double compareName(String name1,String name2) {
        IniFile ini=new IniFile();
        boolean pCorrelation=ini.getPCorrelation();

        if (name1==null || name2==null||name1.length()==0||name2.length()==0) {
            if (pCorrelation)  {
                return 0;
            } else {
                return 1;
            }
        }

        NormalizedLevenshtein var0 = new NormalizedLevenshtein();

        if (pCorrelation)
        {
            return (1-var0.distance(name1,name2));
        } else {
            return var0.distance(name1,name2);
        }
    }

}
