#ifndef SJTU_DEQUE_HPP
#define SJTU_DEQUE_HPP
 
#include "exceptions.hpp"
 
#include <cstddef>
#include <iostream>
 
namespace sjtu { 
const int LEN = 800;

//node* is very like iterator
template<class T>
class node{
	//This type is the vertice
	public:
	//You can transport the data without distruction or construction
	T *data;
	//The name is what it means
	node *last, *next;
	//Construct
	node(): data(NULL), last(NULL), next(NULL){}
	
	//Distruct
	//Note: data might be empty
	~node(){
		if (data != NULL) delete data;
	}
};

//This is a special vector, with node instead of iterator.
//1 based
template<class T>
class vec {
	public:
	//size
	int sz;
	//the head and tail nodes are empty nodes
	node<T> *head, *tail;
	
	//construct
	vec(){
		head = new node<T>;
		tail = new node<T>;
		head -> next = tail;
		tail -> last = head;
		head -> last = NULL;
		tail -> next = NULL;
		sz = 0;
	}
	
	//destruct
	//delete the node , so the data is deleted. 
	~vec(){
		node<T> *p;
		p = head;
		while (p != NULL) {
			p = head -> next;
			delete head;
			head = p;
		}
	}
	
	//clear the vector
	//a little bit faster than you pop it 
	void clear(){
		node<T> *p;
		p = head;
		while (p != NULL) {
			p = head -> next;
			delete head;
			head = p;
		}
		head = new node<T>;
		tail = new node<T>;
		head -> next = tail;
		tail -> last = head;
		head -> last = NULL;
		tail -> next = NULL;
		sz = 0;
	}
	//push left: push an element from the left
	//O(1)
	void push_l(T *val){
		node<T> *p;
		++sz;
		p = new node<T>;
		p -> data = val;
		p -> next = head -> next;
		p -> last = head;
		p -> next -> last = p;
		p -> last -> next = p;
	}
	
	//push right: push an element from the right
	//O(1)
	void push_r(T *val){
		node<T> *p;
		++sz;
		p = new node<T>;
		p -> data = val;
		p -> next = tail;
		p -> last = tail -> last;
		p -> next -> last = p;
		p -> last -> next = p;
	}
	
	//pop left: pop an element from the left
	//O(1)
	void pop_l(){
		if (sz == 0){
			throw runtime_error();
		}
		node<T> *p;
		p = head -> next;
		p -> next -> last = p -> last;
		p -> last -> next = p -> next;
		delete p;
		--sz;
	}
	
	//pop right: pop an element from the left
	//O(1)
	void pop_r(){
		if (sz == 0){
			throw runtime_error();
		}
		node<T> *p;
		p = tail -> last;
		p -> next -> last = p -> last;
		p -> last -> next = p -> next;
		delete p;
		--sz;
	}
	
	//construct with copy
	vec(const vec &other){
		head = new node<T>;
		tail = new node<T>;
		head -> next = tail;
		tail -> last = head;
		head -> last = NULL;
		tail -> next = NULL;
		node<T> *p;
		p = other. head -> next;
		while (p != other . tail){
			push_r(new T (*(p -> data)));
			p = p -> next;
		}
		sz = other.sz;
	}
	
	// = : Example: vec a, b;  a = b;
	//O(n)
	vec& operator = (const vec &other){
		if (this == & other) return *this;
		node<T> *p;
		p = head;
		while (p != NULL) {
			p = head -> next;
			delete head;
			head = p;
		}
		head = new node<T>;
		tail = new node<T>;
		head -> next = tail;
		tail -> last = head;
		head -> last = NULL;
		tail -> next = NULL;
		
		p = other. head -> next;
		while (p != other . tail){
			push_r(new T (*(p -> data)));
			p = p -> next;
		}
		sz = other.sz;
 
		return *this;
	}
	
	// show the top element from the left
	//O(1)
	T& top_l(){
		if (sz == 0){
			throw runtime_error();
		}
		return *(head -> next -> data);
	}
	
