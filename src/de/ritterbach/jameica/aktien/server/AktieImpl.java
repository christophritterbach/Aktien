package de.ritterbach.jameica.aktien.server;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Dividende;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AktieImpl extends AbstractDBObject implements Aktie {

	public AktieImpl() throws RemoteException {
		super();
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
	public Aktie getAktieVon() throws RemoteException {
		try {
			return (Aktie) getAttribute("von_aktien_id");
		} catch (ObjectNotFoundException e) {
			return null;
		}
	}

	@Override
	public void setAktieVon(Aktie aktie) throws RemoteException {
		setAttribute("von_aktien_id", aktie);
	}

	@Override
	public DBIterator<Aktie> getAktienVon() throws RemoteException {
		try {
			DBService service = this.getService();
			DBIterator<Aktie> aktienVonListe = service.createList(Aktie.class);
			aktienVonListe.addFilter("von_aktien_id = ?", this.getID());
			return aktienVonListe;
		} catch (Exception e) {
			throw new RemoteException("unable to load aktien_von list", e);
		}
	}

	@Override
	public DBIterator<Kauf> getKaeufe() throws RemoteException {
		try {
			DBService service = this.getService();
			DBIterator<Kauf> kaufListe = service.createList(Kauf.class);
			kaufListe.addFilter("aktien_id = ?", this.getID());
			return kaufListe;
		} catch (Exception e) {
			throw new RemoteException("unable to load kauf list", e);
		}
	}

	@Override
	public DBIterator<Dividende> getDidivenden() throws RemoteException {
		try {
			DBService service = this.getService();
			DBIterator<Dividende> dividendenListe = service.createList(Dividende.class);
			dividendenListe.addFilter("aktien_id = ?", this.getID());
			return dividendenListe;
		} catch (Exception e) {
			throw new RemoteException("unable to load dividende list", e);
		}
	}

	@Override
	public String getPrimaryAttribute() throws RemoteException {
		return "isin";
	}

	@Override
	protected String getTableName() {
		return "aktien";
	}

	@Override
	protected Class getForeignObject(String field) throws RemoteException {
		// the system is able to resolve foreign keys and loads
		// the according objects automatically. You only have to
		// define which class handles which foreign key.
		if ("von_aktien_id".equals(field))
			return Aktie.class;
		return null;
	}

	public void delete() throws RemoteException, ApplicationException {
		try {
			this.transactionBegin();
			super.delete(); // we delete the aktie itself
			this.transactionCommit();

		} catch (RemoteException re) {
			this.transactionRollback();
			throw re;
		} catch (ApplicationException ae) {
			this.transactionRollback();
			throw ae;
		} catch (Throwable t) {
			this.transactionRollback();
			throw new ApplicationException(Settings.i18n().tr("error while deleting aktie"), t);
		}
	}

	protected void insertCheck() throws ApplicationException {
		try {
			if (getIsin() == null)
				throw new ApplicationException(Settings.i18n().tr("please enter isin"));
			if (getIsin().length() < 12)
				throw new ApplicationException(Settings.i18n().tr("isin has 12 characters"));
			if (getWkn() == null)
				throw new ApplicationException(Settings.i18n().tr("please enter wkn"));
			if (getWkn().length() < 6)
				throw new ApplicationException(Settings.i18n().tr("wkn has 6 characters"));
			if (getBezeichnung() == null)
				throw new ApplicationException(Settings.i18n().tr("please enter bezeichnung"));

		} catch (RemoteException e) {
			Logger.error("insert check of aktie failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to store aktie"));
		}
	}

	protected void updateCheck() throws ApplicationException {
		insertCheck();
	}

	protected void deleteCheck() throws ApplicationException {
		try {
			if (this.getKaeufe().hasNext())
				throw new ApplicationException(Settings.i18n().tr("aktie has transactions"));
		} catch (RemoteException e) {
			Logger.error("delete check of aktie failed", e);
			throw new ApplicationException(Settings.i18n().tr("unable to delete aktie"));
		}
	}
}
