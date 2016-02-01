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

import com.squid.core.sql.render.DelegateSamplingDecorator;
import com.squid.core.sql.render.ISamplingDecorator;
import com.squid.core.sql.render.RenderingException;
import com.squid.core.sql.render.SQLSkin;

public class SamplingPiece 
implements ISamplingDecorator
{
	static final Logger logger = LoggerFactory.getLogger(SamplingPiece.class);
	
	private boolean block = false;
	private double size;
	private double percent;
	private int mode = FRACTION;
	private boolean setSeed = false;
	private int seed;
	
	public SamplingPiece(DelegateSamplingDecorator sampling) {
		super();
		this.block = false;
		this.setSeed = false;
		setSize(sampling.getSize(), sampling.getMode());
	}
	
	public double getSize() {
		return size;
	}

	public int getMode() {
		return mode;
	}
	
	public void setSize(double size, int mode) {
		this.size = size;
		this.mode = mode;
	}

	public boolean isBlock() {
		return block;
	}

	public void setBlock(boolean block) {
		this.block = block;
	}

	public int getSeed() {
		return seed;
	}
	
	public boolean getSetSeed() {
		return setSeed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
		this.setSeed = true;
	}
	
	@Override
	public String render(SQLSkin skin) throws RenderingException {
		//if(logger.isDebugEnabled()){logger.debug(("rand()<"+(this.percent/100)));}
		return "rand()<"+(this.percent/100);
	}

	@Override
	public double getPercent() {
		return this.percent;
	}

	@Override
	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public void setPercent(double count, int mode) {
		this.percent = count;
		this.mode = mode;
	}

}
