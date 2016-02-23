package Evaluation;

import base.ProgressBar;
import base.pair;
import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jblas.DoubleMatrix;
import preprocessing.IniFile;

import java.util.ArrayList;

/**
 * Created by leisun on 15/12/28.
 */

/**
 * contains methods to generate similarity matrices and target value vectors for input to Logistic Regression
 */
public class trainingDataMatrix {

    private ArrayList<patent> patents;
    private ArrayList<String> patentsID;
    private static Logger logger= LogManager.getLogger(trainingDataMatrix.class.getName());
    boolean infoShow=true;
    IniFile iniFile=new IniFile();
    private ArrayList<pair<int[],Double>> lrTrainingData=new ArrayList<>();
    protected ArrayList<String> optionsName;
    protected int numberofOptions=8;
    protected ArrayList<AbstractDistance> distances;
    private pair<DoubleMatrix,DoubleMatrix> p_Matrices;

    public trainingDataMatrix(ArrayList<patent> patents,ArrayList<String> IDs,boolean infoShow ){
        this.patents=patents;
        this.patentsID=IDs;
        this.infoShow=infoShow;
        this.optionsName=iniFile.getOptionsNames();
        generateSeperatedDisFunctions();
        generateDoubleMatrix();
    }

    /**
     * getter function for <code>trainingDataMatrix</code> class
     * @return  the final Abstract Datatype containing similarity matrix and target value vector
     */
    public pair<DoubleMatrix,DoubleMatrix> getPatents_Matrices(){
        return p_Matrices;
    }

    /**
     * calls methods to generate and fill-in the training data matrices
     */
    private void generateDoubleMatrix(){

        if (infoShow) {
            logger.info("Start to generate Matrix...");
            logger.info("Patent Size: "+patents.size());
        }
        generateLRTraininngData(patents,patentsID);
        p_Matrices=this.logisticRTrainingDataGenerator();
        if(infoShow) {
            logger.info("Finish Generating...");
            logger.info("Matrix Rows: "+p_Matrices.firstarg.rows);
        }
    }

    /**
     * Generating the training data of the matrix type.
     */
    public void generateLRTraininngData(ArrayList<patent> patents,ArrayList<String> IDs) {
        /**
         * Clean the Training Data
         */
        this.lrTrainingData.clear();
        for (int i = 0; i < patents.size() - 1; i++) {
            for (int j = i + 1; j < patents.size(); j++) {
                int[] tempint = new int[2];
                tempint[0] = i;
                tempint[1] = j;
                double result;

                if (IDs.get(i).equalsIgnoreCase(IDs.get(j))) {
                    result = 1.0;
                } else {
                    result = 0.0;
                }
                this.lrTrainingData.add(new pair<>(tempint, result));
            }
        }
    }

    /** Generating the training data in the matrix form
     * @return the similarity matrix and the target value vector
     */
    public pair<DoubleMatrix,DoubleMatrix> logisticRTrainingDataGenerator() {

        double[][] similarityMatrix=new double[this.lrTrainingData.size()][numberofOptions+1];
        double[][] targetValueMatrix=new double[this.lrTrainingData.size()][1];
        if (infoShow) logger.info("Start to generate trainingData of patent-patent pair.");
        int i=0;
        double sum=0;
        for(pair<int[],Double> p:this.lrTrainingData) {
            similarityMatrix[i][0]=1.0;
            int lrTrainingDataIterator=1;
            if (infoShow)  System.out.print("\r"+ ProgressBar.barString((int)((i+1)*100/lrTrainingData.size())));
            for(int optionsSize=0;optionsSize<optionsName.size();optionsSize++) {
                if (iniFile.getOptionValue(optionsName.get(optionsSize))) {
                    /*
                    computes the similarity for each pair of patents using the options and weights set in the corresponding instance of CosDistance
                     */
                    similarityMatrix[i][lrTrainingDataIterator]=distances.get(optionsSize).distance(patents.get(p.firstarg[0]), patents.get(p.firstarg[1]));
                    lrTrainingDataIterator++;
                }
            }
            targetValueMatrix[i][0]=p.secondarg;
            i++;
        }

        lrTrainingData.clear();
        DoubleMatrix X=new DoubleMatrix(similarityMatrix);      //X is the similarity matrix and carries same meaning as in the equation for Logistic Regression
        DoubleMatrix Y=new DoubleMatrix(targetValueMatrix);     //Y is the output matrix and carries same meaning as in the equation for Logistic Regression
        if (infoShow) System.out.println();
        return new pair<>(X, Y);
    }

    /**
     * Generate a distance function based on a arraylist of weights and a arraylist of index
     * sets boolean <code>optionValues</code> and weights matrices in an instance of <code>CosDistance</code> to compute the actual similarity/distance measure
     * @param attrIndex distance function index
     * @param weights distance function weights
     * @return the generated distance function
     */
    public CosDistance generateDistanceFunction(ArrayList<Integer> attrIndex, ArrayList<Double> weights) {
        CosDistance cosDistance=new CosDistance();
        if (attrIndex!=null) {
            boolean[] optionValues=new  boolean[this.iniFile.getOptionsNames().size()];
            for(int i=0;i<this.iniFile.getOptionsNames().size();i++) {
                if (attrIndex.contains(i)) {
                    optionValues[i]=true;
                } else {
                    optionValues[i]=false;
                }
            }
            cosDistance.setOptions(optionValues);
        }
        if (weights!=null&&weights.size()>=this.iniFile.getOptionsNames().size()) {
            double[] weightValues=new double[this.iniFile.getOptionsNames().size()];
            for(int i=0;i<this.iniFile.getOptionsNames().size();i++) {
                weightValues[i]=weights.get(i);
            }
            cosDistance.setWeights(weightValues);
        }
        return cosDistance;
    }

    /**
     * generate all the separated distance functions based on the options needed
     * sets values in the <code>distances</code> instance of CosDistance class
     */
    public void generateSeperatedDisFunctions(){

        this.distances=new ArrayList<>();
        int numOfOptions=0;

        for(int i=0;i<optionsName.size();i++) {
            ArrayList<Integer> optionsArray = new ArrayList<>();
            optionsArray.add(i);
            distances.add(this.generateDistanceFunction(optionsArray, null));
            if (iniFile.getOptionValue(optionsName.get(i))) numOfOptions++;
        }
        this.numberofOptions=numOfOptions;
    }
}
