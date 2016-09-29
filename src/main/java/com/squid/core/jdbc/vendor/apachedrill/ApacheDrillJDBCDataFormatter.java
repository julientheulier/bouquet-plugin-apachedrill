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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.core.jdbc.formatter.DataFormatter;
import com.squid.core.jdbc.formatter.DefaultJDBCDataFormatter;

public class ApacheDrillJDBCDataFormatter extends DefaultJDBCDataFormatter {

	static final Logger logger = LoggerFactory.getLogger(ApacheDrillJDBCDataFormatter.class);

	public ApacheDrillJDBCDataFormatter(DataFormatter formatter, Connection connection) {
		super(formatter, connection);
	}

	@Override
	public String formatJDBCObject(final Object jdbcObject, final int colType) throws SQLException {
		return super.formatJDBCObject(jdbcObject, colType);
		/*
		String value = "";

		switch (colType) {
		
		default:
			logger.info("Type of colType for the jdbcObject= " + colType);
			
			break;
		}
		
		if (value == null) {
			value = "";
		}
		return value;
*/
	}
	
	
	@Override
	public Object unboxJDBCObject(final Object jdbcObject, final int colType) throws SQLException {
		String value = "";

		switch (colType) {
		
		// VARCHAR are actually hadoop text
		case Types.VARCHAR:
			return jdbcObject.toString(); 

			
		default:
			return super.unboxJDBCObject(jdbcObject, colType);
		}

	}

	@Override
	public boolean displaysWarnings() {
		return false;
	}

	@Override
	public int getFetchSize() {
		return Integer.MIN_VALUE;
	}

}