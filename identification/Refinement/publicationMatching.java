package Refinement;

import base.indexCluster;
import base.pair;
import base.patent;
import base.patentCluster;
import clustering.Dbscan.DBCluster;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by leisun on 16/1/21.
 */
public class publicationMatching {

    ArrayList<patentCluster> pantentClusters;
    ArrayList<indexCluster> indexClusters;
    String name;
    public double threshold=0;
    String[] IDs;
    ArrayList<publication> publications;
    public ArrayList<patentCluster> resultClusters=new ArrayList<>();
    public ArrayList<indexCluster> resultClusters_index=new ArrayList<>();
    String path="/Users/leisun/Desktop/ThesisData/ES/PBMED/Publication/";
    String NPRPath="/Users/leisun/Desktop/ThesisData/ES/PBMED/PatentsText/";
    HashMap<String,ArrayList<patentCluster>> table=new HashMap<>();
    HashMap<String,ArrayList<indexCluster>> table_index=new HashMap<>();

    public publicationMatching(String name, ArrayList<patentCluster> clusters, ArrayList<indexCluster> indexClusters) {

        this.indexClusters=indexClusters;

        this.name=name;
        this.pantentClusters=clusters;

        this.publications=getAllPublications(path+this.name+"/");
        IDs=new String[clusters.size()];

        setAllNPRs();
        System.out.println();
        for(int i=0;i<pantentClusters.size();i++) {

            String var0=oneClusterMatching(pantentClusters.get(i));

            if (var0==null){

                resultClusters.add(pantentClusters.get(i));
                resultClusters_index.add(indexClusters.get(i));
            } else {
               if(table.containsKey(var0)) {

                   table.get(var0).add(pantentClusters.get(i));
                   table_index.get(var0).add(indexClusters.get(i));
               } else {

                   ArrayList<patentCluster> temp=new ArrayList<>();
                   temp.add(pantentClusters.get(i));
                   ArrayList<indexCluster> temp2=new ArrayList<>();
                   temp2.add(indexClusters.get(i));
                   table.put(var0,temp);
                   table_index.put(var0,temp2);
               }
            }
        }

        pair<ArrayList<patentCluster>,ArrayList<indexCluster>> var2=mergeCluster();

        resultClusters.addAll(var2.firstarg);
        resultClusters_index.addAll(var2.secondarg);
    }


    private pair<ArrayList<patentCluster>,ArrayList<indexCluster>> mergeCluster(){
        ArrayList<patentCluster> result=new ArrayList<>();
        ArrayList<indexCluster> result_2=new ArrayList<>();
        for(String str:table.keySet()) {
            patentCluster temp=new patentCluster();

            DBCluster temp2=new DBCluster();
            for (patentCluster p:table.get(str)){
                for(patent var0:p.getPatents()) {
                    temp.addPatent(var0);
                }
            }


            for(indexCluster var0:table_index.get(str) ) {
                    for (Integer var1 : var0.getPatentsIndex()) {
                        temp2.addInstance(var1);
                    }
            }

            result.add(temp);
            result_2.add(temp2);

        }

        return new pair<>(result,result_2);
    }
    private void setAllNPRs(){
        for(patentCluster cluster:pantentClusters) {
            for(patent p:cluster.getPatents()) {
                p.setNPR(NPRPath);
            }
        }
    }



    private String oneClusterMatching(patentCluster p){
        String result;

        result=NPRMatching(p);

        if (result!=null) return result;

        result=AffiliationMatching(p);

        if (result!=null) return result;

        result=abstractMatching(p);

        if (result!=null) return result;

        return null;
    }



    private String abstractMatching(patentCluster c){
        HashMap<String,Integer> IDS=new HashMap<>();
        for (patent p:c.getPatents()) {
            String var0=abstractMatch(p);
            if (var0!=null) {
                if (IDS.containsKey(var0)) {
                    IDS.put(var0,IDS.get(var0)+1);
                } else {
                    IDS.put(var0,1);
                }
            }
        }

        if (IDS.keySet().size()==0) return null;

        String result="";

        int max=0;

        for (String str:IDS.keySet()) {
            if (IDS.get(str)>max) {
                result=str;
                max=IDS.get(str);
            }
        }

        return result;
    }


    private String AffiliationMatching(patentCluster c){
        HashMap<String,Integer> IDS=new HashMap<>();
        for (patent p:c.getPatents()) {
            String var0=affiliationMatch(p);
            if (var0!=null) {
                if (IDS.containsKey(var0)) {
                    IDS.put(var0,IDS.get(var0)+1);
                } else {
                    IDS.put(var0,1);
                }
            }
        }

        if (IDS.keySet().size()==0) return null;

        String result="";

        int max=0;

        for (String str:IDS.keySet()) {
            if (IDS.get(str)>max) {
                result=str;
                max=IDS.get(str);
            }
        }

        return result;
    }



    private String NPRMatching(patentCluster c){
        HashMap<String,Integer> IDS=new HashMap<>();
        for (patent p:c.getPatents()) {
            String var0=titleMatch(p);
            if (var0!=null) {
                if (IDS.containsKey(var0)) {
                    IDS.put(var0,IDS.get(var0)+1);
                } else {
                    IDS.put(var0,1);
                }
            }
        }

        if (IDS.keySet().size()==0) return null;

        String result="";

        int max=0;

        for (String str:IDS.keySet()) {
            if (IDS.get(str)>max) {
                result=str;
                max=IDS.get(str);
            }
        }

        return result;
    }

    private String titleMatch(patent p){
        if (p.getNPR()==null) return null;
        for(publication var0:publications) {
            if (p.getNPR().contains(var0.getTitle())) {
                if (var0.getAuthorID()!=null) return var0.getAuthorID(); else {
                    return var0.getID();
                }
            }
        }

        return null;
    }

    private String abstractMatch(patent p) {


        if (p.getAbs()==null) return null;
        int index=-1;
        double max=0;

        for (int i=0;i<publications.size();i++) {

            if (publications.get(i).getAbstractText() != null) {
                double temp = abstractMatching.getSimilarity(publications.get(i).getAbstractText(), p.getAbs());

                if (temp > max) {
                    max = temp;
                    index = i;
                }
            }


            }


        if (index == -1) return null;
        if (publications.get(index).getAuthorID() != null) return publications.get(index).getAuthorID();
        else {
            return publications.get(index).getID();

        }
    }


    private String affiliationMatch(patent p) {
        for(publication var0:publications) {
            if (var0.getAffiliation()!=null) {
              //  System.out.println(p.getAssignee()+" "+var0.getAffiliation());
                if(var0.getAffiliation().contains(p.getAssignee())) {
                    if (var0.getAuthorID()!=null) return var0.getAuthorID(); else {
                        return var0.getID();
                    }
                }
            }
        }
        return null;
    }


    private ArrayList<publication> getAllPublications(String path){
        ArrayList<publication> result=new ArrayList();
        File f=new File(path);
        File[] fs=f.listFiles();
        for (File var0:fs) {
           result.add(new publication(var0.getAbsolutePath()));
        }
        return result;
    }



}
