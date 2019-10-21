#include<cmath>
#include<cstring>
#include<cstdio>
using namespace std;
typedef long long LL;
int P[2]={1000000007,1000000009};
int bas=311;
struct HashType{
	LL a[2];
	bool operator == (const HashType &oth)const {
		return a[0]==oth.a[0]&&a[1]==oth.a[1];
	}
};
int min1(int a,int b){
	return (a<b)?a:b;
}
int max1(int a,int b){
	return (a<b)?b:a;
}
const int Len=1000000+2;
const int N=100000+2;
char str[Len+N*2];
int st[N],ed[N],szl=0,szs=1;
int n,q;
HashType Hash[Len+N*2];
HashType BasVal[Len+N*2];


HashType getHash(int l,int r){
	HashType ans;
	ans.a[0]=(Hash[r].a[0]-Hash[l-1].a[0]*BasVal[r-l+1].a[0]% P[0] + P[0])%P[0];
	ans.a[1]=(Hash[r].a[1]-Hash[l-1].a[1]*BasVal[r-l+1].a[1]% P[1] + P[1])%P[1];
	return ans;
}


void scanstring(){
	scanf("%s",str+szs);
	szl++;
	st[szl]=szs;
	ed[szl]=szs+strlen(str+szs)-1;
	szs=ed[szl]+2;
	for (int i=st[szl];i<=ed[szl]+1;i++){
		Hash[i].a[0]=(Hash[i-1].a[0]*bas +str[i])%P[0];
		Hash[i].a[1]=(Hash[i-1].a[1]*bas +str[i])%P[1];
		BasVal[i].a[0]=BasVal[i-1].a[0]*bas %P[0];
		BasVal[i].a[1]=BasVal[i-1].a[1]*bas %P[1];
	}
}
int CommonLen(int s1,int e1,int s2,int e2){
	int l=0;//possible
	int r=min1(e2-s2,e1-s1)+2;//impossible
	int m=(l+r)/2;
	while (l<r-1){
		if (getHash(e1-m+1,e1)==getHash(e2-m+1,e2))l=m;
		else r=m;
		m=(l+r)/2;
	}
	return l;
}




int main(){
	Hash[0].a[0]=0;
	Hash[0].a[1]=0;
	BasVal[0].a[0]=1;
	BasVal[0].a[1]=1;
	scanf("%d%d",&n,&q);
	for (int f1=1;f1<=n;f1++){
		scanstring();
	}
	for (int f1=1;f1<=q;f1++){
		int opt;
		scanf("%d",&opt);
		if (opt==1){
			scanstring();
		}else if (opt==2){
			int m;
			int l,r,it1,len;
			scanf("%d%d",&m,&it1);
			l=st[it1];
			r=ed[it1];
			for (int f2=2;f2<=m;f2++){
				scanf("%d",&it1);
				len=CommonLen(l,r,st[it1],ed[it1]);
				l=r-len+1;
			}
			printf("%d\n",r-l+1);
		}
	}
	return 0;
	
	/*scanstring();
	scanstring();
	scanstring();
	for (int f1=1;f1<szs;f1++){
		printf(" %d",int(str[f1]));
	}
	printf("%d\n",CommonLen(st[1],ed[1],st[2],ed[2]));
	*/
}
