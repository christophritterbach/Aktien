package de.ritterbach.jameica.aktien;

import java.math.BigDecimal;

/**********************************************************************
 * $Source: /cvsroot/jameica/jameica_exampleplugin/src/de/willuhn/jameica/example/Settings.java,v $
 * $Revision: 1.6 $
 * $Date: 2010-11-09 17:20:15 $
 * $Author: willuhn $
 * $Locker:  $
 * $State: Exp $
 *
 * Copyright (c) by willuhn.webdesign
 * All rights reserved
 *
 **********************************************************************/

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import de.ritterbach.jameica.aktien.rmi.AktienDBService;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * This class holds some settings for our plugin.
 */
public class Settings extends de.willuhn.util.Settings {

	private static AktienDBService db = null;
	private static I18N i18n;

	public Settings(Class clazz) {
		this(clazz, true);
	}

	public Settings(Class clazz, boolean overridable) {
		super("cfg", overridable ? Application.getConfig().getConfigDir() : null, clazz);
	}

	public Date getDate(String name, Date defaultValue) {
		return new Date(super.getLong(name, defaultValue.getTime()));
	}
	
	public void setAttribute(String name, Date value) {
		super.setAttribute(name, value.getTime());
	}
	
	/**
	 * Our DateFormatter.
	 */
	public final static DateFormat DATEFORMAT = DateFormat.getDateInstance(DateFormat.DEFAULT,
			Application.getConfig().getLocale());

	/**
	 * Our decimal formatter.
	 */
	public final static DecimalFormat DECIMALFORMAT = (DecimalFormat) DecimalFormat
			.getInstance(Application.getConfig().getLocale());

	public final static DecimalFormat ANZAHLFORMAT = new DecimalFormat("#0.000",
			new DecimalFormatSymbols(Application.getConfig().getLocale()));

	public final static DecimalFormat KUSRFORMAT = new DecimalFormat("#0.0000",
			new DecimalFormatSymbols(Application.getConfig().getLocale()));

	/**
	 * Our currency name.
	 */
	public final static String CURRENCY = "EUR";

	static {
		DECIMALFORMAT.setMinimumFractionDigits(2);
		DECIMALFORMAT.setMaximumFractionDigits(2);
	}

	/**
	 * Small helper function to get the database service.
	 * 
	 * @return db service.
	 * @throws RemoteException
	 */
	public static AktienDBService getDBService() throws RemoteException {
		if (db != null)
			return db;
		try {
			db = (AktienDBService) Application.getServiceFactory().lookup(AktienPlugin.class, "aktien");
			return db;
		} catch (ConnectException ce) {
			// Die Exception fliegt nur bei RMI-Kommunikation mit fehlendem RMI-Server
			I18N i18n = Application.getPluginLoader().getPlugin(AktienPlugin.class).getResources().getI18N();
			String host = Application.getServiceFactory().getLookupHost(AktienPlugin.class, "database");
			int port = Application.getServiceFactory().getLookupPort(AktienPlugin.class, "database");
			String msg = i18n.tr("Aktien-Server \"{0}\" nicht erreichbar", (host + ":" + port));
			try {
				Application.getCallback().notifyUser(msg);
				throw new RemoteException(msg);
			} catch (Exception e) {
				Logger.error("error while notifying user", e);
				throw new RemoteException(msg);
			}
		} catch (ApplicationException ae) {
			// Da interessiert uns der Stacktrace nicht
			throw new RemoteException(ae.getMessage());
		} catch (RemoteException re) {
			throw re;
		} catch (Exception e) {
			throw new RemoteException("unable to open/create database", e);
		}
	}

	/**
	 * Small helper function to get the translator.
	 * 
	 * @return translator.
	 */
	public static I18N i18n() {
		if (i18n != null)
			return i18n;
		i18n = Application.getPluginLoader().getPlugin(AktienPlugin.class).getResources().getI18N();
		return i18n;
	}

	public static Boolean getBoolFromDatabase(Object o, Boolean defaultValue) {
		short istWahr = 1;
		if (o == null) {
			return defaultValue;
		}
		short wert = 0;
		if (o instanceof Integer) {
			wert = ((Integer) o).shortValue();
		} else {
			wert = (short) o;
		}
		return (istWahr == wert);
	}

	public static BigDecimal parseTableEntry(String value, String columnName) throws ApplicationException {
		DecimalFormat decimalFormat = new DecimalFormat("#,##0.0#", new DecimalFormatSymbols(Locale.getDefault()));
		decimalFormat.setParseBigDecimal(true);
		BigDecimal wert = BigDecimal.ZERO;
		try {
			wert = (BigDecimal) decimalFormat.parse(value.replaceAll("[^0-9.,]", ""));
		} catch (ParseException e) {
			Logger.error("can not parse " + columnName + "(" + value + ")", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store {0}", columnName));
		}
		return wert;
	}
}
