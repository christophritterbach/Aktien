package de.ritterbach.jameica.aktien.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.views.details.DividendenView;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Dividende;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class DividendeNewAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Dividende dividende = null;

		try {
			dividende = (Dividende) Settings.getDBService().createObject(Dividende.class, null);
			if (context instanceof Aktie) {
				dividende.setAktie((Aktie) context);
			}
		} catch (RemoteException e) {
			throw new ApplicationException(Settings.i18n().tr("error while creating new dividende"), e);
		}
		GUI.startView(DividendenView.class.getName(), dividende);
	}

}
