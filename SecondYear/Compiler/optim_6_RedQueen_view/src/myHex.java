public class myHex {
    public static String LongToHex(long i){
        String res = Long.toHexString(i);
        String ans = "";
        while (ans.length()+res.length()<16){
            ans += "0";
        }
        return ans + res+"H";
    }
    public static String IntToHex(int i){
        String res = Integer.toHexString(i);
        String ans = "";
        while (ans.length()+res.length()<8){
            ans += "0";
        }
        return ans + res+"H";
    }
}
