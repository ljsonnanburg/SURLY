//CS330 - Winter 2019 - Group 9
//SURLY

public class Attribute{

   String name; // column name
   String type; //column type (ie NUM or CHAR)
   int size; //max length of an element in that column
   
   public Attribute(String x, String y, int z){
   
      name=x;
      type=y;
      size=z;
   }

}