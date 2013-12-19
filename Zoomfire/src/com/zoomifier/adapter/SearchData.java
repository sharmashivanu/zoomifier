package com.zoomifier.adapter;

import java.io.Serializable;

public class SearchData implements Serializable{   
	public String documentId,documentName,documentDiscription,documentOrginalId,documentContentType,
    documentContentWidth,documentContentHeight,documentOwnerId,clienid,userID;
   public String client_id,like_document,SharedName,SharedDate,pageCount;
  public SearchData(String documentId,String documentName,String documentDiscription,String documentOrginalId,
		  String documentContentType,String documentContentWidth,String documentContentHeight,String documentOwnerId,String client_id,
		  String userID,String like_document,String SharedName,String SharedDate,String pageCount)
  
  {
	  
	  this.documentId=documentId;
	  this.documentName=documentName;
	  this.documentDiscription=documentDiscription;
	  this.documentOrginalId=documentOrginalId;
	  this.documentContentType=documentContentType;
	  this.documentContentWidth=documentContentWidth;
	  this.documentContentHeight=documentContentHeight;
	  this.documentOwnerId=documentOwnerId;
	  this.userID=userID;  
	  this.client_id=client_id;
	  this.like_document=like_document;
	  this.SharedName=SharedName;
	  this.SharedDate=SharedDate;
	  this.pageCount=pageCount;
	  
  }
}