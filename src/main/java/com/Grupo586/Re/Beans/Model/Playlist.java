package com.Grupo586.Re.Beans.Model;

import java.util.ArrayList;
import com.Grupo586.Re.Beans.Model.Cancion;
import com.Grupo586.Re.Beans.Model.Usuario;

public class Playlist  {
  String Descripcion;
  Usuario Propietario;
  ArrayList<Cancion> Canciones;

  public Playlist(String descripcion, Usuario propietario, ArrayList<Cancion> canciones) {
    this.Descripcion = descripcion;
    this.Propietario = propietario;
    this.Canciones = canciones;
  }

  public String getDescripcion() {
    return Descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.Descripcion = descripcion;
  }

  public Usuario getPropietario() {
    return Propietario;
  }

  public void setPropietario(Usuario propietario) {
    this.Propietario = propietario;
  }

  public ArrayList<Cancion> getCanciones() {
    return Canciones;
  }

  public void setCanciones(ArrayList<Cancion> canciones) {
    this.Canciones = canciones;
  }
}
