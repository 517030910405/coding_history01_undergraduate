#Author: LiJiasen SJTU
#Copyright is not available
import random
import numpy as np
class batch:
	def __init__(self):
		self.train1_file = open("./data/train.svm.txt","r")
		self.test1_file = open("./data/test.svm.txt","r")
		self.train1 = self.train1_file.read().split("\n")
		self.test1 = self.test1_file.read().split("\n")
		self.train1.pop()
		self.test1.pop()
		self.test1_file.close()
		self.train1_file.close()
		self.dict_size = 645194
	def random_train1(self):
		random.shuffle(self.train1)
	def split_test(self):
		self.random_train1()
		self.train2 = []
		self.test2 = []
		for i in range(15000):
			self.train2.append(self.train1[i])
		for i in range(1000):
			self.test2.append(self.train1[i+15000])
		self.train2_file = open("./data/tr3.txt","w")
		self.test2_file = open("./data/ts3.txt","w")
		self.train2_file.write("\n".join(self.train2))
		self.test2_file.write("\n".join(self.test2))
		self.test2_file.close()
		self.train2_file.close()
	def load2(self):
		self.train2_file = open("./data/tr3.txt","r")
		self.test2_file = open("./data/ts3.txt","r")
		self.test2 = self.test2_file.read().split("\n")
		self.train2 = self.train2_file.read().split("\n")
		self.test2_file.close()
		self.train2_file.close()
	def train2_bag_batch(self,n):
		random.shuffle(self.train2)
		tag = np.zeros((n,2))
		bat = np.zeros((n,self.dict_size))
		for i in range(n):
			str = self.train2[i].split(" ")
			for j in range(len(str)-1):
				bat[i][int(str [j+1])]=1
			tag[i][int(str[0])]=1
		return [tag,bat]
		
	def train1_bag_batch(self,n):
		random.shuffle(self.train1)
		tag = np.zeros((n,2))
		bat = np.zeros((n,self.dict_size))
		for i in range(n):
			str = self.train1[i].split(" ")
			for j in range(len(str)-1):
				bat[i][int(str [j+1])]=1
			tag[i][int(str[0])]=1
		return [tag,bat]
		
	def test2_bag_batch(self,n1,n2):
		tag = np.zeros((n2-n1,2))
		bat = np.zeros((n2-n1,self.dict_size))
		for i in range(n1,n2):
			str = self.test2[i].split(" ")
			for j in range(len(str)-1):
				bat[i-n1][int(str [j+1])]=1
			tag[i-n1][int(str[0])]=1
		return [tag,bat]
		
	def test2_bag_label(self,n1,n2):
		tag = np.zeros((n2-n1,2))
		for i in range(n1,n2):
			str = self.test2[i].split(" ")
			tag[i-n1][int(str[0])]=1
		return [tag]
	
	def test1_bag_batch(self,n1,n2):
		#tag = np.zeros((n2-n1,2))
		bat = np.zeros((n2-n1,self.dict_size))
		for i in range(n1,n2):
			str = self.test1[i].split(" ")
			for j in range(len(str)-1):
				bat[i-n1][int(str [j+1])]=1
			#tag[i-n1][int(str[0])]=1
		return [bat]
	'''
	
	def train2_batch(self,n):
		random.shuffle(self.train2)
		tag = np.zeros((n,2))
		bat = np.zeros((n,19,2718))
		for i in range(n):
			str = self.train2[i].split(" ")
			#print(str)
			#x=0
			for j in range(len(str)-2):
				bat[i][j][int(str [j+2])-1]=1
			tag[i][int(str[0])]=1
		return [tag,bat]
	def train1_batch(self,n):
		random.shuffle(self.train1)
		tag = np.zeros((n,2))
		bat = np.zeros((n,19,2718))
		for i in range(n):
			str = self.train1[i].split(" ")
			#print(str)
			#x=0
			for j in range(len(str)-2):
				bat[i][j][int(str [j+2])-1]=1
			tag[i][int(str[0])]=1
		return [tag,bat]
		
	def test2_batch(self,n1,n2):
		#random.shuffle(self.test2)
		tag = np.zeros((n2-n1,2))
		bat = np.zeros((n2-n1,19,2718))
		for i in range(n1,n2):
			str = self.test2[i].split(" ")
			for j in range(len(str)-2):
				bat[i-n1][j][int(str [j+2])-1]=1
			tag[i-n1][int(str[0])]=1
		return [tag,bat]

	def test2_label(self,n):
		tag = np.zeros((n,2))
		for i in range(n):
			str = self.test2[i].split(" ")
			tag[i][int(str[0])]=1
		return [tag]
		
	def test1_label(self,n):
		tag = np.zeros((n,2))
		for i in range(n):
			str = self.test1[i].split(" ")
			tag[i][int(str[0])]=1
		return [tag]
		
	def test1_batch(self,n1,n2):
		#random.shuffle(self.test2)
		bat = np.zeros((n2-n1,19,2718))
		for i in range(n1,n2):
			str = self.test2[i].split(" ")
			for j in range(len(str)-1):
				bat[i-n1][j][int(str [j+1])-1]=1
		return bat
	'''
		#assert True
if (__name__=="__main__"):
	Batch = batch()
	Batch.split_test()