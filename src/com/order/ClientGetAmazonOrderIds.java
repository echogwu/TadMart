package com.order;

import java.sql.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;

import javax.xml.datatype.XMLGregorianCalendar;

import com.amazonservices.mws.client.MwsUtl;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrders;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersException;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersByNextTokenRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersByNextTokenResponse;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersRequest;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersResponse;
import com.amazonservices.mws.orders._2013_09_01.model.Order;
import com.amazonservices.mws.orders._2013_09_01.model.OrderItem;
import com.amazonservices.mws.orders._2013_09_01.model.ResponseHeaderMetadata;
import com.logger.Logger;
import com.sqlite.SqliteDB;

public class ClientGetAmazonOrderIds {
	
	private static Logger logger = Logger.getInstance();
	
	private LinkedList<String> AmazonOrderIds = new LinkedList<String>();
	
	public void addAmazonOrderIds(String orderId){
		this.AmazonOrderIds.add(orderId);
	}
	
	public LinkedList<String> getAmazonOrderIds(){
		return this.AmazonOrderIds;
	}
	
	/**
     * Call the service, log response and exceptions.
     *
     * @param client
     * @param request
     *
     * @return The response.
     */
	public static ListOrdersResponse invokeListOrders(
            MarketplaceWebServiceOrders client, 
            ListOrdersRequest request) {
        try {
            // Call the service.
            ListOrdersResponse response = client.listOrders(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            /*
            System.out.println("Response:");
            System.out.println("RequestId: "+rhmd.getRequestId());
            System.out.println("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            System.out.println(responseXml);
            */
            logger.write("Response:");
            logger.write("RequestId: "+rhmd.getRequestId());
            logger.write("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            logger.write(responseXml);
            return response;
        } catch (MarketplaceWebServiceOrdersException ex) {
            // Exception properties are important for diagnostics.
            //System.out.println("Service Exception:");
        	logger.write("Service Exception:");
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if(rhmd != null) {
            	/*
                System.out.println("RequestId: "+rhmd.getRequestId());
                System.out.println("Timestamp: "+rhmd.getTimestamp());
                */
            	logger.write("RequestId: "+rhmd.getRequestId());
            	logger.write("Timestamp: "+rhmd.getTimestamp());
            }
            /*
            System.out.println("Message: "+ex.getMessage());
            System.out.println("StatusCode: "+ex.getStatusCode());
            System.out.println("ErrorCode: "+ex.getErrorCode());
            System.out.println("ErrorType: "+ex.getErrorType());
            */
            logger.write("Message: "+ex.getMessage());
            logger.write("StatusCode: "+ex.getStatusCode());
            logger.write("ErrorCode: "+ex.getErrorCode());
            logger.write("ErrorType: "+ex.getErrorType());
            throw ex;
        }
	}
	
	public static ListOrdersByNextTokenResponse invokeListOrdersByNextToken(
            MarketplaceWebServiceOrders client, 
            ListOrdersByNextTokenRequest request) {
        try {
            // Call the service.
            ListOrdersByNextTokenResponse response = client.listOrdersByNextToken(request);
            ResponseHeaderMetadata rhmd = response.getResponseHeaderMetadata();
            // We recommend logging every the request id and timestamp of every call.
            /*
            System.out.println("Response:");
            System.out.println("RequestId: "+rhmd.getRequestId());
            System.out.println("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            System.out.println(responseXml);
            */
            logger.write("Response:");
            logger.write("RequestId: "+rhmd.getRequestId());
            logger.write("Timestamp: "+rhmd.getTimestamp());
            String responseXml = response.toXML();
            logger.write(responseXml);
            return response;
        } catch (MarketplaceWebServiceOrdersException ex) {
            // Exception properties are important for diagnostics.
            //System.out.println("Service Exception:");
        	logger.write("Service Exception:");
            ResponseHeaderMetadata rhmd = ex.getResponseHeaderMetadata();
            if(rhmd != null) {
            	/*
                System.out.println("RequestId: "+rhmd.getRequestId());
                System.out.println("Timestamp: "+rhmd.getTimestamp());
                */
            	logger.write("RequestId: "+rhmd.getRequestId());
            	logger.write("Timestamp: "+rhmd.getTimestamp());
            }
            /*
            System.out.println("Message: "+ex.getMessage());
            System.out.println("StatusCode: "+ex.getStatusCode());
            System.out.println("ErrorCode: "+ex.getErrorCode());
            System.out.println("ErrorType: "+ex.getErrorType());
            */
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
     * @param createdAfter default to be 01/01/1990, month=0 means January
     * @param createdBefore month=0 means January
     * @param marketplaceId default to be amazon.com
     * @param orderStatus default to be "Shipped"
     * 
     *
     * @return The response.
     */
	/*
	public void getAmazonIdsByFilters(
			XMLGregorianCalendar createdAfter, 
			XMLGregorianCalendar createdBefore,
			String marketplaceId,
			String orderStatus
			){
		
		// Get a client connection.
        // Make sure you've set the variables in MWSOrderConfig.java.
        MarketplaceWebServiceOrdersClient client = MarketplaceWebServiceOrdersSampleConfig.getClient();
        
        // Create a request.
        ListOrdersRequest request = new OrderRequestBuilder().
        		createdAfter(createdAfter).
        		createdBefore(createdBefore).
        		marketplaceId(marketplaceId).
        		orderStatus(orderStatus).
        		buildListOrderRequest();        
        
        // Make the call.
        CallOrder.invokeListOrders(client, request);
	}
	*/
	
	public int getAmazonOrderItems(int afterYear, int afterMonth, int afterDay, int beforeYear, int beforeMonth, int beforeDay){
	//public int getAmazonIds(){
		
		// Get a client connection.
        // Make sure you've set the variables in MWSOrderConfig.java.
        MarketplaceWebServiceOrdersClient client = MWSOrdersConfig.getClient();
        ClientGetOderItems clientForItems = new ClientGetOderItems();
        SqliteDB db = new SqliteDB();
        db.createTableOrderItems();

        ListOrdersRequest request = new OrderRequestBuilder().
        		lastUpdatedAfter(MwsUtl.getDTF().newXMLGregorianCalendar(new GregorianCalendar(afterYear,afterMonth,afterDay))).
        		lastUpdatedBefore(MwsUtl.getDTF().newXMLGregorianCalendar(new GregorianCalendar(beforeYear,beforeMonth,beforeDay))).
        		buildListOrderRequest();
        logger.write("period between: \n"+String.valueOf(afterYear)+"/"+String.valueOf(afterMonth)+"/"+String.valueOf(afterDay)+"--"+
        		String.valueOf(beforeYear)+"/"+String.valueOf(beforeMonth)+"/"+String.valueOf(beforeDay)+"\n");
        
        
        // Make the call.
        //return Order.invokeListOrders(client, request);
        int amazonOrderItemsCounter = 0;
        ListOrdersResponse response = ClientGetAmazonOrderIds.invokeListOrders(client, request);
        for (Order order: response.getListOrdersResult().getOrders()){
        	clientForItems.extractOrderItems(order.getAmazonOrderId());
        	XMLGregorianCalendar lastUpdateDate = order.getLastUpdateDate();
        	//System.out.println("==========="+lastUpdateDate+"===========");
        	for(OrderItem item: clientForItems.getItems()){
        		amazonOrderItemsCounter += 1;
        		db.insertOrderItem(item, "AMAZON", lastUpdateDate.toString());
        	}
        	clientForItems.removeItems();
        	this.addAmazonOrderIds(order.getAmazonOrderId());
        }
        //The ListOrders and ListOrdersByNextToken operations together share a maximum request quota of six and a restore rate of one request every minute.
        Throttling.sleep(60);
        Boolean isSetNextToken = response.getListOrdersResult().isSetNextToken();
        int counter = 1;
        int requestNumber = 1;
        String nextToken = "";
        if(isSetNextToken){
        	nextToken = response.getListOrdersResult().getNextToken();
        }
        while(isSetNextToken){
        	counter +=1;
        	requestNumber += 1;
            System.out.format("requestNumber = %d", requestNumber);
        	if(counter == 10){
        		System.out.println("counter = 10");
        		counter = 0;
        		client = null;
        		client = MWSOrdersConfig.getClient();
        	}  		
        	
        	ListOrdersByNextTokenRequest nextTokenRequest = new ListOrdersByNextTokenRequest("A2GN201B8N7Q8Q", "amzn.mws.c4acda24-ca77-1bc2-8ec4-55554e67a0b0", nextToken);
        	ListOrdersByNextTokenResponse nextTokenResponse = ClientGetAmazonOrderIds.invokeListOrdersByNextToken(client, nextTokenRequest);
        	isSetNextToken = nextTokenResponse.getListOrdersByNextTokenResult().isSetNextToken();
        	if(!isSetNextToken){
        		break;
        	}
        	nextToken = nextTokenResponse.getListOrdersByNextTokenResult().getNextToken();
        	for(Order order: nextTokenResponse.getListOrdersByNextTokenResult().getOrders()){
        		
            	clientForItems.extractOrderItems(order.getAmazonOrderId());
            	XMLGregorianCalendar lastUpdateDate = order.getLastUpdateDate();
            	//System.out.println("==========="+lastUpdateDate+"===========");
            	for(OrderItem item: clientForItems.getItems()){
            		amazonOrderItemsCounter += 1;
            		db.insertOrderItem(item, "AMAZON", lastUpdateDate.toString());
            	}
            	clientForItems.removeItems();
        		this.addAmazonOrderIds(order.getAmazonOrderId());
        	}
            Throttling.sleep(60);
        }
        return amazonOrderItemsCounter;
        
	}
}
