//CS330 - Winter 2019 - Group 9
//SURLY


public class DataBase{

	private LinkedList<Relation> tables=new LinkedList<>(); //LL of relations that are being stored
	private Relation Catalog = new Relation();
	private LinkedList<Relation> tempRelations = new LinkedList<>(); //LL of all the relations that will be held as temporary relations

	//stores a relation if it hasn't been created yet
	void storeRelation(String x){

		ParseRelation rel = new ParseRelation(); //parse through the specs of the relation
		Relation currentRelation = rel.parseRelation(x);
		int checkForRelation = returnRelationIndex(currentRelation.relationName); //make sure the relation hasn't already been added
		if(checkForRelation == -1){ //if it hasn't... (aka, no duplicates)
			tables.add(currentRelation); //add it to the tables
			LinkedList<String> rel2add = new LinkedList<>(); //then add it to the catalog
			rel2add.add(currentRelation.relationName);
			Tuple tup2add = new Tuple(rel2add);
			Catalog.tuples.add(tup2add);
		}else{ //if it is a duplicate, do not add it
			System.out.println("The relation: \""+currentRelation.relationName+"\" already exists. Cannot add again");
			System.out.println();
		}

	}

	//inserts a tuple into the correct relation
	void storeInsert(String x){

		Insert currentTuptoAdd = new Insert(); //parse through the tuple that you are about to add
		currentTuptoAdd = currentTuptoAdd.parseInsert(x);
		int currentRelation = returnRelationIndex(currentTuptoAdd.insert_2_curr_table);
		if(currentRelation>=0){ //makes sure the relation that you want to add to exists
			if(checkRelationSpecs(currentTuptoAdd.curr_tuple, currentRelation)){//makes sure the tuple meets all the specs of that relation
				tables.get(currentRelation).tuples.add(currentTuptoAdd.curr_tuple);//adds the tupls
			}else{ //if it doesn't pass the relation specs
				System.out.println(DBConstants.specs);
				tables.get(currentRelation).printRelationSpecs();
			}
		}else{//if the relation doesn't exist
			System.out.println("this relation: \""+currentTuptoAdd.insert_2_curr_table+"\" does not exist");
		}

	}

	//prints a relation with all of it's tuples, or the whole catalog if that's what is called
	void printRelation(LinkedList<String> x){

		int numPrints = x.size(); //number of relations that is desired to print
		for(int h=0; h < numPrints; h++){

			String currentPrint = x.get(h); //get the name of the relation

			int currentRelation = returnRelationIndex(currentPrint); //find the index of that relation

			if(currentRelation>=0){ //if found, print it
				tables.get(currentRelation).printRelation();
				System.out.println();
			}else if(currentPrint.equals("CATALOG")){//if the current thing to print is the catalog, print that
				Catalog.printRelation();
				System.out.println();
			}else if(currentRelation==-1){//if the current relation wasn't found in the tables LL...
				int currentTempRelation = returnTempRelationIndex(currentPrint);
				if(currentTempRelation!=-1){//if it's a temp relation, print that
					tempRelations.get(currentTempRelation).printRelation();
					System.out.println();
				}else{  //if it can't be found anywhere, say that the relation doesn't exist
					System.out.println("The relation: \""+currentPrint+"\" does not exist");
					System.out.println();
				}
			}
		}

	}
	
	void integConst(String x){
        
            LinkedList<Restrictions> toCheck2 = new LinkedList<>();
            UnaryOpsParse UOP = new UnaryOpsParse();
            toCheck2 = UOP.sortIntegConst(x);
            String currRelationName = UOP.relationName;
            int relationIndex = returnRelationIndex(currRelationName);
            if(relationIndex!=-1){
                for(int i=0; i<toCheck2.size(); i++){
                    int currCol = returnTablesCurrentColumn(relationIndex, toCheck2.get(i).lhs);
                    IntegrityConstraint newConstraint = new IntegrityConstraint (currCol, toCheck2.get(i));
                    tables.get(relationIndex).integConst.add(newConstraint);
                }
            }
        
        }

