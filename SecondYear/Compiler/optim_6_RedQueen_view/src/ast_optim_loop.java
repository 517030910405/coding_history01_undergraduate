import java.util.*;

public class ast_optim_loop {
    public static node root;
    public static HashSet <String> visited_func = new HashSet<String>();
    public static String addString(String a,String b){
        return a+"."+b;
    }


    public static void func_dfs(String class_name,String func_name){
        if (visited_func.contains(addString(class_name,func_name))) return;
        visited_func.add(addString(class_name,func_name));
        root = pro1.root1;
        varname func_space;
        if (class_name.equals("")) {
            func_space = root.scope.get(func_name);
        }
    }
    void find_scope(node item){
        node scope_place = item;
        while (!(scope_place.has_scope&&scope_place.scope.containsKey(item.name))){
            item = item.father;
        }


    }


    public static boolean check_simple_function(){

        return true;
    }
    public static void dfs_find_parent(node now){
        for (node item:now.son){
            item.father = now;
        }
        for (node item:now.son){
            dfs_find_parent(now);
        }
    }
    public static void find_parent(){
        dfs_find_parent(pro1.root1);
    }

}
