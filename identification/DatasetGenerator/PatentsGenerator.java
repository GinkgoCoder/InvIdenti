package DatasetGenerator;

import base.ProgressBar;
import base.pair;
import base.patent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.carrot2.core.LanguageCode;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

/**
 * Created by sunlei on 15/10/27.
 */
public class PatentsGenerator {
    private static Logger logger= LogManager.getLogger(PatentsGenerator.class.getName());

    String databasePath=null;
    String textsPath=null;
    String trainingDataPath=null;

    Connection connection=null;
    Statement stmt=null;

    Connection connectionTraining=null;
    Statement stmtTraining=null;
    LanguageCode language = LanguageCode.ENGLISH;
    private String IDType="Upper Bound";

    //not used currently
    public PatentsGenerator(String databasePath) {
        this.databasePath=databasePath;
    }

    public void setIDType(String IDType) {
        this.IDType=IDType;
    }

    //not used currently
    public PatentsGenerator(String databasePath,String textsPath) {
        this.databasePath=databasePath;
        this.textsPath=textsPath;
        try {

            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.databasePath);
            this.connection.setAutoCommit(false);
            this.stmt=connection.createStatement();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    //database path refers to the infodata path, i.e the file PatTest.sqlite
    //the three arguments set all the paths and opens connections to the database
    public PatentsGenerator(String databasePath,String textsPath,String trainingDataPath) {
        this.databasePath=databasePath;
        this.textsPath=textsPath;
        this.trainingDataPath=trainingDataPath;

        //connect to infodata database and trainingdata database
        try {

            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.databasePath);
            this.connection.setAutoCommit(false);
            this.stmt=connection.createStatement();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {

            Class.forName("org.sqlite.JDBC");
            this.connectionTraining = DriverManager.getConnection("jdbc:sqlite:" + this.trainingDataPath);
            this.connectionTraining.setAutoCommit(false);
            this.stmtTraining=connectionTraining.createStatement();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Close the connection of the database
     */
    public void closeDatabase() {
        try {
            stmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
    /**
     * get the patent which gets the text from files.
     * @param patentNumber patent Number
     * @param table table name
     * @param lastname author name
     * @return the patent
     */
    public patent getOnePatentFromText(String patentNumber,String table,String lastname,String firstname) {

        String var1;
        lastname=lastname.trim();
        firstname=firstname.trim();


         lastname=lastname.replaceAll("-"," ").replaceAll("\\.","");
         firstname=firstname.replaceAll("-"," ").replaceAll("\\.","");



        patent var2=null;
        var1=patentNumber;

        //adds leading zero if the patentNumber does not contain 8 chars
        if (patentNumber.length()<8) {
            for(int i=0;i<8-patentNumber.length();i++) {
                var1="0"+var1;
            }
        }

        String sql="Select * from "+ table +" where " + table + ".patent" + "='" + var1 + "';";


        try {

            ResultSet var0=stmt.executeQuery(sql);
            String abs=readText(this.textsPath + patentNumber + "/" + "Abstract.txt");

            //gets first 300 words as stated by research
            abs=getFirstWords(300,abs);

            String claims=readText(this.textsPath + patentNumber + "/" + "Claims.txt");
            claims=getFirstWords(300,claims);

            String description=readText(this.textsPath+patentNumber+"/"+"Description.txt");
            description=getFirstWords(300,description);

            String title=readText(this.textsPath+patentNumber+"/"+"Title.txt");
            String coAuthor="";


            while (var0.next()) {

                String patent=var0.getString("Patent");
                String authorLastName=var0.getString("Lastname").trim();
                String authorFirstName=var0.getString("Firstname").trim();
                String assignee=var0.getString("Assignee");
                String category=var0.getString("Class");
                String lat=var0.getString("Lat");
                String lng=var0.getString("Lng");
                String country=var0.getString("Country");
                String asigneeNum=var0.getString("AsgNum");
                String ID;

                if (this.IDType.equalsIgnoreCase("Upper Bound")) {

                    ID=var0.getString("Invnum_N_UC");
                } else
                {

                    ID=var0.getString("Invnum_N");
                }

                boolean lastC=authorLastName.equalsIgnoreCase(lastname)||authorLastName.replaceAll(" ","").equalsIgnoreCase(lastname)||lastname.replaceAll(" ","").equalsIgnoreCase(authorLastName)||authorLastName.replaceAll(" ","").equalsIgnoreCase(lastname.replaceAll(" ",""));
                if (!lastC) {
                    int num=Math.min(authorLastName.replace(" ","").length(),lastname.replaceAll(" ","").length());
                    lastC=lastC||authorLastName.replaceAll(" ","").substring(0,num).equalsIgnoreCase(lastname.replaceAll(" ","").substring(0,num));

                }
                boolean firstC=authorFirstName.equalsIgnoreCase(firstname)||authorFirstName.replaceAll(" ","").equalsIgnoreCase(firstname)||firstname.replaceAll(" ","").equalsIgnoreCase(authorFirstName)||authorFirstName.replaceAll(" ","").equalsIgnoreCase(firstname.replaceAll(" ",""));

                if(!firstC) {
                    int num=Math.min(authorFirstName.replace(" ","").length(),firstname.replaceAll(" ","").length());
                    firstC=firstC||authorFirstName.replaceAll(" ","").substring(0,num).equalsIgnoreCase(firstname.replaceAll(" ","").substring(0,num));
                }

                if (lastC&&firstC)
                {
                   /*
                    if(abs.length()==0||claims.length()==0||description.length()==0) {
                        System.out.println(patent);
                    }
                    */



                    var2=new patent(patent,abs,claims,description,title,category,assignee,authorLastName,authorFirstName,lat,lng,"",country,asigneeNum);



                    var2.setID(ID);

                } else {

                    coAuthor+=authorLastName+" "+authorFirstName+";";
                }
            }
            if (var2!=null){
                var2.setCoAuthor(coAuthor);
            } else
            {

            }
/*
            if (var2==null) {
                System.out.println(lastname+"+"+firstname);
                System.out.println(patentNumber);
                System.out.println(coAuthor);
                System.exit(0);
            }
*/
            return var2;
        } catch (SQLException e) {
            logger.error("No Information found for patent:"+patentNumber);
        }

        return null;
    }

    /**
     * Read the file from a specified path
     * @param path File path
     * @return the Content in the file
     */

    public String readText(String path){
        File f= null;
        try {
            f = new File(path);
            if (!f.exists()) {
                return null;
            } else {
                BufferedReader r=new BufferedReader(new FileReader(path));
                String str="";
                String line=r.readLine();
                while(line!=null){
                    str+=line;
                    line=r.readLine();
                }
                r.close();
                return str;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param table database table name
     * @param startIndex start patent index
     * @param size the patents size
     */
    public pair<ArrayList<patent>,ArrayList<String>> getTrainingPatents(String table,int startIndex,int size)  {
        ArrayList<patent> patents=new ArrayList<>();
        ArrayList<String> patentsID=new ArrayList<>();
        logger.warn("Start to generate patents.");
        System.out.println();
        String sql="select * from "+table;
        try {
            ResultSet var0=this.stmtTraining.executeQuery(sql);
            int var1=startIndex;

           //seems like a code that is not used
            do {

                if (var1 < 2) {
                    break;

                } else {
                    var1--;
                }
            } while (var0.next());


            int var2=size;

            /*
            commented out by @Sanchit
            int num1=0;
            */
            while (var0.next()) {

                if (var2==0) {
                    break;
                }
                if(var2>=1) {
                    var2--;
                }

                if (size>0) {

                    String bar= ProgressBar.barString((int)((size-var2)*100/size));
                    System.out.print("\r"+bar);

                }

                patent var3=this.getOnePatentFromText(var0.getString("Patent"), "invpat", var0.getString("LastName"),var0.getString("FirstName"));



                if (var3!=null)
                {

                    var3.setID(var0.getString("ID"));
                    patents.add(var3);
                    patentsID.add(var0.getString("ID"));



                } else if (var2>-1) {
                    var2++;
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        pair<ArrayList<patent>,ArrayList<String>> result=new pair<>(patents,patentsID);
        System.out.println();
        logger.warn("Finish generate the patents.");
        return result;
    }


    private String getFirstWords(int num,String str) {
        String temp="";
        if (str==null) return "";
        String[] words=str.split(" ");
        int i=0;
        for(String w:words) {
            if (i>=num) break;
            temp+=w+" ";
            i++;
        }
        return temp;

    }

    /**
     *
     * @param table
     * @param startIndex
     * @param size
     * @return
     */

    public pair<ArrayList<patent>,ArrayList<String>> getTrainingPatentsWithEstimatedID(String table,int startIndex,int size)  {
        ArrayList<patent> patents=new ArrayList<>();
        ArrayList<String> patentsID=new ArrayList<>();
        String sql="select * from "+table;
        try {
            ResultSet var0=this.stmtTraining.executeQuery(sql);
            int var1=startIndex;

            while (var0.next()) {
                if (var1 < 2) {
                    break;

                } else {
                    var1--;
                }
            }

            int var2=size;

            if (size>0) {
                String bar= ProgressBar.barString(size-var2);
                System.out.print(bar);
            }
            while (var0.next()) {
                if (var2<1) {
                    break;

                } else {
                    var2--;
                }
                patent var3=this.getOnePatentFromText(var0.getString("Patent"), "invpat", var0.getString("LastName"),var0.getString("FirstName"));
                if (var3!=null)
                {
                    patents.add(var3);
                    patentsID.add(var3.getID());

                } else {
                    var2++;
                }


            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println();
        pair<ArrayList<patent>,ArrayList<String>> result=new pair<>(patents,patentsID);
        return result;
    }

}
