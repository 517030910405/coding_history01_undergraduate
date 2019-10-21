import java.util.*;
public class codeGen_rename {
	public static String renaming(String str){
		String ans = "";
		for (int i = 0; i< str.length(); ++i){
			String sub = str.substring(i,i+1);
			if (sub.equals(".")){
				ans += "_1";
			} else if (sub.equals("_")){
				ans += "_0";
			} else {
				ans += sub;
			}
		}
		if (ans.equals("main")) return ans;
		return ans+"_";
//		return "ERROR";
	}



}
