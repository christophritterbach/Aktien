package de.ritterbach.jameica.aktien.gui.control.lists;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.gui.action.AktieDetailAction;
import de.ritterbach.jameica.aktien.gui.parts.AktienListPart;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.ritterbach.jameica.aktien.rmi.V_Aktie;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.Part;
import de.willuhn.util.ApplicationException;

public class AktienListControl extends AbstractControl {

	private Part list = null;

	public AktienListControl(AbstractView view) {
		super(view);
	}

	public Part getListe() throws RemoteException {
		if (list != null)
			return list;
		list = new AktienListPart(new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
				if (context instanceof Aktie)
					new AktieDetailAction().handleAction(context);
				if (context instanceof V_Aktie)
					new AktieDetailAction().handleAction(context);
				return;
				
			}
		});
		return list;
	}

}
