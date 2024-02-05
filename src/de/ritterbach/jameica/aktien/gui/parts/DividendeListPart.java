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
import de.ritterbach.jameica.aktien.formatter.KursFormatter;
import de.ritterbach.jameica.aktien.gui.menu.DividendeListMenu;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.V_Dividende;
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

public class DividendeListPart extends TablePart implements Part {
	private I18N i18n = null;
	private Listener listener;
	private DBService service = null;
	private Input from = null;
	private Input to = null;
	private Settings settings = null;

	public DividendeListPart(Action action) throws RemoteException {
		this(init(), action);
	}
	
	public DividendeListPart(GenericIterator<V_Dividende> list, Action action) throws RemoteException {
		super(list, action);
		this.service = Settings.getDBService();
		this.settings = new Settings(Aktie.class);
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
		addColumn(i18n.tr("Datum"), "zahl_datum", new DateFormatter());
		addColumn(i18n.tr("pro Stueck"), "pro_stueck", new CurrencyFormatter(Settings.CURRENCY, null), false, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Gesamt"), "gesamt", new CurrencyFormatter(Settings.CURRENCY, null), false, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Quellensteuer"), "quellensteuer", new CurrencyFormatter(Settings.CURRENCY, null), false, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Wechselkurs"), "devisenkurs", new KursFormatter(null), false, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Waehrung"), "waehrung");
		addColumn(i18n.tr("ISIN"), "isin");
		addColumn(i18n.tr("Bezeichnung"), "bezeichnung");
		setContextMenu(new DividendeListMenu());
		setRememberOrder(true);
		setRememberColWidths(true);
	}

	private static GenericIterator<V_Dividende> init() throws RemoteException {
		DBIterator<V_Dividende> aktienListe = Settings.getDBService().createList(V_Dividende.class);
		aktienListe.setOrder("ORDER BY zahl_datum");
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
		this.from = new DateInput(settings.getDate("from", datum.getTime()));
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
		this.to = new DateInput(settings.getDate("to", datum.getTime()));
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

						DBIterator<V_Dividende> kaufListe = service.createList(V_Dividende.class);
						kaufListe.addFilter("zahl_datum >= ?", (Date) getFrom().getValue());
						kaufListe.addFilter("zahl_datum <= ?", (Date) getTo().getValue());
						while (kaufListe.hasNext())
							addItem(kaufListe.next());
						sort();
						settings.setAttribute("from", (Date) getFrom().getValue());
						settings.setAttribute("to", (Date) getTo().getValue());
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
