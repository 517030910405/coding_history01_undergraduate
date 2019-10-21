#include<cmath>
#include<cstdio>
using namespace std;
const int N=3000000+3;
template<class T>
T max1(T a, T b){
	return(a<b)?b:a;
}
template<class T>
T min1(T a, T b){
	return(b<a)?b:a;
}
typedef long long LL;
LL xdsmin1[N*4],xdsmax1[N*4];
int sz;
int n,k;
LL a[N];
int l[N],r[N];
int seekrmax1(int po, LL val){
	po+=sz;
	int ans=po;
	if (xdsmax1[po]>val) return po-1;
	while (xdsmax1[(ans+1)/2]<=val)ans=(ans+1)/2;
	while (ans<sz){
		while (xdsmax1[ans+1]>val&&ans<sz)ans=ans*2+1;
		if (xdsmax1[ans+1]<=val)ans+=1;
	}
	return ans;
}
int seekrmin1(int po, LL val){
	po+=sz;
	int ans=po;
	if (xdsmin1[po]<val) return po-1;
	while (xdsmin1[(ans+1)/2]>=val)ans=(ans+1)/2;
	while (ans<sz){
		while (xdsmin1[ans+1]<val&&ans<sz)ans=ans*2+1;
		if (xdsmin1[ans+1]>=val)ans+=1;
	}
	return ans;
}
int seeklmax1(int po, LL val){
	po+=sz;
	int ans=po;
	if (xdsmax1[po]>val) return po+1;
	while (xdsmax1[(ans-1)/2]<=val)ans=(ans-1)/2;
	while (ans<sz){
		while (xdsmax1[ans-1]>val&&ans<sz)ans=ans*2;
		if (xdsmax1[ans-1]<=val)ans-=1;
	}
	return ans;
}
int seeklmin1(int po, LL val){
	po+=sz;
	int ans=po;
	if (xdsmin1[po]<val) return po+1;
	while (xdsmin1[(ans-1)/2]>=val)ans=(ans-1)/2;
	while (ans<sz){
		while (xdsmin1[ans-1]<val&&ans<sz)ans=ans*2;
		if (xdsmin1[ans-1]>=val)ans-=1;
	}
	return ans;
}

int main(){
	scanf("%d%d",&k,&n);
	for (int f1=1;f1<=n;++f1)scanf("%lld",&a[f1]);
	sz=1;
	while (sz<n+2)sz*=2;
	for (int f1=1;f1<=n;++f1){xdsmax1[sz+f1]=a[f1];xdsmin1[sz+f1]=a[f1];}
	xdsmax1[sz]=(1ll<<40);
	xdsmin1[sz]=-(1ll<<40);
	xdsmax1[sz*2-1]=xdsmax1[sz];
	xdsmin1[sz*2-1]=xdsmin1[sz];
	for (int f1=sz-1;f1>=1;--f1){
		xdsmax1[f1]=max1(xdsmax1[f1*2],xdsmax1[f1*2+1]);
		xdsmin1[f1]=min1(xdsmin1[f1*2],xdsmin1[f1*2+1]);
	}
	int ans=0;
	for (int f1=1;f1<=n;f1++){
		//printf("%d %d\n%d %d\n\n", seeklmax1(f1,a[f1]+k)-sz, 
		//seekrmax1 (f1,a[f1]+k)-sz, seeklmin1(f1,a[f1])-sz, seekrmin1(f1,a[f1])-sz);
		ans=max1(ans, min1(seekrmax1(f1,a[f1]+k),seekrmin1(f1,a[f1]))
		-max1(seeklmax1(f1,a[f1]+k),seeklmin1(f1,a[f1]))+1 );
		
	}
	printf("%d\n",ans);
}





