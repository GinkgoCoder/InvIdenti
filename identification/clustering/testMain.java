package clustering;


import base.pair;
import info.debatty.java.stringsimilarity.*;
import com.carrotsearch.hppc.IntArrayList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


/**
 * Created by leisun on 15/9/8.
 */
public class testMain
{
    private static Logger l= LogManager.getLogger(testMain.class.getName());
    public static void main(String[] args) {
    Levenshtein method=new Levenshtein();

        System.out.println(method.distance("CLASSIFICATION","CLUSTERING"));

        String str="CLASSIFICATION";
        String str2="CLUSTERING";

        for(int i=0;i<11;i++) {
            for(int j=0;j<15;j++) {
                System.out.print(method.distance(str2.substring(0,i),str.substring(0,j))+" ");
            }
            System.out.println();
        }
//
//
//
//
// SqlitePatents s=new SqlitePatents("/Users/sunlei/Desktop/ThesisData/PatentData/PatTest.sqlite");

/*
        double[][] x={{1,0,1,0,0,0},{0,1,0,0,0,0},{1,1,0,0,0,0},{1,0,0,1,1,0},{0,0,0,1,0,1}};

        Array2DRowRealMatrix X=new Array2DRowRealMatrix(x);

        SingularValueDecomposition de=new SingularValueDecomposition(X);


        Array2DRowRealMatrix U=(Array2DRowRealMatrix) de.getU();
        Array2DRowRealMatrix T=(Array2DRowRealMatrix) de.getVT();

        double[] singularValue=de.getSingularValues();


        for(int i=0;i<singularValue.length;i++) {
            System.out.println(singularValue[i]);
        }

        int[] rows={1,2};



        System.exit(1);

        /**
         * Test one patent
         */
        /*
        String number="05868938";
        USPTOSearch si=new USPTOSearch(number);
        patent p=new patent("",si.getAbs(),si.getClaims(),si.getDescription(),si.getTitle(),"","","");
        s.storeText("/Users/leisun/Desktop/PatentData/PatentText/"+number+"/"+"Abstract.txt",p.getAbs());
        s.storeText("/Users/leisun/Desktop/PatentData/PatentText/"+number+"/"+"Claims.txt",p.getClaims());
        s.storeText("/Users/leisun/Desktop/PatentData/PatentText/"+number+"/"+"Description.txt",p.getDescription());
        s.storeText("/Users/leisun/Desktop/PatentData/PatentText/"+number+"/"+"Title.txt",p.getTitle());
        */

        /**
         * Generate patents arraylist
         */

        //ArrayList<patent> p=s.getNumPatents(100, "invpat");
        // ArrayList<patent> p=s.getPatentsTextFromSQL(50, "/Users/sunlei/Desktop/ThesisData/PatentData/text/", "invpat");
        //s.writeToTexts("/Users/leisun/Desktop/PatentData/PatentText/");
        //s.readText("/Users/leisun/Desktop/PatentData/PatentText/04105099/Abstract.txt");
        //l.info(p.get(1).getClaims());
        /**
         * Clustering process
         */
        //boolean[] var_b={false,false,false,false,false,true};
        /*
        HierClusteringPatents hi=new HierClusteringPatents(p);
        hi.setEps(0.003726864190953778);
        double[] weight={1.0,
                1.8556438515929588E-4,
                0.002305386817577655,
                0.004262380069952872,
                0.002160624848105635,
                0.0013890053935176059};
        CosDistance var0=new CosDistance();
        var0.setWeights(weight);
        //var0.setOptions(var_b);
        l.error(var0.distance(p.get(14),p.get(15)));
        hi.Cluster(var0);
        //System.out.println(hi.toString());
        l.info(hi.toString());
*/


        // NormalizedLevenshtein l1 = new NormalizedLevenshtein();

        /**
         TrainingDataGenerator dg=new TrainingDataGenerator();
         dg.setSize(300);
         dg.buildData();
         **/

        /*
        patent p1=new patent("123","123","123","123","123","123","123","123");
        patent p2=new patent("123","123","123","123","123","123","123","123");
        CosDistance c=new CosDistance();
        l.error(c.distance(p1,p2));
*/
/*
        pair<ArrayList<String>,double[]> temp= TFcalculation("a,app,apple,invent,invention,invent,a,a,app,app",0.2);

        for(int i=0;i<temp.secondarg.length;i++) {
            System.out.println(temp.firstarg.get(i)+" "+temp.secondarg[i]);
        }

        /*
        double[][] var1=new double[temp.secondarg.length][2];
        int i=0;
        for(double var2:temp.secondarg) {
            var1[i][0]=var2;
            i++;
        }
        DenseDoubleMatrix2D f=new DenseDoubleMatrix2D(var1);
        */
 /*
        pair<ArrayList<String>,double[]>temp2=TFcalculation("a,app,apple,b",0.2);
        for(int i=0;i<temp2.secondarg.length;i++) {
            System.out.println(temp2.firstarg.get(i)+" "+temp2.secondarg[i]);
        }

        /*
        double[][] var2=new double[temp.secondarg.length][2];
        i=0;
        for(double var3:temp2.secondarg) {
            var2[i][0]=var3;
            i++;
        }
        DenseDoubleMatrix2D s=new DenseDoubleMatrix2D(var2);
        System.out.println(CosDistance.cosDistance(f,s,temp.firstarg,temp2.firstarg));
    */
    }





