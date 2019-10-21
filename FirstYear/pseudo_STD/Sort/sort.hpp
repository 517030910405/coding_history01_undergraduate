#ifndef sortsjtu
#define sortsjtu
#include<cstdio>
#include<cstdlib>
namespace sjtu{
	template<class T>
	void swap(T &a,T &b){
		T c=a;
		a=b;
		b=c;
	}
	void msort1(int *a,int *m1,int *m2,int l,int r){
		if (l==r)return;
		int m=(l+r)/2;
		msort1(a,m1,m2,l,m);
		msort1(a,m1,m2,m+1,r);
		for (int f1=l;f1<=m;++f1)m1[f1]=a[f1];
		for (int f2=m+1;f2<=r;++f2)m2[f2]=a[f2];
		int f1=l,f2=m+1;
		for (int f3=l;f3<=r;++f3){
			if (f1>m||(f2<=r&&m2[f2]<m1[f1]))a[f3]=m2[f2++];
			else a[f3]=m1[f1++];
		}
	}
	void msort2(int *a,int *m1,int *m2,int l,int r){
		if (r-l<=20){
			for (int f1=l;f1<r;++f1){
				for (int f2=l;f2<r;++f2){
					if (a[f2+1]<a[f2])swap(a[f2],a[f2+1]);
				}
			}
			return;
		}
		int m=(l+r)/2;
		msort2(a,m1,m2,l,m);
		msort2(a,m1,m2,m+1,r);
		for (int f1=l;f1<=m;++f1)m1[f1]=a[f1];
		for (int f2=m+1;f2<=r;++f2)m2[f2]=a[f2];
		int f1=l,f2=m+1;
		for (int f3=l;f3<=r;++f3){
			if (f1>m||(f2<=r&&m2[f2]<m1[f1]))a[f3]=m2[f2++];
			else a[f3]=m1[f1++];
		}
	}

	
	void sort1(int *a, int size){
		int *p1,*p2;
		p1=new int [size];
		p2=new int [size];
		msort1(a,p1,p2,0,size-1);
	}
	void sort3(int *a, int size){
		int *p1,*p2;
		p1=new int [size];
		p2=new int [size];
		msort2(a,p1,p2,0,size-1);
	}
	
	void sort2(int *a, int size){
		int step=1;
		int tmp,f2;
		int cnt=0;
		long long totcnt=0;
		while (step*2<size)step*=2;
		step-=1;
		//std::printf("%d\n",step);
		for (;step>=1;step>>=1){
			cnt=0;
			for (int f1=step;f1<size;++f1){
				tmp=a[f1];
				for (f2=f1-step;f2>=0&&a[f2]>tmp;f2-=step){
					a[f2+step]=a[f2];
					++cnt;
					++totcnt;
				}
				a[f2+step]=tmp;
			}
			std::printf("%d %d\n",cnt,step);
			step+=2;
			cnt=0;
			for (int f1=step;f1<size;++f1){
				tmp=a[f1];
				for (f2=f1-step;f2>=0&&a[f2]>tmp;f2-=step){
					a[f2+step]=a[f2];
					++cnt;
					++totcnt;
				}
				a[f2+step]=tmp;
			}
			std::printf("%d %d\n",cnt,step);
			step-=2;
		}
		std::printf("%lld\n",totcnt);
	}
	
	void qsort1(int *a,int *s1,int *s2,int *s0,int l,int r){
		//printf("(%d,%d)",l,r);
		if (l>=r) return;
		int sz0,sz1,sz2;
		sz0=sz1=sz2=0;
		int k=((rand()<<15)^rand())%(r-l+1)+l;
		for (int f1=l;f1<=r;++f1){
			if (a[f1]<a[k])s1[sz1++]=a[f1];
			else if (a[k]<a[f1])s2[sz2++]=a[f1];
			else s0[sz0++]=a[f1];
		}
		for (int f1=0;f1<sz1;++f1)a[l+f1]=s1[f1];
		for (int f1=0;f1<sz0;++f1)a[l+sz1+f1]=s0[f1];
		for (int f1=0;f1<sz2;++f1)a[l+sz1+sz0+f1]=s2[f1];
		qsort1(a,s1,s2,s0,l,l+sz1-1);
		qsort1(a,s1,s2,s0,l+sz1+sz0,l+sz1+sz0+sz2-1);
	}
	void qsort2(int *a,int *s1,int *s2,int *s0,int l,int r,int kk){
		//printf("(%d,%d)",l,r);
		if (l>=r) return;
		int sz0,sz1,sz2;
		sz0=sz1=sz2=0;
		int k=((rand()<<15)^rand())%(r-l+1)+l;
		for (int f1=l;f1<=r;++f1){
			if (a[f1]<a[k])s1[sz1++]=a[f1];
			else if (a[k]<a[f1])s2[sz2++]=a[f1];
			else s0[sz0++]=a[f1];
		}
		for (int f1=0;f1<sz1;++f1)a[l+f1]=s1[f1];
		for (int f1=0;f1<sz0;++f1)a[l+sz1+f1]=s0[f1];
		for (int f1=0;f1<sz2;++f1)a[l+sz1+sz0+f1]=s2[f1];
		if (l<=kk&&kk<l+sz1)qsort2(a,s1,s2,s0,l,l+sz1-1,kk);
		if (l+sz1+sz0<=kk&&kk<=r)qsort2(a,s1,s2,s0,l+sz1+sz0,l+sz1+sz0+sz2-1,kk);
	}
	
	
	void qsort(int *a,int size){
		int *s1,*s2,*s0;
		s1=new int [size];
		s2=new int [size];
		s0=new int [size];
		qsort1(a,s1,s2,s0,0,size-1);
		delete []s1;
		delete []s0;
		delete []s2;
	}
	void findk(int *a,int size,int k){
		int *s1,*s2,*s0;
		s1=new int [size];
		s2=new int [size];
		s0=new int [size];
		qsort2(a,s1,s2,s0,0,size-1,k);
		delete []s1;
		delete []s0;
		delete []s2;
	}
	
	
	
};




#endif

