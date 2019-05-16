# scorer.py
# Ibrahim Rahman
# Due: March 19, 2018
# 
# The problem to be solved in this part (scorer.py) deals with creating a utility program. This utility 
# program is created to take the input that was created in the tagger.py file. This input file is called
# pos-test-with-tags.txt. This file will then need to be compared to the pos-test-key.txt. After comparison
# the program should be able to output the overall accuracy of the tagging done (from tagger.py), and also
# output a confusion matrix. This will be all printed out to pos-tagging-report.txt.
#
# Program input example:
# python3 scorer.py pos-test-with-tags.txt pos-test-key.txt > pos-tagging-report.txt
#   This program input below is the pos-test-with-tags.txt that we outputted from the tagger.py program.   
#  
#   No/NNP ,/, 
#   it/PRP 
#   was/VBD n't/RB Black/NNP Monday/NNP 
#   ./. 
#   But/NNP while/IN 
#   the/DT New/NNP York/NNP Stock/NNP Exchange/NNP 
#   did/VBD n't/RB 
#   fall/NN 
#   apart/NN 
#   Friday/NNP
# 
# Program output example:
#   The program output will be placed in pos-tagging-report.txt. And will look like this:
#   
#   Accuracy: 85.13128255666619 
#
#   Confusion matrix:
#    RB  ,   PRP VBD NNP
#    RB  1571    0   0   0   0
#    ,   0   3070    0   0   0
#    PRP 0   0   1042    0   0
#    VBD 0   0   0   1470    0
#    NNP 0   0   0   0   3441
#
# Description of algorithm:
#   The algorithm implemented in scorer.py has to deal with comparison and creation to help understand what was
# done. The program will also read in/take in two input files. One being the pos-test-with-tags.txt (our created
# file from tagger.py), and the other being pos-test-key.txt (key). The program will then need to parse through
# both files and manipulate the files so they can be compared. The comparison will be done by counting the number
# of correct prediction, and placing the values of this count/correct predictions for the use in the confusion
# matrix and for accuracy calculations. After this, the program will then output/print the values of accuracy and
# confusion matrix to the pos-tagging-report.txt.


#these are the imports i needed to implement my algorithm
import re
import sys
import math
#import numpy as np

#these are the global variables that i created. 
#the comparetest and comparekey are two lists
# the tagdict/confdict are dicts aka the variable i use to implement the idea of hashes
comparetest = []
comparekey = []

tagdict = dict()
confdict = dict()


def accuracycheck():
    #print(comparetag)
    #print('\n \n', comparetest)
    #hold = ((comparetest == comparetag).sum())
    #print(np.count_nonzero(comparetest == comparetag))

    #this section of code is doing the accuracy calculation to print out the accuracy value. 
    #hold is holding the value for the summation (counter) of every time a value from comparetest and compare key are equal
    hold = (sum(1 for x,y in zip(comparetest,comparekey) if x == y))
    length = len(comparetest)
    lengthkey = len (comparekey)
    #print (hold)
    #print(length)
    #print (lengthkey)
    print ("Accuracy:",(hold/length)*100, "\n")

    i = 0;

    #this while loop will will deal with searching the compare key to replace values that caused errors
    #also within this while loop we create new list variables that will contain the comparetest/comparekey lists
    #and split them by the '/' symbol so we can differentiate the word from the tag with either [0] or [1], respectively.
    while i < length:
        if re.search('\\\/', comparekey[i]):
            comparekey[i] = re.sub('\\\/', '-', comparekey[i])
        if re.search('\\\/', comparetest[i]):
            comparetest[i] = re.sub('\\\/', '-', comparetest[i])
        newonewords = comparetest[i].split('/')
        newonewordkeys = comparekey[i].split('/')

        #this section here will deal with searching the new variable lists and deal with words that have two tags. and 
        #choose the first tag to represent the word.

        if re.search(r'\b\|.*', newonewords[1]):
            newonewords[1] = re.sub('|.*', '', newonewords[1])
        if re.search(r'\b\|.*', newonewordkeys[1]):
            newonewordkeys[1] = re.sub('|.*', '', newonewordkeys[1])

        #this section is creating the tagdict/indexing and placing the lists in the correct places in the dict/'hash'
        if newonewordkeys[1] not in tagdict:
            tagdict[newonewordkeys[1]] = {}
        if newonewords[1] not in tagdict[newonewordkeys[1]]:
            tagdict[newonewordkeys[1]][newonewords[1]] = 1
        else:
            tagdict[newonewordkeys[1]][newonewords[1]] += 1

        #print(tagdict)

            #print(newonewords[1])
            #print(newonewordkeys[1])

        #print(comparetest[i])
        #print(comparekey[i] + '\n')

        #error checking below aka in the while loop, to go through each value of each dict
        if newonewords[0] != newonewordkeys[0]:
            print("error")
            exit(0)
        i+=1

    #do the zeros for the confusion matrix


    #this is the section where i am dealing with printing out the confusion matrix, dealing with all the 0's and printing out
    #useful information to be able to understand the confusion matrix
    tagheader = []
    for tagger in tagdict:
        tagheader.append(tagger)
        confdict[tagger] = {}
    for tagger in tagdict:
        for tagval in tagheader:
            confdict[tagger][tagval] = 0;
            grabvals = tagdict[tagger].items()
        for grabval, count in grabvals:
            confdict[tagger][grabval] += count
    out = "\t"
    for vals in tagheader:
        out = out + vals + "\t"
    print(out)
    for keyvals in confdict:
        outt = keyvals + "\t"
        words = confdict[keyvals].items()
        for vals, count in words:
            outt = outt +str(count) + "\t"
        print(outt)


    for conf in tagdict:
        #print (conf)
        string = ""
        string += conf + "\t"
        tag = tagdict[conf].items()
        for value, count in tag:
            string += str(count) + "\t"
        #print(string)

    #print (tagdict)


# this function compare was created to read both the test file w/ tags we created and the key test file.
# it will deal with making sure that the files read in are placed in correct variables and make sure that 
# the formats of both files are the same. so then we can compare it in the funciton 'accuracycheck'
def compare(testtags, testkey):
    #print('test')
    with open (testkey, 'r') as input:
        for readtestkey in input:
            readtestkey = re.sub('[\[\]]', '', readtestkey)
            readtestkey = readtestkey.strip()
            keyposwords = readtestkey.split(' ')
            #print (readtestkey)
            for keyposword in keyposwords:
                if keyposword is not '':
                    splitterkey = keyposword.split(' ')
                    comparekey.append(keyposword)
        #print(comparetag)

    with open (testtags, 'r') as input:
        for readtesttags in input:
            readtesttags = re.sub('[\[\]]', '', readtesttags)
            readtesttags = readtesttags.strip()
            tagposwords = readtesttags.split(' ')
            #print (readtesttags)
            for tagposword in tagposwords:
                if tagposword is not '':
                    splitterkey = tagposword.split(' ')
                    comparetest.append(tagposword)
        #print(comparetest)

    accuracycheck()

#this is the main function which just deals with reading in the commandline arg, dealing with the names of both files
# and then goes to the compare function (where the files will be set up for comparison)
def main():
    #python3 scorer.py pos-test-with-tags.txt pos-test-key.txt > pos-tagging-report.txt

    testtags = sys.argv[1]
    testkey = sys.argv[2]

    compare(testtags, testkey)



main()
