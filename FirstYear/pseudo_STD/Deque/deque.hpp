#ifndef randmap
#define randmap
#include<iostream>

inline int Rand1(){
	static int x = 0;
	static int y = 0;
	static int z = 0;
	static int w = 0;
	x = (x*3 +10457)%20011;
	y = (y*7 +5011)%20089;
	z = (z*5 +313)%20113;
	w = (w*11 +1031)%30097;
	return (x^y^z^w)&((1<<15)-1);
}
inline int Rand(){
	return (Rand1()<<14)^Rand1();
}
template <class T>
T max(const T &a, const T &b){
	return (a < b)? b:a;
}


template <class T1, class T2>
struct Pair{
	T1 first;
	T2 second;
	Pair(const T1 &v1, const T2 &v2):first(v1), second(v2){}
	Pair(){}
};


template <class KEY, class OTHER>
class Node{
	Pair <KEY, OTHER> data;
	Node *l, *r;
	int size, height, randnum;
	inline int friend getsize(Node *p){
		if (p == NULL) return 0;
		else return p -> size;
	}
	inline int friend geth(Node *p){
		if (p == NULL) return 0;
		else return p -> height;
	}
	inline void friend keep(Node *p1){
		p1 -> size = getsize(p1 -> l) + getsize(p1 -> r) +1;
		p1 -> height = max( geth(p1 -> l), geth(p1 -> r) ) +1;
	}
	public:
	Node():l(NULL), r(NULL), randnum(Rand()), size(1), height(1){
		
	}
	Node(const KEY &key, const OTHER &other):l(NULL), r(NULL), randnum(Rand()),
	size(1), height(1), data(Pair<KEY, OTHER>(key, other)){
		
	}
	~Node (){
		
	}
	
	friend Node* merge(Node *p1, Node *p2){
		if (p1 == NULL) return p2;
		if (p2 == NULL) return p1;
		if (p1 -> randnum >= p2 -> randnum){
			p1 -> r = merge(p1 -> r, p2);
			keep(p1);
			return p1;
		} else{
			p2 -> l = merge(p1, p2 -> l);
			keep(p2);
			return p2;
		}
	}
	
	friend Pair<Node* , Node*> split(Node *p, int cnt){
		Pair<Node* , Node*> ans;
		if (cnt == getsize( p ) ) return Pair<Node* , Node*>(p, NULL);
		if (cnt == 0) return Pair<Node* , Node*>(NULL, p);
		if (cnt == getsize(p -> l)){
			ans.first = p -> l ;
			ans.second = p;
			p -> l = NULL;
			keep(p);
			return ans;
		}
		if (cnt == getsize(p -> l) +1){
			ans.first = p;
			ans.second = p -> r;
			p -> r = NULL;
			keep(p);
			return ans;
		}
		if (cnt <= getsize(p -> l)){
			ans = split(p -> l, cnt);
			p -> l = ans.second;
			keep(p);
			ans.second = p;
			return ans;
		} else{
			ans = split(p -> r, cnt - getsize(p -> l) -1);
			p -> r = ans.first;
			keep(p);
			ans.first = p;
			return ans;
		}
	}
	
	friend Node* find1(Node* p, const KEY &key){
		if (p == NULL) return NULL;
		if (key < p -> data . first) return find1(p -> l, key);
		else if (p -> data . first < key) return find1(p -> r, key);
		else return p;
	}
	
	friend int findpos(Node* p, const KEY &key){
		if (p == NULL) return 0;
		if (key < p -> data . first) return findpos(p -> l, key);
		else if (p -> data . first < key) return findpos(p -> r, key) + getsize(p -> l) +1;
		else return getsize(p -> l) +1;
	}
	
	friend void clear(Node* p){
		if (p == NULL) return;
		clear( p-> l);
		clear( p-> r);
		delete p;
	}
	friend void view1(Node *p){
		if (p == NULL) return;
		view1(p -> l);
		std::cout<<p -> data . first<<' ';
		view1(p -> r);
	}
};
template <class KEY, class OTHER>
class Map{
	Node<KEY, OTHER>* root;
	public:
	Map():root(NULL){
		
	}
	~Map(){
		clear(root);
	}
	void insert(const Pair<KEY, OTHER> &data){
		Node<KEY, OTHER> *newp, *p1;
		Pair<Node<KEY, OTHER>*, Node<KEY, OTHER>*> res;
		int cnt;
		
		p1 = find1(root, data.first);
		if (p1 != NULL) return;
		
		newp = new Node<KEY, OTHER>(data.first, data.second);
		cnt = findpos(root, data.first);
		res = split(root, cnt);
		
		newp = merge(res.first, newp);
		newp = merge(newp, res.second);
		root = newp;
	}
	void erase(const KEY &key){
		Node<KEY, OTHER> *p1;
		Pair<Node<KEY, OTHER>*, Node<KEY, OTHER>*> res, res2;
		int cnt;
		
		p1 = find1(root, key);
		if (p1 == NULL) return;
		
		cnt = findpos(root, key) -1;
		res = split(root, cnt);
		res2 = split(res.second, 1);
		
		clear(res2.first);
		root = merge(res.first, res2.second);
	}
	Node<KEY, OTHER>* find(const KEY &key){
		return find1(root, key);
	}
	void view(){
		//view1(root);
		std::cout<<'\n'<<geth(root)<<' ';
		std::cout<<getsize(root)<<'\n';
	}
	
};


#endif
