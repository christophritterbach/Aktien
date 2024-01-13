package de.ritterbach.jameica.aktien.gui.views.lists;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.control.lists.DividendeControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class Dividenden extends AbstractView {

	@Override
	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Dividende list"));
		DividendeControl control = new DividendeControl(this);
		control.getListe().paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

}
