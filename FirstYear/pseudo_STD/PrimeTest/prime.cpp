#include<cmath>
#include<cstdio>
using namespace std;
typedef long long LL;
LL l,r;
LL n,sqn;
bool *a,*b;
int main(){
	scanf("%lld%lld",&l,&r);
	if (l<2)l=2;
	if (l>r)r=l;
	n=sqrt(double(r))+2;
	sqn=sqrt(double(n))+2;
	a=new bool [n+1];
	for (int f1=2;f1<=n;f1++)a[f1]=true;
	for (int f1=2;f1<=n;f1++){
		if (a[f1]){
			if (f1<=sqn){
				for (int f2=f1*f1;f2<=n;f2+=f1){
					a[f2]=false;
				}
			}
		}
	}
	b=new bool [r-l+1];
	for (LL f1=l;f1<=r;f1++)b[f1-l]=true;
	for (int f1=2;f1<=n;f1++){
		if (a[f1]){
			LL st=l/f1*f1;
			if (st==f1||st<l)st+=f1;
			if (st==f1||st<l)st+=f1;
			for (LL f2=st;f2<=r;f2+=f1){
				b[f2-l]=false;
			}
		}
	}
	for (LL f1=l;f1<=r;f1++)if (b[f1-l])printf("%lld\t",f1);
	delete [] a;
	delete [] b;
}