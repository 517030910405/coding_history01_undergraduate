import java.util.*;
public class simple_instr {

	public String name = "";// ++ + - load store =      tag     function
	public String type = "";// lr r l                   tag     function
	public int tag_num = -1;
	public static int tag_cnt = 0;
	//public int func_num = -1;

	public String func_num = "";
	public simple_addr addr1 = null;
	public simple_addr addr2 = null;
	public simple_addr addr3 = null;
	//this is the triple address IR

	public void malloc_instr(simple_addr res,simple_addr len){
		/**
			len is the wanted len of malloc
		    malloc 8*len
		**/
		name = "malloc x8";
		type = "lr";
		addr1 = res;
		addr2 = len;
	}
	public void setTag(){
		name = "Tag";
		tag_cnt += 1;
		tag_num = tag_cnt;
	}
	public void setFunc(String func_name,long cnt){
		name = "Func";
		func_num = func_name;
		addr2 = simple_addr.val_new(cnt);
	}

	public String view(){
		if (name.equals("Tag")){
			return name+",\t"+tag_num+"\n";
		}
		if (name.equals("Func")){
			return "\n"+name+",\t"+func_num+",\t"+addr2.view()+"\n";
		}
		if (name.equals("Return")){
			return "Return"+",\t"+addr2.view()+"\n";
		}

		String ans = name+",\t"+type+",\t";
		if (addr1!=null){
			ans += addr1.view()+",\t";
		} else {
			ans += "(None)\t";
		}
		if (addr2!=null){
			ans += addr2.view()+",\t";
		} else {
			ans += "(None)\t";
		}
		if (addr3!=null){
			ans += addr3.view()+",\t";
		} else {
			ans += "(None)\t";
		}
		return ans+"\n";
	}

}
