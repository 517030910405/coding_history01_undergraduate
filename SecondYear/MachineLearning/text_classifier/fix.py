import batch2
import tensorflow as tf
import numpy as np
Batch = batch2.batch()
#Batch.split_test()
Batch.load2()
import tensorflow as tf
import numpy as np
Batch = batch2.batch()
#Batch.split_test()
Batch.load2()
def ans_fc(y1,y2):
    y1 = np.array(y1)
    y2 = np.array(y2)
    assert y1.shape == y2.shape
    yy1 = np.argmax(y1, axis = 1)
    yy2 = np.argmax(y2, axis = 1)
    ans = np.mean(np.equal(yy1,yy2))
    return ans
arr = np.zeros((1000,2))
    
for i in range(100):
    ar1 = np.array(np.loadtxt("./run_env/new"+str(i)+".txt"))
    arr = arr + ar1
lab = Batch.test2_label(1000)
arr = np.array(arr)

ans = ans_fc(arr,lab[0])
print(ans)