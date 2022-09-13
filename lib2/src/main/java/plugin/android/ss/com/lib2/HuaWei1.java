package plugin.android.ss.com.lib2;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 输入：
 * 5
 * 1 2 3 2 1
 *
 * 输出3
 *
 *
 * 输入：
 * 9
 * 0 1 2 0 2 4 2 1 0
 *
 * 输出：
 * 6
 *
 */
public class HuaWei1 {
    //private static int result;

    public static void main(String[] args) {
        System.out.println("Please Enter Inputs: ");
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        ArrayList<Integer> inputList = new ArrayList<>();
        for(int i = 0; i < n; i++){
            inputList.add(sc.nextInt());
        }

        //result = 0;
        System.out.println(calculate( inputList));
    }

    private static int calculate(ArrayList<Integer> inputList) {
        int ret = 0;
        System.out.println("inputList contains 0" + inputList.contains(0));
        if(inputList.contains(0)) {
            ArrayList<ArrayList<Integer>> nonZeroLists = new ArrayList<>();
            ArrayList<Integer> tempList = new ArrayList<>();
            for (int index = 0; index < inputList.size(); index++) {
                if (inputList.get(index) != 0) {
                    tempList.add(inputList.get(index));
                } else if (tempList.size() > 0) {
                    nonZeroLists.add(tempList);
                    tempList = new ArrayList<>();
                }
            }
            if (tempList.size() > 0) {
                nonZeroLists.add(tempList);
            }
            for(ArrayList<Integer> list : nonZeroLists){
                System.out.println("before ret is " + ret + "nonZeroLists "+ nonZeroLists.size());
                for (int i : list){
                    System.out.println(i);
                }
                ret += calculate(list);

                System.out.println("after ret is " + ret + "nonZeroLists "+ nonZeroLists.size());
            }
        }else {
            int min = getMinOfList(inputList);
            for(int index = 0; index < inputList.size(); index++){

                inputList.set(index, inputList.get(index) - min);
                System.out.println(inputList.get(index));
            }
            if(min > 0) {
                //System.out.println("before min is " + min + "ret is "+ ret);

                ret = min + calculate(inputList);
                //for (int i : inputList){
                //    System.out.println(i);
                //}
                //System.out.println("min is " + min + "ret is "+ ret);
            }
        }
        return ret;
    }

    private static int getMinOfList(ArrayList<Integer> inputList) {
        int min = -1;
        for(int index = 0; index < inputList.size(); index++){
            if(min == -1 || min > inputList.get(index)){
                min = inputList.get(index);
            }
        }
        return min;
    }
}