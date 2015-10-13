import java.util.LinkedList;

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




