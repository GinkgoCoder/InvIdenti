package clustering.distancefunction;

import base.patent;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import org.apache.logging.log4j.Logger;
import org.ini4j.Wini;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;


/**
 * Created by leisun on 15/9/28.
 *
 */
public abstract class AbstractDistance {

    protected boolean fulltextCompare=false;
    protected boolean abstractCompare=true;
    protected boolean claimsCompare=true;
    protected boolean desComapre=true;
    protected boolean assigneeCompare=true;
    protected boolean categoryCompare=true;

    protected boolean locationCompare=true;
    protected boolean coAuthorCompare=true;
    protected boolean lastNameCompare=true;
    protected boolean firstNameCompare=true;
    protected boolean titleCompare=true;
    protected boolean pCorrelation=true;

    protected double weightFullText=1.0;
    protected double weightAbstract=1.0;
    protected double weightClaims=1.0;
    protected double weightDes=1.0;
    protected double weightAssignee=1.0;
    protected double weightCategory=1.0;
    protected double weightTitle=1.0;

    protected double weightLocation=1.0;
    protected double weightCoAuthor=1.0;
    public double weightLastName=1.0;
    public double weightFirstName=1.0;


    public boolean show=false;

    protected int numofOptions=10;

    protected String distanceType="AbstractDistance";

    private static Logger logger= org.apache.logging.log4j.LogManager.getLogger(AbstractDistance.class.getName());

    public AbstractDistance(){
        this.initialOption();

    }

    /**
     * Calculate the distance as the similarity between two patents
     * @param fisrt first patent
     * @param second second patent
     * @return the similarity of the two patents
     */
    public abstract double distance(patent fisrt,patent second);

    /**
     * Initialize the options based on the initial file
     */
    public void initialOption() {
        try {
            Wini initalFile=new Wini(new File("invidenti.ini"));


            this.fulltextCompare=initalFile.get("DistanceOption","FullTextCompare").equalsIgnoreCase("true");
            this.categoryCompare=initalFile.get("DistanceOption","CategoryCompare").equalsIgnoreCase("true");
            this.assigneeCompare=initalFile.get("DistanceOption","AssigneeCompare").equalsIgnoreCase("true");
            this.abstractCompare=initalFile.get("DistanceOption","AbstractCompare").equalsIgnoreCase("true");
            this.claimsCompare=initalFile.get("DistanceOption","ClaimsCompare").equalsIgnoreCase("true");
            this.desComapre=initalFile.get("DistanceOption","DescriptionCompare").equalsIgnoreCase("true");

            this.coAuthorCompare=initalFile.get("DistanceOption","CoAuthorCompare").equalsIgnoreCase("true");
            this.locationCompare=initalFile.get("DistanceOption","LocationCompare").equalsIgnoreCase("true");
            this.lastNameCompare=initalFile.get("DistanceOption","LastNameCompare").equalsIgnoreCase("true");
            this.firstNameCompare=initalFile.get("DistanceOption","FirstNameCompare").equalsIgnoreCase("true");
            this.titleCompare=initalFile.get("DistanceOption","TitleCompare").equalsIgnoreCase("true");

            this.weightFullText=Double.parseDouble(initalFile.get("Weights", "FullText"));
            this.weightAbstract=Double.parseDouble(initalFile.get("Weights","Abstract"));
            this.weightClaims=Double.parseDouble(initalFile.get("Weights","Claims"));
            this.weightDes=Double.parseDouble(initalFile.get("Weights","Description"));
            this.weightAssignee=Double.parseDouble(initalFile.get("Weights","Assignee"));
            this.weightCategory=Double.parseDouble(initalFile.get("Weights","Category"));

            this.weightCoAuthor= Double.parseDouble(initalFile.get("Weights","CoAuthor"));
            this.weightLocation = Double.parseDouble(initalFile.get("Weights","Location"));
            this.weightLastName=Double.parseDouble(initalFile.get("Weights","LastName"));
            this.weightFirstName=Double.parseDouble(initalFile.get("Weights","FirstName"));
            this.weightTitle=Double.parseDouble(initalFile.get("Weights","Title"));

            this.pCorrelation=initalFile.get("DistanceOption","PCorrelation").equalsIgnoreCase("true");

        } catch (IOException e)
        {
           logger.info("Initial File 'invidenti.ini not found',distance function will use default options");
        }
    }

