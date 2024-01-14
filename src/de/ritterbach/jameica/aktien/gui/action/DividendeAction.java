package de.ritterbach.jameica.aktien.gui.action;

import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class DividendeAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		GUI.startView(de.ritterbach.jameica.aktien.gui.views.lists.DividendenView.class.getName(), null);
	}

}
