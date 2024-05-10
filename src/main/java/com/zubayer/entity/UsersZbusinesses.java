package com.zubayer.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202401068
 */
@Data
@Entity
@Table(name = "users_zbusinesses")
@EqualsAndHashCode(callSuper = true)
public class UsersZbusinesses extends AbstractModel<Integer> {

	private static final long serialVersionUID = -2190145826598442182L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer uid;
	private Integer zid;
}
