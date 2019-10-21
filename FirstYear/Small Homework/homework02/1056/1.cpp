#include<cmath>
#include<cstdio>
using namespace std;
const int N=500100;
int xds[(1<<20)+10];
int par[N];
int szp[N];
int szx=1,n,m;
int fpar(int x){
	if (par[x]==0)return x;
	par[x]=fpar(par[x]);
	return par[x];
}

void xds_change(int x ,int cnt){
	x+=szx;
	while (x>=1){
		xds[x]+=cnt;
		x>>=1;
	}
}
int xds_find(int k){
	int pos=1;
	while (pos<szx){
		if (k<=xds[pos<<1]) pos<<=1;
		else {
			k-=xds[pos<<1];
			pos=(pos<<1)+1;
		}
	}
	return pos-szx;
}
 
int main(){
	scanf("%d%d",&n,&m);
	szx=1;
	while (szx<n+2)szx*=2;
	for (int f1=1;f1<=szx*2;++f1) xds[f1]=0;
	for (int f1=0;f1<=n;f1++){
		par[f1]=0;
		szp[f1]=1;
	}
	xds_change(1,n);
	char ch;
	for (int f1=1;f1<=m;f1++){
		scanf(" %c",&ch);
		if (ch=='C'){
			int x,y;
			scanf("%d%d",&x,&y);
			x=fpar(x);
			y=fpar(y);
			if (x!=y&&szp[x]>0&&szp[y]>0){
				xds_change(szp[x],-1);
				xds_change(szp[y],-1);
				szp[x]+=szp[y];
				par[y]=x;
				xds_change(szp[x],1);
			}
		} else if (ch=='Q'){
			int k;
			scanf("%d",&k);
			if (k>xds[1]){
				printf("0\n");
				
			} else{
				int ans=xds_find(xds[1]-k+1);
				printf("%d\n",ans);
			}
		} else if (ch=='D'){
			int x;
			scanf("%d",&x);
			x=fpar(x);
			if (szp[x]>0){
				xds_change(szp[x],-1);
				szp[x]=0;
			}
		}
	}
}