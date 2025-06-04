package com.Grupo586.Re.Beans.Model;

import java.util.ArrayList;

public class Playlist  {
  private String titulo;
  private Integer id;
  private ArrayList<Integer> canciones;
  private Integer idPropietario;
  private String propietario;

  @Override
  public String toString() {
    return "Playlist{" +
            "titulo='" + titulo + '\'' +
            ", id=" + id +
            ", canciones=" + canciones +
            ", idPropietario=" + idPropietario +
            ", propietario='" + propietario + '\'' +
            '}';
  }

  public Playlist(String titulo, Integer id, ArrayList<Integer> canciones, Integer idPropietario, String propietario) {
    this.titulo = titulo;
    this.id = id;
    if (canciones == null) {
      this.canciones = new ArrayList<Integer>();
    } else {
      this.canciones = canciones;
    }
    this.idPropietario = idPropietario;
    this.propietario = propietario;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public ArrayList<Integer> getCanciones() {
    return canciones;
  }

  public void setCanciones(ArrayList<Integer> canciones) {
    this.canciones = canciones;
  }

  public Integer getIdPropietario() {
    return idPropietario;
  }

  public void setIdPropietario(Integer idPropietario) {
    this.idPropietario = idPropietario;
  }

  public String getPropietario() {
    return propietario;
  }

  public void setPropietario(String propietario) {
    this.propietario = propietario;
  }
}
