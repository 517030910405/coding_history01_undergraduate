#include<cmath>
#include<cstdio>
using namespace std;
int n,m;
char mp[52][52],mp2[52][52];
void cope1(){
	for (int f1=0;f1<=n+1;++f1)
	for (int f2=0;f2<=m+1;++f2)
	mp2[f1][f2]=mp[f1][f2];
}
void floodfill1(int i,int j){
	if (mp2[i][j]!='#'){
		mp2[i][j]='#';
		floodfill1(i-1,j);
		floodfill1(i+1,j);
		floodfill1(i,j-1);
		floodfill1(i,j+1);
	}
}
int check1(){
	int ans=0;
	int an2=0;
	cope1();
	for (int f1=1;f1<=n;++f1){
		for (int f2=1;f2<=m;++f2){
			if (mp2[f1][f2]=='.'){
				++ans;
				floodfill1(f1,f2);
			} else if (mp2[f1][f2]=='?')++an2;
		}
	}
	if (ans>0) return ans;
	if (an2>0) return 1;
	return 0;
}
void check0(){
	int landcnt=0;
	int uncnt=0;
	for (int f1=1;f1<=n;f1++)for (int f2=1;f2<=m;f2++){
		if (mp[f1][f2]=='?')++uncnt;
		if (mp[f1][f2]=='.')++landcnt;
	}
	if (landcnt==0&&uncnt==1){
		for (int f1=1;f1<=n;f1++){
			for (int f2=1;f2<=m;f2++){
				if (mp[f1][f2]=='#')printf("#");else printf(".");
			}
			printf("\n");
		}
		return;
	}
	if (landcnt==0&&uncnt>1){
		printf("Ambiguous\n");
		return;
	}
	int cnt=check1();
	if (cnt>1||cnt==0){
		printf("Impossible\n");
		return;
	}
	
	
	for (int f1=1;f1<=n;f1++){
		for (int f2=1;f2<=m;f2++){
			if (mp2[f1][f2]=='?')mp[f1][f2]='#';
		}
	}
	for (int f1=1;f1<=n;f1++){
		for (int f2=1;f2<=m;f2++){
			if (mp[f1][f2]=='?'){
				mp[f1][f2]='#';
				if (check1()==1){
					printf("Ambiguous\n");
					return;
				}
				mp[f1][f2]='?';
			}
		}
	}
	for (int f1=1;f1<=n;f1++){
		for (int f2=1;f2<=m;f2++){
			if (mp[f1][f2]=='#')printf("#");else printf(".");
		}
		printf("\n");
	}
	return;
	
	
	
}

int main(){
	scanf("%d%d",&n,&m);
	for (int f1=0;f1<=n+1;f1++){mp[f1][0]='#';mp[f1][m+1]='#';}
	for (int f2=0;f2<=m+1;f2++){mp[0][f2]='#';mp[n+1][f2]='#';}
	for (int f1=1;f1<=n;f1++){
		for (int f2=1;f2<=m;f2++){
			scanf(" %c",&mp[f1][f2]);
		}
	}
	check0();
}



