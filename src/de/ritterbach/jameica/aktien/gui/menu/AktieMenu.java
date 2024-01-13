package de.ritterbach.jameica.aktien.gui.menu;

import de.ritterbach.jameica.aktien.Settings;
import de.ritterbach.jameica.aktien.gui.action.AktieDeleteAction;
import de.ritterbach.jameica.aktien.gui.action.AktieDetailAction;
import de.ritterbach.jameica.aktien.gui.action.AktieNewAction;
import de.willuhn.jameica.gui.Action;
import de.willuhn.jameica.gui.parts.CheckedContextMenuItem;
import de.willuhn.jameica.gui.parts.ContextMenu;
import de.willuhn.jameica.gui.parts.ContextMenuItem;
import de.willuhn.util.ApplicationException;

public class AktieMenu extends ContextMenu {
	public AktieMenu() {
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Open..."),new AktieDetailAction()));
		// separator
		addItem(ContextMenuItem.SEPARATOR);
		addItem(new ContextMenuItem(Settings.i18n().tr("New..."),new Action()
		{
			public void handleAction(Object context) throws ApplicationException
			{
				// we force the context to be null to create a new kosten in any case
				new AktieNewAction().handleAction(null);
			}
		}));
		addItem(new CheckedContextMenuItem(Settings.i18n().tr("Delete..."),new AktieDeleteAction()));
	}
}
