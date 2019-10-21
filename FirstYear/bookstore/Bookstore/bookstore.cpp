#include<cmath>
#include<iostream>
#include<fstream>
#include<sstream>
#include<string>
#include<map>
#include<algorithm>
using namespace std;
struct HashType{
	int h[3];
	bool operator==(HashType ha){
		return (h[0]==ha.h[0]&&h[1]==ha.h[1]&&h[2]==ha.h[2]);
	}
};
int PBase[3]={14999947,14999953,14999969};
const string namelist[4]={"ISBN","name","author","keyword"};
const int P1=1998817;//某一个大质数
const int P2=97;//Hashing 599 
const int P3=401;
const int Len1=32,Len2=42;
int zero=0;
double zerp=0.0;
const int UserSpace=Len1*3+sizeof(zero);
const int BookSpace=Len2*4+sizeof(zero)+sizeof(zerp);
bool tf;


stringstream transer;
fstream f_size,f_user,f_usersize,f_data,f_fina,f_rec;
fstream f_datasize,f_datasearch,f_usersearch;
int szdata,szfina,szrec,szuser;
int i_usersize;
int gethash(const string &str);
void hashread(istream &file,HashType &hash);
void hashwrite(istream &file,HashType &hash);
int StringToInteger(const string &str);
double StringToDouble(const string &str);
string IntegerToString(const int &x);
string DoubleToString(const double &x);
void init();
void adduser(const string &username, const string &password, int power, const string &realname);
int finduser(const string &username);
void stringread(istream &file,string &str);
HashType GetHash(const string &str);
void deluser(int userplace);
void changepassword(int userplace,const string &password);
void operateline(string order, string str, int &userplace , int &power , int &bookplace);
void runnerlee();
void inituser();
void runnerfile(string filename, int &userplace , int &power , int &bookplace);
void delsearch(int i, string key, int bookplace);
int findfirst(int i, string key);
void addsearch(int i, string key, int bookplace);
int select_base(string key);
void modify_base(int bookplace, int i, string key);
void modify_price(int bookplace, double price);
void inport_base(int bookplace, int addquan);
double buy(int bookplace, int minusquan);
void show_base(string st0, string st1, string st2, string st3);
void addfina(double plus, double minus);
void show_fina(int tt);
string st1;
fstream comrec,comrec2;

