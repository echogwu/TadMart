package com.order;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import com.amazonservices.mws.client.MwsUtl;
import com.amazonservices.mws.orders._2013_09_01.model.ListOrdersRequest;

public class OrderRequestBuilder {
    private XMLGregorianCalendar _lastUpdatedAfter = MwsUtl.getDTF().newXMLGregorianCalendar(new GregorianCalendar(1990,0,01));
    //_createdBefore is set default to today:00:00:00
    private XMLGregorianCalendar _lastUpdatedBefore = MwsUtl.getDTF().newXMLGregorianCalendar(new GregorianCalendar(new GregorianCalendar().get(GregorianCalendar.YEAR), new GregorianCalendar().get(GregorianCalendar.MONTH), new GregorianCalendar().get(GregorianCalendar.DAY_OF_MONTH)));
    private String _marketplaceId = "ATVPDKIKX0DER";
    private String _orderStatus = "Shipped";
    
    public OrderRequestBuilder(){}
    
    public ListOrdersRequest buildListOrderRequest(){
    	ListOrdersRequest request = new ListOrdersRequest();
    	
    	String sellerId = "A2GN201B8N7Q8Q";
        request.setSellerId(sellerId);
        String mwsAuthToken = "amzn.mws.c4acda24-ca77-1bc2-8ec4-55554e67a0b0";
        request.setMWSAuthToken(mwsAuthToken);
    	
        request.setLastUpdatedAfter(_lastUpdatedAfter);
        request.setLastUpdatedBefore(_lastUpdatedBefore);
    	//request.setCreatedAfter(_createdAfter);
    	//request.setCreatedBefore(_createdBefore);
    	
    	List<String> marketplaceId = new ArrayList<String>();
    	marketplaceId.add(_marketplaceId);
    	request.setMarketplaceId(marketplaceId);
    	
    	List<String> orderStatus = new ArrayList<String>();
    	orderStatus.add(_orderStatus);
    	request.setOrderStatus(orderStatus);
    	
    	return request;   	
    }
    
    public OrderRequestBuilder lastUpdatedAfter(XMLGregorianCalendar _lastUpdatedAfter){
    	this._lastUpdatedAfter = _lastUpdatedAfter;
    	return this;
    }
    
    public OrderRequestBuilder lastUpdatedBefore(XMLGregorianCalendar _lastUpdatedBefore){
    	this._lastUpdatedBefore = _lastUpdatedBefore;
    	return this;
    }
    
    public OrderRequestBuilder marketplaceId(String _marketplaceId){
    	this._marketplaceId = _marketplaceId;
    	return this;
    }
    
    public OrderRequestBuilder orderStatus(String _orderStatus){
    	this._orderStatus = _orderStatus;
    	return this;
    } 
}
