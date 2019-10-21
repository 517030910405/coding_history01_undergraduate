import batch
import tensorflow as tf
import numpy as np
x = tf.placeholder(tf.float32,[None,11757])
W = tf.Variable(tf.zeros([11757,2]))
b = tf.Variable(tf.zeros([2]))
y_ = tf.placeholder(tf.float32,[None,2])
y = tf.nn.softmax(tf.matmul(x,W)+b)
cross_entropy = tf.reduce_mean(-tf.reduce_sum(y_ * tf.log(y), reduction_indices=[1]))
train_step = tf.train.GradientDescentOptimizer(10.0).minimize(cross_entropy)


sess = tf.Session()
sess.run(tf.global_variables_initializer())


for _ in range(5000):
	batch_y , batch_x = batch.batch.getnextbatch_wordbag(200)
	sess.run(train_step, feed_dict={x: batch_x, y_: batch_y})
	
correct_prediction = tf.equal(tf.argmax(y, 1), tf.argmax(y_, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

batch_y , batch_x = batch.batch.gettrainbatch_wordbag()
t_x = batch.batch.gettestbatch_wordbag()
ans = sess.run(accuracy, feed_dict={x: batch_x, y_: batch_y})
print(ans)
res = sess.run(y,feed_dict={x:t_x})
print(res)
np.savetxt("wordbag_ans.txt",res[:,1],delimiter=',')