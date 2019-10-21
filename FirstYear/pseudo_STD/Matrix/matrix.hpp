#ifndef SJTU_MATRIX_HPP
#define SJTU_MATRIX_HPP

#include <cstddef>
#include <initializer_list>
#include <utility>
#include <iterator>
#include <algorithm>
#include <cassert>
using std::size_t;

namespace sjtu
{
	static int cnt;
	template <class T>
	class Matrix
	{
		template <class U>
		friend class Matrix;
	private:
		// your private member variables here.
		T *data;
		int szn,szm;
		int tellsz(int n,int m)const{
			return n*m;
		}
		
	public:
	
		Matrix(){
			szn=szm=0;
			data=0;
		}
		
		Matrix(size_t n, size_t m, T _init = T())
		{
			if (!(n>0&&m>0)){
				throw(std::invalid_argument("SIZE"));
				data=0;
				return ;
			}
			szn=n;
			szm=m;
			data=new T [tellsz(szn,szm)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<szn*szm;f1++)
			data[f1]=_init;
		}
		
		explicit Matrix(std::pair<size_t, size_t> sz, T _init = T())
		{
			if (!(sz.first>0&&sz.second>0)){
				throw(std::invalid_argument("SIZE"));
				data=0;
				return ;
			}
			szn=sz.first;
			szm=sz.second;
			data=new T [tellsz(szn,szm)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<szn*szm;f1++)
			data[f1]=_init;
		}
		
		Matrix(const Matrix &o)
		{
			szn=o.szn;
			szm=o.szm;
			data=new T [tellsz(szn,szm)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<szn*szm;f1++){
				data[f1]=o.data[f1];
			}
		}
		
		template <class U>
		Matrix(const Matrix<U> &o)
		{
			szn=o.szn;
			szm=o.szm;
			data=new T [tellsz(szn,szm)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<szn*szm;f1++)
			data[f1]=T(o.data[f1]);
		}
		
		Matrix &operator=(const Matrix &o)
		{
			if (this==&o) return *this;
			szn=o.szn;
			szm=o.szm;
			if (data!=0) {
				delete [] data;
				{
					cnt--;
					//std::cout<<cnt<<std::endl;
				}
			}
			data=new T [tellsz(szn,szm)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<szn*szm;f1++){
				data[f1]=o.data[f1];
			}
			return *this;
		}
		
		template <class U>
		Matrix &operator=(const Matrix<U> &o)
		{
			//if (this==&o) return *this;
			szn=o.szn;
			szm=o.szm;
			if (data!=0) {
				delete [] data;
				{
					cnt--;
					//std::cout<<cnt<<std::endl;
				}
			}
			data=new T [tellsz(szn,szm)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<szn*szm;f1++)
			data[f1]=T(o.data[f1]);
			return *this;
		}
		
		Matrix(Matrix &&o) noexcept
		{
			szn=o.szn;
			szm=o.szm;
			data=o.data;
			o.data=NULL;
		}
		
		Matrix &operator=(Matrix &&o) noexcept
		{
			if (this==&o) return *this;
			szn=o.szn;
			szm=o.szm;
			if (data!=0) {
				delete [] data;
				{
					cnt--;
					//std::cout<<cnt<<std::endl;
				}
			}
			data=o.data;
			o.data=NULL;
			return *this;
		}
		
		~Matrix() {
			if (data!=0){
				delete [] data;
				{
					cnt--;
					//std::cout<<cnt<<std::endl;
				}
			}
		}
		
