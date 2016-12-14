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

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squid.core.database.impl.DriverShim;
import com.squid.core.database.plugins.BaseBouquetPlugin;

public class ApacheDrillBouquetPlugin extends BaseBouquetPlugin {

	private static final Logger logger = LoggerFactory.getLogger(ApacheDrillBouquetPlugin.class);

	public static final String driverName = "org.apache.drill.jdbc.Driver";

	@Override
	public void loadDriver() {
		URL[] paths = new URL[1];
		paths[0] = this.getClass().getProtectionDomain().getCodeSource().getLocation();

		// load the driver within an isolated classLoader
		this.driverCL = new URLClassLoader(paths);
		ClassLoader rollback = Thread.currentThread().getContextClassLoader();
		Thread.currentThread().setContextClassLoader(driverCL);

		this.drivers = new ArrayList<Driver>();

		Driver driver;
		try {
			driver = (Driver) Class.forName(driverName, true, driverCL).newInstance();
			drivers.add(driver);

		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			logger.info("Failed to instanciate driver " + driverName + " for plug in apache drill");
			e.printStackTrace();
		}

		Thread.currentThread().setContextClassLoader(rollback);

	}
	@Override
	public ArrayList<DriverShim> getDrivers() {
		ArrayList<DriverShim> shims  = new ArrayList<DriverShim>();
		for (Driver d : this.drivers){
			shims.add(new ApacheDrillDriverShim(d));			
		}		
		return shims;		
	}

}
