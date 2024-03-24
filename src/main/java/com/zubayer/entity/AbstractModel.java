package com.zubayer.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

/**
 * @author Zubayer Ahamed
 * @since Mar 23, 2024
 * CSE202101068
 */
@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractModel<U> implements Serializable {

	private static final long serialVersionUID = -7673409459238414720L;

	@CreatedBy
	@Column(name = "createdBy", length = 25)
	private U createdBy;

	@LastModifiedBy
	@Column(name = "updatedBy", length = 25)
	private U updatedBy;

	@CreationTimestamp
	@Column(name = "createdOn", nullable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdOn;

	@UpdateTimestamp
	@Column(name = "updatedOn")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updatedOn;
}
