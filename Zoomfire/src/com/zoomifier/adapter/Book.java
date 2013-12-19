package com.zoomifier.adapter;

public class Book implements Comparable<Book>
{    
    public String title;
    public String reviewName;
    public String time;
    public String numberofstar;
   

    public Book() {
        // TODO Auto-generated constructor stub
    }
    
    public Book(String title,String reviewName,String time,String numberofstar) {
        super();
        this.title = title;
        this.reviewName=reviewName;
        this.time=time;
        this.numberofstar=numberofstar;
     
   
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int compareTo(Book book) {
        return this.getTitle().compareTo(book.getTitle());
    }
}
