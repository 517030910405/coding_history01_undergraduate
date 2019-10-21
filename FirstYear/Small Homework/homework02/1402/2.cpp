#include<cmath>
#include<iostream>
#include<cstdio>
using namespace std;

typedef long long LL;
template <class T>
T max(const T &a, const T &b){
	return (a<b)?b:a;
}
template <class T>
T min(const T &a, const T &b){
	return (b<a)?b:a;
}

struct node{
	int l,r,sz;
	LL val,a,b,sum,c;//y=ax+b
	bool sign,cover;
	int randnum;
	node():l(0),r(0),sz(1),val(0),a(0),b(0),sum(0),c(0),sign(false),cover(false),randnum(0){}
};
const int N=300000+10;
node v[N];
int root,sz=0;;
inline int newnode(){
	v[++sz].randnum=((rand()<<14)^rand());
	return sz;
}
inline void pushdown(int p){
	if (!v[p].sign&&!v[p].cover) return ;
	if (v[p].cover){
		v[p].cover=false;
		int p1;
		v[p].val=v[p].c;
		if (v[p].l!=0){
			p1=v[p].l;
			v[p1].cover=true;
			v[p1].sign=false;
			v[p1].a=0;
			v[p1].b=0;
			v[p1].c=v[p].c;
			v[p1].sum=v[p1].sz*v[p1].c;
		}
		if (v[p].r!=0){
			p1=v[p].r;
			v[p1].cover=true;
			v[p1].sign=false;
			v[p1].a=0;
			v[p1].b=0;
			v[p1].c=v[p].c;
			v[p1].sum=v[p1].sz*v[p1].c;
		}
	}
	if (v[p].sign){
		int cnt=0,p1;
		if (v[p].l!=0){
			p1=v[p].l;
			v[p1].sign=true;
			v[p1].b+=v[p].b;
			v[p1].a+=v[p].a;
			v[p1].sum+=v[p].b*v[p1].sz+v[p].a*v[p1].sz*(v[p1].sz+1)/2;
			cnt+=v[p1].sz;
		}
		v[p].val+=v[p].a*(cnt+1)+v[p].b;
		v[p].sign=false;
		++cnt;
		if (v[p].r!=0){
			p1=v[p].r;
			v[p1].sign=true;
			v[p1].b+=v[p].b+v[p].a*(cnt+1);
			v[p1].a+=v[p].a;
			v[p1].sum+=(v[p].b+v[p].a*(cnt+1))*v[p1].sz+v[p].a*v[p1].sz*(v[p1].sz+1)/2;
			cnt+=v[p1].sz;
		}
		v[p].a=0;
		v[p].b=0;
	}
	return;
	
}
inline void keep(int p){
	v[p].sz=1;
	v[p].sum=v[p].val;
	if (v[p].r!=0){
		v[p].sum+=v[v[p].r].sum;
		v[p].sz+=v[v[p].r].sz;
	}
	if (v[p].l!=0){
		v[p].sum+=v[v[p].l].sum;
		v[p].sz+=v[v[p].l].sz;
	}
	return;
}
int merge(const int &p1,const int &p2){
	if (p1==0)return p2;
	if (p2==0)return p1;
	if (v[p1].randnum>v[p2].randnum){
		pushdown(p1);
		v[p1].r=merge(v[p1].r,p2);
		keep(p1);
		return p1;
	} else{
		pushdown(p2);
		v[p2].l=merge(p1,v[p2].l);
		keep(p2);
		return p2;
	}
}
pair<int,int> split(int p,int n){
	if (n==0) return make_pair(0,p);
	if (n==v[p].sz) return make_pair(p,0);
	if (n<=v[v[p].l].sz){
		pushdown(p);
		pair<int,int> res=split(v[p].l, n);
		v[p].l=res.second;
		keep(p);
		res.second=p;
		return res;
	} else{
		pushdown(p);
		pair<int,int> res=split(v[p].r, n-v[v[p].l].sz-1);
		v[p].r=res.first;
		keep(p);
		res.first=p;
		return res;
	}
}
LL tell (int p,int l,int r){
	if (l<=1&&v[p].sz<=r) {/*printf("[%d]",p);*/return v[p].sum;}
	if (r<1||v[p].sz<l) return 0ll;
	LL ans=0;
	v[p].val;
	pushdown(p);
	int cnt=1;
	if (v[p].l!=0){ans+=tell(v[p].l,l,r);cnt+=v[v[p].l].sz;}
	if (l<=cnt&&cnt<=r)ans+=v[p].val;
	if (v[p].r!=0)ans+=tell(v[p].r,l-cnt,r-cnt);
	return ans;
}
void changec(int p,int l,int r,LL c){
	if (l<=1&&v[p].sz<=r){
		v[p].a=0;
		v[p].b=0;
		v[p].sign=false;
		v[p].sum=c*v[p].sz;
		v[p].c=c;
		v[p].cover=true;
		return;
	}
	if (r<1||v[p].sz<l) return;
	pushdown(p);
	int cnt=1;
	if (v[p].l!=0){changec(v[p].l,l,r,c);cnt+=v[v[p].l].sz;}
	if (l<=cnt&&cnt<=r){
		v[p].val=c;
	}
	if (v[p].r!=0)changec(v[p].r,l-cnt,r-cnt,c);
	keep(p);
	return ;
}

void change(int p,int l,int r,LL a,LL b){
	if (l<=1&&v[p].sz<=r){
		v[p].a+=a;
		v[p].b+=(1-l)*a+b;
		v[p].sign=true;
		v[p].sum+=b*v[p].sz+a*v[p].sz*(v[p].sz+1)/2;
		return;
	}
	if (r<1||v[p].sz<l) return;
	pushdown(p);
	int cnt=1;
	if (v[p].l!=0){change(v[p].l,l,r,a,b);cnt+=v[v[p].l].sz;}
	if (l<=cnt&&cnt<=r){
		v[p].val+=(2-l+v[v[p].l].sz)*a+b;
	}
	if (v[p].r!=0)change(v[p].r,l-cnt,r-cnt,a,b);
	keep(p);
	return ;
}
void visit(int p){
	if (p==0)return;
	visit(v[p].l);
	printf("[pos%d val%lld sum%lld][%d,%d]",p,v[p].val,v[p].sum,v[p].l,v[p].r);
	visit(v[p].r);
	
	
}
int main(){
	rand();rand();rand();
	int n,m;
	scanf("%d%d",&n,&m);
	root=0;
	for (int f1=1;f1<=n;f1++){
		int p=newnode();
		scanf("%lld",&v[p].val);
		v[p].sum=v[p].val;
		root=merge(root,p);
	}
	for (int f1=1;f1<=m;f1++){
		int op;
		scanf("%d",&op);
		if (op==1){
			int l,r;
			LL val;
			scanf("%d%d%lld",&l,&r,&val);
			changec(root,l,r,val);
		} else if (op==2){
			int l,r;
			LL val;
			scanf("%d%d%lld",&l,&r,&val);
			change(root,l,r,val,0);
		} else if (op==3){
			LL val;
			int pos;
			scanf("%lld%d",&val,&pos);
			pair<int,int > res;
			res=split(root,pos-1);
			int p=newnode();
			v[p].val=val;
			v[p].sum=val;
			root=merge(merge(res.first,p),res.second);
			
		} else if (op==4){
			int l,r;
			scanf("%d%d",&l,&r);
			printf("%lld\n",tell(root,l,r));
		}
		visit(root);
	}
}