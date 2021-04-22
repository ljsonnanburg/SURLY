//CS330 - Winter 2019 - Group 9
//SURLY

//This class was made with the help of Dr. Christopher Reedy

public class LinkedList<E> { //<E> is the data type that we are able to manipulate given how we want to use the constructor

   /* The ListNode class for this list. */
   //this is a singally linked list
   private class ListNode {
      E data;
      ListNode next;

      /* Construct a ListNode containing data. */
      ListNode(E data) {
         this.data = data;
         next = null; 
      }

      /* Construct a ListNode containing data, setting the
       * next. */
      ListNode(E data, ListNode next) {
         this.data = data;
         this.next = next;
      }
   }

   /* The first ListNode in the List. */
   private ListNode front;

   /* Construct an empty list object. */
   public LinkedList() {
      front = null;
   }

   /* Return the size (number of items) in this list. */
   public int size() {
      
      ListNode current = front;
      int count = 0;
      
      //while current isn't null, it will continue through the while loop and increment count by +1
      if(current!=null){
         while(current!=null){
            count++;
            current = current.next;
            }
         return count;   
      }
      
      //if front is initially null, then it will never enter the while loop
      return 0;
   }

   /* Return true if this LinkedList has no items.
    * (This is the same as the size equal to zero.) Return
    * false if the size is greater than zero. */
   public boolean isEmpty() {
      
      //if there is no node in the front position, then the list is empty
      if(front == null)
         return true;
         
      return false;
   }

   /* Add the given element, value, to the end of the list. */
   public void add(E value) {
      if (front == null) {
         front = new ListNode(value);
      } else {
         ListNode current = front;
         while (current.next != null) {
            current = current.next;
         }
         current.next = new ListNode(value);
      }
   }

   /* Add the given element, value, to the list at the given index.
    * After this operation is complete, get(index) will return value.
    * This operation is only valid for 0 <= index <= size(). */
   public void add(int index, E value) {
      
      //if index is zero, update the front of the list
      if(index==0){
         front = new ListNode(value, front);
      }else{
         ListNode current = front;   
         
         //go down the Linked List until the desire index and input the value as defined below at the current.next's node
         for(int i=0; i<index-1 && index<=size(); i++){
            current = current.next;
            }  
         
         current.next = new ListNode(value, current.next);
       }    
      
   }

   /* Return the element of the list at the given index. This operation
    * is only valid for 0 <= index < size(). This operation does not modify
    * the list. */
   public E get(int index) {
      
      ListNode current = front;
      
      //go over the whole list until the the index inputed it reached
      for(int i=0; i<index && index<size(); i++){
         current = current.next;
         }
      
      //if the current node is not null, return the data stored at that node
      if(current!=null)
         return current.data;   
      
      return null;
   }

   /* Remove and return the first element (element number zero) from the
    * list. This operation is only valid for non-empty (size() > 0) lists.
    */
   public void remove() {
   
      //if there are elements in the list, then remove the first element in the list and move a node over and return the
      //original first element in the list
      if(size()>0){
         ListNode current = front;
         
         if(front!=null){
            front = front.next;
            //return current.data;
         }
        } 
         
      //return null;
   }

   /* Remove and return the element with the given index from the list. This
    * operation is only valid for 0 <= index < size(). After this operation,
    * all elements that had an index greater than index (as determined by get())
    * will have their index reduced by one.
    */
   public void remove(int index) {
      
      //if there are no values in the LinkedList, then don't return anything
      if(index==0 && front==null){
         //return null;
         System.out.println("This linked list is empty bruv.");
         
      //if the front is the one being removed, then make a call to remove()   
      }else if(index==0 && front!=null){
         remove();
        
      //if any index greater than 0 wanted to be removed, then go through the linked list until that value is reached,
      //update the node to skip over the node being omitted, and return the value in the omittied node  
      }else{
         ListNode current = front;
         for(int i=0; i<index-1 && index<size(); i++){
            current = current.next;
         }
         
         ListNode omit = current.next;
         current.next = current.next.next;
         //return omit.data;
      }  
   }
}