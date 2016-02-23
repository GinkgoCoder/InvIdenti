package base;

import java.io.*;

/**
 * Created by leisun on 16/1/19.
 */
public class textOperator {
    /**
     * Store the text into a text file
     * @param path text path
     * @param follow rewrite option
     * @param str content to write
     */

    public static void storeText(String path,boolean follow,String str){
        if (follow) {
            try {
                FileWriter w=new FileWriter(path,follow);
                w.write(str);
                w.close();
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

    public static String getFirstWords(int num,String str) {
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
     * Read the file from a specified path
     * @param path File path
     * @return the Content in the file
     */

    public static String readText(String path){
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

}
