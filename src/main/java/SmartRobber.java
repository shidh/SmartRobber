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

    /**
     *
     */
    public static void main(String[] args) {
        //get data from the json file
        List <HouseNode> houses = getHouses();

        //run the smart robber recursively
        SmartRobber robber = new SmartRobber();
//        System.out.println("Final profit by robRecursive method: "+ robber.robRecursive(houses));
//        System.out.println("Final profit by robIterator method: "+ robber.robIterator(houses));
        System.out.println("Final profit by robWithMemPath method: "+robber.robWithMemPath(houses));
        //flush output to output.txt file
        writeOutput();
    }

    /**
     *
     * @param houses , List of HouseNode from the json file
     */
    public int robRecursive(List<HouseNode> houses) {

        //init the target robGuideList
        for(int i=0; i<houses.size();i++){
            robGuideList.put(i,"");
        }


        //divide the rob strategies into two cases: without lastNode or firstNode
        if( robIgnoreFirstNode(houses, houses.size() - 1) > robIgnoreLastNode(houses, houses.size() - 1)){
            System.out.println("Strategy: robLast ");
            setRobGuideList(caseIgnoreFirst, houses);
            return robIgnoreFirstNode(houses, houses.size() - 1);

        }else{
            System.out.println("Strategy: robFirst");
            setRobGuideList(caseIgnoreLast, houses);
            return robIgnoreLastNode(houses, houses.size() - 1);

        }
    }

    /**
     *
     * @param houses , List of HouseNode from the json file without the firstNode
     * @param n ,  n houses in the house list
     * @return profit for the given n
     */
    private int robIgnoreFirstNode(List<HouseNode> houses, int n){
        if (caseIgnoreFirst.containsKey(n)) {
            return caseIgnoreFirst.get(n);
        }

        int profit;
        //ignore first house when index == 0, P(0) = 0

        if (n <= 0) {
            profit = 0;
        } else if (n == 1){  //P(1) == V(1)
            profit = houses.get(1).getValue();
        } else{ //P(n) = Max{V(n)+ P(n-2), P(n-1) }
            profit = Math.max(houses.get(n).getValue() + robIgnoreFirstNode(houses, n-2),
                    houses.get(n - 1).getValue() +robIgnoreFirstNode(houses, n-3));
        }
        caseIgnoreFirst.put(n, profit);
        return profit;
    }


    /**
     *
     * @param houses , List of HouseNode from the json file without the lastNode
     * @param n, n houses in the house list
     * @return profit for the given n
     */
    private int robIgnoreLastNode(List<HouseNode> houses, int n){
        if (caseIgnoreLast.containsKey(n)) {
            return caseIgnoreLast.get(n);
        }

        int profit;
        if (n < 0){  // P(0) = 0
            return 0;
        } else if (n == 0) {  //P(1) = V(1)
            profit = houses.get(0).getValue();

        } else{  //P(n) = Max{V(n)+ P(n-2), P(n-1) }
            if (n == houses.size() - 1) {   //ignoreLastNode
                profit = robIgnoreLastNode(houses, n-1);
            } else {
                profit = Math.max(houses.get(n).getValue() + robIgnoreLastNode(houses, n-2),
                        houses.get(n - 1).getValue() +robIgnoreLastNode(houses, n-3));
            }
        }
        caseIgnoreLast.put(n, profit);
        return profit;
    }



    private void setRobGuideList(Map<Integer, Integer> map, List<HouseNode> houses){
        for(int i=houses.size()-1; i>=2; i--){
            //P(n) = Max{V(n)+ P(n-2), P(n-1) }

            if(map.containsKey(i-2) && map.containsKey(i)) {
                if (houses.get(i).getValue() + map.get(i - 2) == map.get(i)) {
                    robGuideList.put(i, "YES");
                } else {
                    robGuideList.put(i, "NO");
                }
            }else{
                robGuideList.put(i, "NO");
            }

        }

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
//            System.out.print("Key = " + entry.getKey() +"  ");
//            System.out.println("Value = " + entry.getValue());

//            if(map.containsKey(entry.getKey() -1)) {
//                if (entry.getValue() - map.get(entry.getKey() - 1) == houses.get(entry.getKey()).getValue()) {
//                    robGuideList.put(entry.getKey(), "YES");
//                } else {
//                    robGuideList.put(entry.getKey(), "NO");
//                }
//            }else{
//                robGuideList.put(entry.getKey(), "NO");
//            }


//            if(map.containsKey(entry.getKey() -1)) {
//                if (entry.getValue() > map.get(entry.getKey() - 1)) {
//                    if(houses.get(entry.getKey()).isMax()) {
//                        robGuideList.put(entry.getKey(), "YES");
//                    }else{
//                        robGuideList.put(entry.getKey(), "NO");
//                    }
//                }else{
//                    robGuideList.put(entry.getKey(), "NO");
//                }
//            }else{
//                robGuideList.put(entry.getKey(), "NO");
//            }
//
//            if(entry.getKey() == 0){
//                if(map.get(0) > 0){
//                    robGuideList.put(0, "YES");
//                }else{
//                    robGuideList.put(0, "NO");
//                }
//            }
        }

    }


    public static List<HouseNode> getHouses(){
        Gson gson = new Gson();
        List<HouseNode> houses = new ArrayList<HouseNode>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("input.json"));
            Type listType = new TypeToken<ArrayList<HouseNode>>(){}.getType();
            houses = gson.fromJson(br, listType);
            System.out.println("The given json file size: "+houses.size());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot find file input.json");
        }
        return houses;
    }

    public static void writeOutput() {
        try {
            FileWriter fw = new FileWriter("output.txt", false);
            BufferedWriter out = new BufferedWriter(fw);

            for (Integer key : robGuideList.keySet()) {
                out.append(robGuideList.get(key) + "\r\n");
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
     * @param houses , List of HouseNode from the json file without the lastNode
     * @return total final profit
     */
    private int robIterator(List<HouseNode> houses) {
        if(houses==null||houses.size()==0)
            return 0;

        int n = houses.size();

        if(n==1){
            return houses.get(0).getValue();
        }

        if(n==2){
            return Math.max(houses.get(1).getValue(), houses.get(0).getValue());
        }

        //ignore first house
        int[] profitRobLast = new int[n+1];
        profitRobLast[0]=0;
        profitRobLast[1]=houses.get(1).getValue();
        for(int i=2; i<n; i++){ //P(n) = Max{V(n)+ P(n-2), P(n-1) }
            profitRobLast[i] = Math.max(profitRobLast[i-2]+houses.get(i).getValue(), profitRobLast[i-1]);
        }

        //ignore last house
        int[] profitRobFirst = new int[n+1];
        profitRobFirst[0]=0;
        profitRobFirst[1]=houses.get(0).getValue();

        for(int i=2; i<n; i++){
            profitRobFirst[i] = Math.max(profitRobFirst[i-2] + houses.get(i-1).getValue(), profitRobFirst[i-1]);
        }
        return Math.max(profitRobLast[n-1], profitRobFirst[n-1]);
    }


    private int robWithMemPath(List<HouseNode> houses){
        if(houses==null||houses.size()==0)
            return 0;

        int n = houses.size();

        if(n==1){
            return houses.get(0).getValue();
        }

        if(n==2){
            return Math.max(houses.get(1).getValue(), houses.get(0).getValue());
        }

        // profit P(index, isRob)                 path(index, isRob) = isRob for index-1
        // P(n, 0) = max{P(n-1,0), P(n-1, 1)}     Path[n, 0] = P(n-1, 0) > P(n-1 , 1) ? 0: 1
        // P(n, 1) = max{P(n-1,0)+ V(n)           Path[n, 1] = 0

        // GetPath:
        //         Path[n, 0] = ?   1 ->   Path[n-1, 1] :  0 -> Path[n-1,0]         .... Path[0,0]
        //         Path[n, 1] = 0}

        //ignore last house
        int[][] profitRobFirst = new int[n][2];

        int[][] path = new int[n][2];

        profitRobFirst[0][0]= 0;
        profitRobFirst[1][1]=houses.get(0).getValue();
        robGuideList.put(1, "YES");


        //get profit
        for(int i=2; i<n; i++){
            profitRobFirst[i][0] = Math.max(profitRobFirst[i-1][0] , profitRobFirst[i-1][1]);
            path[i][0] = (profitRobFirst[i-1][0] > profitRobFirst[i-1][1])? 0:1;

            profitRobFirst[i][1] = Math.max(profitRobFirst[i-1][0] , houses.get(i).getValue() );
            path[i][1] = 0;
        }

        //trace path
        int preview = 0;
        for(int i=n-1; i>2; i--) {
            if(path[i][preview] == 1){
                robGuideList.put(i-1, "YES");
                preview =1;

            }else if(path[i][preview] == 0){
                robGuideList.put(i-1, "NO");
                preview =0;
            }
            System.out.println(i +" "+robGuideList.get(i));

        }


        return Math.max(profitRobFirst[n-1][0], profitRobFirst[n-1][1]);

    }


}