	// show the top element from the right
	//O(1)
	T& top_r(){
		if (sz == 0){
			throw runtime_error();
		}
		return *(tail -> last -> data);
	}
	
	//find the xth element, get the node*
	//O(n)
	node<T>* find(const int &x){
		if (x < 1 || x > sz) throw runtime_error();
		node<T> *p;
		p = head;
		for (int f1=1;f1<=x;f1++){
			p = p -> next;
		}
		return p;
	}
	
	//erase an element
	//O(1)
	void erase(node<T> *p){
		if (p == NULL || p -> next == NULL || p -> last == NULL)
		throw runtime_error();
		p -> next -> last = p -> last;
		p -> last -> next = p -> next;
		delete p;
		--sz;
	}
	
	//insert an element
	//notice: T *val
	//insert(p, new T (value)) is recommanded
	//O(1)
	void insert(node<T> *p, T *val){
		if (p == NULL || p -> last == NULL)
		throw runtime_error();
		node<T> *np;
		np = new node<T>;
		np -> data = val;
		np -> next = p;
		np -> last = p -> last;
		np -> next -> last = np;
		np -> last -> next = np;
		++sz;
	}
	
	//an example of real erase
	void erase(const int &x){
		erase (find (x) );
	}
	
	//You can delete this part: view()
	void view(){
		node <T> *p;
		p = head -> next;
		while (p != tail){
			std::cout<<' '<<*(p -> data);
			p = p -> next;
		}
		std::cout<<"|"<<sz<<"";
	}
};
 
template<class T>
class deque {
	
