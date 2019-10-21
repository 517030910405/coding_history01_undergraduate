import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.rowset.internal.WebRowSetXmlReader;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;
import java.net.Inet4Address;
import java.util.*;
public class pro1 {
	public static OutputStream os1,os2 ,os3;//new FileOutputStream("result.asm");
	public static node root1;
	public static ArrayList<simple_instr> break_tag = new ArrayList<simple_instr>();
	public static ArrayList<simple_instr> continue_tag = new ArrayList<simple_instr>();
	public static instr_set IR_ = new instr_set();
	public static ArrayList<instr_set> IR_List = new ArrayList<instr_set>();
	public static HashMap<Integer,Integer> tag_line_num ;
	public static HashSet<Integer> visited;
	public static HashSet<Integer> visited2;
	public static ArrayList<HashSet<Integer>> line_next,line_last;
	public static int maxscope;
	//public static int register_cnt=0;
	public static ArrayList <node> scope_info = new ArrayList <node>();
	public static String move_addr2_cache = "";
	public static HashMap<String,Integer> RegNum ;
	public static ArrayList<simple_addr> RegList ;
	public static ArrayList<HashSet<Integer>> RegOccupyList;
	public static ArrayList<String> glob_list = new ArrayList<String>();
//	public static HashMap<String,Integer> VarNum;
//	public static ArrayList<simple_addr> VarList;

//	public static int [] VarColor;
	public static int [] RegColor;
	public static int colornum;
	public static ArrayList<HashSet<Integer>> UsedLine ;
	public static ArrayList<HashSet<Integer>> MakedLine;
	public static ArrayList<HashSet<Integer>> KeepedLine;
	public static ArrayList<String> nasm_code;

