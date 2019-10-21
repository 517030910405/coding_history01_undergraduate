#include<cmath>
#include<cstdio>
const int N=100000+10;
typedef long long LL;
LL a[N*2];
LL heap[N*2];
int sz=0;
int n,m;
void heap_push(LL x){
	++sz;
	int pos=sz;
	while (pos>1&&heap[pos>>1]>x){
		heap[pos]=heap[pos>>1];
		pos>>=1;
	}
	heap[pos]=x;
}
void heap_pop(){
	LL x=heap[sz];
	--sz;
	int pos=1,pos2;
	while ((pos<<1)<=sz){
		pos2=(pos<<1);
		if (pos2<sz&&heap[pos2+1]<heap[pos2])++pos2;
		if (x>heap[pos2]){
			heap[pos]=heap[pos2];
			pos=pos2;
		} else{
			heap[pos]=x;
			return;
		}
	}
	heap[pos]=x;
	return;
}
void heap_view(){
	for (int f1=1;f1<=sz;f1++)printf(" %lld",heap[f1]);
	printf("\n");
}
int main(){
	scanf("%d%d",&n,&m);
	for (int f1=1;f1<=n;f1++){
		scanf("%lld",&a[f1]);
	}
	while ((n-1)%(m-1)!=0){
		a[++n]=0;
	}
	for (int f1=1;f1<=n;f1++){
		heap_push(a[f1]);
	}
	LL ans=0;
	LL cc=0;
	while (sz>1){
		cc=0;
		for (int f1=1;f1<=m;f1++){
			cc+=heap[1];
			heap_pop();
		}
		heap_push(cc);
		ans+=cc;
	}
	printf("%lld\n",ans);
}





