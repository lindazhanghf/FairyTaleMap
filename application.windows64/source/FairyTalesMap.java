import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import de.fhpotsdam.unfolding.*; 
import de.fhpotsdam.unfolding.data.*; 
import de.fhpotsdam.unfolding.geo.*; 
import de.fhpotsdam.unfolding.marker.*; 
import de.fhpotsdam.unfolding.utils.*; 
import java.util.List; 
import java.util.LinkedList; 
import java.util.Map.Entry; 
import de.fhpotsdam.unfolding.*; 
import java.util.LinkedList; 
import java.util.Map.Entry; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class FairyTalesMap extends PApplet {








HashMap<String, HashMap> allMaps;
public UnfoldingMap map;
List<Marker> countryMarkers;
List<Feature> countries;
MarkerMaker marks;

public void setup() {
  size(1000, 700);
  smooth();
  //background(137, 200, 225);
  loadData();
}

public void draw() {
  map.draw();
  marks.draw();
//  Location location = map.getLocation(mouseX, mouseY);
//  text(location.getLat() + ", " + location.getLon(), mouseX, mouseY + 10);
  fill(0);
//  text(mouseX + ", " + mouseY, mouseX, mouseY);
  marks.checkOver();

}

public void mouseClicked(){
      marks.mouseClicked();
}


public void loadData() {    
  //Search using the csv files for lists of creatures-----//
  String[] animalsListRaw = loadStrings("data/animalList.csv");
  //String[] animalsListRaw = loadStrings("data/mythical.creatures.csv");
  String[] animalList = getList(animalsListRaw);
  SearchQuery newSearch = new SearchQuery(animalList); 
  allMaps = newSearch.getMap();
  
  map = new UnfoldingMap(this);
  map.setBackgroundColor(color(179, 223, 222, 255));
  map.zoomAndPanTo(width/2, height/2, 2);
  //MapUtils.createDefaultEventDispatcher(this, map);
  countries = GeoJSONReader.loadData(this, "countries.geo.json");
  marks = new MarkerMaker(allMaps, countries);
  map.draw();
}

//void mouseClicked(){
//  marks.mouseClicked();
//}

//get the list of String animals from csv
public String[] getList (String[] lines) {
  String[] list = new String[lines.length];

  for (int i=0; i < lines.length; i++) {
    String[] csvSplit=split(lines[i], ',');
    list[i] = csvSplit[0];
  }
  return list;
}

public float[] getFloatList (String[] lines) {
  float[] list = new float[lines.length];

  for (int i = 0; i < lines.length; i++) {
    list[i] = Float.parseFloat(lines[i]);
    //println(i + ": " + list[i]);
  } 
  return list;
}



public class AnimalList implements Comparable<AnimalList> {
  private LinkedList<DplaItem> list = new LinkedList();
  private String animalName; //name of animal
  private String imgUrl;

  public AnimalList(String name) {
    animalName = name;
    //Searching for url on dp.la
  }  
  
  public void getImageCSV() { 
    String[] lines = loadStrings("data/animal.urls.csv");
    for (int i = 0; i < lines.length; i++) {
       String[] csvSplit = split(lines[i], ',');
       if (animalName.equals(csvSplit[0])) {
         imgUrl = csvSplit[1];
         break;
       } else {
        imgUrl = "NA"; 
       }    
    } //for    
    //println(animalName + " image: " + imgUrl);
  }
  
  public void searchImage(String countryName){
    SearchQuery newSearch = new SearchQuery();

    try {
      countryName = countryName.replace(' ', '+');
      JSONArray searchResult = newSearch.search(animalName + "*+" + countryName);
      JSONObject o = searchResult.getJSONObject(0);
      imgUrl = o.getString("object") + "?api_key=" + apikey;
    } catch (Exception e) {
      try {
        JSONArray searchResult = newSearch.search(animalName+"*+animal*");
        JSONObject o = searchResult.getJSONObject(0);
        imgUrl = o.getString("object") + "?api_key=" + apikey;
      } catch (Exception f) {
        imgUrl = "NA";
      }
    }
  }

  public String getImageUrl() {
    return imgUrl;
  }

  public void add(DplaItem item) {
     list.add(item);    
  }
  
  public String getAnimalName() {
      return animalName;
  }

  public int getSize() {
    return list.size();
  }
  
  public List<DplaItem> getList() {
    return list;
  }

  public int compareTo(AnimalList other) {
    if (this.getSize() > other.getSize())
      return 1;
    else if (this.getSize() < other.getSize())
      return -1;
    else
      return 0;
  }
}




// Each result is used to create a DplaItem
//Based on DPLA_APIwrapper package written by Yanni Loukissas

class DplaItem {
  float x, y;
  float diameter;
  String displayTxt;
  boolean over = false;
  JSONObject jsonParent;
  JSONObject sr;
  JSONArray jsonArray;

  // Constructor
  public DplaItem(JSONObject j) {
    x = random(0, width);
    y = random(0, height);
    diameter = 10;

    // Original JSONObject
    jsonParent = j;
    
    // Nested JSONObject that contains useful information
    sr = SourceResource();
    
    // Modify this to determine what is revealed in mouseOver
    displayTxt = getCollectionTITLE();
  }
  
   
   public void openPageInBrowser(){
      String imageJSONURL = "http://api.dp.la/v2/items?id="+getId()+"&fields=isShownAt&api_key="+apikey;
      
      JSONObject jsonObject = loadJSONObject(imageJSONURL);
      JSONArray results = jsonObject.getJSONArray("docs"); 
      String imageURL = results.getJSONObject(0).getString("isShownAt");

       try {
         //Set your page url in this string. For eg, I m using URL for Google Search engine
         java.awt.Desktop.getDesktop().browse(java.net.URI.create(imageURL));
       }
       catch (java.io.IOException e) {
           System.out.println(e.getMessage());
       }
       
    }

  
  public String getId() {
      return jsonParent.getString("id");    
  }
  
  //return the first item of subject
  public String getFirstSubject() {
    String subject = "";
    try {
      JSONArray array = sr.getJSONArray("subject");
         JSONObject entry = array.getJSONObject(0);
         subject += entry.getString("name"); 
      return subject; 
    } catch (Exception f) {
       return "NA"; 
    }    
  }
  
  public String getSubject() {
    String subject = "";
    try {
      JSONArray array = sr.getJSONArray("subject");
      for (int i = 0; i < array.size(); i++) {
         JSONObject entry = array.getJSONObject(i);
         subject += entry.getString("name"); 
         subject += "; ";
      }
    } catch (Exception e) {
       try {
          subject = sr.getJSONObject("subject").toString(); 
       } catch (Exception f) {
         
         subject = "NA"; 
       }
    }
    return subject;    
  }
  
  //return country 
  public String getCountry() {
    String country;
    try {
       country = sr.getJSONArray("spatial").getJSONObject(0).getString("country");
       return country;
    }
    catch (Exception g) {
      try {
        country = sr.getJSONObject("spatial").getString("country"); 
        return country;
      }
      catch (Exception e) {
          country = "NA";
          return country;
      }
    }
  }

  // Return original URL of item
  public String getItemURL() {
    String shownAt = jsonParent.getString("isShownAt");
    println(shownAt);
    return shownAt;
  }

  // Get inside SourceResource (Where a lot of useful info resides)
  public JSONObject SourceResource() {
    JSONObject source = jsonParent.getJSONObject("sourceResource");
    return source;
  }
  

  // Return Title
  public String getTitle() {
    try {
      return sr.getString("title");
    } 
    catch (Exception e) {
      String s = "NA";
      return s;
    }
  }

  // Return Description
  public String getDescription() {
    try {
      return sr.getString("description");
    } 
    catch (Exception e) {
      String s = "NA";
      return s;
    }
  }

  // Return Format
  public String getFormat() {
    try {
      return sr.getString("format");
    }     
    catch (Exception e) {
      String s = "NA";
      return s;
    }
  }

  // Return Collection Name
  public String getCollectionNAME() {
    try {
      return sr.getJSONObject("collection").getString("name");
    }  //"collection":{"id":"9216dc4b915ae9540bed632c7c89ab61","title":"Children's Book and Play Review","@id":"http://dp.la/api/collections/9216dc4b915ae9540bed632c7c89ab61"}
    catch (Exception e) {
      String s = "NA";
      return s;
    }
  }

  // Return Provider Name
  public String getProviderName() {
    try {
      return sr.getJSONArray("provider").toString();
    } //"provider":{"@id":"http://dp.la/api/contributor/mwdl","name":"Mountain West Digital Library"}
    catch (Exception e) {
      String s = "NA";
      return s;
    }
  }

  // Return CollectionID
  public String getCollectionID() {
    try {
      return sr.getJSONObject("collection").getString("id");
    }
    catch (Exception e) {
      String s = "NA";
      return s;
    }
  }

  // Return Title
  public String getCollectionTITLE() {
    try {
      return sr.getJSONObject("collection").getString("title");
    } 
    catch (Exception e) {
      String s = "NA";
      return s;
    }
  }

  // CHecking if mouse is over the Entry
  public void rollover(float px, float py) {
    float d = dist(px, py, x, y);
    if (d < diameter/2) {
      over = true;
    } else {
      over = false;
    }
  }

  // Display the Entry
  public void display() {
    noStroke();
    fill(200, 100);
    ellipse(x, y, diameter, diameter);
    
    if (over) {
      fill(0);
      textAlign(LEFT);
      text(displayTxt, x, y);
    }

    // Causes "falling" entries
    if (y > height) {
      y = 0;
    } else {
      y+=.5f;
    }
    
  }
}




public class MarkerMaker {
  private ArrayList<ImageView> imageCollection = new ArrayList();
  private HashMap<String, HashMap> allMaps;
  private List<Feature> countries;
  private List<Feature> blankCountries;
  private List<Marker> countryMarkers;
  private List<Marker> blankMarkers;
  boolean click = false;
  int itemsPerLevel; 
  float x, y, r; //x, y coordinates and radios of each ellipses
  Marker animalShowing;
  TableMaker table;

  public MarkerMaker(HashMap<String, HashMap> allMaps, List<Feature> countries) {
    this.allMaps = allMaps;
    this.countries = countries;
    countryMarkers = MapUtils.createSimpleMarkers(countries);
    map.addMarkers(countryMarkers);    

    createImages();
  }

  public void draw() {
    for (ImageView image : imageCollection) {
      image.draw();
    }
    if (click == true) {      
      table.draw();
    }
  }

  public void mouseClicked() {
    if (click == true) {
      if (mouseX <= 80 || mouseX >= 920 || mouseY <= 440 || mouseY >= 670) {
        click = false;
      } else {
        if (table.mouseClicked(mouseX, mouseY) == true) {
          click = false;
        }
      }
    } else {
      for (Marker marker : countryMarkers) {      
        if (marker.isInside(map, mouseX, mouseY)) {
          String countryName = marker.getProperty("name").toString();
          CharSequence america = "America";
          if (countryName.contains(america)) {
            countryName = "United States";
          }
          HashMap<String, AnimalList> country = allMaps.get(countryName);
          if (country == null) {
            break;
          }
          table = new TableMaker(country, countryName);
          click = true;
          break;
        } //if
      }//for each marker
    }//if-else
  }


  public void checkOver() {
    for (Marker marker : countryMarkers) {      
      if (marker.isInside(map, mouseX, mouseY)) {
        fill(0);
        textSize(12);
        text(marker.getProperty("name").toString(), mouseX, mouseY);
      }
    }
    if (click == false) {
      for (ImageView image : imageCollection) {
        image.checkOver();
      }
    }
  }  

  public void createImages() {
    //make the markers
    for (Marker marker : countryMarkers) {  
      //marker.setColor(color(123, 213, 126, 255));  

      String countryName = marker.getProperty("name").toString();
      CharSequence america = "America";
      if (countryName.contains(america)) { //this json file use "United States of America", so have to change it
        countryName = "United States";
      }
      HashMap<String, AnimalList> animals = allMaps.get(countryName); //get a hashMap of all the animals in the country
      if (animals != null) {
        marker.setColor(color(150, 210, 145, 255));
        AnimalList maxAnimal = findMax(animals);
        String imageUrl = maxAnimal.getImageUrl();          
        //println(maxAnimal.getAnimalName() + " at " + countryName);
        if (imageUrl != null && !imageUrl.equals("NA")) {
          Location location = marker.getLocation();
          ScreenPosition position = map.getScreenPosition(location);
          if (countryName.equals("United States")) {
            imageCollection.add(new ImageView(20, 215, 225, imageUrl, maxAnimal.getAnimalName()));
          } else {
            imageCollection.add(new ImageView(20, position.x, position.y, imageUrl, maxAnimal.getAnimalName()));
          }
        }
      } else {
        marker.setColor(color(200, 200, 200, 255));
      }
    } //for each marker
  }

  //find the animal that has the max number of items
  public AnimalList findMax(HashMap<String, AnimalList> animalEntries) {
    //HashMap<String, AnimalList> animalEntries = country.getValue();  
    AnimalList max = new AnimalList("emptyAnimal");
    for (Entry<String, AnimalList> animal : animalEntries.entrySet ()) { 
      if (max.compareTo(animal.getValue()) < 0) {
        max = animal.getValue();
      }
    } //for each animal type in a country (HashMap)              
    if (max.getAnimalName().equals("emptyAnimal")) { //if the country is empty (no animals)
      return null;
    } else {                                               //found a max animal
      return max;
    }
  }

  private class ImageView {
    int r;
    float x, y;
    String imgUrl;
    PImage img;
    PImage imgLarge;
    PGraphics mask;
    String animalName;

    public ImageView(int r, float x, float y, String imgUrl, String animal) {
      this.r= r;
      this.x = x;
      this.y = y;
      this.imgUrl = imgUrl;
      this.animalName = animal;
      try {
        img = loadImage(imgUrl, "jpg");     
        imgLarge = loadImage(imgUrl, "jpg");   //store the orignal Image
        if (imgLarge.height > 300) {
          imgLarge.resize(0, 300);
        } else if (imgLarge.width > 400) {
          imgLarge.resize(400, 0);
        }
        //create thumbnails
        img.resize(0, r);
        mask=createGraphics(img.width, img.height);//draw the mask object
        mask.beginDraw();
        mask.smooth();//this really does nothing, i wish it did
        mask.background(0);//background color to target
        mask.fill(255);
        mask.ellipse(img.width/2, img.height/2, r, r);
        mask.endDraw();
        img.mask(mask);
      } 
      catch (Exception e) {
      }
    }

    public void checkOver() {
      float d = dist(mouseX, mouseY, x, y);
      if (d < r/2) {
        imageMode(CORNER);
        float tempX = x;
        if (tempX + imgLarge.width > 1000) {
          tempX = 1000 - imgLarge.width;
        }
        try{
          image(imgLarge, tempX, y + 10);
  
          fill(225);
          textSize(15);
          text(animalName, tempX + imgLarge.width / 2, y);  
          fill(0);
          textSize(14);
          text(animalName, tempX + imgLarge.width / 2 + 1, y);
        } catch (Exception e) {
        }
      }
    }    

    public void draw() {
      try {
        imageMode(CENTER);
        image(img, x, y);
      } 
      catch (Exception e) {
      }
    }
  }//private class
}

// Please put in your own api_key here.
// Go here to get a key: http://dp.la/info/developers/codex/policies/#get-a-key
public String apikey = "62005299089e4b2c0784d8eaa0ef75ae";
public int pageSize = 1000; //be sure to lower it to 100 if you want to 
                            //search without keyword "fairy tale" AND
                            //get images of animals from DPLA. Otherwise it will take a long time.

public class SearchQuery {
  private HashMap<String, HashMap> directory = new HashMap();
  private float[] categoryList;

  public SearchQuery() {
  };

  // Constructor
  public SearchQuery(String[] animalList) { //(String searchTerm)
    for (String animal : animalList) {
      search(animal, pageSize);     //return a JSONArray
    }
  }

  public HashMap<String, HashMap> getMap() {
    return directory;
  }

  public JSONArray search(String title) {
    String queryURL = "http://api.dp.la/v2/items?q=" + title
      + "&sourceResource.type=image&page_size=1&api_key=";
    queryURL+=apikey;

    println("Search: " + queryURL);

    JSONObject jsonObject = loadJSONObject(queryURL);

    JSONArray results = jsonObject.getJSONArray("docs");  
    return results;
  }


  // Search function
  public JSONArray search(String title, int pageSize) {    
    String queryURL = "http://api.dp.la/v2/items?sourceResource.description=" + title
      + "*&sourceResource.type=text&q=fairy+tale*+OR+story*+OR+stories+OR+myth*&page_size="+pageSize+"&api_key=";
//        String queryURL = "http://api.dp.la/v2/items?q=" + title
//          + "*&sourceResource.type=text&page_size="+pageSize+"&api_key="; //search without keyword "fairy tale"
    queryURL+=apikey;

    println("Search: " + queryURL);

    JSONObject jsonObject = loadJSONObject(queryURL);

    //let's parse the data
    parseData(jsonObject, title);

    JSONArray results = jsonObject.getJSONArray("docs");  

    return results;
  }

  //-----Parses the "docs" JSON array-----//
  public void parseData(JSONObject jsonObject, String title) {    
    JSONArray value = jsonObject.getJSONArray("docs");  

    //println("Size: " + value.size());

    //-----Goes through all entries-----//    
    if (value.size() != 0) {
      //MyEntries newEntry = new MyEntries(title);
      //AnimalList newList = new AnimalList(title);
      for (int i = 0; i < value.size (); i++) {
        //println(i);
        JSONObject entry = value.getJSONObject(i);         
        DplaItem item = new DplaItem(entry);
        //newList.add(item);

        String country = item.getCountry();
        if (!country.equals("NA")) {         //if has a country info
          println("[" + item.getCountry() + "]");
          AnimalList tempList;
          try {
            HashMap<String, AnimalList> animals = directory.get(country); //get the map by country name key
            try {
              tempList = animals.get(title);    //get the list of one kind of animal by animal name key
              tempList.add(item);
              //println("Added " + title + " in " + country);
            }
            catch (Exception f) {
              tempList = new AnimalList(title);
              tempList.add(item);
              animals.put(title, tempList);
              //println("Created " + title + " for " + country);
               println("Loading images, please be patient");
              tempList.getImageCSV();
//                        if (tempList.getImageUrl().equals("NA") ) { //search weird images on DPLA
//                          tempList.searchImage(country);
//                        }
            }
          } 
          catch (Exception e) {
            HashMap<String, AnimalList> newMap = new HashMap();
            tempList = new AnimalList(title);
            tempList.add(item);
            newMap.put(title, tempList);
            directory.put(country, newMap);
            //println("Created " + country + " with " + title );
            println("Loading images, please be patient");
            tempList.getImageCSV();
//            if (tempList.getImageUrl().equals("NA") ) {
//              tempList.searchImage(country);
//            } 
          }
        }//if
      }//end outer for loop
    }//if
  }//end function
}




public class TableMaker {
  HashMap<String, AnimalList> animals;
  ArrayList<Section> sectionCollection = new ArrayList();
  String countryName;
  int rectX = 80;
  int rectY = 440;
  int sectionWidth = 105;


  public TableMaker(HashMap<String, AnimalList> animals, String countryName) {
    this.animals = animals;     
    this.countryName = countryName;
    if (animals.size() < 8 && animals.size() > 4) {      
      sectionWidth = 840 / animals.size();
    } else if (animals.size() <= 4) {
      sectionWidth = 840 / 4;
    }
    println("Section width = " + sectionWidth);
    makeSections();
  }

  public void draw() {
    labelCountry();
    for (Section section : sectionCollection) {
      section.draw();
    }
  }

  public boolean mouseClicked(int x, int y) {
    boolean closeWindow = true;
    for (Section section : sectionCollection) {
      if (section.checkOver(x, y) == true) {
        closeWindow = false;
      }     
    }
    return closeWindow;
  }

  public void labelCountry() {
    fill(0);
    textSize(20);
    text(countryName, rectX, rectY - 35);
  }

  public void makeSections() {
    int row = 0;
    int col = 0;
    for (Entry<String, AnimalList> animalList : animals.entrySet ()) { 
      String animalName = animalList.getValue().getAnimalName();
      //println("Created " + animalName);
      List<DplaItem> list = animalList.getValue().getList();
      for (DplaItem item : list) {
        int x = rectX + sectionWidth * col;
        int y = rectY + 50 * row;
        sectionCollection.add(new Section(item, x, y, animalName));
        if (row == 4) {
          break;
        }
        row++;
      } // for each item
      row = 0;
      if (col == 7) {
        break;
      }
      col++;
    } // for each list
  }


  private class Section {
    private DplaItem item;
    private int x, y;
    private String animalName;
    private String text1, text2, text3;
    private String fullText;

    public Section(DplaItem item, int x, int y, String animalName) {
      this.item = item; 
      this.x = x;
      this.y = y;
      this.animalName = animalName;

      if (!item.getTitle().equals("NA")) {
        fullText = item.getTitle();
      } else if (!item.getSubject().equals("NA")) {
        fullText = item.getSubject();
      } else {
        fullText = "Fairy Tale"; //set as the default title
      } 

      int maxChar = (int) sectionWidth / 6 - 1;
        if (fullText.length() > maxChar * 2 - 3) {
        text1 = fullText.substring(0, maxChar - 1);
        text2 = fullText.substring(maxChar - 1, maxChar * 2 - 3);
        text2 = "-" + text2;
        text3 = fullText.substring(maxChar * 2 - 3, min(maxChar * 3 - 5, fullText.length()));
        text3 = "-" + text3;
        text3 += "...";
      } else if (fullText.length() > maxChar - 1) {
        text1 = fullText.substring(0, maxChar - 1);
        text2 = fullText.substring(maxChar - 1, fullText.length());
        text2 = "-" + text2;
      } else {
        text1 = fullText;
      }


      String description = item.getDescription();
      if (!description.equals("NA")) {
        fullText += description;
      }
    } //constructor
    
    public boolean checkOver(int mousex, int mousey) {
      if (mousex >= x && mousex <= x + sectionWidth && mousey >= y && mousey <= y + 50) {
        item.openPageInBrowser();
        return true;
      } else {
        return false;
      }
    }

    public void draw() {      
      stroke(204, 57, 255);
      //fill(color(204, 60, 255, 70));
      fill(color(255, 253, 176, 120));
      rect(x, y, sectionWidth - 1, 49);

      fill(0);
      textSize(15);
      int adjustment =(int) (sectionWidth - textWidth(animalName)) / 2;
      //text(animalName, rectX + sectionWidth * col + adjustment, rectY - 12, 12);
      text(animalName, x + adjustment, rectY - 12, 12);


      textSize(12);
      adjustment =(int) (sectionWidth - textWidth(text1)) / 2;
      text(text1, x + adjustment + 2, y + 15);
      if (text2 != null) {
        adjustment =(int) (sectionWidth - textWidth(text2)) / 2;
        text(text2, x + adjustment + 2, y + 30);
      }
      if (text3 != null) {
        adjustment =(int) (sectionWidth - textWidth(text3)) / 2;
        text(text3, x + adjustment + 2, y + 45);
      }
    }
  }//private class section
}

  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "FairyTalesMap" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
