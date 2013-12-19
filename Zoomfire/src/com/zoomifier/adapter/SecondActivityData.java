package com.zoomifier.adapter;

public class SecondActivityData implements Comparable<SecondActivityData>{
	   public String 
	   documentId,
	   documentName,
	   documentDiscription,
	   documentContentType,
	   documentContentWidth,
	   documentContentHeight,
	   documentOwnerId,
	   like_document,
	   SharedName,
	   isshared,
	   sharedId,
	   SharedDate,
	   order,
	   pagecount,
	   client_id,
	   clientName,
	   clientformat,
	   originalId,
	   userID;
	   
	
	  public SecondActivityData(String documentId,String documentName,String documentDiscription,
			  String originalId, String documentContentType,String documentContentWidth,String documentContentHeight,String documentOwnerId,String like_document,String SharedName,String isshared,String sharedId,
			  String SharedDate,String order,String pagecount,String client_id,String clientName,String clientformat,String userID)
	  {
		  this.documentId=documentId;
		  this.documentName=documentName;
		  this.documentDiscription=documentDiscription;
		 
		  this.documentContentType=documentContentType;
		  this.documentContentWidth=documentContentWidth;
		  this.documentContentHeight=documentContentHeight;
		  this.documentOwnerId=documentOwnerId;
		  this.userID=userID;  
		  this.client_id=client_id;
		  this.like_document=like_document;
		  this.SharedName=SharedName;
		  this.SharedDate=SharedDate;
		  this.sharedId=sharedId;
		  this.pagecount=pagecount;
		  this.clientName=clientName;
		  this.clientformat=clientformat;
		  this.order=order;
		  this.isshared=isshared;
		  this.originalId=originalId;
		  
	  }
	@Override
	public int compareTo(SecondActivityData arg0) {
		// TODO Auto-generated method stub
		 return this.documentName.compareTo(arg0.documentName);
	}
}