	//completely gets rid of an entire relation
	void destroy_relation(LinkedList<String> x){

		int numDestroys = x.size();
		for(int i=0; i<numDestroys; i++){

			String currentDestroy = x.get(i);

			int relationIndex = returnRelationIndex(currentDestroy);
			int tempRelationIndex = returnTempRelationIndex(currentDestroy);
			if(relationIndex==-1){
				if(tempRelationIndex==-1){
					System.out.println("The relation: "+x.get(i)+" has already been deleted and does not exist.");
				}else{
					tempRelations.remove(tempRelationIndex);
				}
			}else{
				tables.remove(relationIndex);
				Catalog.tuples.remove(relationIndex);
			}
		}

	}

	//deletes the entire contents of a given relation
	void deleteRelation(LinkedList<String> x){


		int numRelations = x.size();
		for(int i=0; i<numRelations; i++){
			String currentDelete = x.get(i);

			int relationIndex = returnRelationIndex(currentDelete);
			int tempRelationIndex = returnTempRelationIndex(currentDelete);
			if(relationIndex==-1){
				if(tempRelationIndex==-1){
					System.out.println("The relation: "+x.get(i)+" has already been deleted and does not exist.");
				}else{
					int currentDeleteSize = tempRelations.get(tempRelationIndex).tuples.size();
					for(int j=0; j<currentDeleteSize; j++){
						tempRelations.get(tempRelationIndex).tuples.remove();
					}
				}
			}else{
				int currentDeleteSize = tables.get(relationIndex).tuples.size();
				for(int j=0; j<currentDeleteSize; j++){
					tables.get(relationIndex).tuples.remove();
				}
			}
		}
	}


	//make a new, temproary relation with specific attributes that define what the relation will contain
	void selectRows(String x){

		LinkedList<Restrictions> toCheck = new LinkedList<>(); //LL containing all of the qualifications that need to be met
		UnaryOpsParse UOP = new UnaryOpsParse();
		toCheck = UOP.sortSelect(x); //get the LL of restrictions that will be checked
		String currRelationName = UOP.relationName; //the relation that will be parsed through to see what tuples pass the given requirements
		String tempRelationName = UOP.tempRelationName; //get the temp relation name
		int relationIndex = returnRelationIndex(currRelationName); //find that relation
		int tempRelationIndex = returnTempRelationIndex(currRelationName);
		int numQuals = toCheck.size(); //number of restrictions to be checked
                int numGroups = 0;
                if(numQuals>0)
                            numGroups = toCheck.get(numQuals-1).group;//number of different or statements (includes ands)
                if(numQuals==0){
                    makeIdenticalRelation(tempRelationName, relationIndex, tempRelationIndex);
                }else if(relationIndex!=-1){
                        Relation newTempRelation = new Relation(tempRelationName, tables.get(relationIndex).type); //make it a new relation
                        for(int i=1; i<=numGroups; i++){
                                int toCheckIndex = returnToCheckIndex(toCheck, i);//returns starting index of current or statment
                                int numInGroup = returnNumInGroup(toCheck, toCheckIndex, i); //reutrns how many an statements are in that group
                                LinkedList<Tuple> toAdd = new LinkedList<>(); //pass filterRelation a blank LL of tuples to be added to 
                                toAdd = filterRelation(tables.get(relationIndex).tuples, toAdd, toCheck, relationIndex, toCheckIndex, numInGroup, 1);//get the tuples that meet the requirements
                                int numTups = toAdd.size();
                                for(int j=0; j<numTups; j++){
                                        if(notDuplicate(newTempRelation.tuples, toAdd.get(j))){//make sure it's not a duplicate
                                                newTempRelation.tuples.add(toAdd.get(j)); //add it to the temp relation
                                        }
                                }
                        }
                        tempRelations.add(newTempRelation);//add it to the temp relation table
                }else if(tempRelationIndex!=-1){
                        Relation newnewTempRelation = new Relation(tempRelationName, tempRelations.get(tempRelationIndex).type); //make it a new relation
                        for(int i=1; i<=numGroups; i++){
                                int toCheckIndex = returnToCheckIndex(toCheck, i);//returns starting index of current or statment
                                int numInGroup = returnNumInGroup(toCheck, toCheckIndex, i); //reutrns how many an statements are in that group
                                LinkedList<Tuple> toAdd = new LinkedList<>(); //pass filterRelation a blank LL of tuples to be added to 
                                toAdd = filterTempRelation(tempRelations.get(tempRelationIndex).tuples, toAdd, toCheck, tempRelationIndex, toCheckIndex, numInGroup, 1);//get the tuples that meet the requirements
                                int numTups = toAdd.size();
                                for(int j=0; j<numTups; j++){
                                        if(notDuplicate(newnewTempRelation.tuples, toAdd.get(j))){//make sure it's not a duplicate
                                                newnewTempRelation.tuples.add(toAdd.get(j)); //add it to the temp relation
                                        }
                                }
                        }
                        tempRelations.add(newnewTempRelation);//add it to the temp relation table
                        removeDuplicateTempRelations(tempRelationName);//delete the previous version of the tempRelation if the same relation name was used before
                }else{
                        System.out.println("The relation: \""+currRelationName+"\" does not exist");
                        System.out.println();
                }

	}


