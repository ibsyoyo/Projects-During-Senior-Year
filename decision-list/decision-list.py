# Ibrahim Rahman
# decision-list.py
# Date: April 4th, 2019
# V00808101
# 
# The problem to be solved in the programming assignment had to deal with creating a program that would
# implement a decision list classifier, which would help perform word sense disambiguation.
# The program was to test our skills on how to create features to help create an accurate classifer.
# The features were identified and chosen from the training data set. The features that I used came from bag of words,
# Bag of words is simple representation to be used for IR, where there is a 'bag' AKA the 'context' from the dataset.
# After choosing these features we then would have to go to the test data set and label the 'senses' for the test data.
# 
# Program input example (command line):
# python3 decision-list.py line-train.txt line-test.txt my-decision-list.txt > my-line-answers.txt
# 
# Program input example of train data (line-train.txt):
# <corpus lang="en">
# <lexelt item="line-n">
# <instance id="line-n.w9_10:6830:">
# <answer instance="line-n.w9_10:6830:" senseid="phone"/>
# <context>
#  <s> The New York plan froze basic rates, offered no protection to Nynex against an economic downturn that sharply cut demand and didn't offer flexible pricing. </s> <@> <s> In contrast, the California economy is booming, with 4.5% access <head>line</head> growth in the past year. </s> 
# </context>
# </instance>
# 
# Program input example of test data (this is given....line-tes.txt):
# <corpus lang="en">
# <lexelt item="line-n">
# <instance id="line-n.w8_059:8174:">
# <context>
#  <s> Advanced Micro Devices Inc., Sunnyvale, Calif., and Siemens AG of West Germany said they agreed to jointly develop, manufacture and market microchips for data communications and telecommunications with an emphasis on the integrated services digital network. </s> <@> </p> <@> <p> <@> <s> The integrated services digital network, or ISDN, is an international standard used to transmit voice, data, graphics and video images over telephone <head>lines</head> . </s> 
# </context>
# </instance>
# 
# Program output from decision list (comes from cmnd line...my-decision-list.txt):
# Feature: telephone	 	Log Val: 4.382026634673881		 Sense: PHONE 
# Feature: machines		Log Val: 4.044522437723423		 Sense: PRODUCT 
# Feature: sales		Log Val: 3.772588722239781		 Sense: PRODUCT 
# Feature: introduced		Log Val: 3.772588722239781		 Sense: PRODUCT 
# Feature: car		Log Val: 3.70805020110221		 Sense: PRODUCT 
# Feature: product		Log Val: 3.639057329615259		 Sense: PRODUCT 
# Feature: ibms		Log Val: 3.639057329615259		 Sense: PRODUCT 
# Feature: plans		Log Val: 3.5649493574615367		 Sense: PRODUCT 
# Feature: brands		Log Val: 3.5649493574615367		 Sense: PRODUCT 
# Feature: cars		Log Val: 3.4849066497880004		 Sense: PRODUCT 
#
# Program output from my answers (comes from cmnd line...my-line-answers.txt)
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

# Description of algorithm:
# The program takes in the training data as an input and then implements the bag of words algorithm. 
# The command line will take all three arguments and validate them. And after that this program starts with
# taking the training data and finds the 'sense' value. Whether it being phone/product. And from that point it will
# take all the context words that define this label/sense value. And place them in a dict (either phonedict/productdict).
# After this I placed all the words in a allwords list which stored both phone and product features. With this allwords list
# I parsed through each word to see if they were in either phone or product dict, and then did the math calculations to find
# logvalues of each word and place the correct log values to the according features. And placed these values into a dict called
# 'logdict'. I then sorted the logdict, so it would be descending values for the logvalues. I sorted these values and placed them in
# lists dealing with either phone or product. After that I created a text file to place my decision list, going in descending order.
# In this decisoin list, it contained the feature name, the log value, and the label/sense that was assigned to that context. I then 
# opened my test data set and used my decision list to assign values to context (which had no sense/label), by looping through the context
# and my decision list. The highest logval from the context would make the test data set get labeled as whatever feature had the highest
# log val to be the label.
# 


#these are the imports that i used to finish this project

import sys
from bs4 import BeautifulSoup
import re
import operator
import math
#these are the global variables that i used for this project
phonedict = dict()
productdict = dict()
logdict = dict()
sortedmerge = []

def createfeat(train, test, mydeclist):

	#this section deals with opening train, looking for the sense id, and taking the context value (for both phone and product)
	#It then places each word from the context in the appropriate dict
	with open (train, 'r') as input:
		for readline in input:
			inputsoup = BeautifulSoup(input, 'lxml')
			#print(inputsoup)			
			for instance in inputsoup.find_all('instance'):

				if instance.answer['senseid'] == 'phone':
					for context in instance.find_all('s'):
						context = context.string
						context = re.sub('\n|[^a-zA-Z0-9\s]', '', context)
						context = re.sub('\s+', ' ', context).strip()
						context = context.lower()
						splitcontext = context.split()
						#print(context)
						for word in splitcontext:
							if word not in phonedict:
								phonedict[word] = 1
							else:
								phonedict[word] += 1
					#print(phonedict)
				else:
					for context in instance.find_all('s'):
						context = context.string
						context = re.sub('\n|[^a-zA-Z0-9\s]', '', context)
						context = re.sub('\s+', ' ', context).strip()
						context = context.lower()
						splitcontext = context.split()
						for word in splitcontext:
							if word not in productdict:
								productdict[word] = 1
							else:
								productdict[word] += 1
						
					#print(productdict)

					#this part deals with me sorting the phone dict and product dict in the correct format
			sortedphone = sorted(phonedict.items(), key = operator.itemgetter(1), reverse = True)

			sortedproduct = sorted(productdict.items(), key = operator.itemgetter(1), reverse = True)
			#print(sortedphone)
			# print(sortedproduct)
		#logval(inputsoup)
			phonecount = 0
			productcount = 0
			allwords = []

			#this is checking if word in phone/product dict is in the allwords list. And places the values in allwords list.

			for word in phonedict:
				if word not in allwords:
					allwords.append(word)
				else:
					pass
			for word in productdict:
				if word not in allwords:
					allwords.append(word)
				else:
					pass

					#this is where i create the logdict that deals with phone and product separately
			logdict["phone"] ={}
			logdict["product"] = {}


