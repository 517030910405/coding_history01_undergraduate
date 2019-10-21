#include<cmath>
#include<cstdio>
using namespace std;
const int N=200000+10;
struct Path{
	int tow,ne;
	int va;
};
Path path[N*2];
int mp[N];
int sz=0;
int len[3][N];
int n;
int a,b,c;
inline void addpath(const int &i,const int &j,const int &va){
	path[++sz].tow=j;
	path[sz].ne=mp[i];
	path[sz].va=va;
	mp[i]=sz;
}
void dfs(const int &p, const int &lenp, const int &fr){
	len[fr][p]=lenp;
	int pos=mp[p];
	while (pos!=0){
		if (len[fr][path[pos].tow]==-1){
			dfs(path[pos].tow, path[pos].va + lenp, fr);
		}
		pos=path[pos].ne;
	}
}


int main(){
	scanf("%d%d%d%d",&n,&a,&b,&c);
	for (int f1=1;f1<=n;f1++){
		mp[f1]=0;
		len[0][f1]=-1;
		len[1][f1]=-1;
		len[2][f1]=-1;
	}
	for (int f1=1;f1<n;f1++){
		int i,j,k;
		scanf("%d%d%d",&i,&j,&k);
		addpath(i,j,k);
		addpath(j,i,k);
	}
	dfs(a,0,0);
	dfs(b,0,1);
	dfs(c,0,2);
	int ans=1;
	for (int f1=1;f1<=n;f1++){
		len[0][f1]+=len[1][f1]+len[2][f1];
	}
	for (int f1=1;f1<=n;f1++){
		if (len[0][f1]<len[0][ans])ans=f1;
	}
	printf("%d\n%d\n",ans,len[0][ans]);
}