	void deleteWhere(String x){

		LinkedList<Restrictions> toCheck = new LinkedList<>(); //LL containing all of the qualifications that need to be met
		UnaryOpsParse UOP = new UnaryOpsParse();
		toCheck = UOP.sortDelete(x); //get the LL of restrictions that will be checked
		String currRelationName = UOP.relationName; //the relation that will be parsed through to see what tuples pass the given requirements
		int relationIndex = returnRelationIndex(currRelationName);
		int tempRelationIndex = returnTempRelationIndex(currRelationName);
		int numQuals = toCheck.size(); //number of restrictions to be checked
		int numGroups = toCheck.get(numQuals-1).group;//number of different or statements (includes ands)
		if(relationIndex!=-1){
			for(int i=1; i<=numGroups; i++){
				int toCheckIndex = returnToCheckIndex(toCheck, i);//returns starting index of current or statment
				int numInGroup = returnNumInGroup(toCheck, toCheckIndex, i); //reutrns how many an statements are in that group
				int numTups = tables.get(relationIndex).tuples.size(); //number of tuples in current relation before removing anything
				LinkedList<Integer> toRemoveIndex = new LinkedList<>(); //indexes of tuples to be removed
				LinkedList<Tuple> toRemove = new LinkedList<>(); //the tuples that meet the requiremtns of being removed that are found in filterRelation
				toRemove = filterRelation(tables.get(relationIndex).tuples, toRemove, toCheck, relationIndex, toCheckIndex, numInGroup, 1);//get the tuples that meet the requirements
				int numRemoved = 0; //counts the amount of tuples removed to account for index change post removal
				int z=0; //counters for the while loop
				int y=0;
				while(z<toRemove.size() || y<numTups){
					Tuple currentTup = tables.get(relationIndex).tuples.get(y);
					if(!(notDuplicate(toRemove, currentTup))){ //find the tuple to be deleted
						toRemoveIndex.add(y-numRemoved); //record it's index
						numRemoved++;
						z++;
						y++;
					}else{
						y++;
					}
				}
				for(int k=0; k<toRemoveIndex.size(); k++){
					int removeIndex = toRemoveIndex.get(k); //remove that tuple
					tables.get(relationIndex).tuples.remove(removeIndex);
				}
			}
			//same as normal realtion delete where
		}else if(tempRelationIndex!=-1){
			for(int i=1; i<=numGroups; i++){
				int toCheckIndex = returnToCheckIndex(toCheck, i);//returns starting index of current or statement
				int numInGroup = returnNumInGroup(toCheck, toCheckIndex, i); //returns how many an statements are in that group
				int numTups = tempRelations.get(tempRelationIndex).tuples.size();
				LinkedList<Integer> toRemoveIndex = new LinkedList<>();
				LinkedList<Tuple> toRemove = new LinkedList<>();
				toRemove = filterTempRelation(tempRelations.get(tempRelationIndex).tuples, toRemove, toCheck, tempRelationIndex, toCheckIndex, numInGroup, 1);//get the tuples that meet the requirements
				int numRemoved = 0;
				int z=0;
				int y=0;
				while(z<toRemove.size() || y<numTups){
					Tuple currentTup = tempRelations.get(tempRelationIndex).tuples.get(y);
					if(!(notDuplicate(toRemove, currentTup))){
						toRemoveIndex.add(y-numRemoved);
						numRemoved++;
						z++;
						y++;
					}else{
						y++;
					}
				}
				for(int k=0; k<toRemoveIndex.size(); k++){
					int removeIndex = toRemoveIndex.get(k);
					tempRelations.get(tempRelationIndex).tuples.remove(removeIndex);
				}
			}
		}

	}

