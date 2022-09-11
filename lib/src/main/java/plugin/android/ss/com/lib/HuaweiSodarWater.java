package plugin.android.ss.com.lib;

import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 输入例子1:
 3
 10
 81
 0

 输出例子1:
 1
 5
 40
 */
public class HuaweiSodarWater {
    public static void main(String[] args) {
        try {
            ArrayList<Integer> inputList = new ArrayList<>();
            System.out.println("Enter inputs");
            while (true) {
                Scanner input = new Scanner(System.in);  // Create a Scanner object
                String countStr = input.nextLine();
                int count = Integer.valueOf(countStr);
                if (count < 0 || count > 100) {
                    System.out.println("includes invalid input " + countStr);
                    System.out.println("Please restart the process again !");
                    break;
                }
                if (count == 00) {
                    break;
                }
                inputList.add(count);
            }

            System.out.println("Outputs: ");
            for (int count : inputList) {
                System.out.println(calculateResult(count));
            }
        }catch (Exception e){
            System.out.println("Please restart the process again !");
        }
    }

    private static int calculateResult(int n) {
        int ret = 0;
        if(n > 3){
            int temp = n/3;
            ret = temp + calculateResult(temp + n%3);
        }else if(n == 2 || n == 3){
            ret = 1;
        }else{
            ret = 0;
        }
        return ret;
    }
}