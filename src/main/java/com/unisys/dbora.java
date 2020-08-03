/**-----------------------------------------------------------------------------
 * Filename: dbora.java
 *     @(#)dbora.java /ismdv/codeBase/ISM/websites/com/unisys/ism/utils/SCCS/s.dbora.java
 *
 * Modification:
 *    Date    Programmer    Description
 *    ----    ----------    ---------------------------------------------------
 *  04/25/01  TLIN          Added getSID()
 *  05/08/01  TLIN          Added getHost(), setHost()
 *  10/16/01  TLIN          Added Status class to aid determining the status of
 *                          this class.
 *  12/03/01  GSHAO         Changed openSql for making connection only if !connected
 *  02/14/02  TLIN          Changed Statement to PreparedStatment object
 *  05/06/02  ROBIN         Added method openCallX() for calling procedures with only input parameters.
 *  12/12/02  ELAP          Removed the hardcode "fred/betty" and read these values from .setup.properties.
 *---------------------------------------------------------------------------**/

package com.unisys;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.*;

//import com.unisys.ism.c2kserv.sysdtg;
//import com.unisys.ism.utils.*;


public class dbora {
  public static PropertyFileReader prop = new PropertyFileReader();	
  public static StringBuilder urlSb = new StringBuilder();
  //DEV SQL SAMPLE URL - "jdbc:sqlserver://172.29.110.224:1433;user=sascargouser;password=Sasportal123$;database=SASCARGOGCPROD";
  //DEV ORACLE SAMPLE URL - "jdbc:oracle:thin:R45UWLMS/R45UWLMS@192.168.145.21:1521:LMSNG45";
  static{
	  
	  try {
		  /*urlSb.append(Constants.JDBCSQL);
		  urlSb.append(prop.getProperty(Constants.DB_HOST_PORT)+Constants.SEMICOLLON);
		  urlSb.append(Constants.USER+prop.getProperty(Constants.DB_USER)+Constants.SEMICOLLON);
		  urlSb.append(Constants.PASSWORD+prop.getProperty(Constants.DB_PWD)+Constants.SEMICOLLON);
		  urlSb.append(Constants.DATABASE+prop.getProperty(Constants.DB_NAME)+Constants.SEMICOLLON);*/		  
		  
		  urlSb.append(Constants.JDBCSQL + Constants.COLLON);
		  urlSb.append(prop.getProperty(Constants.DB_USER) + Constants.DB_SLASH + prop.getProperty(Constants.DB_PWD));
		  urlSb.append(prop.getProperty(Constants.DB_HOST_PORT) + Constants.COLLON + prop.getProperty(Constants.DB_NAME));
		  
		  
	} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  
  }
  
  
	
  public class Status {
    private final static int MAX_STATUS = 6;
    private final String statusDesc[] = {
      "Successfully",              //0, SUCCESS
      "JDBC Connection error",     //1, CONNECT_ERR
      "Query execution error",     //2, ResultSet err
      "Property File Read error",  //3, FILE_READ_ERR
      "JDBC Driver error",         //4, DRIVER_NOT_FOUND
      "Close connection error",    //5, CLOSING_ERR
      "Create statement error",    //6, STATMENT_ERR
      "Unknown error"
    };

    public final static int SUCCESS = 0;
    public final static int CONNECT_ERR=1;
    public final static int RESULT_ERR=2;
    public final static int FILE_READ_ERR=3;
    public final static int DRIVER_NOT_FOUND=4;
    public final static int CLOSING_ERR=5;
    public final static int STATMENT_ERR=6;
    public final static int UNKNOWN_ERR=MAX_STATUS;

    private int code;

    public Status()  { code=SUCCESS; }
    void setStatusCode(int statusCD)  {
      if (statusCD < 0 || statusCD > MAX_STATUS)  code= MAX_STATUS;
      else code = statusCD;
    }
    public int getStatusCode()  { return code;  }
    public String getStatusDesc()  { return statusDesc[code]; }
  }  // end of Status class

