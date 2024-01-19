package de.ritterbach.jameica.aktien.gui.control.details;

import java.math.BigDecimal;
import java.rmi.RemoteException;
import java.util.Date;

import org.eclipse.swt.widgets.Listener;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Dividende;
import de.ritterbach.jameica.aktien.rmi.V_Aktie;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.DecimalInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.input.TextInput;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class DividendenControl extends AbstractControl {

	private Dividende dividende;
	private DateInput zahlDatum;
	private DecimalInput betragProAktie;
	private DecimalInput gesamtBetrag;
	private DecimalInput quellensteuer;
	private DecimalInput wechselkurs;
	private TextInput waehrung;

	public DividendenControl(AbstractView view) {
		super(view);
	}

	private Dividende getDividende() {
		if (dividende != null)
			return dividende;
		dividende = (Dividende) getCurrentObject();
		return dividende;
	}

	public String getAktie() throws RemoteException {
		if (getDividende() == null)
			return "";
		Aktie aktie = null;
		aktie = dividende.getAktie();
		if (aktie == null)
			return "";
		return aktie.getBezeichnung();
	}
	public Input getDividendeZahldatum() throws RemoteException {
		if (zahlDatum != null)
			return zahlDatum;

		Date datum = getDividende().getZahlDatum();
		if (datum == null)
			datum = new Date();
		this.zahlDatum = new DateInput(datum, Settings.DATEFORMAT);
		this.zahlDatum.setName(Settings.i18n().tr("Dividende_Datum"));
		this.zahlDatum.setMandatory(true);
		return this.zahlDatum;
	}

	public Input getDividendeProStueck() throws RemoteException {
		if (betragProAktie != null)
			return betragProAktie;

		betragProAktie = new DecimalInput(getDividende().getProStueck(), Settings.DECIMALFORMAT);
		betragProAktie.setName(Settings.i18n().tr("pro Stueck"));
		betragProAktie.setComment(Settings.CURRENCY);
		betragProAktie.setMandatory(true);
		return this.betragProAktie;
	}

	public Input getDividendeGesamt() throws RemoteException {
		if (gesamtBetrag != null)
			return gesamtBetrag;

		gesamtBetrag = new DecimalInput(getDividende().getGesamt(), Settings.DECIMALFORMAT);
		gesamtBetrag.setName(Settings.i18n().tr("gesamt"));
		gesamtBetrag.setComment(Settings.CURRENCY);
		gesamtBetrag.setMandatory(true);
		gesamtBetrag.addListener(new Listener() {
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) {
				try {
					if (getDividendeGesamt().getValue() == null) {
						Double proStueck = (Double) getDividendeProStueck().getValue();
						Double anzahl = 0d;
						DBIterator<V_Aktie> iter = Settings.getDBService().createList(V_Aktie.class);
						iter.addFilter("id = ?", getDividende().getAktie().getID());
						if (iter.hasNext()) {
							V_Aktie aktie = iter.next();
							anzahl = aktie.getAnzahl().doubleValue();
						}
						if (proStueck != null && anzahl != null) {
							getDividendeGesamt().setValue(proStueck*anzahl);
						}
					}
				} catch (RemoteException e) {
				}
			}
			
		});
		return this.gesamtBetrag;
	}

	public Input getQuellensteuer() throws RemoteException {
		if (quellensteuer != null)
			return quellensteuer;

		quellensteuer = new DecimalInput(getDividende().getProStueck(), Settings.DECIMALFORMAT);
		quellensteuer.setName(Settings.i18n().tr("Quellensteuer"));
		quellensteuer.setComment(Settings.CURRENCY);
		return this.quellensteuer;
	}

	public Input getWechselkurs() throws RemoteException {
		if (wechselkurs != null)
			return wechselkurs;

		wechselkurs = new DecimalInput(getDividende().getDevisenkurs(), Settings.DECIMALFORMAT);
		wechselkurs.setName(Settings.i18n().tr("Wechselkurs"));
		wechselkurs.setComment(Settings.CURRENCY);
		return this.wechselkurs;
	}

	public Input getWaehrung() throws RemoteException {
		if (waehrung != null)
			return waehrung;

		waehrung = new TextInput(getDividende().getWaehrung(), 3, "EUR");
		waehrung.setName(Settings.i18n().tr("Waehrung"));
		waehrung.setComment(Settings.CURRENCY + " or USD");
		return this.waehrung;
	}

	public void handleStore() {
		try {
			Dividende div = getDividende();
			div.setZahlDatum((Date) getDividendeZahldatum().getValue());
			Double dps = (Double) getDividendeProStueck().getValue();
			div.setProStueck(dps == null ? new BigDecimal(0) : BigDecimal.valueOf(dps));
			Double ges = (Double) getDividendeGesamt().getValue();
			div.setGesamt(ges == null ? new BigDecimal(0) : BigDecimal.valueOf(ges));
			Double quell = (Double) getQuellensteuer().getValue();
			div.setQuellensteuer(quell == null ? new BigDecimal(0) : BigDecimal.valueOf(quell));
			Double wk = (Double) getWechselkurs().getValue();
			div.setDevisenkurs(wk == null ? new BigDecimal(0) : BigDecimal.valueOf(wk));
			div.setWaehrung((String) getWaehrung().getValue());
			try {
				div.store();
				Application.getMessagingFactory().sendMessage(new StatusBarMessage(
						Settings.i18n().tr("dividende stored successfully"), StatusBarMessage.TYPE_SUCCESS));
			} catch (ApplicationException e) {
				Application.getMessagingFactory()
						.sendMessage(new StatusBarMessage(e.getMessage(), StatusBarMessage.TYPE_ERROR));
			}
		} catch (RemoteException e) {
			Logger.error("error while storing dividende", e);
			Application.getMessagingFactory()
					.sendMessage(new StatusBarMessage(
							Settings.i18n().tr("error while storing dividende: {0}", e.getMessage()),
							StatusBarMessage.TYPE_ERROR));
		}
	}
}
