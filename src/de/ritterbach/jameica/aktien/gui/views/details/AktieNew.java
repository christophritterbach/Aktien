package de.ritterbach.jameica.aktien.gui.views.details;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.control.details.AktieNewControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;

public class AktieNew extends AbstractView {

	public AktieNew() {
	}

	@Override
	public void bind() throws Exception {
		// draw the title
		GUI.getView().setTitle(Settings.i18n().tr("New Aktie"));
		// instanciate controller
		final AktieNewControl control = new AktieNewControl(this);
		Container c = new SimpleContainer(getParent());
		c.addHeadline("Aktie");
		// layout with 2 columns
		ColumnLayout columns = new ColumnLayout(c.getComposite(), 3);
		// left side
		Container left = new SimpleContainer(columns.getComposite());
		left.addInput(control.getIsin());
		Container middle = new SimpleContainer(columns.getComposite());
		middle.addInput(control.getWkn());
		Container right = new SimpleContainer(columns.getComposite(), true);
		right.addInput(control.getBezeichnung());
		c.addHeadline("Kauf");
		ColumnLayout columns1 = new ColumnLayout(c.getComposite(), 2);
		Container left1 = new SimpleContainer(columns1.getComposite());
		left1.addInput(control.getKaufDatum());
		left1.addInput(control.getAnzahl());
		left1.addInput(control.getKurs());
		left1.addInput(control.getBetrag());
		left1.addInput(control.getKosten());
		Container right1 = new SimpleContainer(columns1.getComposite(), true);
		right1.addHeadline(Settings.i18n().tr("Bemerkung"));
		right1.addInput(control.getBemerkung());

		ButtonArea buttons = new ButtonArea();
		buttons.addButton(Settings.i18n().tr("Store"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				control.handleStore();
			}
		}, null, true); // "true" defines this button as the default button
		// Don't forget to paint the button area
		buttons.paint(getParent());
	}
}
