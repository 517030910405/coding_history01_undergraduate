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
lines.pop()
print(len(lines))

info1 = []
info2 = []
#print(len(lines))
dict = {}
cnt = 0
for i in range(len(lines)):
	str0 = lines[i]
	str1 = str0.split("\t",1)
	seg_list = list(jieba.cut(str1[1]))
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
lines2.pop()
fo3 = open("tr1.txt","w")
fo3.write(("\n".join(info1)))
#print(len(lines2))
for i in range(4189):
	str0 = lines2[i]
	seg_list = list(jieba.cut(str0))
	#print(str1[0]+'\t'+"/".join(seg_list))
	for seg in seg_list:
		if (dict.get(seg) == None):
			cnt = cnt +1
			dict[seg] = cnt
print(cnt)
