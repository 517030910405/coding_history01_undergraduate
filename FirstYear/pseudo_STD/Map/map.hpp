/**
 * implement a container like std::map
 */
#ifndef SJTU_MAP_HPP
#define SJTU_MAP_HPP

// only for std::less<T>
#include <functional>
#include <iostream>
#include <cstddef>
#include "utility.hpp"
#include "exceptions.hpp"
//http://judge.hzfengsy.com/welcome
namespace sjtu {
template <class T>
T max(const T &a, const T &b){
	return (a<b)?b:a;
}



template<
	class Key,
	class T,
	class Compare = std::less<Key>
> class map {
public:
	//Notice the Data is stored in the following way
	typedef pair < Key , T > value_type;
	//A node can be empty without *value
	class node{
		public:
		node *next, *last, *l, *r;
		//Key is key
		//T is val
		value_type *value;
		int height;
		
		//the construction of node without left and right son
		node (const Key &k, const T &v, node *la, node *ne):
		value(new pair < Key , T >(k, v)), next(ne), last(la), l(NULL), r(NULL), height(1){
		}
		
		//the distruction of node
		~node(){
			if (value != NULL) delete value;
		}
		
		//the construction of node without anything
		node(): next(NULL), last(NULL), l(NULL), r(NULL), height(1), value(NULL) {}
		
		//get the height of a node
		friend int geth(const node *p){
			if (p == NULL) return 0;
			else return (*p).height;
		}
		
		//the visit function is not for the task, Do not read this part
		//visit the node with common order
		friend void visit1(node *p){
			if (p==NULL) return ;
			visit1((*p).l);
			std::cout<<(*(*p).value).first<<" "<<(*(*p).value).second<<std::endl;
			visit1((*p).r);
		}
		
		//visit the node with dfs order
		friend void visit2(node *p){
			if (p==NULL) return ;
			std::cout<<(*(*p).value).first<<" "<<(*(*p).value).second<<std::endl;
			visit2((*p).l);
			visit2((*p).r);
		}
		
		//insert a node into a subtree
		//Key and T stands for the value_type of the new node
		friend node* insert1(const Key &k, const T &v, node *ll, node *rr, node *p){
			Compare cmp;
			if (p == NULL){
				node *ans;
				ans=new node (k, v, ll, rr);
				(*ll).next = ans;
				(*rr).last = ans;
				return ans;
			}
			if ( cmp( k , (*(*p).value).first ) ){
				(*p).l=insert1(k, v, ll, p, (*p).l);
				(*p).height = max ( geth((*p).l) , geth((*p).r) ) + 1;
				return balance3(p);
			} else if ( cmp( (*(*p).value).first , k ) ){
				(*p).r=insert1(k, v, p, rr, (*p).r);
				(*p).height = max ( geth((*p).l) , geth((*p).r) ) + 1;
				return balance3(p);
			} else {
				(*(*p).value).second = v;
				return p;
				//Notice: there may be an error
			}
		}
		
		//I want to make the subtree balanced
		//Let there be balance!!!
		friend node* balance1(node* p){
			return p;
			if (p == NULL) return NULL;
			node *f2, *ans;
			if (geth((*p).l) < geth((*p).r)) {
				ans = (*p).r;
				f2 = (*ans).l;
				(*ans).l = p;
				(*p).r = f2;
				(*p).height = max(geth((*p).l), geth((*p).r) ) +1; 
				if (geth((*ans).l) < geth((*ans).r) ){
					(*ans).r = balance1((*ans).r);
					(*ans).height = max(geth((*ans).l), geth((*ans).r)) +1;
				} else if (geth((*ans).r) <= geth((*ans).l) ){
					(*ans).l = balance1((*ans).l);
					(*ans).height = max(geth((*ans).l), geth((*ans).r)) +1;
				}
				return ans;
			} else if (geth((*p).r) < geth((*p).l)){
				ans = (*p).l;
				f2 = (*ans).r;
				(*ans).r = p;
				(*p).l = f2;
				(*p).height = max(geth((*p).l), geth((*p).r) ) +1;
				if (geth((*ans).l) < geth((*ans).r) ){
					(*ans).r = balance1((*ans).r);
					(*ans).height = max(geth((*ans).l), geth((*ans).r)) +1;
				} else if (geth((*ans).r) < geth((*ans).l) ){
					(*ans).l = balance1((*ans).l);
					(*ans).height = max(geth((*ans).l), geth((*ans).r)) +1;
				}
				return ans;
			} else{
				return p;
			}
		}
		
		friend node* balance2(node* p){
			if (p == NULL) return NULL;
			node *f2, *ans;
			if (geth((*p).l) < geth((*p).r)) {
				ans = (*p).r;
				f2 = (*ans).l;
				(*ans).l = p;
				(*p).r = f2;
				(*p).height = max(geth((*p).l), geth((*p).r) ) +1; 
				(*ans).height = max(geth((*ans).l), geth((*ans).r)) +1;
				return ans;
			} else if (geth((*p).r) < geth((*p).l)){
				ans = (*p).l;
				f2 = (*ans).r;
				(*ans).r = p;
				(*p).l = f2;
				(*p).height = max(geth((*p).l), geth((*p).r) ) +1;
				(*ans).height = max(geth((*ans).l), geth((*ans).r)) +1;
				return ans;
			} else{
				return p;
			}
		}
		friend node* balance3(node* p){
			if (p == NULL) return NULL;
			node *f2, *ans;
			if (geth((*p).l) + 1 < geth((*p).r)) {
				if (geth((*(*p).r).r) < geth((*(*p).r).l))
				(*p).r = balance2((*p).r);
				
				ans = (*p).r;
				f2 = (*ans).l;
				(*ans).l = p;
				(*p).r = f2;
				(*p).height = max(geth((*p).l), geth((*p).r) ) +1; 
				(*ans).height = max(geth((*ans).l), geth((*ans).r)) +1;
				return ans;
			} else if (geth((*p).r) + 1 < geth((*p).l)){
				if (geth((*(*p).l).r) > geth((*(*p).l).l))
				(*p).l = balance2((*p).l);

				ans = (*p).l;
				f2 = (*ans).r;
				(*ans).r = p;
				(*p).l = f2;
				(*p).height = max(geth((*p).l), geth((*p).r) ) +1;
				(*ans).height = max(geth((*ans).l), geth((*ans).r)) +1;
				return ans;
			} else{
				return p;
			}
		}

		//This is not TREAP
		//But after erasing a node, merge the subtrees
		friend node* merge1(node* p1, node* p2){
			if (p1 == NULL) return p2;
			if (p2 == NULL) return p1;
			if ( (*p1).height < (*p2).height ){
				(*p2).l = merge1(p1, (*p2).l);
				(*p2).height = max(geth((*p2).l), geth((*p2).r)) +1;
				return balance3(p2);
			} else {
				(*p1).r = merge1( (*p1).r, p2);
				(*p1).height = max(geth((*p1).l), geth((*p1).r)) +1;
				return balance3(p1);
			}
		}

		//the step1 of erasing the node p
		friend node* erase1(node* p){
			node *ans;
			//Notice: ans=merge1((node*)(NULL),(node*)(NULL) ); is possible
			ans = merge1( (*p).l, (*p).r );
			( *(*p).next).last = (*p).last;
			( *(*p).last).next = (*p).next;
			delete p;
			return balance3(ans);
		}
		
		//the step2 of erasing k in the subtree p
		friend node* erase2(node* p, const Key &k){
			Compare cmp;
			if (p == NULL) return NULL;
			if ( cmp(k, (*(*p).value).first ) ){
				(*p).l = erase2((*p).l, k);
				(*p).height = max(geth((*p).l) , geth((*p).r)) +1;
				return balance3(p);
			} else if ( cmp( (*(*p).value).first , k) ){
				(*p).r = erase2((*p).r, k);
				(*p).height = max(geth((*p).l) , geth((*p).r)) +1;
				return balance3(p);
			} else{
				return balance3(erase1(p));
			}
		}
		
		
		//find a node with key in the subtree
		friend  node* find1( node*  p,const  Key &k){
			Compare cmp;
			if (p == NULL) return NULL;
			if ( cmp(k, (*(*p).value).first ) ){
				return find1((*p).l, k);
			} else if ( cmp( (*(*p).value).first , k) ){
				return find1((*p).r, k);
			} else{
				return p;
			}
		}
		
		//find with const, totally the same , no need to read
		friend const node* find1c(const node* const p, const Key &k){
			Compare cmp;
			if (p==NULL) return NULL;
			if (cmp(k, (*(*p).value).first )){
				return find1c((*p).l, k);
			} else if (cmp((*(*p).value).first , k)){
				return find1c((*p).r, k);
			} else{
				return p;
			}
		}
		
		//to find a parent of the key
		//This part is abandoned for the low speed
		friend  node* findpar1(node* p, node* par, const Key &k){
			Compare cmp;
			if (p==NULL) return NULL;
			node *ans;
			if (cmp(k,(*(*p).value).first)){
				return findpar1((*p).l,k,p);
			} else if (cmp((*(*p).value).first,k)){
				return findpar1((*p).r,k,p);
			} else{
				return par;
			}
		}

		//Clear the map
		friend void clear1(node *p){
			if (p == NULL) return;
			clear1((*p).l);
			clear1((*p).r);
			delete p;
		}
	};
	//Data in the Map
	//Data in the Map
	//Data in the Map
	private:
	node *head, *tail, *root;//root has left son only 
	int sz;
	/**
	 * the internal type of data.
	 * it should have a default constructor, a copy constructor.
	 * You can use sjtu::map as value_type by typedef.
	 */
	//typedef pair<const Key, T> value_type;
	/**
	 * see BidirectionalIterator at CppReference for help.
	 *
	 * if there is anything wrong throw invalid_iterator.
	 *     like it = map.begin(); --it;
	 *       or it = map.end(); ++end();
	 */
	public:
	class const_iterator;
	class iterator {
		friend map;
	private:
		/**
		 * TODO add data members
		 *   just add whatever you want.
		 */
		node *root, *pos, *head, *tail;
		value_type* mypair;
		
	public:
		//Construction
		iterator():root(NULL), pos(NULL), head(NULL), tail(NULL) {
			/*Nothing*/
		}
		iterator(const iterator &other):root(other.root), pos(other.pos),
		head(other.head), tail(other.tail) {
			/*Nothing*/
		}
		~iterator(){
			/*Nothing*/
		}
		/**
		 * return a new iterator which pointer n-next elements
		 *   even if there are not enough elements, just return the answer.
		 * as well as operator-
		 */
		/**
		 * TODO iter++
		 */
		iterator operator++(int) {
			if (pos == tail){throw invalid_iterator();}
			iterator ans(*this);
			pos = (*pos).next;
			return ans;
		}
		/**
		 * TODO ++iter
		 */
		iterator & operator++() {
			if (pos == tail){throw invalid_iterator();}
			pos = (*pos).next;
			return *this;
		}
		/**
		 * TODO iter--
		 */
		iterator operator--(int) {
			if (pos == head){throw invalid_iterator();}
			iterator ans(*this);
			pos = (*pos).last;
			if (pos == head){throw invalid_iterator();}
			return ans;
		}
		/**
		 * TODO --iter
		 */
		iterator & operator--() {
			if (pos == head){throw invalid_iterator();}
			pos=(*pos).last;
			if (pos == head){throw invalid_iterator();}
			return *this;
		}
		/**
		 * a operator to check whether two iterators are same (pointing to the same memory).
		 */
		value_type & operator*() {
			return *((*pos).value);
		}
		bool operator==(const iterator &rhs) const {return pos == rhs.pos;}
		bool operator==(const const_iterator &rhs) const {return pos == rhs.pos;}
		/**
		 * some other operator for iterator.
		 */
		bool operator!=(const iterator &rhs) const {return pos != rhs.pos;}
		bool operator!=(const const_iterator &rhs) const {return pos != rhs.pos;}

		/**
		 * for the support of it->first. 
		 * See <http://kelvinh.github.io/blog/2013/11/20/overloading-of-member-access-operator-dash-greater-than-symbol-in-cpp/> for help.
		 */
		value_type* operator->() noexcept {
			return (*pos).value;
		}
	};
	class const_iterator {
		// it should has similar member method as iterator.
		//  and it should be able to construct from an iterator.
		friend map;
		private:
			// data members.
		const node *root, *pos, *head, *tail;

		public:
			const_iterator() :root(NULL), pos(NULL), head(NULL), tail(NULL){
				// TODO
			}
			const_iterator(const const_iterator &other):root(other.root), pos(other.pos),
			head(other.head), tail(other.tail) {
				// TODO
			}
			~const_iterator(){
				// NOTHING
			}
			const_iterator(const iterator &other):root(other.root), pos(other.pos),
			head(other.head), tail(other.tail) {
				// TODO
			}
			// And other methods in iterator.
			// And other methods in iterator.
			// And other methods in iterator.

		const_iterator operator++(int) {
			if (pos == tail){throw invalid_iterator();}
			const_iterator ans(*this);
			pos = (*pos).next;
			return ans;
		}
		/**
		 * TODO ++iter
		 */
		const_iterator & operator++() {
			if (pos == tail){throw invalid_iterator();}
			pos = (*pos).next;
			return *this;
		}
		/**
		 * TODO iter--
		 */
		const_iterator operator--(int) {
			if (pos == head){throw invalid_iterator();}
			const_iterator ans(*this);
			pos = (*pos).last;
			if (pos == head){throw invalid_iterator();}
			return ans;
		}
		/**
		 * TODO --iter
		 */
		const_iterator & operator--() {
			if (pos == head){throw invalid_iterator();}
			pos=(*pos).last;
			if (pos == head){throw invalid_iterator();}
			return *this;
		}
		/**
		 * a operator to check whether two iterators are same (pointing to the same memory).
		 */
		const value_type & operator*() {
			//mypair = ((*pos).value);
			return *((*pos).value);
		}
		bool operator==(const iterator &rhs) const {return pos==rhs.pos;}
		bool operator==(const const_iterator &rhs) const {return pos==rhs.pos;}
		/**
		 * some other operator for iterator.
		 */
		bool operator!=(const iterator &rhs) const {return pos!=rhs.pos;}
		bool operator!=(const const_iterator &rhs) const {return pos!=rhs.pos;}

		/**
		 * for the support of it->first. 
		 * See <http://kelvinh.github.io/blog/2013/11/20/overloading-of-member-access-operator-dash-greater-than-symbol-in-cpp/> for help.
		 */
		const value_type* operator->() noexcept {
			return (*pos).value;
		}

	};
	/**
	 * TODO two constructors
	 */
	public:
	map() {
		head = new node;
		tail = new node;
		(*head).next = tail;
		(*tail).last = head;
		root = new node;
		(*root).l = NULL;
		sz = 0;
	}
	map(const map &other) {
		head = new node;
		tail = new node;
		(*head).next = tail;
		(*tail).last = head;
		root = new node;
		node * p;
		p = (*(other.head)).next;
		
		while (p != other.tail){
			(*root).l = insert1((*(*p).value).first, (*(*p).value).second, head, tail, (*root).l);
			(*root).l = balance1((*root).l);
			(*root).l = balance1((*root).l);
			p = (*p).next;
		}
		sz = other.sz;
	}
	/**
	 * TODO assignment operator
	 */
	map & operator=(const map &other) {
		if (this == &other) return *this;
		clear1((*root).l);
		(*head).next = tail;
		(*tail).last = head;
		(*root).l = NULL;
		node * p;
		p = (*(other.head)).next;
		
		while (p != other.tail){
			(*root).l = insert1((*(*p).value).first, (*(*p).value).second, head, tail, (*root).l);
			(*root).l = balance1((*root).l);
			(*root).l = balance1((*root).l);
			p = (*p).next;
		}
		sz = other.sz;
		return *this;
	}
	/**
	 * TODO Destructors
	 */
	~map() {
		clear1(root);
		delete head;
		delete tail;
	}
	/**
	 * Finds an element with key equivalent to key.
	 * key value of the element to search for.
	 * Iterator to an element with key equivalent to key.
	 *   If no such element is found, past-the-end (see end()) iterator is returned.
	 */
	iterator find(const Key &key) {
		iterator ans;
		ans.root = root;
		ans.pos = find1((*root).l, key);
		if (ans.pos == NULL) return end();
		ans.head = head;
		ans.tail = tail;
		return ans;
	}
		/**
	 * insert an element.
	 * return a pair, the first of the pair is
	 *   the iterator to the new element (or the element that prevented the insertion), 
	 *   the second one is true if insert successfully, or false.
	 */
	pair<iterator, bool> insert( value_type value) {
		if (find1((*root).l,value.first) == NULL){
			++sz;
			(*root).l = insert1((value).first, (value).second, head, tail, (*root).l);
			(*root).l = balance1((*root).l);
			(*root).l = balance1((*root).l);
		//++sz;
		//return pair<iterator, bool>(++begin(), true);
			return pair<iterator, bool>(find((value).first), true);
		}else{
			return pair<iterator, bool>(find((value).first), false);
		}
	}

	/**
	 * TODO
	 * access specified element with bounds checking
	 * Returns a reference to the mapped value of the element with key equivalent to key.
	 * If no such element exists, an exception of type `index_out_of_bound'
	 */
	T & at(const Key &key) {
		node *p;
		p = find1((*root).l, key);
		if (p == NULL) throw index_out_of_bound();
		return (*(*p).value).second;
	}
	const T & at(const Key &key) const {
		node *p;
		p = find1((*root).l, key);
		if (p == NULL) throw index_out_of_bound();
		return (*(*p).value).second;
	}
	/**
	 * TODO
	 * access specified element 
	 * Returns a reference to the value that is mapped to a key equivalent to key,
	 *   performing an insertion if such key does not already exist.
	 */
	T & operator[](const Key &key) {
		node *p;
		p = find1((*root).l, key);
		if (p == NULL) {
			insert(pair<Key,T>(key, T()));
			p = find1((*root).l, key);
		}
		return (*(*p).value).second;
	}
	/**
	 * behave like at() throw index_out_of_bound if such key does not exist.
	 */
	const T & operator[](const Key &key) const {
		node *p;
		p = find1((*root).l, key);
		if (p == NULL) throw index_out_of_bound();
		return (*(*p).value).second;
	}
	/**
	 * return a iterator to the beginning
	 */
	iterator begin() {
		iterator ans;
		ans.root = root;
		ans.pos = (*head).next;
		ans.head = head;
		ans.tail = tail;
		return ans;
	}
	const_iterator cbegin() const {
		const_iterator ans;
		ans.root = root;
		ans.pos = (*head).next;
		ans.head = head;
		ans.tail = tail;
		return ans;
	}
	/**
	 * return a iterator to the end
	 * in fact, it returns past-the-end.
	 */
	iterator end() {
		iterator ans;
		ans.root = root;
		ans.pos = tail;
		ans.head = head;
		ans.tail = tail;
		return ans;
	}
	const_iterator cend() const {
		const_iterator ans;
		ans.root = root;
		ans.pos = tail;
		ans.head = head;
		ans.tail = tail;
		return ans;

	}
	/**
	 * checks whether the container is empty
	 * return true if empty, otherwise false.
	 */
	bool empty() const {
		return sz==0;
	}
	/**
	 * returns the number of elements.
	 */
	size_t size() const {
		return size_t(sz);
	}
	/**
	 * clears the contents
	 */
	void clear() {
		clear1((*root).l);
		(*root).l = NULL;
		sz = 0;
		(*head).next = tail;
		(*tail).last = head;
	}
	/**
	 * erase the element at pos.
	 *
	 * throw if pos pointed to a bad element (pos == this->end() || pos points an element out of this)
	 */
	void erase(iterator pos) {
		if (pos==end() || pos.root!=root) throw runtime_error();
		node *p = pos.pos;
		(*root).l = erase2((*root).l , (*(*p).value).first);
		(*root).l = balance1((*root).l);
		--sz;
	}
	/**
	 * Returns the number of elements with key 
	 *   that compares equivalent to the specified argument,
	 *   which is either 1 or 0 
	 *     since this container does not allow duplicates.
	 * The default method of check the equivalence is !(a < b || b > a)
	 */
	size_t count(const Key &key) const {
		node *p;
		p = find1((*root).l,key);
		if (p == NULL) return 0;
		else return 1;
	}
	/**
	 * Finds an element with key equivalent to key.
	 * key value of the element to search for.
	 * Iterator to an element with key equivalent to key.
	 *   If no such element is found, past-the-end (see end()) iterator is returned.
	 */
	const_iterator find(const Key &key) const {
		const_iterator ans;
		ans.root = root;
		ans.pos = find1c((*root).l, key);
		if (ans.pos == NULL) return cend();
		ans.head = head;
		ans.tail = tail;
		return ans;
	}
	void visit(){
		//visit1((*root).l);
		std::cout<<geth((*root).l)<<std::endl;
		//visit2((*root).l);
		std::cout<<geth((*root).l)<<std::endl;
		
		node *p;
		//return;
		//for (p=(*head).next;p!=tail;p=(*p).next){
			//std::cout<<(*(*p).value).first<<" "<<(*(*p).value).second<<std::endl;
		//}
		std::cout<<geth((*root).l)<<std::endl;
	}
	
};

}

#endif
