#include"map.hpp"
#include<iostream>

using namespace std;
int main(){
	Map <int , int > my;
	for (int f1=1;f1<=1000000;f1++){
		//my.insert(Pair<int, int > (rand()*(1<<14)+rand(),f1+1));
		//my.insert(Pair<int, int > (f1,f1+1));
		my.insert(Pair<int, int > (Rand(),f1+1));
		//my.insert(Pair<int, int > (f1*2,f1+1));
	}
	//my.erase(71);
	my.view();
}
