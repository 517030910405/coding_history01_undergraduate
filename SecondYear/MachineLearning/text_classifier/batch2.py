#Author: LiJiasen SJTU
#Copyright is not available
import random
import numpy as np
class batch:
    def __init__(self):
        self.train1_file = open("tr1.txt","r")
        self.test1_file = open("ts1.txt","r")
        self.train1 = self.train1_file.read().split("\n")
        self.test1 = self.test1_file.read().split("\n")
        self.train1.pop()
        self.test1.pop()
        self.test1_file.close()
        self.train1_file.close()
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
        self.train2_file = open("tr2.txt","w")
        self.test2_file = open("ts2.txt","w")
        self.train2_file.write("\n".join(self.train2))
        self.test2_file.write("\n".join(self.test2))
        self.test2_file.close()
        self.train2_file.close()
    def load2(self):
        self.train2_file = open("tr2.txt","r")
        self.test2_file = open("ts2.txt","r")
        self.test2 = self.test2_file.read().split("\n")
        self.train2 = self.train2_file.read().split("\n")
        self.test2_file.close()
        self.train2_file.close()
        
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
        
        #assert True