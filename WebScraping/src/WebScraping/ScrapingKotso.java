package WebScraping;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class ScrapingKotso {
	
	public static Connection conn;

	public static void main(String[] args) throws Exception {
		
		createNewDatabase("ScrapingDB.db");
		
		// SQLite connection string  
        String url = "jdbc:sqlite:/home/evi-ubuntu/eclipse-workspace/WebScraping/ScrapingDB.db";  
          
        // SQL statement for creating a new table  
        String sql = "CREATE TABLE IF NOT EXISTS products (\n"  
        		+ " title text NOT NULL,\n"
                + " id integer PRIMARY KEY,\n"  
                + " priceActual text NOT NULL,\n"
                + " priceBefore text NOT NULL,\n"
                + " priceSale text NOT NULL,\n"
                + " os text NOT NULL,\n"
                + " cpu text NOT NULL,\n"
                + " ram text NOT NULL,\n"
                + " size text NOT NULL,\n"
                + " cam text NOT NULL\n"
                + ");";  
		
		final Document doc = Jsoup.connect("https://www.kotsovolos.gr/mobile-phones-gps/mobile-phones/smartphones?beginIndex=0&pageSize=372").timeout(50000).maxBodySize(0).get();
		
		Elements list = doc.select(".listWrap.listView .product");
		
		for (Element p : list) {
			//title
			final String title = p.select(".title a").first().ownText();
			
			//id number
			final String id = p.select(".prCode").text();
			
			//all prices (price, before, sale)
			Elements price = p.select(".price .price");
			
			String priceActual, priceBefore, priceSale;
			
			if (price.hasClass("simplePrice")) {
				priceActual = p.select(".price.price.simplePrice").text();
				priceBefore = "€ 0";
				priceSale = "€ 0";
			}
			else {
				priceActual = p.select(".price .price").get(1).text();
				priceBefore = p.select(".init.priceDetail .price").text();
				priceSale = p.select(".details .price").text();
			}
			
			//description
			//os
			final String os = p.select(".prDesc b").get(0).text();
			
			//cpu
			final String cpu = p.select(".prDesc b").get(1).text();
			
			//RAM
			final String ram = p.select(".prDesc b").get(2).text();
			
			//screen size
			final String size = p.select(".prDesc b").get(3).text();
			
			//camera
			final String cam = p.select(".prDesc b").get(4).text();
			
			System.out.println(title + " | " + id + " | " + os + " | " + cpu + " | " + ram + " | " + size + " | " + cam + " | ");
			
			System.out.println(priceBefore + priceSale + priceActual);
		
			conn = DriverManager.getConnection(url);  
			Statement stmt = conn.createStatement();  
			stmt.execute(sql);  
        
			insert(title, id, priceActual, priceBefore, priceSale, os, cpu, ram, size, cam);
		}

	}
	
	
	
	public static void createNewDatabase(String fileName) {  
		   
		String url = "jdbc:sqlite:/home/evi-ubuntu/eclipse-workspace/WebScraping/" + fileName;  
   
        try {  
            Connection conn = DriverManager.getConnection(url);  
            if (conn != null) {  
                DatabaseMetaData meta = conn.getMetaData();  
                System.out.println("The driver name is " + meta.getDriverName());  
                System.out.println("A new database has been created.");  
            }  
   
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }  
    }  
	
	  //insert data in database
	  public static void insert(String title,String id,String priceActual,String priceBefore,String priceSale,String os,String cpu,String ram,String size,String cam) { 
		  String sql = "INSERT INTO products(title, id, priceActual, priceBefore, priceSale, os, cpu, ram, size, cam) VALUES(?,?,?,?,?,?,?,?,?,?)";
	  
	  try{
		  PreparedStatement pstmt = conn.prepareStatement(sql); 
		  pstmt.setString(1, title); 
		  pstmt.setString(2, id); 
		  pstmt.setString(3, priceActual);
		  pstmt.setString(4, priceBefore); 
		  pstmt.setString(5, priceSale);
		  pstmt.setString(6, os);
		  pstmt.setString(7, cpu);
		  pstmt.setString(8, ram);
		  pstmt.setString(9, size);
		  pstmt.setString(10, cam);
		  pstmt.executeUpdate(); 
		  } 
	  catch (SQLException e) {
		  System.out.println(e.getMessage()); 
		  } 
	  }
	 

}
