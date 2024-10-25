import os
from sys import argv

DIR_FICH = "AndroidProject\\app\\src\\main\\java\\es\\unican\\gasolineras\\activities\\main\\IMainContract.java"

if len(argv) > 1:
    DIR_FICH = argv[1]

if not os.path.exists(DIR_FICH):
    print(f"Error: El achivo \"{DIR_FICH}\" no existe")
    exit(1)

with open(DIR_FICH, 'r') as f:
    data = f.read()

data = data.split("\n")

def transform(x):
    x = x.lstrip().rstrip()
    if "public" in x:
        x = x[7:-1]
        pref = "+ "
    else:
        x = x[8:-1]
        pref = "- "
    if "interface" in x or "class" in x:
        return f"[[ {x} ]]"
    t = x.split(" ")[0]
    if "(" in x:
        x = x[len(t)+1:]
        if t != "void":
            x += " : " + t
    return pref + x
    


p = filter(lambda x: "public" in x or "private" in x, data)
p = map(transform, p)

for i in p:
    print(i)