    /**
     * compare two patents assignees by using the levenshtein distance between two string
     * @param str1 first assignee name
     * @param str2 second assignee name
     * @return the comparison result of two assignees;
     */
    public double compareAssignee (String str1,String str2,String code1,String code2) {

        if(code1!=null&&code2!=null&&code1.length()!=0&&code2.length()!=0) {
            if (code1.equals(code2)) {

                if (this.pCorrelation) return 1.0;
               else return 0.0;
            }
            else {
                if (this.pCorrelation) return 0.0;
                else return 1.0;
            }
        }


        if (str1==null || str2==null||str1.length()==0||str2.length()==0) {
          if (this.pCorrelation)  {
              return 0.0;
          } else {
              return 1.0;
          }
        }

        double result=0;

        NormalizedLevenshtein var0 = new NormalizedLevenshtein();

        double var1=var0.distance(str1,str2);

        if (var1>0.4) var1=1.0;

       /*
        if (var1>0.9) var1=1.0;
        if (var1>0.7) var1=2.0;
        else if(var1>0.5) var1=3.0;
        else if(var1>0.3) var1=4.0;
        else if(var1>0.1) var1=5.0;
        else var1=6.0;

        var1=var1/6;
*/
        if (this.pCorrelation)
        {
            return (1.0-var1);
        } else {
            return var1;
        }


    }

    /**
     * compare two patent categories
     * @param str1 first category
     * @param str2 second category
     * @return the comparison result of two patent categories
     */
    public double comapreCategories (String str1,String str2) {
        double result=0.0;
        if (str1==null || str2==null||str1.length()==0||str2.length()==0) {
           if(pCorrelation) {
               return 0;
           } else {
               return 1;
           }
        }
        String[] strs_1=str1.split("/");
        String[] strs_2=str2.split("/");

        Hashtable<String,ArrayList<String>> var0=new Hashtable<>();
        Hashtable<String,ArrayList<String>> var1=new Hashtable<>();
        /**
         * Split the technology class into integral class and subclass
         *
         */
        for(String var2:strs_1) {
            String[] var3=var2.split("-");
            if (var3.length==1) {
                if (!var0.containsKey(var3[0])) {
                    var0.put(var3[0],new ArrayList<>());
                }
            }  else {

                if (!var0.containsKey(var3[0])) {
                    ArrayList<String> temp=new ArrayList<>();
                    temp.add(var3[1]);
                    var0.put(var3[0],temp);
                } else
                {
                    var0.get(var3[0]).add(var3[1]);
                }
            }
        }

        for(String var2:strs_2) {
            String[] var3=var2.split("-");
            if (var3.length==1) {
                if (!var1.containsKey(var3[0])) {
                    var1.put(var3[0],new ArrayList<>());
                }
            }  else {
                if (!var1.containsKey(var3[0])) {
                    ArrayList<String> temp=new ArrayList<>();
                    temp.add(var3[1]);
                    var1.put(var3[0],temp);
                } else
                {
                    var1.get(var3[0]).add(var3[1]);
                }
            }
        }
        /**
         * Compare the technology class
         */

        for (Iterator<String> iterator = var0.keySet().iterator(); iterator.hasNext(); ) {
            String var4 = iterator.next();
            if (var1.containsKey(var4)) {
                result+=0.5;
                ArrayList<String> var5=var0.get(var4);
                ArrayList<String> var6=var1.get(var4);
                for(String var7:var5) {
                    if (var6.contains(var7)) {

                        result+=1.0;
                    }
                }
            }
        }

        if (result>4) {
            result=1.0;
        } else {
            result/=4.0;
        }

      //  logger.error(str1+" "+str2+" "+result);
        if(pCorrelation) {
            return result;
        } else {
            return 1-result;
        }
    }