int main(){
/*	cin>>st1;
	cout<<DoubleToString(StringToDouble(st1)+0.5);
	return 0;*/
	//init();
	//inituser();
	f_size.open("size.lijs",fstream::in|fstream::out|fstream::binary);
	if (!f_size.is_open()){
		init();
		inituser();
		comrec.open("RecordOfCommand.txt",fstream::out | fstream::ate | fstream::binary );
		comrec.close();
		comrec.open("RecordOfCommand0.txt",fstream::out | fstream::ate | fstream::binary );
		comrec.close();
		comrec.open("RecordOfCommand1.txt",fstream::out | fstream::ate | fstream::binary );
		comrec.close();


	} else{
		f_size.close();
	}
	f_size.open("size.lijs",fstream::in|fstream::out|fstream::binary);
	f_size.seekg(0);
	f_size.read((char *)(&szdata),sizeof(szdata));
	f_size.read((char *)(&szfina),sizeof(szfina));
	f_size.read((char *)(&szrec ),sizeof(szrec ));
	f_size.read((char *)(&szuser),sizeof(szuser));
	
	comrec.open("RecordOfCommand.txt",fstream::out | fstream::ate | fstream::binary | fstream::in);
	comrec<<"Opened By root\n"<<endl;
	comrec2.open(("RecordOfCommand"+IntegerToString(1)+".txt").c_str(),
	fstream::out | fstream::ate | fstream::binary | fstream::in);
	
	//adduser("lijiasen","1234567",3,"lijiasen");
	try{
		runnerlee();
	} catch(string str){
		//cout<<str<<endl;
		
	}
/*	
	cout<<select_base("happy1")<<endl;
	cout<<select_base("happy2")<<endl;
	cout<<select_base("happy3")<<endl;
	cout<<select_base("happy1")<<endl;
	cout<<szdata<<endl;
	modify_base(1,1,"happy4|happy1");
	modify_base(2,1,"happy4|happy1");
	modify_base(3,1,"happy4|happy1");
	cout<<select_base("happy1")<<endl;
	cout<<select_base("happy4")<<endl;
	modify_base(4,1,"happy4|happy1");
	cout<<szdata<<endl;
	inport_base(3,30);
	inport_base(2,20);
	show_base("","","","");
	*/
	comrec.close();
	comrec2.close();
	f_size.seekp(0);
	f_size.write((char *)(&szdata),sizeof(szdata));
	f_size.write((char *)(&szfina),sizeof(szfina));
	f_size.write((char *)(&szrec ),sizeof(szrec ));
	f_size.write((char *)(&szuser),sizeof(szuser));
	f_size.close();
}
void inituser(){
	f_size.open("size.lijs",fstream::in|fstream::out|fstream::binary);
	f_size.seekg(0);
	f_size.read((char *)(&szdata),sizeof(szdata));
	f_size.read((char *)(&szfina),sizeof(szfina));
	f_size.read((char *)(&szrec ),sizeof(szrec ));
	f_size.read((char *)(&szuser),sizeof(szuser));
	
	adduser("root","sjtu",7,"administer");
	//cout<<"User Added: root\nPassword: sjtu\nPower: 7\nadminister"<<endl;
	
	f_size.seekp(0);
	f_size.write((char *)(&szdata),sizeof(szdata));
	f_size.write((char *)(&szfina),sizeof(szfina));
	f_size.write((char *)(&szrec ),sizeof(szrec ));
	f_size.write((char *)(&szuser),sizeof(szuser));
	f_size.close();
	
	
	
}
int gethash(const string &str){
	int ans=0;
	for (int f1=0;f1<str.length();f1++){
		ans=(ans*P3+int(str[f1])+(1<<7)+7)%P1;
	}
	return ans;
}
int StringToInteger(const string &str){
	int ans;
	transer.clear();
	transer<<str;
	transer>>ans;
	return ans;
}
double StringToDouble(const string &str){
	double ans;
	transer.clear();
	transer<<str;
	transer>>ans;
	return ans;
}
string IntegerToString(const int &x){
	string str;
	transer.clear();
	transer<<x;
	transer>>str;
	return str;
}
string DoubleToString(const double &x){
	string str;
	transer.clear();
	transer.precision(2);
	transer<<fixed<<x;
	transer>>str;
	return str;
}
void init(){
	{
		f_size.open("size.lijs",fstream::out|fstream::binary|fstream::trunc);
		szdata=0;szfina=1;szrec=0;szuser=0;
		f_size.seekp(0);
		f_size.write((char *)(&szdata),sizeof(szdata));
		f_size.write((char *)(&szfina),sizeof(szfina));
		f_size.write((char *)(&szrec ),sizeof(szrec ));
		f_size.write((char *)(&szuser),sizeof(szuser));
		f_size.close();
		f_data.open("data.lijs",fstream::out | fstream::binary |fstream::trunc);
		f_data.close();
		for (int f1=0;f1<P2;f1++){
			f_user.open(("user"+IntegerToString(f1)+".lijs").c_str(), 
			fstream::out | fstream::binary |fstream::trunc);
			f_user.close();
		}
		f_usersize.open("usersize.lijs", fstream::out | fstream::binary |fstream::trunc);
		for (int f1=0;f1<P2;f1++){
			i_usersize=0;
			f_usersize.write((char *)(&zero), sizeof(zero));
		}
		f_usersize.close();
		for (int f1=0;f1<4;f1++){
			f_datasize.open((namelist[f1]+"size.lijs").c_str(),
			fstream::out | fstream::binary |fstream::trunc);
			for (int f2=0;f2<P2;f2++){
				f_datasize.write((char *)(&zero),sizeof(zero));
				f_datasearch.open((namelist[f1]+IntegerToString(f2)+".lijs").c_str(), 
				fstream::out | fstream::binary |fstream::trunc);
				f_datasearch.close();
			}
			f_datasize.close();
		}
		f_fina.open("fina.lijs",fstream::out | fstream::binary |fstream::trunc);
		f_fina.write((char *)(&zerp), sizeof(zerp));
		f_fina.write((char *)(&zerp), sizeof(zerp));
		f_fina.close();
		f_rec.open("rec.lijs",fstream::out | fstream::binary |fstream::trunc);
		f_rec.close();
		f_user.open("user.lijs",fstream::out | fstream::binary |fstream::trunc);
		f_user.close();
	}
}
int finduser(const string &username){
	int ans;
	int hashplace=gethash(username)%P2;
	HashType hashp=GetHash(username),hashr;
	f_usersize.open("usersize.lijs",fstream::in|fstream::binary);
	f_usersize.seekg(hashplace*sizeof(zero));
	f_usersize.read((char *)(&i_usersize),sizeof(zero));
	f_usersize.close();
	f_usersearch.open(("user"+IntegerToString(hashplace)+".lijs").c_str(),
	fstream::in|fstream::binary);
	//f_usersearch.seekg(0);
	for (int f1=0;f1<i_usersize;f1++){
		f_usersearch.seekg(f1*sizeof(zero)*4);
		hashread(f_usersearch, hashr);
		if (hashr==hashp){
			f_usersearch.read((char *)(&ans),sizeof(ans));
			f_usersearch.close();
			return ans;
		}
	}
	f_usersearch.close();
	return -1;
}
void hashread(istream &file,HashType &hash){
	file.read((char *)(&hash.h[0]),sizeof(zero));
	file.read((char *)(&hash.h[1]),sizeof(zero));
	file.read((char *)(&hash.h[2]),sizeof(zero));
}
void hashwrite(ostream &file,HashType &hash){
	file.write((char *)(&hash.h[0]),sizeof(zero));
	file.write((char *)(&hash.h[1]),sizeof(zero));
	file.write((char *)(&hash.h[2]),sizeof(zero));
}
void adduser(const string &username, const string &password, int power, const string &realname){
	int hashplace=gethash(username)%P2;

	HashType hashp=GetHash(username);

	f_usersize.open("usersize.lijs",fstream::in | fstream::out | fstream::binary);
	f_usersize.seekg(hashplace*4);
	f_usersize.read((char *)(&i_usersize),sizeof(i_usersize));
	++i_usersize;
	f_usersize.seekp(hashplace*4);
	f_usersize.write((char *)(&i_usersize),sizeof(i_usersize));
	//cout<<hashplace<<" "<<i_usersize<<endl;
	f_usersize.close();
	f_usersearch.open(("user"+IntegerToString(hashplace)+".lijs").c_str(), 
	fstream::in | fstream::out | fstream::binary);
	f_usersearch.seekp((i_usersize-1)*4*sizeof(zero));
	//cout<<(i_usersize-1)*4<<endl;
	++szuser;
	f_usersearch.write((char *)(&hashp.h[0]),sizeof(zero));
	f_usersearch.write((char *)(&hashp.h[1]),sizeof(zero));
	f_usersearch.write((char *)(&hashp.h[2]),sizeof(zero));
	f_usersearch.write((char *)(&szuser),sizeof(zero));
	f_usersearch.close();
	f_user.open("user.lijs");
	f_user.seekp((szuser-1)*UserSpace);
	f_user<<username+"`";
	f_user.seekp((szuser-1)*UserSpace+Len1);
	f_user<<password+"`";
	f_user.seekp((szuser-1)*UserSpace+Len1*2);
	f_user.write((char *)(&power),sizeof(power));
	f_user.seekp((szuser-1)*UserSpace+Len1*2+4);
	f_user<<realname+"`";
	f_user.close();
}
HashType GetHash(const string &str){
	HashType ans;
	ans.h[0]=ans.h[1]=ans.h[2]=0;
	for (int i=0;i<3;i++){
		for (int f1=0;f1<str.length();f1++){
			ans.h[i]=((ans.h[i]+0ll)*P3+(1<<7)+7+str[f1])%PBase[i];
		}
	}
	return ans;
}
void stringread(istream &file,string &str){
	char ch ;
	str="";
	ch=file.get();
	while (ch!='`'){
		str=str+ch;
		ch=file.get();
	}
}
void deluser(int userplace){
	string username;
	f_user.open("user.lijs",fstream::in | fstream::out | fstream::binary);
	f_user.seekg((userplace-1)*UserSpace);
	stringread(f_user,username);
	int hashplace=gethash(username)%P2;
	HashType hashp=GetHash(username),hashr;
	f_usersize.open("usersize.lijs",fstream::in | fstream::out | fstream::binary);
	f_usersize.seekg(hashplace*sizeof(zero));
	//cout<<" "<<hashplace<<endl;
	f_usersize.read((char *)(&i_usersize), sizeof(i_usersize));
	f_usersize.close();
	f_usersearch.open(("user"+IntegerToString(hashplace)+".lijs").c_str(),
	fstream::in | fstream::out | fstream::binary);
	for (int f1=0;f1<i_usersize;f1++){
		f_usersearch.seekg(f1*sizeof(zero)*4);
		hashread(f_usersearch, hashr);
		if (hashr==hashp){
			f_usersearch.seekp(f1*sizeof(zero)*4);
			f_usersearch.write((char *)(&zero), sizeof(zero));
			f_usersearch.write((char *)(&zero), sizeof(zero));
			f_usersearch.write((char *)(&zero), sizeof(zero));
			f_usersearch.write((char *)(&zero), sizeof(zero));
			//cout<<f1<<" "<<hashplace<<endl;
		}
	}
	f_user.seekp((userplace-1)*UserSpace);
	f_user<<'`';
	f_usersearch.close();
	f_user.close();
}
void changepassword(int userplace,const string &password){
	f_user.open("user.lijs", fstream::in | fstream::out | fstream::binary);
	f_user.seekp((userplace-1)*UserSpace+Len1);
	f_user<<password<<'`';
	f_user.close();
}
void operateline(string order, string str, int &userplace , int &power , int &bookplace){
	comrec<<order<<' '<<str<<'\n'<<endl;
	comrec2<<order<<' '<<str<<'\n'<<endl;
	if (order=="su"){
		int pos=str.find(" ",0);
		if (pos!=string::npos){
			string username1=str.substr(0,pos);
			string password1=str.substr(pos+1);
			int userp=finduser(username1);
			if (userp==-1){
				cout<<"Invalid"<<endl;
				return;
			}
			//cout<<userp<<endl;
			f_user.open("user.lijs", fstream::in | fstream::binary);
			f_user.seekg((userp-1)*UserSpace+Len1);
			string password2;
			stringread(f_user, password2);
			//cout<<password1<<password2<<endl;
			if (password1.find(" ")!=string::npos){
				password1=password1.substr(0,password1.find(" "));
			}
			if (password1!=password2){
				cout<<"Invalid"<<endl;
				f_user.close();
				return;
			}
			userplace=userp;
			f_user.seekg((userp-1)*UserSpace+Len1*2);
			f_user.read((char *)(& power), sizeof(power));
			f_user.close();
			//cout<<"Power:"<<power<<endl;
			comrec2.close();
			comrec2.open(("RecordOfCommand"+IntegerToString(userplace)+".txt").c_str(),
			fstream::out | fstream::ate | fstream::binary | fstream::in);
			return;
		} else{
			string username1=str;
			int userp=finduser(username1);
			if (userp==-1){
				cout<<"Invalid"<<endl;
				return;
			}
			//cout<<userp<<endl;
			f_user.open("user.lijs", fstream::in | fstream::binary);
			f_user.seekg((userp-1)*UserSpace+Len1*2);
			int newpower;
			f_user.read((char *)(& newpower), sizeof(newpower));
			
			if (newpower>=power){
				cout<<"Invalid"<<endl;
				f_user.close();
				return;
			}
			userplace=userp;
			power=newpower;
			f_user.close();
			comrec2.close();
			comrec2.open(("RecordOfCommand"+IntegerToString(userplace)+".txt").c_str(),
			fstream::out | fstream::ate | fstream::binary | fstream::in);

			//cout<<"Power:"<<power<<endl;
			return;
		}
		
	} else if (order=="logout"){
		if (power==0){
			cout<<"Invalid"<<endl;
			return;
		}
		userplace=0;
		power=0;
		comrec2.close();
		comrec2.open(("RecordOfCommand"+IntegerToString(userplace)+".txt").c_str(),
		fstream::out | fstream::ate | fstream::binary | fstream::in);
		//cout<<"logout success"<<endl;
		return;
	} else if (order=="useradd"){
		int pos=str.find(" ");
		if (pos==string::npos){
			cout<<"Invalid"<<endl;
			return;
		}
		string username1=str.substr(0,pos);
		str=str.substr(pos+1);
		pos=str.find(" ");
		if (pos==string::npos){
			cout<<"Invalid"<<endl;
			return;
		}
		string password1=str.substr(0,pos);
		str=str.substr(pos+1);
		pos=str.find(" ");
		if (pos==string::npos){
			cout<<"Invalid"<<endl;
			return;
		}
		int power1=StringToInteger(str.substr(0,pos));
		str=str.substr(pos+1);
		pos=str.find(" ");
		if (pos!=string::npos){
			cout<<"Invalid"<<endl;
			return;
		}
		
		string realname1=str;
		//cout<<username1<<password1<<power1<<realname1;
		if (power1>=power){
			cout<<"Invalid"<<endl;
			return;
		}
		if (finduser(username1)!=-1){
			cout<<"Invalid"<<endl;
			return;
		}
		adduser(username1,password1,power1,realname1);
		fstream comrec3;
		comrec3.open(("RecordOfCommand"+IntegerToString(finduser(username1))+".txt").c_str(),
		fstream::out | fstream::ate | fstream::binary );
		comrec3.close();

	} else if (order=="register"){
		int pos=str.find(" ");
		if (pos==string::npos){
			cout<<"Invalid"<<endl;
			return;
		}
		string username1=str.substr(0,pos);
		str=str.substr(pos+1);
		pos=str.find(" ");
		if (pos==string::npos){
			cout<<"Invalid"<<endl;
			return;
		}
		string password1=str.substr(0,pos);
		str=str.substr(pos+1);
		string realname1=str;
		if (finduser(username1)!=-1){
			cout<<"Invalid"<<endl;
			return;
		}
		pos=str.find(" ");
		if (pos!=string::npos){
			cout<<"Invalid"<<endl;
			return;
		}
		//cout<<'('+password1+')'<<endl;
		int power1=1;
		adduser(username1,password1,power1,realname1);
		fstream comrec3;
		comrec3.open(("RecordOfCommand"+IntegerToString(finduser(username1))+".txt").c_str(),
		fstream::out | fstream::ate | fstream::binary );
		comrec3.close();

		return;
	} else if (order=="delete"){
		if (power!=7||str=="root"){
			cout<<"Invalid"<<endl;
			return;
		}
		int pos=finduser(str);
		if (pos==-1){
			cout<<"Invalid"<<endl;
			return;
		}
		deluser(pos);
		return;
	} else if (order=="passwd"){
		int pos=str.find(" ");
		string username1=str.substr(0,pos);
		str=str.substr(pos+1);
		int userplace=finduser(username1);
		if (userplace==-1){
			cout<<"Invalid"<<endl;
			return;
		}
		pos=str.find(" ");
		if (pos==string::npos){
			if (power!=7){
				cout<<"Invalid"<<endl;
				return;
			}
			changepassword(userplace,str);
		} else{
			string password1=str.substr(0,pos);
			string password2=str.substr(pos+1);
			string password3;
			f_user.open("user.lijs",fstream::in | fstream::binary | fstream::out);
			f_user.seekg((userplace-1)*UserSpace+Len1);
			stringread(f_user,password3);
			if (password3==password1){
				f_user.seekp((userplace-1)*UserSpace+Len1);
				f_user<<password2+'`';
			} else{
				cout<<"Invalid"<<endl;
			}
			f_user.close();
			return;
		}
	} else if (order=="select"){
		bookplace=select_base(str);
		if (bookplace==-1){
			cout<<"Invalid"<<endl;
			bookplace=0;
		}
		return;
	} else if (order=="modify"){
		if (bookplace==0){
			cout<<"Invalid"<<endl;
			return;
		}
		str=str+" -";
		int pos1=str.find("-");
		int pos2,pos3;
		string st1,st2;
		int i;
		while (pos1!=str.length()-1){
			str=str.substr(pos1+1);
			pos2=str.find("=");
			st1=str.substr(0,pos2);
			str=str.substr(pos2+1);

			pos3=str.find("-");
			st2=str.substr(0,pos3-1);
			str=str.substr(pos3);
			if (st2[0]=='\"'){
				st2=st2.substr(1,st2.length()-2);
			}

			for (i=0;i<4;i++){
				if (namelist[i]==st1){
					if (i==0&&findfirst(0,st2)!=-1){
						cout<<"Invalid"<<endl;
					}else{
						modify_base(bookplace, i, st2);
					}
				}
			}
			if (st1=="price"){
				modify_price(bookplace, StringToDouble(st2));
			}
			pos1=str.find("-");
		}
		return;
	} else if (order=="import"){
		if (bookplace==0){
			cout<<"Invalid"<<endl;
			return;
		}
		int pos=str.find(" ");
		string st1=str.substr(0,pos);
		string st2=str.substr(pos+1);
		
		addfina(0,StringToDouble(st2));
		
		inport_base(bookplace,StringToInteger(st1));
		
		return;
	} else if (order=="buy"){
		int pos=str.find(" ");
		string st1=str.substr(0,pos);
		string st2=str.substr(pos+1);
		
		int bookplace2=findfirst(0,st1);
		if (bookplace2==-1){
			cout<<"Invalid"<<endl;
			return;
		}
		double cost;
		try{
			cost=buy(bookplace2,StringToInteger(st2));
		} catch(string st){
			cout<<"Invalid"<<endl;
			return;
		}
		addfina(cost,0);
		return;
	} else if (order=="show"&&str[0]!='f'){
		if (power==0){
			cout<<"Invalid"<<endl;
			return;
		}
		string stst[4]={"","","",""};
		str=str+" -";
		int pos1=str.find("-");
		int pos2,pos3;
		string st1,st2;
		int i;
		while (pos1!=str.length()-1){
			str=str.substr(pos1+1);
			pos2=str.find("=");
			st1=str.substr(0,pos2);
			str=str.substr(pos2+1);

			pos3=str.find("-");
			st2=str.substr(0,pos3-1);
			str=str.substr(pos3);
			if (st2[0]=='\"'){
				st2=st2.substr(1,st2.length()-2);
			}

			for (i=0;i<4;i++){
				if (namelist[i]==st1){
					stst[i]=st2;
				}
			}
			pos1=str.find("-");
		}
		show_base(stst[0],stst[1],stst[2],stst[3]);

	} else if ((order=="show"&&str[0]=='f')||(order=="log")){
		if (power!=7){
			cout<<"Invalid"<<endl;
			return;
		}
		int pos=str.find(" ");
		if (pos==string::npos){
			show_fina((1<<29));
		} else{
			string st2=str.substr(pos+1);
			show_fina(StringToInteger(st2));
		}
	} else if (order=="report"){
		if (power==7)cout<<"...Inside RecordOfCommand.txt"<<endl;
		cout<<"...Inside RecordOfCommand"<<userplace<<".txt"<<endl;
	} else if (order=="help"){
		cout<<"help.txt"<<endl;
	} else if (order=="exit"){
		throw(order);
	} else {
		cout<<"Invalid"<<endl;
		return;
	}
}
void runnerfile(string filename, int &userplace , int &power , int &bookplace){
	fstream file;
	file.open(filename.c_str(),  fstream::in | fstream::binary);
	string order,str;
	if (file.is_open()){
		while (true){
			if(file>>order){
				getline(file,str);
				if (str.length()>1)str=str.substr(1,str.length()-2);
				//cout<<"("<<order<<")["<<str<<"]"<<endl;
				operateline(order, str, userplace, power, bookplace);
			} else return;
		}
	} else{
		//cout<<"Cannot find File:"<<filename<<endl;
		return;
	}
	file.close();
}
void runnerlee(){
	string comm;//command;
	int userplace=1,power=7,bookplace=0;
	string order,str;
	runnerfile("command.txt",userplace, power, bookplace);
	while (true){
		if(cin>>order){
			getline(cin,str);
			if (str.length()>0)str=str.substr(1,str.length()-1);
			//cout<<"("<<order<<")["<<str<<"]"<<endl;
			if (order=="load"){
				runnerfile(str,userplace, power, bookplace);
			} else{
				operateline(order, str, userplace, power, bookplace);
			}
		} else return;
	}
	
	
}
void addsearch(int i, string key, int bookplace){
	int hashplace=gethash(key)%P2;
	//cout<<hashplace<<endl;
	HashType hashp=GetHash(key);
	f_datasize.open((namelist[i]+"size.lijs").c_str(),
	fstream::in | fstream::out | fstream::binary);
	int i_searchsize;
	f_datasize.seekg(hashplace*sizeof(zero));
	f_datasize.read((char *)(&i_searchsize), sizeof(zero));
	++i_searchsize;
	f_datasize.seekp(hashplace*sizeof(zero));
	f_datasize.write((char *)(&i_searchsize), sizeof(zero));
	f_datasize.close();
	f_datasearch.open((namelist[i]+IntegerToString(hashplace)+".lijs").c_str(),
	fstream::in | fstream::out | fstream::binary);
	f_datasearch.seekp((i_searchsize-1)*sizeof(zero)*4);
	f_datasearch.write((char *)(&hashp.h[0]),sizeof(zero));
	f_datasearch.write((char *)(&hashp.h[1]),sizeof(zero));
	f_datasearch.write((char *)(&hashp.h[2]),sizeof(zero));
	f_datasearch.write((char *)(&bookplace),sizeof(zero));
	f_datasearch.close();
}
int findfirst(int i, string key){
	int hashplace=gethash(key)%P2;
	//cout<<hashplace<<endl;
	HashType hashp=GetHash(key);
	f_datasize.open((namelist[i]+"size.lijs").c_str(),
	fstream::in | fstream::out | fstream::binary);
	int i_searchsize;
	f_datasize.seekg(hashplace*sizeof(zero));
	f_datasize.read((char *)(&i_searchsize), sizeof(zero));
	f_datasize.close();
	f_datasearch.open((namelist[i]+IntegerToString(hashplace)+".lijs").c_str(),
	fstream::in | fstream::out | fstream::binary);
	HashType hashr;
	for (int f1=0;f1<i_searchsize;f1++){
		f_datasearch.seekg(f1*sizeof(zero)*4);
		hashread(f_datasearch, hashr);
		//cout<<hashr.h[0]<<hashp.h[0];
		
		if (hashr==hashp){
			int ans;
			f_datasearch.read((char *)(&ans),sizeof(zero));
			f_datasearch.close();
			return ans;
		}
	}
	f_datasearch.close();
	return -1;
}
void delsearch(int i, string key, int bookplace){
	int hashplace=gethash(key)%P2;
	HashType hashp=GetHash(key);
	f_datasize.open((namelist[i]+"size.lijs").c_str(),
	fstream::in | fstream::out | fstream::binary);
	int i_searchsize;
	f_datasize.seekg(hashplace*sizeof(zero));
	f_datasize.read((char *)(&i_searchsize), sizeof(zero));
	f_datasize.close();
	f_datasearch.open((namelist[i]+IntegerToString(hashplace)+".lijs").c_str(),
	fstream::in | fstream::out | fstream::binary);
	HashType hashr;
	int bookplace2;
	for (int f1=0;f1<i_searchsize;f1++){
		f_datasearch.seekg(f1*sizeof(zero)*4);
		hashread(f_datasearch, hashr);
		f_datasearch.read((char *)(&bookplace2), sizeof(zero));
		if (hashr==hashp && bookplace==bookplace2){
			f_datasearch.seekp(f1*sizeof(zero)*4);
			f_datasearch.write((char *)(&zero), sizeof(zero));
			f_datasearch.write((char *)(&zero), sizeof(zero));
			f_datasearch.write((char *)(&zero), sizeof(zero));
			f_datasearch.write((char *)(&zero), sizeof(zero));
		}
	}
	f_datasearch.close();
}
int select_base(string key){
	int bookplace=findfirst(0,key);
	if (bookplace==-1){
		++szdata;
		f_data.open("data.lijs", fstream::in | fstream::out | fstream::binary);
		f_data.seekp((szdata-1)*BookSpace);
		f_data<<key<<'`';
		f_data.seekp((szdata-1)*BookSpace+Len2);
		f_data<<'`';
		f_data.seekp((szdata-1)*BookSpace+Len2*2);
		f_data<<'`';
		f_data.seekp((szdata-1)*BookSpace+Len2*3);
		f_data<<'`';
		f_data.seekp((szdata-1)*BookSpace+Len2*4);
		f_data.write((char *)(&zerp), sizeof(zerp));
		f_data.write((char *)(&zero), sizeof(zero));
		f_data.close();
		addsearch(0, key, szdata);
		return szdata;
	} else{
		return bookplace;
	}
}
void modify_base(int bookplace, int i, string key){
	f_data.open("data.lijs", fstream::in | fstream::out | fstream::binary);
	
	f_data.seekg((bookplace-1)*BookSpace+i*Len2);
	string key0;
	stringread(f_data, key0);
	
	f_data.seekp((bookplace-1)*BookSpace+i*Len2);
	f_data<<key<<'`';
	
	f_data.close();
	//cout<<"("<<key0<<")["<<key<<"]"<<endl;
	//delsearch(i, key0, bookplace);
	//addsearch(i, key , bookplace);
	string st1;
	int pos;
	pos=key0.find("|");
	while (pos!=string::npos){
		st1=key0.substr(0,pos);
		key0=key0.substr(pos+1);
		delsearch(i, st1, bookplace);
		pos=key0.find("|");
	}
	if (key0!="") delsearch(i, key0, bookplace);
	
	pos=key.find("|");
	while (pos!=string::npos){
		st1=key.substr(0,pos);
		key=key.substr(pos+1);
		addsearch(i, st1, bookplace);
		pos=key.find("|");
	}
	if (key!="") addsearch(i, key, bookplace);
}
void modify_price(int bookplace, double price){
	f_data.open("data.lijs", fstream::in | fstream::out | fstream::binary);
	f_data.seekp((bookplace-1)*BookSpace+4*Len2);
	f_data.write((char *)(& price), sizeof(zerp));
	f_data.close();
}
void inport_base(int bookplace, int addquan){
	double price;int quan;
	f_data.open("data.lijs", fstream::in | fstream::out | fstream::binary);
	
	f_data.seekg((bookplace-1)*BookSpace+4*Len2+sizeof(zerp));
	f_data.read((char *)(& quan ), sizeof(zero));
	
	quan+=addquan;
	//cout<<quan<<endl;
	
	f_data.seekp((bookplace-1)*BookSpace+4*Len2+sizeof(zerp));
	f_data.write((char *)(& quan ), sizeof(zero));
	
	f_data.close();
	
}
double buy(int bookplace, int minusquan){
	double ans;int quan;
	f_data.open("data.lijs", fstream::in | fstream::out | fstream::binary);
	
	f_data.seekg((bookplace-1)*BookSpace+4*Len2);
	f_data.read((char *)(& ans ), sizeof(zerp));
	f_data.read((char *)(& quan), sizeof(zero));
	
	if (quan<minusquan){
		f_data.close();
		throw(string("Quantity Not Enough"));
		return 0;
	}
	
	ans*=minusquan;
	quan-=minusquan;
	
	f_data.seekp((bookplace-1)*BookSpace+4*Len2+sizeof(zerp));
	f_data.write((char *)(& quan), sizeof(zero));
	
	f_data.close();
	return ans;
}
void show_base(string st0, string st1, string st2, string st3){
	map<string , pair <int,int> > mp1;
	map<string , pair <int,int> > ::iterator it1;
	int cnt=0;
	if (st0!=""){
		++cnt;
		int hashplace=gethash(st0)%P2;
		HashType hashp=GetHash(st0),hashr;
		int i_searchsize;
		
		f_datasize.open((namelist[0]+"size.lijs").c_str(), 
		fstream::in | fstream::out | fstream::binary);
		
		f_datasize.seekg(hashplace*sizeof(zero));
		f_datasize.read((char *)(&i_searchsize),sizeof(zero));
		
		f_datasize.close();
		
		f_datasearch.open((namelist[0]+IntegerToString(hashplace)+".lijs").c_str(),
		fstream::in | fstream::out | fstream::binary);
		
		f_data.open("data.lijs", fstream::in | fstream::out | fstream::binary);
		
		for (int f1=0;f1<i_searchsize;f1++){
			f_datasearch.seekg(f1*sizeof(zero)*4);
			hashread(f_datasearch, hashr);
			
			if (hashr==hashp){
				int bookplace;
				string key;
				f_datasearch.read((char *)(&bookplace), sizeof(zero));
				
				f_data.seekg((bookplace-1)*BookSpace);
				stringread(f_data, key);
				
				if (mp1.find(key)==mp1.end()){
					mp1[key]=make_pair(bookplace,0);
				} 
				mp1[key].second++;
			}
			
		}
		
		f_data.close();
		f_datasearch.close();
	}
	if (st1!=""){
		++cnt;
		int hashplace=gethash(st1)%P2;
		HashType hashp=GetHash(st1),hashr;
		int i_searchsize;
		
		f_datasize.open((namelist[1]+"size.lijs").c_str(), 
		fstream::in | fstream::out | fstream::binary);
		
		f_datasize.seekg(hashplace*sizeof(zero));
		f_datasize.read((char *)(&i_searchsize),sizeof(zero));
		
		f_datasize.close();
		
		f_datasearch.open((namelist[1]+IntegerToString(hashplace)+".lijs").c_str(),
		fstream::in | fstream::out | fstream::binary);
		
		f_data.open("data.lijs", fstream::in | fstream::out | fstream::binary);
		
		for (int f1=0;f1<i_searchsize;f1++){
			f_datasearch.seekg(f1*sizeof(zero)*4);
			hashread(f_datasearch, hashr);
			
			if (hashr==hashp){
				int bookplace;
				string key;
				f_datasearch.read((char *)(&bookplace), sizeof(zero));
				
				f_data.seekg((bookplace-1)*BookSpace);
				stringread(f_data, key);
				
				if (mp1.find(key)==mp1.end()){
					mp1[key]=make_pair(bookplace,0);
				} 
				mp1[key].second++;
			}
			
		}
		
		f_data.close();
		f_datasearch.close();
	}
	if (st2!=""){
		++cnt;
		int hashplace=gethash(st2)%P2;
		HashType hashp=GetHash(st2),hashr;
		int i_searchsize;
		
		f_datasize.open((namelist[2]+"size.lijs").c_str(), 
		fstream::in | fstream::out | fstream::binary);
		
		f_datasize.seekg(hashplace*sizeof(zero));
		f_datasize.read((char *)(&i_searchsize),sizeof(zero));
		
		f_datasize.close();
		
		f_datasearch.open((namelist[2]+IntegerToString(hashplace)+".lijs").c_str(),
		fstream::in | fstream::out | fstream::binary);
		
		f_data.open("data.lijs", fstream::in | fstream::out | fstream::binary);
		
		for (int f1=0;f1<i_searchsize;f1++){
			f_datasearch.seekg(f1*sizeof(zero)*4);
			hashread(f_datasearch, hashr);
			
			if (hashr==hashp){
				int bookplace;
				string key;
				f_datasearch.read((char *)(&bookplace), sizeof(zero));
				
				f_data.seekg((bookplace-1)*BookSpace);
				stringread(f_data, key);
				
				if (mp1.find(key)==mp1.end()){
					mp1[key]=make_pair(bookplace,0);
				} 
				mp1[key].second++;
			}
			
		}
		
		f_data.close();
		f_datasearch.close();
	}
	if (st3!=""){
		++cnt;
		int hashplace=gethash(st3)%P2;
		HashType hashp=GetHash(st3),hashr;
		int i_searchsize;
		
		f_datasize.open((namelist[3]+"size.lijs").c_str(), 
		fstream::in | fstream::out | fstream::binary);
		
		f_datasize.seekg(hashplace*sizeof(zero));
		f_datasize.read((char *)(&i_searchsize),sizeof(zero));
		
		f_datasize.close();
		
		f_datasearch.open((namelist[3]+IntegerToString(hashplace)+".lijs").c_str(),
		fstream::in | fstream::out | fstream::binary);
		
		f_data.open("data.lijs", fstream::in | fstream::out | fstream::binary);
		
		for (int f1=0;f1<i_searchsize;f1++){
			f_datasearch.seekg(f1*sizeof(zero)*4);
			hashread(f_datasearch, hashr);
			
			if (hashr==hashp){
				int bookplace;
				string key;
				f_datasearch.read((char *)(&bookplace), sizeof(zero));
				
				f_data.seekg((bookplace-1)*BookSpace);
				stringread(f_data, key);
				
				if (mp1.find(key)==mp1.end()){
					mp1[key]=make_pair(bookplace,0);
				} 
				mp1[key].second++;
			}
			
		}
		
		f_data.close();
		f_datasearch.close();
	}

	f_data.open("data.lijs", fstream::in | fstream::out | fstream::binary);
	//cout<<szdata<<endl;
	if (cnt==0){
		for (int f1=0;f1<szdata;f1++){
			f_data.seekg(f1*BookSpace);
			string key;
			stringread(f_data, key);
			//cout<<key<<endl;
			mp1[key]=make_pair(f1+1,0);
		}
	}

	for (it1=mp1.begin();it1!=mp1.end();it1++){
		if ((*it1).second.second==cnt){
			int bookplace=(*it1).second.first;
			string st1; double price; int quan;
			
			f_data.seekg((bookplace-1)*BookSpace);
			stringread(f_data, st1);
			cout<<st1<<'\t';
			
			f_data.seekg((bookplace-1)*BookSpace+Len2);
			stringread(f_data, st1);
			cout<<st1<<'\t';
			
			f_data.seekg((bookplace-1)*BookSpace+Len2*2);
			stringread(f_data, st1);
			cout<<st1<<'\t';
			
			f_data.seekg((bookplace-1)*BookSpace+Len2*3);
			stringread(f_data, st1);
			cout<<st1<<'\t';
			
			f_data.seekg((bookplace-1)*BookSpace+Len2*4);
			f_data.read((char *)(&price), sizeof(zerp));
			f_data.read((char *)(&quan ), sizeof(zero));
			
			cout<<DoubleToString(price)<<'\t'<<quan<<"本"<<endl;
		}
		
	}
	
	f_data.close();
}
void addfina(double plus, double minus){
	++szfina;
	double pp,mm;
	f_fina.open("fina.lijs", fstream::in | fstream::out | fstream::binary);
	
	f_fina.seekg((szfina-2)*sizeof(zerp)*2);
	f_fina.read((char *)(&pp), sizeof(zerp));
	f_fina.read((char *)(&mm), sizeof(zerp));
	
	pp+=plus;
	mm+=minus;
	
	f_fina.seekp((szfina-1)*sizeof(zerp)*2);
	f_fina.write((char *)(&pp), sizeof(zerp));
	f_fina.write((char *)(&mm), sizeof(zerp));
	
	f_fina.close();
}
void show_fina(int tt){
	int fr=max(1,szfina-tt);
	double p1,m1,p2,m2;
	f_fina.open("fina.lijs", fstream::in | fstream::out | fstream::binary);
	
	f_fina.seekg((fr-1)*sizeof(zerp)*2);
	f_fina.read((char *)(&p1), sizeof(zerp));
	f_fina.read((char *)(&m1), sizeof(zerp));
	
	f_fina.seekg((szfina-1)*sizeof(zerp)*2);
	f_fina.read((char *)(&p2), sizeof(zerp));
	f_fina.read((char *)(&m2), sizeof(zerp));
	
	cout<<"+ "<<DoubleToString(p2-p1)<<" - "<<DoubleToString(m2-m1)<<endl;
	
	f_fina.close();
}





