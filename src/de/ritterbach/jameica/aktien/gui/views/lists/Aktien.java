package de.ritterbach.jameica.aktien.gui.views.lists;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.control.lists.AktienControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class Aktien extends AbstractView {

	@Override
	public void bind() throws Exception {
		GUI.getView().setTitle(Settings.i18n().tr("Aktien list"));
		AktienControl control = new AktienControl(this);
		control.getListe().paint(getParent());
	}

	public void unbind() throws ApplicationException {
	}

}
