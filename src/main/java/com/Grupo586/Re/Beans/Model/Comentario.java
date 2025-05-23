package com.Grupo586.Re.Beans.Model;

import java.time.LocalDateTime;
import com.Grupo586.Re.Beans.Model.Usuario;

public class Comentario {
  String comentario;
  Usuario usuario;
  LocalDateTime fecha;

  public Comentario(String comentario, Usuario usuario, LocalDateTime fecha) {
    this.comentario = comentario;
    this.usuario = usuario;
    this.fecha = fecha;
  }

  public String getComentario() {
    return comentario;
  }

  public void setComentario(String comentario) {
    this.comentario = comentario;
  }

  public Usuario getUsuario() {
    return usuario;
  }

  public void setUsuario(Usuario usuario) {
    this.usuario = usuario;
  }

  public LocalDateTime getFecha() {
    return fecha;
  }

  public void setFecha(LocalDateTime fecha) {
    this.fecha = fecha;
  }
}
