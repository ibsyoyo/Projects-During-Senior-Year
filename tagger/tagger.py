# tagger.py
# Ibrahim Rahman
# Due: March 19, 2018
# 
# The problem to be solved in this part (tagger.py) deals with taking a training file that contains
# part of speech tagged text. The other file that is used for this part is a file containing text that
# will need to be part of speech tagged. This program deals with implementing the 'most likely tag' 
# baseline. What needs to be done is to take the training data, assign POS tags that maximise p(tag|word)
# for the test data. Any word that is found in the test data and not in the training data (unkown word) 
# will automatically assign the word to a NN tag. After assigning all the values/tags to each word in the
# test data we will then calculate the accuracy (of the most likely tagger on a given test file). After this
# point I had to create 5 rules to the tagger, to see how they affected the accuracy.
# 
#
# Program input example:
#   For the command line argument:
#       python3 tagger.py pos-train.txt pos-test.txt > pos-test-with-tags.txt
#       This command line argument states the program name, which is to be run. pos-train.txt which will be used
#       as the training data (gives us the tags for specific words). The pos-test.txt will deal with what we will
#       assign tags to. And the '>' sign lets the console know to assign values to the pos-test.txt but to output it
#       to pos-test-wth-tags.txt.
#   Example of train data:
#        [ Pierre/NNP Vinken/NNP ]
#        ,/, 
#        [ 61/CD years/NNS ]
#        old/JJ ,/, will/MD join/VB 
#        [ the/DT board/NN ]
#        as/IN 
#        [ a/DT nonexecutive/JJ director/NN Nov./NNP 29/CD ]
#   Example of test data:
#         No , 
#        [ it ]
#        [ was n't Black Monday ]
#         . 
#         But while 
#         [ the New York Stock Exchange ]
#         did n't 
#         [ fall ]  
# 
# Program output example:
#   For the program output we sent out the test data with tags (that were decided through training set) to the file name
# pos-test-wth-tags.txt. Here is an example of the output in that file:
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
# Description of algorithm:
#   This algorithm, for this tagger.py portion of the project, deals with takin in two files. The two files being, the train
# and test file. What needs to be done next is parsing through each line in the training file to create a tagging model.
# This model is created by creating word/tag pairs and counting the frequency of each word/tag pair. After this is all done,
# the 'most likey tag' for each word is then chosen (from looking at which tag was associated with the highest frequency with
# a given word). After choosing this most likely tag, the program then needs to apply the tag to the test data set.
#
# Baseline accuracy: 84.32704491060116
# After implementing 5 rules accuracy: 85.13128255666619 
# 
 





#these are the imports i needed to implement my algorithm

import re
import sys

#this the dict and only global variable that was needed to hold my information for this tagger.py
traindict = dict()
#tokens = []


# def findtagval(train, test):
#     #print (traindict)
#     with open(test, 'r') as input:
#         for readtest in input:
#             readtest = re.sub('[\[\]]', ' ', readtest)
#             readtest = readtest.strip()
#             testposwords = readtest.split(' ')
#             #print (testposwords)
#             for testposword in testposwords:
#                 if testposword is not '':
#                     splitter = testposword.split(' ')
#                     #print(splitter)
                

