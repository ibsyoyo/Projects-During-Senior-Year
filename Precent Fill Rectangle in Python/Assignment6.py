# Ibrahim Rahman
# CMSC 403: Zachary Whitten 
# Due: 9:30 AM April 25th

# Tkinter is a Python binding to the Tk GUI toolkit. 
# It is the standard Python interface to the Tk GUI toolkit
from tkinter import *
# pip3 install rectpack
# Rectpack is a collection of heuristic algorithms for solving the 2D 
# knapsack problem, also known as the bin packing problem. In essence 
# packing a set of rectangles into the smallest number of bins.
from rectpack import newPacker
# imported for sys.arg which deals with the command line argument, which
# helps us navigate which text file to open.
import sys

# this is creating an instance of Tk. It will initialize the interpreter and creates
# the root window
root = Tk()
#holds variables in a list, the variables being rectangle objects.
listofrect = []	
# this initializes a new rectangle list that will hold the values of the rectangles
newreclist = []	
packer = newPacker()	


# The pack function will have two parameters, allRect and canvasSize. allRect will be a list
# of Rectangle objects and canvasSize a tuple containing a canvas’ height and width (in that order).
# pack will take the given list of rectangles and determine a location for each rectangle so that each
# rectangle does not overlap another and each rectangle exists within the given canvas size. pack
# will then return a list of placed Rectangle objects. Each given rectangle must be included in the
# returned list. (Note: Each given rectangle is referring to the logical concept of the rectangle shape
# not the specific Rectangle object. Two Rectangle objects are logically equivalent if they have the
# same height and width. When generating the list of Rectangles to return, you can modify the
# given Rectangle objects or create new, but logically equivalent, Rectangle objects) 

# def pack has two parameters allRect and canvasSize... which are the rectangle values (first parameter)
# and the tuple of the size of the canvas (height and width format) (second parameter)
def pack(allRect, canvasSize): 		

	#adds the rectangles to packing queue
	for individualrect in allRect:
		packer.add_rect(*individualrect)

# bins is what will carry the packing of the rectangles list and we initialize it with the canvas size
	bins = [canvasSize]

#add the bins where the rectangles will be placed
	for rectangles in bins:
		packer.add_bin(*rectangles)
	#start packing process
	packer.pack()
	for bin in packer:
		for individualrect in bin:
			binx = individualrect.x
			width = individualrect.width

			biny = individualrect.y
			height = individualrect.height
			#creates newRect from class Rectangle (matches parameters)
			newRect = Rectangle(height, width, binx, biny)
			#adds newRect into a list
			newreclist.append(newRect)

	return newreclist




# Rectangle will have a constructor which takes four explicit parameters, height, width, x,
# and y. All four arguments are expected to be of type int. x and y represent the origin point of the
# given rectangle. The origin point will be in the upper left-hand corner of a canvas. Parameters x
# and y should have default values of zero. The values received as parameters should be stored in
# instance variables of the same name. 
class Rectangle:
	#constructor
	def __init__(self, height, width, x , y):
		self.width = width
		self.x = x

		self.height = height
		self.y = y

	def origin (self, x, y):
		self.x = x
		self.y = y



# CustomCanvas will have a constructor which takes two explicit arguments, height and
# width, which are expected to be of type int. The constructor will create and display a new Canvas
# object with the height and width provided. To do this, you will use Canvas, a built-in object
# defined in the python package tkinter. 
class CustomCanvas:
	#constructor
	def __init__(self, height, width):
		self.height = height
		self.width = width

		self.window = Canvas(height = self.height, width = self.width, bg="salmon")
#loop
	def display(self):
	#start packing process
		self.window.pack()
		#loop starts here
		root.mainloop()

# The main function will read in a filepath as a command line argument. You may assume
# the filepath is always the second command line argument given (the first being the name of the
# class being executed) The given filepath will point to a txt file containing a canvas size and
# rectangles. The first line of the given text file will contain two int’s separated by a comma.
# These int’s represent a canvas’ height and width (in that order) All following lines represent the
# height and width of an individual rectangle. main should parse the data in the file and use the 
# information to create a new CustomCanvas object and a new list of Rectangles. Once generated,
# the list of Rectangles and the size of the canvas should be passed to the pack function. Main
# should then print each Rectangle contained in the retuned list to the instantiated CustomCanvas
# object. Each printed Rectangle should have a black border and a colored (not black or white)
# fill. Main should be called whenever Assignment6.py is run as a stand-alone file but not when
# Assignment6.py is loaded as a library.

#filepath = sys.argv[1] which is the command line arg for which text file to open/read
def main(filepath):	
	lineindex = 0
	#opens the text document from the commandline arg aka text file
	with open(filepath, 'r', encoding='utf-8') as file:
		for newline in file:
			height , width = (newline.split(","))
			#knows that first line always deals with canvas size
			if lineindex == 0:
				CH = int(height)
				CW = int(width)

				canvasSize = CH,CW

			#deals with the rectangle objects after the first line..
			else:
				recth = int(height)
				rectw = int(width)

				listofrect.append([recth, rectw])
				#lineindex = lineindex + 1

			# increases index after its done with the line
			lineindex = lineindex + 1
	#packrectlist now equals the return from class pack
	packrectlist = pack(listofrect, canvasSize)
	representcanvas = CustomCanvas(CH, CW)

	#creates the rectangles in the window of the correct sized canvas
	for individualrect in packrectlist:
		representcanvas.window.create_rectangle(individualrect.x, individualrect.y, individualrect.x + individualrect.width, individualrect.y + individualrect.height, fill='deepskyblue')
	#displaycanvas
	representcanvas.display()

if __name__ == '__main__':
    main(sys.argv[1])