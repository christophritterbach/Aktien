package de.ritterbach.jameica.aktien.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.V_Dividende;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.util.ApplicationException;

public class V_DividendeImpl extends AbstractDBObject implements V_Dividende {

	public V_DividendeImpl() throws RemoteException {
		super();
	}

	@Override
	public Aktie getAktie() throws RemoteException {
		try {
			return (Aktie) getAttribute("aktien_id");
		} catch (ObjectNotFoundException e) {
			return null;
		}
	}

	@Override
	public void setAktie(Aktie aktie) throws RemoteException {
		setAttribute("aktien_id", aktie);
	}

	@Override
	public Date getZahlDatum() throws RemoteException {
		return (Date) getAttribute("zahl_datum");
	}

	@Override
	public void setZahlDatum(Date zahlDatum) throws RemoteException {
		setAttribute("zahl_datum", zahlDatum);
	}

	@Override
	public BigDecimal getProStueck() throws RemoteException {
		return (BigDecimal) getAttribute("pro_stueck");
	}

	@Override
	public void setProStueck(BigDecimal proStueck) throws RemoteException {
		setAttribute("pro_stueck", proStueck);
	}

	@Override
	public BigDecimal getGesamt() throws RemoteException {
		return (BigDecimal) getAttribute("gesamt");
	}

	@Override
	public void setGesamt(BigDecimal gesamt) throws RemoteException {
		setAttribute("gesamt", gesamt);
	}

	@Override
	public BigDecimal getQuellensteuer() throws RemoteException {
		return (BigDecimal) getAttribute("quellensteuer");
	}

	@Override
	public void setQuellensteuer(BigDecimal quellensteuer) throws RemoteException {
		setAttribute("quellensteuer", quellensteuer);
	}

	@Override
	public BigDecimal getDevisenkurs() throws RemoteException {
		return (BigDecimal) getAttribute("devisenkurs");
	}

	@Override
	public void setDevisenkurs(BigDecimal devisenkurs) throws RemoteException {
		setAttribute("devisenkurs", devisenkurs);
	}

	@Override
	public String getWaehrung() throws RemoteException {
		return (String) getAttribute("bemerkung");
	}

	@Override
	public void setWaehrung(String waehrung) throws RemoteException {
		setAttribute("waehrung", "waehrung");
	}

	@Override
	public String getWkn() throws RemoteException {
		return (String) getAttribute("wkn");
	}

	@Override
	public void setWkn(String wkn) throws RemoteException {
		setAttribute("wkn", wkn);
	}

	@Override
	public String getIsin() throws RemoteException {
		return (String) getAttribute("isin");
	}

	@Override
	public void setIsin(String isin) throws RemoteException {
		setAttribute("isin", isin);
	}

	@Override
	public String getBezeichnung() throws RemoteException {
		return (String) getAttribute("bezeichnung");
	}

	@Override
	public void setBezeichnung(String bezeichnung) throws RemoteException {
		setAttribute("bezeichnung", bezeichnung);
	}

	@Override
	public String getPrimaryAttribute() throws RemoteException {
		return "zahl_datum";
	}

	@Override
	protected String getTableName() {
		return "v_dividende";
	}

	protected void insertCheck() throws ApplicationException {
		throw new ApplicationException(Settings.i18n().tr("can not store into view"));
	}

	protected void updateCheck() throws ApplicationException {
		throw new ApplicationException(Settings.i18n().tr("can not store into view"));
	}

	protected void deleteCheck() throws ApplicationException {
		throw new ApplicationException(Settings.i18n().tr("can not delete from view"));
	}

}
