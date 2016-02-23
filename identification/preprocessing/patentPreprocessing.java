package preprocessing;

import base.patent;
import com.carrotsearch.hppc.IntArrayList;
import com.carrotsearch.hppc.IntIntHashMap;
import com.carrotsearch.hppc.cursors.IntIntCursor;
import org.apache.mahout.math.matrix.DoubleMatrix2D;
import org.carrot2.core.Document;
import org.carrot2.core.LanguageCode;
import org.carrot2.text.preprocessing.LabelFormatter;
import org.carrot2.text.preprocessing.PreprocessingContext;
import org.carrot2.text.preprocessing.pipeline.BasicPreprocessingPipeline;
import org.carrot2.text.preprocessing.pipeline.IPreprocessingPipeline;
import org.carrot2.text.vsm.ReducedVectorSpaceModelContext;
import org.carrot2.text.vsm.TermDocumentMatrixBuilder;
import org.carrot2.text.vsm.TermDocumentMatrixReducer;
import org.carrot2.text.vsm.VectorSpaceModelContext;


import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by sunlei on 15/9/7.
 */
public class patentPreprocessing {
    ArrayList<Document> docs = new ArrayList<>();
    ArrayList<patent> patents = new ArrayList<>();
    LanguageCode language = LanguageCode.ENGLISH;
    public int clusterCount = 15;
    public boolean useDimensionalityReduction = false;

    public IPreprocessingPipeline preprocessingPipeline = new BasicPreprocessingPipeline();
    public final TermDocumentMatrixBuilder matrixBuilder = new TermDocumentMatrixBuilder();

    public final TermDocumentMatrixReducer matrixReducer = new TermDocumentMatrixReducer();
    public final LabelFormatter labelFormatter = new LabelFormatter();

    public patentPreprocessing(ArrayList<patent> Pts) {
        this.patents = Pts;
        /**
         * Set the matrix size to build the term frequency.
         */
        this.matrixBuilder.maximumMatrixSize=Pts.size()*1000;
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

  //        if (str.equalsIgnoreCase("FullText")) temp=p.getAbs() + " " + p.getClaims() + " " + p.getDescription();
            if (str.equalsIgnoreCase("Abstract")) temp=p.getAbs();
            if (str.equalsIgnoreCase("Claims"))   temp=p.getClaims();
            if (str.equalsIgnoreCase("Description")) temp=p.getDescription();
            if (str.equalsIgnoreCase("Title")) temp=p.getTitle();

            docs.add(new Document("",temp));
        }


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
            this.matrixBuilder.buildTermDocumentMatrix(var17);
            this.matrixBuilder.buildTermPhraseMatrix(var17);
            IntIntHashMap rowToStemIndex = new IntIntHashMap();
            Iterator tdMatrix = var17.stemToRowIndex.iterator();

            while (tdMatrix.hasNext()) {
                IntIntCursor columns = (IntIntCursor) tdMatrix.next();
                rowToStemIndex.put(columns.value, columns.key);
            }

            DoubleMatrix2D var19;
            if (this.useDimensionalityReduction) {
                this.matrixReducer.reduce(var18, this.clusterCount * 2);
                var19 = var18.coefficientMatrix.viewDice();
            } else {
                var19 = var17.termDocumentMatrix;
            }


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
    }

    }

