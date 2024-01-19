package de.ritterbach.jameica.aktien.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.V_Kauf;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.util.ApplicationException;

public class V_KaufImpl extends AbstractDBObject implements V_Kauf {

	public V_KaufImpl() throws RemoteException {
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
	public Date getKaufDatum() throws RemoteException {
		return (Date) getAttribute("kauf_datum");
	}

	@Override
	public void setKaufDatum(Date kaufDatum) throws RemoteException {
		setAttribute("kauf_datum", kaufDatum);
	}

	@Override
	public BigDecimal getAnzahl() throws RemoteException {
		return (BigDecimal) getAttribute("anzahl");
	}

	@Override
	public void setAnzahl(BigDecimal anzahl) throws RemoteException {
		setAttribute("anzahl", anzahl);
	}

	@Override
	public BigDecimal getKurs() throws RemoteException {
		return (BigDecimal) getAttribute("kurs");
	}

	@Override
	public void setKurs(BigDecimal kurs) throws RemoteException {
		setAttribute("kurs", kurs);
	}

	@Override
	public BigDecimal getBetrag() throws RemoteException {
		return (BigDecimal) getAttribute("betrag");
	}

	@Override
	public void setBetrag(BigDecimal betrag) throws RemoteException {
		setAttribute("betrag", betrag);
	}

	@Override
	public BigDecimal getKosten() throws RemoteException {
		return (BigDecimal) getAttribute("kosten");
	}

	@Override
	public void setKosten(BigDecimal kosten) throws RemoteException {
		setAttribute("kosten", kosten);
	}

	@Override
	public String getBemerkung() throws RemoteException {
		return (String) getAttribute("bemerkung");
	}

	@Override
	public void setBemerkung(String bemerkung) throws RemoteException {
		setAttribute("bemerkung", bemerkung);
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
		return "kauf_datum";
	}

	@Override
	protected String getTableName() {
		return "v_kauf";
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
