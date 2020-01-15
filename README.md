# Small URL is a Java based url shortener that runs through the console.

## How does it work?
First give the program the full url. The program will insert the url into a postgres table and returns the row ID.
Small URL uses the Base 62 algorithm to encode the rowID to create a shorten url. Using the shorten url, you can retrieve the full url by decoding the shorten url back to a base 10 row id.

## To use:
The program runs from the console or terminal. Compile and run program with db username, db pwd, and dburl as arguements. Console will prompt you to enter your userId as a number (this could be any number). A menu will appear in your console.  Select 1 to create a new small url. Select 2 to retrieve the full url using the small url. Select 3 to edit existing full url. Select 4 to delete short/full url record. Select Q to quit.

![alt text](https://raw.githubusercontent.com/mrchowmein/SmallURL/master/images/menu.png)
