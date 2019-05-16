# Ibrahim Rahman
# decision-list.py
# Date: April 4th, 2019
# V00808101
#
# The problem to be solved in this part (scorer.py) deals with creating a utility program. This utility 
# program is created to take the input that was created in the decision-list.py file. This input file is called
# my-line-answer.txt. This file will then need to be compared to the line-key.txt. After comparison
# the program should be able to output the overall accuracy  and also output a confusion matrix. 
# 
#
# Example of output to console (comes from scorer.py)
# Accuracy: 83.33333333333334

# Confusion Matrix:
#          Phone   Product
# Phone      52       20
# Product    53       1
# Correct prediction of phone: 52
# Incorrect prediction of phone: 20
# Correct prediction of product: 53
# Incorrect prediction of product: 1

# Example of input from my-line-answer.txt:
# <answer instance="line-n.w8_059:8174:" senseid="phone"/>
# <answer instance="line-n.w7_098:12684:" senseid="phone"/>
# <answer instance="line-n.w8_106:13309:" senseid="phone"/>
# <answer instance="line-n.w9_40:10187:" senseid="phone"/>
# <answer instance="line-n.w9_16:217:" senseid="phone"/>
# <answer instance="line-n.w8_119:16927:" senseid="product"/>
# <answer instance="line-n.w8_008:13756:" senseid="phone"/>
# <answer instance="line-n.w8_041:15186:" senseid="phone"/>
# <answer instance="line-n.art7} aphb 05601797:" senseid="phone"/>
# <answer instance="line-n.w8_119:2964:" senseid="product"/>
# <answer instance="line-n.w7_040:13652:" senseid="phone"/>
# <answer instance="line-n.w7_122:2194:" senseid="phone"/>

# Example of input we are comparing with (line-key.txt):
# <answer instance="line-n.w7_098:12684:" senseid="phone"/>
# <answer instance="line-n.w8_106:13309:" senseid="phone"/>
# <answer instance="line-n.w9_40:10187:" senseid="phone"/>
# <answer instance="line-n.w9_16:217:" senseid="phone"/>
# <answer instance="line-n.w8_119:16927:" senseid="product"/>
# <answer instance="line-n.w8_008:13756:" senseid="phone"/>
# <answer instance="line-n.w8_041:15186:" senseid="phone"/>
# <answer instance="line-n.art7} aphb 05601797:" senseid="phone"/>
# <answer instance="line-n.w8_119:2964:" senseid="product"/>
# <answer instance="line-n.w7_040:13652:" senseid="phone"/>
# <answer instance="line-n.w7_122:2194:" senseid="phone"/>
# <answer instance="line-n.art7} aphb 45903907:" senseid="phone"/>
# <answer instance="line-n.art7} aphb 43602625:" senseid="phone"/>
# <answer instance="line-n.w8_034:3995:" senseid="product"/>
# <answer instance="line-n.w8_139:696:" senseid="product"/>


# Description of algorithm:
#   The algorithm implemented in scorer.py has to deal with comparison and creation to help understand what was
# done. The program will also read in/take in two input files. One being the my-line-answer.txt  (our created
# file from decision-list.py), and the other being line-key.txt(key). The program will then need to parse through
# both files and manipulate the files so they can be compared. The comparison will be done by counting the number
# of correct prediction, and placing the values of this count/correct predictions for the use in the confusion
# matrix and for accuracy calculations. After this, the program will then output/print the values of accuracy and
# confusion matrix to the console





#these are the imports i used
import re
import sys
import math

#these are the global lists i used to place my sense for my answers, and the key answers
myans = []
keyans = []


def compare(myanswer, linekey):

	#this section deals with reading both the input files and then searching if its a phone or product and then placing in list
	
	with open (linekey, 'r') as input:
		for readline in input:
			if re.search("phone", readline):
				#print("phone")
				keyans.append("phone")
			else:
				keyans.append("product")

	#print(keyans)

	with open (myanswer, 'r') as input:
		for readline in input:
			if re.search("phone", readline):
				#print("phone")
				myans.append("phone")
			else:
				myans.append("product")	

	#print(keyans)
	# print(myans)

	#print(len(myans))
	correct = 0
	incorrect = 0
	corphone = 0
	incphone =0
	corproduct = 0
	incproduct = 0

#this section deals with going through key and checking if the comparisons between both files are true or not, which gives us accuracy
# and also gives the confusion matrix

	for i in range(0,len(keyans)):
		if keyans[i] == myans[i]:
			correct = correct +1
			if keyans[i] == "phone":
				corphone = corphone +1
			else:
				corproduct = corproduct +1
		else:
			incorrect = incorrect +1
			if keyans[i] == "phone":
				incphone = incphone +1
			else:
				incproduct = incproduct +1

	total = correct + incorrect
	#print(total)
	acc = (correct/total)*100


#this part prints out accuracy and the confusion matrix to the console
	print("\nAccuracy: "+ str(acc))

	print("\nConfusion Matrix:")

	print("\t Phone \t Product")
	print("Phone \t   " + str(corphone) + "\t    " + str(incphone))
	print("Product    " + str(corproduct) + "\t    " + str(incproduct))



	print("Correct prediction of phone: " + str(corphone))
	print("Incorrect prediction of phone: " + str(incphone))

	print("Correct prediction of product: " + str(corproduct))			
	print("Incorrect prediction of product: " + str(incproduct))

				



	# while line1 != '' or line2 != '':
	# 	line1 = line1.rstrip()
	# 	line2 = line2.rstrip()

	# 	if line1 == line2:
	# 		correct += 1
	# 	else:
	# 		incorrect += 1

	# line1 = file1.readline()
	# line2 = file2.readline()
	# print(line1)

	# linecount += 1

	# print(correct)

		#print(line1)



	#This is the main section of the program, that handles the command line argument which is in the form of:
##python3 scorer.py my-line-answers.txt line-key.txt
#Then after validating, it will hop to compare function	


def main():
	#python3 scorer.py my-line-answers.txt line-key.txt



    myanswer = sys.argv[1]
    linekey = sys.argv[2]

    compare(myanswer, linekey)

    #print("test")



main()