  public  Status      state = new Status();
  private String      host;
  private String      sid;
 
  private Connection  CON;
  private ResultSet   RES;
  private PreparedStatement   STMT;
  private boolean verbose = false;
  private boolean connected = false;
  private java.util.Date errlogtim = new java.util.Date();

  public dbora() {
    this( Constants.FRED, Constants.BETTY );
  }

  public dbora( String h, String userName, String passWord ) {
    this(userName,passWord);
    setHost(h);
  }

  public dbora( String h ) {
    this(Constants.FRED,Constants.BETTY);
    setHost(h);
  }

  public dbora( String userName, String passWord ) {
	
    //HostEnv h = new HostEnv();
    //sid = new String("CGO");
   // host = new String("localhost");

    //setSID((String) h.getEnv("ORACLE_SID"));
    
    //setHost((String) h.getEnv("ORACLE_HOST"));
	//user = new String( userName + "/" + passWord);
    

    //read the user/password from .setup.properties
    //String usrname = HostEnv.getEnv("FREDUSER");
    //String pwd = HostEnv.getEnv("FREDPWD");
    //user = new String( usrname + "/" + pwd);
     try {
         Class.forName( prop.getProperty(Constants.DRIVER_CLASS));
         System.out.println("dbora driver");
    } catch( ClassNotFoundException e ) {
    	e.printStackTrace();
      System.out.println("Unable to locate MSSQL JDBC!");
      state.setStatusCode(Status.DRIVER_NOT_FOUND);
    } catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    state.setStatusCode(Status.SUCCESS);
    makeConnString();
  }

  public void setSID(String h)  {
	  System.out.println("dbora.setSID()");
    sid = new String(h);
    System.out.println(sid);
  }
  public void setHost(String h)  {
    host = new String(h);
  }

  public String getSID()  {
    return sid;
  }

  public String getHost()  {
    return host;
  }

