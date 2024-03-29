#!pip install deepctr --no-deps
import os
os.system("pip install deepctr  --no-deps")
import pandas as pd
import numpy as np
import deepctr
from tensorflow.python.keras.optimizers import Adam,Adagrad
from tensorflow.python.keras.callbacks import EarlyStopping
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error
from sklearn.metrics import log_loss, roc_auc_score
#from deepctr.models import DeepFM
#from deepctr.models import PNN
from deepctr.models.nffm import NFFM
from deepctr.utils	import VarLenFeat,SingleFeat
if True:
	data = pd.read_csv('/kaggle/input/train.csv')
	train = data
	test = pd.read_csv('/kaggle/input/test.csv')
	sparse_features = ["F"+str(it+1) for it in range(24)]
	target = ["label"]
	# 1.Label Encoding for sparse features,and do simple Transformation for dense features
	for feat in sparse_features:
		lbe = LabelEncoder()
		train[feat] = lbe.fit_transform(train[feat])
		test[feat] = lbe.fit_transform(test[feat])
	# 2.count #unique features for each sparse field
	sparse_feat_list = [SingleFeat(feat,train[feat].nunique()) for feat in sparse_features]
	#singlefeat is dict from feat id to feat onehot dim
	#sparse_feat_list = [SingleFeat(feat,test[feat].nunique()) for feat in sparse_features]
	# 3.generate input data for model
	train_model_input = [train[feat.name].values for feat in sparse_feat_list]
	test_model_input = [test[feat.name].values for feat in sparse_feat_list]
	# 4.Define Model,train,predict and evaluate
	model = NFFM({"sparse": sparse_feat_list},final_activation='sigmoid')
	model.compile("adam", "binary_crossentropy", metrics=['binary_crossentropy'],)
	print("LiJiasen Running")
	es = EarlyStopping(monitor='val_binary_crossentropy')
	history = model.fit(train_model_input, train[target].values,batch_size=20480, 
	epochs=10, verbose=2, validation_split=0.2,callbacks=[es] )
						#epochs is the round number
	pred_ans = model.predict(test_model_input, batch_size=20480)
	np.savetxt("nffm1.txt",pred_ans,fmt = "%.20f")
	print("Ready!")