		Matrix(std::initializer_list<std::initializer_list<T>> il)
		{
			szn=il.size();
			szm=(*il.begin()).size();
			data=new T [tellsz(szn,szm)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			auto it1=il.begin();
			for (int f1=0;f1<szn;f1++){
				if ((*it1).size()!=szm){
					delete [] data;
					{
						cnt--;
						//std::cout<<cnt<<std::endl;
					}
					throw (std::invalid_argument("INIT"));

					return ;
				}
				auto it2=(*it1).begin();
				for (int f2=0;f2<szm;f2++){
					data[f1*szm+f2]=*it2;
					it2++;
				}
				it1++;
			}
		}
		
	public:
		size_t rowLength() const {return szn; }
		
		size_t columnLength() const {return szm; }
		
		void resize(size_t _n, size_t _m, T _init = T())
		{
			if (!(_n>0&&_m>0)){
				throw (std::invalid_argument("SIZE"));
				return ;
			}
			if (int(szn*szm)==int(_n*_m)){
				szn=_n;
				szm=_m;
				return ;
			}
			T *ans;
			ans=new T [tellsz(_n,_m)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<std::min(szn*szm,int(_n)*int(_m));f1++)
			ans[f1]=data[f1];
			for (int f1=szn*szm;f1<int(_n)*int(_m);f1++){
				ans[f1]=_init;
			}
			if (data!=0) {
				delete [] data;
				{
					cnt--;
					//std::cout<<cnt<<std::endl;
				}
			}
			data=ans;
			ans=0;
			szn=_n;
			szm=_m;
		}
		
		void resize(std::pair<size_t, size_t> sz, T _init = T())
		{
			resize(sz.first,sz.second,_init);
		}
		
		std::pair<size_t, size_t> size() const
		{
			return std::make_pair(size_t(szn),size_t(szm));
		};
		
		void clear()
		{
			szn=0;
			szm=0;
			if (data!=0){
				delete [] data;
				{
					cnt--;
					//std::cout<<cnt<<std::endl;
				}
			}
			data=NULL;
		}
		
	public:
		const T &operator()(size_t i, size_t j) const
		{
			if (!(i>=0&&i<szn&&j>=0&&j<szm)){
				throw (std::invalid_argument("SIZE"));
				return data[0];
			}
			return data[i*szm+j];
		}
		
		T &operator()(size_t i, size_t j)
		{
			if (!(i>=0&&i<szn&&j>=0&&j<szm)){
				throw (std::invalid_argument("SIZE"));
				return data[0];
			}
			return data[i*szm+j];
		}
		
		Matrix<T> row(size_t i) const
		{
			Matrix<T> ans;
			ans.szn=1;
			ans.szm=szm;
			ans.data=0;
			if (!(i>=0&&i<szn)){
				throw (std::invalid_argument("SIZE"));
				return ans;
			}
			ans.data=new T [tellsz(ans.szn,ans.szm)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<szm;f1++){
				ans.data[f1]=data[i*szm+f1];
			}
			return ans;
		}
		
		Matrix<T> column(size_t i) const
		{
			Matrix<T> ans;
			ans.szn=szn;
			ans.szm=1;
			ans.data=0;
			if (!(i>=0&&i<szm)){
				throw (std::invalid_argument("SIZE"));
				return ans;
			}
			ans.data=new T [tellsz(ans.szn,ans.szm)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<szn;f1++){
				ans.data[f1]=data[i*szm+f1];
			}
			return ans;
		}
		
		
	public:
		template <class U>
		bool operator==(const Matrix<U> &o) const
		{
			if (szn!=o.szn||szm!=o.szm) return false;
			for (int f1=0;f1<szn*szm;f1++)if (data[f1]!=o.data[f1])return false;
			return true;
		}
		
		template <class U>
		bool operator!=(const Matrix<U> &o) const
		{
			if (szn!=o.szn||szm!=o.szm) return true;
			for (int f1=0;f1<szn*szm;f1++)if (data[f1]!=o.data[f1])return true;
			return false;			
		}
		
		Matrix operator-() const
		{
			Matrix <T> ans;
			ans.szn=szn;
			ans.szm=szm;
			ans.data=new T [tellsz(szn,szm)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<szn*szm;f1++) ans.data[f1]=-data[f1];
			return ans;
		}
		
		template <class U>
		Matrix &operator+=(const Matrix<U> &o)
		{
			//assert(szn==o.szn&&szm==o.szm);
			if (!(szn==o.szn&&szm==o.szm)) {
				throw (std::invalid_argument("SIZE"));
				return *this;
			}
			for (int f1=0;f1<szn*szm;f1++) data[f1]+=o.data[f1];
			return *this;
		}
		
		template <class U>
		Matrix &operator-=(const Matrix<U> &o)
		{
			//assert(szn==o.szn&&szm==o.szm);
			if (!(szn==o.szn&&szm==o.szm)){
				throw (std::invalid_argument("SIZE"));
				return *this;
			}
			for (int f1=0;f1<szn*szm;f1++) data[f1]-=o.data[f1];
			return *this;
		}
		
		template <class U>
		Matrix &operator*=(const U &x)
		{
			assert(szn*szm>0);
			for (int f1=0;f1<szn*szm;f1++)data[f1]*=x;
			return *this;
		}
		
		Matrix tran() const
		{
			Matrix<T> ans;
			ans.szn=szm;
			ans.szm=szn;
			ans.data= new T [tellsz(szm,szn)];
			{
				cnt++;
				//std::cout<<cnt<<std::endl;
			}
			for (int f1=0;f1<szn;f1++) for (int f2=0;f2<szm;f2++){
				ans.data[f2*szn+f1]=data[f1*szm+f2];
			}
			return ans;
		}
		
	public: // iterator
		class iterator
		{
			//friend class Matrix<T>::iterator;
		public:
			using iterator_category = std::random_access_iterator_tag;
			using value_type        = T;
			using pointer           = T *;
			using reference         = T &;
			using size_type         = size_t;
			using difference_type   = std::ptrdiff_t;
			
			iterator() = default;
			
			iterator(const iterator &) = default;
			
			iterator &operator=(const iterator &) = default;
			friend iterator Matrix::begin();
			friend iterator Matrix::end();
			friend std::pair<iterator, iterator> Matrix::subMatrix(std::pair<size_t, size_t> l, std::pair<size_t, size_t> r);
		private:
			T *position;
			int mszn,mszm,i1,i2,j1,j2,pos;
		public:

			difference_type operator-(const iterator &o)
			{
				if (!(position==o.position&&i1==o.i1&&i2==o.i2&&j1==o.j1&&j2==o.j2)){
					throw (std::invalid_argument ("ITERATOR COMPARE"));
					return 0;
				}
				int po2=(int(pos/mszm)-i1)*(j2-j1) +(pos%mszm-j1);
				int po3=(int(o.pos/mszm)-i1)*(j2-j1) +(o.pos%mszm-j1);

				return po2-po3;
			}
			
			iterator &operator+=(difference_type offset)
			{
				//position+=offset;
				int po2=(int(pos/mszm)-i1)*(j2-j1) +(pos%mszm-j1) + offset;
				pos=(int(po2/(j2-j1))+i1)*mszm + po2%(j2-j1)+j1;
				if (!(pos<=(i2)*mszm+j1)){
					throw ("ITER");
					return *this;
				}
				return *this;
			}
			
			iterator operator+(difference_type offset) const
			{
				iterator ans=*this;
				ans+=offset;
				return ans;
			}
			
			iterator &operator-=(difference_type offset)
			{
				int po2=(int(pos/mszm)-i1)*(j2-j1) +(pos%mszm-j1) - offset;
				pos=(int(po2/(j2-j1))+i1)*mszm + po2%(j2-j1)+j1;
				if (!(pos>=(i1)*mszm+j1)){
					throw ("ITER");
					return *this;
				}
				return *this;
			}
			
			iterator operator-(difference_type offset) const
			{
				iterator ans=*this;
				ans-=offset;
				return ans;
			}
			
			iterator &operator++()
			{
				*this+=1;
				return *this;
			}
			
			iterator operator++(int)
			{
				iterator ans=*this;
				*this +=1;
				return ans;
			}
			
			iterator &operator--()
			{
				*this-=1;
				return *this;
			}
			
			iterator operator--(int)
			{
				iterator ans=*this;
				*this -=1;
				return ans;
			}
			
			reference operator*() const
			{
				return *(position+pos);
			}
			
			pointer operator->() const
			{
				return position+pos;
			}
			
			bool operator==(const iterator &o) const
			{
				return (position==o.position&&pos==o.pos);
			}
			
			bool operator!=(const iterator &o) const
			{
				return (position!=o.position||pos!=o.pos);
			}
		};
		
		iterator begin()
		{
			iterator ans;
			ans.position=data;
			ans.mszn=szn;
			ans.mszm=szm;
			ans.i1=0;
			ans.i2=szn;
			ans.j1=0;
			ans.j2=szm;
			ans.pos=0;
			return ans;
		}
		
		iterator end()
		{
			iterator ans;
			ans.position=data;
			ans.mszn=szn;
			ans.mszm=szm;
			ans.i1=0;
			ans.i2=szn;
			ans.j1=0;
			ans.j2=szm;
			ans.pos=szn*szm;
			return ans;
		}
		
		std::pair<iterator, iterator> subMatrix(std::pair<size_t, size_t> l, std::pair<size_t, size_t> r)
		{
/*			std::pair<iterator, iterator> ans;
			ans.first.position =data+ l.first * szm + l.second ;
			ans.second.position=data+r.first*szm+r.second;
*/
			r.first++;
			r.second++;
			iterator ans1,ans2;
			ans1.position=data;
			ans1.mszn=szn;
			ans1.mszm=szm;
			ans1.i1=l.first;
			ans1.i2=r.first;
			ans1.j1=l.second;
			ans1.j2=r.second;
			ans1.pos=l.first * szm + l.second;
			ans2.position=data;
			ans2.mszn=szn;
			ans2.mszm=szm;
			ans2.i1=l.first;
			ans2.i2=r.first;
			ans2.j1=l.second;
			ans2.j2=r.second;
			ans2.pos=r.first*szm+l.second;
			
			return std::make_pair(ans1,ans2);
        }
		template <class TT, class U>
		friend auto operator*(const Matrix<TT> &mat, const U &x);
		template <class TT, class U>
		friend auto operator*(const U &x, const Matrix<TT> &mat);
		template <class U, class V>
		friend auto operator*(const Matrix<U> &a, const Matrix<V> &b);
		template <class U, class V>
		friend auto operator+(const Matrix<U> &a, const Matrix<V> &b);
		template <class U, class V>
		friend auto operator-(const Matrix<U> &a, const Matrix<V> &b);
	};
		
}

//
namespace sjtu
{
	
