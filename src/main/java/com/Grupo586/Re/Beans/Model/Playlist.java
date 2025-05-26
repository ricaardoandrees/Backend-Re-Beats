package com.Grupo586.Re.Beans.Model;

import java.util.ArrayList;
import com.Grupo586.Re.Beans.Model.Cancion;
import com.Grupo586.Re.Beans.Model.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Playlist  {
  String Descripcion;
  String Propietario;
  ArrayList<Cancion> Canciones;

  public Playlist(String descripcion, String propietario, ArrayList<Cancion> canciones) {
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

  public String getPropietario() {
    return Propietario;
  }

  public void setPropietario(String propietario) {
    this.Propietario = propietario;
  }

  public ArrayList<Cancion> getCanciones() {
    return Canciones;
  }

  public void setCanciones(ArrayList<Cancion> canciones) {
    this.Canciones = canciones;
  }
}
