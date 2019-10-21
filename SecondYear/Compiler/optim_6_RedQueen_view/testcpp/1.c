#include<stdio.h>
typedef long long LL;
LL A(LL m,LL n){
	if (m==0) return n+1;
	if (m > 0&&n==0) return A(m-1,1);
	return A(m-1,A(m,n-1));
}

//int s = 0;
int f(int x){
    if (x<=1) return 1;
	 int res = f(x-1)+f(x-2);
	 //s++;
	 return res;
}
int main(){
	//int i;
	//scanf("%d",&i);
    
    //printf("%d\n",f(29));
    printf("%d\n",f(39));
	//printf("%d\n",s);
    //printf("%lld\n",A(4,1));
}