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
import com.squid.core.sql.db.render.ExtractAsFunctionOperatorRenderer;
import com.squid.core.sql.render.OperatorPiece;
import com.squid.core.sql.render.RenderingException;
import com.squid.core.sql.render.SQLSkin;

/**
 * An Operator Renderer handling EXTRACT syntax
 * @author serge fantino
 *
 */
public class ApacheDrillDayOfWeekOperatorRenderer
extends ExtractAsFunctionOperatorRenderer
{
	static final Logger logger = LoggerFactory.getLogger(ApacheDrillDayOfWeekOperatorRenderer.class);

	public ApacheDrillDayOfWeekOperatorRenderer(String mode) {
		super(mode);
	}

	@Override
	public String prettyPrint(SQLSkin skin, OperatorPiece piece, OperatorDefinition opDef, String[] args) throws RenderingException {
		if (args.length!=1) {
			throw new RenderingException("invalid EXTRACT operator");
		}
		String result = super.prettyPrint(skin,piece, opDef, args);
		//if(logger.isDebugEnabled()){logger.debug(("CASE WHEN "+result+"=1 THEN 7 ELSE "+result+"-1 END"));}
		return "CASE WHEN "+result+"=1 THEN 7 ELSE "+result+"-1 END";
	}

}
