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
import com.squid.core.sql.db.render.BaseOperatorRenderer;
import com.squid.core.sql.render.RenderingException;
import com.squid.core.sql.render.SQLSkin;

/**
 * Ticket #1190 implements some ANSI functions
 * @author loivd 
 * Sinh(x) function
 */
public class ApacheDrillSinhOperatorRenderer extends BaseOperatorRenderer {
	static final Logger logger = LoggerFactory.getLogger(ApacheDrillSinhOperatorRenderer.class);
	
	public ApacheDrillSinhOperatorRenderer() {
	}

	public String prettyPrint(SQLSkin skin, OperatorDefinition opDef,
			String[] args) throws RenderingException {
		String parameter = args.length > 1 ? "parameter" : "parameters";
		if (args.length != 1) {
			throw new RenderingException(
					"Have not supported function SINH with " + args.length
							+ parameter);
		}
		// SINH(var) = (EXP(var)-EXP(-var))/2
		String str = "(EXP(var)-EXP(-var))/2";
		//if(logger.isDebugEnabled()){logger.debug((str.replace("var", args[0])));}
		return str.replace("var", args[0]);
	}
}
