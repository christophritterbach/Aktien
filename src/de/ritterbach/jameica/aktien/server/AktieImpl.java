package de.ritterbach.jameica.aktien.server;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Dividende;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
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
	public String getPrimaryAttribute() throws RemoteException {
		return "isin";
	}

	@Override
	protected String getTableName() {
		return "aktien";
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
				throw new ApplicationException(Settings.i18n().tr("Please enter isin"));
			if (getIsin().length() < 12)
				throw new ApplicationException(Settings.i18n().tr("isin has 12 characters"));
			if (getWkn() == null)
				throw new ApplicationException(Settings.i18n().tr("Please enter wkn"));
			if (getWkn().length() < 12)
				throw new ApplicationException(Settings.i18n().tr("wkn has 12 characters"));
			if (getBezeichnung() == null)
				throw new ApplicationException(Settings.i18n().tr("Please enter bezeichnung"));

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
}
