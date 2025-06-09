package com.Grupo586.Re.Beans.Model;

import java.util.ArrayList;

public class Cancion {
  private String titulo;
  private Integer id;
  private String autor;
  private String fecha;
  private String imagen;
  private ArrayList<String> links;
  private ArrayList<Integer> comentarios;

  @Override
  public String toString() {
    return "Cancion{" +
            "titulo='" + titulo + '\'' +
            ", id=" + id +
            ", autor='" + autor + '\'' +
            ", fecha='" + fecha + '\'' +
            ", imagen='" + imagen + '\'' +
            ", links=" + links +
            ", comentarios=" + comentarios +
            '}';
  }

  public Cancion(String titulo, Integer id, String autor, String fecha, String imagen, ArrayList<String> links, ArrayList<Integer> comentarios) {
    this.titulo = titulo;
    this.id = id;
    this.autor = autor;
    this.fecha = fecha;
    this.imagen = imagen;
    if (links == null) {
      this.links = new ArrayList<String>();
    } else {
      this.links = links;
    }
    if (comentarios == null) {
      this.comentarios = new ArrayList<Integer>();
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

  public String getFecha() { return fecha; }
  public void setFecha(String fecha) { this.fecha = fecha; }

  public ArrayList<String> getLinks() { return links; }
  public void setLinks(ArrayList<String> links) { this.links = links; }

  public ArrayList<Integer> getComentarios() { return comentarios; }
  public void setComentarios(ArrayList<Integer> comentarios) { this.comentarios = comentarios; }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }
}