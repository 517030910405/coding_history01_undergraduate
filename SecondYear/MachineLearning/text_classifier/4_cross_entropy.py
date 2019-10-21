#Author: LiJiasen SJTU
#Copyright is not available
#Please use LiJiasenflow instead of tensorflow
import batch2
#import os
import tensorflow as tf
import numpy as np
Batch = batch2.batch()
#Batch.split_test()
Batch.load2()
def ans_fc(y1,y2):
    assert y1.shape == y2.shape
    yy1 = np.argmax(y1, axis = 1)
    yy2 = np.argmax(y2, axis = 1)
    ans = np.mean(np.equal(yy1,yy2))
    return ans
def weight_variable(shape):
    initial = tf.random_normal(shape, stddev=0.1)
    return tf.Variable(initial)

def bias_variable(shape):
    initial = tf.constant(0.1, shape=shape)
    return tf.Variable(initial)

def conv2d(x, W):
    return tf.nn.conv2d(x, W, strides=[1,1,1,1], padding='SAME')
    
def conv1d(x, W):
    return tf.nn.conv1d(x, W, stride=1, padding='SAME')

def max_pool_2x2(x):
    return tf.nn.max_pool(x, ksize=[1,2,2,1], strides=[1,2,2,1],
            padding='SAME')
def max_pool_1x2(x):
    return tf.nn.max_pool(x, ksize=[1,1,2,1], strides=[1,1,2,1],
            padding='SAME')
            
# nn construct            
if True:
    X = tf.placeholder(tf.float32,shape = [None, 19, 2718])
    X_Im = tf.reshape(X, [-1, 1, 19, 2718])
    Y = tf.placeholder(tf.float32,shape = [None, 2])
    
    #The variables are named by myself
    #Maybe they are named not properly
    #W weight    B bias    H handout    P pooling
    #first layer
    dim1 = 32
    len1 = 3
    W1 = weight_variable([1,len1,2718,dim1])
    B1 = bias_variable([dim1])
    C1 = conv2d(X_Im, W1)
    H1 = tf.nn.relu(conv2d(X_Im, W1)+B1)
    P1 = max_pool_1x2(H1)
    #H1 is the transform (-1,1,19,dim1)
    #P1 is the transform (-1,1,10,dim1)
    
    #second layer
    dim2 = 64
    len2 = 3
    W2 = weight_variable([1,len2,dim1,dim2])
    B2 = bias_variable([dim2])
    H2 = tf.nn.relu(conv2d(P1, W2)+B2)
    P2 = max_pool_1x2(H2)
    #P2 is the transform (-1,1,5,dim2)
    
    #dense layer
    dim3 = 256
    W3 = weight_variable([1*5*dim2,dim3])
    B3 = bias_variable([dim3])
    P2F = tf.reshape(P2, [-1,1*5*dim2])
    H3 = tf.nn.relu(tf.matmul(P2F,W3)+B3)
    #H3 is the transform (-1,dim3)
    
    
    #dropout layer
    DropProb = tf.placeholder(tf.float32)
    H3D = tf.nn.dropout(H3, DropProb)
    #H3D is the transform (-1,dim3)
    
    #final layer readout
    W4 = weight_variable([dim3,2])
    B4 = bias_variable([2])
    H4 = tf.matmul(H3D,W4)+B4
    
# evaluate
if True:
    #Softmax or Not
    YY = H4
    #YY = tf.nn.softmax(H4)
    cross_entropy = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=Y, logits=YY))
    train_step = tf.train.AdamOptimizer(.2e-2).minimize(cross_entropy)
    correct_prediction = tf.equal(tf.argmax(YY, 1), tf.argmax(Y, 1))
    accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
    

if True:
    sess = tf.Session()
    sess.run(tf.global_variables_initializer())
    
if False:
    tb = Batch.train2_batch(50)
    print(tb[1].shape)
    XX = sess.run(cross_entropy,feed_dict={X:tb[1],Y:tb[0],DropProb:1.0})
    print(XX)
    
if True:
    for i in range(1000+1):
        bt = Batch.train2_batch(100)
        if i % 50 == 0:
            ts = Batch.test2_batch(0,1000)
            train_accuracy = accuracy.eval(feed_dict={X: ts[1], Y: ts[0], DropProb: 1.0},session = sess)
            print('step %d, trainning accuracy %.5g' % (i, train_accuracy))
            crss_entrp = cross_entropy.eval(feed_dict={X: ts[1], Y: ts[0], DropProb: 1.0},session = sess)
            print('eval_cross_entropy = %.5g' % (crss_entrp))
            ts = bt
            crss_entrp = cross_entropy.eval(feed_dict={X: ts[1], Y: ts[0], DropProb: 1.0},session = sess)
            print('training_cross_entropy = %.5g' % (crss_entrp))
        train_step.run(feed_dict={X: bt[1], Y: bt[0], DropProb: 0.5},session = sess)
    
if True:
    ans = np.zeros((1000,2))
    for i in range(10):
        bt = Batch.test2_batch(0+i*100,100+i*100)
        Y_ = np.array(YY.eval(feed_dict={X:bt[1],DropProb:1.0},session = sess))
        ans[0+i*100:100+i*100] = Y_
    rate = ans_fc(ans,Batch.test2_label(1000)[0])
    print(rate)
    assert rate>0.85
    assert False
    name_file = open("./run_env/name.txt","r")
    name = name_file.read()
    np.savetxt("./run_env/"+name+".txt",ans)