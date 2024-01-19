package de.ritterbach.jameica.aktien.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.views.details.KaufView;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class KaufNewAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Kauf kauf = null;

		try {
			kauf = (Kauf) Settings.getDBService().createObject(Kauf.class, null);
			if (context instanceof Aktie) {
				kauf.setAktie((Aktie) context);
			}
		} catch (RemoteException e) {
			throw new ApplicationException(Settings.i18n().tr("error while creating new kauf"), e);
		}
		GUI.startView(KaufView.class.getName(), kauf);
	}

}
