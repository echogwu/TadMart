package com.sqlite;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.xml.datatype.XMLGregorianCalendar;

import com.amazonservices.mws.FulfillmentInventory._2010_10_01.model.InventorySupply;
import com.amazonservices.mws.orders._2013_09_01.model.Money;
import com.amazonservices.mws.orders._2013_09_01.model.OrderItem;

public class SqliteDB {
	
	private Connection connect(){
		Connection connection = null;
        try
        {
        	// load the sqlite-JDBC driver using the current class loader
        	Class.forName("org.sqlite.JDBC");
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:./RestfulAPIs/tadmart.db");
            }catch(SQLException e){
            	// if the error message is "out of memory",
            	// it probably means no database file is found
            	System.err.println(e.getMessage());
            	} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        return connection;
	}
	
    public void createTableOrderItems(){
    	Connection connection = null;
        try
        {
            connection = this.connect();
        	Statement statement = connection.createStatement();
        	statement.setQueryTimeout(30);  // set timeout to 30 sec.
        	
        	//statement.executeUpdate("drop table if exists ORDERITEMS");
        	statement.executeUpdate("create table if not exists ORDERITEMS ("
        			+ "id integer primary key, "
        			+ "lastUpdateDate String not null, "
        			+ "asin text, "
        			+ "supplier text not null, "
          		+ "sellerSKU text,"
          		+ "orderItemId text,"
          		+ "title text,"
          		+ "quantityShipped integer,"
          		+ "itemPriceCurrency text,"
          		+ "itemPrice double,"
          		+ "shippingPrice double,"
          		+ "giftWrapPrice double,"
          		+ "itemTax double,"
          		+ "shippingTax double,"
          		+ "giftWrapTax double,"
          		+ "shippingDiscount double,"
          		+ "promotionDiscount double,"
          		+ "category text, "
          		+ "inventory_id integer,"
          		+ "FOREIGN KEY (inventory_id) REFERENCES INVENTORY(id));");
          		//+ "promotionDiscount double,"
          		//+ "primary key(orderItemId, asin));");
        }
        catch(SQLException e)
        {
          // if the error message is "out of memory", 
          // it probably means no database file is found
          System.err.println(e.getMessage());
        }
        finally
        {
          try
          {
            if(connection != null)
              connection.close();
          }
          catch(SQLException e)
          {
            // connection close failed.
            System.err.println(e);
          }
        }
        
     
    }
    
    public void createTableInventory(){
    	Connection connection = null;
        try
        {
            connection = this.connect();
        	Statement statement = connection.createStatement();
        	statement.setQueryTimeout(30);  // set timeout to 30 sec.
        	
        	//statement.executeUpdate("drop table if exists INVENTORY");
        	statement.executeUpdate("create table if not exists INVENTORY (id Integer primary key,"
        			+ "asin text, "
        			+ "supplier text, "
        			+ "sellerSKU text not null, "
        			+ "fnsku text not null, "
        			+ "condition text, "
        			+ "totalSupplyQuantity integer, "
        			+ "inStockSupplyQuantity integer, "
        			+ "costEach double"
        			+ "category text);");
        	}
        catch(SQLException e)
        {
          // if the error message is "out of memory", 
          // it probably means no database file is found
          System.err.println(e.getMessage());
        }
        finally
        {
          try
          {
            if(connection != null)
              connection.close();
          }
          catch(SQLException e)
          {
            // connection close failed.
            System.err.println(e);
          }
        }
        
     
    }
    
    public double nullConverter(Money in){
    	if(in == null)
    		return 0;
    	else 
    		return Double.valueOf(in.getAmount());
    }
    
    private Boolean doesEntryExist(String tableName, String key, String value){
        String sql = String.format("SELECT * FROM %s where %s==?", tableName, key);
        Boolean result = false;
        
        Connection connection = this.connect();
        try (PreparedStatement pstmt  = connection.prepareStatement(sql)){
        	pstmt.setString(1, value);
        	ResultSet rs  = pstmt.executeQuery();
        	if(rs.next()){
        		result = true;
        		}
        	}
        catch (SQLException e) {
        	System.out.println(e.getMessage());
        	}
        finally
        {
            try
            {
            	if(connection != null)
            		connection.close();
            	}
            catch(SQLException e){
            	// connection close failed.
            	System.err.println(e);
            	}
            }
        
        return result;
    }
    