    public static pair<ArrayList<String>,double[]> TFcalculation(String var0,double threshold) {

        IPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();
        ArrayList<Document> docs = new ArrayList<>();
        docs.add(new Document(" ", var0));
        PreprocessingContext preprocessingContext = preprocessingPipeline.preprocess(docs, (String) null, LanguageCode.ENGLISH);
        int[] stemsMfow = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
        short[] wordsType = preprocessingContext.allWords.type;
        IntArrayList featureIndices = new IntArrayList(stemsMfow.length);
        for (int vsmContext = 0; vsmContext < stemsMfow.length; ++vsmContext) {
            short reducedVsmContext = wordsType[stemsMfow[vsmContext]];
            if ((reducedVsmContext & 12290) == 0) {
                featureIndices.add(stemsMfow[vsmContext]);
            }
        }

        double[] tfMatrix = new double[preprocessingContext.allStems.tf.length];

        for (int j = 0; j < preprocessingContext.allStems.tf.length; j++) {
            tfMatrix[j]=0.0;
        }


        for (int j = 0; j < preprocessingContext.allStems.tfByDocument.length; j ++) {
            tfMatrix[j]=preprocessingContext.allStems.tfByDocument[j][1];
        }

        double sum=0;

        for(double var1:tfMatrix) {
            sum+=var1;
        }

        for(int vari=0;vari<tfMatrix.length;vari++) {
            tfMatrix[vari]/=sum;
        }

        ArrayList<Double> var1=new ArrayList<>();

        for(double vari:tfMatrix) {
            var1.add(vari);
        }

        ArrayList<String> stems=new ArrayList<>();
        for(int vari=0;vari<preprocessingContext.allStems.image.length;vari++) {

                String varTemp=String.copyValueOf(preprocessingContext.allStems.image[vari]);
                stems.add(varTemp);

        }

        HashMap<Double,ArrayList<String>> stemsWithTF=new HashMap<>();

        for(int i=0;i<tfMatrix.length;i++) {
            if(stemsWithTF.containsKey(tfMatrix[i])) {
                stemsWithTF.get(tfMatrix[i]).add(stems.get(i));
            }
            else {
                ArrayList<String> temp=new ArrayList<>();
                temp.add(stems.get(i));
                stemsWithTF.put(tfMatrix[i],temp);
            }
        }

        Collections.sort(var1);

        int numberofstems= (int) (stems.size()*threshold)+1;

        ArrayList<Double> var3=new ArrayList<>();
        ArrayList<String> var4=new ArrayList<>();
        int i=0;
        for(int j=var1.size()-1;j>=0;j--) {
            double var5=var1.get(j);
            for(String str:stemsWithTF.get(var5)) {
                var4.add(str);
                var3.add(var5);
                i++;
            }
            if(i>=numberofstems) break;
        }


        double[] var6=new double[var3.size()];
        for(int j=0;j<var3.size();j++) {
            var6[j]=var3.get(j);
        }

        return new pair<>(var4,var6);
    }



}