	//friend deque;
private:
	vec < vec < T > > data;
	int sz;
public:
	class iterator;
	class const_iterator;
	friend iterator;
	friend const_iterator;
	friend int getpos(const iterator &pos);
	friend int getpos(const const_iterator &pos);
	class iterator {
		friend deque;
	private:
		//point to which deque
		const deque *deq;
		//which element
		node <vec<T> > *p;
		node <T> *pp;
		/**
		 * TODO add data members
		 *   just add whatever you want.
		 */
	public:
		//construct
		//construct
		//construct
		iterator ():p(NULL), pp(NULL), deq(NULL){
			
		}
		iterator (const iterator &other):p(other.p), pp(other.pp), deq(other.deq){
			
		}
		//get the index of an iterator
		//1 based
		friend int getpos(const iterator &pos){
			//if (pos.pp == pos.p -> data -> tail) return pos.deq ->sz +1;
			node <vec<T> > *p;
			node <T> *pp;
			int ans = 0 ;
			p = (pos . deq) -> data. head -> next;
			while (p != pos.p){
				ans += p -> data -> sz;
				p = p -> next;
			}
			pp = p -> data -> head;
			while (pp != pos.pp ){
				++ans;
				pp = pp -> next;
			}
			return ans;
		}
		
		
		/**
		 * return a new iterator which pointer n-next elements
		 *   even if there are not enough elements, the behaviour is **undefined**.
		 * as well as operator-
		 */
		iterator operator+(const int &n) const {
			if (n < 0) return *this - (-n);
			iterator ans = *this;
			int cnt = n;
			while (cnt > 0 && ans.pp != ans.p -> data -> tail){
				--cnt;
				ans.pp = ans.pp -> next;
			}
			if (ans.pp != ans.p -> data -> tail) return ans;
			++ cnt;
			ans.p = ans.p -> next;
			while (ans.p != deq->data.tail && cnt > ans.p ->data -> sz){
				cnt -= ans.p ->data ->sz;
				ans.p = ans.p -> next;
			}
			if (ans.p == deq->data.tail){
				if (cnt > 1) throw invalid_iterator(); 
				ans.p = ans.p -> last;
				ans.pp = ans.p -> data -> tail;
				return ans;
			}
			ans.pp = ans.p -> data -> find(cnt);
			return ans;
			//TODO
		}
		iterator operator-(const int &n) const {
			if (n < 0) return *this + (-n);
			iterator ans = *this;
			int cnt = n;
			while (cnt > 0 && ans.pp != ans.p -> data -> head){
				--cnt;
				ans.pp = ans.pp -> last;
			}
			if (ans.pp != ans.p -> data -> head) return ans;
			++ cnt;
			ans.p = ans.p -> last;
			while (ans.p != deq->data.head && cnt > ans.p ->data ->sz){
				cnt -= ans.p -> data ->sz;
				ans.p = ans.p -> last;
			}
			if (ans.p == deq->data.head) throw invalid_iterator();
			ans.pp = ans.p -> data -> find(ans.p ->data ->sz +1 -cnt);
			return ans;
			//TODO
		}
		// return th distance between two iterator,
		// if these two iterators points to different vectors, throw invaild_iterator.
		int operator-(const iterator &rhs) const {
			if (deq != rhs.deq ) throw invalid_iterator();
			return getpos(*this) - getpos(rhs);
			//TODO
		}
		iterator& operator+=(const int &n) {
			*this = *this + n;
			return *this;
			//TODO
		}
		iterator& operator-=(const int &n) {
			*this = *this -n;
			return *this;
			//TODO
		}
		/**
		 * TODO iter++
		 */
		iterator operator++(int) {
			iterator ans;
			ans = *this;
			*this = *this +1;
			return ans;
		}
		/**
		 * TODO ++iter
		 */
		iterator& operator++() {
			*this = *this +1;
			return *this;
		}
		/**
		 * TODO iter--
		 */
		iterator operator--(int) {
			iterator ans;
			ans = *this;
			*this = *this -1;
			return ans;
		}
		/**
		 * TODO --iter
		 */
		iterator& operator--() {
			*this = *this -1;
			return *this;
		}
		/**
		 * TODO *it
		 */
		T& operator*() const {
			if (pp -> data == NULL) throw invalid_iterator();
			return *(pp-> data);
		}
		/**
		 * TODO it->field
		 */
		T* operator->() const noexcept {
			if (pp -> data == NULL) throw invalid_iterator();
			return  pp -> data;
		}
		/**
		 * a operator to check whether two iterators are same (pointing to the same memory).
		 */
		bool operator==(const iterator &rhs) const {
			return p == rhs.p && pp == rhs.pp && deq == rhs.deq;
		}
		bool operator==(const const_iterator &rhs) const {
			return p == rhs.p && pp == rhs.pp && deq == rhs.deq;
		}
		/**
		 * some other operator for iterator.
		 */
		bool operator!=(const iterator &rhs) const {
			return p != rhs.p || pp != rhs.pp || deq != rhs.deq;
		}
		bool operator!=(const const_iterator &rhs) const {
			return p != rhs.p || pp != rhs.pp || deq != rhs.deq;
		}
	};
	class const_iterator {
		// it should has similar member method as iterator.
		//  and it should be able to construct from an iterator.

		//most of it is the same as iterator
		//You can see iterator instead
		private:
			const deque *deq;
			const node <vec<T> > *p;
			const node <T> *pp;
			// data members.
		public:
			const_iterator ():p(NULL), pp(NULL), deq(NULL){
				
			}
			const_iterator (const iterator &other):p(other.p), pp(other.pp), deq(other.deq){
				
			}
			const_iterator (const const_iterator &other):p(other.p), pp(other.pp), deq(other.deq){
				
			}
 
