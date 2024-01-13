package de.ritterbach.jameica.aktien.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Dividende;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class DividendeImpl extends AbstractDBObject implements Dividende {

	public DividendeImpl() throws RemoteException {
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
	public String getPrimaryAttribute() throws RemoteException {
		return "zahl_datum";
	}

	@Override
	protected String getTableName() {
		return "dividende";
	}

	protected void insertCheck() throws ApplicationException {
		try {
			if (getZahlDatum() == null)
				throw new ApplicationException(Settings.i18n().tr("Please enter zahl_datum"));
			if (getProStueck() == null)
				throw new ApplicationException(Settings.i18n().tr("Please enter pro_stueck"));
			if (getGesamt() == null)
				throw new ApplicationException(Settings.i18n().tr("Please enter gesamt"));

		} catch (RemoteException e) {
			Logger.error("insert check of dividende failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store dividende"));
		}
	}

	protected void updateCheck() throws ApplicationException {
		insertCheck();
	}

	protected void deleteCheck() throws ApplicationException {
	}

}