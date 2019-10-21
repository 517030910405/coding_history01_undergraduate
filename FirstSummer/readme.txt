This is a deep learning system (platform for machine learning) developed by Li Jiasen as the PPCA Homework
 in order to learn the deep learning and understand how it works. 

It includes BP and auto gradients.
Its gramma is similar to Tensorflow.
There are some examples in dl-system-test-master.zip

Some functions , like conv2d , are implemented by myself ,
 using basic matrix operations. 

There are two versions here ,
 ones is using normal calculation of mat_multi in cpp to speed up the conv2d (extern cpp version) .
 The other is using numpy mat_multi in a smart way , so that the number of matrix multiplies is reduced. 



How to test the code:

Step0:
move the files into the dl-system-test-master

Step1 (if use extern cpp):
g++ -shared con2d.cpp -o con2d.so -fPIC -lpthread -O3

Step2:
python run_test.py your_model
"your_model" can be replaced by the exact name of the flow
