package Evaluation;

import base.pair;
import base.patent;
import clustering.Dbscan.DBScanClusteringPatents;
import clustering.SimMatrix;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import clustering.hierarchy.HierClusteringPatents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.LanguageCode;
import preprocessing.IniFile;
import preprocessing.LRWithBoldDriver;
import preprocessing.patentPreprocessingTF;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by leisun on 15/11/15.
 */
public class Main {

    ArrayList<Double> lumpings_db=new ArrayList<>();
    ArrayList<Double> splittings_db=new ArrayList<>();
    ArrayList<Double> lumpings_hi=new ArrayList<>();
    ArrayList<Double> splittings_hi=new ArrayList<>();

    ArrayList<Integer> testingShuffleIndex;

    public double suml=0;
    public double sums=0;
    private static Logger logger= LogManager.getLogger(Main.class.getName());

    IniFile ini=new IniFile();
    String trainingPath ="/Users/sanchitalekh/Desktop/ThesisData/ES/training.db";
    String testingPath="/Users/sanchitalekh/Desktop/ThesisData/ES/testing.db";
    String infoPath="/Users/sanchitalekh/Desktop/ThesisData/ES/PatTest.sqlite";
    pair<ArrayList<patent>,ArrayList<String>> training;
    pair<ArrayList<patent>,ArrayList<String>> testing;


    public Main(int num){

        training=new patentsDataset(trainingPath,infoPath,ini.getTextPath(),2000,"Benchmark").getPatents();
        System.out.println(training.firstarg.size());
        subsetofTrainingwithRandomly(num);
        testing=new patentsDataset(testingPath,infoPath,"/Users/sanchitalekh/Desktop/ThesisData/ES/PatentsText",2000,"Benchmark").getPatents();
        System.out.println(testing.firstarg.size());
        subsetofTestingwithRandomly(num);
    }

    /**
     * Creates a Random subset of the Testing Data to test the model
     * @param num Number of records to test
     */
    public void subsetofTestingwithRandomly(int num){
        ArrayList<Integer> shuffleIndex = new ArrayList<>();
        for (int i = 0; i < testing.firstarg.size(); i++) {
            shuffleIndex.add(i);
        }
        ArrayList<patent> patents=new ArrayList<>();
        ArrayList<String> patentsID=new ArrayList<>();
        Collections.shuffle(shuffleIndex);
        if (num>testing.firstarg.size()) num=testing.firstarg.size();
        for (int i = 0; i < num; i++) {
            patents.add(testing.firstarg.get(shuffleIndex.get(i)));
            patentsID.add(testing.secondarg.get(shuffleIndex.get(i)));
        }
        testing=new pair<>(patents,patentsID);

       ArrayList<Integer> var0=new ArrayList<>();
        for(int i=0;i<num;i++) {
            var0.add(shuffleIndex.get(i));
        }
        this.testingShuffleIndex=var0;
    }

    public double test(){
        return 0;
        //return testingWithTraining(training,null);
    }

    /**
     * creates a Random subset of the Training Data to train the model
     * @param num Number of records to train
     */
    public void subsetofTrainingwithRandomly(int num){

        ArrayList<Integer> shuffleIndex = new ArrayList<>();
        for (int i = 0; i < training.firstarg.size(); i++) {
            shuffleIndex.add(i);
        }
        ArrayList<patent> patents=new ArrayList<>();
        ArrayList<String> patentsID=new ArrayList<>();
        Collections.shuffle(shuffleIndex);
        if (num>training.firstarg.size()) num=training.firstarg.size();
        for (int i = 0; i < num; i++) {
            patents.add(training.firstarg.get(shuffleIndex.get(i)));
            patentsID.add(training.secondarg.get(shuffleIndex.get(i)));
        }
        training=new pair<>(patents,patentsID);
    }

    /**
     * Build Similarity Matrix
     */
    public void buildsimMatrix()
    {
        double t1=System.currentTimeMillis();
        this.testing.firstarg=preprocess(this.testing.firstarg);
        SimMatrix sim=new SimMatrix(this.testing.firstarg,new CosDistance(),"distanceMatrix1000.txt");
        double t2=System.currentTimeMillis();
        System.out.println("Matrix Time:"+(t2-t1));
        //sim.storeMatrix("distanceMatrix5000.txt");
    }


