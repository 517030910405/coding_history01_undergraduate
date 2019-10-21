import java.util.*;

public class IR_fixer_in_mem {
	public static ArrayList<instr_set> IR_List = null;
	public static int []vis = null;

	//first dim is function overall num
	//second dim is function mem num
	public static HashMap<Integer,Integer> mem_loc_info = new HashMap<Integer, Integer>();
	public static HashMap<Integer,Integer> mem_loc_info2 = new HashMap<Integer, Integer>();

	public static void dfs(int i){
		if (vis[i]==1) return;
		vis[i]=1;
		instr_set IR = IR_List.get(i);
		for (simple_instr item : IR.list){
			if (item.name.equals("call")){
				for (int j=0;j< IR_List.size();++j){
					if (IR_List.get(j).list.get(0).func_num.equals(item.addr2.name)){
						dfs(j);
					}
				}
			}
		}
	}
	public static boolean check(){
		for (int i=0;i<IR_List.size();++i)if (vis[i]==1){
			System.err.print(i+" ");
			System.err.println();
			instr_set IR = IR_List.get(i);
			for (simple_instr instr: IR.list ){
				{
					simple_addr addr = instr.addr1;
					if (addr != null && addr.type.equals("Var") && addr.num == 0) {
						return false;
					}
					if (addr != null && addr.type.equals("Mem x8")){
						return false;
					}
				}
				{
					simple_addr addr = instr.addr2;
					if (addr != null && addr.type.equals("Var") && addr.num == 0) {
						return false;
					}
					if (addr != null && addr.type.equals("Mem x8")){
						return false;
					}
				}
				{
					simple_addr addr = instr.addr3;
					if (addr != null && addr.type.equals("Var") && addr.num == 0) {
						return false;
					}
					if (addr != null && addr.type.equals("Mem x8")){
						return false;
					}
				}
				//global var return false;
				{
					if (instr.name.equals("println")){
						return false;
					}
					if (instr.name.equals("print")){
						return false;
					}
					if (instr.name.equals("getString")){
						return false;
					}
					if (instr.name.equals("getInt")){
						return false;
					}
					if (instr.name.equals("malloc x8")){
						return false;
					}
				}
			}
		}
		return true;
	}
	public static boolean check_program(){
		IR_List = pro1.IR_List;
		for (int i=0;i<IR_List.size();++i){
			//System.err.print(i+" ");
			//System.err.println();
			instr_set IR = IR_List.get(i);
			for (simple_instr instr: IR.list ){
				//global var return false;
				{
					if (instr.name.equals("getString")){
						return false;
					}
					if (instr.name.equals("getInt")){
						return false;
					}
				}
			}
		}
		return true;
	}
//	one input function fix
	public static void run(){

		for (int i=1;i<IR_List.size();++i)if (!IR_List.get(i).list.get(0).func_num.equals("main")){
			vis = new int [IR_List.size()];
			for (int j=0;j<IR_List.size();++j) {
				vis[j]=0;
			}
			dfs(i);
			if (check()){
				System.err.println(i+" is valid");
				int self_call_cnt=0;
				for (simple_instr instr: IR_List.get(i).list){
					if (instr.name.equals("call")&&instr.addr2.name.equals(IR_List.get(i).list.get(0).func_num))
						++self_call_cnt;
				}
				if (self_call_cnt>=1&&IR_List.get(i).list.get(0).addr2.num-2==1){
					mem_loc_info.put(i,mem_loc_info.size()+mem_loc_info2.size());
				}
//				if (self_call_cnt==0&&IR_List.get(i).list.get(0).addr2.num-2==1&&IR_List.get(i).list.get(0).func_num.equals("f")){
//					mem_loc_info.put(i,mem_loc_info.size()+mem_loc_info2.size());
//				}
				if (self_call_cnt>=0&&IR_List.get(i).list.get(0).addr2.num-2==2){
					mem_loc_info2.put(i,mem_loc_info.size()+mem_loc_info2.size());
				}
			} else{
				System.err.println(i+" is not valid");
			}
		}
		System.err.println(mem_loc_info);
		System.err.println(mem_loc_info2);
	}


}
