//CS330 - Winter 2019 - Group 9
//SURLY

import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import java.util.Scanner;

//follows the same logic as parseRelation
public class Insert {
    
    Scanner currentInsert;
    String currentToken;
    String insert_2_curr_table;
    Tuple curr_tuple;
    
    //this initializes the class object
    public Insert(){
    
    }

    // Insert Object right hurr
    public Insert (String input, Tuple tuple){
        insert_2_curr_table = input;
        curr_tuple = tuple;
    }
    
    // Parse Insert method, will return an insert object
    Insert parseInsert(String input){
        currentInsert = new Scanner(input);
        updateNext();
        updateNext();
        String insert_2_table = currentToken;
        LinkedList<String> string_2_add = new LinkedList<>();
        updateNext();
        while(currentInsert.hasNext()){
            
            if(currentToken.contains("'")){
               String compiledToken = parseSingleQuote();
               string_2_add.add(compiledToken);
              }else{
               string_2_add.add(currentToken);
            }   
            
            updateNext();
            

        }
        
        //adds the last token 
        currentToken = cleanToken();
        string_2_add.add(currentToken);
        Tuple new_insert = new Tuple(string_2_add);
        Insert insert_to_return = new Insert(insert_2_table, new_insert);
        return insert_to_return;
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
   
   //keeps things inbetween single quotes as "one" token
   private String parseSingleQuote(){
      StringBuilder innerQuote = new StringBuilder();
      int i =1;
      int length = currentToken.length();
      while(i<currentToken.length()){
        innerQuote.append(currentToken.charAt(i));
        i++;
      }
      i = 0;
      innerQuote.append(" ");
      updateNext();
      while(!(currentToken.contains("'"))){
         innerQuote.append(currentToken);
         innerQuote.append(" ");
         updateNext();
      }
      length = currentToken.length();
      if(currentToken.contains("'") || currentToken.contains(";")){
         while(i<length){
            if(currentToken.charAt(i)==';' || currentToken.charAt(i)=='\''){
               i++;
            }else{
             innerQuote.append(currentToken.charAt(i));
             i++;
            }
         }
        
      }
      
       String toReturn = innerQuote.toString();
         return toReturn;
   }
     
         
   
   
    private void updateNext(){

        if(currentInsert.hasNext()){
            currentToken = currentInsert.next();
        }
    }
}
