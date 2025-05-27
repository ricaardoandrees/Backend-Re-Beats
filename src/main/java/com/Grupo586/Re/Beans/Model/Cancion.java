package com.Grupo586.Re.Beans.Model;

import java.util.ArrayList;
import com.Grupo586.Re.Beans.Model.Comentario;

public class Cancion {
  String titulo;
  String autor;
  String genero;
  String fecha;
  String imagen;
  ArrayList<Comentario> comentarios;
  ArrayList<String> Links;

  @Override
  public String toString() {
    return "Cancion{" +
            "titulo='" + titulo + '\'' +
            ", autor='" + autor + '\'' +
            ", genero='" + genero + '\'' +
            ", fecha='" + fecha + '\'' +
            ", imagen='" + imagen + '\'' +
            ", Comentarios=" + comentarios +
            ", Links=" + Links +
            '}';
  }

  public Cancion(ArrayList<Comentario> comentarios, String imagen, String fecha, String titulo, String autor, String genero, ArrayList<String> links) {
    this.imagen = imagen;
    this.fecha = fecha;
    this.titulo = titulo;
    this.autor = autor;
    this.genero = genero;
    this.Links = links;

    if (comentarios == null) {
      this.comentarios = new ArrayList<Comentario>();
    } else {
      this.comentarios = comentarios;
    }
  }

  // Getters y Setters
  public String getImagen() { return imagen; }
  public void setImagen(String imagen) { this.imagen = imagen; }

  public String getTitulo() { return titulo; }
  public void setTitulo(String titulo) { this.titulo = titulo; }

  public String getAutor() { return autor; }
  public void setAutor(String autor) { this.autor = autor; }

  public String getGenero() { return genero; }
  public void setGenero(String genero) { this.genero = genero; }

  public String getFecha() { return fecha; }
  public void setFecha(String fecha) { this.fecha = fecha; }

  public ArrayList<Comentario> getComentarios() { return comentarios; }
  public void setComentarios(ArrayList<Comentario> comentarios) { this.comentarios = comentarios; }

  public ArrayList<String> getLinks() { return Links; }
  public void setLinks(ArrayList<String> links) { this.Links = links; }
}