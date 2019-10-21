import pandas as pd
from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error
from sklearn.metrics import log_loss, roc_auc_score
from deepctr.models import DeepFM
from deepctr.models import PNN
from deepctr.utils	import VarLenFeat,SingleFeat

if __name__ == "__main__":

	data = pd.read_csv("./criteo_sample.txt")
	#print(data)
	train = data
	test = pd.read_csv("./criteo_sample2.txt")
	sparse_features = ["F1","F2","F3"]
	target = ["label"]
	
	
	# 1.Label Encoding for sparse features,and do simple Transformation for dense features
	for feat in sparse_features:
		lbe = LabelEncoder()
		data[feat] = lbe.fit_transform(data[feat])
	# 2.count #unique features for each sparse field
	sparse_feat_list = [SingleFeat(feat,data[feat].nunique()) for feat in sparse_features]
	#print(sparse_feat_list)
	# 3.generate input data for model
	train, test = train_test_split(data, test_size=0.5)
	#print(test)
	#train = data
	train_model_input = [train[feat.name].values for feat in sparse_feat_list]
	test_model_input = [test[feat.name].values for feat in sparse_feat_list]
	#print(train_model_input)
	# 4.Define Model,train,predict and evaluate
	model = PNN({"sparse": sparse_feat_list},
				   final_activation='linear')
	model.compile("adam", "mse", metrics=['mse'],)
	#model.compile("adam", "mse", metrics=['mse'],)
	#model.compile("adam", "mse", metrics=['mse'],)
	#for i in range(10):
	history = model.fit(train_model_input, train[target].values,
						batch_size=256, epochs=1, verbose=2, validation_split=0.1,)
						#epochs is the round number
	#print(history)
	#print(history)
	pred_ans = model.predict(test_model_input, batch_size=256)
	#print(test_model_input)
	#print(pred_ans)
	print("test mse", round(mean_squared_error(
		test[target].values, pred_ans), 4))
	print(test[target].values)
	print(pred_ans)
	#print("test AUC", round(roc_auc_score(test[target].values, pred_ans), 4))