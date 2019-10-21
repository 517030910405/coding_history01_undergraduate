import java.util.ArrayList;

public class string_const {
    public static ArrayList<Integer> toArray(String str){
        ArrayList<Integer> ans = new ArrayList<Integer>();
        byte [] sstr = str.getBytes();

        for (int i=1;i<str.length()-1;++i){
            if (sstr[i]=='\\'){
                if (sstr[i+1]=='\\'){
                    ans.add((int)'\\');
                }
                else if (sstr[i+1]=='t'){
                    ans.add((int)'\t');
                }
                else if (sstr[i+1]=='n'){
                    ans.add((int)'\n');
                }
                else if (sstr[i+1]=='r'){
                    ans.add((int)'\r');
                }
                else if (sstr[i+1]=='\"'){
                    ans.add((int)'\"');
                }
                ++i;
            } else{
                ans.add((int)sstr[i]);
            }
        }
        return ans;
    }
    public static String  toString(ArrayList<Integer> str){
        char [] sstr = new char[str.size()];
        for (int f1=0;f1<str.size();++f1){
            sstr[f1] = (char)((int) str.get(f1));
        }
        String ans = String.copyValueOf(sstr);
        return ans;
    }
    public static String toDataString(ArrayList<Integer> str){
        String ans="";
        int len = str.size();
        str.add(0);
        while (str.size()%8!=0) str.add(0);
        long st = 0;
        for (int f1=str.size()-1;f1>=0;--f1){
            {
                st = (st<<8)+((int)str.get(f1));
            }
            if (f1%8==0){
                ans = ","+myHex.LongToHex(st)+ans;
                st = 0;
                //if (f1!=0) ans = ','+ans;
            }
        }
        ans = myHex.LongToHex((long)len) + ans;
        return ans;

    }

    public static String naming_strategy(int i){
        return "str_const_"+i;
    }
}
