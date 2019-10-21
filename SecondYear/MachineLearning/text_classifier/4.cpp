//洗数据1
//in:train_shuffle
//out:tr_1
//完善的洗数据：逐字洗数据
#include<bits/stdc++.h>
using namespace std;

map<string,int> id;
int cnt[1000];
int idcnt=0;
int max_line_cnt = 0;
int solve_2(char *str, int & linecnt);
int solve_3(char *str, int linecnt);

int solve(){
	int tag;
	int linecnt=0;
	char str[1000];
	if (scanf("%d",&tag)==EOF){
		return 0;
	}
	scanf("%s",str);
    solve_2(str,linecnt);
	printf("%d %d",tag,linecnt);
    solve_3(str,linecnt);
    return 1;
}
int solve2(){
    char str[1000];
    int linecnt=0;
    if (scanf("%s",str)==EOF){
        return 0;
    }
    solve_2(str,linecnt);
	printf("%d",linecnt);    
    solve_3(str,linecnt);
    return 1;
}

int solve_2(char *str, int & linecnt){
	int pos=0,end_p=strlen(str);
	while (pos<end_p){
		if (str[pos]<0){
			//printf(" %c%c%c",str[pos],str[pos+1],str[pos+2]);
			++linecnt;
			char str_th[5];
			str_th[0] = str[pos];
			str_th[1] = str[pos+1];
			str_th[2] = str[pos+2];
			str_th[3] = 0;
			string str_this = string(str_th);
			if (id.find(str_this)==id.end()){
				id[str_this] = ++idcnt;
			} else {
				
			}
			pos += 3;
		} else {
            ++linecnt;
			char str_th[100];
			int i=0;
			while (str[pos+i]>0) {
				str_th[i] = str[pos+i];
				++i;
			}
			str_th[i] = 0;
			//printf(" %s",str_th);
            pos += i;
			string str_this = string(str_th);
			if (id.find(str_this)==id.end()){
				id[str_this] = ++idcnt;
			} else {
				
			}
		}
	}
	//printf(" endln %d\n",linecnt);
    max_line_cnt = max(linecnt,max_line_cnt);
	return 1;
}
int solve_3(char *str,int linecnt){
	int pos=0,end_p=strlen(str);
	while (pos<end_p){
		if (str[pos]<0){
			//printf(" %c%c%c",str[pos],str[pos+1],str[pos+2]);
			//++linecnt;
			char str_th[5];
			str_th[0] = str[pos];
			str_th[1] = str[pos+1];
			str_th[2] = str[pos+2];
			str_th[3] = 0;
			string str_this = string(str_th);
			if (id.find(str_this)==id.end()){
				id[str_this] = ++idcnt;
                assert(false);
			} else {
				
			}
            printf(" %d",id[str_this]);
            //printf(" %d",id[str_this],str_this.c_str());
			pos += 3;
		} else {
            //++linecnt;
			char str_th[100];
			int i=0;
			while (str[pos+i]>0) {
				str_th[i] = str[pos+i];
				++i;
			}
			str_th[i] = 0;
			//printf(" %s",str_th);
            pos += i;
			string str_this = string(str_th);
			if (id.find(str_this)==id.end()){
				id[str_this] = ++idcnt;
                assert(false);
			} else {
				
			}
            printf(" %d",id[str_this]);
            //printf(" %d",id[str_this],str_this.c_str());
		}
	}
	printf("\n");
    //max_line_cnt = max(linecnt,max_line_cnt);
	return 1;
}


int main(){
	freopen("train_shuffle.txt","r",stdin);
    freopen("tr1.txt","w",stdout);
	while (solve()==1){}
	//exit(0);
	fclose(stdin);
	fclose(stdout);
	freopen("tr1_size.txt","w",stdout);
	printf("%d\n%d\n",idcnt,max_line_cnt);
	fclose(stdout);
    
	freopen("test_handout.txt","r",stdin);
    freopen("ts1.txt","w",stdout);
	while (solve2()==1){}
	//exit(0);
	fclose(stdin);
	fclose(stdout);
    freopen("ts1_size.txt","w",stdout);
	printf("%d\n%d\n",idcnt,max_line_cnt);
	fclose(stdout);
}