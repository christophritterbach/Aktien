package de.ritterbach.jameica.aktien.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class KaufImpl extends AbstractDBObject implements Kauf {

	public KaufImpl() throws RemoteException {
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
		if (kosten == null)
			kosten = BigDecimal.ZERO;
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
	public String getPrimaryAttribute() throws RemoteException {
		return "kauf_datum";
	}

	@Override
	protected String getTableName() {
		return "kauf";
	}

	@Override
	protected Class getForeignObject(String field) throws RemoteException {
		// the system is able to resolve foreign keys and loads
		// the according objects automatically. You only have to
		// define which class handles which foreign key.
		if ("aktien_id".equals(field))
			return Aktie.class;
		return null;
	}

	protected void insertCheck() throws ApplicationException {
		try {
			if (getKaufDatum() == null)
				throw new ApplicationException(Settings.i18n().tr("please enter kauf_datum"));
			if (getAnzahl() == null)
				throw new ApplicationException(Settings.i18n().tr("please enter anzahl"));
			if (getBetrag() == null)
				throw new ApplicationException(Settings.i18n().tr("please enter betrag"));

		} catch (RemoteException e) {
			Logger.error("insert check of kauf failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store kauf"));
		}
	}

	protected void updateCheck() throws ApplicationException {
		insertCheck();
	}

	protected void deleteCheck() throws ApplicationException {
	}

}
