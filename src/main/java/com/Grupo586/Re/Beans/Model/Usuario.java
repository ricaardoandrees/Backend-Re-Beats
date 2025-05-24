package com.Grupo586.Re.Beans.Model;

import java.util.ArrayList;

public class Usuario extends Persona{

  Integer ID;
  ArrayList<Usuario> Amigos;
  ArrayList<Playlist> Playlists;
  
  public Usuario() {
    super(null, null);
  }

  public Usuario(String nombre, String clave, ArrayList<Usuario> amigos, ArrayList<Playlist> playlists, Integer ID) {
    super( nombre, clave);

    if (amigos == null) {
      this.Amigos = new ArrayList<Usuario>();
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

  public ArrayList<Usuario> getAmigos() {
    return Amigos;
  }

  public void setAmigos(ArrayList<Usuario> amigos) {
    Amigos = amigos;
  }

  public ArrayList<Playlist> getPlaylists() {
    return Playlists;
  }

  public void setPlaylists(ArrayList<Playlist> playlists) {
    Playlists = playlists;
  }
}
