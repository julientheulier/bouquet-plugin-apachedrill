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
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.core.database.metadata.IMetadataEngine;
import com.squid.core.database.model.DatabaseProduct;
import com.squid.core.jdbc.formatter.DataFormatter;
import com.squid.core.jdbc.formatter.IJDBCDataFormatter;
import com.squid.core.jdbc.vendor.DefaultVendorSupport;
import com.squid.core.jdbc.vendor.JdbcUrlParameter;
import com.squid.core.jdbc.vendor.JdbcUrlTemplate;

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
	
	/* (non-Javadoc)
	 * @see com.squid.core.jdbc.vendor.DefaultVendorSupport#getJdbcUrlTemplate()
	 */
	@Override
	public JdbcUrlTemplate getJdbcUrlTemplate() {
		JdbcUrlTemplate template = new JdbcUrlTemplate("Drill","jdbc:drill:[drillbit=<drillbit>][zk=<zk name>][:<port][<path>]");
		template.add(new JdbcUrlParameter("drillbit", true));
		template.add(new JdbcUrlParameter("zk name", true));
		template.add(new JdbcUrlParameter("port", true, "2181"));
		template.add(new JdbcUrlParameter("path", true));
		return template;
	}
	
	/* (non-Javadoc)
	 * @see com.squid.core.jdbc.vendor.DefaultVendorSupport#buildJdbcUrl(java.util.Map)
	 */
	@Override
	public String buildJdbcUrl(Map<String, String> arguments) throws IllegalArgumentException {
		String url = "jdbc:drill:";
		String hostname = arguments.get("hostname");
		if (hostname!=null && !hostname.equals("")) {
			if (hostname.startsWith("zk=") || hostname.startsWith("drillbit=")) {
				url += hostname;
			} else {
				throw new IllegalArgumentException("cannot build JDBC url, missing mandatory arguments: <drillbit> either <zk name> must be defined");
			}
		} else {
			String drillbit = arguments.get("drillbit");
			String zcname = arguments.get("zk name");
			if (drillbit!=null && zcname!=null && !drillbit.equals("") && !zcname.equals("")) {
				throw new IllegalArgumentException("cannot build JDBC url, incompatible arguments: <drillbit> either <zk name> must be defined");
			} else if (drillbit!=null && !drillbit.equals("")) {
				url += "drillbit="+drillbit;
			} if (zcname!=null && !zcname.equals("")) {
				url += "zk="+zcname;
			} else {
				throw new IllegalArgumentException("cannot build JDBC url, missing mandatory arguments: <drillbit> either <zk name> must be defined");
			}
		}
		// port
		String port = arguments.get("port");
		if (port!=null && !port.equals("")) {
			// check it's an integer
			try {
				int p = Integer.valueOf(port);
				url += ":"+Math.abs(p);// just in case
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("cannot build JDBC url, <port> value must be a valid port number");
			}
		}
		// path
		String path = arguments.get("path");
		if (path==null || path.equals("")) {
			path = arguments.get("database");
		}
		if (path!=null && !path.equals("")) {
			if (!path.startsWith("/")) url += "/";
			url += path;
		}
		// validate ?
		return url;
	}

}
