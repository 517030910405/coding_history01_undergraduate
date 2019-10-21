import random
import numpy as np

print("Li Jiasen Cooperation")
class batch_manager:
	def __init__(self,is_random = True):
		self.f1 = open("train_batch.txt","r")
		self.tr_b = (self.f1.read()).split("\n")
		self.f3 = open("train_test.txt","r")
		self.tt_b = (self.f3.read()).split("\n")
		if is_random:
			random.shuffle(self.tr_b)
		self.f2 = open("test_batch.txt","r")
		self.ts_b = (self.f2.read()).split("\n")
	def getnextbatch(self,num = 100):
		for i in range(num):
			print(self.tr_b[i])
	def getnextbatch_wordbag(self,num = 100):
		random.shuffle(self.tr_b)
		ans = np.zeros((num,11757))
		tag = np.zeros((num,2))
		for i in range(num):
			str0 = self.tr_b[i]
			str1 = str0.split("\t",1)
			tag [i,int(str1[0])] = 1
			str2 = str1[1].split("/")
			for ss in str2:
				ans[i,int(ss)-1] = ans[i,int(ss)-1] +1
		return tag, ans
	def gettrainbatch_wordbag(self,num = 1000):
		ans = np.zeros((num,11757))
		tag = np.zeros((num,2))
		for i in range(num):
			str0 = self.tt_b[i]
			str1 = str0.split("\t",1)
			tag [i,int(str1[0])] = 1
			str2 = str1[1].split("/")
			for ss in str2:
				ans[i,int(ss)-1] = ans[i,int(ss)-1] +1
		return tag, ans
	def gettestbatch_wordbag(self,num = 4189):
		ans = np.zeros((num,11757))
		for i in range(num):
			str0 = self.ts_b[i]
			str2 = str0.split("/")
			for ss in str2:
				#print(ss)
				ans[i,int(ss)-1] = ans[i,int(ss)-1] +1
		return ans
		
batch = batch_manager()		
if (__name__ == "__main__"):
	batch.getnextbatch_wordbag()
	
	