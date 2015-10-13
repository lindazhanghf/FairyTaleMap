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
  void rollover(float px, float py) {
    float d = dist(px, py, x, y);
    if (d < diameter/2) {
      over = true;
    } else {
      over = false;
    }
  }

  // Display the Entry
  void display() {
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
      y+=.5;
    }
    
  }
}

