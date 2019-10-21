#include<cmath>
#include<cstdio>
#include<iostream>
#include"matrix.hpp"
//#include <crtdbg.h>
using namespace sjtu;

int main(){
	cnt=0;
		const int N = 200, M = 300;
	Matrix<int> a(N, M), b(M, N), c(N, M);
	for (size_t i = 0; i < N; i++)
		for (size_t j = 0; j < M; j++)
			a(i, j) = rand(), b(j, i) = rand(), c(i, j) = rand();
	int cnt = 0;
	try
	{
		//a + b;
	} catch (...) {std::cout<<"\na+b\n"; cnt++; }

	try
	{
		//a += b;
	} catch (...) {std::cout<<"\na+=b\n"; cnt++; }

	try
	{
		//a -= b;
	} catch (...) {std::cout<<"\na-=b\n"; cnt++; }

	try
	{
		//a - b;
	} catch (...) {std::cout<<"\na-b\n"; cnt++; }

	try
	{
		//a * c;
	} catch (...) {std::cout<<"\na*b\n"; cnt++; }

	try
	{
		//a(-1, -1);
	} catch (...) {std::cout<<"\na-1-1\n"; cnt++; }

	try
	{
		//a(N, M);
	} catch (...) {std::cout<<"\nanm\n"; cnt++; }

	try
	{
		//a.row(N);
	} catch (...) {std::cout<<"\narown\n"; cnt++; }

	try
	{
		//a.row(-1);
	} catch (...) {std::cout<<"\nrow-1\n"; cnt++; }

	try
	{
		//a.column(M);
	} catch (...) {std::cout<<"\ncm\n"; cnt++; }

	try
	{
		//a.column(-1);
	} catch (...) {std::cout<<"\nc-1\n"; cnt++; }

	try
	{
		Matrix<int> t = {{ 0, 1 },
						 { 0 }};
	} catch (...) {std::cout<<"\n{}\n"; cnt++; }

	

}
