package de.ritterbach.jameica.aktien.server;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Dividende;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.ritterbach.jameica.aktien.rmi.V_Aktie;
import de.willuhn.datasource.db.AbstractDBObject;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.datasource.rmi.ObjectNotFoundException;
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
	public BigDecimal getGesamt() throws RemoteException {
		return (BigDecimal) getAttribute("gesamt");
	}

	@Override
	public void setGesamt(BigDecimal gesamt) throws RemoteException {
		setAttribute("gesamt", gesamt);
	}

	@Override
	public Date getErsterKauf() throws RemoteException {
		return (Date) getAttribute("erster_kauf");
	}

	@Override
	public void setErsterKauf(Date ersterKauf) throws RemoteException {
		setAttribute("erster_kauf", ersterKauf);
	}
	
	@Override
	public Date getLetzterKauf() throws RemoteException {
		return (Date) getAttribute("letzter_kauf");
	}
	
	@Override
	public void setLetzterKauf(Date letzterKauf) throws RemoteException {
		setAttribute("letzter_kauf", letzterKauf);
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
		throw new ApplicationException(Settings.i18n().tr("can not store into view"));
	}

	protected void updateCheck() throws ApplicationException {
		throw new ApplicationException(Settings.i18n().tr("can not store into view"));
	}

	protected void deleteCheck() throws ApplicationException {
		throw new ApplicationException(Settings.i18n().tr("can not delete from view"));
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
