package de.ritterbach.jameica.aktien.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.views.details.AktieNew;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;

public class AktieNewAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Aktie aktie = null;

		try {
			Logger.info("Lege neue Aktie an");
			aktie = (Aktie) Settings.getDBService().createObject(Aktie.class, null);
		} catch (RemoteException e) {
			throw new ApplicationException(Settings.i18n().tr("error while creating new aktie"), e);
		}
		GUI.startView(AktieNew.class.getName(), aktie);
	}

}
