#!pip install deepctr --no-deps
import os
os.system("pip install deepctr  --no-deps")
import pandas as pd
import numpy as np
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error
from sklearn.metrics import log_loss, roc_auc_score
from deepctr.models import DeepFM
from deepctr.models import PNN
from deepctr.models.nffm import NFFM
from deepctr.utils	import VarLenFeat,SingleFeat
if True:
	train = pd.read_csv('/kaggle/input/train.csv')
	#train = data
	test = pd.read_csv('/kaggle/input/test.csv')
	sparse_features = ["F"+str(it+1) for it in range(24)]
	target = ["label"]
	# 1.Label Encoding for sparse features,and do simple Transformation for dense features
	for feat in sparse_features:
		lbe = LabelEncoder()
		train[feat] = lbe.fit_transform(train[feat])
		test[feat] = lbe.fit_transform(test[feat])
	# 2.count #unique features for each sparse field
	#sparse_feat_list = [SingleFeat(feat,train[feat].nunique()) for feat in sparse_features]
	sparse_feat_list = []
	sparse_feat_list.append(SingleFeat("F1",101449-0))
	sparse_feat_list.append(SingleFeat("F2",101456-101449))
	sparse_feat_list.append(SingleFeat("F3",101480-101456))
	sparse_feat_list.append(SingleFeat("F4",101485-101480))
	sparse_feat_list.append(SingleFeat("F5",107410-101485))
	sparse_feat_list.append(SingleFeat("F6",631082-107410))
	sparse_feat_list.append(SingleFeat("F7",634569-631082))
	sparse_feat_list.append(SingleFeat("F8",634573-634569))
	sparse_feat_list.append(SingleFeat("F9",637708-634573))
	sparse_feat_list.append(SingleFeat("F10",637732-637708))
	sparse_feat_list.append(SingleFeat("F11",637739-637732))
	sparse_feat_list.append(SingleFeat("F12",641741-637739))
	sparse_feat_list.append(SingleFeat("F13",641769-641741))
	sparse_feat_list.append(SingleFeat("F14",642021-641769))
	sparse_feat_list.append(SingleFeat("F15",642031-642021))
	sparse_feat_list.append(SingleFeat("F16",642098-642031))
	sparse_feat_list.append(SingleFeat("F17",642106-642098))
	sparse_feat_list.append(SingleFeat("F18",642113-642106))
	sparse_feat_list.append(SingleFeat("F19",642539-642113))
	sparse_feat_list.append(SingleFeat("F20",642548-642539))
	sparse_feat_list.append(SingleFeat("F21",642714-642548))
	sparse_feat_list.append(SingleFeat("F22",642718-642714))
	sparse_feat_list.append(SingleFeat("F23",642778-642718))
	sparse_feat_list.append(SingleFeat("F24",645195-642778))
	
	#singlefeat is dict from feat id to feat onehot dim
	#sparse_feat_list = [SingleFeat(feat,test[feat].nunique()) for feat in sparse_features]
	# 3.generate input data for model
	train_model_input = [train[feat.name].values for feat in sparse_feat_list]
	test_model_input = [test[feat.name].values for feat in sparse_feat_list]
	# 4.Define Model,train,predict and evaluate
	model = DeepFM({"sparse": sparse_feat_list},final_activation='sigmoid')
	model.compile("adam", "binary_crossentropy", metrics=['binary_crossentropy'],)
	print("LiJiasen Running")
	history = model.fit(train_model_input, train[target].values,
						batch_size=20480, epochs=20, verbose=2, validation_split=0.2,)
						#epochs is the round number
	pred_ans = model.predict(test_model_input, batch_size=20480)
	np.savetxt("deepfm1.txt",pred_ans,fmt = "%.20f")
	print("Ready!")
