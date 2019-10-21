import os
for i in range(100):
    name_file = open("./run_env/name.txt","w")
    name_file.write("new"+str(i))
    name_file.close()
    os.system("time python3 3.py")