	template <class T, class U>
	auto operator*(const Matrix<T> &mat, const U &x)
	{
		using W=decltype(mat.data[0]*x);
		Matrix<W> ans;
		ans.szn=mat.szn;
		ans.szm=mat.szm;
		ans.data=new W [ans.tellsz(ans.szm,ans.szn)];
		{
			cnt++;
			//std::cout<<cnt<<std::endl;
		}
		for (int f1=0;f1<mat.szn*mat.szm;f1++)ans.data[f1]=mat.data[f1]*x;
		return ans;
	}
	
	template <class T, class U>
	auto operator*(const U &x, const Matrix<T> &mat)
	{
		using W=decltype(mat.data[0]*x);
		Matrix<W> ans;
		ans.szn=mat.szn;
		ans.szm=mat.szm;
		ans.data=new W [ans.tellsz(ans.szm,ans.szn)];
		{
			cnt++;
			//std::cout<<cnt<<std::endl;
		}
		for (int f1=0;f1<mat.szn*mat.szm;f1++)ans.data[f1]=mat.data[f1]*x;
		return ans;
	}
	
	template <class U, class V>
	auto operator*(const Matrix<U> &a, const Matrix<V> &b)
	{
		//printf("\n%d %d\n%d %d\n",a.szn,a.szm,b.szn,b.szm);
		//assert(a.szm==b.szn);
		using W=decltype(a.data[0]*b.data[0]);
		Matrix<W> ans;
		ans.szn=a.szn;
		ans.szm=b.szm;
		ans.data=0;
		if (!(a.szm==b.szn)){
			throw (std:: invalid_argument ("SIZE"));
			return ans;
		}
		ans.data=new W [ans.tellsz(ans.szm,ans.szn)];
		{
			cnt++;
			//std::cout<<cnt<<std::endl;
		}
		for (int f1=0;f1<a.szn;f1++)
		for (int f2=0;f2<b.szm;f2++){
			ans.data[f1*ans.szm+f2]=W();
			for (int f3=0;f3<a.szm;f3++){
				ans.data[f1*ans.szm+f2]+=a.data[f1*a.szm+f3]*b.data[f3*b.szm+f2];
			}
		}
		return ans;
	}
	
