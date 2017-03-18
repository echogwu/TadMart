package com.order;

import java.util.LinkedList;

import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrders;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersException;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsByNextTokenRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsByNextTokenResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrderItemsResponse;
import com.amazonservices.mws.orders._2013_09_01.model.OrderItem;
import com.amazonservices.mws.orders._2013_09_01.model.ResponseHeaderMetadata;
import com.logger.Logger;
import com.sqlite.SqliteDB;

public class ClientGetOderItems {
	private static Logger logger = Logger.getInstance();
	
    private LinkedList<OrderItem> items = new LinkedList<OrderItem>();
	
	public void addItem(OrderItem orderItem){
		this.items.add(orderItem);
	}
	
	public LinkedList<OrderItem> getItems(){
		return this.items;
	}
	
	public void removeItems(){
		this.items.clear();
	}
	
	/**
     * Call the service, log response and exceptions.
     *
     * @param client
     * @param request
     *
     * @return The response.
     */
    public static ListOrderItemsResponse invokeListOrderItems(
            MarketplaceWebServiceOrders client, 
            ListOrderItemsRequest request) {
        try {
            // Call the service.
            ListOrderItemsResponse response = client.listOrderItems(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            logger.write("Response:");
            logger.write("RequestId: "+rhmd.getRequestId());
            logger.write("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            logger.write(responseXml);
            return response;
        } catch (MarketplaceWebServiceOrdersException ex) {
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
    
    public static ListOrderItemsByNextTokenResponse invokeListOrderItemsByNextToken(
            MarketplaceWebServiceOrders client, 
            ListOrderItemsByNextTokenRequest request) {
        try {
            // Call the service.
        	ListOrderItemsByNextTokenResponse response = client.listOrderItemsByNextToken(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            logger.write("Response:");
            logger.write("RequestId: "+rhmd.getRequestId());
            logger.write("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            logger.write(responseXml);
            return response;
        } catch (MarketplaceWebServiceOrdersException ex) {
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
    
    
    public void extractOrderItems(String amazonOrderId){
    	// Get a client connection.
        // Make sure you've set the variables in MWSOrdersConfig.
        MarketplaceWebServiceOrdersClient client = MWSOrdersConfig.getClient();

        // Create a request.
        String sellerId = "A2GN201B8N7Q8Q";
        String mwsAuthToken = "amzn.mws.c4acda24-ca77-1bc2-8ec4-55554e67a0b0";
        ListOrderItemsRequest request = new ListOrderItemsRequest(sellerId, mwsAuthToken, amazonOrderId);

        // Make the call.
        ListOrderItemsResponse response = ClientGetOderItems.invokeListOrderItems(client, request);
        for(OrderItem item: response.getListOrderItemsResult().getOrderItems()){
        	this.addItem(item);
        }
        Throttling.sleep(60);
        
        Boolean isSetNextToken = response.getListOrderItemsResult().isSetNextToken();
        String nextToken = "";
        if(isSetNextToken){
        	nextToken = response.getListOrderItemsResult().getNextToken();
        }
        int counter = 1;
        while(isSetNextToken){
        	counter += 1;
        	if(counter == 5){
        		counter = 0;
        		client = null;
        		client = MWSOrdersConfig.getClient();
        	}
        	ListOrderItemsByNextTokenRequest nextTokenRequest = new ListOrderItemsByNextTokenRequest(sellerId,
        			mwsAuthToken,
        			nextToken);
        	ListOrderItemsByNextTokenResponse nextTokenResponse = ClientGetOderItems.invokeListOrderItemsByNextToken(client, nextTokenRequest);
        	for(OrderItem item: nextTokenResponse.getListOrderItemsByNextTokenResult().getOrderItems()){
            	this.addItem(item);
            }
        	isSetNextToken = nextTokenResponse.getListOrderItemsByNextTokenResult().isSetNextToken();
        	if(isSetNextToken){
        		nextToken = nextTokenResponse.getListOrderItemsByNextTokenResult().getNextToken();
        	}
        	Throttling.sleep(60);
        }
        
    }
    
    public static void main(String[] args){
    	ClientGetOderItems clientForOrderItems = new ClientGetOderItems();
    	ClientGetAmazonOrderIds clientForOrderIds = new ClientGetAmazonOrderIds();
    	SqliteDB db = new SqliteDB();
    	db.createTableOrderItems();
    	logger.printFileName();
    	//clientForOrderIds.getAmazonIds();
    	LinkedList<String> amazonOrderIds = clientForOrderIds.getAmazonOrderIds();
		logger.write("\nAmazon Order Ids:");
		logger.write(amazonOrderIds.toString());
		logger.write(String.valueOf(amazonOrderIds.size()));
		logger.write("\n");
		Throttling.sleep(60);
		int i = 0;
		for(String amazonOrderId: amazonOrderIds){
			if(i>=30){
				Throttling.sleep(60*5);
			}
			clientForOrderItems.extractOrderItems(amazonOrderId);
		}
		LinkedList<OrderItem> orderItems = clientForOrderItems.getItems();
		for (OrderItem item: orderItems){
		    logger.write(item.getOrderItemId()+" "+item.getTitle());
		    int j = 1;
		    System.out.println("insert record " + String.valueOf(j));
			/////////////////////////////////////////////////////
		    //db.insertOrderItem(item, "AMAZON");
            /////////////////////////////////////////////////////
		}
		logger.write(String.valueOf(orderItems.size()));
		db.selectAllFromOrderItems();
		System.out.println("Done");
	}
    
}