    public void insertOrderItem(OrderItem item, String supplier, String lastUpdateDate){ 
    	
        if(this.doesEntryExist("Orderitems", "orderItemId", item.getOrderItemId())){
        	System.out.println(item.getOrderItemId()+" in orderitems exists already");
        	return;
        }
    	
    	String sql = "INSERT INTO ORDERITEMS(id, asin, supplier, sellerSKU, orderItemId, title, quantityShipped, "
    			+ "itemPriceCurrency, itemPrice, shippingPrice, giftWrapPrice, itemTax, shippingTax, giftWrapTax, "
    			+ "shippingDiscount, promotionDiscount, inventory_id, lastUpdateDate) "
    			+ "VALUES($next_id,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    	String asin = item.getASIN();
    	String sellerSKU = item.getSellerSKU();
    	String orderItemId = item.getOrderItemId();
    	String title = item.getTitle();
    	int quantityShipped = item.getQuantityShipped();
    	String itemPriceCurrency = "USD";
    	if(item.getItemPrice() != null){
    		itemPriceCurrency = item.getItemPrice().getCurrencyCode();
    	}
    	double itemPrice = this.nullConverter(item.getItemPrice());
    	double shippingPrice = this.nullConverter(item.getShippingPrice());
    	double giftWrapPrice = this.nullConverter(item.getGiftWrapPrice());
    	double itemTax = this.nullConverter(item.getItemTax());
    	double shippingTax = this.nullConverter(item.getShippingTax());
    	double giftWrapTax = this.nullConverter(item.getGiftWrapTax());
    	double shippingDiscount = this.nullConverter(item.getShippingDiscount());
    	double promotionDiscount = this.nullConverter(item.getPromotionDiscount());
    	int inventory_id = 0;
    	
    	Connection connection = this.connect();
    	try {
			ResultSet rs = connection.createStatement().executeQuery("select id from INVENTORY where sellerSKU = " + "'" + sellerSKU + "'");
		    while(rs.next()){
		    	inventory_id = rs.getInt("id");
		    }
    	} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        	pstmt.setString(2, asin);
        	pstmt.setString(3, supplier);
            pstmt.setString(4, sellerSKU);
            pstmt.setString(5, orderItemId);
            pstmt.setString(6, title);
            pstmt.setInt(7, quantityShipped);
            pstmt.setString(8, itemPriceCurrency);
            pstmt.setDouble(9,itemPrice);
            pstmt.setDouble(10, shippingPrice);
            pstmt.setDouble(11, giftWrapPrice);
            pstmt.setDouble(12, itemTax);
            pstmt.setDouble(13, shippingTax);
            pstmt.setDouble(14, giftWrapTax);
            pstmt.setDouble(15, shippingDiscount);
            pstmt.setDouble(16, promotionDiscount);
            pstmt.setInt(17, inventory_id);
            pstmt.setString(18, lastUpdateDate);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally
        {
            try
            {
              if(connection != null)
            	  connection.close();
            }
            catch(SQLException e)
            {
              // connection close failed.
              System.err.println(e);
            }
          }
    }
    
