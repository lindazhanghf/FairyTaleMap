import de.fhpotsdam.unfolding.*;
import de.fhpotsdam.unfolding.data.*;
import de.fhpotsdam.unfolding.geo.*;
import de.fhpotsdam.unfolding.marker.*;
import de.fhpotsdam.unfolding.utils.*;
import java.util.List;

HashMap<String, HashMap> allMaps;
public UnfoldingMap map;
List<Marker> countryMarkers;
List<Feature> countries;
MarkerMaker marks;

void setup() {
  size(1000, 700);
  smooth();
  //background(137, 200, 225);
  loadData();
}

void draw() {
  map.draw();
  marks.draw();
//  Location location = map.getLocation(mouseX, mouseY);
//  text(location.getLat() + ", " + location.getLon(), mouseX, mouseY + 10);
  fill(0);
//  text(mouseX + ", " + mouseY, mouseX, mouseY);
  marks.checkOver();

}

void mouseClicked(){
      marks.mouseClicked();
}


void loadData() {    
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

