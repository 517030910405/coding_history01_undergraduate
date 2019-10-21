#ifndef sjtu_rand
#define sjtu_rand
#include<iostream>
namespace sjtu{
	struct rand_c{
		int a[6];
		public:
		rand_c(int d0 = 0, int d1 = 0, int d2 = 0, int d3 = 0, int d4 = 0, int d5 = 0){
			a[0] = d0;
			a[1] = d1;
			a[2] = d2;
			a[3] = d3;
			a[4] = d4;
			a[5] = d5;
		}
	};
	bool operator == (const rand_c &o1, const rand_c &o2){
		for (int f1 = 0 ; f1 < 6; ++f1) if (o1.a[f1] != o2.a[f1])return false;
		return true;
	}
	bool operator != (const rand_c &o1, const rand_c &o2){
		for (int f1 = 0 ; f1 < 6; ++f1) if (o1.a[f1] != o2.a[f1])return true;
		return false;
	}
	const rand_c P = rand_c(32933,33073,33403,33289,33317,33331);
	const rand_c A = rand_c(1109,1117,1249,1373,1399,1171);
	const rand_c B = rand_c(10163,10303,10177,10193,10487,10789);
	void f_rand(rand_c &x){
		for (int i = 0; i < 6; ++i){
			x.a[i]=(x.a[i]*A.a[i]+B.a[i])%P.a[i];
		}
		return;
	}
	int rand(){
		static rand_c x(594,438,233,666);
		f_rand(x);
		int ans=0;
		for (int i = 0; i < 6; ++i){
			ans ^= x.a[i];
			//std::cout<<x.a[i]<<' ';
		}
		//std::cout<<'\n';
		return (ans & ((1<<15)-1) );
	}
	int Rand(int l, int r){
		return ((rand()<<15)+rand())%(r-l+1)+l;
		
	}
}

#endif