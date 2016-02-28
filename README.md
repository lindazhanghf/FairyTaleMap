# FairyTaleMap

This program was created using Processing, a Java-based programming
language to program images, animation, and interactivity. http:/www.processing.org

A data visualization project that parse the data collected via a search query
to the Digital Public Library of America and visualize using a world map.
The data were categorized by the animals that appeared in the fairy tales 
and the country that the fairy tale is from.

I am using a nested hash table as the data structure for storing the data, 
I got a list of common animals from Wikipedia and saved them in a csv file. 
The program will generate search queries with the key word “fairy tale” 
and animals. The results will be grouped by locations, put into the first 
hash table using country name as the key, and then be grouped by species, 
put into the second hash table.

After parsing the data from the search query, the program will search for 
images of the animals on DPLA or on a list of URLs I found online. The reason 
I search for all the images manually is because the images that are found 
on DPLA are either unrelated to the subject or having a bad image quality. 

Navigating through the program is very straightforward: the small thumbnail 
image shows the animal with the max number of results found for that country, 
and hovering the mouse over the thumbnail will display a larger scale of that 
image; clicking on any country with green color will bring up a table containing 
the detailed information found for each country, organized by animals; clicking 
on any box with a title that interests you will bring you directly to the original 
website of that story.