			friend int getpos(const const_iterator &pos){
				//if (pos.pp == pos.p -> data -> tail) return pos.deq ->sz +1;
				node <vec<T> > *p;
				node <T> *pp;
				int ans = 0 ;
				p = (pos . deq) -> data. head -> next;
				while (p != pos.p){
					ans += p -> data -> sz;
					p = p -> next;
				}
				pp = p -> data -> head;
				while (pp != pos.pp ){
					++ans;
					pp = pp -> next;
				}
				return ans;
			}
			/**
			 * return a new iterator which pointer n-next elements
			 *   even if there are not enough elements, the behaviour is **undefined**.
			 * as well as operator-
			 */
			const_iterator operator+(const int &n) const {
				if (n < 0) return *this - (-n);
				const_iterator ans = *this;
				int cnt = n;
				while (cnt > 0 && ans.pp != ans.p -> data -> tail){
					--cnt;
					ans.pp = ans.pp -> next;
				}
				if (ans.pp != ans.p -> data -> tail) return ans;
				++ cnt;
				ans.p = ans.p -> next;
				while (ans.p != deq->data.tail && cnt > ans.p ->data -> sz){
					cnt -= ans.p ->data ->sz;
					ans.p = ans.p -> next;
				}
				if (ans.p == deq->data.tail){
					if (cnt > 1) throw invalid_iterator(); 
					ans.p = ans.p -> last;
					ans.pp = ans.p -> data -> tail;
					return ans;
				}
				ans.pp = ans.p -> data -> find(cnt);
				return ans;
				//TODO
			}
			const_iterator operator-(const int &n) const {
				if (n < 0) return *this - (-n);
				const_iterator ans = *this;
				int cnt = n;
				while (cnt > 0 && ans.pp != ans.p -> data -> head){
					--cnt;
					ans.pp = ans.pp -> last;
				}
				if (ans.pp != ans.p -> data -> head) return ans;
				++ cnt;
				ans.p = ans.p -> last;
				while (ans.p != deq->data.head && cnt > ans.p ->data ->sz){
					cnt -= ans.p -> data ->sz;
					ans.p = ans.p -> last;
				}
				if (ans.p == deq->data.head) throw invalid_iterator();
				ans.pp = ans.p -> data -> find(ans.p ->data ->sz +1 -cnt);
				return ans;
				//TODO
			}
			// return th distance between two iterator,
			// if these two iterators points to different vectors, throw invaild_iterator.
			int operator-(const const_iterator &rhs) const {
				if (deq != rhs.deq ) throw invalid_iterator();
				return getpos(*this) - getpos(rhs);
				//TODO
			}
			const_iterator& operator+=(const int &n) {
				*this = *this + n;
				return *this;
				//TODO
			}
			const_iterator& operator-=(const int &n) {
				*this = *this -n;
				return *this;
				//TODO
			}
			/**
			 * TODO iter++
			 */
			const_iterator operator++(int) {
				const_iterator ans;
				ans = *this;
				*this = *this +1;
				return ans;
			}
			/**
			 * TODO ++iter
			 */
			const_iterator& operator++() {
				*this = *this +1;
				return *this;
			}
			/**
			 * TODO iter--
			 */
			const_iterator operator--(int) {
				const_iterator ans;
				ans = *this;
				*this = *this -1;
				return ans;
			}
			/**
			 * TODO --iter
			 */
			const_iterator& operator--() {
				*this = *this -1;
				return *this;
			}
			/**
			 * TODO *it
			 */
			T& operator*() const {
				if (pp -> data == NULL) throw invalid_iterator();
				return *(pp-> data);
			}
			/**
			 * TODO it->field
			 */
			T* operator->() const noexcept {
				if (pp -> data == NULL) throw invalid_iterator();
				return  pp -> data;
			}
			/**
			 * a operator to check whether two iterators are same (pointing to the same memory).
			 */
			bool operator==(const iterator &rhs) const {
				return p == rhs.p && pp == rhs.pp && deq == rhs.deq;
			}
			bool operator==(const const_iterator &rhs) const {
				return p == rhs.p && pp == rhs.pp && deq == rhs.deq;
			}
			/**
			 * some other operator for iterator.
			 */
			bool operator!=(const iterator &rhs) const {
				return p != rhs.p || pp != rhs.pp || deq != rhs.deq;
			}
			bool operator!=(const const_iterator &rhs) const {
				return p != rhs.p || pp != rhs.pp || deq != rhs.deq;
			}
			// And other methods in iterator.
			// And other methods in iterator.
			// And other methods in iterator.
	};
	/**
	 * TODO Constructors
	 */
	deque():sz(0) {
		data.push_l( new vec<T>() );
	}
	deque(const deque &other) : sz(other.sz), data(other.data){}
	/**
	 * TODO Deconstructor
	 */
	~deque() {
		//Nothing
	}
	/**
	 * TODO assignment operator
	 */
	deque &operator=(const deque &other) {
		if (this == &other) return *this;
		data = other.data;
		sz = other.sz;
		return *this;
	}
	/**
	 * access specified element with bounds checking
	 * throw index_out_of_bound if out of bound.
	 */
	T & at(const size_t &pos) {
		int seek = int (pos) +1;
		if (seek > sz || seek < 1) throw index_out_of_bound();
		node < vec<T> > *p;
		p =  data . head -> next;
		while (seek > (p -> data -> sz) ){
			seek -= ( p -> data -> sz );
			p = p -> next;
		}
		node <T> *pp;
		pp = p -> data -> find(seek);
		return *(pp -> data);
	}
	const T & at(const size_t &pos) const {
		int seek = int (pos) +1;
		if (seek > sz || seek < 1) throw index_out_of_bound();
		node < vec<T> > *p;
		p =  data . head -> next;
		while (seek > (p -> data -> sz) ){
			seek -= ( p -> data -> sz );
			p = p -> next;
		}
		node <T> *pp;
		pp = p -> data -> find(seek);
		return *(pp -> data);
	}
	T & operator[](const size_t &pos) {
		int seek = int (pos) +1;
		if (seek > sz || seek < 1) throw index_out_of_bound();
		node < vec<T> > *p;
		p =  data . head -> next;
		while (seek > (p -> data -> sz) ){
			seek -= ( p -> data -> sz );
			p = p -> next;
		}
		node <T> *pp;
		pp = p -> data -> find(seek);
		return *(pp -> data);
	}
	const T & operator[](const size_t &pos) const {
		int seek = int (pos) +1;
		if (seek > sz || seek < 1) throw index_out_of_bound();
		node < vec<T> > *p;
		p =  data . head -> next;
		while (seek > (p -> data -> sz) ){
			seek -= ( p -> data -> sz );
			p = p -> next;
		}
		node <T> *pp;
		pp = p -> data -> find(seek);
		return *(pp -> data);
	}
	/**
	 * access the first element
	 * throw container_is_empty when the container is empty.
	 */
	const T & front() const {
		if (sz < 1) throw container_is_empty();
		node <vec<T> > *p;
		node <T> *pp;
		p = data . head -> next;
		pp = p -> data -> head -> next;
		return *(pp -> data);
	}
	/**
	 * access the last element
	 * throw container_is_empty when the container is empty.
	 */
	const T & back() const {
		if (sz < 1) throw container_is_empty();
		node <vec<T> > *p;
		node <T> *pp;
		p = data . tail -> last;
		pp = p -> data -> tail -> last;
		return *(pp -> data);
	}
	/**
	 * returns an iterator to the beginning.
	 */
	iterator begin(){
		iterator ans;
		ans.deq = (this);
		ans.p = (data.head -> next);
		ans.pp = (ans.p ->data ->head ->next);
		return ans;
	}
	const_iterator cbegin() const {
		iterator ans;
		ans.deq = (this);
		ans.p = (data.head -> next);
		ans.pp = (ans.p ->data ->head ->next);
		return ans;
	}
	/**
	 * returns an iterator to the end.
	 */
	iterator end() {
		iterator ans;
		ans.deq = this;
		ans.p = data.tail -> last;
		ans.pp = ans.p -> data -> tail;
		return ans;
	}
	const_iterator cend() const {
		iterator ans;
		ans.deq = this;
		ans.p = data.tail -> last;
		ans.pp = ans.p -> data -> tail;
		return ans;
		
	}
	/**
	 * checks whether the container is empty.
	 */
	bool empty() const {
		return sz == 0;
	}
	/**
	 * returns the number of elements
	 */
	size_t size() const {
		return size_t(sz);
	}
	/**
	 * clears the contents
	 */
	void clear() {
		while (data . sz >0) data . pop_l();
		data . push_l (new vec<T> ());
		sz = 0;
	}
	/**
	 * inserts elements at the specified locat on in the container.
	 * inserts value before pos
	 * returns an iterator pointing to the inserted value
	 *     throw if the iterator is invalid or it point to a wrong place.
	 */
	iterator insert(iterator pos, const T &value) {
		if (pos.deq != this || pos.p == data.head || pos.p == data.tail ||
		pos.pp == pos.p -> data -> head)
		throw runtime_error();
		
		iterator ans;
		ans.deq = this;
		ans.p = pos.p;
		ans.pp = pos.pp -> last;
		
		pos.p -> data -> insert(pos.pp, new T (value));
		++sz;
		node<vec<T> > *p0;
		p0 = pos.p;
		while (p0 -> next != data.tail){
			p0 -> next -> data -> push_l (p0 -> data -> tail -> last -> data);
			p0 -> data -> tail -> last -> data = NULL;
			p0 -> data -> pop_r();
			p0 = p0 -> next;
		}
		if (p0 -> data -> sz > LEN){
			data. insert(p0 -> next, new vec<T>);
			p0 -> next -> data -> push_l (p0 -> data -> tail -> last -> data);
			p0 -> data -> tail -> last -> data = NULL;
			p0 -> data -> pop_r();
		}
		return ans+1;
	}
	/**
	 * removes specified element at pos.
	 * removes the element at pos.
	 * returns an iterator pointing to the following element, if pos pointing to the last element, end() will be returned.
	 * throw if the container is empty, the iterator is invalid or it points to a wrong place.
	 */
	iterator erase(iterator pos) {
		if (pos.deq != this || pos.p == data.head || pos.p == data.tail ||
		pos.pp == pos.p -> data -> head || pos.pp == pos.p -> data -> tail)
		throw runtime_error();
		--sz;
		
		iterator ans;
		ans.deq = this;
		ans.p = pos.p;
		ans.pp = pos.pp -> last;
		
		pos.p -> data -> erase(pos.pp);
		node<vec<T> > *p0;
		p0 = pos.p;
		while (p0 -> next != data.tail){
			p0 -> data -> push_r (p0 -> next -> data -> head -> next -> data);
			p0 -> next -> data -> head -> next -> data = NULL;
			p0 -> next -> data -> pop_l();
			p0 = p0 -> next;
		}
		if (p0 -> data -> sz == 0 && data.sz > 1){
			data.pop_r();
		}
		return ans+1;
	}
	/**
	 * adds an element to the end
	 */
	void push_back(const T &value) {
		if (data. tail -> last -> data -> sz >= LEN){
			data. push_r (new vec<T> ());
		}
		++sz;
		data . tail -> last -> data -> push_r ( new T(value));
	}
	/**
	 * removes the last element
	 *     throw when the container is empty.
	 */
	void pop_back() {
		if (sz <= 0) throw container_is_empty();
		--sz;
		data . tail -> last -> data -> pop_r();
		if (data . tail -> last -> data -> sz == 0 && data . sz >1) data.pop_r();
	}
	/**
	 * inserts an element to the beginning.
	 */
	void push_front(const T &value) {
		if (data. head -> next -> data -> sz >= LEN){
			data. push_l (new vec<T> ());
		}
		++sz;
		data . head -> next -> data -> push_l ( new T(value));
	}
	/**
	 * removes the first element.
	 *     throw when the container is empty.
	 */
	void pop_front() {
		if (sz <= 0) throw container_is_empty();
		--sz;
		data . head -> next -> data -> pop_l();
		if (data . head -> next -> data -> sz == 0 && data . sz >1) data.pop_l();
	}
	
	void view(){
		node<vec<T> > *p;
		node<T> *pp;
		p = data . head -> next;
		while (p != data . tail){
			std::cout<<"(";
			p -> data -> view();
			std::cout<<")";
			p = p -> next;
		}
		std::cout<<sz<<"\n";
	}
};
 
}
 
#endif