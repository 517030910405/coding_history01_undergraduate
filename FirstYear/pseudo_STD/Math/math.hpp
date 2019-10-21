#ifndef mymathmath
#define mymathmath

#include<cstdio>
namespace math{
	typedef long double LD;
	template <class T>
	T abs(const T &a){
		return (a<0)?-a:a;
	}
	
	template <class T>
	T exp(T x){
		int time=0;
		while (abs(x)>.1){
			++time;
			x/=2;
		}
		T ans=0,it=1;
		for (int f1=1;f1<=20;f1++){
			ans=ans+it;
			it=it/f1*x;
		}
		for (int f1=1;f1<=time;f1++)ans*=ans;
		return ans;
	}
	
	template <class T>
	T sqrt(T x){
		if (x<1)return 1/sqrt(1/x);
		T ans=0,time=1,it=1,sick=1/T(2);
		
		for (int f1=10;f1>=1;--f1){
			while (x>(1<<(f1<<1))){
				x/=(1<<(f1<<1));
				time*=(1<<f1);
			}
		}
		for (int f1=1;f1<=4;++f1){
			while (x>(1/(1-1/T(1<<f1)))*(1/(1-1/T(1<<f1)))){
				time*=(1/(1-1/T(1<<f1)));
				x/=(1/(1-1/T(1<<f1)))*(1/(1-1/T(1<<f1)));
			}
		}
		x-=1;
		for (int f1=1;f1<=20;f1++){
			ans+=it;
			it=it*sick*x/f1;
			sick-=1;
		}
		return ans*time;
	}
	template<class T>
	T sqrt2(T x){
		T l=0,r=x,m;
		if (r<1)r=1;
		int cnt=0;
		while(l/r!=r/r&&cnt<200){
			m=(l+r)/2;
			if (m*m<=x)l=m;
			else r=m;
			++cnt;
			//std::printf("%d\n",cnt);
		}
		//std::printf("%d\n",cnt);
		return l;
	}
}


#endif