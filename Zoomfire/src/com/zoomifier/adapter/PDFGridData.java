package com.zoomifier.adapter;

public class PDFGridData  implements Comparable<SecondActivityData>{
	   public String documentId,documentName,documentDiscription,documentOrginalId,documentContentType,
	    documentContentWidth,documentContentHeight,documentOwnerId,clienid,userID;
	   public String client_id,like_document,SharedName,SharedDate;
	   public int signal;
	  public PDFGridData(String documentId,String documentName,String documentDiscription,String documentOrginalId,
			  String documentContentType,String documentContentWidth,String documentContentHeight,String documentOwnerId,String client_id,
			  String userID,String like_document,String SharedName,String SharedDate,int signla)
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
		  this.signal=signal;
		  
	  }
	@Override
	public int compareTo(SecondActivityData arg0) {
		// TODO Auto-generated method stub
		 return this.documentName.compareTo(arg0.documentName);
	}
}

