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
int xds[N*4];
int sz;
int n,k;
int a[N];
int l[N],r[N];
char geti;
int getian;
int seekrmax1(int po, int val){
	po+=sz;
	int ans=po;
	if (xds[po]>val) return po-1;
	while (xds[(ans+1)>>1]<=val)ans=((ans+1)>>1);
	while (ans<sz){
		while (xds[ans+1]>val&&ans<sz)ans=((ans<<1)^1);
		if (xds[ans+1]<=val)ans+=1;
	}
	return ans;
}
int seekrmin1(int po, int val){
	po+=sz;
	int ans=po;
	if (xds[po]<val) return po-1;
	while (xds[(ans+1)>>1]>=val)ans=((ans+1)>>1);
	while (ans<sz){
		while (xds[ans+1]<val&&ans<sz)ans=((ans<<1)^1);
		if (xds[ans+1]>=val)ans+=1;
	}
	return ans;
}
int seeklmax1(int po, int val){
	po+=sz;
	int ans=po;
	if (xds[po]>val) return po+1;
	while (xds[(ans-1)>>1]<=val)ans=((ans-1)>>1);
	while (ans<sz){
		while (xds[ans-1]>val&&ans<sz)ans=(ans<<1);
		if (xds[ans-1]<=val)ans-=1;
	}
	return ans;
}
int seeklmin1(int po, int val){
	po+=sz;
	int ans=po;
	if (xds[po]<val) return po+1;
	while (xds[(ans-1)>>1]>=val)ans=((ans-1)>>1);
	while (ans<sz){
		while (xds[ans-1]<val&&ans<sz)ans=(ans<<1);
		if (xds[ans-1]>=val)ans-=1;
	}
	return ans;
}
int getint(){
	geti=getchar();
	while (!(geti<='9'&&geti>='0'))geti=getchar();
	getian=0;
	while (geti<='9'&&geti>='0'){
		(getian*=10)+=geti-'0';
		geti=getchar();
	}
	return getian;
}
int main(){
	scanf("%d%d",&k,&n);
	for (int f1=1;f1<=n;++f1)a[f1]=getint();
	sz=1;
	//printf("***\n");
	while (sz<n+2)sz*=2;
	for (int f1=1;f1<=n;++f1)xds[sz+f1]=a[f1];
	xds[sz]=2000000000+2;
	xds[sz*2-1]=xds[sz];
	for (int f1=sz-1;f1>=1;--f1){
		xds[f1]=max1(xds[f1*2],xds[f1*2+1]);
	}
	for (int f1=1;f1<=n;f1++){
		l[f1]=seeklmax1(f1, int(min1(a[f1]+0ll+k, 2000000001ll)));
		r[f1]=seekrmax1(f1, int(min1(a[f1]+0ll+k, 2000000001ll)));
	}

	for (int f1=1;f1<=n;++f1)xds[sz+f1]=a[f1];
	xds[sz]=-2000000000-2;
	xds[sz*2-1]=xds[sz];
	for (int f1=sz-1;f1>=1;--f1){
		xds[f1]=min1(xds[f1*2],xds[f1*2+1]);
	}
	for (int f1=1;f1<=n;f1++){
		l[f1]=max1(l[f1],seeklmin1(f1, a[f1]));
		r[f1]=min1(r[f1],seekrmin1(f1, a[f1]));
	}
	int ans=0;
	for (int f1=1;f1<=n;f1++){
		//printf("%d %d\n",l[f1],r[f1]);
		ans=max1(ans,r[f1]-l[f1]+1);
	}
	printf("%d\n",ans);
}





