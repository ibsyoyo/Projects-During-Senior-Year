# 1) Overall comment at start of program – a clear and concise introduction that has three
# components : 1) describe the problem to be solved well enough so that someone not
# familiar with our class could understand, 2) give actual examples of program input and
# output, along with usage instructions, and 3) describe the algorithm you have used to solve
# the problem, specified in a stepwise or point by point fashion. Your introduction should also
# include identifying information (your name, date, etc.) 
# 
# Name: Ibrahim Rahman
# Due Date: April 30th, 2019
#
# The problem to be solved in this NLP assignment, is to create a Question Answering system. This system
# will be able to answer "Who/What/When/Where" questions, but not why or how. The answers will be in complete
# answers and should be able to answer the specific question asked. This time around we want shorter and concise answers.
# With new rules to help get better quality results.
# 
# Program Input (command line):
# 	#python3 qa-system.py mylogfile.txt
#
#
# Program Input (after running program):
# Who is C3PO
# what is eid
# Where is Rome

# Program Output (respective order from above):
# C3PO  or SeeThreepio is a humanoid robot character from the Star Wars franchise who appears in the 
# original trilogy the prequel trilogy and the sequel trilogy
#
# Eid Mubarak or  is an Arabic term that means happy holiday
# 
# Rome  is the capital city and a special comune of Italy 
#

# 
# Algorithm:
# # The algorithm for this program was to create a main class. This main class would take in the command line arguments.
# It then would open the mylogfile.txt and then ask to take an user input. It would then split the user input and write
# the input into the text file but also check if it said exit, to exit the program. If it wasn't exit, it would then
# search wikipedia with the wikipedia api with the user input. Afterwards I tried to implement some regex's that deal with
# creating more concise and to the point answers.


#these are the imports i used for this program
import wikipedia
import sys
import re


#this function opens the logfile and prints the information to logfile and console and even does the search queries
def ask(logfile):
	#printlogfile = open(logfile, "w")

#this opens the logfile, so then we can append the log file and print out the questions, context, and answers.
	with open(logfile, 'a') as printlogfile:
		#printlogfile.write("test")
		print("\nThis is a QA system by Ibrahim Rahman. \nIt will try to answer questions that start with 'Who', 'What', 'When' or 'Where'. \nType 'exit' to leave the program.")
		

# this while statement checks the user input and ends when the user types in 'exit'
		while(1):
			userinput = input("\nType a question or type 'exit': ")
			splituser = userinput.split()
			#print(splituser)
			printlogfile.write("user question/input: \t" +str(userinput) + "\n\n")

			if userinput == 'exit':
				print("Thank you! Goodbye.")
				exit()
			# if userinput not in wikipedia.summary(userinput):
			# 	print("sorry")
			# else:
			# 	check = wikipedia.exceptions.WikipediaException(error)
			# 	print("wikipedia exception error...???")


			#search = wikipedia.search(userinput)
			#search = re.sub(r'\([^)]*\)', '', str(search))
	#this is where i take the user input and search through the wikipedia api and grab the summary context and split by periods		
			wikipedia.set_lang("en")
			search = wikipedia.summary(userinput)
			search = re.split("\.", search)
			#these new lines below gets rid of extra verbage and handles creating a smaller output
			searchtest = re.sub(r'\([^)]*\)', '', search[0])
			searchtest = s = re.sub(r'[^\w\s]','', searchtest)
								#search = re.sub("\([^\)]*\)", '', str(search))
			#search = re.sub(r'[\[\]]', '', str(search))
			#"\\(.+?\\)"

			#this is where I tried to implement regex's and also above to deal with different questions and how to answer
			# i will have to heavily update this section, even though i passed most test cases today in class
				
			if re.search('(W|w)ho|(W|w)hat|(W|w)here|(W|w)hen', splituser[0]):
				#print("\n"+ splituser[2] + " "+ splituser[1] + searchword[5])
				#print("\n"+search[0])
				if re.search('(I|i)s', splituser[1]):
					
					print("\n"+ searchtest)
					
				else:
					#print("\n else statement"+ searchtest)
					print("\n"+searchtest)

				# if re.search('(I|i)s', splituser[1]):
					
				# 	after = searchtest.split("is",1)[1]
				# 	print ("\n"+ splituser[2] + " " + splituser[1] + after)
				# 	#print(search[0])
				# else:
				# 	print("\n"+ searchtest)
				# 	#print(search[0])


				

			
#this is where i print to the logfiles, the systems answer and the raw context from wiki
			printlogfile.write("systemanswer: \t" + str(searchtest)+ "\n\n")

			printlogfile.write("raw result from wiki: \t" + str(search)+ "\n\n")
			

#main section where i deal with the arguments and opening and closing the log file...

def main():

	#python3 qa-system.py mylogfile.txt
	logfile = sys.argv[1]

	open(logfile, 'w').close()

	ask(logfile)

main()