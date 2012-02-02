package com.signavio.warehouse.query.gateway.util;


public class Configuration {
//	private static final String CONFIG_FILENAME = "../config.xml";

    private String dbDriverName = "com.mysql.jdbc.Driver";
    private String dbUser = "root";
    private String dbPassword = "asadmin";
    private String dbURI = "jdbc:mysql://localhost/process_query";

    private int dbPoolMinSize = 30;
    private int dbPoolMaxSize = 70;

//    public Configuration() {
//        SAXBuilder builder = new SAXBuilder();
//
//        try {
//
//            InputStream is =
//            this.getClass().getClassLoader().getResourceAsStream( CONFIG_FILENAME );
//
//            Document doc = builder.build ( is );
//            Element root = doc.getRootElement();
//
//            dbDriverName = root.getChild("dbDriverName").getTextTrim();
//            dbUser = root.getChild("dbUser").getTextTrim();
//            dbPassword = root.getChild("dbPassword").getTextTrim();
//            dbURI = root.getChild("dbURI").getTextTrim();
//            dbPoolMinSize = 
//            	Integer.parseInt( root.getChild("dbPoolMinSize").getTextTrim() );
//            dbPoolMaxSize = 
//            	Integer.parseInt( root.getChild("dbPoolMaxSize").getTextTrim() );
//
//        }   catch ( Exception ex ) {
//            System.out.println( "Could not read configuration file: "+ ex );
//        }
//
//    }
//
//
    public String getDbDriverName() {
        return dbDriverName;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public String getDbURI() {
        return dbURI;
    }

    public int getDbPoolMinSize() {
        return dbPoolMinSize;
    }

    public int getDbPoolMaxSize() {
        return dbPoolMaxSize;
    }
//
//    public String toString() {
//    	ReflectionToStringBuilder tsb = new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
//        return tsb.toString();
//    }
}
