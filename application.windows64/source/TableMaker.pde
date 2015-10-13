import java.util.LinkedList;
import java.util.Map.Entry;

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

