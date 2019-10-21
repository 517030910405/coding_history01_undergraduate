#include<cmath>
#include<cstdio>
/*
	written by Li Jiasen user_name
*/
using namespace std;
typedef long long LL;
struct node{
	int sz;
	LL a,b;//+ax+b
	LL sum;
	LL cover;
	bool ctf;
	node():sz(0),a(0ll),b(0ll),sum(0ll),cover(0ll),ctf(false){
	
	}
};
inline node merge(const node &f1,const node &f2){
	node ans;
	ans.sz=f1.sz+f2.sz;
	ans.sum=f1.sum+f2.sum;
	return ans;
}
const int N=300000+10;
node xds[(1<<21)+3];
int n,m,szx,p;//p is the real number of node
LL inp1[N];
int inp2[N][3];
LL inp3[N];
int findk(int x){
	int pos=1;
	while (pos<szx){
		if (x<=xds[pos<<1].sz){
			pos<<=1;
		} else{
			x-=xds[pos<<1].sz;
			(pos<<=1)^=1;
		}
	}
	return pos;
}
inline void covernode(const int &pos,const LL &cover){
	xds[pos].ctf=true;
	xds[pos].cover=cover;
	xds[pos].a=0;
	xds[pos].b=0;
	xds[pos].sum=cover*xds[pos].sz;
}
inline void addAP(const int &pos,const LL &a, const LL &b){
	xds[pos].a+=a;
	xds[pos].b+=b;
	xds[pos].sum+=a*xds[pos].sz*(xds[pos].sz+1)/2 + b*xds[pos].sz;
}

inline void pushdown(const int &pos){
	//not the root
	if (xds[pos].ctf){
		covernode(pos*2,xds[pos].cover);
		covernode(pos*2+1,xds[pos].cover);
		xds[pos].ctf=false;
	}
	addAP(pos*2,xds[pos].a,xds[pos].b);
	addAP(pos*2+1,xds[pos].a,xds[pos].b+xds[pos*2].sz*xds[pos].a);
	xds[pos].a=0;
	xds[pos].b=0;
}
void change1(const int &pos,const LL x,const int &l0,const int &r0,const int &l,const int &r){
	if (l0<=l&&r<=r0){
		covernode(pos,x);
		return;
	}
	if (r<l0||r0<l)return;
	pushdown(pos);
	change1(pos*2,x,l0,r0,l,(l+r)/2);
	change1(pos*2+1,x,l0,r0,(l+r)/2+1,r);
	xds[pos]=merge(xds[pos*2],xds[pos*2+1]);
}
void change2(const int &pos,const LL &a,const LL &b,const int &l0,const int &r0,const int &l,const int &r){
	if (l0<=l&&r<=r0){
		addAP(pos,a,b);
		return;
	}
	if (r<l0||r0<l)return;
	pushdown(pos);
	change2(pos*2,a,b,l0,r0,l,(l+r)/2);
	change2(pos*2+1,a,b+xds[pos*2].sz*a,l0,r0,(l+r)/2+1,r);
	xds[pos]=merge(xds[pos*2],xds[pos*2+1]);
}
void change3(const int &pos,const int &c,const LL &x,const int &l,const int &r){
	if (l==c&&r==c){
		xds[pos].sz++;
		xds[pos].sum=x;
		return ;
	}
	if (r<c||c<l)return;
	pushdown(pos);
	change3(pos*2,c,x,l,(l+r)/2);
	change3(pos*2+1,c,x,(l+r)/2+1,r);
	xds[pos]=merge(xds[pos*2],xds[pos*2+1]);
}
LL tell(const int &pos,const int &l0,const int &r0,const int &l,const int &r){
	if (l0<=l&&r<=r0){
		return xds[pos].sum;
	}
	if (r0<l||r<l0) return 0ll;
	pushdown(pos);
	return tell(pos*2,l0,r0,l,(l+r)/2)+tell(pos*2+1,l0,r0,(l+r)/2+1,r);
}

int main(){
	scanf("%d%d",&n,&m);
	for (int f1=1;f1<=n;++f1)scanf("%lld",&inp1[f1]);
	p=n;
	for (int f1=1;f1<=m;++f1){
		scanf("%d%d",&inp2[f1][0],&inp2[f1][1]);
		if (inp2[f1][0]!=3)scanf("%d",&inp2[f1][2]);
		if (inp2[f1][0]!=4)scanf("%lld",&inp3[f1]);
		if (inp2[f1][0]==3)++p;
	}
	szx=1;
	while (szx<p+2)szx+=szx;
	for (int f1=1;f1<=p;f1++){
		xds[f1+szx].sz=1;
	}
	for (int f1=szx-1;f1>=1;--f1){
		xds[f1]=merge(xds[f1*2],xds[f1*2+1]);
	}
	for (int f1=m;f1>=1;--f1){
		if (inp2[f1][0]==3){
			int pos=findk(inp2[f1][1]);
			inp2[f1][2]=pos;
			while(pos>=1){
				xds[pos].sz-=1;
				pos>>=1;
			}
		}
	}
	for (int f1=1;f1<=n;f1++){
		int pos=findk(f1);
		xds[pos].sum=inp1[f1];
		pos>>=1;
		while (pos>=1){
			xds[pos]=merge(xds[pos*2],xds[pos*2+1]);
			pos>>=1;
		}
	}
	
	for (int f1=1;f1<=m;++f1){
		if (inp2[f1][0]==1){
			int l=findk(inp2[f1][1])-szx,r=findk(inp2[f1][2])-szx;
			change1(1,inp3[f1],l,r,0,szx-1);
		}else if (inp2[f1][0]==2){
			int l=findk(inp2[f1][1])-szx,r=findk(inp2[f1][2])-szx;
			LL a,b;
			a=inp3[f1];
			b=a-inp2[f1][1]*a;
			change2(1,a,b,l,r,0,szx-1);
		} else if (inp2[f1][0]==3){
			int c=inp2[f1][2]-szx;
			change3(1,c,inp3[f1],0,szx-1);
		} else if (inp2[f1][0]==4){
			int l=findk(inp2[f1][1])-szx,r=findk(inp2[f1][2])-szx;
			LL ans=tell(1,l,r,0,szx-1);
			printf("%lld\n",ans);
		}
	}
}
