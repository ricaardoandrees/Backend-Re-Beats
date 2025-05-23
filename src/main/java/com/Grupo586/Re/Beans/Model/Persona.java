package com.Grupo586.Re.Beans.Model;

import java.util.ArrayList;

public abstract class Persona {
  public String nombre;
  public String clave;

  public Persona(String nombre,String clave) {
    this.nombre = nombre;
    this.clave=clave;
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
}
