import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by allen on 12/11/15.
 */

/*
Input:
    grab “input.json” file in attached.
    an JSON array with information about each houses, each one is a dictionary with the following keys:
    “value” ­> this house’s own profit score “prev” ­> previous house’s score
    “next” ­> next house’s score

    {"prev": 631,
     "value": 856,
     "next": 114}

Output:
    A line­by­line decision to hit or not for a particular house,
    starting from the first house.
    Generate a file.
    Sample #1: your result mostly likely looks like this YES
    YES
    NO
    YES
    ...
 */

public class SmartRobber {
    /* Find the DP state function

       Define P(n) is the Profit for n houses,  V(n) is the value of the n-th house.

       P(0) = 0
       P(1) = V(1)
       P(2) = Max{V(2), V(1)}, two cases based on the decision of grabbing V(2) or not
       P(3) = Max{V(3), Max{V(1), V(2)}} = Max{V(3), P(2)}
       P(4) = Max{V(4)+V(2),  V(1)+V(3)}
       ~No State Transfer function fund~

       Case1: Assume V(1) will never not be robbed
       P(0) = 0
       P(1) = 0 , V(1) because will never not be robbed
       P(2) = V(2)
       P(3) = Max{V(3), V(2)}
       P(4) = Max{V(4)+V(2),  Max{V(3), V(2)} } = Max{V(4)+P(2),  P(3) }
       ...
       P(n) = Max{V(n)+ P(n-2), P(n-1) }  State Transfer function!!!
     */

    //The HashMap stores the profile state list with the HouseID:CurrentProfit as kay:value pair
    Map<Integer, Integer> caseIgnoreFirst = new LinkedHashMap<Integer, Integer>();
    Map<Integer, Integer> caseIgnoreLast = new LinkedHashMap<Integer, Integer>();

