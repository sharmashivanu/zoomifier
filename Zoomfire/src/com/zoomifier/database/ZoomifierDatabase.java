package com.zoomifier.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ZoomifierDatabase {
	public static final String COMPANY_NAME = "_company";
	public static final String COMPANY_ID = "_companyid";
	public static final String DOCUMENT_ID = "_documentId";
	public static final String DOCUMENT_NAME = "_documentname";
	public static final String DOCUMENT_DISCRIPTION = "_documentdiscription";
	public static final String DOCUMENT_ORGINAL_ID = "_documentorginalid";
	public static final String DOCUMENT_CONTENTTYPE = "_contenttype";
	public static final String DOCUMENT_CONTENT_WIDTH = "_contentwidth";
	public static final String DOCUMENT_CONTENT_HEIGHT = "_contentheight";
	public static final String DOCUMENT_OWNER_ID = "_documentownerid";
	public static final String DOCUMENT_LIKE = "_documentlike";
	public static final String DOCUMENT_CLIENT_ID = "_document_client_id";
	public static final String DOCUMENT_USER_ID = "_document_image_id";
	public static final String DOCUMENT_REVIEW = "_document_review";
	public static final String DOCUMENT_REVIEW_TITLE = "_document_review_title";
	public static final String PAGE_NO = "_page_No";
	public static final String NO_OF_STAR = "_no_of_star";
	public static final String REVIEW_DATE = "review_date";
	public static final String MEMBER_NAME = "_membername";
	public static final String OCCASSION_TYPE = "_occassion_type";
	public static final String CREATION_DATE = "_creationdate";
	public static final String OCCASSION_ID = "_occassionid";

	public static List<String> companyName = new ArrayList<String>();
	public static List<String> companyId = new ArrayList<String>();
	public static List<String> documentName = new ArrayList<String>();
	public static List<String> documentID = new ArrayList<String>();
	public static List<String> documentDiscription = new ArrayList<String>();
	public static List<String> documentorginalID = new ArrayList<String>();
	public static List<String> documentcontntwidth = new ArrayList<String>();
	public static List<String> documentcontetntype = new ArrayList<String>();
	public static List<String> documentcontenntheight = new ArrayList<String>();
	public static List<String> documentownerid = new ArrayList<String>();
	public static List<String> documentclientid = new ArrayList<String>();
	public static List<String> documentlike = new ArrayList<String>();
	public static List<String> imageid = new ArrayList<String>();
	public static List<String> sharedName = new ArrayList<String>();
	public static List<String> sharedDate = new ArrayList<String>();
	public static List<String> imageformat = new ArrayList<String>();
	public static List<String> startTime = new ArrayList<String>();
	public static List<String> endTime = new ArrayList<String>();
	public static List<String> pagenumber = new ArrayList<String>();
	public static List<String> search_tag = new ArrayList<String>();
	public static List<String> location = new ArrayList<String>();

	public static List<String> pageAnayltic_docid = new ArrayList<String>();
	public static List<String> pageAnalytic_clientid = new ArrayList<String>();
	public static List<String> pageAnalytic_starttime = new ArrayList<String>();
	public static List<String> pageAnalytic_endtime = new ArrayList<String>();
	public static List<String> pageAnalytic_searchtag = new ArrayList<String>();
	public static List<String> pageAnalytic_location = new ArrayList<String>();
	public static List<String> pageAnalytic_pageno = new ArrayList<String>();
	public static List<String> pageAnalytic_first_object = new ArrayList<String>();
	public static List<String> pageAnalytic_second_object = new ArrayList<String>();
	public static List<String> pageAnalytic_share_to = new ArrayList<String>();
	public static List<String> pageAnalytic_share_type = new ArrayList<String>();
	public static List<String> pageAnalytic_reader_id = new ArrayList<String>();
	public static List<String> pageAnalytic_content_type = new ArrayList<String>();
	public static List<String> pageAnalytic_eventtype = new ArrayList<String>();

	public static List<String> memberName = new ArrayList<String>();
	public static List<String> occassionType = new ArrayList<String>();
	public static List<String> creationDate = new ArrayList<String>();
	public static List<String> occassionId = new ArrayList<String>();
	public static List<String> documentreviewtitle = new ArrayList<String>();
	public static List<String> documentreview = new ArrayList<String>();
	public static List<String> noforeviewstar = new ArrayList<String>();
	public static List<String> time = new ArrayList<String>();
	public static List<String> scanedate = new ArrayList<String>();

	private static final String DATABASE_NAME = "zoomifierdatabase";
	public static final String COMPANY_TABLE = "zoomifiercompanytable";
	public static final String DOCUMENT_TABLE = "ToDoListEntryTable";
	public static final String DOCUMENT_REVIEW_TABLE = "DocumentReview";
	public static final String OCCASSION_TABLE = "occassionTable";
	public static final String PAGE_TABLE = "pageTable";
	public static final String SHAREDNAME = "_sharedname";
	public static final String SHARDDATE = "_shareddate";
	public static final String IMAGE_FORMAT = "_imageformat";
	public static final String START_TIME = "_starttime";
	public static final String END_TIME = "_endtime";
	public static final String LOCATION = "_location";
	public static final String SEARCH_TAG = "_searchtag";
	public static final String PAGENO = "_page_no";
	public static final String DOCUMENT_ANALYTICS = "zoomifieranyalytics";
	public static final String PAGE_ANALYTICS = "pageanalytics";
	public static final String CLIENT_ID = "_clientid";
	public static final String DOCUMENT_ANALYTIC_SHARE_TYPE = "_documentsharetype";
	public static final String DOCUMETN_ANYLYTIC_SHARETO = "_documentshreto";
	public static final String OBJECT_FIRST = "_firstobject";
	public static final String OBJECT_SECOND = "_secondobject";
	public static final String ANLYTICS_READERID = "_anylaytcialreadid";
	public static final String EVENT_TYPE = "_eventtype";
	public static final String CLIENT_NAME = "_clientname";
	public static final String PAGE_COUNT = "_pagecount";
	public static final String IS_SHARED = "_isshared";
	public static final String ORDER = "_order";
	public static final String SHARED_ID = "_shardid";
	public static final String QRTITTLE = "_qrtitle";

	public static final String QRCODENAME = "_qrcode";
	public static final String QRCODETABLE = "_qrtable";
	public static final String QRCODELIKE = "_qrcodelike";
	public static final String QRCODESCANEDATE = "_qrscanedate";
	public static List<String> qrName = new ArrayList<String>();
	public static List<String> qrLike = new ArrayList<String>();
	public static List<String> qrtitle = new ArrayList<String>();

	public static List<String> clientname = new ArrayList<String>();
	public static List<String> pagecount = new ArrayList<String>();
	public static List<String> isshared = new ArrayList<String>();
	public static List<String> order = new ArrayList<String>();
	public static List<String> sharedId = new ArrayList<String>();

	private static final int DATABASE_VERSION = 2;
	private DataBaseObject dataBaseObject;
	private final Context context;
	private SQLiteDatabase sqLiteDatabase;
	public String listId;

	private static class DataBaseObject extends SQLiteOpenHelper {

		public DataBaseObject(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL("CREATE TABLE " + COMPANY_TABLE + " ( " + COMPANY_ID
						+ " TEXT NOT NULL ," + COMPANY_NAME
						+ " TEXT NOT NULL ," + IMAGE_FORMAT
						+ " TEXT NOT NULL );");

				db.execSQL("CREATE TABLE " + QRCODETABLE + " ( " + QRCODENAME
						+ " TEXT NOT NULL ," + QRCODELIKE + " TEXT NOT NULL ,"
						+ QRTITTLE + " TEXT NOT NULL ," + QRCODESCANEDATE
						+ " TEXT NOT NULL );");

				db.execSQL("CREATE TABLE " + PAGE_TABLE + " ( " + DOCUMENT_ID
						+ " TEXT NOT NULL ," + PAGE_NO + " TEXT NOT NULL ,"
						+ SHAREDNAME + " TEXT NOT NULL ," + SHARDDATE
						+ " TEXT NOT NULL);");

				db.execSQL("CREATE TABLE " + DOCUMENT_REVIEW_TABLE + " ( "
						+ DOCUMENT_ID + " TEXT NOT NULL ,"
						+ DOCUMENT_REVIEW_TITLE + " TEXT NOT NULL ,"
						+ NO_OF_STAR + " TEXT NOT NULL ," + REVIEW_DATE
						+ " TEXT NOT NULL ," + DOCUMENT_REVIEW
						+ " TEXT NOT NULL);");

				db.execSQL("CREATE TABLE " + PAGE_ANALYTICS + " ( "
						+ DOCUMENT_ID + " TEXT NOT NULL," + CLIENT_ID
						+ " TEXT NOT NULL ," + PAGE_NO + " TEXT NOT NULL,"
						+ DOCUMENT_ANALYTIC_SHARE_TYPE + " TEXT NOT NULL,"
						+ EVENT_TYPE + " TEXT NOT NULL ," + OBJECT_FIRST
						+ " TEXT NOT NULL ," + OBJECT_SECOND
						+ " TEXT NOT NULL ," + ANLYTICS_READERID
						+ " TEXT NOT NULL ," + DOCUMENT_CONTENTTYPE
						+ " TEXT NOT NULL ," + DOCUMETN_ANYLYTIC_SHARETO
						+ " TEXT NOT NULL ," + START_TIME + " TEXT NOT NULL ,"
						+ END_TIME + " TEXT NOT NULL ," + LOCATION
						+ " TEXT NOT NULL ," + SEARCH_TAG + " TEXT NOT NULL);");

				db.execSQL("CREATE TABLE " + DOCUMENT_TABLE + " ( "
						+ DOCUMENT_ID + " TEXT NOT NULL, " + DOCUMENT_NAME
						+ " TEXT NOT NULL," + DOCUMENT_DISCRIPTION
						+ " TEXT NOT NULL," + DOCUMENT_ORGINAL_ID
						+ " TEXT NOT NULL," + DOCUMENT_CONTENTTYPE
						+ " TEXT NOT NULL," + DOCUMENT_CONTENT_WIDTH
						+ " TEXT NOT NULL," + DOCUMENT_CONTENT_HEIGHT
						+ " TEXT NOT NULL," + DOCUMENT_LIKE + " TEXT NOT NULL,"
						+ DOCUMENT_CLIENT_ID + " TEXT NOT NULL," + CLIENT_NAME
						+ " TEXT NOT NULL," + IMAGE_FORMAT + " TEXT NOT NULL,"
						+ PAGE_COUNT + " TEXT NOT NULL," + SHARED_ID
						+ " TEXT NOT NULL," + IS_SHARED + " TEXT NOT NULL,"
						+ ORDER + " TEXT NOT NULL," + DOCUMENT_USER_ID
						+ " TEXT NOT NULL," + DOCUMENT_OWNER_ID
						+ " TEXT NOT NULL," + SHAREDNAME + " TEXT NOT NULL ,"
						+ SHARDDATE + " TEXT NOT NULL);");

				/*
				 * db.execSQL("CREATE TABLE " +OCCASSION_TABLE + " ( "
				 * +OCCASSION_TYPE+ " TEXT NOT NULL, " +CREATION_DATE+
				 * " TEXT NOT NULL, " + OCCASSION_ID+ "TEXT NOT NULL );");
				 */
			} catch (SQLiteException e) {
				e.printStackTrace();

			} catch (SQLException e) {
				e.printStackTrace();

			}

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP " + COMPANY_TABLE);
			db.execSQL("DROP TABLE IF EXIST" + DOCUMENT_TABLE);
			onCreate(db);
		}
	}

	public ZoomifierDatabase(Context context) {
		this.context = context;
	}

	public ZoomifierDatabase openAndWriteDataBase() throws SQLException {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getWritableDatabase();
		return this;
	}

	public void deleteallRow() {
		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();

			sqLiteDatabase.delete("zoomifiercompanytable", null, null);
		} catch (SQLException e) {
			e.printStackTrace();

		}

	}
	public void deleteallQrTable() {
		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();

			sqLiteDatabase.delete(QRCODETABLE, null, null);
		} catch (SQLException e) {
			e.printStackTrace();

		}

	}
	/*public long insertIntoPageTable(String docID, String PangeNo) {

		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put(DOCUMENT_ID, docID);
			values.put(PAGE_NO, PangeNo);

			long l = sqLiteDatabase.insert(PAGE_TABLE, null, values);
			dataBaseObject.close();
			return l;

		} catch (SQLiteException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (SQLException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (Exception e) {
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;

		}
	}*/

	public void deletealreviw() {
		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();

			sqLiteDatabase.delete(DOCUMENT_REVIEW_TABLE, null, null);
		} catch (SQLException e) {
			e.printStackTrace();

		}

	}

	public void openAndReadCompanyTable() throws SQLException {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();

		String[] columns = null;
		Cursor c;

		companyId.clear();
		companyName.clear();
		imageformat.clear();

		columns = new String[] { COMPANY_ID, COMPANY_NAME, IMAGE_FORMAT };
		c = sqLiteDatabase.query(COMPANY_TABLE, columns, null, null, null,
				null, null);
		if (c != null) {
			int indexCompanyId = c.getColumnIndex(COMPANY_ID);
			int indexTO_DO_LIST_TITEL = c.getColumnIndex(COMPANY_NAME);
			int indexIMAGE_FORMAT = c.getColumnIndex(IMAGE_FORMAT);
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				companyId.add(c.getString(indexCompanyId));
				companyName.add(c.getString(indexTO_DO_LIST_TITEL));
				imageformat.add(c.getString(indexIMAGE_FORMAT));
			}
		}
		c.close();
		// dataBaseObject.close();
	}
	public void findVender(String clientId) throws SQLException {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();

		String[] columns = null;
		Cursor c;

		companyId.clear();
		companyName.clear();
		imageformat.clear();
		String condition = COMPANY_ID + " = \"" +clientId + "\"";
		columns = new String[] { COMPANY_ID, COMPANY_NAME, IMAGE_FORMAT };
		c = sqLiteDatabase.query(COMPANY_TABLE, columns, condition, null, null,
				null, null);
		if (c != null) {
			int indexCompanyId = c.getColumnIndex(COMPANY_ID);
			int indexTO_DO_LIST_TITEL = c.getColumnIndex(COMPANY_NAME);
			int indexIMAGE_FORMAT = c.getColumnIndex(IMAGE_FORMAT);
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				companyId.add(c.getString(indexCompanyId));
				companyName.add(c.getString(indexTO_DO_LIST_TITEL));
				imageformat.add(c.getString(indexIMAGE_FORMAT));
			}
		}
		c.close();
		// dataBaseObject.close();
	}

	public void openAndReadQRTable() throws SQLException {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		try {
			String[] columns = null;
			Cursor c;

			qrName.clear();
			qrLike.clear();
			scanedate.clear();
			qrtitle.clear();
			columns = new String[] { QRCODENAME, QRCODELIKE, QRCODESCANEDATE,
					QRTITTLE };
			c = sqLiteDatabase.query(QRCODETABLE, columns, null, null, null,
					null, null);
			if (c != null) {
				int indexCompanyId = c.getColumnIndex(QRCODENAME);
				int indexTO_DO_LIST_TITEL = c.getColumnIndex(QRCODELIKE);
				int indexscanedate = c.getColumnIndex(QRCODESCANEDATE);
				int indexqrtitle = c.getColumnIndex(QRTITTLE);

				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					qrName.add(c.getString(indexCompanyId));
					qrLike.add(c.getString(indexTO_DO_LIST_TITEL));
					qrtitle.add(c.getString(indexqrtitle));
					scanedate.add(c.getString(indexscanedate));

				}
			}
			c.close();
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public void openAndReadPageAnalytic() throws SQLException {
		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();

			String[] columns = null;
			Cursor c;

			pageAnayltic_docid.clear();
			pageAnalytic_clientid.clear();
			pageAnalytic_starttime.clear();
			pageAnalytic_endtime.clear();
			pageAnalytic_pageno.clear();
			pageAnalytic_searchtag.clear();
			pageAnalytic_reader_id.clear();
			pageAnalytic_first_object.clear();
			pageAnalytic_second_object.clear();
			pageAnalytic_share_to.clear();
			pageAnalytic_share_type.clear();
			pageAnalytic_content_type.clear();
			pageAnalytic_location.clear();
			pageAnalytic_eventtype.clear();
			time.clear();
			columns = new String[] { DOCUMENT_ID, PAGE_NO, CLIENT_ID,
					START_TIME, END_TIME, DOCUMENT_CONTENTTYPE, SEARCH_TAG,
					LOCATION, OBJECT_FIRST, OBJECT_SECOND, ANLYTICS_READERID,
					DOCUMENT_ANALYTIC_SHARE_TYPE, DOCUMETN_ANYLYTIC_SHARETO,
					EVENT_TYPE };
			c = sqLiteDatabase.query(PAGE_ANALYTICS, columns, null, null, null,
					null, null);
			if (c != null) {
				int indexdocid = c.getColumnIndex(DOCUMENT_ID);
				int indexpageno = c.getColumnIndex(PAGE_NO);
				int indexclientid = c.getColumnIndex(CLIENT_ID);
				int indexstarttime = c.getColumnIndex(START_TIME);
				int indexendtime = c.getColumnIndex(END_TIME);
				int indexsearchtag = c.getColumnIndex(SEARCH_TAG);
				int indexlocation = c.getColumnIndex(LOCATION);
				int indexobjectfirst = c.getColumnIndex(OBJECT_FIRST);
				int indexobject2 = c.getColumnIndex(OBJECT_SECOND);
				int indexreaderid = c.getColumnIndex(ANLYTICS_READERID);
				int indexsharetype = c
						.getColumnIndex(DOCUMENT_ANALYTIC_SHARE_TYPE);
				int indexshareto = c.getColumnIndex(DOCUMETN_ANYLYTIC_SHARETO);
				int indexcontenttype = c.getColumnIndex(DOCUMENT_CONTENTTYPE);
				int indexeventtype = c.getColumnIndex(EVENT_TYPE);
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					pageAnayltic_docid.add(c.getString(indexdocid));
					pageAnalytic_clientid.add(c.getString(indexclientid));
					pageAnalytic_starttime.add(c.getString(indexstarttime));
					pageAnalytic_endtime.add(c.getString(indexendtime));
					pageAnalytic_searchtag.add(c.getString(indexsearchtag));
					pageAnalytic_location.add(c.getString(indexlocation));
					pageAnalytic_pageno.add(c.getString(indexpageno));
					pageAnalytic_reader_id.add(c.getString(indexreaderid));
					pageAnalytic_first_object
							.add(c.getString(indexobjectfirst));
					pageAnalytic_second_object.add(c.getString(indexobject2));
					pageAnalytic_share_to.add(c.getString(indexshareto));
					pageAnalytic_share_type.add(c.getString(indexsharetype));
					pageAnalytic_content_type
							.add(c.getString(indexcontenttype));
					pageAnalytic_eventtype.add(c.getString(indexeventtype));

				}
				c.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public long insertIntoPageAnaylytics(String docID, String pageNo,
			String startTime, String endtime, String location,
			String searchtag, String clientid, String readerId,
			String shareType, String shareTo, String contentType,
			String object1, String object2, String eventtype) {
		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put(DOCUMENT_ID, docID);
			values.put(CLIENT_ID, clientid);
			values.put(PAGENO, pageNo);
			values.put(START_TIME, startTime);
			values.put(END_TIME, endtime);
			values.put(LOCATION, location);
			values.put(SEARCH_TAG, searchtag);
			values.put(ANLYTICS_READERID, readerId);
			values.put(DOCUMENT_ANALYTIC_SHARE_TYPE, shareType);
			values.put(OBJECT_FIRST, object1);
			values.put(OBJECT_SECOND, object2);
			values.put(DOCUMETN_ANYLYTIC_SHARETO, shareTo);
			values.put(DOCUMENT_CONTENTTYPE, contentType);
			values.put(EVENT_TYPE, eventtype);

			long l = sqLiteDatabase.insert(PAGE_ANALYTICS, null, values);
			return l;

		} catch (SQLiteException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (SQLException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (Exception e) {
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;

		}
	}

	public void deletAnayticsData() {
		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();

			sqLiteDatabase.delete(PAGE_ANALYTICS, null, null);
		} catch (SQLException e) {
			e.printStackTrace();

		}

	}

	public void openAndReadReviewTable(String documentID) throws SQLException {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();

		String[] columns = null;
		Cursor c;
		String condition = DOCUMENT_ID + " = \"" + documentID + "\"";
		documentreviewtitle.clear();
		documentreview.clear();
		noforeviewstar.clear();
		time.clear();

		columns = new String[] { DOCUMENT_ID, DOCUMENT_REVIEW_TITLE,
				NO_OF_STAR, REVIEW_DATE, DOCUMENT_REVIEW, };
		c = sqLiteDatabase.query(DOCUMENT_REVIEW_TABLE, columns, condition,
				null, null, null, null);
		if (c != null) {
			int indexCompanyId = c.getColumnIndex(DOCUMENT_REVIEW_TITLE);
			int indexTO_DO_LIST_TITEL = c.getColumnIndex(DOCUMENT_REVIEW);
			int indexNoofstar = c.getColumnIndex(NO_OF_STAR);
			int indexrevieTime = c.getColumnIndex(REVIEW_DATE);
			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				documentreviewtitle.add(c.getString(indexCompanyId));
				documentreview.add(c.getString(indexTO_DO_LIST_TITEL));
				noforeviewstar.add(c.getString(indexNoofstar));
				time.add(c.getString(indexrevieTime));
			}
		}
		c.close();
		// dataBaseObject.close();
	}

	public long insertIntoReviewTable(String docID, String reviewtitle,
			String review, String noofstar, String time) {

		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put(DOCUMENT_ID, docID);
			values.put(DOCUMENT_REVIEW_TITLE, reviewtitle);
			values.put(DOCUMENT_REVIEW, review);
			values.put(NO_OF_STAR, noofstar);
			values.put(REVIEW_DATE, time);

			long l = sqLiteDatabase.insert(DOCUMENT_REVIEW_TABLE, null, values);
			// dataBaseObject.close();
			return l;

		} catch (SQLiteException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (SQLException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (Exception e) {
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;

		}
	}

	public long insertIntoPageTable(String docID, String PangeNo,
			String SharedName, String SharedDate) {

		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put(DOCUMENT_ID, docID);
			values.put(PAGE_NO, PangeNo);
			values.put(SHAREDNAME, SharedName);
			values.put(SHARDDATE, SharedDate);

			long l = sqLiteDatabase.insert(PAGE_TABLE, null, values);
			dataBaseObject.close();
			return l;

		} catch (SQLiteException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (SQLException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (Exception e) {
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;

		}
	}

	/*
	 * public void close() { dataBaseObject.close(); }
	 */
	public long insertIntoCompanyTable(String companyID, String comapnyName,
			String imageformat) {

		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put(COMPANY_ID, companyID);
			values.put(COMPANY_NAME, comapnyName);
			values.put(IMAGE_FORMAT, imageformat);

			long l = sqLiteDatabase.insert(COMPANY_TABLE, null, values);
			dataBaseObject.close();
			return l;

		} catch (SQLiteException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (SQLException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (Exception e) {
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;

		}

	}

	public long insertIntoQRTable(String qrName, String like,
			String qrscanedate, String qrtitle) {

		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put(QRCODENAME, qrName);
			values.put(QRCODELIKE, like);
			values.put(QRCODESCANEDATE, qrscanedate);
			values.put(QRTITTLE, qrtitle);
			long l = sqLiteDatabase.insert(QRCODETABLE, null, values);
			dataBaseObject.close();
			return l;

		} catch (SQLiteException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (SQLException e) {
			e.printStackTrace();
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;
		} catch (Exception e) {
			dataBaseObject = new DataBaseObject(context);
			Log.i("Data Base Error", e.toString());
			return -6;

		}

	}

	public long insertIntoDocumentTable(String documentId, String documentName,
			String documentDiscription, String orginalId,
			String documentContentType, String documentContentWidth,
			String documentContentHeight, String documentOwnerId,
			String like_document, String SharedName, String isshared,
			String sharedId, String SharedDate, String order, String pagecount,
			String client_id, String clientName, String clientformat,
			String userID) {

		try {
			dataBaseObject = new DataBaseObject(context);
			sqLiteDatabase = dataBaseObject.getReadableDatabase();
			ContentValues values = new ContentValues();
			values.put(DOCUMENT_ID, documentId);
			values.put(DOCUMENT_NAME, documentName);
			values.put(DOCUMENT_DISCRIPTION, documentDiscription);
			values.put(DOCUMENT_OWNER_ID, documentOwnerId);
			values.put(DOCUMENT_CONTENT_WIDTH, documentContentWidth);
			values.put(DOCUMENT_CONTENT_HEIGHT, documentContentHeight);
			values.put(DOCUMENT_CONTENTTYPE, documentContentType);
			values.put(DOCUMENT_LIKE, like_document);
			values.put(DOCUMENT_CLIENT_ID, client_id);
			values.put(DOCUMENT_USER_ID, userID);
			values.put(SHAREDNAME, SharedName);
			values.put(SHARDDATE, SharedDate);
			values.put(IMAGE_FORMAT, clientformat);
			values.put(ORDER, order);
			values.put(IS_SHARED, isshared);
			values.put(CLIENT_NAME, clientName);
			values.put(DOCUMENT_ORGINAL_ID, clientName);
			values.put(SHARED_ID, sharedId);
			values.put(PAGE_COUNT, sharedId);

			long l = sqLiteDatabase.insert(DOCUMENT_TABLE, null, values);
			dataBaseObject.close();
			return l;
		} catch (Exception e) {
			dataBaseObject.close();
			Log.i("Data Base Error", e.toString());
			return -6;
		}
	}

	public void openAndReadDocumentTable(String clientid) {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		documentName.clear();
		documentID.clear();
		documentDiscription.clear();
		documentorginalID.clear();
		documentcontntwidth.clear();
		documentcontetntype.clear();
		documentcontenntheight.clear();
		documentownerid.clear();
		documentlike.clear();
		documentclientid.clear();
		imageid.clear();
		sharedDate.clear();
		sharedName.clear();
		clientname.clear();
		pagecount.clear();
		isshared.clear();
		order.clear();
		sharedId.clear();
		pagecount.clear();
		String condition = DOCUMENT_CLIENT_ID + " = \"" + clientid + "\"";
		columns = new String[] { DOCUMENT_ID, DOCUMENT_NAME,
				DOCUMENT_DISCRIPTION, DOCUMENT_ORGINAL_ID,
				DOCUMENT_CONTENTTYPE, DOCUMENT_CONTENT_HEIGHT,
				DOCUMENT_CONTENT_WIDTH, DOCUMENT_OWNER_ID, DOCUMENT_LIKE,
				DOCUMENT_CLIENT_ID, DOCUMENT_USER_ID, SHAREDNAME, SHARDDATE,
				ORDER, SHARED_ID, CLIENT_NAME, IS_SHARED, IMAGE_FORMAT,
				PAGE_COUNT };
		c = sqLiteDatabase.query(DOCUMENT_TABLE, columns, condition, null,
				null, null, null);
		if (c != null) {
			int indexdocumentid = c.getColumnIndex(DOCUMENT_ID);
			int indexdocumentdis = c.getColumnIndex(DOCUMENT_DISCRIPTION);
			int indexdocumentname = c.getColumnIndex(DOCUMENT_NAME);
			int indexdocumentownerid = c.getColumnIndex(DOCUMENT_OWNER_ID);
			int indexdocumentorginalid = c.getColumnIndex(DOCUMENT_ORGINAL_ID);
			int indexdocumentcontentwidth = c
					.getColumnIndex(DOCUMENT_CONTENT_WIDTH);
			int indexdocumentcontentheight = c
					.getColumnIndex(DOCUMENT_CONTENT_HEIGHT);
			int indexdocumentcontentid = c.getColumnIndex(DOCUMENT_CONTENTTYPE);
			int indexclientid = c.getColumnIndex(DOCUMENT_CLIENT_ID);
			int indexlike = c.getColumnIndex(DOCUMENT_LIKE);
			int indeximageid = c.getColumnIndex(DOCUMENT_USER_ID);
			int indexsharedName = c.getColumnIndex(SHAREDNAME);
			int indexshareDate = c.getColumnIndex(SHARDDATE);
			int indexpagecount = c.getColumnIndex(PAGE_COUNT);
			int indexisshared = c.getColumnIndex(IS_SHARED);
			int indexorder = c.getColumnIndex(ORDER);
			int indeximageformat = c.getColumnIndex(IMAGE_FORMAT);
			int indexshardId = c.getColumnIndex(SHARED_ID);
			int indexclientname = c.getColumnIndex(CLIENT_NAME);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				documentID.add(c.getString(indexdocumentid));
				documentName.add(c.getString(indexdocumentname));
				documentDiscription.add(c.getString(indexdocumentdis));
				documentorginalID.add(c.getString(indexdocumentorginalid));
				documentcontntwidth.add(c.getString(indexdocumentcontentwidth));
				documentcontenntheight.add(c
						.getString(indexdocumentcontentheight));
				documentcontetntype.add(c.getString(indexdocumentcontentid));
				documentownerid.add(c.getString(indexdocumentownerid));
				documentclientid.add(c.getString(indexclientid));
				documentlike.add(c.getString(indexlike));
				imageid.add(c.getString(indeximageid));
				sharedName.add(c.getString(indexsharedName));
				sharedDate.add(c.getString(indexshareDate));
				pagecount.add(c.getString(indexpagecount));
				isshared.add(c.getString(indexisshared));
				order.add(c.getString(indexorder));
				imageformat.add(c.getString(indeximageformat));
				sharedId.add(c.getString(indexshardId));
				clientname.add(c.getString(indexclientname));

			}
		}
		c.close();
		dataBaseObject.close();

	}

	public void FindDocument(String documetnID) {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		documentName.clear();
		documentID.clear();
		documentDiscription.clear();
		documentorginalID.clear();
		documentcontntwidth.clear();
		documentcontetntype.clear();
		documentcontenntheight.clear();
		documentownerid.clear();
		documentlike.clear();
		documentclientid.clear();
		imageid.clear();
		sharedDate.clear();
		sharedName.clear();
		clientname.clear();
		pagecount.clear();
		isshared.clear();
		order.clear();
		sharedId.clear();
		pagecount.clear();
		String condition = DOCUMENT_ID + " = \"" + documetnID + "\"";
		columns = new String[] { DOCUMENT_ID, DOCUMENT_NAME,
				DOCUMENT_DISCRIPTION, DOCUMENT_ORGINAL_ID,
				DOCUMENT_CONTENTTYPE, DOCUMENT_CONTENT_HEIGHT,
				DOCUMENT_CONTENT_WIDTH, DOCUMENT_OWNER_ID, DOCUMENT_LIKE,
				DOCUMENT_CLIENT_ID, DOCUMENT_USER_ID, SHAREDNAME, SHARDDATE,
				ORDER, SHARED_ID, CLIENT_NAME, IS_SHARED, IMAGE_FORMAT,
				PAGE_COUNT };
		c = sqLiteDatabase.query(DOCUMENT_TABLE, columns, condition, null,
				null, null, null);
		if (c != null) {
			int indexdocumentid = c.getColumnIndex(DOCUMENT_ID);
			int indexdocumentdis = c.getColumnIndex(DOCUMENT_DISCRIPTION);
			int indexdocumentname = c.getColumnIndex(DOCUMENT_NAME);
			int indexdocumentownerid = c.getColumnIndex(DOCUMENT_OWNER_ID);
			int indexdocumentorginalid = c.getColumnIndex(DOCUMENT_ORGINAL_ID);
			int indexdocumentcontentwidth = c
					.getColumnIndex(DOCUMENT_CONTENT_WIDTH);
			int indexdocumentcontentheight = c
					.getColumnIndex(DOCUMENT_CONTENT_HEIGHT);
			int indexdocumentcontentid = c.getColumnIndex(DOCUMENT_CONTENTTYPE);
			int indexclientid = c.getColumnIndex(DOCUMENT_CLIENT_ID);
			int indexlike = c.getColumnIndex(DOCUMENT_LIKE);
			int indeximageid = c.getColumnIndex(DOCUMENT_USER_ID);
			int indexsharedName = c.getColumnIndex(SHAREDNAME);
			int indexshareDate = c.getColumnIndex(SHARDDATE);
			int indexpagecount = c.getColumnIndex(PAGE_COUNT);
			int indexisshared = c.getColumnIndex(IS_SHARED);
			int indexorder = c.getColumnIndex(ORDER);
			int indeximageformat = c.getColumnIndex(IMAGE_FORMAT);
			int indexshardId = c.getColumnIndex(SHARED_ID);
			int indexclientname = c.getColumnIndex(CLIENT_NAME);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				documentID.add(c.getString(indexdocumentid));
				documentName.add(c.getString(indexdocumentname));
				documentDiscription.add(c.getString(indexdocumentdis));
				documentorginalID.add(c.getString(indexdocumentorginalid));
				documentcontntwidth.add(c.getString(indexdocumentcontentwidth));
				documentcontenntheight.add(c
						.getString(indexdocumentcontentheight));
				documentcontetntype.add(c.getString(indexdocumentcontentid));
				documentownerid.add(c.getString(indexdocumentownerid));
				documentclientid.add(c.getString(indexclientid));
				documentlike.add(c.getString(indexlike));
				imageid.add(c.getString(indeximageid));
				sharedName.add(c.getString(indexsharedName));
				sharedDate.add(c.getString(indexshareDate));
				pagecount.add(c.getString(indexpagecount));
				isshared.add(c.getString(indexisshared));
				order.add(c.getString(indexorder));
				imageformat.add(c.getString(indeximageformat));
				sharedId.add(c.getString(indexshardId));
				clientname.add(c.getString(indexclientname));

			}
		}
		c.close();
		dataBaseObject.close();

	}

	public void openAndReadAllDocumentTable() {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		documentName.clear();
		documentID.clear();
		documentDiscription.clear();
		documentorginalID.clear();
		documentcontntwidth.clear();
		documentcontetntype.clear();
		documentcontenntheight.clear();
		documentownerid.clear();
		documentlike.clear();
		documentclientid.clear();
		imageid.clear();
		sharedDate.clear();
		sharedName.clear();
		clientname.clear();
		pagecount.clear();
		isshared.clear();
		order.clear();
		sharedId.clear();
		pagecount.clear();

		columns = new String[] { DOCUMENT_ID, DOCUMENT_NAME,
				DOCUMENT_DISCRIPTION, DOCUMENT_ORGINAL_ID,
				DOCUMENT_CONTENTTYPE, DOCUMENT_CONTENT_HEIGHT,
				DOCUMENT_CONTENT_WIDTH, DOCUMENT_OWNER_ID, DOCUMENT_LIKE,
				DOCUMENT_CLIENT_ID, DOCUMENT_USER_ID, SHAREDNAME, SHARDDATE,
				ORDER, SHARED_ID, CLIENT_NAME, IS_SHARED, IMAGE_FORMAT,
				PAGE_COUNT };
		c = sqLiteDatabase.query(DOCUMENT_TABLE, columns, null, null, null,
				null, null);
		if (c != null) {
			int indexdocumentid = c.getColumnIndex(DOCUMENT_ID);
			int indexdocumentdis = c.getColumnIndex(DOCUMENT_DISCRIPTION);
			int indexdocumentname = c.getColumnIndex(DOCUMENT_NAME);
			int indexdocumentownerid = c.getColumnIndex(DOCUMENT_OWNER_ID);
			int indexdocumentorginalid = c.getColumnIndex(DOCUMENT_ORGINAL_ID);
			int indexdocumentcontentwidth = c
					.getColumnIndex(DOCUMENT_CONTENT_WIDTH);
			int indexdocumentcontentheight = c
					.getColumnIndex(DOCUMENT_CONTENT_HEIGHT);
			int indexdocumentcontentid = c.getColumnIndex(DOCUMENT_CONTENTTYPE);
			int indexclientid = c.getColumnIndex(DOCUMENT_CLIENT_ID);
			int indexlike = c.getColumnIndex(DOCUMENT_LIKE);
			int indeximageid = c.getColumnIndex(DOCUMENT_USER_ID);
			int indexsharedName = c.getColumnIndex(SHAREDNAME);
			int indexshareDate = c.getColumnIndex(SHARDDATE);
			int indexpagecount = c.getColumnIndex(PAGE_COUNT);
			int indexisshared = c.getColumnIndex(IS_SHARED);
			int indexorder = c.getColumnIndex(ORDER);
			int indeximageformat = c.getColumnIndex(IMAGE_FORMAT);
			int indexshardId = c.getColumnIndex(SHARED_ID);
			int indexclientname = c.getColumnIndex(CLIENT_NAME);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				documentID.add(c.getString(indexdocumentid));
				documentName.add(c.getString(indexdocumentname));
				documentDiscription.add(c.getString(indexdocumentdis));
				documentorginalID.add(c.getString(indexdocumentorginalid));
				documentcontntwidth.add(c.getString(indexdocumentcontentwidth));
				documentcontenntheight.add(c
						.getString(indexdocumentcontentheight));
				documentcontetntype.add(c.getString(indexdocumentcontentid));
				documentownerid.add(c.getString(indexdocumentownerid));
				documentclientid.add(c.getString(indexclientid));
				documentlike.add(c.getString(indexlike));
				imageid.add(c.getString(indeximageid));
				sharedName.add(c.getString(indexsharedName));
				sharedDate.add(c.getString(indexshareDate));
				pagecount.add(c.getString(indexpagecount));
				isshared.add(c.getString(indexisshared));
				order.add(c.getString(indexorder));
				imageformat.add(c.getString(indeximageformat));
				sharedId.add(c.getString(indexshardId));
				clientname.add(c.getString(indexclientname));

			}
		}
		c.close();
		dataBaseObject.close();

	}

	public String openAndReadDocumentLikeUnlike(String docid) {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		String like = null;
		documentlike.clear();

		String condition = DOCUMENT_ID + " = \"" + docid + "\"";
		columns = new String[] { DOCUMENT_LIKE };
		c = sqLiteDatabase.query(DOCUMENT_TABLE, columns, condition, null,
				null, null, null);
		if (c != null) {

			int indexlike = c.getColumnIndex(DOCUMENT_LIKE);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				like = c.getString(indexlike);

			}
		}
		c.close();
		dataBaseObject.close();
		return like;

	}

	public void openAndReadPageDocID() {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		String like = null;
		documentID.clear();

		columns = new String[] { DOCUMENT_ID };
		c = sqLiteDatabase.query(PAGE_TABLE, columns, null, null, null, null,
				null);
		if (c != null) {

			int indexlike = c.getColumnIndex(DOCUMENT_ID);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				documentID.add(c.getString(indexlike));

			}
		}
		c.close();
		dataBaseObject.close();

	}

	public String openAndReadPageTable(String docid) {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		String like = null;

		String condition = DOCUMENT_ID + " = \"" + docid + "\"";
		columns = new String[] { PAGE_NO };
		c = sqLiteDatabase.query(PAGE_TABLE, columns, condition, null, null,
				null, null);
		if (c != null) {

			int indexlike = c.getColumnIndex(PAGE_NO);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				like = c.getString(indexlike);

			}
		}
		c.close();
		dataBaseObject.close();
		return like;

	}

	public String openAndReadPageTableSharedName(String docid) {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		String like = null;

		String condition = DOCUMENT_ID + " = \"" + docid + "\"";
		columns = new String[] { SHAREDNAME };
		c = sqLiteDatabase.query(PAGE_TABLE, columns, condition, null, null,
				null, null);
		if (c != null) {

			int indexlike = c.getColumnIndex(SHAREDNAME);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				like = c.getString(indexlike);

			}
		}
		c.close();
		dataBaseObject.close();
		return like;

	}

	public String openAndReadPageTableSHaredDate(String docid) {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		String like = null;

		String condition = DOCUMENT_ID + " = \"" + docid + "\"";
		columns = new String[] { SHARDDATE };
		c = sqLiteDatabase.query(PAGE_TABLE, columns, condition, null, null,
				null, null);
		if (c != null) {

			int indexlike = c.getColumnIndex(SHARDDATE);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				like = c.getString(indexlike);

			}
		}
		c.close();
		dataBaseObject.close();
		return like;

	}

	public void openAndReadClientID() {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		documentclientid.clear();

		columns = new String[] { DOCUMENT_ID, DOCUMENT_NAME,
				DOCUMENT_DISCRIPTION, DOCUMENT_ORGINAL_ID,
				DOCUMENT_CONTENTTYPE, DOCUMENT_CONTENT_HEIGHT,
				DOCUMENT_CONTENT_WIDTH, DOCUMENT_OWNER_ID, DOCUMENT_LIKE,
				DOCUMENT_CLIENT_ID };
		c = sqLiteDatabase.query(DOCUMENT_TABLE, columns, null, null, null,
				null, null);
		if (c != null) {

			int indexclientid = c.getColumnIndex(DOCUMENT_CLIENT_ID);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

				documentclientid.add(c.getString(indexclientid));

			}
		}
		c.close();
		dataBaseObject.close();

	}

	public void openAndReadDocumentLikeTable() {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		documentName.clear();
		documentID.clear();
		documentDiscription.clear();
		documentorginalID.clear();
		documentcontntwidth.clear();
		documentcontetntype.clear();
		documentcontenntheight.clear();
		documentownerid.clear();
		documentlike.clear();
		documentclientid.clear();
		imageid.clear();
		sharedName.clear();
		sharedDate.clear();
		String condition = DOCUMENT_LIKE + " = \"" + "1" + "\"";
		columns = new String[] { DOCUMENT_ID, DOCUMENT_NAME,
				DOCUMENT_DISCRIPTION, DOCUMENT_ORGINAL_ID,
				DOCUMENT_CONTENTTYPE, DOCUMENT_CONTENT_HEIGHT,
				DOCUMENT_CONTENT_WIDTH, DOCUMENT_OWNER_ID, DOCUMENT_LIKE,
				DOCUMENT_CLIENT_ID, DOCUMENT_USER_ID, SHAREDNAME, SHARDDATE };
		c = sqLiteDatabase.query(DOCUMENT_TABLE, columns, condition, null,
				null, null, null);
		if (c != null) {
			int indexdocumentid = c.getColumnIndex(DOCUMENT_ID);
			int indexdocumentdis = c.getColumnIndex(DOCUMENT_DISCRIPTION);
			int indexdocumentname = c.getColumnIndex(DOCUMENT_NAME);
			int indexdocumentownerid = c.getColumnIndex(DOCUMENT_OWNER_ID);
			int indexdocumentorginalid = c.getColumnIndex(DOCUMENT_ORGINAL_ID);
			int indexdocumentcontentwidth = c
					.getColumnIndex(DOCUMENT_CONTENT_WIDTH);
			int indexdocumentcontentheight = c
					.getColumnIndex(DOCUMENT_CONTENT_HEIGHT);
			int indexdocumentcontentid = c.getColumnIndex(DOCUMENT_CONTENTTYPE);
			int indexclientid = c.getColumnIndex(DOCUMENT_CLIENT_ID);
			int indexlike = c.getColumnIndex(DOCUMENT_LIKE);
			int indeximageid = c.getColumnIndex(DOCUMENT_USER_ID);
			int indexsharedname = c.getColumnIndex(SHAREDNAME);
			int indexsharedate = c.getColumnIndex(SHARDDATE);

			for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
				documentID.add(c.getString(indexdocumentid));
				documentName.add(c.getString(indexdocumentname));
				documentDiscription.add(c.getString(indexdocumentdis));
				documentorginalID.add(c.getString(indexdocumentorginalid));
				documentcontntwidth.add(c.getString(indexdocumentcontentwidth));
				documentcontenntheight.add(c
						.getString(indexdocumentcontentheight));
				documentcontetntype.add(c.getString(indexdocumentcontentid));
				documentownerid.add(c.getString(indexdocumentownerid));
				documentclientid.add(c.getString(indexclientid));
				documentlike.add(c.getString(indexlike));
				imageid.add(c.getString(indeximageid));
				sharedName.add(c.getString(indexsharedname));
				sharedDate.add(c.getString(indexsharedate));
			}

		}
		c.close();
		dataBaseObject.close();

	}

	public void updateDocumentLikes(String documentid) {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(DOCUMENT_LIKE, "1");
			String condition = DOCUMENT_ID + "=\"" + documentid + "\"";
			sqLiteDatabase.update(DOCUMENT_TABLE, values, condition, null);
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updateQRTitle(String qr_title, String url) {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(QRTITTLE, url);
			String condition = QRCODENAME + "=\"" + url + "\"";
			sqLiteDatabase.update(QRCODETABLE, values, condition, null);
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void updateDocumentUnlikes(String documentid) {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		try {
			ContentValues values = new ContentValues();
			values.put(DOCUMENT_LIKE, "0");
			String condition = DOCUMENT_ID + "=\"" + documentid + "\"";
			sqLiteDatabase.update(DOCUMENT_TABLE, values, condition, null);
		}

		catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void deleteGroupMember(String groupName) throws SQLException {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String condition = MEMBER_NAME + " = \"" + groupName + "\"";
		sqLiteDatabase.delete(DOCUMENT_TABLE, condition, null);

	}
	public void deleteDocumentRow(String documetnID) throws SQLException {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String condition = DOCUMENT_ID + " = \"" +documetnID + "\"";
		sqLiteDatabase.delete(DOCUMENT_TABLE, condition, null);

	}
	public void deleteClientDocument(String clientId) throws SQLException {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String condition = DOCUMENT_CLIENT_ID + " = \"" +clientId + "\"";
		sqLiteDatabase.delete(DOCUMENT_TABLE, condition, null);

	}
	
	public void deleteVender(String clientId) throws SQLException {
		try{
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String condition = COMPANY_ID+ " = \"" +clientId+ "\"";
		sqLiteDatabase.delete(COMPANY_TABLE, condition, null);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	public void deleteGroup(String groupName) throws SQLException {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String condition = COMPANY_NAME + " = \"" + groupName + "\"";
		sqLiteDatabase.delete(DOCUMENT_TABLE, condition, null);
		sqLiteDatabase.delete(COMPANY_TABLE, condition, null);

	}

	public void insertIntoOccassionTable(String occassion, String creationDate,
			String occasionId) {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		ContentValues values = new ContentValues();
		// String condition = COMPANY_NAME + " = \"" + groupName + "\"";
		values.put(OCCASSION_TYPE, occassion);
		values.put(CREATION_DATE, creationDate);
		values.put(OCCASSION_ID, occasionId);
		sqLiteDatabase.insert(OCCASSION_TABLE, null, values);
	}

	public void openandReadOccassionDatabase() {
		dataBaseObject = new DataBaseObject(context);
		sqLiteDatabase = dataBaseObject.getReadableDatabase();
		String[] columns = null;
		Cursor c;
		occassionType.clear();
		creationDate.clear();
		occassionId.clear();
		try {
			columns = new String[] { OCCASSION_TYPE, CREATION_DATE,
					OCCASSION_ID };
			c = sqLiteDatabase.query(OCCASSION_TABLE, columns, null, null,
					null, null, null);
			if (c != null) {
				int indexOccassionType = c.getColumnIndex(OCCASSION_TYPE);
				int indexTOccassionDate = c.getColumnIndex(CREATION_DATE);
				int indexOfoccasionId = c.getColumnIndex(OCCASSION_ID);
				for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
					occassionType.add(c.getString(indexOccassionType));
					creationDate.add(c.getString(indexTOccassionDate));
					occassionId.add(c.getString(indexOfoccasionId));
				}
			}
			c.close();
		} catch (Exception e)

		{
			e.printStackTrace();

		}

	}

	public void close() {
		dataBaseObject.close();
	}

}
