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
import com.squid.core.domain.extensions.cast.CastOperatorDefinition;
import com.squid.core.domain.operators.ExtendedType;
import com.squid.core.domain.operators.OperatorDefinition;
import com.squid.core.sql.db.render.CastOperatorRenderer;
import com.squid.core.sql.render.OperatorPiece;
import com.squid.core.sql.render.RenderingException;
import com.squid.core.sql.render.SQLSkin;

/**
 * ex to cast date in char: cast(date_format(a1.`item_scheduledenddate`,'%m-%Y %H') as CHAR)
 * to be implemented
 * @author jtheulier
 *
 */
public class ApacheDrillCastOperatorRenderer
extends CastOperatorRenderer {
    static final Logger logger = LoggerFactory.getLogger(ApacheDrillCastOperatorRenderer.class);

	@Override
	public String prettyPrint(SQLSkin skin, OperatorPiece piece,
			OperatorDefinition opDef, String[] args) throws RenderingException {
		if (args.length==1) {
			return prettyPrintSingleArg(skin, opDef, piece, args);
		} else if (args.length==2) {
			return prettyPrintTwoArgs(skin, piece, opDef, args);
		} else {
			if (CastOperatorDefinition.TO_NUMBER.equals(opDef.getExtendedID())) {
				return prettyPrintSingleArg(skin, opDef, piece, args);
			} else {
				throw new RenderingException("Invalid operator " +  opDef.getSymbol());
			}
		}
	}

	@Override
	public String prettyPrint(SQLSkin skin, OperatorDefinition opDef, String[] args) throws RenderingException {
		return prettyPrint(skin, null, opDef, args);
	}

	@Override
	public String prettyPrintTwoArgs(SQLSkin skin, OperatorPiece piece, OperatorDefinition opDef, String[] args) throws RenderingException {
		ExtendedType[] types = getExtendedPieces(piece);
		if (CastOperatorDefinition.TO_CHAR.equals(opDef.getExtendedID())) {
			if (types[0].getDomain().isInstanceOf(IDomain.TEMPORAL)) {
				return "CAST(TO_DATE("+args[0]+","+formatMapping(args[1])+") AS CHAR)";
			} else {
				return super.prettyPrintTwoArgs(skin, piece, opDef, args);
			}
		} else if (CastOperatorDefinition.TO_DATE.equals(opDef.getExtendedID()) || CastOperatorDefinition.TO_DATE.equals(opDef.getExtendedID())) {
				return "TO_DATE("+args[0]+","+formatMapping(args[1])+")";
		} else {
			return super.prettyPrintTwoArgs(skin, piece, opDef, args);
		}		
	}
	@Override
	protected String prettyPrintSingleArg(SQLSkin skin, OperatorDefinition opDef, OperatorPiece piece, String[] args) throws RenderingException {
		String txt = "CAST(";
		txt += args[0] + " AS ";
		if (CastOperatorDefinition.TO_TIMESTAMP.equals(opDef.getExtendedID())) {
			txt += "DATETIME)";
		} else if (CastOperatorDefinition.TO_DATE.equals(opDef.getExtendedID())){
			txt += "DATE)";
		} else if (CastOperatorDefinition.TO_CHAR.equals(opDef.getExtendedID())){
			//length is optional on mysql
			txt += "CHAR(";
			txt+= ((CastOperatorDefinition)opDef).getPieceLength(getExtendedPieces(piece))+"))";
			// TODO ACTIVATE DECIMAL IN DRILL CONF BY DEFAULT 
			// DECIMAL ARE DISABLE BY DEFAULT
		/*} else if (CastOperatorDefinition.TO_NUMBER.equals(opDef.getExtendedID())){ 
			if (args.length==1) {
				//txt = "(" + args[0] + " + 0.0)";
				txt +="DECIMAL(65,30))";
			} else if (args.length==3) {
				txt +="DECIMAL("+args[1]+","+args[2]+"))";
			}*/ 
		} else if (CastOperatorDefinition.TO_INTEGER.equals(opDef.getExtendedID()) || (CastOperatorDefinition.TO_NUMBER.equals(opDef.getExtendedID()))){
			txt +="BIGINT)"; //8 bytes signed integer
		}
		//if(logger.isDebugEnabled()){logger.debug(("Casting "+args[0]+ " "+txt));}
		return txt;
	}
	
	public String formatMapping(String format) {
		//Handle 2 or 4 year digits format properly
		if (format.toLowerCase().indexOf("yyyy")!=-1) {
			format = format.replaceAll("Y*Y","%Y").replaceAll("y*y","%Y");
		} else if (format.toLowerCase().indexOf("yy")!=-1) {
			format = format.replaceAll("Y*Y","%y").replaceAll("y*y","%y");
		}
		//Handle 3 month digits format properly
		if (format.toLowerCase().indexOf("mmm")!=-1) {
			format = format.replaceAll("M*M","%b").replaceAll("m*m","%b");
		}
		return format.replaceAll("MM","%m").replaceAll("mm","%m").replaceAll("HH","%H").replaceAll("mm","%i").replaceAll("d*d","%d").replaceAll("D*D","%d").replaceAll("S*S","%f").replaceAll("s*s","%s");
	}

}
