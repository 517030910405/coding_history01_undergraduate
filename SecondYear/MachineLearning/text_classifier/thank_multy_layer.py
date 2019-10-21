#Author: LiJiasen SJTU
#Copyright is not available
import random
import numpy as np
from sklearn.metrics import log_loss, roc_auc_score
from matplotlib import pyplot as plt
class batch:
	def __init__(self):
		self.train1_file = open("/kaggle/input/tr2.txt","r")
		self.test1_file = open("/kaggle/input/ts2.txt","r")
		self.train1 = self.train1_file.read().split("\n")
		self.test1 = self.test1_file.read().split("\n")
		self.train1.pop()
		self.test1.pop()
		self.test1_file.close()
		self.train1_file.close()
		self.dict_size = 30000
	def random_train1(self):
		random.seed(2019)
		random.shuffle(self.train1)
	def split_test(self):
		self.random_train1()
		self.train2 = []
		self.test2 = []
		for i in range(13000):
			self.train2.append(self.train1[i])
		for i in range(13000,16000):
			self.test2.append(self.train1[i])
	def train2_bag_batch(self,n):
		random.shuffle(self.train2)
		tag = np.zeros((n,2))
		bat = np.zeros((n,self.dict_size))
		for i in range(n):
			str = self.train2[i].split(" ")
			for j in range(len(str)-1):
				bat[i][int(str [j+1])]+=1
			bat[i] = bat[i] / np.sqrt((bat[i]*bat[i]).sum())
			tag[i][int(str[0])]+=1
		return [tag,bat]
				
	def test2_bag_batch(self,n1,n2):
		tag = np.zeros((n2-n1,2))
		bat = np.zeros((n2-n1,self.dict_size))
		for i in range(n1,n2):
			str = self.test2[i].split(" ")
			for j in range(len(str)-1):
				bat[i-n1][int(str [j+1])]+=1
			bat[i] = bat[i] / np.sqrt((bat[i]*bat[i]).sum())
			tag[i-n1][int(str[0])]+=1
		return [tag,bat]
		
	def test1_bag_batch(self,n1,n2):
		#tag = np.zeros((n2-n1,2))
		bat = np.zeros((n2-n1,self.dict_size))
		for i in range(n1,n2):
			str = self.test1[i].split(" ")
			for j in range(len(str)-1):
				bat[i-n1][int(str [j+1])]+=1
			bat[i] = bat[i] / np.sqrt((bat[i]*bat[i]).sum())
		return [bat]

import tensorflow as tf
import numpy as np
Batch = batch()
Batch.split_test()
def ans_fc(y1,y2):
	assert y1.shape == y2.shape
	yy1 = np.argmax(y1, axis = 1)
	yy2 = np.argmax(y2, axis = 1)
	ans = np.mean(np.equal(yy1,yy2))
	return ans
def testrate():
	assert True
	ans = np.zeros((1000,2))
	for i in range(10):
		bt = Batch.test2_bag_batch(0+i*100,100+i*100)
		Y_ = np.array(YYY.eval(feed_dict={X:bt[1]},session = sess))
		ans[0+i*100:100+i*100] = Y_
	rate = ans_fc(ans,Batch.test2_bag_label(0,1000)[0])
	print(rate)
	
#Batch.test2_bag_batch(0,10)
#print(Batch.test2_bag_label(0,10))
dict_size = 30000
layer0 = dict_size
layer1 = 512
layer2 = 512
layer3 = 2
DropProb = tf.placeholder(tf.float64)

X = tf.placeholder(tf.float64,[None,dict_size])
Y = tf.placeholder(tf.float64,[None,2])
#W = tf.Variable(tf.zeros([dict_size,2]))
#B = tf.Variable(tf.zeros([2]))

W1 = tf.Variable(tf.random_normal([layer0,layer1], stddev=0.008,dtype = tf.float64))
B1 = tf.Variable(tf.random_normal([layer1], stddev=0,dtype = tf.float64))
H1 = tf.nn.relu(tf.matmul(X,W1)+B1)
H1 = tf.nn.dropout(H1, DropProb)

#DropProb = tf.placeholder(tf.float64)
#H1 = tf.nn.dropout(H1, DropProb)

W2 = tf.Variable(tf.random_normal([layer1,layer2], stddev=0.06,dtype = tf.float64))
B2 = tf.Variable(tf.random_normal([layer2], stddev=0,dtype = tf.float64))
H2 = tf.nn.relu(tf.matmul(H1,W2)+B2)


H2 = tf.nn.dropout(H2, DropProb)


W3 = tf.Variable(tf.random_normal([layer2,layer3], stddev=0.06,dtype = tf.float64))
B3 = tf.Variable(tf.random_normal([layer3], stddev=0,dtype = tf.float64))
H3 = tf.matmul(H2,W3)+B3


