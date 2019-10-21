#include<stdio.h>
int f(int x){
    if (x<=1) return 1;
    if (true){
        return f(x-1)+f(x-2);
    }
	return 0;
}
int main(){
	int i;
	scanf("%d",&i);
    printf("%d\n",f(0));
    printf("%d\n",f(1));
    printf("%d\n",f(2));
    printf("%d\n",f(3));
    printf("%d\n",f(4));
    printf("%d\n",f(5));
    printf("%d\n",f(6));
    printf("%d\n",f(7));
    printf("%d\n",f(8));
    printf("%d\n",f(9));
    printf("%d\n",f(10));
    printf("%d\n",f(i));
}