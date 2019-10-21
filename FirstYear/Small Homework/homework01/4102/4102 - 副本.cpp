#include<cmath>
#include<cstdio>
using namespace std;
const int N=3000000+3;
template<class T>
T max(T a, T b){
	return(a<b)?b:a;
}
template<class T>
T min(T a, T b){
	return(b<a)?b:a;
}
typedef long long LL;
LL xdsmin[N*4],xdsmax[N*4];
int sz;
int n,k;
LL a[N];
int l[N],r[N];
int 


int main(){
	scanf("%d%d",&n);
	for (int f1=1;f1<=n;++f1)scanf("%lld",&a[f1]);
	sz=1;
	while (sz<n+2)sz*=2;
	for (int f1=1;f1<=n;++f1){xdsmax[sz+f1]=a[f1];xdsmin[sz+f1]=a[f1];}
	xdsmax[sz]=(1ll<<40);
	xdsmin[sz]=-(1ll<<40);
	xdsmax[sz*2-1]=xdsmax[sz];
	xdsmin[sz*2-1]=xdsmin[sz];
	for (int f1=sz-1;f1>=1;--f1){
		xdsmax[f1]=max(xdsmax[f1*2],xdsmax[f1*2+1]);
		xdsmin[f1]=min(xdsmin[f1*2],xdsmin[f1*2+1]);
	}
	
}





