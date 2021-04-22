//CS330 - Winter 2019 - Group 9
//SURLY

import java.io.File;
import java.util.*;
import java.io.FileNotFoundException;

public class ParseRelation{

   Scanner currentRelation;
   String currentToken;
   
   Relation parseRelation(String x){
   
      currentRelation = new Scanner(x);
      updateNext();
      updateNext();
      String relationName = currentToken;
      int count =0;
      LinkedList<Attribute> relationTypes = new LinkedList<>();//make a LL of attributes that will store the relation specs
      updateNext();
         while(currentRelation.hasNext()){
               String attName = currentToken;
               updateNext();
               String typeCast = currentToken;
               updateNext();
               if(currentToken.contains(";")){
                  currentToken=cleanToken();
               }
               try{
                  //if it is a number, then add it and create the relation attribue
                  int length = Integer.parseInt(currentToken);
                  count++;
                  Attribute tupleType = new Attribute(attName, typeCast, length);
                  relationTypes.add(tupleType);
                  updateNext();
               }catch(Exception e){
                  //else, don't add that column and let the user know they need to retry
                  System.out.println("You tried to put in the string: \""+currentToken+"\" as an integer");
                  System.out.println(e);
                  System.out.println();
                  updateNext();
               }
            }
       
       //after compiling everything, return the relation to be added to the catalog
       Relation toAdd = new Relation(relationName, relationTypes);
       
      return toAdd;
   }      
   
   
   private String cleanToken(){
      
      int length = currentToken.length();
      int count=0;
      StringBuilder num = new StringBuilder();
      while(count<length){
         if(currentToken.charAt(count)==';'){
            count++;
         }else{
            num.append(currentToken.charAt(count));
            count++;
         }
      }
         
      return num.toString();
   }
   
   
   private void updateNext(){
      
      if(currentRelation.hasNext()){
         currentToken = currentRelation.next();
      }
         
   }  



}