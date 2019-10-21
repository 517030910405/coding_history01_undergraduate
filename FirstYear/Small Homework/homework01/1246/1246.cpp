#include<cmath>
#include<cstdio>
using namespace std;
typedef long long LL;
const int N=1000+2;
int h[N][N];
LL s[N][N];
LL s1[N][N];
LL s2[N][N];
LL xdss2[N][N*4];
int szs2,szs1;
LL xdss1[N*4];
int n,m,a,b,c,d;
LL ans=-1;
int an1,an2,an3,an4;
void swap1(int &a,int &b){
	int c=a;
	a=b;
	b=c;
}
LL min1(LL a, LL b){
	return (a<b)?a:b;
}
LL max1(LL a, LL b){
	return (a>b)?a:b;
}
LL tells2(int linea,int l,int r){
	l+=szs2-1;
	r+=szs2+1;
	LL ans=(1ll<<60);
	while ((l>>1)!=(r>>1)){
		if (!bool(l&1)) ans=min1(ans,xdss2[linea][l+1]);
		if (bool(r&1))ans=min1(ans,xdss2[linea][r-1]);
		l>>=1;
		r>>=1;
	}
	return ans;
}
LL tells1(int l,int r){
	l+=szs1-1;
	r+=szs1+1;
	LL ans=(1ll<<60);
	while ((l>>1)!=(r>>1)){
		if (!bool(l&1))ans=min1(xdss1[l+1],ans);
		if (bool(r&1))ans=min1(xdss1[r-1],ans);
		l>>=1;
		r>>=1;
	}
	return ans;
}
int main(){
	scanf("%d%d%d%d%d%d",&n,&m,&a,&b,&c,&d);
	swap1(n,m);
	swap1(a,b);
	swap1(c,d);
	for (int f1=1;f1<=n;f1++){
		for (int f2=1;f2<=m;f2++){
			scanf("%d",&h[f1][f2]);
		}
	}
	for (int f1=0;f1<=n;f1++)s[f1][0]=0;
	for (int f2=0;f2<=m;f2++)s[0][f2]=0;
	for (int f1=1;f1<=n;f1++){
		for (int f2=1;f2<=m;f2++){
			s[f1][f2]=s[f1-1][f2]+s[f1][f2-1]-s[f1-1][f2-1]+h[f1][f2];
		}
	}
	LL ans=0;
	int b1,b2,b3,b4;
	for (int f1=1;f1<=n-a+1;f1++){
		for (int f2=1;f2<=m-b+1;f2++){
			s1[f1][f2]=s[f1+a-1][f2+b-1]-s[f1+a-1][f2-1]-s[f1-1][f2+b-1]+s[f1-1][f2-1];
		}
	}
	for (int f1=1;f1<=n-c+1;f1++){
		for (int f2=1;f2<=m-d+1;f2++){
			s2[f1][f2]=s[f1+c-1][f2+d-1]-s[f1+c-1][f2-1]-s[f1-1][f2+d-1]+s[f1-1][f2-1];
		}
	}
	//printf("%d %d\n%d %d\n%lld\n",b1,b2,b3,b4,ans);
	szs2=1;
	while (szs2<m-d+1+2)szs2*=2;
	for (int f1=1;f1<=n-c+1;f1++){
		for (int f2=1;f2<=m-d+1;f2++){
			xdss2[f1][szs2+f2]=s2[f1][f2];
		}
		for (int f2=szs2-1;f2>=1;f2--){
			xdss2[f1][f2]=min1(xdss2[f1][f2*2],xdss2[f1][f2*2+1]);
		}
	}
	szs1=1;
	while (szs1<n-c+1+2)szs1*=2;
	for (int f2=1;f2<=m-b+1;f2++){
		for (int f3=1;f3<=n-c+1;f3++){
			xdss1[szs1+f3]=tells2(f3,f2+1,f2+b-d-1);
		}
		for (int i=szs1-1;i>=1;i--){
			xdss1[i]=min1(xdss1[i*2],xdss1[i*2+1]);
		}
		for (int f1=1;f1<=n-a+1;f1++){
			if (s1[f1][f2]-tells1(f1+1,f1+a-c-1)>ans){
				ans=s1[f1][f2]-tells1(f1+1,f1+a-c-1);
				an1=f1;
				an2=f2;
			}
		}
	}
	for (int f3=an1+1;f3<=an1+a-c-1;f3++){
		for (int f4=an2+1;f4<=an2+b-d-1;f4++){
			if (ans==s1[an1][an2]-s2[f3][f4]){
				an3=f3;an4=f4;
			}
		}
	}
	//printf("%lld\n",ans);
	printf("%d %d\n%d %d\n",an2,an1,an4,an3);
}
