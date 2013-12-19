package com.zoomifier.adapter;

public class GridData implements Comparable<GridData>{
	public String companyId;
	public String companyName;
	public String readerId;
	public String imageFormat;
	
	public GridData(String companyId,String comapanyName,String readerId,String imageFormat)
	{
		this.companyId=companyId;
		this.companyName=comapanyName;
		this.readerId=readerId;
		this.imageFormat=imageFormat;
	}

	@Override
	public int compareTo(GridData arg0) {
	
		 return this.companyName.compareTo(arg0.companyName);
	}

}
