package com.Grupo586.Re.Beans.Model;

import java.util.ArrayList;

public class Usuario extends Persona{

  Integer ID;
  ArrayList<String> Amigos;
  ArrayList<Playlist> Playlists;
  
  public Usuario() {
    super(null, null);
  }

  public Usuario(String nombre, String clave, ArrayList<String> amigos, ArrayList<Playlist> playlists, Integer ID) {
    super( nombre, clave);

    if (amigos == null) {
      this.Amigos = new ArrayList<String>();
    } else {
      this.Amigos = amigos;
    }

    if (playlists == null) {
      this.Playlists = new ArrayList<Playlist>();
    } else {
      this.Playlists = playlists;
    }

    this.ID = ID;
  }

  public Integer getID() {
    return ID;
  }

  public void setID(Integer ID) {
    this.ID = ID;
  }

  public ArrayList<String> getAmigos() {
    return Amigos;
  }

  public void setAmigos(ArrayList<String> amigos) {
    Amigos = amigos;
  }

  public ArrayList<Playlist> getPlaylists() {
    return Playlists;
  }

  public void setPlaylists(ArrayList<Playlist> playlists) {
    Playlists = playlists;
  }
}