	void project(String x){

		LinkedList<String> toCheck = new LinkedList<>(); //LL containing all of the qualifications that need to be met
		LinkedList<Attribute> tempTypes = new LinkedList<>(); //LL for the attributes to be added to the new temp relation
		UnaryOpsParse UOP = new UnaryOpsParse();
		toCheck = UOP.sortProject(x); //get the LL of restrictions that will be checked
		String currRelationName = UOP.relationName; //the relation that will be parsed through to see what tuples pass the given requirements
		String newRelationName = UOP.tempRelationName; //name for the new relation
		int relationIndex = returnRelationIndex(currRelationName);
		int tempRelationIndex = returnTempRelationIndex(currRelationName);
		int numQuals = toCheck.size();
		int[] colIndexes = new int[numQuals];
		if(relationIndex!=-1){
			for(int i=0; i<numQuals; i++){
				int currColIndex = returnTablesCurrentColumn(relationIndex, toCheck.get(i));
				colIndexes[i]=currColIndex;
				tempTypes.add(tables.get(relationIndex).type.get(currColIndex));
			}
			Relation newRelation = new Relation(newRelationName, tempTypes);
			for(int j=0; j<tables.get(relationIndex).tuples.size(); j++){
				LinkedList<String> elements = new LinkedList<>();
				for(int k=0; k<numQuals; k++){
					elements.add(tables.get(relationIndex).tuples.get(j).tuple.get(colIndexes[k]));
				}
				Tuple tempTuple = new Tuple(elements);
				newRelation.tuples.add(tempTuple);
				tempRelations.add(newRelation);
			}

		}else if(tempRelationIndex!=-1){
			for(int i=0; i<numQuals; i++){
				int currColIndex = returnTempRelCurrentCol(tempRelationIndex, toCheck.get(i));
				colIndexes[i]=currColIndex;
				tempTypes.add(tempRelations.get(tempRelationIndex).type.get(currColIndex));
			}
			Relation newRelation = new Relation(newRelationName, tempTypes);
			for(int j=0; j<tempRelations.get(tempRelationIndex).tuples.size(); j++){
				LinkedList<String> elements = new LinkedList<>();
				for(int k=0; k<numQuals; k++){
					elements.add(tempRelations.get(tempRelationIndex).tuples.get(j).tuple.get(colIndexes[k]));
				}
				Tuple tempTuple = new Tuple(elements);
				newRelation.tuples.add(tempTuple);
				tempRelations.add(newRelation);
				removeDuplicateTempRelations(newRelationName);
			}
		}


	}
   
   private void makeIdenticalRelation(String relName, int relIndex, int tempRelIndex){
   
      Relation placeHolder = new Relation(relName);
      
      if(relIndex!=-1){
         placeHolder.type = tables.get(relIndex).type;
         placeHolder.tuples = tables.get(relIndex).tuples;
         tempRelations.add(placeHolder);
      }else if(tempRelIndex!=-1){
         placeHolder.type = tempRelations.get(tempRelIndex).type;
         placeHolder.tuples = tempRelations.get(tempRelIndex).tuples;
         tempRelations.add(placeHolder);
         removeDuplicateTempRelations(relName);
      }
   }
         


