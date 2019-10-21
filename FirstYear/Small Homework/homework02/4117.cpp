#include<cmath>
#include<cstdio>
using namespace std;
typedef long long LL;
int n,m;
LL xds[(1<<18)+5];
LL lazy[(1<<18)+5];
int szx;
inline void change(const int &ll,const int &rr,const LL &cc){
	register int szl=0,szr=0,sz=1,l=ll,r=rr;
	register LL c=cc;
	l+=szx-1;r+=szx+1;
	while ((l>>1)!=(r>>1)){
		xds[l]+=szl*c;xds[r]+=szr*c;
		if (!bool(l&1)){lazy[l+1]+=c;szl+=sz;xds[l+1]+=sz*c;}
		if (bool(r&1)){lazy[r-1]+=c;szr+=sz;xds[r-1]+=sz*c;}
		l>>=1;r>>=1;sz<<=1;
	}
	while (l>=1){
		xds[l]+=szl*c;xds[r]+=szr*c;
		l>>=1;r>>=1;
	}
}
inline LL tell(const int &ll,const int &rr){
	register int szl=0,szr=0,sz=1,l=ll,r=rr;
	register LL ansl=0,ansr=0;
	l+=szx-1;r+=szx+1;
	while ((l>>1)!=(r>>1)){
		ansl+=szl*lazy[l];ansr+=szr*lazy[r];
		if (!bool(l&1)){szl+=sz;ansl+=xds[l+1];}
		if (bool(r&1)){szr+=sz;ansr+=xds[r-1];}
		l>>=1;r>>=1;sz<<=1;
	}
	while (l>=1){
		ansl+=szl*lazy[l];
		ansr+=szr*lazy[r];
		l>>=1;r>>=1;
	}
	return ansl+ansr;
}


int main(){
	scanf("%d%d",&n,&m);
	szx=1;
	while (szx<n+2)szx+=szx;
	for (register int f1=1;f1<=n;f1++){
		scanf("%lld",&xds[f1+szx]);
	}
	for (register int f1=szx-1;f1>=1;--f1){
		xds[f1]=xds[f1*2]+xds[f1*2+1];
	}
	for (int f1=1;f1<=m;f1++){
		char cc;
		int i,j;
		LL c;
		scanf(" %c%d%d",&cc,&i,&j);
		if (cc=='Q'){
			printf("%lld\n",tell(i,j));
		} else{
			scanf("%lld",&c);
			change(i,j,c);
		}
		
	}
}