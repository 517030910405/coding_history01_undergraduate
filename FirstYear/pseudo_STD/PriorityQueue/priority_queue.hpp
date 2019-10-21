#ifndef SJTU_PRIORITY_QUEUE_HPP
#define SJTU_PRIORITY_QUEUE_HPP

#include <cstddef>
#include <functional>
#include "exceptions.hpp"
#include <iostream>
//http://106.15.192.159/KitJudge/
namespace sjtu {

/**
 * a container like std::priority_queue which is a heap internal.
 * it should be based on the vector written by yourself.
 */
template<typename T>
void swap(T &a, T &b){
	T c=a;
	a=b;
	b=c;
}
 
 
template<typename T, class Compare = std::less<T> >
class priority_queue {
	friend class priority_queue<T>;
private:
	class nodeljs{
		public:
		nodeljs *l, *r;
		int sz;
		T val;
		nodeljs(const T &init) : l (NULL), r(NULL), sz(1), val(init){
			
		}

		~nodeljs (){
			//No Action is Taken
		}
		friend bool szcmp(const nodeljs *n1, const nodeljs *n2){
			return getsz(n1) < getsz(n2);
		}
		friend int getsz(const nodeljs *n1){
			if (n1 == NULL) return 0;
			return (*n1).sz;
		}
		friend nodeljs* merge1(nodeljs *n1, nodeljs *n2){
			if (n1 == NULL) swap(n1,n2);
			if (n2 == NULL) return n1;
			Compare valcmp;
			if (valcmp( (*n1).val , (*n2).val ) ) swap(n1,n2);
			nodeljs *m1, *m2, *m3;
			m1 = (*n1).l;
			m2 = (*n1).r;
			m3 = n2;
			if (szcmp(m1, m2)) swap(m1, m2);
			if (szcmp(m2, m3)) swap(m2, m3);
			if (szcmp(m1, m2)) swap(m1, m2);
			m2 = merge1(m2, m3);
			if (szcmp(m1, m2)) swap(m1, m2);
			(*n1).l = m1;
			(*n1).r = m2;
			(*n1).sz = getsz(m1) + getsz(m2) + 1;
			return n1;
		}
		friend nodeljs* pop1(nodeljs *n1){
			nodeljs *m1, *m2;
			m1 = (*n1).l;
			m2 = (*n1).r;
			delete n1;
			return merge1(m1, m2);
		}
		friend nodeljs* push1(nodeljs *n1, const T &x){
			nodeljs *m1;
			m1 = new nodeljs(x);
			return merge1(n1, m1);
		}
		friend void clear1(nodeljs *n1){
			if ((*n1).l != NULL) clear1((*n1).l);
			if ((*n1).r != NULL) clear1((*n1).r);
			delete n1;
		}
		friend nodeljs* copy1(const nodeljs *n1){
			if (n1 == NULL) return NULL;
			nodeljs *m1;
			m1 = new nodeljs((*n1).val);
			(*m1).sz = (*n1).sz;
			(*m1).l = copy1((*n1).l);
			(*m1).r = copy1((*n1).r);
			return m1;
		}
	};
public:
	nodeljs *head;
	
public:
	/**
	 * TODO constructors
	 */
	priority_queue() {head = NULL;}
	priority_queue(const priority_queue &other) {
		head = copy1(other.head);
	}
	/**
	 * TODO deconstructor
	 */
	~priority_queue() {
		if (getsz(head) > 0) clear1(head);
	}
	/**
	 * TODO Assignment operator
	 */
	priority_queue &operator=(const priority_queue &other) {
		if (this == &other) return *this;
		if (head != NULL) clear1(head);
		head = copy1(other.head);
		return *this;
	}
	/**
	 * get the top of the queue.
	 * @return a reference of the top element.
	 * throw container_is_empty if empty() returns true;
	 */
	const T & top() const {
		if (getsz(head) == 0) throw container_is_empty();
		return (*head).val;
	}
	/**
	 * TODO
	 * push new element to the priority queue.
	 */
	void push(const T &e) {
		head = push1(head, e);
	}
	/**
	 * TODO
	 * delete the top element.
	 * throw container_is_empty if empty() returns true;
	 */
	void pop() {
		if (getsz(head) == 0) throw container_is_empty();
		head = pop1(head);
	}
	/**
	 * return the number of the elements.
	 */
	size_t size() const {
		return getsz(head);
	}
	/**
	 * check if the container has at least an element.
	 * @return true if it is empty, false if it has at least an element.
	 */
	bool empty() const {
		return getsz(head) == 0;
	}
	/**
	 * return a merged priority_queue with at least O(logn) complexity.
	 */
	void merge(priority_queue &other) {
		head = merge1(head, other.head);
		other.head = NULL;
	}
};

}

#endif
