/*******************************************************************************
 * Copyright © Squid Solutions, 2016
 *
 * This file is part of Open Bouquet software.
 *  
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation (version 3 of the License).
 *
 * There is a special FOSS exception to the terms and conditions of the 
 * licenses as they are applied to this program. See LICENSE.txt in
 * the directory of this program distribution.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * Squid Solutions also offers commercial licenses with additional warranties,
 * professional functionalities or services. If you purchase a commercial
 * license, then it supersedes and replaces any other agreement between
 * you and Squid Solutions (above licenses and LICENSE.txt included).
 * See http://www.squidsolutions.com/EnterpriseBouquet/
 *******************************************************************************/
package com.squid.core.jdbc.vendor.apachedrill;

import java.io.IOException;
import java.sql.Connection;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.core.database.metadata.IMetadataEngine;
import com.squid.core.database.model.DatabaseProduct;
import com.squid.core.jdbc.formatter.DataFormatter;
import com.squid.core.jdbc.formatter.IJDBCDataFormatter;
import com.squid.core.jdbc.vendor.DefaultVendorSupport;

public class ApacheDrillVendorSupport extends DefaultVendorSupport {
	
	public static final String VENDOR_ID =  IMetadataEngine.APACHEDRILL_NAME;
    static final Logger logger = LoggerFactory.getLogger(ApacheDrillVendorSupport.class);
	private Properties properties;

	@Override
	public String getVendorId() {
		return VENDOR_ID;
	}

	@Override
	public String getVendorVersion() {
		try {
			this.properties = new Properties();
			properties.load(this.getClass().getClassLoader().getResourceAsStream("application.properties"));
			return properties.getProperty("application.version");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "-1";
	}

	@Override
	public boolean isSupported(DatabaseProduct product) {
		return VENDOR_ID.equals(product.getProductName());
	}

	@Override
	public IJDBCDataFormatter createFormatter(DataFormatter formatter,
			Connection connection) {
		return new ApacheDrillJDBCDataFormatter(formatter, connection);
	}

}
