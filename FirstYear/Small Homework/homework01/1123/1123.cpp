#include<cmath>
#include<cstdio>
using namespace std;
typedef long long LL;
struct node{
	int x, y;
};
int n,m;
const int P=100007;
node a[50000+10];
node b[50000+10];
int cnt[1000+2];
LL xds[200000*4];
LL s[50000+10],t[50000+10];
int sz;
LL ans;
void sort(int x){
	for (int f1=0;f1<=1000;f1++)cnt[f1]=0;
	for (int f1=1;f1<=n;f1++){
		++cnt[(a[f1].x/x%1000)];
	}
	for (int f1=1;f1<1000;f1++){
		cnt[f1]+=cnt[f1-1];
	}
	for (int f1=n;f1>=1;f1--){
		b[cnt[a[f1].x/x%1000]]=a[f1];
		--cnt[a[f1].x/x%1000];
	}
	for (int f1=1;f1<=n;f1++)a[f1]=b[f1];
}
LL tell(int l,int r){
	LL ans=0;
	//if (l>=r) return 0;
	l+=sz-1;
	r+=sz+1;
	while ((l>>1)!=(r>>1)){
		if (!bool(l&1))(ans+=xds[l+1])%=P;
		if (bool(r&1))(ans+=xds[r-1])%=P;
		l>>=1;
		r>>=1;
	}
	return ans%P;
}
void change(int po,LL val){
	po+=sz;
	xds[po]=val%P;
	po>>=1;
	while (po>=1){
		xds[po]=(xds[po*2]+xds[po*2+1])%P;
		po>>=1;
	}
}
int main(){
	scanf("%d%d",&n,&m);
	for (int f1=1;f1<=n;f1++)scanf("%d%d",&a[f1].x,&a[f1].y);
	sort(1);
	sort(1000);
	sz=1;
	ans=0;
	while (sz<100000+2)sz*=2;
	for (int f1=1;f1<=n;f1++)s[f1]=1;
	for (int f1=1;f1<=m;f1++){
		for (int f2=1;f2<=100000;f2++){
			xds[sz+f2]=0;
		}
		for (int f2=sz-1;f2>=1;f2--)
		xds[f2]=(xds[f2*2]+xds[f2*2+1])%P;
		for (int f2=1;f2<=n;f2++){
			if (bool(f1&1)){
				t[f2]=tell(1,a[f2].y-1);
			} else{
				t[f2]=tell(a[f2].y+1,100000);
			}
			change(a[f2].y,(s[f2]+t[f2])%P);
		}
		for (int f2=1;f2<=n;f2++){
			s[f2]=t[f2];
		}
	}
	
	for (int f1=1;f1<=n;f1++){
		(ans+=s[f1])%=P;
	}

	for (int f1=1;f1<=n;f1++)s[f1]=1;
	for (int f1=1;f1<=m;f1++){
		for (int f2=1;f2<=100000;f2++){
			xds[sz+f2]=0;
		}
		for (int f2=sz-1;f2>=1;f2--)
		xds[f2]=(xds[f2*2]+xds[f2*2+1])%P;
		for (int f2=1;f2<=n;f2++){
			if (!bool(f1&1)){
				t[f2]=tell(1,a[f2].y-1);
			} else{
				t[f2]=tell(a[f2].y+1,100000);
			}
			change(a[f2].y,(s[f2]+t[f2])%P);
		}
		for (int f2=1;f2<=n;f2++){
			s[f2]=t[f2];
		}
	}
	
	for (int f1=1;f1<=n;f1++){
		(ans+=s[f1])%=P;
	}
	printf("%lld\n",ans);;
}