	//sorts through the rows (tuples) and returns the tuples that meet the requirements specified in the where clause
	private LinkedList<Tuple> filterRelation(LinkedList<Tuple> currTups, LinkedList<Tuple> newTups, LinkedList<Restrictions> toCheck, int relIndex, int toCheckIndex, int numInGroup, int currIter){
   
		LinkedList<Tuple> tempTups = newTups; //new list of tuples
		if(currIter==numInGroup){//if it has gone through all the "and" statements/it's only an "or" statement
			int currCol = returnTablesCurrentColumn(relIndex, toCheck.get(toCheckIndex).lhs); //get the current column that is going to be checked
			String currColType = tables.get(relIndex).type.get(currCol).type; //get the cast of whatever the item is in that col
			for(int j=0; j<currTups.size(); j++){
				Tuple currTuple = currTups.get(j); //get the element in that col from a tuple
				if(performOperation(currCol, currTuple, currColType, toCheck.get(toCheckIndex))){ //check to see if it passed the requirement
					tempTups.add(currTuple); //add it
				}
			}
			return tempTups;
		}else{//if there are more "and" conditions to check, keep checking them to only add all the tuples that meet the requirements
			int currCol = returnTablesCurrentColumn(relIndex, toCheck.get(toCheckIndex).lhs); //get the current column that is going to be checked
			String currColType = tables.get(relIndex).type.get(currCol).type; //get the cast of whatever the item is in that col
			for(int j=0; j<currTups.size(); j++){
				Tuple currTuple = currTups.get(j); //get the element in that col from a tuple
				if(performOperation(currCol, currTuple, currColType, toCheck.get(toCheckIndex))){ //check to see if it passed the requirement
					if(notDuplicate(tempTups, currTuple)){//make sure it's not a duplicate
						tempTups.add(currTuple); //add it
					}
				}
			}
			LinkedList<Tuple> thatNewNew = new LinkedList<>();
			int nextCheck = toCheckIndex+1;
			//System.out.println(nextCheck);
			int nextIter = currIter+1;
			return filterRelation(tempTups, thatNewNew, toCheck, relIndex, nextCheck, numInGroup, nextIter); //recursively add the new tuples that passed the current "and" requirement
		}
	}

	//same as filterRelation, but accesses the tempRelations table instead of the normal table of relations
	private LinkedList<Tuple> filterTempRelation(LinkedList<Tuple> currTups, LinkedList<Tuple> newTups, LinkedList<Restrictions> toCheck, int relIndex, int toCheckIndex, int numInGroup, int currIter){

		LinkedList<Tuple> tempTups = newTups;
		if(currIter==numInGroup){
			int currCol = returnTempRelCurrentCol(relIndex, toCheck.get(toCheckIndex).lhs); //get the current column that is going to be checked
			String currColType = tempRelations.get(relIndex).type.get(currCol).type; //get the cast of whatever the item is in that col
			for(int j=0; j<currTups.size(); j++){
				Tuple currTuple = currTups.get(j); //get the element in that col from a tuple
				if(performOperation(currCol, currTuple, currColType, toCheck.get(toCheckIndex))){ //check to see if it passed the requirement
					tempTups.add(currTuple); //add it
				}
			}
			return tempTups;
		}else{
			int currCol = returnTempRelCurrentCol(relIndex, toCheck.get(toCheckIndex).lhs); //get the current column that is going to be checked
			String currColType = tempRelations.get(relIndex).type.get(currCol).type; //get the cast of whatever the item is in that col
			for(int j=0; j<currTups.size(); j++){
				Tuple currTuple = currTups.get(j); //get the element in that col from a tuple
				if(performOperation(currCol, currTuple, currColType, toCheck.get(toCheckIndex))){ //check to see if it passed the requirement
					if(notDuplicate(tempTups, currTuple)){//make sure it's not a duplicate
						tempTups.add(currTuple); //add it
					}
				}
			}
			LinkedList<Tuple> thatNewNew = new LinkedList<>();
			return filterRelation(tempTups, thatNewNew, toCheck, relIndex, toCheckIndex+1, numInGroup, currIter+1);
		}
	}