	template <class U, class V>
	auto operator+(const Matrix<U> &a, const Matrix<V> &b)
	{
		//assert(a.szn==b.szn&&a.szm==b.szm);
		using W=decltype(a.data[0]+b.data[0]);
		Matrix<W> ans;
		ans.szn=a.szn;
		ans.szm=b.szm;
		ans.data=0;
		if (!(a.szn==b.szn&&a.szm==b.szm)){
			throw (std:: invalid_argument ("SIZE"));
			return ans;
		}
		ans.data=new W [ans.tellsz(ans.szm,ans.szn)];
		{
			cnt++;
			//std::cout<<cnt<<std::endl;
		}
		for (int f1=0;f1<a.szn*a.szm;f1++) ans.data[f1]=a.data[f1]+b.data[f1];
		return ans;
	}
	
	template <class U, class V>
	auto operator-(const Matrix<U> &a, const Matrix<V> &b)
	{
		//assert(a.szn==b.szn&&a.szm==b.szm);
		using W=decltype(a.data[0]+b.data[0]);
		Matrix<W> ans;
		ans.szn=a.szn;
		ans.szm=b.szm;
		ans.data=0;
		if (!(a.szn==b.szn&&a.szm==b.szm)){
			throw (std:: invalid_argument ("SIZE"));
			return ans;
		}
		ans.data=new W [ans.tellsz(ans.szm,ans.szn)];
		{
			cnt++;
			//std::cout<<cnt<<std::endl;
		}
		for (int f1=0;f1<a.szn*a.szm;f1++) ans.data[f1]=a.data[f1]-b.data[f1];
		return ans;		
	}
	
}

#endif //SJTU_MATRIX_HPP

