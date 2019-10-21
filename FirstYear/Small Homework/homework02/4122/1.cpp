#include<cmath>
#include<cstdio>
#include<cstring>
#include<iostream>
using namespace std;
typedef long long LL;
const int N1=1003,N2=10;
const int M=100000+100;
const int N=1010;
int sz[N1];
pair<pair<int,int>, int > Hasse[N1][N2];
int szh=0;
pair<int,int> hash1(const char *st){
	const char *ch;
	ch=st;
	LL ans1=0,ans2=0;
	while ((*ch)!=0){
		((ans1*=313)+=(*ch+128))%=1000000007;
		((ans2*=1031)+=(*ch+129))%=1000000009;
		++ch;
	}
	return pair<int,int>(int(ans1),int(ans2));
}
int find(const pair<int,int> &p){
	int x=p.first%N1;
	for (int f1=0;f1<sz[x];++f1){
		if (Hasse[x][f1].first==p) return Hasse[x][f1].second;
	}
	return 0;
}
void insert(const pair<int,int> &p, const int &i){
	if (find(p)==0){
		int x=p.first%N1;
		Hasse[x][sz[x]++]=pair<pair<int,int> ,int>(p,i);
	}
}

int a[M];
int cnt[N];
int n,m;
char st[50];
int main(){
	scanf("%d",&n);
	for (int f1=0;f1<N1;f1++){
		sz[f1]=0;
	}
	for (int f1=1;f1<=n;f1++){
		scanf("%s",st);
		insert(hash1(st),f1);
		cnt[f1]=0;
	}
	scanf("%d",&m);
	for (int f1=1;f1<=m;f1++){
		scanf("%s",st);
		a[f1]=find(hash1(st));
	}
	a[m+1]=0;
	//for (int f1=1;f1<=m;f1++){
		//printf(" %d",a[f1]);
	//}
	int l,r;
	int an1=0,an2=m;
	l=1;r=m;
	for (int f1=1;f1<=m;f1++){
		++cnt[a[f1]];
	}
	for (int f1=1;f1<=n;f1++){
		if (cnt[f1]>0)++an1;
	}
	while (cnt[a[r]]>1){
		--cnt[a[r--]];
	}
	while (r<=m){
		while (cnt[a[l]]>1)--cnt[a[l++]];
		if (r-l+1<an2)an2=r-l+1;
		//printf("%d %d\n",l,r);
		++cnt[a[++r]];
	}
	if (an1<=1)an2=an1;
	printf("%d\n%d\n",an1,an2);
}