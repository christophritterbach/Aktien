package de.ritterbach.jameica.aktien.rmi;

import java.math.BigDecimal;
import java.rmi.RemoteException;

import de.willuhn.datasource.rmi.DBIterator;
import de.willuhn.datasource.rmi.DBObject;

public interface V_Aktie extends DBObject {
	public Aktie getAktie() throws RemoteException;
	public void setAktie(Aktie aktie) throws RemoteException;
	public String getWkn() throws RemoteException;
	public void setWkn(String wkn) throws RemoteException;
	public String getIsin() throws RemoteException;
	public void setIsin(String isin) throws RemoteException;
	public String getBezeichnung() throws RemoteException;
	public void setBezeichnung(String bezeichnung) throws RemoteException;
	public BigDecimal getAnzahl() throws RemoteException;
	public void setAnzahl(BigDecimal anzahl) throws RemoteException;
	public DBIterator<Kauf> getKaeufe() throws RemoteException;
}