regularizer = tf.contrib.layers.l2_regularizer(scale=1e-3)
tf.add_to_collection(tf.GraphKeys.WEIGHTS, W1)
tf.add_to_collection(tf.GraphKeys.WEIGHTS, B1)
tf.add_to_collection(tf.GraphKeys.WEIGHTS, W2)
tf.add_to_collection(tf.GraphKeys.WEIGHTS, B2)
tf.add_to_collection(tf.GraphKeys.WEIGHTS, W3)
tf.add_to_collection(tf.GraphKeys.WEIGHTS, B3)
reg_term = tf.contrib.layers.apply_regularization(regularizer)


YY = H3
YYY = tf.nn.softmax(YY)
#YY = tf.matmul(X,W)+B
#YYY = tf.nn.softmax(YY)

global_step = tf.Variable(0, trainable=False)
starter_learning_rate = 0.001
learning_rate = tf.train.exponential_decay(starter_learning_rate, global_step,500, 0.5, staircase=True)



correct_prediction = tf.equal(tf.argmax(YY, 1), tf.argmax(Y, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
cross_entropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits_v2(labels=Y, logits=YY))
train_step = tf.train.AdamOptimizer(learning_rate).minimize(cross_entropy+reg_term,global_step=global_step)

sess = tf.Session()
sess.run(tf.global_variables_initializer())

#bta = Batch.train2_bag_batch(15000)
#btb = Batch.test2_bag_batch(0,1000)
btc = Batch.test1_bag_batch(0,4189)

cr_en_list1 = []
cr_en_list2 = []
auc_list1 = []
auc_list2 = []
for i in range(1000):
	bta = Batch.train2_bag_batch(1000)
	sess.run(train_step,feed_dict = {X:bta[1],Y:bta[0],DropProb:0.5})
	
	if (i%50==0):
		print(i)
		print(" val")
		btb = Batch.test2_bag_batch(0,3000)
		acc,cr_en,Y_ = sess.run([accuracy,cross_entropy+reg_term,YYY],feed_dict={X:btb[1],Y:btb[0],DropProb:1.0})
		cr_en_list1.append((cr_en))
		print('accuracy = %.5f, loss = %.5f'%(acc,cr_en))
		auc_ = round(roc_auc_score(btb[0], Y_), 6)
		print("test AUC", auc_)
		auc_list1.append(auc_)
		
		
		Y_ = sess.run(YYY,feed_dict={X:btc[0],DropProb:1.0})
		np.savetxt(str(10000+i)+"_"+str(auc_)+".txt",Y_)
		print(" train")
		bta = Batch.train2_bag_batch(3000)
		acc,cr_en,Y_ = sess.run([accuracy,cross_entropy+reg_term,YYY],feed_dict={X:bta[1],Y:bta[0],DropProb:1.0})
		cr_en_list2.append((cr_en))
		print('accuracy = %.5f, loss = %.5f'%(acc,cr_en))
		auc_ = round(roc_auc_score(bta[0], Y_), 6)
		auc_list2.append(auc_)
		print("train AUC", auc_)
		print("")
		
plt.plot(cr_en_list1)
plt.title('training cross entropy')
plt.savefig("test.png")
plt.show()

plt.plot(cr_en_list2)
plt.title('validation cross entropy')
plt.savefig("train.png")
plt.show()

plt.plot(auc_list1)
plt.plot(auc_list2)
plt.savefig("auc.png")
plt.show()
# fo = open("history.txt","w")
# fo.write(str(cr_en_list1)+str(cr_en_list2))
# fo.close()
if False:
	ans = np.zeros((700,2))
	for i in range(10):
		bt = Batch.test2_bag_batch(0+i*100,100+i*100)
		Y_ = np.array(YYY.eval(feed_dict={X:bt[1],DropProb:1.0},session = sess))
		ans[0+i*100:100+i*100] = Y_
	rate = ans_fc(ans,Batch.test2_bag_label(0,1000)[0])
	print(rate)
	name = "ml_out_1"
	np.savetxt("../run_env2/"+name+".txt",ans,fmt = "%.20f")
if False:
	ans = np.zeros((4189,2))
	for i in range(41):
		bt = Batch.test1_bag_batch(0+i*100,100+i*100)
		Y_ = np.array(YYY.eval(feed_dict={X:bt[0],DropProb:1.0},session = sess))
		ans[0+i*100:100+i*100] = Y_
	bt = Batch.test1_bag_batch(4100,4189)
	Y_ = np.array(YYY.eval(feed_dict={X:bt[0],DropProb:1.0},session = sess))
	ans[4100:4189] = Y_

	#name_file = open("./cnn_ensemble1/name.txt","r")
	#name = name_file.read()
	name = "ml_out_lw_3"
	np.savetxt("../run_env2/"+name+".txt",ans,fmt = "%.20f")
