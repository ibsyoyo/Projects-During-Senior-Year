# ngram.py
#
# Ibrahim Rahman
# Due Date: 2/19/2019
# CMSC 416
#
#   The problem to be solved in this assignment was to design and implement a program that
# would be able to learn an N-gram language model. It would base this language model off
# of plain text files that would be stated in the command line argument. The program then
# will be able to generate a number of sentences (also will be define in command arg line)
# The command line argument will be in the format: ngram.pl n m input-file/s (where n = value
# of n-gram size and then m = desired output amount of generated sentences). This program will
# convert all the plain text file into lower case, and includes punctuation in the mdoels.
#
#
#   Actual examples of program input (feel free to look at log file as well):
#
#   python3 ngram.py 2 10 input.txt input2.txt input3.txt
#   python3 ngram.py 3 5 input.txt input2.txt input3.txt
#
#   Actual examples of program output (feel free to look at log file as well):
#
#   i am going from her on the mysterious poetical veil of joy every morning
# at the first time to send police force consists in the pretty young ?
#
#    won’t do nothing to him but prince andrew understood that the little
# while still to oblige one word her—“as a double kits !
#
#
#   The algorithm used to solve this problem had many checks and was integrated
# with the use of for and if statements. The algorithm used many of these statements
# because the project required large input, and had to parse through the whole file.
# Not only did it parse the whole file, it built a dict() variable throughout this process.
# This dict variable was created to hold every instance a word followed a word/sequence and
# would use a counter to see how many times and everytime a certain sentence was formed (via ngram
# model which would let us know how many preceding words to look at). This helped with the n gram
# probabilities, and helped build a 'new language model'.

import re
import sys
import random

# this is the 'hash table esque f(x)' which is considered a 'dict' in python
dict = dict()

def ngrams(inputfile, size):
    # this with open section open the input file and reads in as input and then we start
    # reading one line at a time in
    with open (inputfile, 'r') as input:
        for readline in input:
            # turn input all into lowercases
            readline = readline.lower()
                    # spacing for !?., so they can be considered tokens and not worrying about ()
                    # (was told to not worry about in class)
            # the start token will hold the start tag
            start = '<start>'
            # for statement that checks i and see if its in range of 0 -> int(size)-2 which is =
            # 0 -> n value -2... where we will increment i by one, whenever we add another start tag to help with
            # the popping off later in generatesentences funciton
            for i in range(0,int(size) - 2):
                start += ' <start>'
                i += 1

            # this below is the regex's used to substitute certain values when read in from readline
            # example of regex: readline = re.sub('abc',  'def', readline)           # Replace pattern abc -> def
            readline = re.sub('\.', ' ', readline)
            readline = re.sub('([!?.])', r' \1 '+ start, readline)
            readline = re.sub('([!?.,])', r' \1 ', readline)
            readline = re.sub('\s{2,}', ' ', readline)
            #this one below replaces the \n
            readline = re.sub('\n', ' ', readline)
            # split sentences into tokens, and remove all the empty ones...
            # the statement below will tokenize all words from the .txt files
            tokens = [tokens for tokens in readline.split(" ") if tokens != '']
                    # The zip() function take iterables (can be zero or more), makes iterator
                    # that aggregates elements based on the iterables passed, and returns an iterator of tuples.#
                    # and instead of return we used print so we can see the values in console
                    # zip(*iterables)


            #this statement below will make sure to keep within the boundaries and takes into consideration the
            #start tag/token
            for i in range(len(tokens) - int(size) + 1):
                        # The join() method is a string method and returns a string in which the
                        # elements of sequence have been joined by str separator.
                #intializing first and last, which will be used for indexing arrays
                last = tokens[i+int(size)-1]
                first = tuple(tokens[i:i + int(size)-1])

                        #print (first)
                        #print(last)
                        #print (hold + "\n\n")

                #this code below will check if value is already in dict and if not it will add
                if first not in dict:
                    dict [first]={}
                    #below is first time seeing last word
                if last not in dict[first]:
                    dict [first][last] = 1
                #increment below, if last is in dict[first]
                else:
                    dict [first][last] += 1

                        # for now it will be containing all the ngram counts as it goes in ngramshash...
                        #dict.setdefault(hold,0)
                        #dict[hold] += 1
                        # increase count by one if
                    #print(dict)

#this function generates the sentences with the info above
def generatesentences(size, ransen):
    for x in range(0,int(ransen)):
        generated = ''
        init = ''
        start = []
        #this is initialzing variables, and below is adding start tag/token to array
        start.append('<start>')
        i = 1
        for i in range(0,int(size) -2):
            start.append('<start>')
            i += 1

        while 1:
            var = 0
            holdtuple = tuple(start[var:var +int(size)])
            #print(holdtuple)
            if holdtuple not in dict:
                #print(holdtuple)
                start = init
                #start.pop(0)

                generated += ' '

                break
            else:
                #print("holdtuple3")
                var2 = 0
                # this is recounting for var so basically probability
                for variabledict in dict[holdtuple]:
                    var2 += dict[holdtuple][variabledict]
                randvar = random.randint(1, var2)
                #print(randvar)
                #print(var2)
                if re.match('([!?.,])', holdtuple[int(size)-2]):
                    #print("holdtuple4")

                    break
                    #ending sentence
                if len(dict[holdtuple]) == 1:
                    #print("holdtuple5")

                    #this is now creating a list to place in start array, because we are dealing with ending of sentence
                    listfirst = list(dict[holdtuple].keys())[0]
                    if listfirst == '()':
                        #print("holdtuple6")

                        generated += ' '
                        break
                    start.pop(0)
                    start.append(listfirst)
                    generated += ' ' + listfirst
                else:
                    #print("holdtuple7")

                    for next in dict[holdtuple]:
                        #print("holdtuple8")

                        if next == '':
                            #print("holdtuple9")

                            continue
                        else:
                            #print("holdtuple10")

                            var += dict[holdtuple][next]
                            if (var >= randvar):
                                #print("holdtuple11")

                                init = start
                                start.pop(0)
                                start.append(next)
                                generated += ' ' + next
                                #print (generated)

                                break


        print ("\n" + generated)


def readinputfile(size):
    thirdval = 3
    # this is set to 3 because after the first two values we need to be able to read x amount of input files
    while(thirdval < len(sys.argv)):
        ngrams(sys.argv[thirdval], size)
        thirdval += 1


def main():
    size = sys.argv[1]
    # this will get the n value for the command line arg: ngram.pl n m
    ransen = sys.argv[2]
    # this will get the m value for the comamnd line arg: ngram.pl n m
    print ("\n\nName: Ibrahim Rahman")
    print ("This program was created to generate random sentences via the Ngram Model.\n\n")
    readinputfile(size)
    generatesentences(size, ransen)
    #print(dict)

main()
