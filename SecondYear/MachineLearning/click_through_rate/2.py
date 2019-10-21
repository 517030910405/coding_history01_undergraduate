#coding=utf-8
import os
import sys
import pickle
import numpy as np
import xgboost as xgb
import scipy
#import batch1_bagofwords as batch1_man

if __name__ == "__main__":
	#number_of_train_sample = 1200
	
	""" read in data """
	#X_path = 'freq_mat.pkl'
	#y_path = 'label.pkl'
	#ifs = open(X_path, 'rb')
	#X_train_test = pickle.load(ifs).tocsr()
	'''Batch = batch1_man.batch()
	bag_batch = Batch.train1_bag_batch(10)
	X_train_test = bag_batch[1]'''
	#X_train = X_train_test[0 : number_of_train_sample]
	#X_test = X_train_test[number_of_train_sample : ]
	print('opening file X has been done!')
	
	#ifs = open(y_path, 'rb')
	#y_train = np.array(pickle.load(ifs))
	#y_train = bag_batch[0][0:number_of_train_sample]
	
	print('opening file y has been done!')

	#print('X_train shape: ', X_train.shape)
	#print('X_test shape: ', X_test.shape)
	#print('y_train shape: ', y_train.shape)

	""" train """
	#data_train = xgb.DMatrix(X_train, label= y_train)
	#data_test = xgb.DMatrix(X_test)
	data_train = xgb.DMatrix('./data/train.svm.txt')
	data_test = xgb.DMatrix('./data/test.svm.txt')

	xgb_params = {
		'booster' : 'gbtree',
		'nthread' : 6,
		'silent' : True,
		'objective' : 'binary:logistic',
		'eta' : 0.05, # learning rate
		'max_depth' : 30,
		'min_child_weight' : 3,
		'colsample_bytree' : 1.0,
		'subsample' : 1.0, # 1.0
		# 'gamma' : 0.17,
		# 'lambda' : 2.5,
		# 'scale_pos_weight' : 1.0,
		# 'num_parallel_tree' : 1,
		'eval_metric' : 'auc'
	}
	print(xgb_params)
	model = xgb.train(xgb_params, data_train, \
				num_boost_round = 5000, \
				early_stopping_rounds= 5, \
				evals= [(data_train, 'train')])
	y_train_pred = model.predict(data_train)
	#print(type(y_train_pred))
	y_test = model.predict(data_test)
	print(y_test.shape)
	np.savetxt("mypredict1.txt",y_test)
	#ofs = open('./data/y_train.pkl', 'wb')
	#pickle.dump(y_train_pred, ofs)
	#ofs = open('./data/y_test.pkl', 'wb')
	#pickle.dump(y_test, ofs)
	