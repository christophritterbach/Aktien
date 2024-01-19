package de.ritterbach.jameica.aktien.gui.views.details;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.control.details.DividendenControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.jameica.gui.parts.ButtonArea;
import de.willuhn.jameica.gui.util.ColumnLayout;
import de.willuhn.jameica.gui.util.Container;
import de.willuhn.jameica.gui.util.SimpleContainer;
import de.willuhn.util.ApplicationException;

public class DividendenView extends AbstractView {

	@Override
	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Dividendenzahlung"));
		DividendenControl control = new DividendenControl(this);
		Container c = new SimpleContainer(getParent());
		c.addHeadline(control.getAktie());
		ColumnLayout columns = new ColumnLayout(c.getComposite(), 2);
		Container left = new SimpleContainer(columns.getComposite());
		left.addInput(control.getDividendeZahldatum());
		left.addInput(control.getDividendeProStueck());
		left.addInput(control.getDividendeGesamt());

		Container right = new SimpleContainer(columns.getComposite(), true);
		right.addInput(control.getQuellensteuer());
		right.addInput(control.getWechselkurs());
		right.addInput(control.getWaehrung());

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
