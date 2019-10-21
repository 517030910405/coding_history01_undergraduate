import os
os.system("pip install deepctr  --no-deps")
import pandas as pd
import numpy as np
from sklearn.preprocessing import LabelEncoder, MinMaxScaler
from sklearn.model_selection import train_test_split
from sklearn.metrics import log_loss, roc_auc_score
from deepctr.models import DeepFM
from deepctr.models import PNN
from deepctr.models import NFFM
from deepctr.models.dien import DIEN
from deepctr.models.autoint import AutoInt
from deepctr.utils	import SingleFeat

if __name__ == "__main__":
	data = pd.read_csv('/kaggle/input/supertrain-ctr/super_train.csv')

	sparse_features = ["F"+str(it+1) for it in range(24)]
	data[sparse_features] = data[sparse_features].fillna('-1', )
	target = ['label']
	# 1.Label Encoding for sparse features,and do simple Transformation for dense features
	for feat in sparse_features:
		lbe = LabelEncoder()
		data[feat] = lbe.fit_transform(data[feat])
	# 2.count #unique features for each sparse field,and record dense feature field name

	sparse_feature_list = [SingleFeat(feat, data[feat].nunique())
						   for feat in sparse_features]

	# 3.generate input data for model
	train, test = train_test_split(data, test_size=0.2,shuffle = False)
	train1, train2 = train_test_split(train, test_size=0.1,shuffle = True)
	train1_model_input = [train1[feat.name].values for feat in sparse_feature_list] 
	train2_model_input = [train2[feat.name].values for feat in sparse_feature_list] 
	test_model_input = [test[feat.name].values for feat in sparse_feature_list]

	# 4.Define Model,train,predict and evaluate
	model = AutoInt({"sparse": sparse_feature_list}, final_activation='sigmoid')
	model.compile("adam", "binary_crossentropy",
				  metrics=['binary_crossentropy'], )
	for i in range(4):
		print("round "+str(i))
		history = model.fit(train1_model_input, train1[target].values,
							batch_size=10240, epochs=1, verbose=2, validation_split=0.1, )
							#batch_size = 10240 or 5120
							
		pred_ans = model.predict(train2_model_input, batch_size=20480)
		print("test LogLoss", round(log_loss(train2[target].values, pred_ans), 6))
		print("test AUC", round(roc_auc_score(train2[target].values, pred_ans), 6))
		pred_ans = model.predict(test_model_input, batch_size=20480)
		np.savetxt("new_idea.ans"+str(i),pred_ans,fmt="%.20f")