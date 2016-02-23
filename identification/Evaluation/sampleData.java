package Evaluation;

import base.pair;
import base.patent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.LanguageCode;
import org.jblas.DoubleMatrix;
import preprocessing.IniFile;
import preprocessing.patentPreprocessingTF;

import java.util.ArrayList;

/**
 * Created by sunlei on 15/12/25.
 */
public class sampleData {
    IniFile iniFile=new IniFile();
    String sampleDataPath;
    String textPath;
    String infoPath;

    private static Logger logger= LogManager.getLogger(sampleData.class.getName());
    private pair<ArrayList<patent>,ArrayList<String>> training;
    private pair<ArrayList<patent>,ArrayList<String>> validation;

    protected ArrayList<String> optionsName;
    protected int numberofOptions;


    protected pair<DoubleMatrix,DoubleMatrix> training_matrices;
    protected pair<DoubleMatrix,DoubleMatrix> validation_matrices;

    pair<ArrayList<patent>,ArrayList<String>> sampleData;
    public sampleData(int numofData) {
        sampleDataPath=iniFile.getSamplePath();
        infoPath=iniFile.getInfoDataPath();
        textPath=iniFile.getTextPath();
        optionsName=iniFile.getOptionsNames();



        ArrayList<patent> training_p=new ArrayList<>();
        ArrayList<String> training_ID=new ArrayList<>();
        ArrayList<patent> validation_p=new ArrayList<>();
        ArrayList<String> validation_ID=new ArrayList<>();

        logger.info("Start to import the sample data");
        sampleData=new patentsDataset(sampleDataPath+"sample.db",infoPath,textPath,numofData,"Benchmark").getPatents();
        logger.info("Sample Data Size: "+sampleData.firstarg.size()+" Patents");
        logger.info("Separate the sample data into training data and validation data");
        for(int i=0;i<sampleData.firstarg.size();i++) {
            if (i<sampleData.firstarg.size()/2) {
                training_p.add(sampleData.firstarg.get(i));
                training_ID.add(sampleData.secondarg.get(i));
            }
            else {
                validation_ID.add(sampleData.secondarg.get(i));
                validation_p.add(sampleData.firstarg.get(i));
            }
        }

        training=new pair<>(training_p,training_ID);
        validation=new pair<>(validation_p,validation_ID);

        sampleData=null;

        logger.info("Training Data Size:"+training.firstarg.size());
        logger.info("Validation Data Size:"+validation.firstarg.size());
        preprocess();
       countOptions();



    }


    /**
     * Output a matrix
     * @param x the matrix
     * @param name the name of the matrix
     */
    public void outputMatrix(DoubleMatrix x,String name) {
        logger.error("Matrix Name:" +name);
        int var0=0;
        for (int i=0;i<x.rows;i++) {
            String temp="";
            for(int j=0;j<x.columns;j++) {

                temp+=x.get(i,j)+" ";


            }
            logger.error(temp);
        }



    }

    /**
     * preprocess the training.firstarg;
     */
    protected void preprocess() {
        double start=System.currentTimeMillis();
        patentPreprocessingTF preprocess = new patentPreprocessingTF(this.training.firstarg);

        preprocess.setLanguage(LanguageCode.ENGLISH);
        preprocess.preprocess();
        this.training.firstarg = preprocess.getPatents();
        double end=System.currentTimeMillis();
        System.out.println("Preprocessing Time"+(end-start));

    }


    /**
     * geneerate all the seperated distance functions based on the options needed
     */

    public void countOptions(){




        int var0=0;

        for(int i=0;i<optionsName.size();i++) {

            if (iniFile.getOptionValue(optionsName.get(i))) var0++;

        }

        this.numberofOptions=var0;
    }

    public void estimatelearningRate(){
       // pair<DoubleMatrix,DoubleMatrix> result=new trainingDataMatrix(training.firstarg,training.secondarg,false).getPatents_Matrices();
        new miniBatchLearningRate(this.numberofOptions,training.firstarg,training.secondarg);
    }

    public void estimateRegularizationParameter(int num) {

        new regularizationParameter(training.firstarg,training.secondarg,numberofOptions, num);
    }

    public void estimateEarlyStop() {
        earlyStop earlyStop=new earlyStop(training.firstarg,training.secondarg,this.numberofOptions);
    }

    public void estimateBatchSize(){
        BatchSize batchSize=new BatchSize(training.firstarg,training.secondarg,this.numberofOptions);
    }


    public void estimatePQ(){
       pq q=new pq(training.firstarg,training.secondarg,this.numberofOptions);
    }
    public static void main(String[] args) {
        /*sampleData l=new sampleData();
        //l.estimatelearningRate();
        l.estimateRegularizationParameter();
    */
  /*
       for(int i=0;i<10;i++) {
           SampleDataGenerator s = new SampleDataGenerator();
           sampleData l = new sampleData();
           l.estimateRegularizationParameter(i);
       }
       */
       for(int i=3000;i<=6000;i+=4000) {
           SampleDataGenerator s = new SampleDataGenerator(7000);
           sampleData l = new sampleData(4000);
           l.estimatelearningRate();
       }
    }
}
