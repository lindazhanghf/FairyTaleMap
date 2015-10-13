import java.util.Map.Entry;
import de.fhpotsdam.unfolding.*;

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

