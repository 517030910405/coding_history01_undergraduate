#include"sort.hpp"
#include<cstdio>
#include<cstdlib>
#include<algorithm>
using namespace std;
int a[5000000],b[5000000],c[5000000];
int main(){
	rand();
	rand();
	for (int f1=0;f1<5000000;++f1){
		a[f1]=(rand()<<15)^rand();
		c[f1]=b[f1]=a[f1];
		
	}
	//sort(a,a+5000000);
	sjtu::sort1(a,5000000);
	sjtu::qsort(b,5000000);
	//sjtu::findk(c,5000000,1000000);
	//sjtu::findk(c,5000000,1000000);
	//sort(a,a+5000000);
	int cnt=0;
	for (int f1=0;f1<5000000;++f1){
		if (a[f1]!=b[f1])//printf(" %d",f1);
		++cnt;
	}
	//for (int f1=1;f1<=10;++f1){
		//int k=((rand()<<14)^rand())%5000000;
		//sjtu::findk(c,5000000,k);
		//if (a[k]!=c[k])printf("error %d\n",k);
	//}
	printf("%d\n",cnt);
}