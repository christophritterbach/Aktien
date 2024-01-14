package de.ritterbach.jameica.aktien.gui.control.details;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextAreaInput;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AktieNewControl extends AbstractControl {

	private Aktie aktie;
	private Kauf kauf;
	private TextInput wkn;
	private TextInput isin;
	private TextInput bezeichnung;
	private DateInput kaufDatum;
	private DecimalInput anzahl;
	private DecimalInput betrag;
	private DecimalInput kosten;
	private DecimalInput kurs;
	private TextAreaInput bemerkung;

	public AktieNewControl(AbstractView view) {
		super(view);
	}

	private Aktie getAktie() {
		if (aktie != null)
			return aktie;
		aktie = (Aktie) getCurrentObject();
		return aktie;
	}

	private Kauf getKauf() throws RemoteException {
		Aktie aktie = getAktie();
		if (aktie != null && aktie.getKaeufe().hasNext()) {
			return aktie.getKaeufe().next();
		}
		kauf = Settings.getDBService().createObject(Kauf.class, null);
		return kauf;
	}

	public Input getWkn() throws RemoteException {
		if (wkn != null)
			return wkn;

		this.wkn = new TextInput(getAktie().getWkn(), 6);
		this.wkn.setName(Settings.i18n().tr("WKN"));
		wkn.setMandatory(true);
		return this.wkn;
	}

	public Input getIsin() throws RemoteException {
		if (isin != null)
			return isin;

		this.isin = new TextInput(getAktie().getIsin(), 12);
		this.isin.setName(Settings.i18n().tr("ISIN"));
		isin.setMandatory(true);
		return this.isin;
	}

	public Input getBezeichnung() throws RemoteException {
		if (bezeichnung != null)
			return bezeichnung;

		this.bezeichnung = new TextInput(getAktie().getBezeichnung(), 100, Settings.i18n().tr("Name der Aktie"));
		this.bezeichnung.setName(Settings.i18n().tr("Bezeichnung"));
		bezeichnung.setMandatory(true);
		return this.bezeichnung;
	}

	public Input getKaufDatum() throws RemoteException {
		if (kaufDatum != null)
			return kaufDatum;
		this.kaufDatum = new DateInput(getKauf().getKaufDatum(), Settings.DATEFORMAT);
		this.kaufDatum.setName("Kaufdatum");
		this.kaufDatum.setText("Kaufdatum");
		this.kaufDatum.setMandatory(true);
		return kaufDatum;
	}

	public Input getAnzahl() throws RemoteException {
		if (anzahl != null)
			return anzahl;
		anzahl = new DecimalInput(getKauf().getAnzahl(), Settings.DECIMALFORMAT);
		anzahl.setName(Settings.i18n().tr("Anzahl"));
		anzahl.setMandatory(true);
		return anzahl;
	}

	public Input getBetrag() throws RemoteException {
		if (betrag != null)
			return betrag;
		betrag = new DecimalInput(getKauf().getBetrag(), Settings.DECIMALFORMAT);
		betrag.setName(Settings.i18n().tr("Betrag"));
		betrag.setComment(Settings.CURRENCY);
		betrag.setMandatory(true);
		return betrag;
	}

	public Input getKurs() throws RemoteException {
		if (kurs != null)
			return kurs;
		kurs = new DecimalInput(getKauf().getKurs(), Settings.DECIMALFORMAT);
		kurs.setName(Settings.i18n().tr("Kurs"));
		kurs.setMandatory(true);
		return kurs;
	}

	public Input getKosten() throws RemoteException {
		if (kosten != null)
			return kosten;
		kosten = new DecimalInput(getKauf().getKosten(), Settings.DECIMALFORMAT);
		kosten.setName(Settings.i18n().tr("T_Kosten"));
		kosten.setComment(Settings.CURRENCY);
		return kosten;
	}

	public Input getBemerkung() throws RemoteException {
		if (bemerkung != null)
			return bemerkung;
		bemerkung = new TextAreaInput(getKauf().getBemerkung());
		bemerkung.setName("");
		return bemerkung;
	}

	public void handleStore() {
		try {
			Aktie aktie = getAktie();
			aktie.setWkn((String) getWkn().getValue());
			aktie.setIsin((String) getIsin().getValue());
			aktie.setBezeichnung((String) getBezeichnung().getValue());
			Kauf kauf = getKauf();
			kauf.setAktie(aktie);
			kauf.setKaufDatum((Date) getKaufDatum().getValue());
			Double wert = (Double) getAnzahl().getValue();
			kauf.setAnzahl(wert == null ? BigDecimal.ZERO : BigDecimal.valueOf(wert));
			wert = (Double) getKurs().getValue();
			kauf.setKurs(wert == null ? BigDecimal.ZERO : BigDecimal.valueOf(wert));
			wert = (Double) getBetrag().getValue();
			kauf.setBetrag(wert == null ? BigDecimal.ZERO : BigDecimal.valueOf(wert));
			wert = (Double) getKosten().getValue();
			kauf.setKosten(wert == null ? BigDecimal.ZERO : BigDecimal.valueOf(wert));
			kauf.setBemerkung((String) getBemerkung().getValue());
			try {
				aktie.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("aktie stored successfully"), StatusBarMessage.TYPE_SUCCESS));
				kauf.setAktie(aktie);
				kauf.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("kauf stored successfully"), StatusBarMessage.TYPE_SUCCESS));
			} catch (ApplicationException e) {
				Application.getMessagingFactory()
						.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
			}
		} catch (RemoteException e) {
			Logger.error("error while storing aktie", e);
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(
					Settings.i18n().tr("error while storing aktie: {0}", e.getMessage()), StatusBarMessage.TYPE_ERROR));
		}
	}
}
