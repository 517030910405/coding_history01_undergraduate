#include<cmath>
#include<cstdio>
using namespace std;
const int N=510;
const int P=1000000+100;
int n,m,p;
int a[N][N];
int s[N][N];
int pp[P];
inline int dis(const int &x){
	return (x>=0)?x:x+p;
}
int main(){
	scanf("%d%d%d",&n,&m,&p);
	for (int f1=1;f1<=n;f1++){
		for (int f2=1;f2<=m;++f2){
			scanf("%d",&a[f1][f2]);
			a[f1][f2]=a[f1][f2]%p;
		}
	}
	for (int f1=0;f1<=n;f1++){
		s[f1][0]=0;
	}
	for (int f2=0;f2<=m;f2++){
		s[0][f2]=0;
	}
	for (int f1=0;f1<=p;f1++){
		pp[f1]=0;
	}
	for (int f1=1;f1<=n;f1++){
		for (int f2=1;f2<=m;++f2){
			s[f1][f2]=(a[f1][f2]+s[f1-1][f2]+s[f1][f2-1]-s[f1-1][f2-1]+p)%p;
			//printf(" %d",s[f1][f2]);
		}
		//printf("\n");
	}
	long long ans=0;
	for (int f1=0;f1<n;++f1){
		for (int f2=f1+1;f2<=n;++f2){
			for (int f3=0;f3<=m;++f3){
				ans+=(pp[dis(s[f2][f3]-s[f1][f3])]++);
			}
			for (int f3=0;f3<=m;++f3){
				--pp[dis(s[f2][f3]-s[f1][f3])];
			}
			//printf("%d\n",ans);	
		}
	}
	printf("%lld\n",ans);
}



