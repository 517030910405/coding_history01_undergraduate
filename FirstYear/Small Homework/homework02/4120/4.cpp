#include<cmath>
#include<cstdio>

using namespace std;
const int N=100000+10;
const int M=300000+10;
struct Path{
	int ne,tow;
};
Path path[M*2];
int mpin[N],mpou[N],ind[N],oud[N];
int vis[N];
int df1num[N],df1order[N],sz1ord=0;
int col[N],szc,sz[N];
int par[N];
int szp=0;
int n,m;
int ans=0;
inline void addpathfr(const int &i, const int &j){
	path[++szp].tow=j;
	path[szp].ne=mpin[i];
	mpin[i]=szp;
}
inline void addpathto(const int &i, const int &j){
	path[++szp].tow=j;
	path[szp].ne=mpou[i];
	mpou[i]=szp;
}
void dfs1(int pos){
	if (vis[pos]!=0) return ;
	vis[pos]=1;
	int pa=mpou[pos];
	while (pa!=0){
		dfs1(path[pa].tow);
		pa=path[pa].ne;
	}
	df1order[++sz1ord]=pos;
	df1num[pos]=sz1ord;
}
void dfs2(int pos,int color,int &cnt){
	//printf(" %d",pos);
	if (vis[pos]!=1) return ;
	vis[pos]=2;
	col[pos]=color;
	++cnt;
	int pa=mpin[pos];
	while (pa!=0){
		dfs2(path[pa].tow,color,cnt);
		pa=path[pa].ne;
	}
	
}

int main(){
	scanf("%d%d",&n,&m);
	for (int f1=1;f1<=n;f1++){
		mpin[f1]=0;
		mpou[f1]=0;
		ind[f1]=0;
		oud[f1]=0;
	}
	for (int f1=1;f1<=m;f1++){
		int i,j;
		scanf("%d%d",&i,&j);
		addpathfr(j,i);
		addpathto(i,j);
		//++ind[j];
		//++oud[i];
	}
	for (int f1=1;f1<=n;f1++){
		dfs1(f1);
	}
	
	szc=0;
	for (int f1=n;f1>=1;f1--){
		int pos=df1order[f1];
		if (vis[pos]==1){
			++szc;
			dfs2(pos,szc,sz[szc]);
		}
	}
	//for (int f1=1;f1<=n;f1++){
		//printf("%d",col[f1]);
	//}
	for (int f1=1;f1<=n;f1++){
		int pa=mpou[f1];
		while (pa!=0){
			int j=path[pa].tow;
			if (col[j]!=col[f1]){
				//printf(" %d->%d",f1,j);
				++ind[col[j]];
				++oud[col[f1]];
			}
			pa=path[pa].ne;
		}
	}
	int tf=0;
	for (int f1=1;f1<=szc;f1++){
		//printf(" %d,%d,%d",ind[f1],oud[f1],sz[f1]);
		if (ind[f1]==0&&oud[f1]==0&&sz[f1]==1)tf=1;
		if (ind[f1]==0)++ans;
	}
	//printf("%d\n",ans);
	//return 0;
	ans-=1;
	printf("%.6f\n",((n-ans)*1000000ll/n)*0.000001);
}