	public static ArrayList<String> nasm_global_data = new ArrayList<String>();
	//return with too many arguments CE
	//assign is not atom
	public static void dfs(node now,int i)throws Exception{
		for (int f1=0;f1<i;++f1){
			//System.err.print("    ");
		}
		//System.err.print('(');
//		System.err.print(i);
//		System.err.print(',');
//		System.err.print(now.type);
//		System.err.print(',');
//		System.err.print(now.name);
		if (!now.data_type.equals("")) {
//			System.err.print("[" + now.data_type + "]");
		}
		//System.err.print(',');
//		System.err.println();
		for (node item:now.son){
			dfs(item,i+1);
		}
		if (now.type.equals("return")){
			if (now.son.size()>1){
				System.err.println();
				System.err.println("CE");
				System.err.println();
				throw new Exception("CE_return");
			}
		}
		if (now.type.equals("self_function")){
			//if (now.son.size()!=0){
			//Not Defined
			//}
		}
		if (now.type.equals("variable")){
			System.err.println(now.son.get(1).name);
			if (!now.son.get(1).type.equals("atom")){
				throw new Exception("Variable Declaim CE");
			}
		}
		if (now.type.equals("input_variable")){
			if (!now.son.get(1).type.equals("atom")){
				throw new Exception("Variable Declaim CE");
			}
		}
		//System.err.print(now.type);
		//System.err.print(',');
		//System.err.print(now.name);
		//System.err.print(')');
	}
	//global function & class name
	public static void dfs1(node now,int i) throws Exception{
		if (now.type.equals("class")){
			varname newname = new varname();
			newname.type = "class";
			newname.name = now.name;
			newname.location = now;
			if (root1.scope.containsKey(now.name)){
				throw new Exception("Class Name");
			}
			root1.scope.put(now.name,newname);
			//System.err.println(now.name);
		}
		if (now.type.equals("function")){
			varname newname = new varname();
			newname.type = "function";
			newname.name = now.name;
			newname.location = now;
			if (root1.scope.containsKey(now.name)){
				throw new Exception("Function Name");
			}
			root1.scope.put(now.name,newname);
		}
		//System.err.println(now.type);
		if (i<=0){
			for (node item:now.son){
				dfs1(item,i+1);
			}
		}
	}
	//sub function name
	public static void dfs2(node now,int i) throws Exception{
		if (now.type.equals("class")){
			for (node item:now.son){
				if (item.type.equals("function")){
					varname newname = new varname();
					newname.type = "function";
					newname.name = item.name;
					newname.location = item;
					//System.err.println(item.name);
					if (now.scope.containsKey(newname.name)){
						throw new Exception("CE_Name_Function");
					}
					now.scope.put(newname.name,newname);
				}

				if (item.type.equals("self_function")){
					//Nothing to do
					if (!item.name.equals(now.name)){
						throw new Exception("self_function_name2");
					}
					now.has_self_function = true;
				}
			}
		}
		//System.err.println(now.type);
		if (i<=0){
			for (node item:now.son){
				dfs2(item,i+1);
			}
		}
	}
	//input of function
	public static void dfs3(node now,int i) throws Exception{
		if (now.type.equals("class")){
			//System.err.println(now.name);
		}
		if (now.type.equals("function")){
			{
				node nowson = now.son.get(0);
				int arrdim = 0;
				while (nowson.name.equals("array")){
					arrdim = arrdim+1;
					nowson = nowson.son.get(0);
				}
				now.output_variable_type = nowson.name;
				now.output_variable_array_dim = arrdim;
				if (!root1.scope.containsKey(nowson.name)){
					throw new Exception(nowson.name+": Type Not Defined");
				}
				varname thistype = root1.scope.get(nowson.name);
				if (!thistype.type.equals("class")){
					throw new Exception(nowson.name+"Is Not A Type");
				}
			}
			for (int j=1;j<now.son.size()-1;j=j+1){
				if (now.name.equals("main")&&i==1){
					//System.out.println(i);
					throw new Exception("main should have no input");
				}
				int arrdim = 0;
				node nowson = now.son.get(j).son.get(0);
				while (nowson.name.equals("array")){
					arrdim = arrdim+1;
					nowson = nowson.son.get(0);
				}
				now.input_variable_type.add(nowson.name);
				now.input_variable_array_dim.add(arrdim);
				if (!root1.scope.containsKey(nowson.name)){
					throw new Exception(nowson.name+": Type Not Defined");
				}
				varname thistype = root1.scope.get(nowson.name);
				if (!thistype.type.equals("class")){
					throw new Exception(nowson.name+"Is Not A Type");
				}
				//System.err.println(nowson.name);
			}
		}
		//System.err.println(now.type);
		if ((i<=0)||(now.type.equals("class"))){
			for (node item:now.son){
				dfs3(item,i+1);
			}
		}
	}
	//all scope
	public static void dfs4(node now,int i) throws Exception{
		if (now.has_scope){
			scope_info.add(now);
		}
		if (now.type.equals("variable")||now.type.equals("input_variable")){
			if (now.son.get(1).data_type.equals("int")||now.son.get(1).data_type.equals("string")){
				throw new Exception("Variable is not const");
			}
			varname newname = new varname();
			newname.name = now.son.get(1).name;
			if (newname.name.equals("this")){
				throw new  Exception("invalid variable name 5");
			}
			node nowtype = now.son.get(0);
			int arrdim=0;
			while (nowtype.name.equals("array")){
				arrdim = arrdim +1;
				nowtype = nowtype.son.get(0);
			}
			newname.array_dim = arrdim;
			newname.type = nowtype.name;
			newname.location = now;
			if (!root1.scope.containsKey(newname.type)){
				System.err.println();
				System.err.println(newname.type);
				throw new Exception("Invalid Type");
			}
			if (!root1.scope.get(newname.type).type.equals("class")){
				throw new Exception("Invalid Type");
			}
			if (scope_info.get(scope_info.size()-1).scope.containsKey(newname.name)){
				throw new Exception("Variable Name Invalid");
			}
			{
				//System.err.print("");
				/*System.err.print(newname.name);
				System.err.print(" ");
				System.err.print(newname.type);
				System.err.print(" ");
				System.err.print(newname.array_dim);
				System.err.println();*/
			}
			if (scope_info.get(0).scope.containsKey(newname.name)&&
					(scope_info.get(0).scope.get(newname.name).type.equals("class")
							||scope_info.get(0).scope.get(newname.name).type.equals("function"))){
				//throw new Exception("Variable Name Invalid 2");
			}
			if (scope_info.size()>=2&&scope_info.get(1).scope.containsKey(newname.name)&&
					(scope_info.get(1).scope.get(newname.name).type.equals("function"))){
				//throw new Exception("Variable Name Invalid 3");
			}
			scope_info.get(scope_info.size()-1).scope.put(newname.name,newname);
		}
		for (node item:now.son){
			dfs4(item,i+1);
		}
		if (now.has_scope) {
			scope_info.remove(scope_info.size() - 1);
		}
	}
	//expression's type
	public static void dfs5(node now,int i) throws Exception{
		//head
		if (now.has_scope){
			scope_info.add(now);
		}
		System.err.print(now.type);
		System.err.print(" ");
		System.err.print(now.name);
		System.err.print(" ");
		System.err.println(scope_info.size());
		if (now.type.equals("variable")|| now.type.equals("input_variable")){
			//scope_info.get(scope_info.size()-1).scope.get(now.son.get(1).name).activate =true;
			//Nothing to do
		}

		//middle

		if (now.type.equals("variable")|| now.type.equals("input_variable")) {
			for (int j = 0;j < now.son.size(); ++j){
				if (j!=1){
					dfs5(now.son.get(j),i+1);
				}
			}
		} else
		if (now.type.equals("expression")&&now.name.equals("function")&&now.son.get(0).type.equals("atom")){
			for (int j = 1;j < now.son.size(); ++j){
				if (j!=0){
					dfs5(now.son.get(j),i+1);
				}
			}
		} else
		if (now.type.equals("expression")&&now.name.equals("sub")){
			if (!now.son.get(1).type.equals("atom")){
				throw new Exception("Sub is too Complicated");
			}
			dfs5(now.son.get(0),i+1);
		} else{
			for (node item:now.son){
				dfs5(item,i+1);
			}
		}
		//tail
		if (now.type.equals("atom")){
			if (now.name.equals("this")){
				if (scope_info.size()>=3&&scope_info.get(1).type.equals("class")
						&&(scope_info.get(2).type.equals("function")||
						scope_info.get(2).type.equals("self_function"))){
					now.data_type = scope_info.get(1).name;
					now.data_array_dim = 0;
				}
			}
			if (now.data_type.equals("")){
				int j=scope_info.size()-1;
//				System.err.println(now.name);
				while (j>=0&&((!scope_info.get(j).scope.containsKey(now.name))
						||(!scope_info.get(j).scope.get(now.name).activate))){
					j=j-1;
				}
				if (j==-1){
					throw new Exception("Variable Not Defined");
				}
				now.data_type = scope_info.get(j).scope.get(now.name).type;
				now.data_array_dim = scope_info.get(j).scope.get(now.name).array_dim;
				now.left_value = true;
			}
		}
		if (now.type.equals("expression")){
			if (now.name.equals("+")&&now.expr_type.equals("lr")){
				if (now.son.get(0).data_type.equals("int")&&now.son.get(1).data_type.equals("int")
						&&now.son.get(0).data_array_dim==0&&now.son.get(1).data_array_dim==0){
					now.data_type = "int";
					now.data_array_dim = 0;
					now.left_value = false;
				}else if (now.son.get(0).data_type.equals("string")&&now.son.get(1).data_type.equals("string")
						&&now.son.get(0).data_array_dim==0&&now.son.get(1).data_array_dim==0){
					now.data_type = "string";
					now.data_array_dim = 0;
					now.left_value = false;
				}else {
					System.err.println("["+now.son.get(0).data_type+"]");
					throw new Exception("Expression Type Error: operator = "+now.name);
				}
			} else
			if (now.name.equals("-")&&now.expr_type.equals("lr")) {
				if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim == 0
						&& now.son.get(1).data_type.equals("int")&&now.son.get(1).data_array_dim == 0){
					now.data_type = "int";
					now.data_array_dim = 0;
					now.left_value = false;
				} else{
					throw new Exception("Expression Type Error: operator = "+now.name);
				}
			} else
			if (now.name.equals("+")&&now.expr_type.equals("r")) {
				if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim == 0){
					now.data_array_dim = 0;
					now.data_type = "int";
					now.left_value = false;
				} else{
					throw new Exception("Expression Type Error: operator = "+now.name);
				}
			} else
			if (now.name.equals("-")&&now.expr_type.equals("r")) {
				if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim == 0){
					now.data_array_dim = 0;
					now.data_type = "int";
					now.left_value = false;
				} else{
					throw new Exception("Expression Type Error: operator = "+now.name);
				}
			} else
			if (now.name.equals("*")||now.name.equals("/")||now.name.equals("%")||
					now.name.equals("&")||now.name.equals("|")||now.name.equals("^")
					||now.name.equals("<<")||now.name.equals(">>")){
				if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim == 0
						&& now.son.get(1).data_type.equals("int")&&now.son.get(1).data_array_dim == 0){
					now.data_type = "int";
					now.data_array_dim = 0;
					now.left_value = false;
				} else{
					throw new Exception("Expression Type Error: operator = "+now.name);
				}
			} else
			if (now.name.equals("~")){
				if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim == 0){
					now.data_type = "int";
					now.data_array_dim = 0;
					now.left_value = false;
				} else{
					throw new Exception("Expression Type Error: operator = "+now.name);
				}
			} else
			if (now.name.equals("&&")||now.name.equals("||")){
				if (now.son.get(0).data_type.equals("bool")&&now.son.get(0).data_array_dim == 0
						&& now.son.get(1).data_type.equals("bool")&&now.son.get(1).data_array_dim == 0){
					now.data_type = "bool";
					now.data_array_dim = 0;
					now.left_value = false;
				} else{
					throw new Exception("Expression Type Error: operator = "+now.name);
				}
			} else
			if (now.name.equals("!")){
				if (now.son.get(0).data_type.equals("bool")&&now.son.get(0).data_array_dim == 0){
					now.data_type = "bool";
					now.data_array_dim = 0;
					now.left_value = false;
				} else{
					throw new Exception("Expression Type Error: operator = "+now.name);
				}
			} else
			if (now.name.equals("<")||now.name.equals(">")||now.name.equals("<=")||now.name.equals(">=")){
				if (now.son.get(0).data_type.equals("int")&&now.son.get(1).data_type.equals("int")
						&&now.son.get(0).data_array_dim==0&&now.son.get(1).data_array_dim==0){
					now.data_type = "bool";
					now.data_array_dim = 0;
					now.left_value = false;
				}else if (now.son.get(0).data_type.equals("string")&&now.son.get(1).data_type.equals("string")
						&&now.son.get(0).data_array_dim==0&&now.son.get(1).data_array_dim==0){
					now.data_type = "bool";
					now.data_array_dim = 0;
					now.left_value = false;
				}else {
					throw new Exception("Expression Type Error: operator = "+now.name);
				}
			} else
			if (now.name.equals("==")||now.name.equals("!=")){
				if (now.son.get(0).data_type.equals(now.son.get(1).data_type)
						&&now.son.get(0).data_array_dim==now.son.get(1).data_array_dim){
					now.data_type = "bool";
					now.data_array_dim = 0;
					now.left_value = false;
				}else if (now.son.get(1).data_type.equals("null")&&now.son.get(1).data_array_dim==0
						&&(!((now.son.get(0).data_type.equals("int")||now.son.get(0).data_type.equals("string"))
						&&now.son.get(0).data_array_dim==0))){
					now.data_type = "bool";
					now.data_array_dim = 0;
					now.left_value = false;
				}else if (now.son.get(0).data_type.equals("null")&&now.son.get(0).data_array_dim==0
						&&(!((now.son.get(1).data_type.equals("int")||now.son.get(1).data_type.equals("string"))
						&&now.son.get(1).data_array_dim==0))){
					now.data_type = "bool";
					now.data_array_dim = 0;
					now.left_value = false;
				}else {
					throw new Exception("Expression Type Error: operator = "+now.name);
				}
			} else
			if (now.name.equals("++")||now.name.equals("--")) {
				if (now.son.get(0).left_value){
					if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim==0){
						now.data_type = "int";
						now.data_array_dim = 0;
						now.left_value = (now.expr_type.equals("r"));
					}else {
						throw new Exception("Expression Type Error: operator = "+now.name);
					}
				}else{
					throw new Exception("++/-- Not a Variable! ");
				}
			} else
			if (now.name.equals("sub")){
				String typea =now.son.get(0).data_type;
				int dima = now.son.get(0).data_array_dim;
				String nameb = now.son.get(1).name;
				varname namespace = root1.scope.get(typea);
				if (!namespace.type.equals("class")){
					throw new Exception("Not a class");
				}
				if (dima!=0){
					if (!nameb.equals("size")) {
						throw new Exception("sub Error");
					}
				} else {
					if (typea.equals("string")&&nameb.equals("length")){
						now.data_type = "function";
						now.left_value = now.son.get(0).left_value;
						now.data_array_dim = 0;
					} else
					if (typea.equals("string")&&nameb.equals("ord")){
						now.data_type = "function";
						now.left_value = now.son.get(0).left_value;
						now.data_array_dim = 0;
					} else
					if (typea.equals("string")&&nameb.equals("substring")){
						now.data_type = "function";
						now.left_value = now.son.get(0).left_value;
						now.data_array_dim = 0;
					} else
					if (typea.equals("string")&&nameb.equals("parseInt")){
						now.data_type = "function";
						now.left_value = now.son.get(0).left_value;
						now.data_array_dim = 0;
					} else {
						if (namespace.location==null){
							throw new Exception(nameb + " is not a sub of " + typea);
						}
						if (!namespace.location.scope.containsKey(nameb)) {
							throw new Exception(nameb + " is not a sub of " + typea);
						}
						now.data_type = namespace.location.scope.get(nameb).type;
						now.data_array_dim = namespace.location.scope.get(nameb).array_dim;
						now.left_value = true;
						//now.left_value = now.son.get(0).left_value;
						//examine_tag1
					}
				}
			} else
			if (now.name.equals("=")){
				if (!now.son.get(0).left_value) {
					throw new Exception("= is not available1");
				}
				if (!((now.son.get(0).data_type.equals("int")||now.son.get(0).data_type.equals("string"))&&
						now.son.get(0).data_array_dim == 0)&&now.son.get(1).data_type.equals("null")) {

				} else{
					if (!(now.son.get(0).data_type.equals(now.son.get(1).data_type)
							&& now.son.get(0).data_array_dim == now.son.get(1).data_array_dim)) {
						System.err.println("["+now.son.get(0).data_type+"] ["+now.son.get(1).data_type+"]");
						throw new Exception("= is not available2");
					}
				}
				now.data_array_dim = 0;
				now.data_type = "void";
				now.left_value = false;
			} else
			if (now.name.equals("array")){
				if (now.son.size()!=2){
					throw new Exception("Array Missing");
				}
				if (now.son.get(0).data_array_dim>0&&now.son.get(1).data_type.equals("int")
						&&now.son.get(1).data_array_dim==0){
					now.data_type = now.son.get(0).data_type;
					now.data_array_dim = now.son.get(0).data_array_dim -1;
					now.left_value = true;
					//examine_tag3
					//now.left_value = now.son.get(0).left_value;
				} else {
					throw new Exception("Array Error");
				}
			} else
			if (now.name.equals("function")){
				if (now.son.get(0).name.equals("sub")){
					if (now.son.get(0).son.get(0).data_array_dim>0&&
							now.son.get(0).son.get(1).name.equals("size")){
						if (now.son.size()!=1){
							throw new Exception("array.size() Error");
						}
						now.data_type = "int";
						now.data_array_dim = 0;
						now.left_value = false;
					} else
					if (now.son.get(0).son.get(0).data_array_dim==0 &&now.son.get(0).son.get(0).data_type.equals("string")
							&&now.son.get(0).son.get(1).name.equals("length")){
						//if (!(now.son.get(0).son.get(0).data_array_dim==0&&now.son.get(0).son.get(0).data_type.equals("string"))){
						//    throw new Exception("length Error");
						//}
						if (now.son.size()!=1){
							throw new Exception("string.length() Error");
						}
						now.data_type = "int";
						now.data_array_dim = 0;
						now.left_value = false;
					} else
					if (now.son.get(0).son.get(0).data_array_dim==0 &&now.son.get(0).son.get(0).data_type.equals("string")
							&&now.son.get(0).son.get(1).name.equals("substring")){
						//if (!(now.son.get(0).son.get(0).data_array_dim==0&&now.son.get(0).son.get(0).data_type.equals("string"))){
						//    throw new Exception("substring Error");
						//}
						if (!(now.son.get(1).data_array_dim==0&&now.son.get(1).data_type.equals("int"))){
							throw new Exception("substring Error");
						}
						if (!(now.son.get(2).data_array_dim==0&&now.son.get(2).data_type.equals("int"))){
							throw new Exception("substring Error");
						}
						if (now.son.size()!=3){
							throw new Exception("string.substring() Error");
						}
						now.data_type = "string";
						now.data_array_dim = 0;
						now.left_value = false;
					} else
					if (now.son.get(0).son.get(0).data_array_dim==0 &&now.son.get(0).son.get(0).data_type.equals("string")
							&&now.son.get(0).son.get(1).name.equals("parseInt")){
						//if (!(now.son.get(0).data_array_dim==0&&now.son.get(0).data_type.equals("string"))){
						//    throw new Exception("parseInt Error");
						//}
						if (now.son.size()!=1){
							throw new Exception("string.parseInt() Error");
						}
						now.data_type = "int";
						now.data_array_dim = 0;
						now.left_value = false;
					} else
					if (now.son.get(0).son.get(0).data_array_dim==0 &&now.son.get(0).son.get(0).data_type.equals("string")
							&&now.son.get(0).son.get(1).name.equals("ord")){
						//if (!(now.son.get(0).data_array_dim==0&&now.son.get(0).data_type.equals("string"))){
						//    throw new Exception("ord Error");
						//}
						if (!(now.son.get(1).data_array_dim==0&&now.son.get(1).data_type.equals("int"))){
							throw new Exception("ord Error");
						}
						if (now.son.size()!=2){
							throw new Exception("string.ord() Error");
						}
						now.data_type = "int";
						now.data_array_dim = 0;
						now.left_value = false;
					} else {
						node upperclass = now.son.get(0).son.get(0);
						node lowerclass = now.son.get(0).son.get(1);
						if (upperclass.data_array_dim != 0) {
							throw new Exception("function Error");
						}
						if (!now.son.get(0).data_type.equals("function")) {
							throw new Exception("function Error");
						}
						node func_space = root1.scope.get(upperclass.data_type).location.
								scope.get(lowerclass.name).location;
						if (func_space.input_variable_type.size() + 1 != now.son.size()) {
							throw new Exception("function Error1");
						}
						for (int j = 0; j < func_space.input_variable_type.size(); ++j) {
							if (!(func_space.input_variable_type.get(j).equals(now.son.get(j + 1).data_type) &&
									func_space.input_variable_array_dim.get(j) == now.son.get(j + 1).data_array_dim)) {
								if (!(now.son.get(j + 1).data_type.equals("null")&&now.son.get(j + 1).data_array_dim==0))
								throw new Exception("function Error2");
							}
						}
						now.data_type = func_space.output_variable_type;
						now.data_array_dim = func_space.output_variable_array_dim;
						now.left_value = false;
					}
				}else if (now.son.get(0).type.equals("atom")){
					node lowerclass = now.son.get(0);
					int j=scope_info.size()-1;
					/*if (scope_info.size()>1&&scope_info.get(1).scope.containsKey(lowerclass.name)){
						j=1;
					} else if (scope_info.get(0).scope.containsKey(lowerclass.name)){
						j=0;
					} else{
						throw new Exception("function Error3");
					}*/
					while (!scope_info.get(j).scope.containsKey(lowerclass.name)){
						j=j-1;
					}
					//Automatic throw if no function
					if (j==0&&now.son.get(0).name.equals("print")){
						if (now.son.size()!=2){
							throw new Exception("print Error");
						}
						if (!(now.son.get(1).data_type.equals("string")&&now.son.get(1).data_array_dim==0)){
							throw new Exception("print Error");
						}
						now.data_type = "void";
						now.data_array_dim = 0;
					} else
					if (j==0&&now.son.get(0).name.equals("println")){
						if (now.son.size()!=2){
							throw new Exception("println Error");
						}
						if (!(now.son.get(1).data_type.equals("string")&&now.son.get(1).data_array_dim==0)){
							throw new Exception("println Error");
						}
						now.data_type = "void";
						now.data_array_dim = 0;
					} else
					if (j==0&&now.son.get(0).name.equals("getString")){
						if (now.son.size()!=1){
							throw new Exception("getString Error");
						}
						now.data_array_dim = 0;
						now.data_type = "string";
					} else
					if (j==0&&now.son.get(0).name.equals("getInt")){
						if (now.son.size()!=1){
							throw new Exception("getInt Error");
						}
						now.data_array_dim = 0;
						now.data_type = "int";
					} else
					if (j==0&&now.son.get(0).name.equals("toString")){
						if (now.son.size()!=2){
							throw new Exception("toString Error");
						}
						if (!(now.son.get(1).data_type.equals("int")&&now.son.get(1).data_array_dim==0)){
							throw new Exception("toString Error");
						}
						now.data_array_dim = 0;
						now.data_type = "string";
					} else{
						node func_namespace = scope_info.get(j).scope.get(lowerclass.name).location;
						if (!func_namespace.type.equals("function")) {
							throw new Exception("function Error4");
						}
						if (func_namespace.input_variable_type.size() + 1 != now.son.size()) {
							throw new Exception("function Error5");
						}
						for (int k = 0; k < func_namespace.input_variable_type.size(); ++k) {
							if (!(func_namespace.input_variable_type.get(k).equals(now.son.get(k + 1).data_type) &&
									func_namespace.input_variable_array_dim.get(k) == now.son.get(k + 1).data_array_dim)) {
								//System.err.println(func_namespace.input_variable_type.get(k));
								//System.err.println(now.son.get(k + 1).data_type);
								if (!(now.son.get(k+1).data_type.equals("null")&&now.son.get(k + 1).data_array_dim==0))
								throw new Exception("function Error5");
							}
						}
						now.data_type = func_namespace.output_variable_type;
						now.data_array_dim = func_namespace.output_variable_array_dim;
						now.left_value = false;
					}
					//
				}
			} else
			if (now.name.equals("new")){
				node typea = now.son.get(0);
				int arr_dim = 0;
				boolean sign = false;
				while (typea.son.size()!=0){
					if (sign){
						if (typea.son.size()==1){
							throw new Exception("new Error1");
						}
						if (!typea.son.get(1).data_type.equals("int")){
							throw new Exception("new Error2");
						}
						arr_dim = arr_dim+1;
						typea = typea.son.get(0);
					}else{
						if (typea.son.size()==2){
							sign = true;
							if (!typea.son.get(1).data_type.equals("int")){
								throw new Exception("new Error3");
							}
						}
						arr_dim = arr_dim+1;
						typea = typea.son.get(0);
					}
				}
				if (!(root1.scope.containsKey(typea.name)&&root1.scope.get(typea.name).type.equals("class"))){
					throw new Exception("new Error4");
				}
				if (typea.name.equals("void")) {
					throw new Exception("new void Error");
				}
				if ((!sign)&&arr_dim>0){
					throw new Exception("Warning: no use \"new\"");
				}
				now.data_type = typea.name;
				//System.err.println("["+now.data_type+"]");
				now.data_array_dim = arr_dim;
				now.left_value = false;
			}
			else{
				throw new Exception("CE! I dont know");
			}

			//To be done
		}
		if (now.type.equals("variable")){
			if (scope_info.get(scope_info.size()-1).scope.get(now.son.get(1).name).type.equals("void")){
				throw new Exception("void variable Error");
			}
			if (now.son.size()==3){
				varname typea = scope_info.get(scope_info.size()-1).scope.get(now.son.get(1).name);
				if ((!((typea.type.equals("int")||typea.type.equals("string")||typea.type.equals("bool"))&&typea.array_dim==0))
						&&now.son.get(2).data_type.equals("null")){
				} else
				if (!(typea.type.equals(now.son.get(2).data_type)
						&&typea.array_dim == now.son.get(2).data_array_dim)){
					throw new Exception("variable Error");
				}
			}
		}
		if (now.type.equals("for")){
			if (now.son.get(1).type.equals("none")){
				//Nothing
			} else{
				if (now.son.get(1).data_type.equals("bool")&&now.son.get(1).data_array_dim==0){
					//Nothing
				} else{
					throw new Exception("not bool in for");
				}
			}
		}
		if (now.type.equals("while")){
			if (now.son.get(0).data_type.equals("bool")&&now.son.get(1).data_array_dim==0){
//				System.err.println("not bool");
				//Nothing
			} else{
				throw new Exception("not bool in while");
			}
		}
		if (now.type.equals("if")){
			if (now.son.get(0).data_type.equals("bool")&&now.son.get(1).data_array_dim==0){
				//Nothing
			} else{
				throw new Exception("not bool in if");
			}
		}
		//return check
		if (now.type.equals("return")) {
			int j;
			if (scope_info.get(1).type.equals("class")) {
				j=2;
			}
			else if (scope_info.get(1).type.equals("function")){
				j=1;
			}
			else{
				throw new Exception("CE: return1");
			}
			if (scope_info.get(j).type.equals("self_function")){
				if (now.son.size()==0){

				} else{
					throw new Exception("CE: return 4");
				}
			} else
			if (scope_info.get(j).output_variable_type.equals("void")){
				if(scope_info.get(j).output_variable_array_dim==0&&now.son.size()==0){

				} else throw new Exception("return in void func");
				//examine_tag2
			}else
			if (scope_info.get(j).output_variable_type.equals(now.son.get(0).data_type)){
				if (now.son.get(0).data_type.equals("void")){
					throw new Exception("CE: return void");
				}


				if (scope_info.get(j).output_variable_array_dim==now.son.get(0).data_array_dim){

				}
				else{
					throw new Exception("CE: return2");
				}
			}
			else if (now.son.get(0).data_type.equals("null")){
				if (scope_info.get(j).output_variable_type.equals("int")&&scope_info.get(j).output_variable_array_dim==0){
					throw new Exception("CE: return5");
				}
				if (scope_info.get(j).output_variable_type.equals("string")&&scope_info.get(j).output_variable_array_dim==0){
					throw new Exception("CE: return5");
				}

			}
			else {
				System.err.println("ERROR");
				System.err.println(scope_info.get(j).output_variable_type);
				System.err.println(now.son.get(0).data_type);
				throw new Exception("CE: return3");
			}
		}
		if (now.type.equals("continue")||now.type.equals("break")){
			int j = scope_info.size()-1;
			while (!(scope_info.get(j).type.equals("for")||scope_info.get(j).type.equals("while"))){
//				System.err.println(scope_info.get(j).type);
				j = j-1;
			}
			//Automatic CE if no for or while
		}
		if (now.type.equals("variable")|| now.type.equals("input_variable")){
			scope_info.get(scope_info.size()-1).scope.get(now.son.get(1).name).activate = true;
		}
		if (now.has_scope) {
			scope_info.remove(scope_info.size() - 1);
		}
	}
	//ir: class size check
	public static void dfs6(node now,int i) throws Exception{
		for (node item1: now.son){
			if (item1.type.equals("class")){
				//System.err.println(item1.name);
				for (node item2: item1.son){
					if (item2.type.equals("variable")) {
						varname item3 = item1.scope.get(item2.son.get(1).name);
						//System.err.println(item2.son.get(1).name);
						if (item3.type.equals("int") && item3.array_dim == 0) {
							item3.mem_position = item1.mem_size;
							item1.mem_size += 1;
						} else
						if (item3.type.equals("string") && item3.array_dim == 0) {
							item3.mem_position = item1.mem_size;
							item1.mem_size += 1;
						} else
						if (item3.array_dim > 0 ){
							//System.err.println(item3.array_dim);
							item3.mem_position = item1.mem_size;
							item1.mem_size += 1;
						} else {
							item3.mem_position = item1.mem_size;
							item1.mem_size += 1;
						}
						//System.err.println("(" + item3.type + " " + item3.name + ")");
					}
				}
				//System.err.println(item1.mem_size);
			}
		}
	}
	//this is the ir gen
	public static instr_set dfs7(node now,int i) throws Exception{
		//head
//		for (int k=0;k<i;++k) {
//			System.err.print("   .");
//		}
//		System.err.println(now.name);
		if (now.has_scope){
			scope_info.add(now);
		}
		if (now.type.equals("variable")|| now.type.equals("input_variable")){
			//scope_info.get(scope_info.size()-1).scope.get(now.son.get(1).name).activate =true;
			//Nothing to do
		}

		//middle
		if (now.type.equals("class")){
//			done
			instr_set ans = new instr_set();

			for (node item:now.son){
				if (item.type.equals("function")||item.type.equals("self_function")){
					instr_set res = dfs7(item,i+1);
					ans.list.addAll(res.list);
					IR_List.add(res);
				}
				else if (item.type.equals("variable")){
					scope_info.get(scope_info.size()-1).scope.get(item.son.get(1).name) .activate= true;
				}
			}
			if (now.has_scope){
				scope_info.remove(scope_info.size()-1);
			}
			return ans;
		}


		if (now.type.equals("none")){
			//done
			instr_set ans = new instr_set();
			ans.ret_value = simple_addr.val_new(1);
			return ans;
		}
		if (now.type.equals("MxLang")){
			//done
			instr_set ans = new instr_set();
			{
				simple_instr instr = new simple_instr();
				instr.setFunc("Init..init",2);
				ans.list.add(instr);
			}
			for (node item:now.son) if (item.type.equals("variable")){
				instr_set res = dfs7(item,i+1);
				ans.list.addAll(res.list);
			}
			{
				simple_instr instr = new simple_instr();
				instr.name = "Return";
				instr.addr2 = simple_addr.val_new(0);
				ans.list.add(instr);
			}
			instr_set an2 = new instr_set();
			an2.list.addAll(ans.list);
			IR_List.add(an2);

			for (node item:now.son) if (!item.type.equals("variable")){
				instr_set res = dfs7(item,i+1);
				ans.list.addAll(res.list);
				if (item.type.equals("function")){
					IR_List.add(res);
				}
			}
			if (now.has_scope){
				scope_info.remove(scope_info.size()-1);
			}
			return ans;
		}
		if (now.type.equals("function")||now.type.equals("self_function")){
//			System.err.println("visit function");
			instr_set ans = new instr_set();
			simple_instr functag = new simple_instr();
			if (i==2){
				if (now.type.equals("self_function"))
				functag.setFunc(scope_info.get(1).name+"."+now.name,now.son.size()+2);
				else
				functag.setFunc(scope_info.get(1).name+"."+now.name,now.son.size()+1);
			} else {
				functag.setFunc(now.name,now.son.size());
			}
			ans.list.add(functag);
			if (functag.func_num.equals("main")){
//				System.err.println("init here");
				simple_instr call_init = new simple_instr();
				call_init.name = "call";
				call_init.addr2 = simple_addr.func_new("Init..init");
				call_init.addr3 = simple_addr.val_new(0);
				ans.list.add(call_init);
			}

			if (i==2){
				simple_instr instr = new simple_instr();
				instr.name = "pop_function";
				instr.addr1 = simple_addr.var_new("this",2);
				instr.addr2 = simple_addr.val_new(0);
				ans.list.add(instr);
			}
			for (int j=1;j<=now.son.size()-2;++j){
				simple_instr instr = new simple_instr();
				instr.name = "pop_function";
				instr.addr1 = simple_addr.var_new(now.son.get(j).son.get(1).name,i);
				instr.addr2 = simple_addr.val_new(j+i-2);
				dfs7(now.son.get(j),i+1);
				//this is only for variable activation
				ans.list.add(instr);
			}
//			for (int j=1;j<now.son.size()-1;++j){
//				simple_instr instr = new simple_instr();
//				instr.name = "pop_function";
//				instr.addr1 = simple_addr.var_new(now.son.get(j).son.get(1).name,i);
//				instr.addr2 = simple_addr.val_new(j+i-2);
//				dfs7(now.son.get(j),i+1);
//				ans.list.add(instr);
//			}
			instr_set res = dfs7(now.son.get(now.son.size()-1),i+1);
			ans.list.addAll(res.list);
			simple_instr instr0 = new simple_instr();
			instr0.name = "Return";
			instr0.addr2 = simple_addr.val_new(0);
			ans.list.add(instr0);
			if (now.has_scope){
				scope_info.remove(scope_info.size()-1);
			}
			return ans;

		}
		if (now.type.equals("statements")){
			//System.err.println("visit sts");
			instr_set ans = new instr_set();
			for (node item:now.son){
				instr_set res = dfs7(item,i+1);
				if (res!=null) ans.list.addAll(res.list);
			}
			if (now.has_scope){
				scope_info.remove(scope_info.size()-1);
			}
			return ans;
		}else
		if (now.type.equals("variable")|| now.type.equals("input_variable")) {
//			System.err.println(now.son.get(1).name);
			varname var = scope_info.get(scope_info.size()-1).scope.get(now.son.get(1).name);
			if (now.son.size()==3&&now.type.equals("variable")){
				instr_set ans = new instr_set();
				instr_set res = dfs7(now.son.get(2),i+1);
				//System.err.println(now.son.get(1).name);
				//System.err.println(scope_info.size());
				var.activate = true;
				if (res!=null){
					ans.list.addAll(res.list);
					ans.ret_value = null;
					simple_instr instr = new simple_instr();
					instr.name = "=";
					instr.type = "lr";
					instr.addr1 = simple_addr.var_new(var.name,scope_info.size()-1);
					instr.addr2 = res.ret_value;
					ans.list.add(instr);
					return ans;
				}
				return ans;
			}
			else{
				instr_set ans = new instr_set();
				var.activate = true;
				simple_instr instr = new simple_instr();
				instr.name = "=";
				instr.type = "lr";
				instr.addr1 = simple_addr.var_new(var.name,scope_info.size()-1);
				instr.addr2 = simple_addr.val_new(0);
				ans.list.add(instr);
				return ans;
			}
		}
		//tail
		if (now.type.equals("atom")){
			if (now.name.equals("this")){
				instr_set ans = new instr_set();
				ans.ret_value = simple_addr.var_new("this",2);
				return ans;
			} else
			if (now.left_value){
				instr_set ans = new instr_set();
				int j = scope_info.size()-1;
//				System.err.println(now.name);

				while (scope_info.get(j).scope.get(now.name)==null
						||
						(!scope_info.get(j).scope.get(now.name).activate)
				){
					j = j-1;
				}
				if (scope_info.get(j).type.equals("class")){
					ans.ret_value = simple_addr.mem_offset(
							simple_addr.var_new("this",2),
							scope_info.get(j).scope.get(now.name).mem_position
					);
				}else {
					ans.ret_value = simple_addr.var_new(now.name, j);
				}
				return ans;
			}
			else

			if (now.data_type.equals("int")){
				instr_set ans = new instr_set();
				ans.ret_value = simple_addr.val_new(Integer.parseInt(now.name));
				return ans;
			} else
			if (now.data_type.equals("bool")){
				instr_set ans = new instr_set();
				//ans.ret_value = new simple_addr();
				if (now.name.equals("true") || now.name.equals("True")) {
						ans.ret_value = simple_addr.val_new(1);
				}
				else if (now.name.equals("false") || now.name.equals("False")) {
						ans.ret_value = simple_addr.val_new(0);
				}
				return ans;
			}else
			if (now.data_type.equals("string")){
				instr_set ans = new instr_set();
				ans.ret_value = simple_addr.string_const(now.name);
				return ans;

			}else
			if (now.data_type.equals("null")){
				instr_set ans = new instr_set();
				ans.ret_value = simple_addr.val_new(0);
				return ans;
			}else
			if (now.data_type.equals("void")) {
				//Nothing ro do
			}
			else{
				throw new Exception("IR Gen");
//				int j = scope_info.size()-1;
//				System.err.println(now.name);
//				while (scope_info.get(j).scope.get(now.name)==null
//						||
//						(!scope_info.get(j).scope.get(now.name).activate)
//				){
//					j = j-1;
//				}
//				instr_set ans = new instr_set();
//				ans.ret_value = simple_addr.var_new(now.name,j);
//				return ans;
			}
		}
		//todo here
		if (now.type.equals("expression")){
			if (now.name.equals("+")&&now.expr_type.equals("lr")){
				if (now.son.get(0).data_type.equals("int")&&now.son.get(1).data_type.equals("int")
						&&now.son.get(0).data_array_dim==0&&now.son.get(1).data_array_dim==0){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);
					ans.list.addAll(res1.list);
					ans.list.addAll(res2.list);
					simple_instr newinstr = new simple_instr();
					newinstr.name = "+";
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(4);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
				else if (now.son.get(0).data_type.equals("string")&&now.son.get(1).data_type.equals("string")
						&&now.son.get(0).data_array_dim==0&&now.son.get(1).data_array_dim==0){
//					todo
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);
					ans.list.addAll(res1.list);
					ans.list.addAll(res2.list);
					simple_instr newinstr = new simple_instr();
					newinstr.name = "string_+";
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(4);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
			} else
			if (now.name.equals("-")&&now.expr_type.equals("lr")) {
				if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim == 0
						&& now.son.get(1).data_type.equals("int")&&now.son.get(1).data_array_dim == 0){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = "-";
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(4);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
			} else
			if (now.name.equals("+")&&now.expr_type.equals("r")) {
				if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim == 0){
					return dfs7(now.son.get(0),i+1);
				}
			} else
			if (now.name.equals("-")&&now.expr_type.equals("r")) {
				if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim == 0){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
//					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
//					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = "-";
					newinstr.type = "r";
					newinstr.addr2 = res1.ret_value;
//					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(4);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
			} else
			if (now.name.equals("*")||now.name.equals("/")||now.name.equals("%")||
					now.name.equals("&")||now.name.equals("|")||now.name.equals("^")
					||now.name.equals("<<")||now.name.equals(">>")){
				if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim == 0
						&& now.son.get(1).data_type.equals("int")&&now.son.get(1).data_array_dim == 0){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = now.name;
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(4);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
			} else
			if (now.name.equals("~")){
				if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim == 0){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
//					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
//					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = "~";
					newinstr.type = "r";
					newinstr.addr2 = res1.ret_value;
//					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(4);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
			} else
			if (now.name.equals("&&")){
				if (now.son.get(0).data_type.equals("bool")&&now.son.get(0).data_array_dim == 0
						&& now.son.get(1).data_type.equals("bool")&&now.son.get(1).data_array_dim == 0){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);


					//simple_addr tag1 = simple_addr.tag_new();
					simple_instr tag1 = new simple_instr();
					tag1.setTag();
					//simple_addr tag1_addr = simple_addr.tag_new(tag1.tag_num);

					ans.list.addAll(res1.list);
					simple_instr instr0 = new simple_instr();
					instr0.name = "BZ";
					instr0.addr2 = res1.ret_value;
					instr0.addr1 = simple_addr.tag_new(tag1.tag_num);
					ans.list.add(instr0);


					ans.list.addAll(res2.list);
					ans.list.add(tag1);

					simple_instr newinstr = new simple_instr();
					newinstr.name = now.name;
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(1);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
			} else
			if (now.name.equals("||")){
				if (now.son.get(0).data_type.equals("bool")&&now.son.get(0).data_array_dim == 0
						&& now.son.get(1).data_type.equals("bool")&&now.son.get(1).data_array_dim == 0){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);

					simple_instr tag1 = new simple_instr();
					tag1.setTag();


					ans.list.addAll(res1.list);
					simple_instr instr0 = new simple_instr();
					instr0.name = "BNZ";
					instr0.addr2 = res1.ret_value;
					instr0.addr1 = simple_addr.tag_new(tag1.tag_num);
					ans.list.add(instr0);

					ans.list.addAll(res2.list);
					ans.list.add(tag1);

					simple_instr newinstr = new simple_instr();
					newinstr.name = now.name;
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(1);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;

				}
			} else
			if (now.name.equals("!")){
				if (now.son.get(0).data_type.equals("bool")&&now.son.get(0).data_array_dim == 0){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
//					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
//					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = "!";
					newinstr.type = "r";
					newinstr.addr2 = res1.ret_value;
//					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(1);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
			} else
			if (now.name.equals("<")||now.name.equals(">")||now.name.equals("<=")||now.name.equals(">=")){
				if (now.son.get(0).data_type.equals("int")&&now.son.get(1).data_type.equals("int")
						&&now.son.get(0).data_array_dim==0&&now.son.get(1).data_array_dim==0){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = now.name;
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(1);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}else
				if (now.son.get(0).data_type.equals("string")&&now.son.get(1).data_type.equals("string")
						&&now.son.get(0).data_array_dim==0&&now.son.get(1).data_array_dim==0){
//					todo
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = "string_"+now.name;
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(1);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
			} else
			if (now.name.equals("==")||now.name.equals("!=")){

				if (now.son.get(0).data_type.equals("string") && now.son.get(1).data_type.equals("string")
						&&now.son.get(0).data_array_dim==0 && now.son.get(1).data_array_dim==0){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = "string_"+now.name;
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(1);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
				if (now.son.get(0).data_type.equals(now.son.get(1).data_type)
						&&now.son.get(0).data_array_dim==now.son.get(1).data_array_dim){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = now.name;
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(1);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
				else if (now.son.get(1).data_type.equals("null")&&now.son.get(1).data_array_dim==0
						&&(!((now.son.get(0).data_type.equals("int")||now.son.get(0).data_type.equals("string"))
						&&now.son.get(0).data_array_dim==0))){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = now.name;
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(1);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
				else if (now.son.get(0).data_type.equals("null")&&now.son.get(0).data_array_dim==0
						&&(!((now.son.get(1).data_type.equals("int")||now.son.get(1).data_type.equals("string"))
						&&now.son.get(1).data_array_dim==0))){
					instr_set ans = new instr_set();
					instr_set res1 = dfs7(now.son.get(0),i+1);
					instr_set res2 = dfs7(now.son.get(1),i+1);

					ans.list.addAll(res1.list);
					ans.list.addAll(res2.list);

					simple_instr newinstr = new simple_instr();
					newinstr.name = now.name;
					newinstr.type = "lr";
					newinstr.addr2 = res1.ret_value;
					newinstr.addr3 = res2.ret_value;
					newinstr.addr1 = simple_addr.reg_new(1);
					ans.list.add(newinstr);
					ans.ret_value = newinstr.addr1;
					return ans;
				}
			} else
			if (now.name.equals("++")||now.name.equals("--")) {
				if (now.son.get(0).left_value){
					if (now.son.get(0).data_type.equals("int")&&now.son.get(0).data_array_dim==0){
						if (now.expr_type.equals("r")) {

							instr_set ans = new instr_set();
							instr_set res1 = dfs7(now.son.get(0), i + 1);
//							instr_set res2 = dfs7(now.son.get(1),i+1);

							ans.list.addAll(res1.list);
//							ans.list.addAll(res2.list);

							simple_instr newinstr = new simple_instr();
							newinstr.name = now.name;
							newinstr.type = now.expr_type;
							newinstr.addr2 = res1.ret_value;
							newinstr.addr1 = newinstr.addr2;
//							newinstr.addr3 = res2.ret_value;
							ans.list.add(newinstr);
							ans.ret_value = newinstr.addr2;
							return ans;
						} else {
							instr_set ans = new instr_set();
							instr_set res1 = dfs7(now.son.get(0), i + 1);
//							instr_set res2 = dfs7(now.son.get(1),i+1);

							ans.list.addAll(res1.list);
//							ans.list.addAll(res2.list);
							{
								simple_instr instr = new simple_instr();
								instr.name = "=";
								instr.type = "lr";
								instr.addr1 = simple_addr.reg_new(8);
								instr.addr2 = res1.ret_value;
								ans.list.add(instr);
								ans.ret_value = instr.addr1;
							}
							simple_instr newinstr = new simple_instr();
							newinstr.name = now.name;
							newinstr.type = now.expr_type;
							newinstr.addr2 = res1.ret_value;
							newinstr.addr1 = newinstr.addr2;
//							newinstr.addr3 = res2.ret_value;
							ans.list.add(newinstr);
//							ans.ret_value = newinstr.addr2;
							return ans;
						}
					}
				}
			} else
			if (now.name.equals("sub")){
				String typea =now.son.get(0).data_type;
				int dima = now.son.get(0).data_array_dim;
				String nameb = now.son.get(1).name;
				varname namespace = root1.scope.get(typea);
				{
					if (typea.equals("string")&&nameb.equals("length")){
						//todo
//						now.data_type = "function";
//						now.left_value = now.son.get(0).left_value;
//						now.data_array_dim = 0;
					} else
					if (typea.equals("string")&&nameb.equals("ord")){
//						//todo
//						now.data_type = "function";
//						now.left_value = now.son.get(0).left_value;
//						now.data_array_dim = 0;
					} else
					if (typea.equals("string")&&nameb.equals("substring")){
//						todo
//						now.data_type = "function";
//						now.left_value = now.son.get(0).left_value;
//						now.data_array_dim = 0;
					} else
					if (typea.equals("string")&&nameb.equals("parseInt")){
//						//todo
//						now.data_type = "function";
//						now.left_value = now.son.get(0).left_value;
//						now.data_array_dim = 0;
					}
					else {
						instr_set res1 = dfs7(now.son.get(0),i+1);
						instr_set ans = new instr_set();
						ans.list.addAll(res1.list);

						varname subname =  namespace.location.scope.get(nameb);
//						simple_instr instr1 = new simple_instr();
//						instr1.name = "+";
//						instr1.type = "lr";
//						instr1.addr2 = res1.ret_value;
//						instr1.addr3 = simple_addr.val_new(subname.mem_position);
//						instr1.addr1 = simple_addr.reg_new(8);

//						ans.list.add(instr1);
//						ans.ret_value = simple_addr.mem_new(instr1.addr1.num);
						if (res1.ret_value.op1!=null){
							simple_instr instr = new simple_instr();
							instr.name = "=";
							instr.type = "lr";
							instr.addr1 = simple_addr.reg_new(8);
							instr.addr2 = res1.ret_value;
							ans.list.add(instr);
							ans.ret_value = simple_addr.mem_offset(instr.addr1,subname.mem_position);

						}else {
							ans.ret_value = simple_addr.mem_offset(res1.ret_value, subname.mem_position);
						}
						return ans;
					}
				}
			} else
			if (now.name.equals("=")){
				instr_set res1 = dfs7(now.son.get(0),i+1);
				instr_set res2 = dfs7(now.son.get(1),i+1);
				instr_set ans = new instr_set();
				ans.list.addAll(res1.list);
				ans.list.addAll(res2.list);
				simple_instr instr1 = new simple_instr();
				instr1.name = "=";
				instr1.type = "lr";
				instr1.addr1 = res1.ret_value;
				instr1.addr2 = res2.ret_value;
				ans.list.add(instr1);
				//ans.ret_value = instr1.addr1;
				return ans;
			} else
			if (now.name.equals("array")){
//				todo
				instr_set res1 = dfs7(now.son.get(0),i+1);
				instr_set res2 = dfs7(now.son.get(1),i+1);
				instr_set ans = new instr_set();

				simple_addr addr1 ;
				simple_addr addr2 ;
				ans.list.addAll(res1.list);
				ans.list.addAll(res2.list);
				if (res1.ret_value.op1 == null){
					addr1 = res1.ret_value;

				} else{
					simple_instr instr = new simple_instr();
					instr.name = "=";
					instr.type = "lr";
					instr.addr1 = simple_addr.reg_new(8);
					instr.addr2 = res1.ret_value;
					ans.list.add(instr);
					addr1 = instr.addr1;
				}
//				System.err.println(now.son.get(0).name);
				if (res2.ret_value.op1 == null){
					addr2 = res2.ret_value;

				} else{
					simple_instr instr = new simple_instr();
					instr.name = "=";
					instr.type = "lr";
					instr.addr1 = simple_addr.reg_new(8);
					instr.addr2 = res2.ret_value;
					ans.list.add(instr);
					addr2 = instr.addr1;
				}
				ans.ret_value = simple_addr.mem_offset(addr1,addr2);
				return ans;
				//simple_instr instr = new simple_instr();


//				simple_instr instr1 = new simple_instr();
//				instr1.name = "Load";
//				instr1.type = "";
//				instr1.addr2 = res1.ret_value;
//				instr1.addr3 = res2.ret_value;
//				instr1.addr1 = simple_addr.reg_new(8);
//
//				ans.ret_value = instr1.addr1;
//				ans.list.add(instr1);
//				return ans;
			} else
			if (now.name.equals("function")){
				if (now.son.get(0).name.equals("sub")){
					if (now.son.get(0).son.get(0).data_array_dim>0&&
							now.son.get(0).son.get(1).name.equals("size")){
						instr_set ans = new instr_set();
						instr_set res0 = dfs7(now.son.get(0).son.get(0),i+1);
						ans.list.addAll(res0.list);
						simple_addr addr0 = res0.ret_value;
						if (addr0.op1 != null){
							simple_instr instr = new simple_instr();
							instr.name = "=";
							instr.type = "lr";
							instr.addr1 = simple_addr.reg_new(8);
							instr.addr2 = addr0;
							ans.list.add(instr);
							addr0 = instr.addr1;
						}
						{
							ans.ret_value = simple_addr.mem_offset(addr0,-1);
						}
						return ans;
//						instr_set ans = new instr_set();
//						instr_set res0 = dfs7(now.son.get(0),i+1);
//						simple_instr instr0 = new simple_instr();
//						instr0.name = "push_function";
//						instr0.addr2 = res0.ret_value;
//						ans.list.addAll(res0.list);
//						ans.list.add(instr0);
//						for (int j=2;j<now.son.size();++j){
//							instr_set res1 = dfs7(now.son.get(j),i+1);
//							ans.list.addAll(res1.list);
//							simple_instr instr1 = new simple_instr();
//							instr1.addr2 = res1.ret_value;
//							instr1.name = "push_function";
//						}
//						return ans;
					} else
					if (now.son.get(0).son.get(0).data_array_dim==0 &&now.son.get(0).son.get(0).data_type.equals("string")
							&&now.son.get(0).son.get(1).name.equals("length")){

						instr_set ans = new instr_set();
						instr_set res0 = dfs7(now.son.get(0).son.get(0),i+1);
						ans.list.addAll(res0.list);
						simple_addr addr0 = res0.ret_value;
						if (addr0.op1 != null){
							simple_instr instr = new simple_instr();
							instr.name = "=";
							instr.type = "lr";
							instr.addr1 = simple_addr.reg_new(8);
							instr.addr2 = addr0;
							ans.list.add(instr);
							addr0 = instr.addr1;
						}
						{
							ans.ret_value = simple_addr.mem_offset(addr0,0);
						}
						return ans;
//						now.data_type = "int";
//						now.data_array_dim = 0;
//						now.left_value = false;
					} else
					if (now.son.get(0).son.get(0).data_array_dim==0 &&now.son.get(0).son.get(0).data_type.equals("string")
							&&now.son.get(0).son.get(1).name.equals("substring")){
						//if (!(now.son.get(0).son.get(0).data_array_dim==0&&now.son.get(0).son.get(0).data_type.equals("string"))){
						//    throw new Exception("substring Error");
						//}
						instr_set ans = new instr_set();
						instr_set res0 = dfs7(now.son.get(0).son.get(0),i+2);
						instr_set res1 = dfs7(now.son.get(1),i+1);
						instr_set res2 = dfs7(now.son.get(2),i+1);
						ans.list.addAll(res2.list);
						ans.list.addAll(res1.list);
						ans.list.addAll(res0.list);
						simple_addr addr0 = res0.ret_value;
						simple_addr addr1 = res1.ret_value;
						simple_addr addr2 = res2.ret_value;
						{
							simple_instr instr = new simple_instr();
							instr.name = "push_function";
							instr.addr1 = simple_addr.val_new(2);
							instr.addr2 = addr2;
							ans.list.add(instr);
						}
						{
							simple_instr instr = new simple_instr();
							instr.name = "push_function";
							instr.addr1 = simple_addr.val_new(1);
							instr.addr2 = addr1;
							ans.list.add(instr);
						}
						{
							simple_instr instr = new simple_instr();
							instr.name = "push_function";
							instr.addr1 = simple_addr.val_new(0);
							instr.addr2 = addr0;
							ans.list.add(instr);
						}
						{
							simple_instr instr = new simple_instr();
							instr.name = "call substring";
							instr.addr2 = simple_addr.func_new("string.substring");
							ans.list.add(instr);
						}
						{
							if (!(now.son.get(1).data_array_dim == 0 && now.son.get(1).data_type.equals("int"))) {
								throw new Exception("substring Error");
							}
							if (!(now.son.get(2).data_array_dim == 0 && now.son.get(2).data_type.equals("int"))) {
								throw new Exception("substring Error");
							}
							if (now.son.size() != 3) {
								throw new Exception("string.substring() Error");
							}
						}
						{
							simple_instr instr = new simple_instr();
							instr.name = "=";
							instr.type = "lr";
							instr.addr1 = simple_addr.reg_new(8);
							instr.addr2 = simple_addr.return_register();
							ans.list.add(instr);
							ans.ret_value = instr.addr1;
						}
						//ans.ret_value = simple_addr.return_register();
						return ans;
//						now.data_type = "string";
//						now.data_array_dim = 0;
//						now.left_value = false;
					} else
					if (now.son.get(0).son.get(0).data_array_dim==0 &&now.son.get(0).son.get(0).data_type.equals("string")
							&&now.son.get(0).son.get(1).name.equals("parseInt")){
						instr_set ans = new instr_set();
						instr_set res0 = dfs7(now.son.get(0).son.get(0),i+2);
						ans.list.addAll(res0.list);
						simple_addr addr0 = res0.ret_value;
						{
							simple_instr instr = new simple_instr();
							instr.name = "parseInt";
							instr.addr1 = simple_addr.reg_new(8);
							instr.addr2 = addr0;
							ans.ret_value = instr.addr1;
							ans.list.add(instr);
						}
//						{
//							simple_instr instr = new simple_instr();
//							instr.name = "push_function";
//							instr.addr1 = simple_addr.val_new(0);
//							instr.addr2 = addr0;
//							ans.list.add(instr);
//						}
//						{
//							simple_instr instr = new simple_instr();
//							instr.name = "call";
//							instr.addr2 = simple_addr.func_new("string.parseInt");
//							ans.list.add(instr);
//						}
//
//						if (now.son.size()!=1){
//							throw new Exception("string.parseInt() Error");
//						}
//						{
//							simple_instr instr = new simple_instr();
//							instr.name = "=";
//							instr.type = "lr";
//							instr.addr1 = simple_addr.reg_new(8);
//							instr.addr2 = simple_addr.return_register();
//							ans.list.add(instr);
//							ans.ret_value = instr.addr1;
//						}
						return ans;
					} else
					if (now.son.get(0).son.get(0).data_array_dim==0 &&now.son.get(0).son.get(0).data_type.equals("string")
							&&now.son.get(0).son.get(1).name.equals("ord")){
						//if (!(now.son.get(0).data_array_dim==0&&now.son.get(0).data_type.equals("string"))){
						//    throw new Exception("ord Error");
						//}
						instr_set ans = new instr_set();
						instr_set res0 = dfs7(now.son.get(0).son.get(0),i+2);
						instr_set res1 = dfs7(now.son.get(1),i+1);
						ans.list.addAll(res0.list);
						ans.list.addAll(res1.list);
						simple_addr addr0 = res0.ret_value;
						simple_addr addr1 = res1.ret_value;
						if (addr0.op1 != null){
							simple_instr instr = new simple_instr();
							instr.name = "=";
							instr.type = "lr";
							instr.addr2 = addr0;
							instr.addr1 = simple_addr.reg_new(8);
							ans.list.add(instr);
							addr0 = instr.addr1;
						}
						if (addr1.op1!=null){
							simple_instr instr = new simple_instr();
							instr.name = "=";
							instr.type = "lr";
							instr.addr2 = addr1;
							instr.addr1 = simple_addr.reg_new(8);
							ans.list.add(instr);
							addr1 = instr.addr1;
						}
						{
							simple_instr instr = new simple_instr();
							instr.name = "ord";
							instr.type = "lr";
							instr.addr3 = addr1;
							instr.addr2 = addr0;
							instr.addr1 = simple_addr.reg_new(8);
							ans.list.add(instr);
							ans.ret_value = instr.addr1;
						}
						if (!(now.son.get(1).data_array_dim==0&&now.son.get(1).data_type.equals("int"))){
							throw new Exception("ord Error");
						}
						if (now.son.size()!=2){
							throw new Exception("string.ord() Error");
						}
//						now.data_type = "int";
//						now.data_array_dim = 0;
//						now.left_value = false;
						return ans;
					}
					else {
						varname firstspace = scope_info.get(0).scope.get(now.son.get(0).son.get(0).data_type);
						varname secondspace = firstspace.location.scope.get(now.son.get(0).son.get(1).name);
						instr_set ans = new instr_set();

						instr_set pusher = new instr_set();


						for (int j=now.son.size()-1;j>=1;--j){
							instr_set res1 = dfs7(now.son.get(j),i+1);
							ans.list.addAll(res1.list);
							simple_instr instr1 = new simple_instr();
							instr1.addr2 = res1.ret_value;
							instr1.name = "push_function";
							instr1.addr1 = simple_addr.val_new(j);
							pusher.list.add(instr1);
						}
						instr_set res0 = dfs7(now.son.get(0).son.get(0),i+2);
						simple_instr instr0 = new simple_instr();
						instr0.name = "push_function";
						instr0.addr2 = res0.ret_value;
						instr0.addr1 = simple_addr.val_new(0);
						ans.list.addAll(res0.list);
						pusher.list.add(instr0);

						ans.list.addAll(pusher.list);
						simple_instr instr2 = new simple_instr();
						instr2.name = "call";
						instr2.addr2 = simple_addr.func_new( firstspace.name+"."+secondspace.name);
						instr2.addr3 = simple_addr.val_new(now.son.size());
						ans.list.add(instr2);
//						simple_instr instr3 = new simple_instr();
//						instr3.name = "=";
//						instr3.type = "lr";
//						instr3.addr2 = simple_addr.return_register();
//						instr3.addr1 = simple_addr.reg_new(8);
//						ans.list.add(instr3);
						{
							simple_instr instr = new simple_instr();
							instr.name = "=";
							instr.type = "lr";
							instr.addr1 = simple_addr.reg_new(8);
							instr.addr2 = simple_addr.return_register();
							ans.list.add(instr);
							ans.ret_value = instr.addr1;
						}
//						ans.ret_value = simple_addr.return_register();
						return ans;
					}
				}
				else if (now.son.get(0).type.equals("atom")){
					node lowerclass = now.son.get(0);
					int j=scope_info.size()-1;
					while (!scope_info.get(j).scope.containsKey(lowerclass.name)){
						j=j-1;
					}
					if (j==1){
//						System.err.println(scope_info.get(1).name);
						varname firstspace = scope_info.get(0).scope.get(scope_info.get(1).name);
						varname secondspace = firstspace.location.scope.get(now.son.get(0).name);
						instr_set ans = new instr_set();

						instr_set pusher = new instr_set();


						for (int jj=now.son.size()-1;jj>=1;--jj){
							instr_set res1 = dfs7(now.son.get(jj),i+1);
							ans.list.addAll(res1.list);
							simple_instr instr1 = new simple_instr();
							instr1.addr2 = res1.ret_value;
							instr1.name = "push_function";
							instr1.addr1 = simple_addr.val_new(jj);
							instr1.name = "push_function";
							pusher.list.add(instr1);
						}
						//instr_set res0 = dfs7(now.son.get(0).son.get(0),i+2);
						simple_instr instr0 = new simple_instr();
						instr0.name = "push_function";
						instr0.addr2 = simple_addr.var_new("this",2);
						instr0.addr1 = simple_addr.val_new(0);
						//ans.list.addAll(res0.list);
						pusher.list.add(instr0);

						ans.list.addAll(pusher.list);
						simple_instr instr2 = new simple_instr();
						instr2.name = "call";
//						System.err.println(firstspace.name);
						instr2.addr2 = simple_addr.func_new( firstspace.name+"."+secondspace.name);
						instr2.addr3 = simple_addr.val_new(now.son.size());
						ans.list.add(instr2);
//						simple_instr instr3 = new simple_instr();
//						instr3.name = "=";
//						instr3.type = "lr";
//						instr3.addr2 = simple_addr.return_register();
//						instr3.addr1 = simple_addr.reg_new(8);
//						ans.list.add(instr3);
						{
							simple_instr instr = new simple_instr();
							instr.name = "=";
							instr.type = "lr";
							instr.addr1 = simple_addr.reg_new(8);
							instr.addr2 = simple_addr.return_register();
							ans.list.add(instr);
							ans.ret_value = instr.addr1;
						}
//						ans.ret_value = simple_addr.return_register();
						return ans;

					}
					//Automatic throw if no function
					if (j==0&&now.son.get(0).name.equals("print")){
						if (now.son.size()!=2){
							throw new Exception("print Error");
						}
						if (!(now.son.get(1).data_type.equals("string")&&now.son.get(1).data_array_dim==0)){
							throw new Exception("print Error");
						}
						instr_set ans = new instr_set();
						instr_set res0 = dfs7(now.son.get(1),i+1);
						ans.list.addAll(res0.list);
						{
							simple_instr instr = new simple_instr();
							instr.name = "print";
							instr.addr2 = res0.ret_value;
							ans.list.add(instr);
						}
						return ans;
//						now.data_type = "void";
//						now.data_array_dim = 0;
					} else
					if (j==0&&now.son.get(0).name.equals("println")){

						if (now.son.size()!=2){
							throw new Exception("println Error");
						}
						if (!(now.son.get(1).data_type.equals("string")&&now.son.get(1).data_array_dim==0)){
							throw new Exception("println Error");
						}
						instr_set ans = new instr_set();
						instr_set res0 = dfs7(now.son.get(1),i+1);
						ans.list.addAll(res0.list);
						{
							simple_instr instr = new simple_instr();
							instr.name = "println";
							instr.addr2 = res0.ret_value;
							ans.list.add(instr);
						}
						return ans;
//						now.data_type = "void";
//						now.data_array_dim = 0;
					} else
					if (j==0&&now.son.get(0).name.equals("getString")){
						if (now.son.size()!=1){
							throw new Exception("getString Error");
						}
//						now.data_array_dim = 0;
//						now.data_type = "string";
						instr_set ans = new instr_set();
//						instr_set res0 = dfs7(now.son.get(1),i+1);
//						ans.list.addAll(res0.list);
						{
							simple_instr instr = new simple_instr();
							instr.name = "getString";
							instr.addr1 = simple_addr.reg_new(8);
							ans.list.add(instr);
							ans.ret_value = instr.addr1;
						}
						return ans;
					} else
					if (j==0&&now.son.get(0).name.equals("getInt")){
						if (now.son.size()!=1){
							throw new Exception("getInt Error");
						}
						instr_set ans = new instr_set();
						//instr_set res0 = dfs7(now.son.get(1),i+1);
						//ans.list.addAll(res0.list);
						{
							simple_instr instr = new simple_instr();
							instr.name = "getInt";
							instr.addr1 = simple_addr.reg_new(8);
							//instr.addr2 = res0.ret_value;
							ans.list.add(instr);
							ans.ret_value = instr.addr1;
						}
						return ans;

//						now.data_array_dim = 0;
//						now.data_type = "int";
					} else
					if (j==0&&now.son.get(0).name.equals("toString")){
						if (now.son.size()!=2){
							throw new Exception("toString Error");
						}
						if (!(now.son.get(1).data_type.equals("int")&&now.son.get(1).data_array_dim==0)){
							throw new Exception("toString Error");
						}
						instr_set ans = new instr_set();
						instr_set res0 = dfs7(now.son.get(1),i+1);
						ans.list.addAll(res0.list);
						{
							simple_instr instr = new simple_instr();
							instr.name = "toString";
							instr.addr1 = simple_addr.reg_new(8);
							instr.addr2 = res0.ret_value;
							ans.list.add(instr);
							ans.ret_value = instr.addr1;
						}
						return ans;

//						now.data_array_dim = 0;
//						now.data_type = "string";
					}
					else{
						instr_set ans = new instr_set();
						instr_set pusher = new instr_set();
						varname space = scope_info.get(0).scope.get(now.son.get(0).name);

						for (int k=now.son.size()-1;k>=1;--k){
							instr_set res1 = dfs7(now.son.get(k),i+1);
							ans.list.addAll(res1.list);
							simple_instr instr1 = new simple_instr();
							instr1.name = "push_function";
							instr1.addr2 = res1.ret_value;
							instr1.addr1 = simple_addr.val_new(k-1);
							pusher.list.add(instr1);
						}
						ans.list.addAll(pusher.list);
						simple_instr instr2 = new simple_instr();
						instr2.name = "call";
						instr2.addr2 = simple_addr.func_new(space.name);
						instr2.addr3 = simple_addr.val_new(now.son.size()-1);
						ans.list.add(instr2);
//						simple_instr instr3 = new simple_instr();
//						instr3.name = "=";
//						instr3.type = "lr";
//						instr3.addr2 = simple_addr.return_register();
//						instr3.addr1 = simple_addr.reg_new(8);
//						ans.list.add(instr3);
						{
							simple_instr instr = new simple_instr();
							instr.name = "=";
							instr.type = "lr";
							instr.addr1 = simple_addr.reg_new(8);
							instr.addr2 = simple_addr.return_register();
							ans.list.add(instr);
							ans.ret_value = instr.addr1;
						}
//						ans.ret_value = simple_addr.return_register();
						return ans;

					}
				}
			} else
			if (now.name.equals("new")){
//				todo later
				if (now.data_array_dim == 0 ){
					instr_set ans = new instr_set();

					varname space = scope_info.get(0).scope.get(now.data_type);

					simple_addr res = simple_addr.reg_new(8);

					simple_instr instr = new simple_instr();
					instr.malloc_instr(res,simple_addr.val_new(space.location.mem_size));
					ans.list.add(instr);
					{
						if (space.location.has_self_function){
							{
								simple_instr instr0 = new simple_instr();
								instr0.name = "push_function";
								instr0.addr2 = res;
								instr0.addr1 = simple_addr.val_new(0);
								ans.list.add(instr0);
							}
							{
								simple_instr instr0 = new simple_instr();
								instr0.name = "call";
								instr0.addr2 = simple_addr.func_new(space.name+"."+space.name);
								instr0.addr3 = simple_addr.val_new(1);
								ans.list.add(instr0);
							}
						} else{
//							System.err.println(space.name);
//							System.err.println(space.location.scope);
//							System.err.println("self_function_not_found");
						}
					}

					ans.ret_value = res;
//					simple_instr instr1 = new simple_instr();
//					instr1.name = "=";
//					instr1.type = "lr";
//					instr1.addr1 = simple_addr.reg_new(8);
//					instr1.addr2 = simple_addr.heap_pointer();
//
//					simple_instr instr0 = new simple_instr();
//					instr0.name = "+";
//					instr0.type = "lr";
//					instr0.addr1 = instr1.addr2;
//					instr0.addr2 = instr0.addr1;
//					instr0.addr3 = simple_addr.val_new(space.location.mem_size);
//
//					ans.list.add(instr1);
//					ans.list.add(instr0);
//					ans.ret_value = instr1.addr1;
					return ans;
				} else {
//					simple_addr heap_pointer = simple_addr.heap_pointer();
//					simple_addr res = simple_addr.reg_new(8);

					ArrayList<instr_set> dimmer = new ArrayList<instr_set>();
					ArrayList<simple_addr> dimmerp1 = new ArrayList<simple_addr>();
					ArrayList<simple_addr> index = new ArrayList<simple_addr>();
					ArrayList<simple_addr> location = new ArrayList<simple_addr>();
					ArrayList<simple_instr> tag = new ArrayList<simple_instr>();
					instr_set ans = new instr_set();

					node typea = now.son.get(0);
					while (typea.son.size()!=0){
						if (typea.son.size()==2){
							dimmer.add(dfs7(typea.son.get(1),i+1));
							index.add(simple_addr.reg_new(8));
							location.add(simple_addr.reg_new(8));
							dimmerp1.add(simple_addr.reg_new(8));
							simple_instr instr0 = new simple_instr();
							instr0.setTag();
							tag.add(instr0);
						}
						typea = typea.son.get(0);
					}
//						todo is the following
//						dimmer[] += 1
//						malloc location[] dimmerp1[]
//						index[] = 1
//                      tag[]
//						...
//						[location[] + index[]*8] = location[i-1]
//						index ++
//						BLT tag[] index[] dimmerp1[]
//						location[]++
					for (int j = 0; j < dimmer.size();++j){
						simple_instr instr;

						instr = new simple_instr();
						instr.name = "announce";
						instr.type = "lr";
						instr.addr1 = index.get(j);
						ans.list.add(instr);

						instr = new simple_instr();
						instr.name = "announce";
						instr.type = "lr";
						instr.addr1 = location.get(j);
						ans.list.add(instr);

						ans.list.addAll(dimmer.get(j).list);

						simple_instr instr0 = new simple_instr();
						instr0.name = "+";
						instr0.type = "lr";
						instr0.addr1 = dimmerp1.get(j);
						instr0.addr2 = dimmer.get(j).ret_value;
						instr0.addr3 = simple_addr.val_new(1);
						ans.list.add(instr0);
					}

					for (int j = dimmer.size()-1;j>0;--j){
//						simple_instr instr0 = new simple_instr();
//						instr0.name = "+";
//						instr0.type = "lr";
//						instr0.addr1 = dimmerp1.get(j);
//						instr0.addr2 = dimmer.get(j).ret_value;
//						instr0.addr3 = simple_addr.val_new(1);
//						ans.list.add(instr0);

						simple_instr instr1 = new simple_instr();
						instr1.malloc_instr(location.get(j),dimmerp1.get(j));
						ans.list.add(instr1);

						simple_instr instr2 = new simple_instr();
						instr2.name = "=";
						instr2.type = "lr";
						instr2.addr1 = index.get(j);
						instr2.addr2 = simple_addr.val_new(1);
						ans.list.add(instr2);

						ans.list.add(tag.get(j));

					}
					{
						int j=0;
//						simple_instr instr0 = new simple_instr();
//						instr0.name = "+";
//						instr0.type = "lr";
//						instr0.addr1 = dimmerp1.get(j);
//						instr0.addr2 = dimmer.get(j).ret_value;
//						instr0.addr3 = simple_addr.val_new(1);
//						ans.list.add(instr0);

						simple_instr instr1 = new simple_instr();
						instr1.malloc_instr(location.get(j),dimmerp1.get(j));
						ans.list.add(instr1);

						instr1 = new simple_instr();
						instr1.name = "++8";
						instr1.type = "r";
						instr1.addr2 = location.get(j);
						instr1.addr1 = instr1.addr2;
						ans.list.add(instr1);

					}
					for (int j=1;j<dimmer.size();++j){
						simple_instr instr = new simple_instr();
						instr.name = "=";
						instr.type = "lr";
						instr.addr1 = simple_addr.mem_offset(location.get(j),index.get(j));
						instr.addr2 = location.get(j-1);
						ans.list.add(instr);

						instr = new simple_instr();
						instr.name = "++";
						instr.type = "r";
						instr.addr2 = index.get(j);
						instr.addr1 = instr.addr2;

						ans.list.add(instr);

						instr = new simple_instr();
						instr.name = "BLT";
						instr.addr1 = simple_addr.tag_new(tag.get(j).tag_num);
						instr.addr2 = index.get(j);
						instr.addr3 = dimmerp1.get(j);
						ans.list.add(instr);

						instr = new simple_instr();
						instr.name = "++8";
						instr.type = "r";
						instr.addr2 = location.get(j);
						instr.addr1 = instr.addr2;
						ans.list.add(instr);
					}
					ans.ret_value = location.get(location.size()-1);
					return ans;
				}
//				node typea = now.son.get(0);
//				int arr_dim = 0;
//				boolean sign = false;
//				while (typea.son.size()!=0){
//					if (sign){
//						if (typea.son.size()==1){
//							throw new Exception("new Error1");
//						}
//						if (!typea.son.get(1).data_type.equals("int")){
//							throw new Exception("new Error2");
//						}
//						arr_dim = arr_dim+1;
//						typea = typea.son.get(0);
//					}else{
//						if (typea.son.size()==2){
//							sign = true;
//							if (!typea.son.get(1).data_type.equals("int")){
//								throw new Exception("new Error3");
//							}
//						}
//						arr_dim = arr_dim+1;
//						typea = typea.son.get(0);
//					}
//				}
//				if (!(root1.scope.containsKey(typea.name)&&root1.scope.get(typea.name).type.equals("class"))){
//					throw new Exception("new Error4");
//				}
//				if (typea.name.equals("void")) {
//					throw new Exception("new void Error");
//				}
//				if ((!sign)&&arr_dim>0){
//					throw new Exception("Warning: no use \"new\"");
//				}
//				now.data_type = typea.name;
//				//System.err.println("["+now.data_type+"]");
//				now.data_array_dim = arr_dim;
//				now.left_value = false;
			}
			else{
				throw new Exception("CE! I dont know");
			}

			//To be done
		}
//		if (now.type.equals("variable")){ }
		if (now.type.equals("for")){
			simple_instr instr,tag1,tag2,tag3;
			tag1 = new simple_instr();
			tag1.setTag();

			tag2 = new simple_instr();
			tag2.setTag();

			tag3 = new simple_instr();
			tag3.setTag();

			break_tag.add(tag2);
			continue_tag.add(tag3);

			instr_set ans = new instr_set();
			instr_set res0 = dfs7(now.son.get(0),i+1);
			instr_set res1 = dfs7(now.son.get(1),i+1);
			instr_set res2 = dfs7(now.son.get(2),i+1);
			instr_set res3 = dfs7(now.son.get(3),i+1);
			ans.list.addAll(res0.list);

			ans.list.addAll(res1.list);

			instr = new simple_instr();
			instr.name = "BZ";
			instr.addr2 = res1.ret_value;
			instr.addr1 = simple_addr.tag_new(tag2.tag_num);

			ans.list.add(instr);
			ans.list.add(tag1);
			ans.list.addAll(res3.list);
			ans.list.add(tag3);
			ans.list.addAll(res2.list);

			res1 = dfs7(now.son.get(1),i+1);
			ans.list.addAll(res1.list);

			instr = new simple_instr();
			instr.name = "BNZ";
			instr.addr2 = res1.ret_value;
			instr.addr1 = simple_addr.tag_new(tag1.tag_num);

			ans.list.add(instr);
			ans.list.add(tag2);

			if (now.has_scope){
				scope_info.remove(scope_info.size()-1);
			}
			break_tag.remove(break_tag.size()-1);
			continue_tag.remove(continue_tag.size()-1);
			return ans;
		}
		if (now.type.equals("while")){
			instr_set ans = new instr_set();
			simple_instr tag1,tag2,tag3;
			tag1 = new simple_instr();
			tag2 = new simple_instr();
			tag3 = new simple_instr();
			tag1.setTag();
			tag2.setTag();
			tag3.setTag();

			break_tag.add(tag2);
			continue_tag.add(tag3);

			instr_set res0 = dfs7(now.son.get(0),i+1);
			instr_set res1 = dfs7(now.son.get(1),i+1);
			ans.list.addAll(res0.list);

			simple_instr instr = new simple_instr();
			instr.name = "BZ";
			instr.addr2 = res0.ret_value;
			instr.addr1 = simple_addr.tag_new(tag2.tag_num);

			ans.list.add(instr);
			ans.list.add(tag1);

			ans.list.addAll(res1.list);
			res0 = dfs7(now.son.get(0),i+1);
			ans.list.add(tag3);
			ans.list.addAll(res0.list);

			instr = new simple_instr();
			instr.name = "BNZ";
			instr.addr2 = res0.ret_value;
			instr.addr1 = simple_addr.tag_new(tag1.tag_num);
			ans.list.add(instr);

			ans.list.add(tag2);

			if (now.has_scope){
				scope_info.remove(scope_info.size()-1);
			}

			break_tag.remove(break_tag.size()-1);
			continue_tag.remove(continue_tag.size()-1);

			return ans;
		}
		if (now.type.equals("if")){
			instr_set ans = new instr_set();
			if (now.son.size()==3){
				simple_instr tag1,tag2;
				tag1 = new simple_instr();
				tag1.setTag();
				tag2 = new simple_instr();
				tag2.setTag();
				instr_set res0 = dfs7(now.son.get(0),i+1);
				instr_set res1 = dfs7(now.son.get(1),i+1);
				instr_set res2 = dfs7(now.son.get(2),i+1);

				ans.list.addAll(res0.list);
				simple_instr instr = new simple_instr();
				instr.name = "BZ";
				instr.addr2 = res0.ret_value;
				instr.addr1 = simple_addr.tag_new(tag1.tag_num);
				ans.list.add(instr);

				ans.list.addAll(res1.list);

				instr = new simple_instr();
				instr.name = "JAL";
				instr.addr1 = simple_addr.tag_new(tag2.tag_num);
				ans.list.add(instr);

				ans.list.add(tag1);
				ans.list.addAll(res2.list);
				ans.list.add(tag2);
			} else {

				simple_instr tag = new simple_instr();
				tag.setTag();

				instr_set res0 = dfs7(now.son.get(0),i+1);
				instr_set res1 = dfs7(now.son.get(1),i+1);

				ans.list.addAll(res0.list);

				simple_instr instr = new simple_instr();
				instr.name = "BZ";
				instr.addr2 = res0.ret_value;
				instr.addr1 = simple_addr.tag_new(tag.tag_num);

				ans.list.add(instr);

				ans.list.addAll(res1.list);

				ans.list.add(tag);

			}
			if (now.has_scope){
				scope_info.remove(scope_info.size()-1);
			}
			return ans;
		}
		//return check
		if (now.type.equals("return")) {
			instr_set ans = new instr_set();
			if (now.son.size()==1){
				simple_instr instr = new simple_instr();
				instr.name = "Return";
				instr_set res = dfs7(now.son.get(0),i+1);
				ans.list.addAll(res.list);
				instr.addr2 = res.ret_value;
				ans.list.add(instr);
			} else{
				simple_instr instr = new simple_instr();
				instr.name = "Return";
				instr.addr2 = simple_addr.val_new(0);
				ans.list.add(instr);
			}
			return ans;
		}
		if (now.type.equals("continue")){
//			int j = scope_info.size()-1;
//			while (!(scope_info.get(j).type.equals("for")||scope_info.get(j).type.equals("while"))){
//				System.err.println(scope_info.get(j).type);
//				j = j-1;
//			}
//			Automatic CE if no for or while
			instr_set ans = new instr_set() ;
			{
				simple_instr instr = new simple_instr();
				instr.name = "JAL";
				instr.addr1 = simple_addr.tag_new(continue_tag.get(continue_tag.size()-1).tag_num);
				ans.list.add(instr);
			}
			return ans;
		}
		if (now.type.equals("break")){
			instr_set ans = new instr_set() ;
			{
				simple_instr instr = new simple_instr();
				instr.name = "JAL";
				instr.addr1 = simple_addr.tag_new(break_tag.get(break_tag.size()-1).tag_num);
				ans.list.add(instr);
			}
			return ans;
		}
		if (now.type.equals("variable")|| now.type.equals("input_variable")){
			scope_info.get(scope_info.size()-1).scope.get(now.son.get(1).name).activate = true;
		}
		if (now.has_scope) {
			scope_info.remove(scope_info.size() - 1);
		}
		return null;
	}
	//this is the register matching
	public static void try_add(simple_addr addr){
		if (addr==null) return;
		if ((addr.type.equals("Reg")&&addr.name.equals(""))||(addr.type.equals("Var")&&addr.num>0)){
			if (!RegNum.containsKey(addr.generate_String())){
				RegNum.put(addr.generate_String(),RegList.size());
				RegList.add(addr);
				if (addr.type.equals("Var")){
					if (addr.num>maxscope) maxscope = (int)(addr.num);
				}
			}
		}
		if (addr.type.equals("Mem x8")||addr.type.equals("Mem x1")){
			try_add(addr.op1);
			try_add(addr.offset);
		}
	}

	public static void try_add_Used(simple_addr addr,int line_num){
		if (addr==null) return;
		if ((addr.type.equals("Reg")&&addr.name.equals(""))||(addr.type.equals("Var")&&addr.num>0)){
			int addr_num = RegNum.get(addr.generate_String());
			UsedLine.get(addr_num).add(line_num);
		}
		if (addr.type.equals("Mem x8")||addr.type.equals("Mem x1")){
			try_add_Used(addr.op1,line_num);
			try_add_Used(addr.offset,line_num);
		}
	}
	public static void try_add_Maked(simple_addr addr,int line_num){
		if (addr==null) return;
		if ((addr.type.equals("Reg")&&addr.name.equals(""))||(addr.type.equals("Var")&&addr.num>0)){
			int addr_num = RegNum.get(addr.generate_String());
			MakedLine.get(addr_num).add(line_num);
		}
		if (addr.type.equals("Mem x8")||addr.type.equals("Mem x1")){
			try_add_Used(addr.op1,line_num);
			try_add_Used(addr.offset,line_num);
		}
	}

	public static void collect_tag_line_num(){
		for (int i=0;i<IR_.list.size();++i){
			simple_instr item = IR_.list.get(i);
			if (item.name.equals("Tag")){
				tag_line_num.put(item.tag_num,i);
			}
		}
	}
	public static HashSet<Integer> op_and (HashSet<Integer> a,HashSet<Integer> b){
		HashSet<Integer> ans = new HashSet<Integer>();
		ans.addAll(a);
		ans.retainAll(b);
		return ans;
	}
	public static HashSet<Integer> op_or(HashSet<Integer> a, HashSet<Integer> b){
		HashSet<Integer> ans = new HashSet<Integer>();
		ans.addAll(a);
		ans.addAll(b);
		return ans;
	}

	public static void decide_edge(int i){
		simple_instr instr = IR_.list.get(i);
		if (instr.addr1!=null&&instr.addr1.type.equals("Tag")){
			line_next.get(i).add(tag_line_num.get((int)(instr.addr1.num)));
			line_last.get(tag_line_num.get((int)(instr.addr1.num))).add(i);
		}
		if (!instr.name.equals("JAL")&&!instr.name.equals("Return")&&i+1<IR_.list.size()){
			line_next.get(i).add(i+1);
			line_last.get(i+1).add(i);
		}
	}
	public static void visit_without_reg(int i,String without_reg,boolean tf){
		if (tf||(!visited.contains(i)&&	(IR_.list.get(i).addr1==null||
				!IR_.list.get(i).addr1.generate_String().equals(without_reg)) ) ){
			visited.add(i);
			for (Integer j: line_last.get(i)){
				visit_without_reg(j,without_reg,false);
			}
		}
	}
	public static void visit_without_reg2(int i,String without_reg){
		if (!visited2.contains(i)){
			visited2.add(i);
			for (Integer j: line_next.get(i)){
				visit_without_reg2(j,without_reg);
			}
		}
	}

	public static void RegMatch()throws Exception{
		maxscope = 0;
		for (int j=0;j<IR_.list.size();++j){
			simple_instr instr = IR_.list.get(j);
			try_add(instr.addr1);
			try_add(instr.addr2);
			try_add(instr.addr3);
		}
		UsedLine = new ArrayList<HashSet<Integer>>();
		MakedLine = new ArrayList<HashSet<Integer>>();
		KeepedLine = new ArrayList<HashSet<Integer>>();
//		maxscope = 0;


		for (int j=0;j<RegList.size();++j){
			UsedLine.add(new HashSet<Integer>());
			MakedLine.add(new HashSet<Integer>());
			KeepedLine.add(new HashSet<Integer>());
		}
		for (int j=0;j<IR_.list.size();++j){
			simple_instr instr = IR_.list.get(j);
			try_add_Maked(instr.addr1,j);
			try_add_Used(instr.addr2,j);
			try_add_Used(instr.addr3,j);
		}
		for (int j=0;j<RegList.size();++j){
			visited = new HashSet<Integer>();
			visited2 = new HashSet<Integer>();
			for (Integer n1:UsedLine.get(j)){
				visit_without_reg(n1,RegList.get(j).generate_String(),true);
			}
			for (Integer n1:MakedLine.get(j)){
				visit_without_reg2(n1,RegList.get(j).generate_String());
			}
			KeepedLine.get(j).addAll(visited);
			KeepedLine.get(j).retainAll(visited2);
			KeepedLine.get(j).addAll(UsedLine.get(j));
			//this is safety belt
			KeepedLine.get(j).addAll(MakedLine.get(j));

			//System.err.println(RegList.get(j).generate_String()+"\t\t"+KeepedLine.get(j));
		}
		/* upper is generating keeped line
		todo add optimizor here */


		RegColor = new int[RegList.size()];
		//arr = new int[IR_.size()][IR_.size()];

		RegOccupyList = new ArrayList<HashSet<Integer>>();
		colornum = 1;
		for (int j=0;j<RegList.size()+10;++j){
			HashSet <Integer> occupied = new HashSet<Integer>();
			for (int k=0;k<RegList.size();++k)if (RegList.get(k).type.equals("Reg")){
				if (RegColor[k]==0 && op_and(occupied,KeepedLine.get(k)).size()==0){
					RegColor[k] = colornum;
					occupied = op_or(occupied,KeepedLine.get(k));
				}
			}
			for (int scopenum = maxscope;scopenum>0;--scopenum){
				for (int k=0;k<RegList.size();++k)
				if (RegList.get(k).type.equals("Var")&&RegList.get(k).num==scopenum){
					if (RegColor[k]==0 && op_and(occupied,KeepedLine.get(k)).size()==0){
						RegColor[k] = colornum;
						occupied = op_or(occupied,KeepedLine.get(k));
					}
				}
//				System.err.println(scopenum);
			}

			if (occupied.size()==0){
				j = RegList.size()+20;
				//System.err.println("end_coloring");
				colornum -= 1;
			} else{
				//System.err.println(occupied);
				RegOccupyList.add(occupied);
				colornum += 1;
			}
		}
		for (int j=0;j<RegList.size();++j){
			//if (j%10==0) System.err.println();
			//System.err.print("\t"+RegList.get(j).view()+" :"+RegColor[j]);

		}
		//System.err.println("\n"+colornum);
	}
	//todo pause codeGen ,fix reg because of some bugs
	public static String data_addr_name(simple_addr addr) throws Exception{
		return "Var_"+addr.name;
	}

	public static String addr_name(simple_addr addr)throws Exception{
		if (addr.type.equals("Var")&&addr.num==0){
			return " qword [rel Var_"+addr.name+"] ";
		}
		if (addr.type.equals("Val")){
			if (addr.str_const==null)
				return ""+(addr.num);
			else
				return string_const.naming_strategy((int)addr.num);
		}
		if (addr.type.equals("Reg")&&addr.name.equals("Return")){
			return codeGen_Reg.RetReg;
		}
		if (addr.type.equals("Reg")&&!addr.name.equals("Return")){
			return codeGen_Reg.R(RegColor[RegNum.get(addr.generate_String())]);
		}
		if (addr.type.equals("Var")&&addr.num>0){
			return codeGen_Reg.R(RegColor[RegNum.get(addr.generate_String())]);
		}
		if (addr.type.equals("Mem x1")){
			return " byte ["+addr_name(addr.op1)+"+"+addr_name(addr.offset)+"]";
		}
		if (addr.type.equals("Mem x8")){
			return " qword ["+addr_name(addr.op1)+"+"+addr_name(addr.offset)+"*8]";
		}
//		if (addr.type.equals())
		return "ERROR!";
	}
	public static int addr_type(simple_addr addr)throws Exception{
		/*
		used directedly 1
		memory use 2
		invalid memory use 3
		*/
		if (addr.type.equals("Var")&&addr.num==0){
			return 2;
		}
		if (addr.type.equals("Val")){
			return 1;
		}
		if (addr.type.equals("Reg")&&addr.name.equals("Return")){
			return 1;
		}
		if (addr.type.equals("Reg")&&!addr.name.equals("Return")){
			return codeGen_Reg.R_type(RegColor[RegNum.get(addr.generate_String())]);
		}
		if (addr.type.equals("Var")&&addr.num>0){
			return codeGen_Reg.R_type(RegColor[RegNum.get(addr.generate_String())]);
		}
		if (addr.type.equals("Mem x1")){
			if (addr_type(addr.offset)==1&&addr_type(addr.op1)==1){
				return 2;
			} else {
				return 3;
			}
		}
		if (addr.type.equals("Mem x8")){
			if (addr_type(addr.offset)==1&&addr_type(addr.op1)==1){
				return 2;
			} else {
				return 3;
			}
		}
		throw new Exception("Unknown Address Type");
//		return 4;
	}
//	public static ArrayList<String> move_into_optim(String reg,simple_addr addr)throws Exception{
//		ArrayList<String> ans = new ArrayList<String>();
//		ans.addAll(move_addr_into());
//	}
	public static ArrayList<String> move_addr_into(String reg,simple_addr addr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		ans.add("mov " + reg + "," + addr_name(addr.offset));
		if (addr.type.equals("Mem x8"))
			ans.add("shl " + reg + " 3");
		ans.add("add " + reg + "," + addr_name(addr.op1));
		return ans;
	}
	public static ArrayList<String> move_addr_into2(String reg,simple_addr addr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(addr.op1)==2&&addr_type(addr.offset)==2) {
			ans.add("mov " + reg + "," + addr_name(addr.offset));
			if (addr.type.equals("Mem x8"))
				ans.add("shl " + reg + ",3");
			ans.add("add " + reg + "," + addr_name(addr.op1));
			if (addr.type.equals("Mem x8"))
				move_addr2_cache = "qword["+reg+"]";
			else
				move_addr2_cache = "byte["+reg+"]";
		} else if (addr_type(addr.op1)==2&&addr_type(addr.offset)==1){
			ans.add("mov "+reg+","+addr_name(addr.op1));
			if (addr.type.equals("Mem x8"))
				move_addr2_cache = "qword["+reg+"+"+addr_name(addr.offset)+"*8]";
			else
				move_addr2_cache = "byte["+reg+"+"+addr_name(addr.offset)+"]";
		} else if (addr_type(addr.op1)==1&&addr_type(addr.offset)==2){
			ans.add("mov " + reg + "," + addr_name(addr.offset));
			if (addr.type.equals("Mem x8"))
				move_addr2_cache = "qword["+addr_name(addr.op1)+"+"+reg+"*8]";
			else
				move_addr2_cache = "byte["+addr_name(addr.op1)+"+"+reg+"]";
		} else if (addr_type(addr.op1)==1&&addr_type(addr.offset)==1){
			if (addr.type.equals("Mem x8"))
				move_addr2_cache = "qword["+addr_name(addr.op1)+"+"+addr_name(addr.offset)+"*8]";
			else
				move_addr2_cache = "byte["+addr_name(addr.op1)+"+"+addr_name(addr.offset)+"]";
		}
		return ans;
	}
	public static String get_val_from_addr(String reg)throws Exception{
		return "mov "+reg+",qword["+reg+"]";
	}

	public static ArrayList<String> add_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		String op1,op2,op3;
		if (addr_type(instr.addr1)==1&&addr_type(instr.addr2)<=2&&addr_type(instr.addr3)<=2){
			//op1 = addr_name(instr.addr1);
			ans.add("mov " + addr_name(instr.addr1) + "," + addr_name(instr.addr2));
			ans.add("add " + addr_name(instr.addr1) + "," + addr_name(instr.addr3));
			return ans;
		}else {
			if (addr_type(instr.addr2)==3){
				simple_addr addr = instr.addr2;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
				ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
//				ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
//				ans.add("mov "+codeGen_Reg.ConstReg1+", qword ["+codeGen_Reg.ConstReg1+"]");
			} else {
				ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
			}
			if (addr_type(instr.addr3)==3){
				simple_addr addr = instr.addr3;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
				ans.add("add "+codeGen_Reg.ConstReg1 + ","+move_addr2_cache);
			} else{
				ans.add("add "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr3));
			}
			if (addr_type(instr.addr1)<=2){
				ans.add("mov "+addr_name(instr.addr1) + ","+codeGen_Reg.ConstReg1);
			} else{
				simple_addr addr = instr.addr1;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
				ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
			}
		}
		return ans;
	}
	public static ArrayList<String> sub_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		String op1,op2,op3;
		if (addr_type(instr.addr1)==1&&addr_type(instr.addr2)<=2&&addr_type(instr.addr3)<=2){
			//op1 = addr_name(instr.addr1);
			ans.add("mov " + addr_name(instr.addr1) + "," + addr_name(instr.addr2));
			ans.add("sub " + addr_name(instr.addr1) + "," + addr_name(instr.addr3));
			return ans;
		}else {
			if (addr_type(instr.addr2)==3){
				simple_addr addr = instr.addr2;
				ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
				ans.add("mov "+codeGen_Reg.ConstReg1+",qword["+codeGen_Reg.ConstReg1+"]");
			} else {
				ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
			}
			if (addr_type(instr.addr3)==3){
				simple_addr addr = instr.addr3;
				ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
				ans.add("sub "+codeGen_Reg.ConstReg1 + ",qword["+codeGen_Reg.ConstReg2+"]");
			} else{
				ans.add("sub "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr3));
			}
			if (addr_type(instr.addr1)<=2){
				ans.add("mov "+addr_name(instr.addr1) + ","+codeGen_Reg.ConstReg1);
			} else{
				simple_addr addr = instr.addr1;
				ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
				ans.add("mov qword["+codeGen_Reg.ConstReg2 +"],"+codeGen_Reg.ConstReg1);
			}
		}
		return ans;
	}
	public static ArrayList<String> mul_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr2)==3){
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
//			ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
//			ans.add("mov "+codeGen_Reg.ConstReg1+",qword["+codeGen_Reg.ConstReg1+"]");
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);
//			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
//			ans.add("mov "+codeGen_Reg.ConstReg2+",qword["+codeGen_Reg.ConstReg2+"]");
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg2+","+addr_name(instr.addr3));
		}