#this function 'readtrain' is poorly named. not only does it read the in the training data file, but also the test file
# the opening of the training file deals with setting up the training data to be formatted and split correctly. and also 
# deals with creating a splitter variable that helps split the words and also place them into the global variable dict
# this global variable traindict will hold the values. the splitter variable makes the words split from the tag and helps with
# assigning tags for the test set later.
def readtrain(train, test):
    with open (train, 'r') as input:
        for readline in input:
            readline = re.sub('[\[\]]', '', readline)

            readline = readline.strip()

            poswords = readline.split(' ')

            for posword in poswords:
                if posword is not '':
                    splitter = posword.split('/')

                if splitter[0] not in traindict:
                    traindict[splitter[0]] = {}
                    #print (splitter[0])

                if splitter[1] not in traindict[splitter[0]]:
                    traindict[splitter[0]][splitter[1]] = 1

                else:
                    traindict[splitter[0]][splitter[1]] += 1


    #this open section opens the test data set, which we are to assign tags to and also deals with formatting the test data set.
    # we also deal with max probability, and holding string values to print out to the stdout file.
    #this section also deals with specific cases like \n, empty strings, and etc. 
    # it also deals with the 6 rules that i created/implemented for this pos tagger model.
    # and after the rules this section implements the algorithm to find the most likey tag, and prints the word with the tag to 
    # the stdout. 
    with open (test, 'r') as input:
        for readtest in input:
            readtest = re.sub('[\[\]]', '', readtest)

            testhold = ''
            readtest = readtest.strip()
            testposwords = readtest.split(' ')
            for testposword in testposwords:
                #print (testposword)
                maxprob = 0
                holdprob = ''
                if testposword == '\n':
                    pass                    
                elif testposword == ' ':
                    pass    
                elif testposword == '':
                    pass
                #RULE #1
                elif re.search('[0-9]', testposword):
                    testhold = testhold + testposword + "/CD" + " "
                #RULE #2
                elif re.search('.*ing', testposword):
                    testhold = testhold + testposword + "/VBG" + " "
                #RULE #3
                elif re.search('[A-Z].*', testposword):
                    testhold = testhold + testposword + "/NNP" + " "
                #RULE #4
                elif re.search('.*ed', testposword):
                    testhold = testhold + testposword + "/VBD" + " "
                #elif re.search('is', testposword):
                #    testhold = testhold + testposword + "/VBZ" + " "
                #RULE #5
                elif re.search('million|thousand|hundred', testposword):
                    testhold = testhold + testposword + "/CD" + " "
                #RULE #6
                elif re.search('through|about', testposword):
                    testhold = testhold + testposword + "/IN" + " "
               
                else:    
                    if testposword not in traindict:
                        holdprob = 'NN'
                    else:
                        mostlikelytag = traindict[testposword].items()
                        #print (mostlikelytag)

                        for postokenizingtag, count in mostlikelytag:
                            if count > maxprob:
                                maxprob = count
                                holdprob = postokenizingtag
                    adding = testposword + '/' + holdprob
                    testhold = testhold + adding + " " 
            print (testhold)


            #readtest = re.sub('[\[\]]', ' ', readtest)
            #readtest = readtest.strip()
            #testposwords = readtest.split(' ')
            #print (testposwords)
            #for testposword in testposwords:
            #    if testposword is not '':
            #        splittertwo = testposword.split(' ')
            #        print(splittertwo)
                #if splitter[0] not in splittertwo:
                #    print(splitter)
                #if splitter[1] not in splittertwo[0]:
                #    print(splitter)

                #else:
                #    print("we hurr3")


            #readline = re.sub('\n', ' ', readline)
            #readline = re.split('/\//', readline)
            #print (" we here: " +readline)

            #tokens = [tokens for tokens in readline.split(" ") if tokens != '']




# for line in file:
# line = re.sub('[\[\]]', "", line)
# line = line.strip()
# words = line.split(" ")
# for word in words:
# if word is not "":
# split = word.split("/")
# if split[0] not in posDict:
# posDict[split[0]] = {}
# if split[1] not in posDict[split[0]]:
# posDict[split[0]][split[1]] = 1
# else:
# posDict[split[0]][split[1]] += 1



#this is the main method which deals with reading in the command line args. The command line args deal with reading in the 
#input files, which are tagger.py pos-train.txt and pos-test.txt. And then goes to 'readtrain' function.
def main():
    #python3 tagger.py pos-train.txt pos-test.txt > pos-test-wth-tags.txt

    train = sys.argv[1]
    test = sys.argv[2]

    readtrain(train, test)
    #findtagval(train, test);





main()
