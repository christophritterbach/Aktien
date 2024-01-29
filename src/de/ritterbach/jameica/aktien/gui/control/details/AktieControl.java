package de.ritterbach.jameica.aktien.gui.control.details;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.action.AktieDetailAction;
import de.ritterbach.jameica.aktien.gui.action.DividendeDetailAction;
import de.ritterbach.jameica.aktien.gui.action.KaufDetailAction;
import de.ritterbach.jameica.aktien.gui.dialog.AktienAuswahlDialog;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.V_Aktie;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AktieControl extends AbstractControl {

	private Aktie aktie;
	private V_Aktie vAktie = null;
	private TextInput wkn;
	private TextInput isin;
	private TextInput bezeichnung;
	private TextInput vonAktieIsin;
	private DecimalInput anzahl;
	private DecimalInput kauf;
	private DecimalInput dividende;
	private DecimalInput kosten;
	private Button showParentAktie = null;
	private Button addParentAktie = null;
	private Button doStore = null;

	public AktieControl(AbstractView view) {
		super(view);
	}

	public Aktie getAktie() {
		if (aktie != null)
			return aktie;
		aktie = (Aktie) getCurrentObject();
		return aktie;
	}

	public V_Aktie getV_Aktie() {
		if (vAktie == null) {
			try {
				DBIterator<V_Aktie> iter;
				iter = Settings.getDBService().createList(V_Aktie.class);
				iter.addFilter("id=?", getAktie().getID());
				if (iter.hasNext())
					this.vAktie = iter.next();
			} catch (RemoteException e) {
				Logger.error("Can not load V_Aktie for Aktie", e);
			}
		}
		return this.vAktie;
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
 
	public Aktie getParentAktie() throws RemoteException {
		return getAktie().getAktieVon();
	}
	public boolean aktieHasParent() throws RemoteException {
		Aktie aktieVon = getParentAktie();
		return (aktieVon != null);
	}
	public Input vonAktieIsin() throws RemoteException {
		if (vonAktieIsin != null)
			return vonAktieIsin;
		String isin = "";
		Aktie aktieVon = getParentAktie();
		if (aktieVon != null)
			isin = aktieVon.getIsin();
		this.vonAktieIsin = new TextInput(isin);
		this.vonAktieIsin.setName(Settings.i18n().tr("Aktie von"));
		this.vonAktieIsin.setEnabled(false);
		return this.vonAktieIsin;
	}
 
	public Input getAnzahl() throws RemoteException {
		if (anzahl != null)
			return anzahl;
		
		this.anzahl = new DecimalInput(getV_Aktie().getAnzahl(), Settings.DECIMALFORMAT);
		this.anzahl.setName(Settings.i18n().tr("Anzahl"));
		this.anzahl.setComment(Settings.i18n().tr("im_bestand"));
		this.anzahl.setEnabled(false);
		return this.anzahl;
	}

	public Input getKauf() throws RemoteException {
		if (kauf != null)
			return kauf;
		
		this.kauf = new DecimalInput(getV_Aktie().getBetrag(), Settings.DECIMALFORMAT);
		this.kauf.setName(Settings.i18n().tr("Kauf"));
		this.kauf.setComment(Settings.i18n().tr("gesamt_kauf_verkauf"));
		this.kauf.setEnabled(false);
		return this.kauf;
	}

	public Input getDividende() throws RemoteException {
		if (dividende != null)
			return dividende;
		
		this.dividende = new DecimalInput(getV_Aktie().getGesamt(), Settings.DECIMALFORMAT);
		this.dividende.setName(Settings.i18n().tr("Dividende"));
		this.dividende.setComment(Settings.i18n().tr("alle_dividenden"));
		this.dividende.setEnabled(false);
		return this.dividende;
	}

	public Input getKosten() throws RemoteException {
		if (kosten != null)
			return kosten;
		
		this.kosten = new DecimalInput(getV_Aktie().getKosten(), Settings.DECIMALFORMAT);
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
	
	public boolean aktieHasAktien() throws RemoteException {
		Aktie aktie = getAktie();
		return (aktie.getAktienVon().size() > 0);
	}
	
	public TablePart getAktienVonListPart() throws RemoteException {
		return new AktieAktienVonListPart(aktie.getAktienVon(), new AktieDetailAction(), aktie);
	}
	
	public Button getShowButton() throws RemoteException {
		this.showParentAktie = new Button(Settings.i18n().tr("show_parent"), new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
				Aktie aktie = null;
				try {
					aktie = getParentAktie();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				new AktieDetailAction().handleAction(aktie);
			}
		});
		showParentAktie.setEnabled(aktieHasParent());
		return this.showParentAktie;
	}

	public Button getAddButton() throws RemoteException {
		this.addParentAktie = new Button(Settings.i18n().tr("add_parent"), new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
				AktienAuswahlDialog d;
				try {
					d = new AktienAuswahlDialog(1, getV_Aktie());
					Aktie parentAktie = d.open();
					getAktie().setAktieVon(parentAktie);
					if (parentAktie != null)
						vonAktieIsin.setValue(parentAktie.getIsin());
					else
						vonAktieIsin.setValue("");
					showParentAktie.setEnabled(aktieHasParent());
				} catch (OperationCanceledException oce) {
					return;
				} catch (ApplicationException ae) {
					throw ae;
				} catch (Exception e) {
					Logger.error("error while choosing umsatztyp", e);
				}
			}
		});
		return this.addParentAktie;
	}

	public Button getStoreButton() throws RemoteException {
		this.doStore = new Button(Settings.i18n().tr("Store"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				handleStore();
			}
		}, null, true); // "true" defines this button as the default button
		return this.doStore;
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