//		ans.add("imul "+codeGen_Reg.ConstReg2);
		ans.add("imul edx");
		ans.add("cdqe");
		if (addr_type(instr.addr1)==3){
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
//			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
//			ans.add("mov qword["+codeGen_Reg.ConstReg2+"] , "+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+ addr_name(instr.addr1)+", "+codeGen_Reg.ConstReg1);
		}
		//todo ......
		return ans;
	}
	public static ArrayList<String> div_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr2)==3){
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg3,addr));
			ans.add("mov "+codeGen_Reg.ConstReg3+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg3+","+addr_name(instr.addr3));
		}
		//ans.add("mov rcx, rdx");
		ans.add("mov rdx,0");
		ans.add("cdq");
		ans.add("idiv ecx");
		ans.add("cdqe");
		//ans.add("mov rax,eax");
		if (addr_type(instr.addr1)==3){
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+ addr_name(instr.addr1)+", "+codeGen_Reg.ConstReg1);
		}
		//todo ......
		return ans;
	}
	public static ArrayList<String> mod_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr2)==3){
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg3,addr));
			ans.add("mov "+codeGen_Reg.ConstReg3+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg3+","+addr_name(instr.addr3));
		}
		//ans.add("mov rcx, rdx");
		ans.add("mov rdx,0");
		ans.add("cdq");
		ans.add("idiv ecx");
		ans.add("mov eax,edx");
		ans.add("cdqe");
		if (addr_type(instr.addr1)==3){
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+ addr_name(instr.addr1)+", "+codeGen_Reg.ConstReg1);
		}
		//todo ......
		return ans;
	}
	public static ArrayList<String> plusplus_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr1)<=2){
			ans.add("add "+addr_name(instr.addr1)+",1");
			return ans;
		}
		if (addr_type(instr.addr1)==2){
			ans.add("mov "+ codeGen_Reg.ConstReg1 +","+addr_name(instr.addr1));
			ans.add("add "+ codeGen_Reg.ConstReg1 + ",1");
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
			return ans;
		}
		if (addr_type(instr.addr1)==3){
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);

			ans.add("add "+codeGen_Reg.ConstReg2+ ",1");
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg2);
			return ans;
		}
		return ans;
	}
	public static ArrayList<String> plusplus8_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr1)<=2){
			ans.add("add "+addr_name(instr.addr1)+",8");
			return ans;
		}
		if (addr_type(instr.addr1)==2){
			ans.add("mov "+ codeGen_Reg.ConstReg1 +","+addr_name(instr.addr1));
			ans.add("add "+ codeGen_Reg.ConstReg1 + ",8");
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
			return ans;
		}
		if (addr_type(instr.addr1)==3){
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);

			ans.add("add "+codeGen_Reg.ConstReg2+ ",8");
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg2);
			return ans;
		}
		return ans;
	}
	public static ArrayList<String> minusminus_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr1)<=2){
			ans.add("sub "+addr_name(instr.addr1)+",1");
			return ans;
		}
		if (addr_type(instr.addr1)==2){
			ans.add("mov "+ codeGen_Reg.ConstReg1 +","+addr_name(instr.addr1));
			ans.add("sub "+ codeGen_Reg.ConstReg1 + ",1");
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
			return ans;
		}
		if (addr_type(instr.addr1)==3){
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);

			ans.add("sub "+codeGen_Reg.ConstReg2+ ",1");
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg2);
			return ans;
		}
		return ans;
	}
	public static ArrayList<String> less_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		String tag1 = codeGen_Reg.give_new_tag();
		String tag2 = codeGen_Reg.give_new_tag();
		if (addr_type(instr.addr2)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr2));
		} else{
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+",qword["+codeGen_Reg.ConstReg1+"]");
		}
		if (addr_type(instr.addr3)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg2+ ","+ addr_name(instr.addr3));
		} else{
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+",qword["+codeGen_Reg.ConstReg2+"]");
		}
		ans.add("cmp "+codeGen_Reg.ConstReg1+","+codeGen_Reg.ConstReg2);
		ans.add("jl "+tag1);
		ans.add("mov "+codeGen_Reg.ConstReg1+",0");
		ans.add("jmp "+tag2);
		ans.add(tag1+":");
		ans.add("mov "+codeGen_Reg.ConstReg1+",1");
		ans.add(tag2+":");
		if (addr_type(instr.addr1)<=2){
			ans.add("mov "+ addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		} else {
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov qword["+codeGen_Reg.ConstReg2+"],"+codeGen_Reg.ConstReg1);
		}
		return ans;
	}
	public static ArrayList<String> condition_judge_cg(String op,simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		String tag1 = codeGen_Reg.give_new_tag();
		String tag2 = codeGen_Reg.give_new_tag();
		if (addr_type(instr.addr2)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr2));
		} else{
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+",qword["+codeGen_Reg.ConstReg1+"]");
		}
		if (addr_type(instr.addr3)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg2+ ","+ addr_name(instr.addr3));
		} else{
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+",qword["+codeGen_Reg.ConstReg2+"]");
		}
		ans.add("cmp "+codeGen_Reg.ConstReg1+","+codeGen_Reg.ConstReg2);
		ans.add(op+" "+tag1);
		ans.add("mov "+codeGen_Reg.ConstReg1+",0");
		ans.add("jmp "+tag2);
		ans.add(tag1+":");
		ans.add("mov "+codeGen_Reg.ConstReg1+",1");
		ans.add(tag2+":");
		if (addr_type(instr.addr1)<=2){
			ans.add("mov "+ addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		} else {
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov qword["+codeGen_Reg.ConstReg2+"],"+codeGen_Reg.ConstReg1);
		}
		return ans;
	}
	public static ArrayList<String> condition_judge_cg2(String op,simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		String tag1 = codeGen_Reg.give_new_tag();
		String tag2 = codeGen_Reg.give_new_tag();
		if (addr_type(instr.addr2)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr2));
		} else{
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		}
		if (addr_type(instr.addr3)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg2+ ","+ addr_name(instr.addr3));
		} else{
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);
		}
		ans.add("cmp "+codeGen_Reg.ConstReg1+","+codeGen_Reg.ConstReg2);
		ans.add(op+" al");
		ans.add("movzx eax,al");

		if (addr_type(instr.addr1)<=2){
			ans.add("mov "+ addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		} else {
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		}
		return ans;
	}
	public static ArrayList<String> greater_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		String tag1 = codeGen_Reg.give_new_tag();
		String tag2 = codeGen_Reg.give_new_tag();
		if (addr_type(instr.addr2)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr2));
		} else{
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+",qword["+codeGen_Reg.ConstReg1+"]");
		}
		if (addr_type(instr.addr3)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg2+ ","+ addr_name(instr.addr3));
		} else{
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+",qword["+codeGen_Reg.ConstReg2+"]");
		}
		ans.add("cmp "+codeGen_Reg.ConstReg1+","+codeGen_Reg.ConstReg2);
		ans.add("jg "+tag1);
		ans.add("mov "+codeGen_Reg.ConstReg1+",0");
		ans.add("jmp "+tag2);
		ans.add(tag1+":");
		ans.add("mov "+codeGen_Reg.ConstReg1+",1");
		ans.add(tag2+":");
		if (addr_type(instr.addr1)<=2){
			ans.add("mov "+ addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		} else {
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov qword["+codeGen_Reg.ConstReg2+"],"+codeGen_Reg.ConstReg1);
		}
		return ans;
	}
	public static ArrayList<String> less_equal_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		String tag1 = codeGen_Reg.give_new_tag();
		String tag2 = codeGen_Reg.give_new_tag();
		if (addr_type(instr.addr2)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr2));
		} else{
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+",qword["+codeGen_Reg.ConstReg1+"]");
		}
		if (addr_type(instr.addr3)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg2+ ","+ addr_name(instr.addr3));
		} else{
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+",qword["+codeGen_Reg.ConstReg2+"]");
		}
		ans.add("cmp "+codeGen_Reg.ConstReg1+","+codeGen_Reg.ConstReg2);
		ans.add("jle "+tag1);
		ans.add("mov "+codeGen_Reg.ConstReg1+",0");
		ans.add("jmp "+tag2);
		ans.add(tag1+":");
		ans.add("mov "+codeGen_Reg.ConstReg1+",1");
		ans.add(tag2+":");
		if (addr_type(instr.addr1)<=2){
			ans.add("mov "+ addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		} else {
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov qword["+codeGen_Reg.ConstReg2+"],"+codeGen_Reg.ConstReg1);
		}
		return ans;
	}
	public static ArrayList<String> greater_equal_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		String tag1 = codeGen_Reg.give_new_tag();
		String tag2 = codeGen_Reg.give_new_tag();
		if (addr_type(instr.addr2)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr2));
		} else{
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+",qword["+codeGen_Reg.ConstReg1+"]");
		}
		if (addr_type(instr.addr3)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg2+ ","+ addr_name(instr.addr3));
		} else{
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+",qword["+codeGen_Reg.ConstReg2+"]");
		}
		ans.add("cmp "+codeGen_Reg.ConstReg1+","+codeGen_Reg.ConstReg2);
		ans.add("jge "+tag1);
		ans.add("mov "+codeGen_Reg.ConstReg1+",0");
		ans.add("jmp "+tag2);
		ans.add(tag1+":");
		ans.add("mov "+codeGen_Reg.ConstReg1+",1");
		ans.add(tag2+":");
		if (addr_type(instr.addr1)<=2){
			ans.add("mov "+ addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		} else {
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov qword["+codeGen_Reg.ConstReg2+"],"+codeGen_Reg.ConstReg1);
		}
		return ans;
	}
	public static ArrayList<String> equal_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		String tag1 = codeGen_Reg.give_new_tag();
		String tag2 = codeGen_Reg.give_new_tag();
		if (addr_type(instr.addr2)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr2));
		} else{
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+",qword["+codeGen_Reg.ConstReg1+"]");
		}
		if (addr_type(instr.addr3)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg2+ ","+ addr_name(instr.addr3));
		} else{
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+",qword["+codeGen_Reg.ConstReg2+"]");
		}
		ans.add("cmp "+codeGen_Reg.ConstReg1+","+codeGen_Reg.ConstReg2);
		ans.add("je "+tag1);
		ans.add("mov "+codeGen_Reg.ConstReg1+",0");
		ans.add("jmp "+tag2);
		ans.add(tag1+":");
		ans.add("mov "+codeGen_Reg.ConstReg1+",1");
		ans.add(tag2+":");
		if (addr_type(instr.addr1)<=2){
			ans.add("mov "+ addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		} else {
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov qword["+codeGen_Reg.ConstReg2+"],"+codeGen_Reg.ConstReg1);
		}
		return ans;

	}
	public static ArrayList<String> not_equal_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		String tag1 = codeGen_Reg.give_new_tag();
		String tag2 = codeGen_Reg.give_new_tag();
		if (addr_type(instr.addr2)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr2));
		} else{
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+",qword["+codeGen_Reg.ConstReg1+"]");
		}
		if (addr_type(instr.addr3)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg2+ ","+ addr_name(instr.addr3));
		} else{
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov "+codeGen_Reg.ConstReg2+",qword["+codeGen_Reg.ConstReg2+"]");
		}
		ans.add("cmp "+codeGen_Reg.ConstReg1+","+codeGen_Reg.ConstReg2);
		ans.add("jne "+tag1);
		ans.add("mov "+codeGen_Reg.ConstReg1+",0");
		ans.add("jmp "+tag2);
		ans.add(tag1+":");
		ans.add("mov "+codeGen_Reg.ConstReg1+",1");
		ans.add(tag2+":");
		if (addr_type(instr.addr1)<=2){
			ans.add("mov "+ addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		} else {
			simple_addr addr = instr.addr1;
			ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
			ans.add("mov qword["+codeGen_Reg.ConstReg2+"],"+codeGen_Reg.ConstReg1);
		}
		return ans;
	}
	public static ArrayList<String> assign_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr1)+addr_type(instr.addr2)<=3){
			ans.add("mov "+addr_name(instr.addr1)+","+addr_name(instr.addr2));
			return ans;
		} else{
			if (addr_type(instr.addr2)<=2) {
				ans.add("mov " +codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
			} else{
				simple_addr addr = instr.addr2;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
				ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache+"");
			}
			if (addr_type(instr.addr1)<=2) {
				ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
			} else{
				simple_addr addr = instr.addr1;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
				ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
			}
			return ans;
		}
