package clustering;

import base.indexCluster;
import base.pair;
import base.patent;
import base.patentCluster;
import clustering.distancefunction.AbstractDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.LanguageCode;
import org.ini4j.Wini;
import preprocessing.patentPreprocessingTF;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by leisun on 15/9/18.
 */
public abstract class patentClustering
{
    protected static Logger logger= LogManager.getLogger(patentClustering.class.getName());
    protected ArrayList<patent> patents;
    protected LanguageCode language=LanguageCode.ENGLISH;
    protected ArrayList<patentCluster> clusters=new ArrayList<>();
    protected ArrayList<indexCluster> clustersIndex=new ArrayList<>();
    protected Instances instances;
    protected int number_Cluster=1;
    protected boolean pCorrelation=true;
    protected boolean initilization=false;
    protected String clusteringType="Abstract Clustering!";

    protected ArrayList<Integer> shuffleIndex;

    protected double threshold=0;

    protected pair<HashMap<String,Integer>,HashMap<String,Integer>> attriInfo;

    public void setLanguage(LanguageCode code)
    {
        this.language=code;
    }

    public void setNumberofCluster(Integer num)
    {
        this.number_Cluster=num;
    }

    public void setThreshold(double threshold) {
        this.threshold=threshold;
    }

    public void setShuffleIndex(ArrayList<Integer> var0) {
        this.shuffleIndex=var0;
    }


    public ArrayList<patentCluster> getClusters()
    {
        return this.clusters;
    }

    public ArrayList<indexCluster> getClustersIndex()
    {
        return this.clustersIndex;
    }

    /**Get original index of the patent**/
    public int getIndex(patent p)
    {

        for(int i=0;i<this.patents.size();i++)
        {
            if(patents.get(i).getPatent_number().equals(p.getPatent_number())) return i;
        }

        return -1;
    }

    /**output the clustering result**/
    public String toString()
    {
        String str="Clustering Type: "+this.clusteringType+"\nThreshold:"+this.threshold+"\nCluster Number:"+clusters.size()+"\n";
        int var0=2000;
        String var1="";
        while(var0>1) {
            var1+="-";
            var0--;
        }

        str+=var1+"\n";
        for(int i=0;i<this.clusters.size();i++)
        {
            str+=("Cluster "+i);
            str+=("\n----------\n");
            for(patent p:this.clusters.get(i).getPatents())
            {
                str+=p.getPatent_number()+"\t"+p.getAuthor();
                if(p.getAuthor().length()<13)
                    for(int j=0;j<(13-p.getAuthor().length());j++)
                    {
                        str+=" ";
                    }
                str+="\t"+p.getTitle()+"\n";
            }
            str+=var1+"\n";
        }
        return str;
    }

    public patentClustering()
    {
        try {
            Wini initalFile=new Wini(new File("invidenti.ini"));
            this.pCorrelation=initalFile.get("DistanceOption","PCorrelation").equalsIgnoreCase("true");

        } catch (IOException e)
        {
            System.out.println("Initial File 'invidenti.ini not found',distance function will use default options");
        }


    }


    public void initilizewithoutpreprocessing(ArrayList<patent> patents) {
        this.patents=patents;
    }

    public void ininitialize(ArrayList<patent> patents,boolean ini) {
        this.patents=patents;
        this.initilization=true;
        clustersIndex.clear();
        clusters.clear();

      if(!ini){
          preprocess();
      }
    }

    public abstract void Cluster(AbstractDistance distance);

    /**Preprocessing for the clustering**/
    protected void preprocess() {

        patentPreprocessingTF preprocess = new patentPreprocessingTF(this.patents);
        preprocess.setLanguage(this.language);
        preprocess.preprocess();
        this.patents = preprocess.getPatents();

    }
}
