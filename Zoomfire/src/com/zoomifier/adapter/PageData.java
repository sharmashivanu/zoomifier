package com.zoomifier.adapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PageData implements Serializable{
	
	public ArrayList<String> page=new ArrayList<String>();
	public PageData(ArrayList<String>  page)
	{
		this.page=page;
	}

}