    /**
     * Compare the names
     * @param name1 first name
     * @param name2 second name
     * @return the levenshtein distance between two names according to the pCorrelation
     */
    public double compareName(String name1,String name2) {
        if (name1==null || name2==null||name1.length()==0||name2.length()==0) {
            if (this.pCorrelation)  {
                return 0.0;
            } else {
                return 1.0;
            }
        }

        NormalizedLevenshtein var0 = new NormalizedLevenshtein();

        if (this.pCorrelation)
        {
            return (1-var0.distance(name1,name2));
        } else {
            return var0.distance(name1,name2);
        }
    }

    /**
     * Calculate the similarity by using the coAuthors
     * @param coAuthor1 first patent coAuthor1
     * @param coAuthor2 second patent coAuthor2
     * @return the similarity between coAuthor1 and coAuthor2
     */
    public double compareCoAuthor(String coAuthor1,String coAuthor2) {
        double result=0.0;

        if (coAuthor1==null||coAuthor2==null||coAuthor1.length()==0||coAuthor2.length()==0) {
            if (this.pCorrelation) {
                return 0;
            } else {
                return 1;
            }

        }

        String[] coAuthorNames1=coAuthor1.split(";");
        String[] coAuthorNames2=coAuthor2.split(";");
        String[] lessNames;
        String[] moreNames;

        HashSet<String> first=new HashSet<>();
        HashSet<String> second=new HashSet<>();

        for(String str:coAuthorNames1) {
            first.add(str);
        }
        for(String str:coAuthorNames2) {
            second.add(str);
        }

        HashSet<String> intersect=new HashSet<>();
        HashSet<String> union=new HashSet<>();

        intersect.addAll(first);
        intersect.retainAll(second);

        union.addAll(first);
        union.addAll(second);

        result=(double) intersect.size()/union.size();


       /*
        if (coAuthorNames1.length>coAuthorNames2.length) {
            lessNames=coAuthorNames2;
            moreNames=coAuthorNames1;
        } else {
            moreNames=coAuthorNames2;
            lessNames=coAuthorNames1;
        }



        for(String var1:lessNames) {

            for (String var2: moreNames) {
            if (var2.equalsIgnoreCase(var1)) {
                result+=1.0;
            }
            }
        }
        if (result>6.0) {
            result=6.0;
        }

        }*/
        if (this.pCorrelation) {
            return result;
        } else {
            return (1-result);
        }
    }


    public  double compareLocation(String country1,String lat1,String lng1,String country2,String lat2,String lng2) {
        double result=0.0;


        if (country1==null||country2==null||country1.equalsIgnoreCase(country2)) {
            if (lat1==null||lat2==null||lng1==null||lng2==null) {
                result=1.0;
            } else {
                double lat1_d = Double.parseDouble(lat1)/180;
                double lng1_d = Double.parseDouble(lng1)/180;
                double lat2_d = Double.parseDouble(lat2)/180;
                double lng2_d = Double.parseDouble(lng2)/180;
                double distance = getDistance(lat1_d,lng1_d,lat2_d,lng2_d);
                if ( distance < 5.0 )
                    result = 5.0;
                else if ( distance < 10.0 )
                    result = 4.0;
                else if ( distance < 25.0)
                    result = 3.0;
                else if ( distance < 50.0 )
                    result = 2.0;
                else
                {
                    result = 1.0;
                }

                if ((country1==null||country2==null)&&distance>500) {
                    result=0.0;
                }

            }

        }

        if (this.pCorrelation) {
            return result/5.0;
        } else {
            return (1-result/5.0);
        }
    }


