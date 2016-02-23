package Evaluation;

import Refinement.Refinement;
import base.indexCluster;
import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.patentClustering;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.LanguageCode;
import preprocessing.*;

import java.util.ArrayList;

/**
 * Created by leisun on 15/11/3.
 */
public class Evaluation {

    private static Logger logger= LogManager.getLogger(Evaluation.class.getName());


    ArrayList<patent> patents;
    ArrayList<String> patentsID;
    public double lumping;
    public double splitting;





    public Evaluation(ArrayList<patent> patents,ArrayList<String> IDs) {

        this.patentsID=IDs;
        this.patents=patents;
        //this.patents=preprocess(patents);

    }

    public double evaluate(AbstractDistance distance,double threshold,patentClustering method){


        method.ininitialize(patents,false);

        method.setThreshold(threshold);
        logger.info("Clustering...");
        method.Cluster(distance);

        //logger.info("Publication-Patent Matching...");
        //Refinement refinement=new Refinement(method.getClusters(),method.getClustersIndex());
       // textOperator.storeText("result.txt",false,method.toString());

       // textOperator.storeText("BenchmarkClusteringResult.txt",false,method.toString());

        //double FMeasurement=getFScoreofClustering(refinement.clusters_index_r,patentsID);



        double FMeasurement=getFScoreofClustering(method.getClustersIndex(),patentsID);

        logger.warn("F Measurement:"+ FMeasurement);

        return FMeasurement;
    }

    /**
     * Evaluate the clustering result using F-measure
     * @param clusters clustering result
     * @param patentsID true patents clustering index
     * @return F measurement
     */
    public double getFScoreofClustering(ArrayList<indexCluster> clusters, ArrayList<String> patentsID) {
        int TN, TP, FN, FP;
        TP = FP = 0;
        for (indexCluster c : clusters) {
            for (int i = 0; i < c.getPatentsIndex().size(); i++) {
                for (int j = 0 ; j < c.getPatentsIndex().size(); j++) {
                    if (patentsID.get(c.getPatentsIndex().get(i)).equalsIgnoreCase(patentsID.get(c.getPatentsIndex().get(j)))) {
                        TP++;
                    } else {
                      //  logger.warn(patents.get(c.getPatentsIndex().get(i)).getPatent_number()+" "+patents.get(c.getPatentsIndex().get(j)).getPatent_number());
                    }
                    FP++;
                }
            }
        }

        FP = FP - TP;

        TN = FN = 0;
        for (int i = 0; i < clusters.size(); i++) {
            for (int j = 0; j < clusters.size(); j++) {
                if (j != i) {
                    for (Integer var1 : clusters.get(i).getPatentsIndex()) {
                        for (Integer var2 : clusters.get(j).getPatentsIndex()) {
                            if (patentsID.get(var1).equalsIgnoreCase(patentsID.get(var2))) {
                                FN++;
                            }
                            TN++;
                        }
                    }
                }
            }
        }
        TN = TN - FN;


        double precision;
        double recall;



    //    System.out.println(TP + " " + FP + " " + " " + TN + " " + FN);

//        System.out.print("lumping:"+(double)FP/(TP+FN));
  //      System.out.print("Splitting:"+(double)FN/(TP+FN));

        this.lumping=(double)FP/(TP+FN);
        this.splitting=(double)FN/(TP+FN);

        if ((TP + FP) != 0) {
            precision = (double) TP / (TP + FP);
        } else {
            precision = 0;
        }
        if ((TP + FN) != 0) {
            recall = (double) TP / (TP + FN);

        } else {
            recall = 0;
        }

        if ((precision + recall) != 0) {
            return (double) 2 * precision * recall / (precision + recall);
        }
        else  {
            return 0;
        }
    }

    /**
     * preprocess the patents;
     */
    protected ArrayList<patent> preprocess(ArrayList<patent> patents) {
        double start=System.currentTimeMillis();
        patentPreprocessingTF preprocess = new patentPreprocessingTF(patents);

        preprocess.setLanguage(LanguageCode.ENGLISH);
        preprocess.preprocess();
        double end=System.currentTimeMillis();
        System.out.println("Preprocessing Time"+(end-start));

        return patents;
    }

/*

    public void wrongPatents(){
        ArrayList<Integer> wrongIDs=new ArrayList<>();
        ArrayList<patent> training=new ArrayList<>();
        ArrayList<patent> testing=new ArrayList<>();
        ArrayList<String> trainingIDs=new ArrayList<>();
        ArrayList<String> testingIDs=new ArrayList<>();

        for(int i:this.wrongIndex) {
            wrongIDs.add(i);
        }

        for(int i=0;i<this.patents.size();i++) {
            if (wrongIDs.contains(i)) {
                testing.add(patents.get(i));
                testingIDs.add(patentsID.get(i));
            } else {
                training.add(patents.get(i));
                trainingIDs.add(patentsID.get(i));
            }
        }

        weightlearning.initilize(training,trainingIDs);

        CosDistance d=(CosDistance)weightlearning.estimateDistanceFunction();

        logger.info(d);
        logger.info("Training Data Size:"+training.size());
        logger.info("Testing Data Size:"+testing.size());

        double threshold=weightlearning.getThreshold();

        clusteirngMethod.ininitialize(testing);


        //((HierClusteringPatents)clusteirngMethod).setEps(threshold*di);

        clusteirngMethod.setThreshold(threshold);

        logger.warn(threshold);

        clusteirngMethod.Cluster(d);

        logger.error(clusteirngMethod);

        d.show=false;

        //logger.error(d.distance(patents.get(232),patents.get(239)));

        //logger.error(d.compareAssignee(patents.get(232).getAssignee(),patents.get(239).getAssignee(),patents.get(232).getAsgNum(),patents.get(239).getAsgNum()));

        //logger.error(d.compareLocation(patents.get(232).getCountry(),patents.get(232).getLat(),patents.get(232).getLng(),patents.get(239).getCountry(),patents.get(239).getLat(),patents.get(239).getLng()));

        double FMeasurement = getFScoreofClustering(clusteirngMethod.getClustersIndex(), testingIDs);

        logger.warn("F Measurement:" + FMeasurement);


    }
*/


}
