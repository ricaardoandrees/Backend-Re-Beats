package com.Grupo586.Re.Beans.Model;
import java.util.ArrayList;
//import com.Grupo586.Re.Beans.Model.Comentario; No se porque no es necesario, parece que si esta en la misma carpeta lo reconoce

public class Cancion {
  // Poner privates
  String titulo;
  String autor;
  String genero;
  String fecha;
  String imagen;
  ArrayList<Comentario> Comentarios;
  //Agregar atributo array de links (este si esta en el constructor)

  // Puedes quitar el array de comentarios en el constructor
  public Cancion(ArrayList<Comentario> comentarios, String imagen, String fecha, String titulo, String autor, String genero) {
    this.Comentarios = comentarios;
    this.imagen = imagen;
    this.fecha = fecha;
    this.titulo = titulo;
    this.autor = autor;
    this.genero = genero;
  }

  public String getImagen() {
    return imagen;
  }

  public void setImagen(String imagen) {
    this.imagen = imagen;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getAutor() {
    return autor;
  }

  public void setAutor(String autor) {
    this.autor = autor;
  }

  public String getGenero() {
    return genero;
  }

  public void setGenero(String genero) {
    this.genero = genero;
  }

  public String getFecha() {
    return fecha;
  }

  public void setFecha(String fecha) {
    this.fecha = fecha;
  }
}