//		return ans;
	}
	public static ArrayList<String> and_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr1)==1&&addr_type(instr.addr2)<=2&&addr_type(instr.addr3)<=2){
			ans.add("mov " + addr_name(instr.addr1) + "," + addr_name(instr.addr2));
			ans.add("and " + addr_name(instr.addr1) + "," + addr_name(instr.addr3));
			return ans;
		}else {
			if (addr_type(instr.addr2)==3){
				simple_addr addr = instr.addr2;
				ans.addAll(move_addr_into(codeGen_Reg.ConstReg1,addr));
				ans.add("mov "+codeGen_Reg.ConstReg1+", qword ["+codeGen_Reg.ConstReg1+"]");
			} else {
				ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
			}
			if (addr_type(instr.addr3)==3){
				simple_addr addr = instr.addr3;
				ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
				ans.add("and "+codeGen_Reg.ConstReg1 + ", qword["+codeGen_Reg.ConstReg2+"]");
			} else{
				ans.add("and "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr3));
			}
			if (addr_type(instr.addr1)<=2){
				ans.add("mov "+addr_name(instr.addr1) + ","+codeGen_Reg.ConstReg1);
			} else{
				simple_addr addr = instr.addr1;
				ans.addAll(move_addr_into(codeGen_Reg.ConstReg2,addr));
				ans.add("mov qword["+codeGen_Reg.ConstReg2 +"],"+codeGen_Reg.ConstReg1);
			}
		}
		return ans;
	}
	public static ArrayList<String> like_add_cg(String op,simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr1)==1&&addr_type(instr.addr2)<=2&&addr_type(instr.addr3)<=2){
			ans.add("mov " + addr_name(instr.addr1) + "," + addr_name(instr.addr2));
			ans.add(op+" " + addr_name(instr.addr1) + "," + addr_name(instr.addr3));
			return ans;
		}else {
			if (addr_type(instr.addr2)==3){
				simple_addr addr = instr.addr2;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
				ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache+"");
			} else {
				ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
			}
			if (addr_type(instr.addr3)==3){
				simple_addr addr = instr.addr3;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
				ans.add(op+" "+codeGen_Reg.ConstReg1 + ","+move_addr2_cache+"");
			} else{
				ans.add(op+" "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr3));
			}
			if (addr_type(instr.addr1)<=2){
				ans.add("mov "+addr_name(instr.addr1) + ","+codeGen_Reg.ConstReg1);
			} else{
				simple_addr addr = instr.addr1;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
				ans.add("mov "+move_addr2_cache +","+codeGen_Reg.ConstReg1);
			}
		}
		return ans;
	}

	public static ArrayList<String> like_add2_cg_for_sh(String op,simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr1)==1&&addr_type(instr.addr2)<=2&&instr.addr3.type.equals("Val")) {
			ans.add("mov " + addr_name(instr.addr1) + "," + addr_name(instr.addr2));
			ans.add(op + " " + addr_name(instr.addr1) + "," + addr_name(instr.addr3));
			return ans;
		}
		else if (addr_type(instr.addr1)==1&&addr_type(instr.addr2)<=2&&addr_type(instr.addr3)<=2){
			if (codeGen_Reg.myreg.contains(1)){
//				ans.add("mov "+codeGen_Reg.ConstReg2+","+codeGen_Reg.ConstReg3);
//				ans.add("mov qword[rbp-8],rcx");
			}
			ans.add("mov " + addr_name(instr.addr1) + "," + addr_name(instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg3+","+addr_name(instr.addr3));
			ans.add(op+" "+addr_name(instr.addr1)+",cl");
			if (codeGen_Reg.myreg.contains(1)){
//				ans.add("mov "+codeGen_Reg.ConstReg3+","+codeGen_Reg.ConstReg2);
//				ans.add("mov rcx,qword[rbp-8]");
			}

		}
		else {
			if (codeGen_Reg.myreg.contains(1)){
//				ans.add("mov "+codeGen_Reg.ConstReg2+","+codeGen_Reg.ConstReg3);
//				ans.add("mov qword[rbp-8],rcx");
			}

			if (addr_type(instr.addr2)==3){
				simple_addr addr = instr.addr2;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
				ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache+"");
			} else {
				ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
			}
			if (addr_type(instr.addr3)==3){
				simple_addr addr = instr.addr3;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg3,addr));
				ans.add("mov "+codeGen_Reg.ConstReg3 + ","+move_addr2_cache+"");
			} else{
				ans.add("mov "+codeGen_Reg.ConstReg3+","+addr_name(instr.addr3));
			}
			ans.add(op+" "+codeGen_Reg.ConstReg1+",cl");
			if (addr_type(instr.addr1)<=2){
				ans.add("mov "+addr_name(instr.addr1) + ","+codeGen_Reg.ConstReg1);
			} else{
				simple_addr addr = instr.addr1;
				ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
				ans.add("mov "+move_addr2_cache +","+codeGen_Reg.ConstReg1);
			}
			if (codeGen_Reg.myreg.contains(1)){
//				ans.add("mov "+codeGen_Reg.ConstReg3+","+codeGen_Reg.ConstReg2);
//				ans.add("mov rcx,qword[rbp-8]");
			}

		}
		return ans;
	}


	public static ArrayList<String> condition_jump_cg(String op,simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr2)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr2));
		} else{
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache+"");
		}
		if (addr_type(instr.addr3)<=2){
			ans.add("cmp "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr3));
		} else{
			simple_addr addr = instr.addr3;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,addr));
			ans.add("cmp "+codeGen_Reg.ConstReg1+","+move_addr2_cache+"");
		}
		ans.add(op+" "+instr.addr1.tag_str());
		return ans;
	}
	public static ArrayList<String> condition_zero_jump_cg(String op,simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr2)<=2){
			ans.add("mov "+ codeGen_Reg.ConstReg1+ ","+ addr_name(instr.addr2));
		} else{
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache+"");
		}
		ans.add("cmp "+ codeGen_Reg.ConstReg1+ ",0");

		ans.add(op+" "+instr.addr1.tag_str());
		return ans;
	}
	public static ArrayList<String> tag_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		ans.add("tag_old"+instr.tag_num+":");
		return ans;
	}
	public static ArrayList<String> JAL_jump_cg(simple_instr instr)throws Exception {
		ArrayList<String> ans = new ArrayList<String>();
//		if ()
		ans.add("jmp tag_old"+instr.addr1.num);
		return ans;
	}

	public static ArrayList<String> return_cg(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr2)==3){
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+", "+move_addr2_cache+"");
		} else {
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		ans.add("mov rsp,rbp");
		ans.add("pop rbp");
//		ans.add("leav");
		ans.add("ret");
		return ans;
	}


	public static ArrayList<String> return_mem_cg(simple_instr instr,int func_overall_num)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr2)==3){
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+", "+move_addr2_cache+"");
		} else {
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		ans.add("mov qword[func_y],rax");

		ans.add("mov rdx,qword[rbp+16]");
		ans.add("mov qword[func_x],rdx");
		ans.add("mov qword[func_num],"+IR_fixer_in_mem.mem_loc_info.get(func_overall_num));
		ans.add("call set_func");
		ans.add("mov rax,qword[func_y]");

		ans.add("mov rsp,rbp");
		ans.add("pop rbp");
//		ans.add("leav");
		ans.add("ret");
		return ans;
	}

	public static ArrayList<String> return_mem2_cg(simple_instr instr,int func_overall_num)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr2)==3){
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("mov "+codeGen_Reg.ConstReg1+", "+move_addr2_cache+"");
		} else {
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		ans.add("mov qword[func_y],rax");

		ans.add("mov rdx,qword[rbp+16]");
		ans.add("mov rcx,qword[rbp+24]");
		ans.add("mov qword[func_x],rdx");
		ans.add("mov qword[func_x2],rcx");
		ans.add("mov qword[func_num],"+IR_fixer_in_mem.mem_loc_info2.get(func_overall_num));
		ans.add("call set_func2");
		ans.add("mov rax,qword[func_y]");

		ans.add("mov rsp,rbp");
		ans.add("pop rbp");
//		ans.add("leav");
		ans.add("ret");
		return ans;
	}



	public static ArrayList<String> push_function(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr2)==3){
			simple_addr addr = instr.addr2;
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
			ans.add("push "+move_addr2_cache+"");
		} else {
			ans.add("push "+addr_name(instr.addr2));
		}
		return ans;
	}
	public static ArrayList<String> pop_function(simple_instr instr)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		if (addr_type(instr.addr1)==3){
			throw new Exception("pop_function_error:not a input");
//			simple_addr addr = instr.addr2;
//			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,addr));
//			ans.add("push "+move_addr2_cache+"");
		} else {
			//ans.add("push "+addr_name(instr.addr2));
			if (addr_type(instr.addr1)==2){
				ans.add("mov "+codeGen_Reg.ConstReg2+","+"qword[rbp+"+((int)(instr.addr2.num+2)*8)+"]");
				ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg2);
			} else
			ans.add("mov "+addr_name(instr.addr1)+","+"qword[rbp+"+((int)(instr.addr2.num+2)*8)+"]");
		}
		return ans;
	}
	public static ArrayList<String> call_function_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("call "+codeGen_rename.renaming(instr.addr2.name));
		ans.add("add rsp,"+instr.addr3.num*8);
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));
		return ans;
	}
	public static ArrayList<String> call_println_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("call println");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));
		return ans;
	}

	public static ArrayList<String> call_print_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("call print");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));
		return ans;
	}

	public static ArrayList<String> call_toString_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("call toString");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));
		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_mallocx8_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("call mallocx8");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_parseInt_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("call parseInt");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_str_add_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr3));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg2+","+addr_name(instr.addr3));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("mov rsi,rdx");
		ans.add("call str_add");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}


	public static ArrayList<String> call_str_le_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr3));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg2+","+addr_name(instr.addr3));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("mov rsi,rdx");
		ans.add("call str_le");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_str_l_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr3));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg2+","+addr_name(instr.addr3));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("mov rsi,rdx");
		ans.add("call str_l");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_str_ge_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr3));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg2+","+addr_name(instr.addr3));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("mov rsi,rdx");
		ans.add("call str_ge");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_str_g_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr3));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg2+","+addr_name(instr.addr3));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("mov rsi,rdx");
		ans.add("call str_g");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_str_e_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr3));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg2+","+addr_name(instr.addr3));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("mov rsi,rdx");
		ans.add("call str_e");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_str_ne_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr3));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg2+","+addr_name(instr.addr3));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("mov rsi,rdx");
		ans.add("call str_ne");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}


	public static ArrayList<String> call_ord_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		if (addr_type(instr.addr2)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg1,instr.addr2));
			ans.add("mov "+codeGen_Reg.ConstReg1+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg1+","+addr_name(instr.addr2));
		}
		if (addr_type(instr.addr3)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr3));
			ans.add("mov "+codeGen_Reg.ConstReg2+","+move_addr2_cache);
		} else{
			ans.add("mov "+codeGen_Reg.ConstReg2+","+addr_name(instr.addr3));
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("mov rdi,rax");
		ans.add("mov rsi,rdx");
		ans.add("call ord");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_getString_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("call getString");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_getInt_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("call getInt");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}

		return ans;
	}

	public static ArrayList<String> call_substring_cg(simple_instr instr,int linenum)throws Exception{
		ArrayList<String> ans = new ArrayList<String>();
		HashSet<Integer> regused = new HashSet<Integer>();
		//System.err.println(colornum+","+RegOccupyList.size());
		for (int i=0;i<colornum;++i){
			if (RegOccupyList.get(i).contains(linenum)){
				regused.add(i);
			}
		}
		ans.addAll(codeGen_Reg.push_all(colornum,regused));
		ans.add("pop rdi");
		ans.add("pop rsi");
		ans.add("pop rdx");

		ans.add("call substring");
		ans.addAll(codeGen_Reg.pop_all(colornum,regused));

		/*if (addr_type(instr.addr1)==3){
			ans.addAll(move_addr_into2(codeGen_Reg.ConstReg2,instr.addr1));
			ans.add("mov "+move_addr2_cache+","+codeGen_Reg.ConstReg1);
		} else{
			ans.add("mov "+addr_name(instr.addr1)+","+codeGen_Reg.ConstReg1);
		}*/

		return ans;
	}

	public static void codeGen_data_place() throws Exception{
		//System.err.println("here is codeGen Data");
		for (simple_instr item : IR_.list){
			if (item.addr1!=null&&item.addr1.type.equals("Var")&&item.addr1.num==0){
				nasm_global_data.add(data_addr_name(item.addr1)+":");
				nasm_global_data.add("dq "+myHex.LongToHex(0));
				glob_list.add("global "+data_addr_name(item.addr1));
			}
		}
		for (int f1=0;f1<simple_addr.str_const_record.size();++f1){
			String item = simple_addr.str_const_record.get(f1);
			nasm_global_data.add(string_const.naming_strategy(f1)+":");
			nasm_global_data.add("dq "+string_const.toDataString(string_const.toArray(item)));
		}
//		for (String item:simple_addr.str_const_record){
//
//
//		}
		for (String item:nasm_global_data){
			os2.write((item+"\n").getBytes());
		}
		for (String item:glob_list){
			os3.write((item+"\n").getBytes());
		}
	}
	public static void codeGen(int func_overall_num) throws Exception{
		nasm_code = new ArrayList<String>();
		if (IR_.list.get(0).name.equals("Func")) {
			glob_list.add("global "+codeGen_rename.renaming(IR_.list.get(0).func_num));
			nasm_code.add(codeGen_rename.renaming(IR_.list.get(0).func_num)+": ;" +IR_.list.get(0).func_num);
			nasm_code.add("push rbp");
			nasm_code.add("mov rbp,rsp");
			nasm_code.add("sub rsp,"+(((colornum+5)/2)*2+IR_.list.get(0).addr2.num%2)*8);
			if (IR_.list.get(0).func_num.equals("Init..init")){
				if (IR_fixer_in_mem.mem_loc_info.size()+IR_fixer_in_mem.mem_loc_info2.size()>0){
					nasm_code.add("call init_func");
				}
			}
			if (IR_fixer_in_mem.mem_loc_info.containsKey(func_overall_num)){
				nasm_code.add("mov rdx,qword[rbp+16]");
				nasm_code.add("mov qword[func_x],rdx");
				nasm_code.add("mov qword[func_num],"+IR_fixer_in_mem.mem_loc_info.get(func_overall_num));
				nasm_code.add("call seek_func");
				nasm_code.add("cmp qword[func_valid],0");
				nasm_code.add("je mem_tag_"+func_overall_num);

				nasm_code.add("mov rax,qword[func_y]");
				nasm_code.add("mov rsp,rbp");
				nasm_code.add("pop rbp");
				nasm_code.add("ret");

				nasm_code.add("mem_tag_"+func_overall_num+":");
			}
			if (IR_fixer_in_mem.mem_loc_info2.containsKey(func_overall_num)){
				nasm_code.add("mov rdx,qword[rbp+16]");
				nasm_code.add("mov rcx,qword[rbp+24]");
				nasm_code.add("mov qword[func_x],rdx");
				nasm_code.add("mov qword[func_x2],rcx");
				nasm_code.add("mov qword[func_num],"+IR_fixer_in_mem.mem_loc_info2.get(func_overall_num));
				nasm_code.add("call seek_func2");
				nasm_code.add("cmp qword[func_valid],0");
				nasm_code.add("je mem_tag_"+func_overall_num);

				nasm_code.add("mov rax,qword[func_y]");
				nasm_code.add("mov rsp,rbp");
				nasm_code.add("pop rbp");
				nasm_code.add("ret");

				nasm_code.add("mem_tag_"+func_overall_num+":");
			}
			for (int i=1;i<IR_.list.size();++i){
				simple_instr instr = IR_.list.get(i);
				if (instr.name.equals("+")&&instr.type.equals("lr")){
					nasm_code.addAll(like_add_cg("add",instr));
				}
				else if (instr.name.equals("-")&&instr.type.equals("lr")){
					nasm_code.addAll(like_add_cg("sub",instr));
				}
				else if (instr.name.equals("*")){
					nasm_code.addAll(mul_cg(instr));
				}
				else if (instr.name.equals("/")){
					nasm_code.addAll(div_cg(instr));
				}
				else if (instr.name.equals("%")){
					nasm_code.addAll(mod_cg(instr));
				}
				else if (instr.name.equals("-")&&instr.type.equals("r")){
					instr.addr3 = instr.addr2;
					instr.addr2 = simple_addr.val_new(0);
					nasm_code.addAll(like_add_cg("sub",instr));
				}
				else if (instr.name.equals("++")){
					nasm_code.addAll(plusplus_cg(instr));
				}
				else if (instr.name.equals("++8")){
					nasm_code.addAll(plusplus8_cg(instr));
				}
				else if (instr.name.equals("--")){
					nasm_code.addAll(minusminus_cg(instr));
				}
				else if (instr.name.equals("<")){
					nasm_code.addAll(condition_judge_cg2("setl",instr));
				}
				else if (instr.name.equals("<=")){
					nasm_code.addAll(condition_judge_cg2("setle",instr));
				}
				else if (instr.name.equals(">")){
					nasm_code.addAll(condition_judge_cg2("setg",instr));
				}
				else if (instr.name.equals(">=")){
					nasm_code.addAll(condition_judge_cg2("setge",instr));
				}
				else if (instr.name.equals("==")){
					nasm_code.addAll(condition_judge_cg2("sete",instr));
				}
				else if (instr.name.equals("!=")){
					nasm_code.addAll(condition_judge_cg2("setne",instr));
				}
				else if (instr.name.equals("=")){
					nasm_code.addAll(assign_cg(instr));
				}
				else if (instr.name.equals("&&")){
					nasm_code.addAll(like_add_cg("and",instr));
				}
				else if (instr.name.equals("||")){
					nasm_code.addAll(like_add_cg("or",instr));
				}
				else if (instr.name.equals("&")){
					nasm_code.addAll(like_add_cg("and",instr));
				}
				else if (instr.name.equals("|")){
					nasm_code.addAll(like_add_cg("or",instr));
				}
				else if (instr.name.equals("^")){
					nasm_code.addAll(like_add_cg("xor",instr));
				}
				else if (instr.name.equals("!")){
					instr.addr3 = simple_addr.val_new(1);
					nasm_code.addAll(like_add_cg("xor",instr));
				}
				else if (instr.name.equals("~")){
					instr.addr3 = simple_addr.val_new(-1);
					nasm_code.addAll(like_add_cg("xor",instr));
				}
				else if (instr.name.equals("<<")){
					nasm_code.addAll(like_add2_cg_for_sh("shl",instr));
				}
				else if (instr.name.equals(">>")){
					nasm_code.addAll(like_add2_cg_for_sh("sar",instr));
				}
				else if (instr.name.equals("BNZ")){
					nasm_code.addAll(condition_zero_jump_cg("jne",instr));
				}
				else if (instr.name.equals("BZ")){
					nasm_code.addAll(condition_zero_jump_cg("je",instr));
				}
				else if (instr.name.equals("BLT")){
					nasm_code.addAll(condition_jump_cg("jl",instr));
				}
				else if (instr.name.equals("Tag")){
					nasm_code.addAll(tag_cg(instr));
				}
				else if (instr.name.equals("JAL")){
					nasm_code.addAll(JAL_jump_cg(instr));
				}
				else if (instr.name.equals("Return")){
					if (IR_fixer_in_mem.mem_loc_info.containsKey(func_overall_num)){
						nasm_code.addAll(return_mem_cg(instr,func_overall_num));
					}
					if (IR_fixer_in_mem.mem_loc_info2.containsKey(func_overall_num)){
						nasm_code.addAll(return_mem2_cg(instr,func_overall_num));
					}
					else {
						nasm_code.addAll(return_cg(instr));
					}
				}
				else if (instr.name.equals("push_function")){
					nasm_code.addAll(push_function(instr));
				}
				else if (instr.name.equals("call")){
					nasm_code.addAll(call_function_cg(instr,i));
				}
				else if (instr.name.equals("pop_function")){
					nasm_code.addAll(pop_function(instr));
				}
				else if (instr.name.equals("toString")){
					nasm_code.addAll(call_toString_cg(instr,i));
				}
				else if (instr.name.equals("println")){
					nasm_code.addAll(call_println_cg(instr,i));
				}
				else if (instr.name.equals("print")){
					nasm_code.addAll(call_print_cg(instr,i));
				}
				else if (instr.name.equals("malloc x8")){
					nasm_code.addAll(call_mallocx8_cg(instr,i));
				}
				else if (instr.name.equals("parseInt")){
					nasm_code.addAll(call_parseInt_cg(instr,i));
				}
				else if (instr.name.equals("string_+")){
					nasm_code.addAll(call_str_add_cg(instr,i));
				}
				else if (instr.name.equals("string_<=")){
					nasm_code.addAll(call_str_le_cg(instr,i));
				}
				else if (instr.name.equals("string_<")){
					nasm_code.addAll(call_str_l_cg(instr,i));
				}
				else if (instr.name.equals("string_>=")){
					nasm_code.addAll(call_str_ge_cg(instr,i));
				}
				else if (instr.name.equals("string_>")){
					nasm_code.addAll(call_str_g_cg(instr,i));
				}
				else if (instr.name.equals("string_==")){
					nasm_code.addAll(call_str_e_cg(instr,i));
				}
				else if (instr.name.equals("string_!=")){
					nasm_code.addAll(call_str_ne_cg(instr,i));
				}
				else if (instr.name.equals("getString")){
					nasm_code.addAll(call_getString_cg(instr,i));
				}
				else if (instr.name.equals("getInt")){
					nasm_code.addAll(call_getInt_cg(instr,i));
				}
				else if (instr.name.equals("ord")){
					nasm_code.addAll(call_ord_cg(instr,i));
				}
				else if (instr.name.equals("call substring")){
					nasm_code.addAll(call_substring_cg(instr,i));
				}
				nasm_code.add(";"+instr.view());
			}
			for (String item: nasm_code){
				os1.write((item+"\n").getBytes());
			}
		}
	}
	public static void fix_together() throws Exception{
		InputStream fix_in;
		OutputStream fix_out;
		int len=0;
		fix_out = new FileOutputStream("result_all.asm");
		byte [] buffer ;
		int sisi=5000000;

		buffer = new byte[sisi];
		fix_in = new FileInputStream("info1.asm");
		len = fix_in.read(buffer);
		if (len<0) len = 0;
		fix_out.write(buffer,0,len);


		buffer = new byte[sisi];
		fix_in = new FileInputStream("result3.asm");
		len = fix_in.read(buffer);
		if (len<0) len = 0;
		fix_out.write(buffer,0,len);

		buffer = new byte[sisi];
		fix_in = new FileInputStream("info2.asm");
		len = fix_in.read(buffer);
		if (len<0) len = 0;
		fix_out.write(buffer,0,len);

		buffer = new byte[sisi];
		fix_in = new FileInputStream("info3.asm");
		len = fix_in.read(buffer);
		if (len<0) len = 0;
		fix_out.write(buffer,0,len);

		buffer = new byte[sisi];
		fix_in = new FileInputStream("result1.asm");
		len = fix_in.read(buffer);
		if (len<0) len = 0;
		fix_out.write(buffer,0,len);

		buffer = new byte[sisi];
		fix_in = new FileInputStream("info4.asm");
		len = fix_in.read(buffer);
		if (len<0) len = 0;
		fix_out.write(buffer,0,len);

		buffer = new byte[sisi];
		fix_in = new FileInputStream("result2.asm");
		len = fix_in.read(buffer);
		if (len<0) len = 0;
		//System.err.println(len);
		fix_out.write(buffer,0,len);

		buffer = new byte[sisi];
		fix_in = new FileInputStream("info5.asm");
		len = fix_in.read(buffer);
		if (len<0) len = 0;
		fix_out.write(buffer,0,len);


	}

	public static void input_fixer() throws Exception{
		InputStream fix_in;
		OutputStream fix_out;
		int len=0;
		fix_out = new FileOutputStream("program_fix.txt");
		byte [] buffer ;
		byte [] buffer1 ;

		int sisi=5000000;
		int clever  = 0;

		buffer = new byte[sisi];
		buffer1 = new byte[sisi];
		fix_in = new FileInputStream("program.txt");
		len = fix_in.read(buffer);
		if (len<0) len = 0;
		int buffer1_len = 0;
		for (int i=0;i<=len;++i){
			buffer1[buffer1_len++] = buffer[i];
			if (i-4>=0&&buffer[i-4]=='a'&&buffer[i-3]=='r'&&buffer[i-2]=='r'&&buffer[i-1]=='a'&&buffer[i]=='y'){
//				array
				buffer1[buffer1_len++] = '_';
			}
		}
		fix_out.write(buffer1,0,buffer1_len-1);

	}

	public static void IR_fix_in_mem_function(){
		IR_fixer_in_mem.IR_List = IR_List;
		IR_fixer_in_mem.run();
	}

	public static void view1(node now,int i)throws Exception{
		for (int j=0;j<i;++j){
			System.err.print("    ");
		}
		System.err.print(now.type + " " + now.name);
		if (now.type.equals("function")){
			System.err.print(" "+now.output_variable_type+" ");
			System.err.print(now.output_variable_array_dim);
			for (int j=0;j<now.input_variable_type.size();++j){
				System.err.print(" "+now.input_variable_type.get(j)+" ");
				System.err.print(now.input_variable_array_dim.get(j));
			}
		}
		System.err.println();
		for (HashMap.Entry<String,varname> item:now.scope.entrySet()){
			if (item.getValue().location!=null){
				view1(item.getValue().location,i+1);
			}
		}
	}
	public static void view2(node now,int i) throws Exception{
		if (now.has_scope){
			System.err.print(now.name);
			System.err.print("-");
			System.err.println(now.type);
		}
		for (node item:now.son){
			view2(item,i+1);
		}
	}
	public static void view3(node now,int i)throws Exception{
		for (int j=0;j<i;++j){
			System.err.print("    ");
		}
		System.err.print(now.type + " " + now.name);
		if (now.type.equals("function")){
			System.err.print(" "+now.output_variable_type+" ");
			System.err.print(now.output_variable_array_dim);
			for (int j=0;j<now.input_variable_type.size();++j){
				System.err.print(" "+now.input_variable_type.get(j)+" ");
				System.err.print(now.input_variable_array_dim.get(j));
			}
		}
		System.err.println();
		if (now.has_scope){
			for (int j=0;j<i;++j){
				System.err.print("    ");
			}
			System.err.print(" ");
			for (varname item:now.scope.values()){
				System.err.print("(");
				System.err.print(item.type);
				System.err.print(" ");
				System.err.print(item.name);
				System.err.print(" ");
				System.err.print(item.array_dim);
				System.err.print(")");
			}
			System.err.println();
		}
		for (HashMap.Entry<String,varname> item:now.scope.entrySet()){
			if (item.getValue().location!=null){
				view3(item.getValue().location,i+1);
			}
		}
	}
	public static void view4(node now,int i)throws Exception{
		if (now.has_scope){
			for (int j=0;j<i;++j){
				System.err.print("    ");
			}
			System.err.print(now.type + " " + now.name);
			if (now.type.equals("function")){
				System.err.print(" "+now.output_variable_type+" ");
				System.err.print(now.output_variable_array_dim);
				for (int j=0;j<now.input_variable_type.size();++j){
					System.err.print(" "+now.input_variable_type.get(j)+" ");
					System.err.print(now.input_variable_array_dim.get(j));
				}
			}
			System.err.println();
			for (int j=0;j<i;++j){
				System.err.print("    ");
			}
			System.err.print(" ");
			for (varname item:now.scope.values()){
				System.err.print("(");
				System.err.print(item.type);
				System.err.print(" ");
				System.err.print(item.name);
				System.err.print(" ");
				System.err.print(item.array_dim);
				System.err.print(")");
			}
			System.err.println();
		}
		for (node item:now.son){
			if (now.has_scope) {
				view4(item, i + 1);
			} else{
				view4(item,i);
			}
		}
	}
	public static void unactivate(node now,int i) throws  Exception{
		if (now.has_scope){
			for (varname item: now.scope.values()){
				if (item.activate) {
					item.activate = false;
					//System.err.println(item.type+" "+item.name);
				}
			}
		}
		for (node item: now.son){
			unactivate(item,i+1);
		}
	}
	public static void view5(node now,int i)throws Exception{
		for (int f1=0;f1<i;++f1){
			System.err.print("   :");
		}
		System.err.print(now.type+",");
		System.err.print(now.name+",");
		System.err.print(now.data_type+",");
		//System.err.print("data_arr_dim");
		System.err.print(now.data_array_dim);
		System.err.print("\n");
		for (node item:now.son){
			view5(item,i+1);
		}
	}
	public static void main(String[] args) throws IOException , Exception {
		input_fixer();
		try{
			//InputStream is = new FileInputStream("example/12.txt"); // or System.in;
			InputStream is = new FileInputStream("program_fix.txt"); // or System.in;
			//InputStream is = System.in; // or System.in;
			ANTLRInputStream input = new ANTLRInputStream(is);
			MxLexer lexer = new MxLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			MxParser parser = new MxParser(tokens);
			parser.setErrorHandler(new BailErrorStrategy());
			ParseTree tree = parser.mx(); // calc is the starting rule

			//System.err.println("LISP:");
			//System.err.println(tree.toStringTree(parser));
			//System.err.println();

			//System.err.println("Visitor:");
			EvalVisitor evalByVisitor = new EvalVisitor();
			root1 = evalByVisitor.visit(tree);
			////System.err.println();
			dfs(root1, 0);
			//System.err.println();
			//System.err.println("----");
			//--------------------------------
			{
				{
					varname newname = new varname();
					newname.type = "class";
					newname.name = "int";
					root1.scope.put(newname.name, newname);
				}
				{
					varname newname = new varname();
					newname.type = "class";
					newname.name = "bool";
					root1.scope.put(newname.name, newname);
				}
				{
					varname newname = new varname();
					newname.type = "class";
					newname.name = "void";
					root1.scope.put(newname.name, newname);
				}
				{
					varname newname = new varname();
					newname.type = "class";
					newname.name = "string";
					root1.scope.put(newname.name, newname);
				}
				{
					varname newname = new varname();
					newname.type = "function";
					newname.name = "print";
					root1.scope.put(newname.name, newname);
				}
				{
					varname newname = new varname();
					newname.type = "function";
					newname.name = "println";
					root1.scope.put(newname.name, newname);
				}
				{
					varname newname = new varname();
					newname.type = "function";
					newname.name = "getString";
					root1.scope.put(newname.name, newname);
				}
				{
					varname newname = new varname();
					newname.type = "function";
					newname.name = "getInt";
					root1.scope.put(newname.name, newname);
				}
				{
					varname newname = new varname();
					newname.type = "function";
					newname.name = "toString";
					root1.scope.put(newname.name, newname);
				}
			}
			System.err.println("dfs1");
			dfs1(root1, 0);
			System.err.println("dfs2");
			dfs2(root1, 0);
			System.err.println();
			System.err.println("dfs3");
			dfs3(root1, 0);
			System.err.println("view1");
			view1(root1, 0);
			System.err.println("view2");
			view2(root1, 0);
			System.err.println("dfs4");
			dfs4(root1, 0);
			System.err.println("view3");
			view3(root1, 0);
			System.err.println("view4");
			view4(root1, 0);
			System.err.println("dfs5");
			dfs5(root1, 0);
			if(root1.son.size()==0){
				throw new Exception("Empty");
			}
			if(!root1.scope.containsKey("main")){
				throw new Exception("No Main");
			}
			if (!root1.scope.get("main").type.equals("function")){
				throw new Exception("No Main");
			}
			if (!(root1.scope.get("main").location.output_variable_array_dim==0
					&&root1.scope.get("main").location.output_variable_type.equals("int"))){
				throw new Exception("main not good");
			}
		}catch (Throwable eee)
		{
			//throw new Exception("Well");
			//System.err.println("CE");
			throw eee;
			//System.exit(-1);
		}
		//System.err.println("OK");
		{
			//System.err.println("\nun activate");
			unactivate(root1,0);
			//System.err.println("\ndfs6");
			dfs6(root1,0);
			//view5(root1,0);


			instr_set ir = dfs7(root1,0);
			//System.err.print(ir.view());

			IR_.list.addAll(ir.list);
		}


		IR_fix_in_mem_function();

		{
			os1 = new FileOutputStream("result1.asm");
			os2 = new FileOutputStream("result2.asm");
			os3 = new FileOutputStream("result3.asm");
			for (int ii = 0;ii<IR_List.size();++ii){
				RegNum  = new HashMap<String,Integer>();
				RegList = new ArrayList<simple_addr>();
				instr_set item = IR_List.get(ii);
				System.err.print("\n"+item.view());
				IR_ = item;
//				path = new int[IR_.list.size()][IR_.list.size()];
				tag_line_num = new HashMap<Integer, Integer>();
				collect_tag_line_num();
//				for (int i=0;i<IR_.list.size();++i){
//					//System.err.print("step"+i+"\t:\t");
//					pathview(i,i);
//					//System.err.println();
//				}
				line_last = new ArrayList<HashSet<Integer>>();
				line_next = new ArrayList<HashSet<Integer>>();
				for (int i=0;i<IR_.list.size();++i){
					line_next.add(new HashSet<Integer>());
					line_last.add(new HashSet<Integer>());
				}
				for (int i=0;i<IR_.list.size();++i){
					decide_edge(i);
				}
				RegMatch();

				codeGen(ii);
			}
			{
				IR_ = IR_List.get(0);
				codeGen_data_place();
			}
//			System.err.println(codeGen_rename.renaming("123abc_._123"));
		}
		{
			fix_together();
			int runable = 0;
			if (IR_fixer_in_mem.check_program()){
				runable = 1;
			}
			OutputStream fix_out;
			fix_out = new FileOutputStream("lijiasen_signal.txt");
			fix_out.write((runable+"\n").getBytes());

		}
		System.err.println(IR_fixer_in_mem.mem_loc_info);
		System.err.println(IR_fixer_in_mem.mem_loc_info2);
		//System.err.println("finished");

	}
}
