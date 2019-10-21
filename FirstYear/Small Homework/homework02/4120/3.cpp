#include<cmath>
#include<cstdio>
//Notice 假如有单独一个点，可以不管
using namespace std;
const int N=100000+10;
const int M=300000+10;
struct Path{
	int ne,tow;
};
Path path[M*2];
int mpin[N],mpou[N],ind[N],oud[N];
int col[N];
int szp=0;
int n,m;
int ans=0;
int xds[(1<<18)+2];
int szx;
int store[N],szst;
int cntp;
int better(int i,int j){
	return (oud[i]>=oud[j])?i:j;
}
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
int main(){
	scanf("%d%d",&n,&m);
	for (int f1=1;f1<=n;f1++){
		mpin[f1]=0;
		mpou[f1]=0;
		ind[f1]=0;
		oud[f1]=0;
		col[f1]=1;
	}
	for (int f1=1;f1<=m;f1++){
		int i,j;
		scanf("%d%d",&i,&j);
		addpathfr(j,i);
		addpathto(i,j);
		++ind[j];
		++oud[i];
	}
	szx=1;
	while (szx<n+2)szx+=szx;
	oud[0]=-1;
	for (int f1=1;f1<=n;f1++)xds[f1+szx]=f1;
	for (int f1=szx-1;f1>=1;--f1)xds[f1]=better(xds[f1*2],xds[f1*2+1]);
	
	double ans=0;
	double safe=1;
	
	cntp=n;
	while (xds[1]!=0){
		int pos=xds[1];
		if (cntp!=1)safe*=(1-1.0/cntp);
		//printf(" [%d %d]",pos,oud[xds[1]]);
		szst=1;
		store[szst]=pos;
		int pa=mpou[pos];
		while (pa!=0){
			store[++szst]=path[pa].tow;
			pa=path[pa].ne;
		}
		ans+=safe*(double(szst)/cntp);
		cntp-=szst;
		for (pos=1;pos<=szst;pos++){
			pa=mpin[store[pos]];
			while (pa!=0){
				int tow=path[pa].tow;
				oud[tow]--;
				tow+=szx;
				tow>>=1;
				while (tow>=1){
					xds[tow]=better(xds[tow*2],xds[tow*2+1]);
					tow>>=1;
				}
				pa=path[pa].ne;
			}
		}
		for (pos=1;pos<=szst;++pos){
			int tow=store[pos];
			xds[tow+szx]=0;
			tow+=szx;
			tow>>=1;
			while (tow>=1){
				xds[tow]=better(xds[tow*2],xds[tow*2+1]);
				tow>>=1;
			}
		
		}
	}
	
	printf("%.6f\n",int(ans*1000000)*0.000001);
	
}