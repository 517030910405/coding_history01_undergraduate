#include<cmath>
#include<cstdio>
using namespace std;
typedef long long LL;
LL pw(LL a,LL b,LL p){
	LL ans=1;
	while (b>0){
		if (bool(b&1)){
			(ans*=a)%=p;
		}
		(a*=a)%=p;
		b>>=1;
	}
	return ans;
}



bool primetest(LL p,LL x){
	int cnt=0;
	LL a=x;
	LL b=p-1;
	//x^(p-1)==1 mod p
	while (!bool(b&1)){
		b>>=1;
		++cnt;
	}
	LL ans=pw(a,b,p);
	if (ans==1) return true;
	for (int f1=0;f1<cnt;f1++){
		if (ans==p-1)return true;
		(ans*=ans)%=p;
	}
	return false;
}
bool primetest(LL p){
	return (primetest(p,2)&&primetest(p,3)&&primetest(p,5)&&primetest(p,7)&&primetest(p,11)
	&&primetest(p,13)&&primetest(p,17)&&primetest(p,19))
	||p==2||p==3||p==5||p==7||p==11||p==13||p==17||p==19;
}


int main(){
	for (LL f1=1000000000ll-1000;f1<=1000000000ll+1000;f1+=1)if (primetest(f1)) printf("%lld\t",f1);
	
	
}

