#include<cmath>
#include<cstdio>
using namespace std;
const int N=10;
int cnt[(1<<N)+3];
int dp[(1<<N)+3][N];
int n,m,st,len;
int pth[N][N];
int ans=0;
int main(){
	scanf("%d%d%d%d",&n,&m,&st,&len);
	--st;
	for (int f1=0;f1<n;f1++)for (int f2=0;f2<n;f2++)pth[f1][f2]=0;
	for (int f1=1;f1<=m;f1++){
		int i,j;
		scanf("%d%d",&i,&j);
		--i;--j;
		pth[i][j]=1;
	}
	cnt[0]=0;
	for (int f1=1;f1<(1<<n);f1++){
		cnt[f1]=cnt[f1>>1]+(f1&1);
	}
	for (int f1=1;f1<(1<<n);f1++){
		for (int f2=0;f2<n;f2++){
			if (!bool((f1>>f2)&1)){
				dp[f1][f2]=0;
				
			} else if (f1==(1<<f2)){
				if (f2==st)dp[f1][f2]=1;
				else dp[f1][f2]=0;
				
			} else if (cnt[f1]==1){
				dp[f1][f2]=0;
			} else{
				dp[f1][f2]=0;
				for (int f3=0;f3<n;f3++){
					if (bool((f1>>f3)&1)&&bool(pth[f3][f2])){
						dp[f1][f2]+=dp[f1^(1<<f2)][f3];
					}
				}
			}
			if (cnt[f1]==len+1)ans+=dp[f1][f2];
		}
	}
	printf("%d\n",ans);
}
