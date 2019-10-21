import java.util.ArrayList;

public class simple_addr {

    public String type = "";//"Var" "Reg" "Val" "Func" "Class" "Tag"
    public String name = "";//Var:"i"  Reg:""  Val:""  Func:"dfs" Class:"myclass" Tag:""
    public long num = -1;//Var:scope num  (0 for global)    Reg:register index    Val:value
    public int addr_len = 8;
    public simple_addr op1 = null;
    public simple_addr offset = null;
	public String str_const = null;
    public static int str_const_cnt = 0;

    public static ArrayList<String> str_const_record = new ArrayList<String>();

    //addr is only an assistant variable
    public String generate_String(){
        return type+"|"+name+"|"+num;
    }
    //this string can be a key in map
    public static int register_cnt = 0;
    public static simple_addr reg_new(int len){
        simple_addr ans = new simple_addr();
        ans.type = "Reg";
        register_cnt += 1;
        ans.num = register_cnt;
        ans.addr_len = len;
        return ans;
    }
    public static simple_addr reg_num(int num){
        simple_addr ans = new simple_addr();
        ans.type = "Reg";
        ans.num = num;
        ans.addr_len = 8;
        return ans;
    }
    public static simple_addr var_new(String vname,int scope_num){
        simple_addr ans = new simple_addr();
        ans.type = "Var";
        ans.name = vname;
        ans.num = scope_num;
        //ans.addr_len
        return ans;
    }
    public static simple_addr tag_new(int tagnum){
        simple_addr ans = new simple_addr();
        ans.type = "Tag";
        ans.num = tagnum;
        return ans;
    }
    public static simple_addr val_new(long value){
        simple_addr ans = new simple_addr();
        ans.type = "Val";
        ans.num = value;
        return ans;
    }
    public static simple_addr string_const(String str){
		simple_addr ans = new simple_addr();
		ans.type = "Val";
		ans.num = str_const_cnt;
        str_const_cnt += 1;
        ans.str_const = str;
        str_const_record.add(str);
	    return ans;
    }
    public static simple_addr func_new(String func_name){
        simple_addr ans = new simple_addr();
        ans.type = "Func";
        ans.name = func_name;
        return ans;
    }
    public static simple_addr return_register(){
        simple_addr ans = new simple_addr();
        ans.type = "Reg";
        ans.name = "Return";
        return  ans;
    }
    public static simple_addr mem_new(long num){
        simple_addr ans = new simple_addr();
        ans.type = "Mem";
        ans.name = "Reg";
        ans.num = num;
        return ans;
    }
    public static simple_addr mem_offset(simple_addr op1,long offset){
        return mem_offset(op1,val_new(offset));
//        simple_addr ans = new simple_addr();
//        ans.type = "Mem x8";
//        ans.op1 = op1;
//        ans.num = offset;
//        return ans;
    }
    public static simple_addr mem_offset(simple_addr op1,simple_addr offset){
        simple_addr ans = new simple_addr();
        ans.type = "Mem x8";
        ans.op1 = op1;
        ans.offset = offset;
        return ans;
    }
	public static simple_addr mem_offset_x1(simple_addr op1,simple_addr offset){
        simple_addr ans = new simple_addr();
        ans.type = "Mem x1";
        ans.op1 = op1;
        ans.offset = offset;
        return ans;
    }

    public static simple_addr heap_pointer(){
        simple_addr ans = new simple_addr();
        ans.type = "Reg";
        ans.name = "Heap";
        //Heap start from 0 to 2^n
        return ans;
    }
    public String tag_str(){
        return "tag_old"+num;
    }

    public String view(){
        if (str_const!=null){
//             return "(" + type + "|" + name + "|" + num + "|"+ string_const.toString(string_const.toArray(str_const) )+ ")";
             return "(" + type + "|" + name + "|" + num + "|"+ str_const + ")";
        }
        if (!type.equals("Mem x8")) {
            return "(" + type + "|" + name + "|" + num + ")";
        } else{
            return "(" + type + "|" + name + "|" + num + "|"+op1.view()+"_"+offset.view()+")";
        }
    }
}
