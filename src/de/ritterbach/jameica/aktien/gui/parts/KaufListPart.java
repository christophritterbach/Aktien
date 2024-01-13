package de.ritterbach.jameica.aktien.gui.parts;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Date;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;

import de.ritterbach.jameica.aktien.AktienPlugin;
import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.V_Kauf;
import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.input.DateInput;
import de.willuhn.jameica.gui.input.Input;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.Column;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.gui.util.TabGroup;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

public class KaufListPart extends TablePart implements Part {
	private I18N i18n = null;
	private Listener listener;
	private DBService service = null;
	private Input from = null;
	private Input to = null;

	public KaufListPart(Action action) throws RemoteException {
		this(init(), action);
	}
	
	public KaufListPart(GenericIterator<V_Kauf> list, Action action) throws RemoteException {
		super(list, action);
		this.service = Settings.getDBService();
		this.listener = new Listener() {
			public void handleEvent(Event event) {
				// Wenn das event "null" ist, kann es nicht von SWT ausgeloest worden sein
				// sondern manuell von uns. In dem Fall machen wir ein forciertes Update
				// - ohne zu beruecksichtigen, ob in den Eingabe-Feldern wirklich was
				// geaendert wurde
				handleReload(event == null);
			}
		};
		this.i18n = Application.getPluginLoader().getPlugin(AktienPlugin.class).getResources().getI18N();
		addColumn(i18n.tr("Datum"), "kauf_datum", new DateFormatter());
		addColumn(i18n.tr("Anzahl"), "anzahl");
		addColumn(i18n.tr("Kurs"), "kurs", new CurrencyFormatter(Settings.CURRENCY, null), false, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Betrag"), "betrag", new CurrencyFormatter(Settings.CURRENCY, null), false, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Kosten"), "kosten", new CurrencyFormatter(Settings.CURRENCY, null), false, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Bemerkung"), "bemerkung");
		addColumn(i18n.tr("ISIN"), "isin");
		//setContextMenu(new KaufMenu());
		setRememberOrder(true);
		setRememberColWidths(true);
	}

	private static GenericIterator<V_Kauf> init() throws RemoteException {
		DBIterator<V_Kauf> aktienListe = Settings.getDBService().createList(V_Kauf.class);
		aktienListe.setOrder("ORDER BY kauf_datum");
		return aktienListe;
	}

	public synchronized void paint(Composite parent) throws RemoteException {
		final TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		TabGroup tab = new TabGroup(folder, i18n.tr("Anzeige_einschraenken"));
		ColumnLayout cols = new ColumnLayout(tab.getComposite(), 2);
		Container left = new SimpleContainer(cols.getComposite());
		//left.addInput(this.getZaehlerAuswahl());
		Container right = new SimpleContainer(cols.getComposite());
		right.addInput(getFrom());
		right.addInput(getTo());
		ButtonArea buttons = new ButtonArea();
		buttons.addButton(i18n.tr("Aktualisieren"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				handleReload(true);
			}
		}, null, true, "view-refresh.png");
		buttons.paint(parent);

		this.handleReload(true);
		super.paint(parent);
	}

	private Input getFrom() {
		if (this.from != null)
			return this.from;
		Calendar datum = Calendar.getInstance();
		datum.set(Calendar.HOUR_OF_DAY, 0);
		datum.set(Calendar.MINUTE, 0);
		datum.set(Calendar.SECOND, 0);
		datum.set(Calendar.MILLISECOND, 0);
		datum.set(Calendar.DAY_OF_YEAR, datum.getActualMinimum(Calendar.DAY_OF_YEAR));
		this.from = new DateInput(datum.getTime());
		this.from.setName(i18n.tr("von"));
		this.from.setComment(null);
		this.from.addListener(this.listener);
		return this.from;
	}

	private Input getTo() {
		if (this.to != null)
			return this.to;
		Calendar datum = Calendar.getInstance();
		datum.set(Calendar.HOUR_OF_DAY, 0);
		datum.set(Calendar.MINUTE, 0);
		datum.set(Calendar.SECOND, 0);
		datum.set(Calendar.MILLISECOND, 0);
		datum.set(Calendar.DAY_OF_YEAR, datum.getActualMaximum(Calendar.DAY_OF_YEAR));
		this.to = new DateInput(datum.getTime());
		this.to.setName(i18n.tr("bis"));
		this.to.setComment(null);
		this.to.addListener(this.listener);
		return this.to;
	}

	private synchronized void handleReload(boolean force) {
		try {
			GUI.startSync(new Runnable() { // Sanduhr anzeigen
				public void run() {
					try {
						removeAll();

						DBIterator<V_Kauf> kaufListe = service.createList(V_Kauf.class);
						kaufListe.addFilter("kauf_datum >= ?", (Date) getFrom().getValue());
						kaufListe.addFilter("kauf_datum <= ?", (Date) getTo().getValue());
						while (kaufListe.hasNext())
							addItem(kaufListe.next());
						sort();
					} catch (Exception e) {
						Logger.error("error while reloading table", e);
						Application.getMessagingFactory().sendMessage(new StatusBarMessage(
								i18n.tr("Fehler beim Aktualisieren der Tabelle"), StatusBarMessage.TYPE_ERROR));
					}
				}
			});
		} catch (Exception e) {
			Logger.error("error while reloading data", e);
			Application.getMessagingFactory().sendMessage(new StatusBarMessage(
					i18n.tr("Fehler beim Aktualisieren der Tabelle"), StatusBarMessage.TYPE_ERROR));
		}
	}
}