  public String[] sqlarray( String sql ) {
    int cnt;
    ResultSet r;
    Vector str = new Vector();
    String s[];

    r = openSql(sql);
    if( r != null ) {
      try {
        while( r.next() ) {
          String s1 = r.getString(1);
          if( s1 == null ) s1 = new String(Constants.EMPTY);
          str.addElement( s1 );
        }
        closeSql();

      } catch( SQLException se ) {
        str.addElement( new String("SQL Exception") );
        msg(Constants.EXECUTEQUERYFAILED + se.getErrorCode()+Constants.SPACE+ Constants.DATE + errlogtim );
      }
    } else {
      str.addElement( new String(Constants.DOUBLEHYPHEN) );
    }

    cnt = str.size();
    s = new String[ cnt ];
    for( int i = 0; i < cnt; i++ ) {
      s[i] = new String( (String)str.elementAt(i) );
    }
    return s;
  }


/* -------------------------------------------------------------------------
 String makeConnString() - make a JDBC connect string to the server
----------------------------------------------------------------------------*/
  private void makeConnString() {
   
    if( !connected ) {
      
       try {     
    	   
    	   //System.out.println(urlSb.toString());
    	   //CON = DriverManager.getConnection(urlSb.toString());
    	  //CON = DriverManager.getConnection("jdbc:sqlserver://172.29.110.224:1433;user=sascargouser;password=Sasportal123$;database=SASCARGOGCPROD");	
    	 //CON = DriverManager.getConnection("jdbc:sqlserver://129.227.153.83:1433;user=Sa;password=Welcome@1;database=SASCARGOGCPROD");
    	   
    	   System.out.println(prop.getProperty(Constants.DB_URL));
    	   CON = DriverManager.getConnection(prop.getProperty(Constants.DB_URL), prop.getProperty(Constants.DB_USER), prop.getProperty(Constants.DB_PWD));
    	  
    	  System.out.println("connected>"+CON);
        connected = true;
        System.out.println(connected);
      } catch( SQLException e ) {
    	  e.printStackTrace();
        msg(Constants.GETCONNECTIONFAILED + e.getErrorCode()+Constants.SPACE+ Constants.DATE + errlogtim );
        state.setStatusCode(Status.CONNECT_ERR);} catch (FileNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
    }
  }

// ----------------------------------------------------------------------------
    public void closeSql() {
      try {
        if( STMT!=null )STMT.close();
        if( RES!=null) RES.close();
        if( connected ) shutdown();
      } catch( SQLException e ) {
        msg(Constants.CLOSESQL + e.getErrorCode() +Constants.SPACE+ Constants.DATE + errlogtim);
        state.setStatusCode(Status.CLOSING_ERR);
      }
    }
// -----------------------------------------------------------------------------
  public void close() {
    shutdown();
  }

  public void shutdown()  {
    if( connected ) {
      try  {
    	  System.out.println("Connection closed");
        CON.close();
        connected = false;
      }  catch (SQLException sqlex)  {
        err(Constants.UNBLETODISCONNECT + sqlex.getErrorCode() +Constants.SPACE+ Constants.DATE + errlogtim);
        sqlex.printStackTrace();
      }
    }
  }
// -----------------------------------------------------------------------------------
  public int getCount( String sql ) {
    int    count = 0;

    if( connected ) {
      try {

          STMT   = CON.prepareStatement(sql);
        try {
          RES    = STMT.executeQuery();
          if( RES.next() ) {
            count = RES.getInt(1);
          }
          closeSql();
        } catch(SQLException e) {
          err(Constants.EXECUTEQUERYFAILED + e.getErrorCode() + Constants.SPACE + Constants.DATE + errlogtim );
          err(Constants.SQLEQUAL + sql + Constants.BRACKET );
          state.setStatusCode(Status.RESULT_ERR);
        }

      } catch( SQLException e ) {
        msg( Constants.CREATEDSTATEMENTFAILED + e.getErrorCode() +Constants.SPACE+ Constants.DATE + errlogtim );
        state.setStatusCode(Status.STATMENT_ERR);
      }
    }

    return count;
  }
  
//-----------------------------------------------------------------------------------
 public int getLength( String sql ) {
   int    length = 0;

   if( connected ) {
     try {

         STMT   = CON.prepareStatement(sql);
       try {
         RES    = STMT.executeQuery();
         if( RES.next() ) {
           length = RES.getInt(1);
         }
         closeSql();
       } catch(SQLException e) {
         err(Constants.EXECUTEQUERYFAILED + e.getErrorCode() + Constants.SPACE + Constants.DATE + errlogtim );
         err(Constants.SQLEQUAL + sql + Constants.BRACKET );
         state.setStatusCode(Status.RESULT_ERR);
       }

     } catch( SQLException e ) {
       msg(Constants.CREATEDSTATEMENTFAILED + e.getErrorCode() +Constants.SPACE+ Constants.DATE + errlogtim );
       state.setStatusCode(Status.STATMENT_ERR);
     }
   }

   return length;
 }
  
//----------------------------------------------------------------------------- 
  public String getSqlColumn(String sql){
	  String res= Constants.EMPTY ;
	  if( connected ) {
	      try {

	          STMT   = CON.prepareStatement(sql);
	        try {
	          RES    = STMT.executeQuery();
	          if( RES.next() ) {
	        	  res = RES.getString(1);
	          }
	          //closeSql();
	        } catch(SQLException e) {
	          err(Constants.GETSQLCOLUMNFAILED + e.getErrorCode() + Constants.SPACE + Constants.DATE + errlogtim );
	          err(Constants.SQLEQUAL + sql + Constants.BRACKET );
	          state.setStatusCode(Status.RESULT_ERR);
	        }

	      } catch( SQLException e ) {
	        msg(Constants.GETSQLCOLUMNFAILED + e.getErrorCode() +Constants.SPACE+ Constants.DATE + errlogtim );
	        state.setStatusCode(Status.STATMENT_ERR);
	      }
	    }

	    return res;

  }
  
  

// -----------------------------------------------------------------------------
  public boolean execute( String sql ) {
    boolean stat = false;
    if( connected ) {
      openSql(sql);
      //closeSql();
      stat = true;      
      
		try {
			if(RES != null)
			RES.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    return stat;
  }

// -----------------------------------------------------------------------------
  public ResultSet openSql( String sql ) {
	  System.out.println("dbora.openSql()");
    if( connected ) {
    	System.out.println("Connected");
    	System.out.println(sql);}
    	else{
    		makeConnString();
    	}
      try {
        STMT = CON.prepareStatement(sql);
        try {
          RES    = STMT.executeQuery();
        } catch( SQLException e ) {
          
          System.out.println("ERROR"+e.getErrorCode());            
          msg(Constants.SQLEQUAL + sql + Constants.BRACKET );
          msg(Constants.EXECUTEQUERYFAILED + e.getErrorCode() + Constants.SPACE + Constants.DATE + errlogtim );
          state.setStatusCode(Status.RESULT_ERR);
        }
        
        STMT.close();

      } catch( SQLException e ) {
        msg(Constants.CREATEDSTATEMENTFAILED + e.getErrorCode() +Constants.SPACE+ Constants.DATE + errlogtim);
        state.setStatusCode(Status.STATMENT_ERR);
      }
    
      

    return RES;
  }


// ------------------------------------------------------------------------------------
    private void fmsg( String m ) {
      boolean sv = verbose;
      verbose = true;
      msg( m );
      verbose = sv;
    }

    private void msg( String m ) {
      if( verbose ) {
        System.out.println("dbora: " + m);
      }
    }
    private void err( String msg ) {
      System.out.println( "dbora: " + msg );
    }
    
 // ------------------------------------------------------------------------------------
    
   /* public static void main(String args[]){
    	dbora test = new dbora();
    	test.makeConnString();
    	StringBuffer sQuery = new StringBuffer();
    	sQuery.append("INSERT INTO DMSFUNCTIONAUDIT(CARRIER,PORTALIDENTITY,BRANCHID,BINARYDOCUMENT,PORTALFUNCTION,SUBFUNCTION,USERID, AWBNUMBER,DOCEXTN,FILENAME,TXNSTATUS,FROMPAGE,ORIGIN,DESTINATION,DMSSTATUS,PENDINGDOCSTATUS,REFERENCENO,COMMENTS,ACCNUMBER,COMPANYNAME,DMSFORMTYPE,STATUS,SERVERNAME,IPADDRESS,ERRORTXT,FILESIZE,DOCUMENT,CONFNUMBER,BATCHEXEDATE,FILEPATH,HOSTERROR,HAWBNUMBER,VERSIONNUMBER,HOUSEVERSIONNO) VALUES('','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','')");
    	sQuery.append("INSERT INTO DMSFUNCTIONAUDIT(CARRIER,PORTALIDENTITY,BRANCHID,BINARYDOCUMENT,PORTALFUNCTION,SUBFUNCTION,USERID, AWBNUMBER,DOCEXTN,FILENAME,TXNSTATUS,FROMPAGE,ORIGIN,DESTINATION,DMSSTATUS,PENDINGDOCSTATUS,REFERENCENO,COMMENTS,ACCNUMBER,COMPANYNAME,DMSFORMTYPE,STATUS,SERVERNAME,IPADDRESS,ERRORTXT,FILESIZE,DOCUMENT,CONFNUMBER,BATCHEXEDATE,FILEPATH,HOSTERROR,HAWBNUMBER,VERSIONNUMBER,HOUSEVERSIONNO) VALUES('','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','','')");
    	test.execute(sQuery.toString());
    }*/
    
}


// GMIKE
