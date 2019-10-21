#include<cmath>
#include<cstdio>
using namespace std;
const int N=100000+10;
int n,m;
char st[N];
int cnt[26];
inline void operate1(int l,int r){
	for (register int f1=0;f1<26;++f1)cnt[f1]=0;
	for (register int f1=l;f1<=r;++f1)++cnt[st[f1]-'a'];
	register int pos=0;
	for (register int f1=l;f1<=r;++f1){
		while (cnt[pos]==0)++pos;
		--cnt[pos];
		st[f1]='a'+pos;
	}
}
inline void operate0(int l,int r){
	for (register int f1=0;f1<26;++f1)cnt[f1]=0;
	for (register int f1=l;f1<=r;++f1)++cnt[st[f1]-'a'];
	register int pos=0;
	for (register int f1=r;f1>=l;--f1){
		while (cnt[pos]==0)++pos;
		--cnt[pos];
		st[f1]='a'+pos;
	}
}
int main(){
	scanf("%d%d",&n,&m);
	scanf("%s",st+1);
	int o1,o2,o3;
	for (int f1=1;f1<=m;f1++){
		scanf("%d%d%d",&o1,&o2,&o3);
		if (o3==0)operate0(o1,o2);
		else operate1(o1,o2);
		
	}
	printf("%s\n",st+1);
}