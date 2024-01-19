package de.ritterbach.jameica.aktien.gui.control.lists;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.gui.action.DividendeDetailAction;
import de.ritterbach.jameica.aktien.gui.parts.DividendeListPart;
import de.ritterbach.jameica.aktien.rmi.Dividende;
import de.ritterbach.jameica.aktien.rmi.V_Dividende;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.Part;
import de.willuhn.util.ApplicationException;

public class DividendeControl extends AbstractControl {

	private Part list = null;

	public DividendeControl(AbstractView view) {
		super(view);
	}

	public Part getListe() throws RemoteException {
		if (list != null)
			return list;
		list = new DividendeListPart(new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
				if (context instanceof Dividende)
					new DividendeDetailAction().handleAction(context);
				if (context instanceof V_Dividende)
					new DividendeDetailAction().handleAction(context);
				return;
			}
		});
		return list;
	}

}
