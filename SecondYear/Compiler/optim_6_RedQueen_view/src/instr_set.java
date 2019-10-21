import java.util.*;
public class instr_set {

    public ArrayList<simple_instr> list = new ArrayList<simple_instr>();
    public simple_addr ret_value = null;

    //ret_value is for expression

//    public instr_set next1 = null;//if jal is false   // if not jal
//    public instr_set next2 = null;//if jal is true
//    public instr_set last1 = null;//the last instr_set in the list
//    public instr_set last2 = null;//the last instr_set in the list
    public int set_id = 0;
    public static int set_cnt = 0;


    public instr_set(){
        set_cnt = set_cnt+1;
        set_id = set_cnt;
    }
    public simple_instr get(int i){
        return list.get(i);
    }
    public int size(){
        return list.size();
    }
    public String view(){
        String ans="";
        for (int i=0;i<list.size();++i){
            simple_instr item = list.get(i);
            ans += i+"\t\t"+item.view();
        }
        return ans;
    }



}
