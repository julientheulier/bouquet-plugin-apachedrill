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
package com.squid.core.jdbc.vendor.apachedrill.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.core.domain.operators.OperatorDefinition;
import com.squid.core.jdbc.vendor.apachedrill.ApacheDrillJDBCDataFormatter;
import com.squid.core.sql.db.render.DateIntervalOperatorRenderer;
import com.squid.core.sql.render.OperatorPiece;
import com.squid.core.sql.render.RenderingException;
import com.squid.core.sql.render.SQLSkin;

/**
 * The SQL render for converting a time difference into a specific period
 * @author julien theulier
 *
 */
public class ApacheDrillDateIntervalOperatorRenderer
extends DateIntervalOperatorRenderer
{
	
	static final Logger logger = LoggerFactory.getLogger(ApacheDrillDateIntervalOperatorRenderer.class);


	public ApacheDrillDateIntervalOperatorRenderer() {
		super();
	}

	@Override
	public String prettyPrint(SQLSkin skin, OperatorPiece piece, OperatorDefinition opDef, String[] args) throws RenderingException {
		super.validateArgs(skin, opDef, args);
		//Time difference computation
		System.err.println("DIFF prettyPrint in ApacheDrill");
		//Extract periods & convert them into the desired period
		int position = periods.indexOf(args[2].trim().replaceAll("'", "")	);
		if (position==-1) {
			throw new RuntimeException("The last argument must be a valid period");
		}
		String txt = "TIMESTAMPDIFF("+args[2].trim().replaceAll("'", "")+", ";
		txt += args[1] + ", " + args[0];
		txt += ")";
		//if(logger.isDebugEnabled()){logger.debug((txt));}
		return txt;
	}

}
