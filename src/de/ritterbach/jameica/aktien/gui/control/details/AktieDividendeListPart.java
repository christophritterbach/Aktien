package de.ritterbach.jameica.aktien.gui.control.details;

import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TabFolder;

import de.ritterbach.jameica.aktien.AktienPlugin;
import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.formatter.KursFormatter;
import de.ritterbach.jameica.aktien.gui.menu.DividendeMenu;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Dividende;
import de.willuhn.datasource.BeanUtil;
import de.willuhn.datasource.GenericIterator;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBService;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.Part;
import de.willuhn.jameica.gui.formatter.CurrencyFormatter;
import de.willuhn.jameica.gui.formatter.DateFormatter;
import de.willuhn.jameica.gui.parts.Column;
import de.willuhn.jameica.gui.parts.TableChangeListener;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.messaging.StatusBarMessage;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

public class AktieDividendeListPart extends TablePart implements Part {
	private I18N i18n = null;
	private Listener listener;
	private DBService service = null;

	public AktieDividendeListPart(GenericIterator<Dividende> list, Action action, Aktie aktie) throws RemoteException {
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
		addColumn(i18n.tr("Datum"), "zahl_datum", new DateFormatter());
		addColumn(i18n.tr("pro Stueck"), "pro_stueck", new CurrencyFormatter(Settings.CURRENCY, null), false, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Gesamt"), "gesamt", new CurrencyFormatter(Settings.CURRENCY, null), true, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Quellensteuer"), "quellensteuer", new CurrencyFormatter(Settings.CURRENCY, null), true, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Wechselkurs"), "devisenkurs", new KursFormatter(null), true, Column.ALIGN_RIGHT);
		addColumn(i18n.tr("Waehrung"), "waehrung");
		addChangeListener(new TableChangeListener() {
			public void itemChanged(Object object, String attribute, String newValue) throws ApplicationException {
				try {
					Dividende dividende = (Dividende) object;
					BeanUtil.set(dividende, attribute, newValue);
					dividende.store();
				} catch (ApplicationException ae) {
					throw ae;
				} catch (Exception e) {
					Logger.error("unable to apply changes", e);
					throw new ApplicationException(i18n.tr("Fehlgeschlagen: {0}", e.getMessage()));
				}
			}
		});
		setContextMenu(new DividendeMenu(aktie));
		setRememberOrder(true);
		setRememberColWidths(true);
	}

	public synchronized void paint(Composite parent) throws RemoteException {
		final TabFolder folder = new TabFolder(parent, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		//this.handleReload(true);
		super.paint(parent);
	}

	private synchronized void handleReload(boolean force) {
		try {
			GUI.startSync(new Runnable() { // Sanduhr anzeigen
				public void run() {
					try {
						removeAll();

						DBIterator<Dividende> dividendeList = service.createList(Dividende.class);
						while (dividendeList.hasNext())
							addItem(dividendeList.next());
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
