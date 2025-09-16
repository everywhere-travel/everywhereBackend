
package com.everywhere.backend.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "categorias")
public class Categoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cat_id_int")
	private int id;

	@Column(name = "cat_nom_vac", length = 100)
	private String nombre;

	@Column(name = "cat_cre_tmp")
	private LocalDateTime creado;

	@Column(name = "cat_upd_tmp")
	private LocalDateTime actualizado;

}