	//returns the index of the current group being checked in select/delete where
	private int returnToCheckIndex(LinkedList<Restrictions> x, int groupNum){

		int numQuals = x.size();
		int i = 0;
		while(i<numQuals){
			if(x.get(i).group==groupNum){
				return i;
			}else{
				i++;
			}
		}

		return i;
	}

	//returns the number of "and" statements to be checked
	private int returnNumInGroup(LinkedList<Restrictions> x, int index, int groupNum){

		int i=index;
		int count =1;
		int numQuals = x.size();
		while(i<numQuals){
			if(x.get(i).group!=groupNum){
				return count-1;
			}else{
				i++;
				count++;
            
			}
		}
		return count-1;
	}

	private void removeDuplicateTempRelations(String relName){

		int i=0;
                int count = 0;
                            int numRelations = tempRelations.size();
                for(int j=0; j<tempRelations.size(); j++){
                    if(relName.equals(tempRelations.get(i).relationName)){
                        count++;
                    }
                }
                
                if(count>1){
                            while(i<numRelations){
                                    if(relName.equals(tempRelations.get(i).relationName)){
                                            tempRelations.remove(i);
                                            numRelations=numRelations+1;
                                    }else{
                                            i++;
                                    }
                            }
                }
        }

	//checks to make sure we aren't adding duplicate tuples to temporary relations
	private boolean notDuplicate(LinkedList<Tuple> x, Tuple y){

		int numCols = y.tuple.size();
		int numTuples = x.size();
		int sameCount = 0;
		for(int i=0; i<numTuples; i++){
			sameCount = 0;
			for(int j=0; j<numCols; j++){ //parse through each column of the relation
				if(y.tuple.get(j).equals(x.get(i).tuple.get(j))){
					sameCount++; //add one to same count if the element is identical to the one in that column
				}
			}
			if(sameCount==numCols){ //if it is 100% identical, then do not add it to the temp relation
				return false;
			}
		}

		return true;
	}

	//this checks to see if a certain tuple meets the requirements given by the restrictions
	private boolean performOperation(int colIndex, Tuple x, String type, Restrictions y){

		//if it is of type char
		if(type.equals("CHAR")){
			if(y.operator.equals("=")){
				if(x.tuple.get(colIndex).equals(y.rhs)){
					return true;
				}
			}else if(y.operator.equals("!=")){
				if(!(x.tuple.get(colIndex).equals(y.rhs))){
					return true;
				}
			}
			//else, if it is of type int
		}else if(type.equals("NUM")){
			int currentValue = getColumnValue(x, colIndex);
			int rhsValue = getRHSValue(y.rhs);
			if(y.operator.equals("=")){
				if(currentValue==rhsValue){
					return true;
				}
			}else if(y.operator.equals("!=")){
				if(currentValue!=rhsValue){
					return true;
				}
			}else if(y.operator.equals("<")){
				if(currentValue<rhsValue){
					return true;
				}
			}else if(y.operator.equals(">")){
				if(currentValue>rhsValue){
					return true;
				}
			}else if(y.operator.equals("<=")){
				if(currentValue<=rhsValue){
					return true;
				}
			}else if(y.operator.equals(">=")){
				if(currentValue>=rhsValue){
					return true;
				}
			}
		}


		return false;

	}

	//get the specfic tuple/column value that is going to be checked, if it's of type NUM
	private int getColumnValue(Tuple tup, int col){

		String numToParse = tup.tuple.get(col);
		try{
			int x = Integer.parseInt(numToParse);
			return x;
		}catch (Exception e){
			System.out.println(e);
		}

		return 0;

	}

