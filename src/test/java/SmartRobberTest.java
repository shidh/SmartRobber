import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

        SmartRobber robber = new SmartRobber();
        List<HouseNode> houses = robber.getHouses();

        robber.robRecursive(houses);
        List<String> output = readOutputFile();

        //Test case1: assert statements no two "YES" as neighbor
        assertFalse(output.get(0).trim().equals("YES") && output.get(output.size()-1).trim().equals("YES"));
        for(int i = 1; i<output.size()-1; i++){
            assertFalse(output.get(i).trim().equals("YES") && output.get(i+1).trim().equals("YES"));
        }

    }


    @Test
    public void expectedProfitShouldBeSame() {
        int profit = 0;
        SmartRobber robber = new SmartRobber();

        List<HouseNode> houses = robber.getHouses();

        //Test case2: assert the total profit by following the YES/NO list equals the calculation
        List<String> output = readOutputFile();
        for(int i = 0; i<output.size(); i++){
            if(output.get(i).equals("YES")){
                profit += houses.get(i).getValue();
            }
        }
        assertEquals(robber.robRecursive(houses), profit);
    }


    /**
     * helper method to read the output file
     * @return
     */
    private List<String> readOutputFile(){
        List<String> output = new ArrayList<String>();
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
        return output;
    }

}
