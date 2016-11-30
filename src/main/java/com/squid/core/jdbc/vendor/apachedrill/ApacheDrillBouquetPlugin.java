package com.squid.core.jdbc.vendor.apachedrill;

import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Driver;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

}
