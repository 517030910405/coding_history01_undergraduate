#include<stdio.h>
int h(int a){
    if (a == 0 || a == 1) return 1;
    int sum = 0;
    int i;
    for (i = 0;i < a;i ++)
        sum = sum + h(i) * h(a - 1 - i);
    return sum;
}

int main(){
    int a;
    a=17;
    printf("%d\n",h(a));
    return 0;
}
