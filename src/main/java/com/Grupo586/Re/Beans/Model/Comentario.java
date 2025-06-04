package com.Grupo586.Re.Beans.Model;

public class Comentario {
  private Integer id;
  private String comentario;
  private Integer idPropietario;
  private String propietario;

  @Override
  public String toString() {
    return "Comentario{" +
            "id=" + id +
            ", comentario='" + comentario + '\'' +
            ", idPropietario=" + idPropietario +
            ", propietario='" + propietario + '\'' +
            '}';
  }

  public Comentario(Integer id, String comentario, Integer idPropietario, String propietario) {
    this.id = id;
    this.comentario = comentario;
    this.idPropietario = idPropietario;
    this.propietario = propietario;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getComentario() {
    return comentario;
  }

  public void setComentario(String comentario) {
    this.comentario = comentario;
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
