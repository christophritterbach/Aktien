package de.ritterbach.jameica.aktien;

import java.rmi.RemoteException;

import de.ritterbach.jameica.aktien.rmi.AktienDBService;
import de.ritterbach.jameica.aktien.server.AktienDbServiceImpl;
import de.ritterbach.jameica.aktien.server.DBSupportH2Impl;
import de.willuhn.jameica.plugin.AbstractPlugin;
import de.willuhn.jameica.plugin.Version;
import de.willuhn.jameica.system.Application;
import de.willuhn.logging.Logger;
import de.willuhn.util.ApplicationException;
import de.willuhn.util.I18N;

/**
 * You need to have at least one class wich inherits from
 * <code>AbstractPlugin</code>. If so, Jameica will detect your plugin
 * automatically at startup.
 */
public class AktienPlugin extends AbstractPlugin {

	/**
	 * This method is invoked on every startup. You can make here some stuff to init
	 * your plugin. If you get some errors here and you dont want to activate the
	 * plugin, simply throw an ApplicationException. You dont need to implement this
	 * function.
	 * 
	 * @see de.willuhn.jameica.plugin.AbstractPlugin#init()
	 */
	public void init() throws ApplicationException {
		Logger.info("starting init process for hibiscus");

	    call(new ServiceCall()
	    {
	      public void call(AktienDBService service) throws ApplicationException, RemoteException
	      {
	        service.checkConsistency();
	      }
	    });
	}

	/**
	 * This method is called only the first time, the plugin is loaded (before
	 * executing init()). if your installation procedure was not successfull, throw
	 * an ApplicationException. You dont need to implement this function.
	 * 
	 * @see de.willuhn.jameica.plugin.AbstractPlugin#install()
	 */
	public void install() throws ApplicationException {
	    call(new ServiceCall() {
	        
	        public void call(AktienDBService service) throws ApplicationException, RemoteException
	        {
	          service.install();
	        }
	      });
	}

	/**
	 * This method will be executed on every version change. You dont need to
	 * implement this function.
	 * 
	 * @see de.willuhn.jameica.plugin.AbstractPlugin#update(de.willuhn.jameica.plugin.Version)
	 */
	public void update(Version oldVersion) throws ApplicationException {
		super.update(oldVersion);
	}

	/**
	 * Here you can do some cleanup stuff. The method will be called on every clean
	 * shutdown of jameica. You dont need to implement this function.
	 * 
	 * @see de.willuhn.jameica.plugin.AbstractPlugin#shutDown()
	 */
	public void shutDown() {
		super.shutDown();
	}

	  /**
	   * Hilfsmethode zum bequemen Ausfuehren von Aufrufen auf dem Service.
	   */
	  private interface ServiceCall
	  {
	    /**
	     * @param service
	     * @throws ApplicationException
	     * @throws RemoteException
	     */
	    public void call(AktienDBService service) throws ApplicationException, RemoteException;
	  }
	  
	  /**
	   * Hilfsmethode zum bequemen Ausfuehren von Methoden auf dem Service.
	   * @param call der Call.
	   * @throws ApplicationException
	   */
	  private void call(ServiceCall call) throws ApplicationException
	  {
	    if (Application.inClientMode())
	      return; // als Client muessen wir die DB nicht installieren

	    AktienDBService service = null;
	    try
	    {
	      // Da die Service-Factory zu diesem Zeitpunkt noch nicht da ist, erzeugen
	      // wir uns eine lokale Instanz des Services.
		  Logger.info("Starte Db Service");
	      service = new AktienDbServiceImpl();
	      service.start();
	      call.call(service);
	    }
	    catch (ApplicationException ae)
	    {
	      throw ae;
	    }
	    catch (Exception e)
	    {
	      Logger.error("unable to init db service",e);
	      I18N i18n = getResources().getI18N();
	      String msg = i18n.tr("Aktien-Datenbank konnte nicht initialisiert werden.\n\n{0} ", e.getMessage());
	      
	      // Wenn wir die H2-DB verwenden, koennte es sich um eine korrupte Datenbank handeln
	      String driver = AktienDBService.SETTINGS.getString("database.driver",null);
	      if (driver != null && driver.equals(DBSupportH2Impl.class.getName()))
	      {
	        msg += "\n\nMöglicherweise ist die Aktien-Datenbank defekt. Klicken Sie bitte auf \"Datei>Backups verwalten\", " +
	        		   "wählen Sie das Backup vom letzten Tag aus, an dem der Fehler noch nicht auftrat und klicken " +
	        		   "Sie anschließend auf \"Ausgewähltes Backup wiederherstellen...\". Beim nächsten Start von Aktie" +
	        		   "wird das Backup automatisch wiederhergestellt. Sollte sich das Problem hierdurch nicht beheben lassen, " +
	        		   "besuchen Sie bitte http://www.willuhn.de/wiki/doku.php?id=support:fehlermelden";
	      }

	      throw new ApplicationException(msg,e);
	    }
	    finally
	    {
	      if (service != null)
	      {
	        try
	        {
	          service.stop(true);
	        }
	        catch (Exception e)
	        {
	          Logger.error("error while closing db service",e);
	        }
	      }
	    }
	  }
}

