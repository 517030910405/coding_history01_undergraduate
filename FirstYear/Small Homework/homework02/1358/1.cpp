#include<cmath>
#include<cstdio>
using namespace std;
int n;
struct Path{
	int tow,ne;
};
Path *path;
int *mp;
int *lv;
int *sztr;
int *ans;
int sz=0;
inline void addpath(const int &i, const int &j){
	path[++sz].tow=j;
	path[sz].ne=mp[i];
	mp[i]=sz;
}
void dfs(int p, int level){
	lv[p]=level;
	int pos=mp[p];
	while (pos!=0){
		if (lv[path[pos].tow]==0)dfs(path[pos].tow,level+1);
		pos=path[pos].ne;
	}
	pos=mp[p];
	int maxtr=0;
	while(pos!=0){
		if (lv[path[pos].tow]==level+1){
			sztr[p]+=sztr[path[pos].tow];
			if (sztr[path[pos].tow]>maxtr)maxtr=sztr[path[pos].tow];
		}
		pos=path[pos].ne;
	}
	if (maxtr<=n/2&&n-sztr[p]<=n/2)ans[p]=1;
	
}


int main(){
	scanf("%d",&n);
	path=new Path[n*2+4];
	mp=new int [n+2];
	lv=new int [n+2];
	ans=new int [n+2];
	sztr=new int [n+2];
	for (int f1=0;f1<=n;f1++){
		lv[f1]=0;
		mp[f1]=0;
		ans[f1]=0;
		sztr[f1]=1;
	}
	for (int f1=1;f1<=n-1;f1++){
		int i,j;
		scanf("%d%d",&i,&j);
		addpath(i,j);
		addpath(j,i);
	}
	dfs(1,1);
	for (int f1=1;f1<=n;f1++){
		if (ans[f1]==1){
			printf("%d\n",f1);
		}
	}
	
	
	delete [] path;
	delete [] mp;
	delete [] lv;
	delete [] ans;
	delete [] sztr;
	
}
