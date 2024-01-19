package de.ritterbach.jameica.aktien.gui.views.details;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.control.details.KaufControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;

public class KaufView extends AbstractView {

	@Override
	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Kauf"));
		KaufControl control = new KaufControl(this);
		Container c = new SimpleContainer(getParent());
		c.addHeadline(control.getAktie());
		ColumnLayout columns = new ColumnLayout(c.getComposite(), 2);
		Container left = new SimpleContainer(columns.getComposite());
		left.addInput(control.getKaufDatum());
		left.addInput(control.getAnzahl());
		left.addInput(control.getKurs());
		left.addInput(control.getBetrag());
		left.addInput(control.getKosten());

		Container right = new SimpleContainer(columns.getComposite(), true);
		right.addHeadline(Settings.i18n().tr("Bemerkung"));
		right.addInput(control.getBemerkung());

		ButtonArea buttons = new ButtonArea();
		//buttons.addButton(Settings.i18n().tr("Delete"), new AbschlagDeleteAction(), control.getCurrentObject());
		buttons.addButton(Settings.i18n().tr("Store"), new Action() {
			public void handleAction(Object context) throws ApplicationException {
				control.handleStore();
			}
		}, null, true); // "true" defines this button as the default button
		// Don't forget to paint the button area
		buttons.paint(getParent());
	}

}
