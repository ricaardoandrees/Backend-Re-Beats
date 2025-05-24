package com.Grupo586.Re.Beans.Model;

import java.util.ArrayList;
//import com.Grupo586.Re.Beans.Model.Cancion;
//import com.Grupo586.Re.Beans.Model.Usuario; no se porque no es necesario, parece que si esta en la misma carpeta lo reconoce

public class Playlist  {
  // Poner privates
  String Descripcion; //La playlist no tiene descripcion, tiene nombre
  Usuario Propietario;
  ArrayList<Cancion> Canciones;

  // Quitar el array de canciones en el constructor cuando se cree la playlist no tendra canciones
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
