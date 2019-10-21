import java.util.*;

public class codeGen_Reg {
	public static String ConstReg1 = "rax";
	public static String ConstReg2 = "rdx";
	public static String ConstReg3 = "rcx";
	public static String RetReg = "rax";
	public static int oth_tag = 0;
	public static String give_new_tag(){
		++oth_tag;
		return "tag2_"+oth_tag;
	}

	public static ArrayList<Integer> myreg = new ArrayList<Integer>(){{
//		add(0);//rax
//		add(1);//rcx
//		add(2);//rdx
		add(3);
//		add(4);//rsp
//		add(5);//rbp
		add(6);
		add(7);
		add(8);
		add(9);
		add(10);
		add(11);
		add(12);
		add(13);
		add(14);
		add(15);

//		rax div rdx = rax
//		rax mod rdx = rdx
//		rax mul rdx = rax
//		div rdx   /   mul rdx
	}};
	public static ArrayList<String> public_reg_name = new ArrayList<String>(){{
		add("rax");
		add("rcx");
		add("rdx");
		add("rbx");
		add("rsp");
		add("rbp");
		add("rsi");
		add("rdi");
		add("r8");
		add("r9");
		add("r10");
		add("r11");
		add("r12");
		add("r13");
		add("r14");
		add("r15");
	}};

	public static ArrayList<String> push_all(int num,HashSet<Integer> reguse){
		ArrayList<String> ans = new ArrayList<String>();
		if (num>myreg.size()){
			num = myreg.size();
		}
		ans.add(";push all todo");
		for (int f1=0;f1<num;++f1)if (reguse.contains(f1)){
			//ans.add("push "+public_reg_name.get(myreg.get(f1)));
			ans.add("mov qword [ rbp - "+((f1+2)*8)+"], "+R(f1+1));
		}
		ans.add(";push all done");
		return ans;
	}
	public static ArrayList<String> pop_all(int num,HashSet<Integer> reguse){
		ArrayList<String> ans = new ArrayList<String>();
		if (num>myreg.size()){
			num = myreg.size();
		}
		ans.add(";pop all todo");
		for (int f1=0;f1<num;++f1)if (reguse.contains(f1)){
			//ans.add("push "+public_reg_name.get(myreg.get(f1)));
			ans.add("mov "+R(f1+1)+",qword [ rbp - "+((f1+2)*8)+"]");
		}
		ans.add(";pop all done");
		return ans;
	}

	public static String R(int num){
		if (num <= myreg.size()){
			return public_reg_name.get(myreg.get(num-1));
		} else{
			return " qword [rbp - "+((num+1)*8)+" ] ";
		}
	}
	public static int R_type(int num){
		if (num <= myreg.size()){
			return 1;
		} else{
			return 2;
		}
	}

}
