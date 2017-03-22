package com.inventory;

import java.util.GregorianCalendar;
import java.util.LinkedList;

import javax.xml.datatype.XMLGregorianCalendar;

import com.amazonservices.mws.FulfillmentInventory._2010_10_01.FBAInventoryServiceMWS;
import com.amazonservices.mws.FulfillmentInventory._2010_10_01.FBAInventoryServiceMWSClient;
import com.amazonservices.mws.FulfillmentInventory._2010_10_01.FBAInventoryServiceMWSException;
import com.amazonservices.mws.FulfillmentInventory._2010_10_01.model.InventorySupply;
import com.amazonservices.mws.FulfillmentInventory._2010_10_01.model.ListInventorySupplyByNextTokenRequest;
import com.amazonservices.mws.FulfillmentInventory._2010_10_01.model.ListInventorySupplyByNextTokenResponse;
import com.amazonservices.mws.FulfillmentInventory._2010_10_01.model.ListInventorySupplyRequest;
import com.amazonservices.mws.FulfillmentInventory._2010_10_01.model.ListInventorySupplyResponse;
import com.amazonservices.mws.FulfillmentInventory._2010_10_01.model.ResponseHeaderMetadata;
import com.amazonservices.mws.FulfillmentInventory._2010_10_01.model.SellerSkuList;
import com.amazonservices.mws.client.MwsUtl;
import com.logger.Logger;
import com.order.Throttling;
import com.sqlite.SqliteDB;

public class ClientGetInventory {
    private static Logger logger = Logger.getInstance();
	
    private LinkedList<InventorySupply> items = new LinkedList<InventorySupply>();
	
	public void addItem(InventorySupply inventoryItem){
		this.items.add(inventoryItem);
	}
	
	public LinkedList<InventorySupply> getItems(){
		return this.items;
	}
	
	public static ListInventorySupplyResponse invokeListInventorySupply(
            FBAInventoryServiceMWS client, 
            ListInventorySupplyRequest request) {
        try {
            // Call the service.
            ListInventorySupplyResponse response = client.listInventorySupply(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            logger.write("Response:");
            logger.write("RequestId: "+rhmd.getRequestId());
            logger.write("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            logger.write(responseXml);
            return response;
        } catch (FBAInventoryServiceMWSException ex) {
            // Exception properties are important for diagnostics.
            logger.write("Service Exception:");
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if(rhmd != null) {
                logger.write("RequestId: "+rhmd.getRequestId());
                logger.write("Timestamp: "+rhmd.getTimestamp());
            }
            logger.write("Message: "+ex.getMessage());
            logger.write("StatusCode: "+ex.getStatusCode());
            logger.write("ErrorCode: "+ex.getErrorCode());
            logger.write("ErrorType: "+ex.getErrorType());
            throw ex;
        }
    }
	
	/**
     * Call the service, log response and exceptions.
     *
     * @param client
     * @param request
     *
     * @return The response.
     */
    public static ListInventorySupplyByNextTokenResponse invokeListInventorySupplyByNextToken(
            FBAInventoryServiceMWS client, 
            ListInventorySupplyByNextTokenRequest request) {
        try {
            // Call the service.
            ListInventorySupplyByNextTokenResponse response = client.listInventorySupplyByNextToken(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            logger.write("Response:");
            logger.write("RequestId: "+rhmd.getRequestId());
            logger.write("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            logger.write(responseXml);
            return response;
        } catch (FBAInventoryServiceMWSException ex) {
            // Exception properties are important for diagnostics.
            logger.write("Service Exception:");
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if(rhmd != null) {
                logger.write("RequestId: "+rhmd.getRequestId());
                logger.write("Timestamp: "+rhmd.getTimestamp());
            }
            logger.write("Message: "+ex.getMessage());
            logger.write("StatusCode: "+ex.getStatusCode());
            logger.write("ErrorCode: "+ex.getErrorCode());
            logger.write("ErrorType: "+ex.getErrorType());
            throw ex;
        }
    }
    
    public int extractInventoryItems(XMLGregorianCalendar queryStartDateTime){
    	// Get a client connection.
        // Make sure you've set the variables in MWSInventoryConfig.
        FBAInventoryServiceMWSClient client = MWSInventoryConfig.getClient();

        SqliteDB db = new SqliteDB();
        db.createTableInventory();
        
        int supplyCounter = 0;
        
        // Create a request.
        //ListInventorySupplyRequest request = new ListInventorySupplyRequest();
        String sellerId = "A2GN201B8N7Q8Q";
        String mwsAuthToken = "amzn.mws.c4acda24-ca77-1bc2-8ec4-55554e67a0b0";
        String marketplace = "US";
        String marketplaceId = "ATVPDKIKX0DER";
        SellerSkuList sellerSkus = new SellerSkuList();
        //XMLGregorianCalendar queryStartDateTime = MwsUtl.getDTF().newXMLGregorianCalendar(new GregorianCalendar(2016,0,01));
        String responseGroup = "Basic";
        ListInventorySupplyRequest request = new ListInventorySupplyRequest(sellerId, mwsAuthToken, marketplace, marketplaceId, sellerSkus, queryStartDateTime, responseGroup);

        // Make the call.
        ListInventorySupplyResponse response = ClientGetInventory.invokeListInventorySupply(client, request);
        
        for(InventorySupply item: response.getListInventorySupplyResult().getInventorySupplyList().getMember()){
        	supplyCounter += 1;
        	db.insertInventory(item, "AMAZON");
        }
        //Throttling.sleep(60);
        
        Boolean isSetNextToken = response.getListInventorySupplyResult().isSetNextToken();
        String nextToken = "";
        if(isSetNextToken){
        	nextToken = response.getListInventorySupplyResult().getNextToken();
        }
        int counter = 1;
        while(isSetNextToken){
        	counter += 1;
        	if(counter == 5){
        		counter = 0;
        		client = null;
        		client = MWSInventoryConfig.getClient();
        	}
        	ListInventorySupplyByNextTokenRequest nextTokenRequest = new ListInventorySupplyByNextTokenRequest(sellerId,
        			mwsAuthToken, marketplace,
        			nextToken);
        	ListInventorySupplyByNextTokenResponse nextTokenResponse = ClientGetInventory.invokeListInventorySupplyByNextToken(client, nextTokenRequest);
        	for(InventorySupply item: nextTokenResponse.getListInventorySupplyByNextTokenResult().getInventorySupplyList().getMember()){
        		supplyCounter += 1;
        		db.insertInventory(item, "AMAZON");
            }
        	isSetNextToken = nextTokenResponse.getListInventorySupplyByNextTokenResult().isSetNextToken();
        	if(isSetNextToken){
        		nextToken = nextTokenResponse.getListInventorySupplyByNextTokenResult().getNextToken();
        	}
        	Throttling.sleep(60);
        }
        return supplyCounter;
    }
}
