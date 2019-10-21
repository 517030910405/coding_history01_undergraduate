#include<cmath>
#include<cstdio>
#include<iostream>
#include"matrix.hpp"
using namespace sjtu;

int main(){
	Matrix<double> m1={{1.2,1.5},{0.3,1.7}};
	if (m1*m1==m1){
		printf("YES");
	} else {
		printf("NO");
	}
	Matrix<int> m2(m1);
	m2=m2;
	for (int f1=0;f1<m2.rowLength();f1++)
		for (int f2=0;f2<m2.rowLength();f2++)std::cout<<' '<<m2(f1,f2);
}
