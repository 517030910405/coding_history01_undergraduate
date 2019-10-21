#include<cmath>
#include<cstdio>
//Notice 假如有单独一个点，可以不管
using namespace std;
const int N=100000+10;
const int M=300000+10;
struct Path{
	int ne,tow;
};
Path path[M*2];
int mpin[N],mpou[N],ind[N],oud[N];
int col[N];
int szp=0;
int n,m;
int ans=0;
inline void addpathfr(const int &i, const int &j){
	path[++szp].tow=j;
	path[szp].ne=mpin[i];
	mpin[i]=szp;
}
inline void addpathto(const int &i, const int &j){
	path[++szp].tow=j;
	path[szp].ne=mpou[i];
	mpou[i]=szp;
}
int main(){
	scanf("%d%d",&n,&m);
	for (int f1=1;f1<=n;f1++){
		mpin[f1]=0;
		mpou[f1]=0;
		ind[f1]=0;
		oud[f1]=0;
		col[f1]=1;
	}
	for (int f1=1;f1<=m;f1++){
		int i,j;
		scanf("%d%d",&i,&j);
		addpathfr(j,i);
		addpathto(i,j);
		++ind[j];
		++oud[i];
	}
	for (int f1=1;f1<=n;f1++){
		if (ind[f1]==0){
			
			
		}
		
	}
}