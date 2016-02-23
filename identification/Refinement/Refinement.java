package Refinement;

import base.indexCluster;
import base.patent;
import base.patentCluster;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by leisun on 16/1/20.
 */
public class Refinement {
    ArrayList<patentCluster> clusters;
    ArrayList<indexCluster> clusters_index;

    public ArrayList<patentCluster> clusters_r=new ArrayList<>();
    public ArrayList<indexCluster> clusters_index_r=new ArrayList<>();

    HashMap<String,ArrayList<patentCluster>> clusterTable=new HashMap<>();
    HashMap<String,ArrayList<indexCluster>> clusterTable_index=new HashMap<>();

    public Refinement(ArrayList<patentCluster> clusters,ArrayList<indexCluster> clusters_index) {
        this.clusters=clusters;
        this.clusters_index=clusters_index;
        hashSetting();
    }

    public void hashSetting(){
        for(int i=0;i<clusters.size();i++) {
           String name=findTheName(clusters.get(i));
            if (clusterTable.containsKey(name)) {
                clusterTable.get(name).add(clusters.get(i));
                clusterTable_index.get(name).add(clusters_index.get(i));

            } else {
                ArrayList<patentCluster> temp=new ArrayList<>();
                temp.add(clusters.get(i));
                ArrayList<indexCluster> temp_2=new ArrayList<>();
                temp_2.add(clusters_index.get(i));
                clusterTable.put(name,temp);
                clusterTable_index.put(name,temp_2);
            }
        }
        System.out.println("Finding possible clusters to merge...");
        for(String str:clusterTable.keySet()) {
            if (clusterTable.get(str).size()>1) {
                System.out.println(str);
                publicationMatching matching=new publicationMatching(str,clusterTable.get(str),clusterTable_index.get(str));
                clusters_r.addAll(matching.resultClusters);
                clusters_index_r.addAll(matching.resultClusters_index);
            } else {
                clusters_r.addAll(clusterTable.get(str));
                clusters_index_r.addAll(clusterTable_index.get(str));
            }
        }

    }

    public String findTheName(patentCluster c){

        HashMap<String,Integer> nameTable=new HashMap<>();
        for(patent p:c.getPatents()) {
            String fistnames=p.getFirstName();
            String lastname=p.getLastName();
            String[] var0=fistnames.split(" ");
            String var1=lastname+" ";
            for (String str:var0) {
                var1+=str.charAt(0);
            }

            if (nameTable.containsKey(var1)) {
                nameTable.put(var1,nameTable.get(var1)+1);
            } else {
                nameTable.put(var1,1);
            }
        }
        String str="";
        int max=0;
        for(String var2:nameTable.keySet()) {
            if (nameTable.get(var2)>max) {
                max=nameTable.get(var2);
                str=var2;
            }
        }
        return str;
    }

}
