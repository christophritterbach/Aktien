package de.ritterbach.jameica.aktien.gui.control.details;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.action.DividendeDetailAction;
import de.ritterbach.jameica.aktien.gui.action.KaufDetailAction;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.V_Aktie;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AktieControl extends AbstractControl {

	private Aktie aktie;
	private V_Aktie vAktie;
	private TextInput wkn;
	private TextInput isin;
	private TextInput bezeichnung;
	private DecimalInput anzahl;
	private DecimalInput kauf;
	private DecimalInput dividende;
	private DecimalInput kosten;

	public AktieControl(AbstractView view) {
		super(view);
	}

	private Aktie getAktie() {
		if (aktie != null)
			return aktie;
		aktie = (Aktie) getCurrentObject();
		try {
			DBIterator<V_Aktie> iter;
			iter = Settings.getDBService().createList(V_Aktie.class);
			iter.addFilter("id=?", getAktie().getID());
			if (iter.hasNext())
				vAktie = iter.next();
		} catch (RemoteException e) {
			Logger.error("Can not load V_Aktie for Aktie", e);
		}
		return aktie;
	}

	public Input getWkn() throws RemoteException {
		if (wkn != null)
			return wkn;

		this.wkn = new TextInput(getAktie().getWkn(), 6);
		this.wkn.setName(Settings.i18n().tr("WKN"));
		return this.wkn;
	}

	public Input getIsin() throws RemoteException {
		if (isin != null)
			return isin;

		this.isin = new TextInput(getAktie().getIsin(), 12);
		this.isin.setName(Settings.i18n().tr("ISIN"));
		return this.isin;
	}

	public Input getBezeichnung() throws RemoteException {
		if (bezeichnung != null)
			return bezeichnung;

		this.bezeichnung = new TextInput(getAktie().getBezeichnung(), 100);
		this.bezeichnung.setName(Settings.i18n().tr("Bezeichnung"));
		return this.bezeichnung;
	}
 
	public Input getAnzahl() throws RemoteException {
		if (anzahl != null)
			return anzahl;
		
		this.anzahl = new DecimalInput(vAktie.getAnzahl(), Settings.DECIMALFORMAT);
		this.anzahl.setName(Settings.i18n().tr("Anzahl"));
		this.anzahl.setComment(Settings.i18n().tr("im_bestand"));
		this.anzahl.setEnabled(false);
		return this.anzahl;
	}

	public Input getKauf() throws RemoteException {
		if (kauf != null)
			return kauf;
		
		this.kauf = new DecimalInput(vAktie.getBetrag(), Settings.DECIMALFORMAT);
		this.kauf.setName(Settings.i18n().tr("Kauf"));
		this.kauf.setComment(Settings.i18n().tr("gesamt_kauf_verkauf"));
		this.kauf.setEnabled(false);
		return this.kauf;
	}

	public Input getDividende() throws RemoteException {
		if (dividende != null)
			return dividende;
		
		this.dividende = new DecimalInput(vAktie.getGesamt(), Settings.DECIMALFORMAT);
		this.dividende.setName(Settings.i18n().tr("Dividende"));
		this.dividende.setComment(Settings.i18n().tr("alle_dividenden"));
		this.dividende.setEnabled(false);
		return this.dividende;
	}

	public Input getKosten() throws RemoteException {
		if (kosten != null)
			return kosten;
		
		this.kosten = new DecimalInput(vAktie.getKosten(), Settings.DECIMALFORMAT);
		this.kosten.setName(Settings.i18n().tr("Kosten"));
		this.kosten.setComment(Settings.i18n().tr("kosten_kauf_verkauf"));
		this.kosten.setEnabled(false);
		return this.kosten;
	}

	public TablePart getKaufListPart() throws RemoteException {
		return new AktieKaufListPart(aktie.getKaeufe(), new KaufDetailAction(), aktie);
	}
	
	public TablePart getDivideneListPart() throws RemoteException {
		return new AktieDividendeListPart(aktie.getDidivenden(), new DividendeDetailAction(), aktie);
	}
	
	public void handleStore() {
		try {
			Aktie aktie = getAktie();
			aktie.setWkn((String)getWkn().getValue());
			aktie.setIsin((String)getIsin().getValue());
			aktie.setBezeichnung((String)getBezeichnung().getValue());
			try {
				aktie.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("aktie stored successfully"), StatusBarMessage.TYPE_SUCCESS));
			} catch (ApplicationException e) {
				Application.getMessagingFactory()
						.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
			}
		} catch (RemoteException e) {
			Logger.error("error while storing aktie", e);
			Application.getMessagingFactory()
					.sendMessage(new StatusBarMessage(
							Settings.i18n().tr("error while storing aktie: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}
}