    /**
     * Gets the geographical distance from the latitute and longitude data
     * @param latitute and longitude of the two places
     * @return the distance between the two places
     */
    public double getDistance(double lat1, double lng1, double lat2, double lng2) {

        double a = lat1 - lat2;
        double b = lng1 - lng2;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) +
                Math.cos(lat1)*Math.cos(lat2)*Math.pow(Math.sin(b/2),2)));
        s = s * 6371;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    /**
     * Set the options of the distance
     * @param options the boolean array for the options
     * @return true if the functions set the options correctly
     */
    public boolean setOptions(boolean[] options) {

        if (options.length<this.numofOptions) {
            logger.warn("Option setting fails!");
            return false;
        }

        this.fulltextCompare=options[0];
        this.abstractCompare=options[1];
        this.claimsCompare=options[2];
        this.desComapre=options[3];
        this.categoryCompare=options[4];
        this.assigneeCompare=options[5];

        this.lastNameCompare=options[6];
        this.coAuthorCompare=options[7];
        this.locationCompare=options[8];
        this.firstNameCompare=options[9];
        this.titleCompare=options[10];

        return true;
    }

    /**
     * Set the weights of the scores
     * @param options a double array for the weights of the scores
     * @return true if the setting is successful
     */
    public boolean setWeights(double[] options) {
        if (options.length<this.numofOptions) {
            logger.warn("Weights Setting fails");
            return false;
        }
        this.weightFullText=options[0];
        this.weightAbstract=options[1];
        this.weightClaims=options[2];
        this.weightDes=options[3];
        this.weightCategory=options[4];
        this.weightAssignee=options[5];

        this.weightLastName=options[6];
        this.weightCoAuthor=options[7];
        this.weightLocation=options[8];
        this.weightFirstName=options[9];
        this.weightTitle=options[10];


        return true;
    }



    public void setpCorrelation(boolean pCorrelation){
        this.pCorrelation=pCorrelation;
    }

    public String toString() {
        String var0="Distance Type:"+this.distanceType+"\n";
        DecimalFormat df = new DecimalFormat("#0.00");
        var0+="Options:\n";

        var0+="\t"+"FullText    |"+df.format(this.weightFullText)+" \t|"+this.fulltextCompare+"\n";
        var0+="\t"+"Abstract    |"+df.format(this.weightAbstract)+" \t|"+this.abstractCompare+"\n";
        var0+="\t"+"Claims      |"+df.format(this.weightClaims)+" \t|"+this.claimsCompare+"\n";
        var0+="\t"+"Description |"+df.format(this.weightDes)+" \t|"+this.desComapre+"\n";
        var0+="\t"+"Categories  |"+df.format(this.weightCategory)+" \t|"+this.categoryCompare+"\n";
        var0+="\t"+"Assignee    |"+df.format(this.weightAssignee)+" \t|"+this.assigneeCompare+"\n";
        var0+="\t"+"LastName    |"+df.format(this.weightLastName)+" \t|"+this.lastNameCompare+"\n";
        var0+="\t"+"FirstName   |"+df.format(this.weightFirstName)+" \t|"+this.firstNameCompare+"\n";
        var0+="\t"+"CoAuthor    |"+df.format(this.weightCoAuthor)+" \t|"+this.coAuthorCompare+"\n";
        var0+="\t"+"Location    |"+df.format(this.weightLocation)+" \t|"+this.locationCompare+"\n";
        var0+="\t"+"Title       |"+df.format(this.weightTitle)+" \t|"+this.titleCompare+"\n";

        return var0;
    }


    public String getWeights(){
        return this.weightAbstract+" "+weightDes+" "+weightClaims+" "+weightTitle+" "+weightFirstName+" "+weightLastName+" "+weightCategory+" "+weightAssignee+" "+weightCoAuthor+" "+weightLocation;
    }


}