    //The HashMap stores the target house list with the HouseID:YES/NO as key:value pair
    static Map<Integer, String> robGuideList = new LinkedHashMap<Integer, String>();
    Map<Integer, String> caseIgnoreFirstTargetHouseList = new LinkedHashMap<Integer, String>();
    Map<Integer, String> caseIgnoreLastTargetHouseList = new LinkedHashMap<Integer, String>();

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        Gson gson = new Gson();
        List<HouseNode> houses = new ArrayList<HouseNode>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("input.json"));
            Type listType = new TypeToken<ArrayList<HouseNode>>(){}.getType();
            houses = gson.fromJson(br, listType);
            System.out.println("size: "+houses.size());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot find file input.json");
        }

        SmartRobber robber = new SmartRobber();
        robber.rob(houses);


        try {
            FileWriter fw = new FileWriter("output.txt", false);
            BufferedWriter out = new BufferedWriter(fw);

            for (Integer key : robGuideList.keySet()) {
//                System.out.print("Key = " + key +"  ");
//                System.out.println("Value = " + robGuideList.get(key));
                out.append(robGuideList.get(key)+"\r\n");
            }

            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot write into file output.txt, check your disk or permissions");
        }
}

    /**
     *
     * @param houses , List of HouseNode from the json file
     */
    private void rob(List<HouseNode> houses) {

        //init the targetHouseList for case 1
        for(int i=0; i<houses.size();i++){
            caseIgnoreFirstTargetHouseList.put(i,"");
        }

        //init the targetHouseList for case 2
        for(int i=0; i<houses.size();i++){
            caseIgnoreLastTargetHouseList.put(i,"");
        }

        int totalProfit =0;
        //if the data is null or empty
        if (houses == null || houses.size()== 0){
            System.out.println("Please make sure the data file input.json is not empty");
            System.exit(-1);
        }

        //divide the rob strategies into two cases: without lastNode or firstNode
        if( robIgnoreFirstNode(houses, houses.size() - 1) >= robIgnoreLastNode(houses, houses.size() - 1)){
            totalProfit = robIgnoreFirstNode(houses, houses.size() - 1);

            for (Map.Entry<Integer, Integer> entry : caseIgnoreFirst.entrySet()) {
                System.out.print("Key = " + entry.getKey() +"  ");
                System.out.println("Value = " + entry.getValue());

                if(caseIgnoreFirst.containsKey(entry.getKey() -1)) {
                    if (entry.getValue() > caseIgnoreFirst.get(entry.getKey() - 1)) {
                        caseIgnoreFirstTargetHouseList.put(entry.getKey(), "YES");
                    }else{
                        caseIgnoreFirstTargetHouseList.put(entry.getKey(), "NO");
                    }
                }
            }

            robGuideList = caseIgnoreFirstTargetHouseList;
        }else{
            totalProfit = robIgnoreLastNode(houses, houses.size() - 1);
            for (Map.Entry<Integer, Integer> entry : caseIgnoreLast.entrySet()) {
                System.out.print("Key = " + entry.getKey() +"  ");
                System.out.println("Value = " + entry.getValue());

                if(caseIgnoreLast.containsKey(entry.getKey() -1)) {
                    if (entry.getValue() > caseIgnoreLast.get(entry.getKey() - 1)) {
                        caseIgnoreLastTargetHouseList.put(entry.getKey(), "YES");
                    }else{
                        caseIgnoreLastTargetHouseList.put(entry.getKey(), "NO");

                    }
                }
            }

            robGuideList = caseIgnoreLastTargetHouseList;
        }
        System.out.println("Final profit: "+ totalProfit);
    }

    /**
     *
     * @param houses , List of HouseNode from the json file without the firstNode
     * @param n ,  n houses in the house list
     * @return
     */
    private int robIgnoreFirstNode(List<HouseNode> houses, int n){
        if (caseIgnoreFirst.containsKey(n)) {
            return caseIgnoreFirst.get(n);
        }

        int profit;
        //ignore first house when index == 0, P(0) = 0
        if (n <= 0) {
            profit = 0;
        }
        //
        else if (n == 1){  //P(1) == V(1)
            profit = houses.get(1).getValue();
        }
        else{ //P(n) = Max{V(n)+ P(n-2), P(n-1) }
            if(houses.get(n).getValue() + robIgnoreFirstNode(houses, n-2) >
                    houses.get(n - 1).getValue() +robIgnoreFirstNode(houses, n-3)){
                profit = houses.get(n).getValue() + robIgnoreFirstNode(houses, n-2);
            }else{
                profit =  houses.get(n - 1).getValue() +robIgnoreFirstNode(houses, n-3);
            }

        }
        caseIgnoreFirst.put(n, profit);
        return profit;
    }


    /**
     *
     * @param houses , List of HouseNode from the json file without the lastNode
     * @param n, n houses in the house list
     * @return
     */
    private int robIgnoreLastNode(List<HouseNode> houses, int n){
        if (caseIgnoreLast.containsKey(n)) {
            return caseIgnoreLast.get(n);
        }

        int profit;
        if (n < 0){  // P(0) = 0
            return 0;
        }
        else if (n == 0){  //P(1) = V(1)
            profit = houses.get(0).getValue();

        }
        else{  //P(n) = Max{V(n)+ P(n-2), P(n-1) }
            if (n == houses.size() - 1) {   //ignoreLastNode
                profit = robIgnoreLastNode(houses, n-1);
            } else {
                if(houses.get(n).getValue() + robIgnoreFirstNode(houses, n-2) >
                        houses.get(n - 1).getValue() +robIgnoreFirstNode(houses, n-3)){
                    profit = houses.get(n).getValue() + robIgnoreFirstNode(houses, n-2);
                }else{
                    profit = houses.get(n - 1).getValue() +robIgnoreFirstNode(houses, n-3);

                }

            }
        }
        caseIgnoreLast.put(n, profit);
        return profit;
    }

}
