//Ï´Êý¾Ý1
//in:train_shuffle
//out:tr_1
#include<bits/stdc++.h>
using namespace std;

map<string,int> id;
int cnt[1000];
int idcnt=0;
int max_line_cnt = 0;


int solve(){
	int tag;
	int linecnt=0;
	char str[1000];
	if (scanf("%d",&tag)==EOF){
		return 0;
	}
	scanf("%s",str);
	printf("%d",tag);
	int pos=0,end_p=strlen(str);
	while (pos<end_p){
		if (str[pos]<0){
			printf(" %c%c%c",str[pos],str[pos+1],str[pos+2]);
			++linecnt;
			pos += 3;
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
		} else {
            ++linecnt;
			char str_th[100];
			int i=0;
			while (str[pos+i]>0) {
				str_th[i] = str[pos+i];
				++i;
			}
			str_th[i] = 0;
			printf(" %s",str_th);
            pos += i;
			string str_this = string(str_th);
			if (id.find(str_this)==id.end()){
				id[str_this] = ++idcnt;
			} else {
				
			}
		}
	}
	printf(" endln %d\n",linecnt);
    max_line_cnt = max(linecnt,max_line_cnt);
	return 1;
}
int main(){
	freopen("train_shuffle.txt","r",stdin);
	while (solve()==1){}
	printf("\n%d\n%d\n",idcnt,max_line_cnt);
	exit(0);
	fclose(stdin);
}