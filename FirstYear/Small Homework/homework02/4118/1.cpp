#include<cmath>
#include<cstdio>
#include<cstring>
using namespace std;
typedef long long LL;
int str1[30],str2[30];
LL cc[30][30];
char inp[30];
int n,m;
char ch;
LL ans=1;
void getans(int *str1,int *str2,int len){
	if (len<=1) return;
	int l,r,cnt=0;
	l=1;
	r=l-1;
	while (l<len){
		++cnt;
		while (str2[r]!=str1[l])++r;
		++r;
		getans(str1+l,str2+l-1,r-l+1);
		l=r+1;
	}
	ans*=cc[m-cnt][cnt];
}


int main(){
	scanf("%d",&m);
	scanf("%s",inp);
	n=strlen(inp);
	for (int f1=0;f1<n;f1++){
		str1[f1]=inp[f1]-'a';
	}
	for (int f1=0;f1<n;f1++){
		scanf(" %c",&ch);
		str2[f1]=ch-'a';
	}
	for (int f1=0;f1<30;++f1){
		cc[f1][0]=1;
		cc[0][f1]=1;
	}
	for (int f1=1;f1<30;f1++){
		for (int f2=1;f2<30;f2++){
			cc[f1][f2]=cc[f1-1][f2]+cc[f1][f2-1];
		}
	}
	getans(str1,str2,n);
	printf("%lld\n",ans);
}
