package de.ritterbach.jameica.aktien.gui.control.details;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AktieControl extends AbstractControl {

	private Aktie aktie;
	private TextInput wkn;
	private TextInput isin;
	private TextInput bezeichnung;

	public AktieControl(AbstractView view) {
		super(view);
	}

	private Aktie getAktie() {
		if (aktie != null)
			return aktie;
		aktie = (Aktie) getCurrentObject();
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

		this.bezeichnung = new TextInput(getAktie().getBezeichnung(), 12);
		this.bezeichnung.setName(Settings.i18n().tr("Bezeichnung"));
		return this.bezeichnung;
	}
 
	public TablePart getKaufListPart() throws RemoteException {
		return new AktieKaufListPart(aktie.getKaeufe(), null);
	}
	
	public TablePart getDivideneListPart() throws RemoteException {
		return new AktieDividendeListPart(aktie.getDidivenden(), null);
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
