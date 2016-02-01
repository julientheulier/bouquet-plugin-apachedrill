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

import com.squid.core.domain.IDomain;
import com.squid.core.domain.extensions.DateTruncateOperatorDefinition;
import com.squid.core.domain.operators.ExtendedType;
import com.squid.core.domain.operators.OperatorDefinition;
import com.squid.core.sql.db.render.DateTruncateOperatorRenderer;
import com.squid.core.sql.render.OperatorPiece;
import com.squid.core.sql.render.RenderingException;
import com.squid.core.sql.render.SQLSkin;

/**
 * @author luatnn
 *
 */
public class ApacheDrillDateTruncateOperatorRenderer extends DateTruncateOperatorRenderer {
    
	static final Logger logger = LoggerFactory.getLogger(ApacheDrillDateTruncateOperatorRenderer.class);
	
    protected String prettyPrintTwoArgs(SQLSkin skin, OperatorPiece piece, OperatorDefinition opDef, String[] args) throws RenderingException {
        ExtendedType[] extendedTypes = null;
        extendedTypes = getExtendedPieces(piece);
        if(DateTruncateOperatorDefinition.WEEK.equals(args[1].replaceAll("'", ""))) { // TODO replace Week
        	//if(logger.isDebugEnabled()){logger.debug(("CAST(SUBDATE("+ args[0] +", INTERVAL weekday("+ args[0] +") DAY) as DATE)"));}
            return "CAST(SUBDATE("+ args[0] +", INTERVAL weekday("+ args[0] +") DAY) as DATE)";
        } else if(DateTruncateOperatorDefinition.MONTH.equals(args[1].replaceAll("'", ""))) { //Overly complicated but Drill wants a DATE type to be able to use EXTRACT
        	//if(logger.isDebugEnabled()){logger.debug(("CAST(CONCAT(EXTRACT(year FROM (CAST("+args[0]+" AS DATE))),'-',EXTRACT(month FROM (CAST("+args[0]+" AS DATE))),'-01') AS DATE)"));}
        	return "CAST(CONCAT(EXTRACT(year FROM (CAST("+args[0]+" AS DATE))),'-',EXTRACT(month FROM (CAST("+args[0]+" AS DATE))),'-01') AS DATE)" ;
            //return "CAST(EXTRACT("+ args[0] +" ,'%Y-%m-01') as DATE)";
        } else if(DateTruncateOperatorDefinition.YEAR.equals(args[1].replaceAll("'", ""))) {
        	//if(logger.isDebugEnabled()){logger.debug(("CAST(CONCAT(EXTRACT(year FROM (CAST("+args[0]+" AS DATE))),'-',EXTRACT(month FROM (CAST("+args[0]+" AS DATE))),'-',EXTRACT(day FROM (CAST("+args[0]+" AS DATE)))) AS DATE)"));}
        	return "CAST(CONCAT(EXTRACT(year FROM (CAST("+args[0]+" AS DATE))),'-',EXTRACT(month FROM (CAST("+args[0]+" AS DATE))),'-',EXTRACT(day FROM (CAST("+args[0]+" AS DATE)))) AS DATE)" ;
            //return "CAST(DATE_FORMAT("+ args[0] +" ,'%Y-01-01') as DATE)";
        } else if (extendedTypes[0].getDomain().isInstanceOf(IDomain.TIMESTAMP)) {
            //a timestamp has to be truncated so it becomes a date
        	//if(logger.isDebugEnabled()){logger.debug(("TO_DATE("+ args[0] +" ,'YYYY-MM-DD')"));}
            return "TO_DATE("+ args[0] +" ,'YYYY-MM-DD')";
        } else if (extendedTypes[0].getDomain().isInstanceOf(IDomain.DATE)) {
            // If it is already a date, no transformation is required
            return args[0];
        }
        //if(logger.isDebugEnabled()){logger.debug((opDef.getSymbol() + "(" + args[0] + "," + args[1] + ")"));}
        return opDef.getSymbol() + "(" + args[0] + "," + args[1] + ")";
    }
    
}