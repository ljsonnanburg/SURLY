//CS330 - Winter 2019 - Group 9
//SURLY

import java.util.ArrayList;
import java.util.List;
import java.lang.String;
import java.util.Scanner;

public class UnaryOpsParse{

    private Scanner currentLine;
    private String currentToken;
    private int currentGroup = 1;
    String relationName; //these two are public so they can be accessed in the DataBase class
    String tempRelationName;
    String joinRelationName1;
    String joinRelationName2;
    private LinkedList<Restrictions> toPass = new LinkedList<>(); //this is private and is returned to the DataBase class

    public UnaryOpsParse(){

    }

    // T1 = SELECT OFFERING WHERE CNUM = CSCI241 and SECTION > 27922;
    LinkedList<Restrictions> sortSelect(String x){

        currentLine = new Scanner(x);
        int currentGroup = 1;
        int priority = 1;
        updateNext();//temp relation name
        tempRelationName = currentToken;
        updateNext(); //=
        updateNext(); //SELECT
        updateNext(); //actual relation
        relationName = currentToken;
        updateNext(); //WHERE
        updateNext(); //restrictions start coming into play
        compileRestrictions(); //sort through the restrictions

        return toPass;

    }

    LinkedList<Restrictions> sortDelete(String x){

        currentLine = new Scanner(x);
        updateNext();//DELETE
        updateNext();//relationName
        relationName = currentToken;
        updateNext();//WHERE
        updateNext();//restrictions start
        compileRestrictions();
        return toPass;
    }

    //P = PROJECT CREDITS, CNUM FROM COURSE;
    LinkedList<String> sortProject(String x){

        currentLine = new Scanner(x);
        LinkedList<String> toPassString = new LinkedList<>();
        updateNext();//tempRelationName
        tempRelationName = currentToken;
        updateNext();// =
        updateNext();// PROJECT
        updateNext();//start of columns to be checked
        while(!(currentToken.equals("FROM"))){
            toPassString.add(currentToken);
            updateNext();
        }
        updateNext();//relation name
        relationName = currentToken;
        return toPassString;

    }
    LinkedList<Restrictions> sortJoin(String x){
        currentLine = new Scanner(x);
        int currentGroup = 1;
        int priority = 1;
        updateNext(); // temp relation
        tempRelationName = currentToken;
        updateNext();
        updateNext(); //=
        updateNext(); //relation_name 1
        joinRelationName1 = currentToken;
        updateNext(); //relation_name 2
        joinRelationName2 = currentToken;
        updateNext(); //ON
        updateNext(); //restrictions
        compileRestrictions();

        return toPass;

    }

	LinkedList<Restrictions> sortIntegConst(String x){
    
	      currentLine = new Scanner(x);
	      int currentGroup = 1;
	      updateNext(); //relation name
	      relationName = currentToken;
	      updateNext(); //where
	      updateNext();//restrictions start
	      compileRestrictions();
	      return toPass;

	    
	    }

    private void compileRestrictions(){
    
        while(currentLine.hasNext()){

            //initialize all of the instances that will be passed to the restriction
            String compiledToken1=null;
            String op=null;
            String compiledToken2=null;
            String conjunction = null;
            //left hand side of operation
            if(currentToken!=null && currentToken.contains("'")){
                compiledToken1 = parseSingleQuote();
            }else if(currentToken!=null){
                compiledToken1=currentToken;
            }
            //operation
            updateNext();
            if(currentToken!=null)
               op = currentToken;
            updateNext();
            //right hand side of operation
            if(currentToken!=null && currentToken.contains("'")){
                compiledToken2 = parseSingleQuote();
            }else if(currentToken!=null){
                compiledToken2=currentToken;
            }
            //if there is more to the current string
            if(currentLine.hasNext()){
                updateNext();
                if(currentToken!=null)
                  conjunction = currentToken;
            }
            Restrictions currRestriction = new Restrictions(compiledToken1, op, compiledToken2, currentGroup);
            if(compiledToken1!=null)
               toPass.add(currRestriction);
            if(conjunction!=null && conjunction.equals("or")){
                    currentGroup++;
                }

            updateNext();
        }
    }

    //DELETE PREREQ WHERE CNUM = CSCI241 or CNUM = CSCI145 and PNUM != CSCI141;

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

        if(currentLine.hasNext()){
            currentToken = currentLine.next();
        }
    }


}
