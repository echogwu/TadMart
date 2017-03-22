import java.util.GregorianCalendar;

import javax.xml.datatype.XMLGregorianCalendar;

import com.amazonservices.mws.client.MwsUtl;
import com.inventory.ClientGetInventory;
import com.logger.Logger;
import com.order.ClientGetAmazonOrderIds;
import com.sqlite.SqliteDB;
import com.order.Throttling;

import org.joda.time.DateTime;

public class Runner {

	private static Logger logger = Logger.getInstance();
	
	public void getInventoryInfo(XMLGregorianCalendar queryStartDateTime){
		int counter = 0;
		ClientGetInventory clientForInventory = new ClientGetInventory();
    	counter = clientForInventory.extractInventoryItems(queryStartDateTime);    	
    	logger.write("\nAmazon inventory entries from:" + queryStartDateTime.toString() + String.valueOf(counter) + "\n\n");
	
	}
	
	public void getOrdersInfo(DateTime yesterday, DateTime today){
    	int counter = 0;
    	ClientGetAmazonOrderIds order = new ClientGetAmazonOrderIds();
    	counter = order.getAmazonOrderItems(yesterday.getYear(), yesterday.getMonthOfYear()-1, yesterday.getDayOfMonth(), today.getYear(), today.getMonthOfYear()-1, today.getDayOfMonth());
    	logger.write("\n\nAmazon Order Items from" + yesterday.toString() + "to " + today.toString() +": " + String.valueOf(counter)+"\n\n");
    }
	/*
	public static void main(String[] args){
		logger.printFileName();
		Runner runner = new Runner();
		Boolean checked = false;
		while(true){
			DateTime now = new DateTime();
			DateTime yesterday = now.minusDays(1);
			if(now.getHourOfDay() == 0 && checked){
				Throttling.sleep(30*60);
				continue;
			}
			if(now.getHourOfDay() == 0){
				runner.getInventoryInfo(MwsUtl.getDTF().newXMLGregorianCalendar(new GregorianCalendar(yesterday.getYear(),yesterday.getMonthOfYear()-1,yesterday.getDayOfMonth())));
				runner.getOrdersInfo(yesterday, now);
				SqliteDB db = new SqliteDB();
				db.selectAllFromOrderItems();
				db.selectAllFromInventory();
				System.out.println("Done");
				checked = true;
			}
			if(now.getHourOfDay() != 0){
				checked = false;
			}
			Throttling.sleep(60*60);  //sleep for one hour
		}		
	}
	*/
	
	public static void main(String[] args){
		logger.printFileName();
		Runner runner = new Runner();
		DateTime now = new DateTime();
		DateTime yesterday = now.minusDays(30);
		runner.getInventoryInfo(MwsUtl.getDTF().newXMLGregorianCalendar(new GregorianCalendar(yesterday.getYear(),yesterday.getMonthOfYear()-1,yesterday.getDayOfMonth())));
		runner.getOrdersInfo(yesterday, now);
		SqliteDB db = new SqliteDB();
		db.selectAllFromOrderItems();
		db.selectAllFromInventory();
		System.out.println("Done");
		
	}
	
}
