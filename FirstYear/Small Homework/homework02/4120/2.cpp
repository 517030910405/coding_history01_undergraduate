#include<cmath>
#include<cstdio>
using namespace std;
struct Path{
	int ne,tow;
};
const int N=100000+10,M=300000+10;
Path path[M];
int mp[N];
int szp=0,n,m;
void addpath(int i,int j){
	path[++szp].tow=j;
	path[szp].ne=mp[i];
	mp[i]=szp;
}



int main(){
	scanf("%d%d",&n,&m);
	for (int f1=1;f1<=m;f1++){
		int i,j;
		scanf("%d%d",&i,&j);
		addpath(i,j);
	}
	
}