package preprocessing;

import base.patent;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.apache.mahout.math.matrix.impl.DenseDoubleMatrix2D;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.text.vsm.*;
import org.jblas.DoubleMatrix;


import java.util.*;

/**
 * Created by sunlei on 15/9/7.
 */

/**
 *  Main <code>preprocessing</code> class for the text mining process
 *  performs stop word removal, stemming, term weighting and singular value decomposition
 */
public class patentPreprocessingTF {
    ArrayList<Document> docs = new ArrayList<>();
    ArrayList<patent> patents = new ArrayList<>();
    LanguageCode language = LanguageCode.ENGLISH;
    public int clusterCount = 15;
    public boolean useDimensionalityReduction = false;
    public IPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();
    public final TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();
    public final TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();

    private static Logger logger= LogManager.getLogger(patentPreprocessingTF.class.getName());

    public patentPreprocessingTF(ArrayList<patent> Pts) {
        this.patents = Pts;
        /**
         * Set the matrix size to build the term frequency.
         */
        this.matrixBuilder.maximumMatrixSize=Pts.size()*1000;
        this.matrixBuilder.maxWordDf=1.0;
    }

    public void setLanguage(LanguageCode code) {
        this.language = code;
    }
    public void setClusterCount(int ClusterCount) {
        this.clusterCount = ClusterCount;
    }
    public void setUseDimensionalityReduction(boolean r) {
        this.useDimensionalityReduction = r;
    }
    public ArrayList<patent> getPatents()
    {
        return this.patents;
    }

    /**
     *Note: can be greatly parallelized if each runs on own thread
     */
    public void preprocess()
    {
//      this.generateTextVector("FullText");
        this.generateTextVector("Abstract");
        this.generateTextVector("Claims");
        this.generateTextVector("Description");
        this.generateTextVector("Title");
    }

    public void generateTextVector(String str)
    {
        docs.clear();
        //Build arrayList of docs
        for (patent p : patents)
        {
            String temp=" ";
            // if (str.equalsIgnoreCase("FullText")) temp=p.getAbs() + " " + p.getClaims() + " " + p.getDescription();
            if (str.equalsIgnoreCase("Abstract")) temp=p.getAbs();
            if (str.equalsIgnoreCase("Claims"))   temp=p.getClaims();
            if (str.equalsIgnoreCase("Description")) temp=p.getDescription();
            if (str.equalsIgnoreCase("Title")) temp=p.getTitle();

            /**
             * @params title of the document
             * @params summary of the document
             */
            docs.add(new Document("",temp));
        }

        logger.info("Term Frequency Generating "+str+"...");

        PreprocessingContext preprocessingContext = this.preprocessingPipeline.preprocess(this.docs, (String) null, language);



        int[] stemsMfow = preprocessingContext.allStems.mostFrequentOriginalWordIndex;
        short[] wordsType = preprocessingContext.allWords.type;
        IntArrayList featureIndices = new IntArrayList(stemsMfow.length);

        for (int vsmContext = 0; vsmContext < stemsMfow.length; ++vsmContext) {
            short reducedVsmContext = wordsType[stemsMfow[vsmContext]];
            if ((reducedVsmContext & 12290) == 0) {
                featureIndices.add(stemsMfow[vsmContext]);
            }
        }

        preprocessingContext.allLabels.featureIndex = featureIndices.toArray();
        preprocessingContext.allLabels.firstPhraseIndex = -1;
        if (preprocessingContext.hasLabels()) {
            VectorSpaceModelContext var17 = new VectorSpaceModelContext(preprocessingContext);
            ReducedVectorSpaceModelContext var18 = new ReducedVectorSpaceModelContext(var17);

            this.matrixBuilder.termWeighting=new TfTermWeighting();
            this.matrixBuilder.buildTermDocumentMatrix(var17);
            IntIntHashMap rowToStemIndex = new IntIntHashMap();
            Iterator tdMatrix = var17.stemToRowIndex.iterator();

            while (tdMatrix.hasNext()) {
                IntIntCursor columns = (IntIntCursor) tdMatrix.next();
                rowToStemIndex.put(columns.value, columns.key);
            }

            DoubleMatrix2D var19;
            var19 = var17.termDocumentMatrix;
            var19=normalize(var19);

            /**
             * Number of rows before Singular Value Decomposition
             */
            System.out.println(var19.rows());
            logger.info("Singular Value Decomposition...");
            Array2DRowRealMatrix original=new Array2DRowRealMatrix(var19.toArray());
            SingularValueDecomposition decomposition=new SingularValueDecomposition(original);

            double[] singularvalues=decomposition.getSingularValues();
            DoubleMatrix sv=new DoubleMatrix(singularvalues);
            double sum=sv.transpose().mmul(sv).get(0);
            double sum1=0;
            int numofs=0;
            for (double d:singularvalues) {
                sum1+=d*d;
                /**
                 * Generates 300 dimensions after Singular Value Decomposition
                 */
                if (numofs>299) break;
                numofs++;
            }

            double[][] u=new double[original.getRowDimension()][numofs];


         //  System.out.println(((Array2DRowRealMatrix)decomposition.getU()).getColumnDimension());

            //note: what does this do?
            decomposition.getU().copySubMatrix(0,original.getRowDimension()-1,0,numofs-1,u);
            DoubleMatrix U1=new DoubleMatrix(u);
            DoubleMatrix M=new DoubleMatrix(original.getData());
            M=U1.transpose().mmul(M);
            var19=new DenseDoubleMatrix2D(M.toArray2());
            System.out.println(var19.rows());

            IntArrayList intA=new IntArrayList();

            for(int i=0;i<var19.columns();i++)
            {
                intA.clear();
                intA.add(i);

                //   if (str.equalsIgnoreCase("FullText")) patents.get(i).setTd(var19.viewSelection((int[]) null, intA.toArray()).copy());
                if (str.equalsIgnoreCase("Abstract")) patents.get(i).setTd_abs(var19.viewSelection((int[]) null, intA.toArray()).copy());
                if (str.equalsIgnoreCase("Claims"))  patents.get(i).setTd_claims(var19.viewSelection((int[]) null, intA.toArray()).copy());
                if (str.equalsIgnoreCase("Description")) patents.get(i).setTd_des(var19.viewSelection((int[]) null, intA.toArray()).copy());
                if (str.equalsIgnoreCase("Title")) patents.get(i).setTd_title(var19.viewSelection((int[]) null, intA.toArray()).copy());

            }


        }

        logger.info("Finish the text preprocessing...");
    }


    public DoubleMatrix2D normalize(DoubleMatrix2D var) {
        double[] sum=new double[var.columns()];
        for(int i=0;i<var.columns();i++) {
            sum[i]=var.viewColumn(i).zSum();
        }

        for(int i=0;i<var.columns();i++) {
            for(int j=0;j<var.rows();j++) {
                if(var.get(j,i)>0) {
                    var.set(j,i,var.get(j,i)/sum[i]);
                }
            }
        }
        return var;
    }

}