	//gets the value of the restriction, if it's of type NUM
	private int getRHSValue(String y){

		try{
			int x = Integer.parseInt(y);
			return x;
		}catch(Exception e){
			System.out.println(e);
		}

		return 0;
	}

	//returns the index of the relation you are trying to find
	private int returnRelationIndex(String relationName){

		int i = 0;
		while(i<tables.size()){
			if(tables.get(i).relationName.equals(relationName)){
				return i; //return the relation indes
			}else{
				i ++;
			}
		}

		//if nothing is found, return -1
		return -1;
	}

	//returns the temporary relation index
	private int returnTempRelationIndex(String relationName){

		int i = 0;
		while(i<tempRelations.size()){
			if(tempRelations.get(i).relationName.equals(relationName)){
				return i; //return the relation indes
			}else{
				i ++;
			}
		}

		//if nothing is found, return -1
		return -1;
	}

	//returns the index of the column you are looking for in a particular relation
	private int returnTablesCurrentColumn(int relIndex, String colName){

		int i=0;
		while(i<tables.get(relIndex).type.size()){
			if(tables.get(relIndex).type.get(i).name.equals(colName)){
				return i;
			}else{
				i++;
			}
		}

		//if nothing is found, return -1
		return -1;

	}

	private int returnTempRelCurrentCol(int relIndex, String colName){

		int i=0;
		while(i<tempRelations.get(relIndex).type.size()){
			if(tempRelations.get(relIndex).type.get(i).name.equals(colName)){
				return i;
			}else{
				i++;
			}
		}

		//if nothing is found, return -1
		return -1;

	}

	private boolean checkRelationSpecs(Tuple tup, int relationIndex){
		int numCols = tables.get(relationIndex).type.size(); //get number of columns
		int numElements = tup.tuple.size(); //get number of elements in the tuple
		int count = 0; //count to make sure all spec requirements are met
		if(numCols == numElements){ //if the size of the tuple fits into the number of columns needed to make tuple
			for(int i=0; i<numCols; i++){
				int allowedLength = tables.get(relationIndex).type.get(i).size; //get allowed size length
				int elementLength = tup.tuple.get(i).length(); //get length of element going into that particular column
				if(tup.tuple.get(i).contains("'")){
					elementLength=elementLength-2;
				}
				if(elementLength <= allowedLength){ //make sure the element isn't too long
					if(checkType(tup.tuple.get(i), i, relationIndex)){ //finally, check to make sure it's of the proper type
						//System.out.println("you made it here");
						count++; //if everything checks out, that element is good to be added
					}else{
						return false;
					}
				}else{
					System.out.println("element is too long: \""+tup.tuple.get(i)+"\" with length: "+elementLength);
					return false;
				}
			}
		}

		if(count==numElements){
                    if(checkIntegConstraints(tup, relationIndex)){
			return true;
                    }
		}

		return false;
	}

	// checkType method, takes a string representing a typle element, checks it against the constraints 
	// on a relation's specified attribute
	private boolean checkType(String currentElement, int colIndex, int relationIndex){
		String typeInput = tables.get(relationIndex).type.get(colIndex).type;
		if(typeInput.equals("CHAR")){
			return true; //returns true because everything is initially stored as a string
		}else if(typeInput.equals("NUM")){
			//try and convert all nums to ints, and if that doesn't work, throw an exception
			try{
				int currentNum = Integer.parseInt(currentElement);
			}catch(Exception e){
				System.out.println("Current element: \""+currentElement+"\" is not of type: "+typeInput);
				System.out.println(e);
				return false;
			}
			return true;
		}
		return false;
	}
	
  private boolean checkIntegConstraints(Tuple tup, int relationIndex){
   
      int numCols = tables.get(relationIndex).type.size();
      for(int i=0; i<numCols; i++){
         LinkedList<Integer> numToCheck = returnNumConstrainsInColumn(relationIndex, i);
         if(!(numToCheck.isEmpty())){
            for(int j=0; j<numToCheck.size(); j++){
               String typeInput = tables.get(relationIndex).type.get(i).type;
               Restrictions currRestriction = tables.get(relationIndex).integConst.get(numToCheck.get(j)).toPass;
               if(!(performOperation(i, tup, typeInput, currRestriction))){
                  System.out.println();
                  tables.get(relationIndex).printIntegConst(tup.tuple.get(i), currRestriction);
                  //print integ constrain specs
                  return false;
               }
                  

            }
         }
       }
   
      return true;
   }
   
