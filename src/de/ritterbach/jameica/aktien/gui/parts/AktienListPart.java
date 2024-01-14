package de.ritterbach.jameica.aktien.gui.parts;

import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;

import de.ritterbach.jameica.aktien.AktienPlugin;
import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.formatter.DecimalFormatter;
import de.ritterbach.jameica.aktien.gui.menu.AktieMenu;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.V_Aktie;
import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.input.CheckboxInput;
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

public class AktienListPart extends TablePart implements Part {
	private I18N i18n = null;
	private Listener listener;
	private DBService service = null;
	private CheckboxInput nurBestand = null;
	private Settings settings = null;

	public AktienListPart(Action action) throws RemoteException {
		this(init(), action);
	}

	public AktienListPart(GenericIterator<V_Aktie> list, Action action) throws RemoteException {
		super(list, action);
		this.settings = new Settings(Aktie.class);
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
		addColumn(i18n.tr("WKN"), "wkn");
		addColumn(i18n.tr("ISIN"), "isin");
		addColumn(i18n.tr("Bezeichnung"), "bezeichnung");
		addColumn(i18n.tr("Anzahl"), "anzahl", new DecimalFormatter(Settings.ANZAHLFORMAT));
		addColumn(i18n.tr("Betrag"), "betrag", new CurrencyFormatter(Settings.CURRENCY, null), false, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Dividenden"), "gesamt", new CurrencyFormatter(Settings.CURRENCY, null), false, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("T_Kosten"), "kosten", new CurrencyFormatter(Settings.CURRENCY, null), false, Column.ALIGN_RIGHT);
		setContextMenu(new AktieMenu());
		setRememberOrder(true);
		setRememberColWidths(true);
	}

	private static GenericIterator<V_Aktie> init() throws RemoteException {
		DBIterator<V_Aktie> aktienListe = Settings.getDBService().createList(V_Aktie.class);
		aktienListe.setOrder("ORDER BY isin");
		return aktienListe;
	}

	public synchronized void paint(Composite parent) throws RemoteException {
		final TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		TabGroup tab = new TabGroup(folder, i18n.tr("Anzeige_einschraenken"));
		ColumnLayout cols = new ColumnLayout(tab.getComposite(), 2);
		Container left = new SimpleContainer(cols.getComposite());
		left.addInput(this.getIstNurBestand());
		Container right = new SimpleContainer(cols.getComposite());
		// right.addInput(getFrom());
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

	public CheckboxInput getIstNurBestand() throws RemoteException {
		if (nurBestand != null)
			return nurBestand;

		nurBestand = new CheckboxInput(settings.getBoolean("nur_bestand", true));
		nurBestand.setName(Settings.i18n().tr("nur Bestand"));
		nurBestand.setComment(Settings.i18n().tr("nur Bestandsaktien"));
		nurBestand.addListener(this.listener);
		return this.nurBestand;
	}

	private synchronized void handleReload(boolean force) {
		try {
			GUI.startSync(new Runnable() { // Sanduhr anzeigen
				public void run() {
					try {
						removeAll();

						DBIterator<V_Aktie> aktienListe = service.createList(V_Aktie.class);
						if ((Boolean) getIstNurBestand().getValue())
							aktienListe.addFilter("anzahl>0");
						while (aktienListe.hasNext())
							addItem(aktienListe.next());
						sort();
						settings.setAttribute("nur_bestand", (Boolean) getIstNurBestand().getValue());
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
