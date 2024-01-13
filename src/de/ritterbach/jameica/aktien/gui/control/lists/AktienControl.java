package de.ritterbach.jameica.aktien.gui.control.lists;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.gui.parts.AktienListPart;
import de.ritterbach.jameica.aktien.rmi.Aktie;
import de.willuhn.jameica.gui.AbstractControl;
import de.willuhn.jameica.gui.AbstractView;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.Part;
import de.willuhn.util.ApplicationException;

public class AktienControl extends AbstractControl {

	private Part list = null;

	public AktienControl(AbstractView view) {
		super(view);
	}

	public Part getListe() throws RemoteException {
		if (list != null)
			return list;
		list = new AktienListPart(new Action() {
			@Override
			public void handleAction(Object context) throws ApplicationException {
				if (!(context instanceof Aktie))
					return;
			}
		});
		return list;
	}

}
