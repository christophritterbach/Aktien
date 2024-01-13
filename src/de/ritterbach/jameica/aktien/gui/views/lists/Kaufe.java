package de.ritterbach.jameica.aktien.gui.views.lists;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.control.lists.KaufControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class Kaufe extends AbstractView {

	@Override
	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Kauf list"));
		KaufControl control = new KaufControl(this);
		control.getListe().paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

}
