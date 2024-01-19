package de.ritterbach.jameica.aktien.gui.action;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.ritterbach.jameica.aktien.rmi.V_Kauf;
import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.GUI;
import de.willuhn.util.ApplicationException;

public class KaufDetailAction implements Action {

	@Override
	public void handleAction(Object context) throws ApplicationException {
		Kauf kauf = null;

		if (context != null && (context instanceof Kauf)) {
			kauf = (Kauf) context;
		} else if (context != null && (context instanceof V_Kauf)) {
			V_Kauf vKauf = (V_Kauf)context;
			try {
				DBIterator<Kauf> iter = Settings.getDBService().createList(Kauf.class);
				iter.addFilter("id=?", vKauf.getID());
				if (iter.hasNext())
					kauf = iter.next();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		} else {
			throw new ApplicationException(Settings.i18n().tr("error while creating new kauf"));
		}
		GUI.startView(de.ritterbach.jameica.aktien.gui.views.details.KaufView.class.getName(), kauf);
	}

}