#this part helped me figure out my baseline accuracy so i could see what I was doing, and see if my accuracy went up.
#it also told me that if there was no word in the test context that it should be considered default (by whichever comes up more: phone/product)
			phonesense = len(inputsoup.find_all(senseid="phone"))
			productsense = len(inputsoup.find_all(senseid="product"))
			if phonesense > productsense:
				default = "phone"
			else:
				default = "product"
			#print(default)



#this goes through each word in all words, and checks if it is in phone dict/product and also deals with smoothing

			for word in allwords:
				if word in phonedict and word in productdict:
					sensephone = phonedict[word]/(phonedict[word]+productdict[word])
					senseproduct = productdict[word]/(phonedict[word]+productdict[word])
				elif word in phonedict:
					sensephone = phonedict[word]/(phonedict[word]+1)
					senseproduct = 1/(phonedict[word] +1)
				else:
					senseproduct = productdict[word]/(productdict[word]+1)
					sensephone = 1/(productdict[word] +1)

					#this is the log calcualtion that i use to give log values to features

				phonelog = math.log(sensephone/senseproduct)
				productlog = 1-phonelog


				if phonelog > productlog:
					logdict["phone"][word] =  phonelog
				else:
					logdict["product"][word] = productlog

			sortedphonelog = sorted(logdict["phone"].items(), key = operator.itemgetter(1), reverse = True)
			sortedproductlog = sorted(logdict["product"].items(), key = operator.itemgetter(1), reverse = True)

			#sorteddict = sorted(logdict.items(), key = lambda x : x[1])
		
		
		#print(logdict)
		#print(sortedphonelog)
		#print(sortedproductlog)

		#this is when i merged my lists of sorted phone log and sortedproduct log, and then also sorted them

		mergedlist = sortedphonelog + sortedproductlog
		sortedmerge = sorted(mergedlist, key = operator.itemgetter(1), reverse = True)
		#print(mergedlist)


#this is the part where i start to write to my decision list, in the format of feature, log val, and then sense.

		printdec = open(mydeclist, "w")
		with open(mydeclist, 'w') as deccer:
			#printdec.write(str(len(mergedlist)))
			for i in range(0,len(sortedmerge)):
				if sortedmerge[i] in sortedphonelog:
					#splitsort = sortedmerge[i].split(',')
					printdec.write("Feature: " + str(sortedmerge[i][0]) + "\t \t")
					printdec.write("Log Val: " + str(sortedmerge[i][1]))
					phone = "phone"
					sortedmerge[i] = sortedmerge[i] + ("phone",)
					#print(sortedmerge)
					printdec.write("\t\t Sense: PHONE \n")
				else:
					printdec.write("Feature: " + str(sortedmerge[i][0]) + "\t\t")
					printdec.write("Log Val: " + str(sortedmerge[i][1]))
					phone = "product"
					sortedmerge[i] = sortedmerge[i] + ("product",)
					#print(sortedmerge)
					printdec.write("\t\t Sense: PRODUCT \n")
				
		
#this is the section where I look into the test data set and label the test value, by parsing the test data context and comparing the words
#in my sortedmerge, and being able to print out to my answers with the id val and senseid values, so then they would match the key (in terms of how they looked)

		with open (test, 'r') as output:
			for readline in output:
				outputsoup = BeautifulSoup(output, 'lxml')
				#print(outputsoup)
				for instance in outputsoup.find_all('instance'):
					context = instance.find('context').text
					context = re.sub('\n|[^a-zA-Z0-9\s]', '', context)
					context = re.sub('\s+', ' ', context).strip()
					context = context.lower()
					splitcontext = context.split()
					#print(context)
					idval = instance['id']
					#print(splitcontext)

					for i in range(0, len(sortedmerge)):
						if sortedmerge[i][0] in splitcontext:
							print("<answer instance=\"" + str(idval) +"\" senseid=" +"\"" + str(sortedmerge[i][2]) +"\"/>")
							#print(idval)
							#print("Feature: " + str(sortedmerge[i][0]))
							#print("Sense: " + str(sortedmerge[i][2]))
							break

								
#This is the main section of the program, that handles the command line argument which is in the form of:
#python3 decision-list.py line-train.txt line-test.txt my-decision-list.txt > my-line-answers.txt
#Then after validating, it will hop to the creatfeat function.

def main():


	train = sys.argv[1]
	test = sys.argv[2]
	mydeclist = sys.argv[3]

	createfeat(train, test, mydeclist)



main()