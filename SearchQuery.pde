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

