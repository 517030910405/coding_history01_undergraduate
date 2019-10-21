#coding=utf-8
import jieba

jieba.add_word("不喜欢", freq=None, tag=None)
jieba.add_word("抹茶", freq=None, tag=None)
jieba.add_word("奶香", freq=None, tag=None)
jieba.add_word("味道", freq=None, tag=None)
jieba.add_word("才有", freq=None, tag=None)
jieba.add_word("吃完", freq=None, tag=None)
str0 = ""
fo = open("train_shuffle.txt", "r")
lines = (fo.read()).split("\n")
info1 = []
info2 = []
#print(len(lines))
dict = {}
cnt = 0
for i in range(16000):
	str0 = lines[i]
	str1 = str0.split("\t",1)
	seg_list = list(jieba.cut_for_search(str1[1]))
	#print(str1[0]+'\t'+"/".join(seg_list))
	info_list = []
	for seg in seg_list:
		if (dict.get(seg) == None):
			cnt = cnt +1
			dict[seg] = cnt
		info_list.append(str(dict[seg]))
	info1.append(str1[0]+"\t"+"/".join(info_list))
#这个是用来看train的东西
#print("\n".join(info1))
print(cnt)	
fo2 = open("test_handout.txt","r")	
lines2 = (fo2.read()).split("\n")
#print(len(lines2))
for i in range(4189):
	info_list = []
	str0 = lines2[i]
	seg_list = list(jieba.cut_for_search(str0))
	#print(str1[0]+'\t'+"/".join(seg_list))
	for seg in seg_list:
		if (dict.get(seg) == None):
			cnt = cnt +1
			dict[seg] = cnt
		info_list.append(str(dict[seg]))
	info2.append("/".join(info_list))
	
print(cnt)
print("\n".join(info2))
	


'''
seg_list = jieba.cut("没想到环境不太好，脏兮兮的，再也不去了")
print("Default Mode: " + "/".join(seg_list))  # 精确模式 
seg_list = jieba.cut("我来到北京清华大学", cut_all=True)
print("Full Mode: " + "/ ".join(seg_list))  # 全模式
 
seg_list = jieba.cut("我来到北京清华大学", cut_all=False)
print("Default Mode: " + "/ ".join(seg_list))  # 精确模式
 
seg_list = jieba.cut("他来到了网易杭研大厦")  # 默认是精确模式
print("/".join(seg_list))
 
seg_list = jieba.cut_for_search("小明硕士毕业于中国科学院计算所，不喜欢日本京都大学")  # 搜索引擎模式
seg_list = jieba.cut("小明硕士毕业于中国科学院计算所，不喜欢日本京都大学")  # 搜索引擎模式
print("/".join(seg_list))
'''