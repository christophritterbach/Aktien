package de.ritterbach.jameica.aktien.rmi;

import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObject;

public interface Aktie extends DBObject {
	public String getWkn() throws RemoteException;
	public void setWkn(String wkn) throws RemoteException;
	public String getIsin() throws RemoteException;
	public void setIsin(String isin) throws RemoteException;
	public String getBezeichnung() throws RemoteException;
	public void setBezeichnung(String bezeichnung) throws RemoteException;
	public DBIterator<Kauf> getKaeufe() throws RemoteException;
	public DBIterator<Dividende> getDidivenden() throws RemoteException;
}
