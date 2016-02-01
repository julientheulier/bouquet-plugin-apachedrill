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

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.core.database.model.DatabaseProduct;
import com.squid.core.database.model.Table;
import com.squid.core.sql.db.render.FromTablePiece;
import com.squid.core.sql.db.templates.DefaultJDBCSkin;
import com.squid.core.sql.db.templates.ISkinProvider;
import com.squid.core.sql.render.DelegateSamplingDecorator;
import com.squid.core.sql.render.ISamplingDecorator;
import com.squid.core.sql.render.RenderingException;
import com.squid.core.sql.render.SQLSkin;

public class ApacheDrillSkin extends DefaultJDBCSkin {

	static final Logger logger = LoggerFactory.getLogger(ApacheDrillSkin.class);
	
	protected ApacheDrillSkin(ISkinProvider provider, DatabaseProduct product) {
		super(provider, product);
		//
	}

	@Override
	protected void initFormat() {
		super.initFormat();	
		// use backtick "`" by default
		setIdentifier_quote("`");
		setLiteral_quote("\'");
	}


	
	//@Override
	public ISamplingDecorator createSamplingDecorator(DelegateSamplingDecorator sampling) {
		return new SamplingPiece(sampling);
	}

	@Override
	protected String render(SQLSkin skin,FromTablePiece piece) throws RenderingException, IOException {
		String render = "";
		final Table table = piece.getTable();
		if (table==null) {
			throw new RenderingException("table definition is null");
		}
		//
		if (piece.getSamplingDecorator()!=null) {
			// sampling version
			// select Y.x from (select * from table where random()<.y) as Y
			//
			render += " ( select * from ";
			if (table.getSchema()!=null&&!table.getSchema().isNullSchema()) {
				render += skin.quoteSchemaIdentifier(table.getSchema());
				render += ".";
			}
			render += skin.quoteTableIdentifier(table);
			// sampling
			if (piece.getSamplingDecorator().getMode()==ISamplingDecorator.FRACTION) {
				render += " where rand()<"+piece.getSamplingDecorator().getPercent()/100;
			} else {
				render += " limit "+(int)piece.getSamplingDecorator().getSize();
			}
			render += " ) as "+ piece.getAlias();
		} else {
			if (table.getSchema()!=null&&!table.getSchema().isNullSchema()) {
				render += skin.quoteSchemaIdentifier(table.getSchema());
				render += ".";
			}
			render += skin.quoteTableIdentifier(table);
			//
			// alias
			render += " "+piece.getAlias();
		}
		//
		// joining
		render += renderJoinDecorator(skin,piece);
		//if(logger.isDebugEnabled()){logger.debug((render));}
		return render;
	}
		

	/* (non-Javadoc)
	 * @see com.squid.core.sql2.template.DefaultSQLSkin#getSkinPrefix()
	 */
	@Override
	public String getSkinPrefix() {
		return this.getProvider().getSkinPrefix(getProduct());
	}

	@Override
	public String quoteComment(String text) {
		if (isComments()) {
			return "\n/*\n"+text+"\n*/\n";
		} else {
			return "";
		}
	}
	
}
