package com.order;

import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersAsyncClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersClient;
import com.amazonservices.mws.orders._2013_09_01.MarketplaceWebServiceOrdersConfig;

public class MWSOrdersConfig {
	/** Developer AWS access key. */
    private static final String accessKey = "AKIAIU4CSGP74RKYZYCQ";

    /** Developer AWS secret key. */
    private static final String secretKey = "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";

    /** The client application name. */
    private static final String appName = "TadMart";

    /** The client application version. */
    private static final String appVersion = "1.0";

    /**
     * The endpoint for region service and version.
     * ex: serviceURL = MWSEndpoint.NA_PROD.toString();
     */
    private static final String serviceURL = "https://mws.amazonservices.com/";

    /** The client, lazy initialized. Async client is also a sync client. */
    private static MarketplaceWebServiceOrdersAsyncClient client = null;

    /**
     * Get a client connection ready to use.
     *
     * @return A ready to use client connection.
     */
    public static MarketplaceWebServiceOrdersClient getClient() {
        return getAsyncClient();
    }

    /**
     * Get an async client connection ready to use.
     *
     * @return A ready to use client connection.
     */
    public static synchronized MarketplaceWebServiceOrdersAsyncClient getAsyncClient() {
        if (client==null) {
            MarketplaceWebServiceOrdersConfig config = new MarketplaceWebServiceOrdersConfig();
            config.setServiceURL(serviceURL);
            // Set other client connection configurations here.
            client = new MarketplaceWebServiceOrdersAsyncClient(accessKey, secretKey, 
                    appName, appVersion, config, null);
        }
        return client;
    }
}
