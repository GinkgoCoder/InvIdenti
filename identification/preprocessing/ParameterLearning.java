package preprocessing;

import base.patent;
import clustering.distancefunction.AbstractDistance;
import clustering.distancefunction.CosDistance;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.LanguageCode;

import java.util.ArrayList;

/**
 * Created by leisun on 15/11/3
 *Used as a base class for different weight learning methods
 * Contains methods to preprocess the supplied patents and generate separate distance functions for them as per the options
 */
public abstract class ParameterLearning {

    public abstract AbstractDistance estimateDistanceFunction();

    protected static Logger logger = LogManager.getLogger(ParameterLearning.class.getName());
    protected ArrayList<patent> patents;
    protected ArrayList<String> patentsID;
    protected ArrayList<String> optionsName;
    protected int numberofOptions = 8;
    protected boolean initialization = false;
    double threshold = -Double.MAX_VALUE;

    protected ArrayList<AbstractDistance> distances;
    IniFile ini;

    public ParameterLearning() {
        ini = new IniFile();
        this.optionsName = ini.getOptionsNames();
        generateSeperatedDisFunctions();
    }

    public double getThreshold() {
        return this.threshold;
    }
    public void setInitialization(boolean var) {
        initialization=var;
    }

    /**
     * Initialize the Parameter Learning with arraylist of patents and arraylist of patentsID
     *@param patents   arraylist of patents
     * @param patentsID arraylist of patentsID
     */
    //note:typo in method name and boolean variable name
    public void initilize(ArrayList<patent> patents, ArrayList<String> patentsID, boolean initilization) {
        this.patents = patents;
        this.patentsID = patentsID;
        if (!initilization) {
            preprocess();
        }
        this.initialization = true;
    }

    /**
     * calls the <code>patentProcessingTF</code> to preprocess the patents;
     */
    protected void preprocess() {
        double start=System.currentTimeMillis();
        patentPreprocessingTF preprocess = new patentPreprocessingTF(this.patents);
        preprocess.setLanguage(LanguageCode.ENGLISH);
        preprocess.preprocess();
        this.patents = preprocess.getPatents();
        double end=System.currentTimeMillis();
        System.out.println("Preprocessing Time"+(end-start));
    }

    /**
     * Generate a distance function based on a arraylist of weights and a arraylist of index
     * @param attrIndex distance function index
     * @param weights distance function weights
     * @return the generated distance function
     */
    public CosDistance generateDistanceFunction(ArrayList<Integer> attrIndex,ArrayList<Double> weights) {
        CosDistance cosDistance=new CosDistance();
        if (attrIndex!=null) {
            boolean[] optionTrue=new  boolean[this.ini.getOptionsNames().size()];
            for(int i=0;i<this.ini.getOptionsNames().size();i++) {
                if (attrIndex.contains(i)) {
                    optionTrue[i]=true;
                }
                else {
                    optionTrue[i]=false;
                }
            }
            cosDistance.setOptions(optionTrue);
        }
        if (weights!=null&&weights.size()>=this.ini.getOptionsNames().size()) {
            double[] optionValues=new double[this.ini.getOptionsNames().size()];
            for(int i=0;i<this.ini.getOptionsNames().size();i++) {
                optionValues[i]=weights.get(i);
            }
            cosDistance.setWeights(optionValues);
        }
        return cosDistance;
    }

    /**
     * generate all the separated distance functions based on the options needed
     */
    public void generateSeperatedDisFunctions(){
        ArrayList<String> optionsName=ini.getOptionsNames();
        this.distances=new ArrayList<>();
        int var0=0;

        for(int i=0;i<optionsName.size();i++) {
            ArrayList<Integer> var1 = new ArrayList<>();
            var1.add(i);
            //supplies the attribute index and calculates Distance Function for each and stores in distances ArrayList
            distances.add(this.generateDistanceFunction(var1, null));
            if (ini.getOptionValue(optionsName.get(i))) var0++;
        }
        this.numberofOptions=var0;
    }
}
