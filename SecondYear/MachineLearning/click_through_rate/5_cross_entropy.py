import pandas as pd
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error
from sklearn.metrics import log_loss, roc_auc_score
from deepctr.models import DeepFM
from deepctr.models import PNN
from deepctr.utils	import VarLenFeat,SingleFeat
import numpy as np
if __name__ == "__main__":

	data = pd.read_csv("train.csv")
	#print(data)
	train = data
	test = pd.read_csv("test.csv")
	sparse_features = ["F1","F2","F3","F4","F5","F6","F7","F8","F9","F10","F11","F12","F13","F14","F15","F16","F17","F18","F19","F20","F21","F22","F23","F24"]
	target = ["label"]
	
	
	# 1.Label Encoding for sparse features,and do simple Transformation for dense features
	for feat in sparse_features:
		lbe = LabelEncoder()
		train[feat] = lbe.fit_transform(train[feat])
		test[feat] = lbe.fit_transform(test[feat])
	# 2.count #unique features for each sparse field
	sparse_feat_list = [SingleFeat(feat,train[feat].nunique()) for feat in sparse_features]
	sparse_feat_list = [SingleFeat(feat,test[feat].nunique()) for feat in sparse_features]
	#print(sparse_feat_list)
	# 3.generate input data for model
	#train, test = train_test_split(data, test_size=0.5)
	#print(test)
	#train = data
	train_model_input = [train[feat.name].values for feat in sparse_feat_list]
	test_model_input = [test[feat.name].values for feat in sparse_feat_list]
	#print(train_model_input)
	# 4.Define Model,train,predict and evaluate
	model = PNN({"sparse": sparse_feat_list},
				   final_activation='sigmoid')
	model.compile("adam", "binary_crossentropy", metrics=['binary_crossentropy'],)
	#model.compile("adam", "mse", metrics=['mse'],)
	#model.compile("adam", "mse", metrics=['mse'],)
	#for i in range(10):
	history = model.fit(train_model_input, train[target].values,
						batch_size=256, epochs=6, verbose=2, validation_split=0.1,)
						#epochs is the round number
	#print(history)
	#print(history)
	pred_ans = model.predict(test_model_input, batch_size=256)
	#print(test_model_input)
	#print(pred_ans)
	#print("test binary_crossentropy", round(mean_squared_error(
	#	test[target].values, pred_ans), 4))
	#print(test[target].values)
	#print(pred_ans)
	np.savetxt("1.txt",pred_ans,fmt = "%.20f")
	print("Ready!")
	#print("test AUC", round(roc_auc_score(test[target].values, pred_ans), 4))