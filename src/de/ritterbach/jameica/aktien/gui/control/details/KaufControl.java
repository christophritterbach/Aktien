package de.ritterbach.jameica.aktien.gui.control.details;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import org.eclipse.swt.widgets.Listener;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.willuhn.datasource.rmi.Event;
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

public class KaufControl extends AbstractControl {

	private Kauf kauf;
	private DateInput kaufDatum;
	private DecimalInput anzahl;
	private DecimalInput kurs;
	private DecimalInput kosten;
	private DecimalInput betrag;
	private TextInput bemerkung;

	public KaufControl(AbstractView view) {
		super(view);
	}

	private Kauf getKauf() {
		if (kauf != null)
			return kauf;
		kauf = (Kauf) getCurrentObject();
		return kauf;
	}

	public String getAktie() throws RemoteException {
		if (getKauf() == null)
			return "";
		Aktie aktie = null;
		aktie = kauf.getAktie();
		if (aktie == null)
			return "";
		return aktie.getBezeichnung();
	}
	public Input getKaufDatum() throws RemoteException {
		if (kaufDatum != null)
			return kaufDatum;

		Date datum = getKauf().getKaufDatum();
		if (datum == null)
			datum = new Date();
		this.kaufDatum = new DateInput(datum, Settings.DATEFORMAT);
		this.kaufDatum.setName(Settings.i18n().tr("Kauf_Datum"));
		this.kaufDatum.setMandatory(true);
		return this.kaufDatum;
	}

	public Input getAnzahl() throws RemoteException {
		if (anzahl != null)
			return anzahl;

		anzahl = new DecimalInput(getKauf().getAnzahl(), Settings.ANZAHLFORMAT);
		anzahl.setName(Settings.i18n().tr("Anzahl"));
		anzahl.setMandatory(true);
		return this.anzahl;
	}

	public Input getKurs() throws RemoteException {
		if (kurs != null)
			return kurs;

		kurs = new DecimalInput(getKauf().getKurs(), Settings.DECIMALFORMAT);
		kurs.setName(Settings.i18n().tr("Kurs"));
		kurs.setComment(Settings.CURRENCY);
		kurs.setMandatory(true);
		return this.kurs;
	}

	public Input getBetrag() throws RemoteException {
		if (betrag != null)
			return betrag;

		betrag = new DecimalInput(getKauf().getBetrag(), Settings.DECIMALFORMAT);
		betrag.setName(Settings.i18n().tr("Betrag"));
		betrag.setComment(Settings.CURRENCY);
		betrag.setMandatory(true);
		betrag.addListener(new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				try {
					if (getBetrag().getValue() == null) {
						Double kurs = (Double) getKurs().getValue();
						Double anzahl = (Double) getAnzahl().getValue();
						if (kurs != null && anzahl != null) {
							getBetrag().setValue(kurs*anzahl*(-1));
						}
					}
				} catch (RemoteException e) {
				}
			}
			
		});
		return this.betrag;
	}

	public Input getKosten() throws RemoteException {
		if (kosten != null)
			return kosten;

		kosten = new DecimalInput(getKauf().getKosten(), Settings.DECIMALFORMAT);
		kosten.setName(Settings.i18n().tr("Kosten"));
		kosten.setComment(Settings.CURRENCY);
		return this.kosten;
	}

	public Input getBemerkung() throws RemoteException {
		if (bemerkung != null)
			return bemerkung;

		bemerkung = new TextAreaInput(getKauf().getBemerkung(), 1000);
		bemerkung.setName(Settings.i18n().tr(""));
		return this.bemerkung;
	}

	public void handleStore() {
		try {
			Kauf kauf = getKauf();
			kauf.setKaufDatum((Date) getKaufDatum().getValue());
			Double anz = (Double) getAnzahl().getValue();
			kauf.setAnzahl(anz == null ? new BigDecimal(0) : BigDecimal.valueOf(anz));
			Double kurs = (Double) getKurs().getValue();
			kauf.setKurs(kurs == null ? new BigDecimal(0) : BigDecimal.valueOf(kurs));
			Double kost = (Double) getKosten().getValue();
			kauf.setKosten(kost == null ? new BigDecimal(0) : BigDecimal.valueOf(kost));
			Double betr = (Double) getBetrag().getValue();
			kauf.setBetrag(betr == null ? new BigDecimal(0) : BigDecimal.valueOf(betr));
			kauf.setBemerkung((String) getBemerkung().getValue());
			try {
				kauf.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("kauf stored successfully"), StatusBarMessage.TYPE_SUCCESS));
			} catch (ApplicationException e) {
				Application.getMessagingFactory()
						.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
			}
		} catch (RemoteException e) {
			Logger.error("error while storing kauf", e);
			Application.getMessagingFactory()
					.sendMessage(new StatusBarMessage(
							Settings.i18n().tr("error while storing kauf: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}
}
