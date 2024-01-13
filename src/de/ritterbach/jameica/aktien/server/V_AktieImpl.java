package de.ritterbach.jameica.aktien.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.ritterbach.jameica.aktien.rmi.V_Aktie;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class V_AktieImpl extends AbstractDBObject implements V_Aktie {

	public V_AktieImpl() throws RemoteException {
		super();
	}

	@Override
	public Aktie getAktie() throws RemoteException {
		try {
			DBIterator<Aktie> aktien = Settings.getDBService().createList(Aktie.class);
			aktien.addFilter("id=?", this.getID());
			if (aktien.hasNext()) {
				return aktien.next();
			} else {
				return null;
			}
		} catch (ObjectNotFoundException e) {
			return null;
		}
	}

	@Override
	public void setAktie(Aktie aktie) throws RemoteException {
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
	public BigDecimal getAnzahl() throws RemoteException {
		return (BigDecimal) getAttribute("anzahl");
	}

	@Override
	public void setAnzahl(BigDecimal anzahl) throws RemoteException {
		setAttribute("anzahl", anzahl);
	}

	@Override
	public String getPrimaryAttribute() throws RemoteException {
		return "isin";
	}

	@Override
	protected String getTableName() {
		return "v_aktien";
	}

	protected void insertCheck() throws ApplicationException {
		throw new ApplicationException(Settings.i18n().tr("Can not store into view"));
	}

	protected void updateCheck() throws ApplicationException {
		throw new ApplicationException(Settings.i18n().tr("Can not store into view"));
	}

	protected void deleteCheck() throws ApplicationException {
		throw new ApplicationException(Settings.i18n().tr("Can not delete from view"));
	}

	@Override
	public DBIterator<Kauf> getKaeufe() throws RemoteException {
		try {
			DBService service = this.getService();
			DBIterator<Kauf> kaufListe = service.createList(Kauf.class);
			kaufListe.addFilter("aktien_id = " + this.getID());
			return kaufListe;
		} catch (Exception e) {
			throw new RemoteException("unable to load kauf list", e);
		}
	}
	
}
