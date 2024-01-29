package de.ritterbach.jameica.aktien.gui.dialog;

import java.rmi.RemoteException;

import org.eclipse.swt.widgets.Composite;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.V_Aktie;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.dialogs.AbstractDialog;
import de.willuhn.jameica.gui.parts.Button;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.parts.TablePart;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.jameica.system.OperationCanceledException;
import de.willuhn.util.ApplicationException;

public class AktienAuswahlDialog extends AbstractDialog<Aktie> {
	private final static int WINDOW_WIDTH = 450;
	private final static int WINDOW_HEIGHT = 500;
	private Aktie choosen = null;
	private V_Aktie aktie = null;
	private TablePart table = null;
	private Button apply = null;

	public AktienAuswahlDialog(int position, V_Aktie aktie) {
		super(position);
		this.aktie = aktie;
		this.setTitle(i18n.tr("Auswahl der Aktie"));
		setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
	}

	private class Apply implements Action {
		public void handleAction(Object context) throws ApplicationException {
			V_Aktie b = (V_Aktie) getTable().getSelection();
			try {
				choosen = b.getAktie();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			if (choosen != null)
		        close();
		}
	}

	private Button getApplyButton() {
		if (this.apply != null)
			return this.apply;

		this.apply = new Button(i18n.tr("Uebernehmen"), new Apply(), null, true, "ok.png");
		// this.apply.setEnabled(false); // initial deaktiviert
		return this.apply;
	}

	@Override
	protected void paint(Composite parent) throws Exception {
		Container group = new SimpleContainer(parent, true);
		group.addPart(getTable());
		ButtonArea buttons = new ButtonArea();
		buttons.addButton(this.getApplyButton());
		buttons.addButton(i18n.tr("Keine Aktie"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				choosen = null;
				close();
			}
		}, null, false, "list-remove.png");
		buttons.addButton(i18n.tr("Abbrechen"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				throw new OperationCanceledException();
			}
		}, null, false, "process-stop.png");

		group.addButtonArea(buttons);

		// Unabhaengig von dem, was der User als Groesse eingestellt hat, bleibt das die
		// Minimalgroesse.
		getShell().setMinimumSize(WINDOW_WIDTH, WINDOW_HEIGHT);

	}

	@Override
	protected Aktie getData() throws Exception {
		return choosen;
	}

	private TablePart getTable() {
		if (this.table != null)
			return this.table;

		DBIterator<V_Aktie> iter = null;
		try {
			iter = Settings.getDBService().createList(V_Aktie.class);
			if (this.aktie != null) {
				iter.addFilter("id != ?", this.aktie.getID());
				iter.addFilter("erster_kauf < ?", this.aktie.getErsterKauf());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		this.table = new TablePart(iter, new Apply());
		this.table.addColumn(i18n.tr("ISIN"), "isin");
		this.table.addColumn(i18n.tr("WKN"), "wkn");
		this.table.addColumn(i18n.tr("Bezeichnung"), "bezeichnung");
		return this.table;
	}
}
