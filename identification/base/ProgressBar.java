package base;

/**
 * Created by sunlei on 15/12/25.
 */
public class ProgressBar {
    int number=100;

    public static String barString(int number) {
        String var0="|";
        for(int i=0;i<number;i++) {
            var0+="=";
        }
        for(int i=0;i<100-number;i++) {
            var0+=" ";
        }
        var0+="|";
        var0+=number+"%";
        return var0;

    }

    public static void main(String[] args) {
        for(int i=0;i<=100;i++) {

                System.out.print("\r"+ProgressBar.barString(i));

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println();
    }

}