   private LinkedList<Integer> returnNumConstrainsInColumn(int relationIndex, int colNumber){
    
     LinkedList<Integer> indexesToCheck = new LinkedList<>(); 
     int numIntegConst = tables.get(relationIndex).integConst.size();
     for(int i=0; i<numIntegConst; i++){
      if(colNumber == tables.get(relationIndex).integConst.get(i).colNumber){
         indexesToCheck.add(i);
      }
     }
     
     return indexesToCheck;
   }

	// join method, takes a string, returns nothing, constructs a new temp relation joined from two tables on a condition
	void join(String x) {
		//T1 = JOIN COURSE, PREREQ ON CNUM = PNUM;
		LinkedList<Restrictions> toCheck = new LinkedList<>(); //LL containing all of the qualifications that need to be met
		UnaryOpsParse UOP = new UnaryOpsParse();
		toCheck = UOP.sortJoin(x); //get the LL of restrictions that will be checked
		String tempRelationName = UOP.tempRelationName; // get the temp relation name
		String joinRelationName1 = UOP.joinRelationName1; // name of first joined table
		String joinRelationName2 = UOP.joinRelationName2; // name of second joined table
		// finds indices of relations in Catalog
		int relationIndex1 = returnRelationIndex(joinRelationName1); 
		int relationIndex2 = returnRelationIndex(joinRelationName2);
		// finds indices of attributes in joined relations
		int attributeIndex1 = tables.get(relationIndex1).returnAttributeIndex(toCheck.get(0).lhs);
		int attributeIndex2 = tables.get(relationIndex2).returnAttributeIndex(toCheck.get(0).rhs);

		LinkedList<Attribute> tempAttributes = new LinkedList<>(); // linked list for new table's attributes
		// loops retrieve ALL attributes from both tables, add to tempAttributes
		for(int i=0; i<tables.get(relationIndex1).type.size(); i++){
			tempAttributes.add(tables.get(relationIndex1).type.get(i));
		}
		for(int i=0; i<tables.get(relationIndex2).type.size(); i++){
			tempAttributes.add(tables.get(relationIndex2).type.get(i));
		}
		// Creates the new temp relation with all combined items from both tables
		Relation newTempRelation = new Relation(tempRelationName, tempAttributes);
		// outer loop checks each tuple in table1
		for (int i = 0; i < tables.get(relationIndex1).tuples.size(); i++) {
			//inner loop checks table1 tuple against table2 tuple
			for (int j = 0; j < tables.get(relationIndex2).tuples.size(); j++) {
				// finds matches between table1 and table2 in the desired attributes
				if (tables.get(relationIndex1).tuples.get(i).tuple.get(attributeIndex1).equals
						(tables.get(relationIndex2).tuples.get(j).tuple.get(attributeIndex2))) 
				{
					LinkedList<String> tupleValues = new LinkedList<>(); // list of tuple values
					// loop adding table 1's matching tuple values
					for (int k = 0; k < tables.get(relationIndex1).type.size(); k++) {
						tupleValues.add(tables.get(relationIndex1).tuples.get(i).tuple.get(k));
					}
					// loop adding table 2's matching tuple values
					for (int k = 0; k < tables.get(relationIndex2).type.size(); k++) {
						tupleValues.add(tables.get(relationIndex2).tuples.get(j).tuple.get(k));
					}
					// constructs a tuple with all values from matching tuples in order 
					Tuple newTuple = new Tuple(tupleValues);
					newTempRelation.tuples.add(newTuple);
				}
			}
		}
		tempRelations.add(newTempRelation); // adds the new temp relation to the database
	}
}   