    /**
     * calls instance of <code>patentPreprocessingTF</code> to preprocess the patents;
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

    public void testingWithTraining(){

        //Training var2=new Training(training.firstarg,training.secondarg,new LRWeightLearning());
        //pair<AbstractDistance,Double> var3=var2.estimateParameter();
        //System.out.println(var3.firstarg);
        //System.out.println(var3.secondarg);

        System.out.println("Testing:"+testing.firstarg.size());
        Evaluation evaluation=new Evaluation(testing.firstarg,testing.secondarg);

        double F_hier,F_db,lumping_hier,lumping_db,splitting_hier,splitting_db;
        double t1=System.currentTimeMillis();
        ArrayList<Double> fs=new ArrayList<>();
        ArrayList<Double> ls=new ArrayList<>();
        ArrayList<Double> ss=new ArrayList<>();
        DBScanClusteringPatents db=new DBScanClusteringPatents(testingShuffleIndex);
        db.setMinPts(1);
        evaluation.evaluate(new CosDistance(),20.93,db);
        logger.warn("Lumping Error: "+evaluation.lumping);
        logger.warn("Splitting Error: "+evaluation.splitting);

        //some old unit-test code
        /*
        for(int i=0;i<31;i++) {
            db.setMinPts(i+1);
            F_db=e.evaluate(new CosDistance(),20.93,db);
            fs.add(F_db);
            ls.add(e.lumping);
            ss.add(e.splitting);
        }

        for(int i=0;i<31;i++) {
            System.out.println((i+21)+" "+fs.get(i)+" "+ls.get(i)+" "+ss.get(i));
        }

       double t2=System.currentTimeMillis();
       System.out.println("DBScan Clustering time:"+(t2-t1));
       lumping_db=e.lumping;
       splitting_db=e.splitting;

       t1=System.currentTimeMillis();
       F_hier=e.evaluate(new CosDistance(),20.93,new HierClusteringPatents(testingShuffleIndex));lumping_hier=e.lumping;
       splitting_hier=e.splitting;

       t2= System.currentTimeMillis();
       logger.warn(lumping_hier+" "+splitting_hier);
       System.out.println("Hierchical Clustering time:"+(t2-t1));


       String temp="Patent Size: "+testing.firstarg.size()+"\n";
       temp+="Hierarchical Clustering: "+F_hier+" "+splitting_hier+" "+lumping_hier+"\n";
       temp+="DBScan: "+F_db+" "+splitting_db+" "+lumping_db+"\n";
       storeText("CluteringWithTest.txt",true,temp);
      */

    }


    /**
     * Training the model and test the performance on a test dataset.
     * @param training training dataset
     * @param testing testing dataset
     * @return
     */
    public pair<Double,Double> testingWithTraining(pair<ArrayList<patent>,ArrayList<String>> training,pair<ArrayList<patent>,ArrayList<String>> testing) {

        /**
         * Training via Logistic Regression with Bold Driver method
         */
        Training var4=new Training(training.firstarg,training.secondarg,new LRWithBoldDriver());
        pair<AbstractDistance,Double> var5=var4.estimateParameter();
        storeText("ClusteringDistance.txt",true,var5.firstarg.getWeights()+" "+var5.secondarg+"\n");

        logger.warn(var5.firstarg);
        logger.warn("Threshold: " + var5.secondarg );

        Evaluation e=new Evaluation(testing.firstarg,testing.secondarg);
        double FMeasure_DB=e.evaluate(var5.firstarg,var5.secondarg,new DBScanClusteringPatents(testingShuffleIndex));
        this.lumpings_db.add(e.lumping);
        this.splittings_db.add(e.splitting);

        double FMeasure_Hi=e.evaluate(var5.firstarg,var5.secondarg,new HierClusteringPatents(testingShuffleIndex));
        this.lumpings_hi.add(e.lumping);
        this.splittings_hi.add(e.splitting);

        return new pair<>(FMeasure_DB,FMeasure_Hi);
    }

    /**
     * Cross validation
     * @param k K-fold
     * @return the average Fmeasure
     */
    public double crossValidate(int k){

        ArrayList<patent> patents=new ArrayList<>();
        ArrayList<String> patentsID=new ArrayList<>();
        Boolean shuffle=true;
        ArrayList<Integer> shuffleIndex;

        shuffleIndex = new ArrayList<>();
        for (int i = 0; i < training.firstarg.size(); i++) {
            shuffleIndex.add(i);
        }

        //Shuffle the patents list
        if(shuffle) {
           Collections.shuffle(shuffleIndex);
            for (int i = 0; i < training.firstarg.size(); i++) {
                patents.add(training.firstarg.get(shuffleIndex.get(i)));
                patentsID.add(training.secondarg.get(shuffleIndex.get(i)));
            }
        }else {
            patents=training.firstarg;
            patentsID=training.secondarg;

        }

        storeText("ClusteringDistance.txt",true,"Patents:"+patents.size()+"\n");

        int numberOfTesting=patents.size()/k;
        ArrayList<Double> FMeasure_db=new ArrayList<>();
        ArrayList<Double> FMeasure_hi=new ArrayList<>();

        for(int i=0;i<k;i++){
            logger.info("The "+(i+1)+"th cross validation for "+patents.size());
            int start=i*numberOfTesting;
            int end=start+numberOfTesting;
            if (end>patents.size()-1) end=patents.size()-1;
            pair<pair<ArrayList<patent>,ArrayList<String>>,pair<ArrayList<patent>,ArrayList<String>>> var0=this.getTrainingandTestingPatents(start,end,patents,patentsID,shuffleIndex);
            pair<Double,Double> fs=testingWithTraining(var0.firstarg,var0.secondarg);
            FMeasure_db.add(fs.firstarg);
            FMeasure_hi.add(fs.secondarg);
        }

        String result="Patent Number: "+patents.size()+"\n"+"DBScan:\n";
        logger.info("");
        double sum=0;
        for(double d:FMeasure_db) {
            result+=d+" ";
            System.out.print(d+",");
            sum+=d;
        }
        logger.info("");
        logger.error("F measure for the DBScan:"+ sum/k);
        double F1=sum/k;
        result+=F1+"\n";
        sum=0;
        for(double d:lumpings_db) {
            result+=d+" ";
            sum+=d;
        }
        suml=sum/k;
        logger.error("Average Lumping:"+sum/k);
        result+=suml+"\n";
        sum=0;
        for(double d:splittings_db) {
            result+=d+" ";
            sum+=d;
        }
        sums=sum/k;
        result+=sums+"\n";
        logger.error("Average splitting:"+sum/k);

        result+="Hierarchical Clustering:\n";
        sum=0;
        for(double d:FMeasure_hi) {
            result+=d+" ";
            System.out.print(d+",");
            sum+=d;
        }
        logger.info("");
        logger.error("F measure for the Hierarchical Clustering:"+ sum/k);
        F1=sum/k;
        result+=F1+"\n";
        sum=0;
        for(double d:lumpings_db) {
            result+=d+" ";
            sum+=d;
        }
        suml=sum/k;
        logger.error("Average Lumping:"+sum/k);
        result+=suml+"\n";
        sum=0;
        for(double d:splittings_db) {
            result+=d+" ";
            sum+=d;
        }
        sums=sum/k;
        result+=sums+"\n";
        storeText("Clustering.txt",true,result);
        logger.error("Average splitting:"+sum/k);

        return (F1);
    }



    /**
     * Store the text into a text file
     * @param path text path
     * @param follow rewrite option
     * @param str content to write
     */

    public void storeText(String path,boolean follow,String str){
        if (follow) {
            try {
                FileWriter writer=new FileWriter(path,follow);
                writer.write(str);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileWriter w=new FileWriter(path,follow);
                w.write(str);
                w.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Seperate the patents set into training set and testing set
     * @param start start index
     * @param end end index
     * @param patents patents list
     * @param patentsID patents id list
     * @param shuffleIndex random index list
     * @return training set and testing set
     */
    public pair<pair<ArrayList<patent>,ArrayList<String>>,pair<ArrayList<patent>,ArrayList<String>>> getTrainingandTestingPatents(int start, int end, ArrayList<patent> patents, ArrayList<String> patentsID, ArrayList<Integer> shuffleIndex) {

        ArrayList<patent> training=new ArrayList<>();
        ArrayList<patent> testing=new ArrayList<>();
        ArrayList<String> trainingIDs=new ArrayList<>();
        ArrayList<String> testingIDs=new ArrayList<>();

        for(int i=0;i<patents.size();i++) {
            if (i>=start&&i<end) {
                testing.add(patents.get(i));
                System.out.print(shuffleIndex.get(i)+" ");
                testingIDs.add(patentsID.get(i));
            } else {
                training.add(patents.get(i));
                trainingIDs.add(patentsID.get(i));
            }
        }
        logger.info("");
        pair<ArrayList<patent>,ArrayList<String>> train = new pair<>(training,trainingIDs);
        pair<ArrayList<patent>,ArrayList<String>> test = new pair<>(testing,testingIDs);
        return new pair<>(train,test);

    }


    public static void main(String[] args) {
        long begintime=System.currentTimeMillis();

        //initialize object of main class with number of dataset records
        Main temp=new Main(1000);
        //temp.crossValidate(5);
        temp.testingWithTraining();

        //some old testing code
        /*
       temp.buildsimMatrix();
        for(int i=2000;i<=5000;i+=1000) {

       logger.warn("Size: "+i);
            Main temp=new Main(i);
            temp.crossValidate(5);
        }
        */
        long endtime=System.currentTimeMillis();
        System.out.println("Time Cost:"+(endtime-begintime));
    }
}