    public void insertInventory(InventorySupply item, String supplier){ 
    	if(this.doesEntryExist("Inventory", "sellerSKU", item.getSellerSKU())){
    		System.out.println(item.getSellerSKU()+" in Inventory exists already");
        	return;
        }
    	
    	String sql = "INSERT INTO INVENTORY(asin, supplier, sellerSKU, fnsku, condition, totalSupplyQuantity, inStockSupplyQuantity, costEach, id) "
    			+ "VALUES(?,?,?,?,?,?,?,0,$next_id)";
    	String sellerSKU = item.getSellerSKU();
    	String fnsku = item.getFNSKU();
    	String asin = item.getASIN();
    	String condition = item.getCondition();
    	int totalSupplyQuantity = item.getTotalSupplyQuantity();
    	int inStockSupplyQuantity = item.getInStockSupplyQuantity();
    	
    	Connection connection = this.connect();
    	
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, asin);
        	pstmt.setString(2, supplier);
        	pstmt.setString(3, sellerSKU);
            pstmt.setString(4, fnsku);
            pstmt.setString(5, condition);
            pstmt.setInt(6, totalSupplyQuantity);
            pstmt.setInt(7, inStockSupplyQuantity);
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        finally
        {
            try
            {
              if(connection != null)
            	  connection.close();
            }
            catch(SQLException e)
            {
              // connection close failed.
              System.err.println(e);
            }
          }
    }
    
    //format of date: "2017-02-03"
    public void selectOrderItemAfterDate(String date){
    	
    	String sql = "SELECT id, datetime(lastUpdateDate), orderItemId, asin, supplier, sellerSKU, title, quantityShipped, itemPrice "
                + "FROM ORDERITEMS WHERE lastUpdateDate >= date(?)";
        //String sql = "SELECT * FROM ORDERITEMS";
    	Connection connection = this.connect();
    	
        try (PreparedStatement pstmt  = connection.prepareStatement(sql)){
  
        // set the value
        pstmt.setString(1, date);
        //
        ResultSet rs  = pstmt.executeQuery();
  
        System.out.println("id\t\tlastUpdateDate\t\torderItemId\t\tasin\t\tsupplier\t\tsellerSKU\t\ttitle\t\tquantityShipped\t\titemPrice");
        // loop through the result set
        while (rs.next()) {
            System.out.println(rs.getInt("id") + "\t\t" +
            		rs.getString("datetime(lastUpdateDate)") + "\t\t" +
            		rs.getString("orderItemId") + "\t\t" +
            		     rs.getString("asin") + "\t\t" +
                         rs.getString("supplier") + "\t\t" + 
                         rs.getString("sellerSKU") + "\t\t" +
                         rs.getString("title") + "\t\t" +
            		     rs.getInt("quantityShipped") + "\t\t" +
                         rs.getString("itemPrice"));
            }
        }
        catch (SQLException e) {
        	System.out.println(e.getMessage());
        	}
        finally
        {
            try
            {
            	if(connection != null)
            		connection.close();
            	}
            catch(SQLException e){
            	// connection close failed.
            	System.err.println(e);
            	}
            }
        }
    
    public void selectInventoryItemBySellerSKU(String sellerSKU){
    	String sql = "SELECT id, asin, supplier, sellerSKU, totalSupplyQuantity"
                + "FROM INVENTORY WHERE sellerSKU = ?";
        
    	Connection connection = this.connect();
    	
        try (PreparedStatement pstmt  = connection.prepareStatement(sql)){
  
        // set the value
        pstmt.setString(1, sellerSKU);
        //
        ResultSet rs  = pstmt.executeQuery();
  
        // loop through the result set
        System.out.println("id\t\tasin\t\tsupplier\t\tsellerSKU\t\ttotalSupplyQuantity");
        while (rs.next()) {
            System.out.println(rs.getInt("id") +"\t\t" +
                    rs.getString("asin") + "\t\t" +
                    rs.getString("supplier") + "\t\t" +
                    rs.getString("sellerSKU") + "\t\t" +
                    rs.getInt("totalSupplyQuantity"));
            }
        }
        catch (SQLException e) {
        	System.out.println(e.getMessage());
        	}
        finally
        {
            try
            {
            	if(connection != null)
            		connection.close();
            	}
            catch(SQLException e){
            	// connection close failed.
            	System.err.println(e);
            	}
            }
        }
    
    public void selectAllFromOrderItems(){
        String sql = "SELECT * FROM ORDERITEMS";
        Connection connection = this.connect();
        try (
             Statement stmt  = connection.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            System.out.println("id\t\torderItemId\t\tasin\t\ttitle\t\tquantityShipped\t\titemPrice");
            // loop through the result set
            while (rs.next()) {
            	System.out.println(
            			//rs.getInt("rowid") + "\t\t" +
            			rs.getInt("id") +"\t\t" +
            			rs.getString("orderItemId") + "\t\t" +
            			rs.getString("asin") + "\t\t" +
                        rs.getString("title") + "\t\t" +
            			rs.getInt("quantityShipped") + "\t\t" +
                        rs.getDouble("itemPrice"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally
        {
            try
            {
            	if(connection != null)
            		connection.close();
            	}
            catch(SQLException e){
            	// connection close failed.
            	System.err.println(e);
            	}
            }
    }
    
    public void selectAllFromInventory(){
        String sql = "SELECT * FROM INVENTORY";
        Connection connection = this.connect();
        try (
             Statement stmt  = connection.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)){
            
        	System.out.println("id\t\tasin\t\tsupplier\t\tsellerSKU\t\ttotalSupplyQuantity");
            // loop through the result set
            while (rs.next()) {
            	System.out.println(
            			rs.getInt("id") +"\t\t" +
            			rs.getString("asin") + "\t\t" +
            			rs.getString("supplier") + "\t\t" +
                        rs.getString("sellerSKU") + "\t\t" +
            			rs.getInt("totalSupplyQuantity"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally
        {
            try
            {
            	if(connection != null)
            		connection.close();
            	}
            catch(SQLException e){
            	// connection close failed.
            	System.err.println(e);
            	}
            }
    }

}
