package de.ritterbach.jameica.aktien.gui.control.lists;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.gui.action.KaufDetailAction;
import de.ritterbach.jameica.aktien.gui.parts.KaufListPart;
import de.ritterbach.jameica.aktien.rmi.Kauf;
import de.ritterbach.jameica.aktien.rmi.V_Kauf;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.Part;
import de.willuhn.util.ApplicationException;

public class KaufControl extends AbstractControl {

	private Part list = null;

	public KaufControl(AbstractView view) {
		super(view);
	}

	public Part getListe() throws RemoteException {
		if (list != null)
			return list;
		list = new KaufListPart(new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
				if (context instanceof Kauf)
					new KaufDetailAction().handleAction(context);
				if (context instanceof V_Kauf)
					new KaufDetailAction().handleAction(context);
				return;
			}
		});
		return list;
	}

}
