package src.model;

public class Brand {

     // FIELDS
     private int id;
     private String name;

     // CONSTRUCTOR
     public Brand() {
     }

     public Brand(int id, String name) {
          this.id = id;
          this.name = name;
     }

     // METHODS
     public int getId() {
          return id;
     }

     public void setId(int id) {
          this.id = id;
     }

     public String getName() {
          return name;
     }

     public void setName(String name) {
          this.name = name;
     }

     @Override
     public String toString() {
          return name;
     }
}
