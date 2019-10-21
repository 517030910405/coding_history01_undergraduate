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
	#for feat in sparse_features:
	#	lbe = LabelEncoder()
	#	train[feat] = lbe.fit_transform(train[feat])
	#	test[feat] = lbe.fit_transform(test[feat])
	train["F1"] -= 0
	train["F2"] -= 101449
	train["F3"] -= 101456
	train["F4"] -= 101480
	train["F5"] -= 101485
	train["F6"] -= 107410
	train["F7"] -= 631082
	train["F8"] -= 634569
	train["F9"] -= 634573
	train["F10"] -= 637708
	train["F11"] -= 637732
	train["F12"] -= 637739
	train["F13"] -= 641741
	train["F14"] -= 641769
	train["F15"] -= 642021
	train["F16"] -= 642031
	train["F17"] -= 642098
	train["F18"] -= 642106
	train["F19"] -= 642113
	train["F20"] -= 642539
	train["F21"] -= 642548
	train["F22"] -= 642714
	train["F23"] -= 642718
	train["F24"] -= 642778
	
	test["F1"] -= 0
	test["F2"] -= 101449
	test["F3"] -= 101456
	test["F4"] -= 101480
	test["F5"] -= 101485
	test["F6"] -= 107410
	test["F7"] -= 631082
	test["F8"] -= 634569
	test["F9"] -= 634573
	test["F10"] -= 637708
	test["F11"] -= 637732
	test["F12"] -= 637739
	test["F13"] -= 641741
	test["F14"] -= 641769
	test["F15"] -= 642021
	test["F16"] -= 642031
	test["F17"] -= 642098
	test["F18"] -= 642106
	test["F19"] -= 642113
	test["F20"] -= 642539
	test["F21"] -= 642548
	test["F22"] -= 642714
	test["F23"] -= 642718
	test["F24"] -= 642778
	
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
