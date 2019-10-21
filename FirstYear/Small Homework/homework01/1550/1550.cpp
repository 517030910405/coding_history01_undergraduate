#include<cmath>
#include<cstdio>
using namespace std;
typedef long long LL;
int n,m;
int a[1000+3];
LL ans;
int better(int i, int j){
	return (a[i]>a[j]||(a[i]==a[j]&&i<j))?i:j;
}
int reader(){
	char c=getchar();
	int ans=0;
	while (!(c>='0'&&c<='9'))c=getchar();
	while ((c>='0'&&c<='9')){
		(ans*=10)+=c-'0';
		c=getchar();
	}
	return ans;
}
int main(){
	scanf("%d",&n);
	int max1=1;
	for (int f1=1;f1<=n;f1++){
		a[f1]=reader();
		max1=better(max1,f1);
	}
	int po=1;
	ans=0;
	for (int f1=1;f1<max1;f1++){
		po=better(po,f1);
		ans+=a[po]-a[f1];
	}
	po=n;
	for (int f1=n;f1>max1;f1--){
		po=better(po,f1);
		ans+=a[po]-a[f1];
	}
	printf("%lld\n",ans);
}
