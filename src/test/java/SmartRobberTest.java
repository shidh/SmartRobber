import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by allen on 13/11/15.
 */

public class SmartRobberTest {

    @Test
    public void noAdjacentHouseCanBeRobbed() {

        SmartRobber.main();
        List<String> output = new ArrayList<String>();
        int profit = 0;
        try {
            BufferedReader br = new BufferedReader(new FileReader("output.txt"));
                String line;
                while ((line = br.readLine()) != null) {
                    output.add(line);
                }
            }
        catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("Cannot find file output.txt");
        }


        Gson gson = new Gson();
        List<HouseNode> houses = new ArrayList<HouseNode>();
        try {
            BufferedReader br = new BufferedReader(new FileReader("input.json"));
            Type listType = new TypeToken<ArrayList<HouseNode>>(){}.getType();
            houses = gson.fromJson(br, listType);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Cannot find file input.json");
        }


        //Test case1: assert statements no two "YES" as neighbor
        assertFalse(output.get(0).trim().equals("YES") && output.get(output.size()-1).trim().equals("YES"));
        for(int i = 1; i<output.size()-1; i++){
            assertFalse(output.get(i).trim().equals("YES") && output.get(i+1).trim().equals("YES"));
        }


        //Test case2: assert the total profit by following the YES/NO list equals the calculation
        for(int i = 0; i<output.size(); i++){
            if(output.get(i).equals("YES")){
                profit += houses.get(i).getValue();
            }
        }
        assertEquals(new SmartRobber().robRecursive(houses), profit);

    }
}
