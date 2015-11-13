import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * Created by allen on 13/11/15.
 */

public class SmartRobberTest {

    @Test
    public void noAdjacentHouseCanBeRobbed() {

        SmartRobber.main();
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
        }

        // assert statements no two "YES" as neighbor
        assertFalse(output.get(0).trim().equals("YES") && output.get(output.size()-1).trim().equals("YES"));
        for(int i = 1; i<output.size()-1; i++){
            assertFalse(output.get(i).trim().equals("YES") && output.get(i+1).trim().equals("YES"));
        }
    }
}
