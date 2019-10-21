#include<stdlib.h>
#include<string.h>
#include<stdio.h>
#define buffer __buffer_cache__
#define func_maxnum 512
typedef long long LL;
LL n =0;
LL m =1;
LL buffer[10000];
LL **func_valid_arr = 0;
LL **func_x_arr = 0;
LL **func_y_arr = 0;
LL func_num = 1;
LL func_x = 1;
LL func_valid = 1;
LL func_y = 1;
void set_func(){
	int tmp = func_x & (func_maxnum-1);
	func_valid_arr[func_num][tmp] = 1;
	func_x_arr[func_num][tmp] = func_x;
	func_y_arr[func_num][tmp] = func_y;
}
void seek_func(){
	int tmp = func_x & (func_maxnum-1);
	func_valid = 0;
	if (func_valid_arr[func_num][tmp] == 1 && func_x_arr[func_num][tmp] == func_x){
		func_y = func_y_arr[func_num][tmp];
		func_valid = 1;
	}
}
void init_func(){
	func_valid_arr = calloc((size_t)20,(size_t)8);
	for (int f1=0;f1<20;++f1){
		func_valid_arr[f1] = calloc((size_t)func_maxnum,(size_t)8);
	}
	
	func_x_arr = calloc((size_t)20,(size_t)8);
	for (int f1=0;f1<20;++f1){
		func_x_arr[f1] = calloc((size_t)func_maxnum,(size_t)8);
	}
	
	func_y_arr = calloc((size_t)20,(size_t)8);
	for (int f1=0;f1<20;++f1){
		func_y_arr[f1] = calloc((size_t)func_maxnum,(size_t)8);
	}
	
}


LL* toString(LL a){
	LL* ans = malloc(32);
	sprintf((char*)(ans+1),"%lld",a);
	*ans = (LL)(strlen((char*)(ans+1)));
	return ans;
}
void print(LL *a){
	printf("%s",(char*)(a+1));
	return;
}
void println(LL *a){
	printf("%s\n",(char*)(a+1));
	return;
}
LL fifib(LL a){
	if (a==0) return 1;
	if (a==1) return 1;
	return fifib(a-1)+fifib(a-2);
}
LL* mallocx8(LL a){
	LL* ans = malloc((a+2)*8);
	* ans = a-1;
	return ans;
}
LL parseInt(LL *a){
	LL ans;
	sscanf((char*)(a+1),"%lld",&ans);
	return ans;
}
LL* str_add(LL *a,LL *b){
	LL * ans;
	LL len = (*a)+(*b);
	ans = malloc(len+16);
	*ans = len;
	memcpy((char*)(ans+1),(char*)(a+1),(size_t)(*a));
	memcpy((char*)(ans+1)+(*a),(char*)(b+1),(size_t)(*b));
	*(((char*)(ans+1))+(*a)+(*b)) = 0;
	return ans;
}
LL str_le(LL *a,LL *b){
	//LL res = strcmp((char*)(a+1),(char*)(b+1));
	return (strcmp((char*)(a+1),(char*)(b+1))<=0);
}
LL str_ge(LL *a,LL *b){
	//LL res = strcmp((char*)(a+1),(char*)(b+1));
	return (strcmp((char*)(a+1),(char*)(b+1))>=0);
}
LL str_l(LL *a,LL *b){
	//LL res = strcmp((char*)(a+1),(char*)(b+1));
	return (strcmp((char*)(a+1),(char*)(b+1))<0);
}
LL str_g(LL *a,LL *b){
	//LL res = strcmp((char*)(a+1),(char*)(b+1));
	return (strcmp((char*)(a+1),(char*)(b+1))>0);
}
LL str_e(LL *a,LL *b){
	//LL res = strcmp((char*)(a+1),(char*)(b+1));
	return (strcmp((char*)(a+1),(char*)(b+1))==0);
}
LL str_ne(LL *a,LL *b){
	//LL res = strcmp((char*)(a+1),(char*)(b+1));
	return (strcmp((char*)(a+1),(char*)(b+1))!=0);
}
LL* getString(){
	if (n!=0) {
		if(getchar()=='\r') getchar();
	}
	++n;
	scanf("%[^\n\r]",(char*)buffer);
	LL len = strlen((char*)buffer);
	LL * ans;
	ans = malloc(len+16);
	* ans = len;
	memcpy((char*)(ans+1),(char*)(buffer),(size_t)len);
	*(((char*)(ans+1))+len) = 0;
	return ans;
}
LL str_len(LL * a){
	return *a;
}
void printlnInt(LL a){
	println(toString(a));
	return;
}
LL getInt(){
	if (n!=0) {
		if(getchar()=='\r') getchar();
	}
	++n;
	LL ans;
	scanf("%lld",&ans);
	return ans;
}
LL* substring(LL *str,LL st,LL ed){
	LL len = ed-st+1;
	LL * ans;
	ans = malloc(len+16);
	(*ans) = len;
	memcpy((char*)((ans+1)),((char*)((str+1)))+st,len);
	*((char*)(ans+1)+len) = 0;
	return ans;
}
LL ord(LL* a,LL pos){
	return (LL)(*(((char*)(a+1))+pos));
}




int main(){
	//printlnInt(main_());
	printlnInt(n);
	//println(toString(ord(toString(12345),3)));
	//printlnInt(getInt());
	println(getString());
	println(getString());
	init_func();
	{
		func_x = getInt();
		func_y = getInt();
		func_num = getInt();
		set_func();
	}
	{
		func_x = getInt();
		func_y = getInt();
		func_num = getInt();
		set_func();
	}
	{
		func_x = getInt();
		func_num = getInt();
		seek_func();
		printlnInt(func_y);
		
	}
	//return 96;
}

