#include<bits/stdc++.h>
using namespace std;
int signal(){
	freopen("lijiasen_signal.txt","r",stdin);
	int ans = 0;
	scanf("%d",&ans);
	fclose(stdin);
	return ans;
}


int solve(){
	system("./run.bash result_all");
	system("./my > LiJiasen_Out_Put.lijiasen");
}
int head(){
	freopen("ofst1.asm","r",stdin);
	char c;
	while (scanf("%c",&c)!=EOF) printf("%c",c);
	fclose(stdin);
	return 0;
}
int tail(){
	freopen("ofst2.asm","r",stdin);
	char c;
	while (scanf("%c",&c)!=EOF) printf("%c",c);
	fclose(stdin);
	return 0;
}
int mid(){
	freopen("LiJiasen_Out_Put.lijiasen","r",stdin);
	char c;
	int x;
	while (scanf("%c",&c)!=EOF){
		x = c;
		printf(" %02xH,",x);
	}
	fclose(stdin);
	return 0;
	
}

int main(){
	if (signal()==0) return 0;
	solve();
	freopen("result_all.asm","w",stdout);
	head();
	mid();
	tail();
	fclose(stdout);
	return 0;
}