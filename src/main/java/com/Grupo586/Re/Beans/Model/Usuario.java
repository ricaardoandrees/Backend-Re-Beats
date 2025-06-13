package com.Grupo586.Re.Beans.Model;

import java.util.ArrayList;

public class Usuario {
  private String nombre;
  private String clave;
  private Integer id;
  private RolUsuario rol;
  private ArrayList<Integer> amigos;
  private ArrayList<Integer> playlists;

  public enum RolUsuario {
    Admin,
    Usuario
  }

  @Override
  public String toString() {
    return "Usuario{" +
            "nombre='" + nombre + '\'' +
            ", clave='" + clave + '\'' +
            ", id=" + id +
            ", rol=" + rol +
            ", amigos=" + amigos +
            ", playlists=" + playlists +
            '}';
  }

  public Usuario(String nombre, String clave, Integer id, RolUsuario rol, ArrayList<Integer> amigos, ArrayList<Integer> playlists) {
    this.nombre = nombre;
    this.clave = clave;
    this.id = id;
    this.rol = rol;

    if (amigos == null) {
      this.amigos = new ArrayList<Integer>();
    } else {
      this.amigos = amigos;
    }

    if (playlists == null) {
      this.playlists = new ArrayList<Integer>();
    } else {
      this.playlists = playlists;
    }
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public RolUsuario getRol() {
    return rol;
  }

  public void setRol(RolUsuario rol) {
    this.rol = rol;
  }

  public ArrayList<Integer> getAmigos() {
    return amigos;
  }

  public void setAmigos(ArrayList<Integer> amigos) {
    this.amigos = amigos;
  }

  public ArrayList<Integer> getPlaylists() {
    return playlists;
  }

  public void setPlaylists(ArrayList<Integer> playlists) {
    this.playlists = playlists;
  }


}
