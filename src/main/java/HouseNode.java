/**
 * Created by allen on 12/11/15.
 */
public class HouseNode {
    /*
    {   "prev": 631,
        "value": 856,
        "next": 114
    }
    */

    private int prev;
    private int value;
    private int next;

    public int getPrev() {
        return prev;
    }

    public void setPrev(int prev) {
        this.prev = prev;
    }

    public int getNext() {
        return next;
    }

    public void setNext(int next) {
        this.next = next;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isMax(){
        if(value == Math.max(Math.max(prev, value), next)){
            return true;
        }else{
            return false;
        }
    }
}
